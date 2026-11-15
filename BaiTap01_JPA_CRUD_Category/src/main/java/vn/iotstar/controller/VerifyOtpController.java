package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpManager;

/**
 * Controller xử lý xác minh mã OTP 6 chữ số và gửi lại mã OTP
 */
@WebServlet(urlPatterns = {"/verify-otp", "/resend-otp"})
public class VerifyOtpController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IUserService userService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.emailService = EmailService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String purpose = req.getParameter("purpose");

        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        email = email.trim().toLowerCase();
        OtpManager.OtpSession session = OtpManager.getSession(email);

        if (session == null) {
            req.setAttribute("error", "Phiên xác thực đã hết hạn hoặc không tồn tại. Vui lòng thực hiện lại từ đầu!");
        } else {
            req.setAttribute("email", email);
            req.setAttribute("purpose", session.getPurpose());
            req.setAttribute("remainingAttempts", OtpManager.MAX_FAILED_ATTEMPTS - session.getFailedAttempts());
            req.setAttribute("cooldown", OtpManager.getRemainingCooldownSeconds(email));
        }

        req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();
        if (uri.endsWith("/resend-otp")) {
            handleResendOtp(req, resp);
        } else {
            handleVerifyOtp(req, resp);
        }
    }

    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");
        String purpose = req.getParameter("purpose");

        if (email == null || otp == null || email.trim().isEmpty() || otp.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ mã xác thực OTP 6 chữ số!");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();
        otp = otp.trim();

        OtpManager.OtpSession session = OtpManager.getSession(email);
        if (session == null) {
            req.setAttribute("error", "Phiên xác thực đã hết hiệu lực (quá 5 phút) hoặc đã bị hủy. Vui lòng yêu cầu lại mã mới!");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        OtpManager.VerifyResult result = OtpManager.verifyOtp(email, otp);

        switch (result) {
            case SUCCESS:
                if ("REGISTER".equalsIgnoreCase(session.getPurpose())) {
                    // Kích hoạt và lưu tài khoản vào CSDL
                    User pendingUser = (User) session.getPayload();
                    if (pendingUser != null) {
                        userService.register(pendingUser);
                    }
                    OtpManager.removeSession(email);
                    req.getSession().setAttribute("message", "Đăng ký và kích hoạt tài khoản thành công! Vui lòng đăng nhập.");
                    resp.sendRedirect(req.getContextPath() + "/login");
                } else if ("FORGOT_PASSWORD".equalsIgnoreCase(session.getPurpose()) || "RESET_PASSWORD".equalsIgnoreCase(session.getPurpose())) {
                    // Cấp quyền chuyển sang trang đặt lại mật khẩu mới
                    HttpSession httpSession = req.getSession();
                    String resetToken = java.util.UUID.randomUUID().toString();
                    httpSession.setAttribute("RESET_PASSWORD_VERIFIED", true);
                    httpSession.setAttribute("RESET_PASSWORD_EMAIL", email);
                    httpSession.setAttribute("RESET_TOKEN", resetToken);
                    httpSession.setAttribute("RESET_EMAIL", email);
                    httpSession.setAttribute("RESET_EXPIRES", System.currentTimeMillis() + 10 * 60 * 1000L);
                    OtpManager.removeSession(email);
                    resp.sendRedirect(req.getContextPath() + "/reset-password");
                } else {
                    OtpManager.removeSession(email);
                    resp.sendRedirect(req.getContextPath() + "/login");
                }
                break;

            case INVALID:
                int remaining = OtpManager.MAX_FAILED_ATTEMPTS - session.getFailedAttempts();
                req.setAttribute("error", "Mã OTP không chính xác! Bạn còn " + remaining + " lần nhập thử trước khi mã bị hủy.");
                req.setAttribute("email", email);
                req.setAttribute("purpose", session.getPurpose());
                req.setAttribute("remainingAttempts", remaining);
                req.setAttribute("cooldown", OtpManager.getRemainingCooldownSeconds(email));
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case EXCEEDED_ATTEMPTS:
                req.setAttribute("error", "Bạn đã nhập sai mã OTP quá " + OtpManager.MAX_FAILED_ATTEMPTS + " lần. Vì lý do bảo mật, mã OTP này đã bị hủy! Vui lòng yêu cầu gửi lại mã mới.");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case EXPIRED:
                req.setAttribute("error", "Mã OTP đã hết hạn sử dụng (quá 5 phút). Vui lòng nhấn 'Gửi lại mã OTP'!");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case NOT_FOUND:
            default:
                req.setAttribute("error", "Không tìm thấy phiên xác thực tương ứng!");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;
        }
    }

    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        email = email.trim().toLowerCase();
        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            req.setAttribute("error", "Vui lòng đợi " + cooldown + " giây nữa trước khi gửi lại OTP!");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        OtpManager.OtpSession oldSession = OtpManager.getSession(email);
        String purpose = oldSession != null ? oldSession.getPurpose() : req.getParameter("purpose");
        Object payload = oldSession != null ? oldSession.getPayload() : null;

        try {
            String newOtp = OtpManager.generateOtp();
            OtpManager.createOtpSession(email, newOtp, purpose != null ? purpose : "AUTH", payload);

            String purposeText = "FORGOT_PASSWORD".equalsIgnoreCase(purpose) ? "ĐẶT LẠI MẬT KHẨU" : "ĐĂNG KÝ TÀI KHOẢN";
            String recipientName = (payload instanceof User) ? ((User) payload).getFullname() : email;

            emailService.sendOtpEmail(email, recipientName, newOtp, purposeText);

            req.setAttribute("message", "Đã gửi mã OTP mới tới email của bạn. Vui lòng kiểm tra hộp thư!");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.setAttribute("remainingAttempts", OtpManager.MAX_FAILED_ATTEMPTS);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi gửi lại email OTP: " + e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
        }
    }
}
