package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpManager;

/**
 * Controller API phục vụ tương tác AJAX mượt mà cho chức năng Quên mật khẩu & OTP
 * Phục vụ cả Modal Popup và các form thông thường
 */
@WebServlet(urlPatterns = {
    "/api/auth/forgot-password",
    "/api/auth/resend-otp",
    "/api/auth/verify-otp",
    "/api/auth/reset-password"
})
public class AuthApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IUserService userService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.emailService = EmailService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getServletPath();

        switch (path) {
            case "/api/auth/forgot-password":
                handleForgotPassword(req, resp);
                break;
            case "/api/auth/resend-otp":
                handleResendOtp(req, resp);
                break;
            case "/api/auth/verify-otp":
                handleVerifyOtp(req, resp);
                break;
            case "/api/auth/reset-password":
                handleResetPassword(req, resp);
                break;
            default:
                writeJsonResponse(resp, false, "Endpoint không hợp lệ", null);
                break;
        }
    }

    /**
     * 1. Xử lý yêu cầu gửi mã OTP quên mật khẩu
     */
    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            writeJsonResponse(resp, false, "Vui lòng nhập địa chỉ email tài khoản đã đăng ký (Gmail, Email HCMUTE,...)!", null);
            return;
        }

        email = email.trim().toLowerCase();

        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            writeJsonResponse(resp, false, "Địa chỉ email không đúng định dạng!", null);
            return;
        }

        // Kiểm tra email tồn tại trong hệ thống
        User user = userService.findByEmail(email);
        if (user == null) {
            writeJsonResponse(resp, false, "Địa chỉ email '" + email + "' chưa được đăng ký trong hệ thống!", null);
            return;
        }

        // Kiểm tra rate limit cooldown (Backend kiểm tra chặt chẽ)
        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            writeJsonResponse(resp, false, "Thao tác quá nhanh! Vui lòng đợi " + cooldown + " giây nữa để gửi lại OTP.", 
                    String.format("\"cooldown\":%d", cooldown));
            return;
        }

        try {
            // Sinh mã OTP 6 chữ số ngẫu nhiên an toàn bằng SecureRandom
            String otp = OtpManager.generateOtp();

            // Lưu phiên OTP với mục đích RESET_PASSWORD (vô hiệu hóa mã cũ ngay lập tức)
            OtpManager.createOtpSession(email, otp, "RESET_PASSWORD", user);

            // Gửi email thật qua Gmail SMTP
            emailService.sendOtpEmail(email, user.getFullname(), otp, "ĐẶT LẠI MẬT KHẨU");

            String masked = OtpManager.maskEmail(email);
            writeJsonResponse(resp, true, "Mã OTP 6 chữ số đã được gửi thành công đến Gmail của bạn!", 
                    String.format("\"maskedEmail\":\"%s\",\"cooldown\":60", masked));

        } catch (Exception e) {
            e.printStackTrace();
            writeJsonResponse(resp, false, "Không thể gửi email OTP: " + e.getMessage(), null);
        }
    }

    /**
     * 2. Xử lý gửi lại mã OTP (áp dụng cooldown 60s và vô hiệu hóa mã cũ)
     */
    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            writeJsonResponse(resp, false, "Thiếu thông tin email!", null);
            return;
        }

        email = email.trim().toLowerCase();

        // Kiểm tra backend rate limit
        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            writeJsonResponse(resp, false, "Vui lòng đợi " + cooldown + " giây nữa mới được gửi lại mã!", 
                    String.format("\"cooldown\":%d", cooldown));
            return;
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            writeJsonResponse(resp, false, "Người dùng không tồn tại!", null);
            return;
        }

        try {
            // Sinh mã OTP mới (tự động xóa mã cũ)
            String newOtp = OtpManager.generateOtp();
            OtpManager.createOtpSession(email, newOtp, "RESET_PASSWORD", user);

            // Gửi email mới qua Gmail SMTP
            emailService.sendOtpEmail(email, user.getFullname(), newOtp, "ĐẶT LẠI MẬT KHẨU");

            writeJsonResponse(resp, true, "Đã gửi lại mã OTP mới đến Gmail của bạn. Vui lòng kiểm tra!", 
                    "\"cooldown\":60");

        } catch (Exception e) {
            e.printStackTrace();
            writeJsonResponse(resp, false, "Lỗi khi gửi lại OTP: " + e.getMessage(), null);
        }
    }

    /**
     * 3. Xác minh mã OTP (6 chữ số, tối đa 5 lần thử, mục đích RESET_PASSWORD)
     */
    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");

        if (email == null || otp == null || email.trim().isEmpty() || otp.trim().isEmpty()) {
            writeJsonResponse(resp, false, "Vui lòng nhập đủ 6 chữ số mã xác thực OTP!", null);
            return;
        }

        email = email.trim().toLowerCase();
        otp = otp.trim().replaceAll("\\s+", "");

        OtpManager.OtpSession session = OtpManager.getSession(email);
        if (session == null) {
            writeJsonResponse(resp, false, "Phiên xác thực đã hết hiệu lực (quá 5 phút) hoặc đã bị hủy. Vui lòng yêu cầu mã mới!", 
                    "\"expired\":true");
            return;
        }

        OtpManager.VerifyResult result = OtpManager.verifyOtp(email, otp, "RESET_PASSWORD");

        switch (result) {
            case SUCCESS:
                // Sinh resetToken ngẫu nhiên an toàn một lần duy nhất
                String resetToken = UUID.randomUUID().toString();
                HttpSession httpSession = req.getSession();
                httpSession.setAttribute("RESET_TOKEN", resetToken);
                httpSession.setAttribute("RESET_EMAIL", email);
                httpSession.setAttribute("RESET_EXPIRES", System.currentTimeMillis() + 10 * 60 * 1000L); // 10 phút

                // Vô hiệu hóa phiên OTP ngay sau khi xác thực thành công (OTP chỉ dùng 1 lần)
                OtpManager.removeSession(email);

                writeJsonResponse(resp, true, "Xác nhận mã OTP thành công! Mời bạn thiết lập mật khẩu mới.", 
                        String.format("\"resetToken\":\"%s\"", resetToken));
                break;

            case INVALID:
                int remaining = OtpManager.MAX_FAILED_ATTEMPTS - session.getFailedAttempts();
                writeJsonResponse(resp, false, "Mã OTP không chính xác! Bạn còn " + remaining + " lần nhập thử.", 
                        String.format("\"remainingAttempts\":%d", remaining));
                break;

            case EXCEEDED_ATTEMPTS:
                writeJsonResponse(resp, false, "Bạn đã nhập sai OTP quá 5 lần. Mã OTP đã bị hủy vì lý do an toàn! Vui lòng gửi lại mã mới.", 
                        "\"exceeded\":true");
                break;

            case EXPIRED:
                writeJsonResponse(resp, false, "Mã OTP đã hết hạn sử dụng (quá 5 phút). Vui lòng gửi lại mã mới!", 
                        "\"expired\":true");
                break;

            case PURPOSE_MISMATCH:
                writeJsonResponse(resp, false, "Mục đích xác thực không hợp lệ!", null);
                break;

            case NOT_FOUND:
            default:
                writeJsonResponse(resp, false, "Không tìm thấy phiên xác thực tương ứng!", null);
                break;
        }
    }

    /**
     * 4. Đặt lại mật khẩu mới sau khi xác thực OTP thành công
     */
    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession httpSession = req.getSession(false);
        String resetToken = req.getParameter("resetToken");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Kiểm tra phiên xác thực an toàn (chặn bypass)
        if (httpSession == null) {
            writeJsonResponse(resp, false, "Phiên làm việc đã kết thúc. Vui lòng thực hiện lại từ đầu!", null);
            return;
        }

        String sessionToken = (String) httpSession.getAttribute("RESET_TOKEN");
        String sessionEmail = (String) httpSession.getAttribute("RESET_EMAIL");
        Long sessionExpires = (Long) httpSession.getAttribute("RESET_EXPIRES");

        if (sessionToken == null || sessionEmail == null || resetToken == null || 
            !sessionToken.equals(resetToken) || sessionExpires == null || System.currentTimeMillis() > sessionExpires) {
            writeJsonResponse(resp, false, "Yêu cầu không hợp lệ hoặc đã hết hạn! Vui lòng xác minh OTP lại.", 
                    "\"invalidSession\":true");
            return;
        }

        // Kiểm tra mật khẩu mới
        if (newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            writeJsonResponse(resp, false, "Vui lòng nhập mật khẩu mới và xác nhận mật khẩu!", null);
            return;
        }

        if (newPassword.length() < 6) {
            writeJsonResponse(resp, false, "Mật khẩu mới phải có tối thiểu 6 ký tự!", null);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            writeJsonResponse(resp, false, "Mật khẩu mới và xác nhận mật khẩu không trùng khớp!", null);
            return;
        }

        try {
            // Cập nhật mật khẩu băm BCrypt vào database
            userService.updatePassword(sessionEmail, newPassword);

            // Xóa cờ phiên đặt lại mật khẩu để ngăn chặn sử dụng lại token
            httpSession.removeAttribute("RESET_TOKEN");
            httpSession.removeAttribute("RESET_EMAIL");
            httpSession.removeAttribute("RESET_EXPIRES");

            writeJsonResponse(resp, true, "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay bằng mật khẩu mới.", 
                    String.format("\"email\":\"%s\"", sessionEmail));

        } catch (Exception e) {
            e.printStackTrace();
            writeJsonResponse(resp, false, "Có lỗi xảy ra khi lưu mật khẩu: " + e.getMessage(), null);
        }
    }

    /**
     * Xuất chuỗi JSON phản hồi chuẩn
     */
    private void writeJsonResponse(HttpServletResponse resp, boolean success, String message, String extraFields) throws IOException {
        PrintWriter out = resp.getWriter();
        StringBuilder json = new StringBuilder("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        if (extraFields != null && !extraFields.trim().isEmpty()) {
            json.append(",").append(extraFields);
        }
        json.append("}");
        out.print(json.toString());
        out.flush();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
