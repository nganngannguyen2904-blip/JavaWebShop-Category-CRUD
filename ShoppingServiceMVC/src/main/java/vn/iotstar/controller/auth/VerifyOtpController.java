package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import vn.iotstar.model.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpManager;

/**
 * Controller xử lý xác thực mã OTP kích hoạt tài khoản đăng ký và gửi lại mã OTP
 * URL: /verify-otp, /resend-otp
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/verify-otp", "/resend-otp"})
public class VerifyOtpController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();
    private final EmailService emailService = EmailService.getInstance();

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
            req.setAttribute("alert", "Phiên xác thực đã hết hiệu lực hoặc chưa được khởi tạo. Vui lòng thực hiện lại!");
            req.setAttribute("alertType", "warning");
        } else {
            req.setAttribute("email", email);
            req.setAttribute("maskedEmail", OtpManager.maskEmail(email));
            req.setAttribute("purpose", session.getPurpose());
            req.setAttribute("remainingAttempts", OtpManager.MAX_FAILED_ATTEMPTS - session.getFailedAttempts());
            req.setAttribute("cooldown", OtpManager.getRemainingCooldownSeconds(email));
        }

        req.setAttribute("email", email);
        req.setAttribute("purpose", (purpose != null && !purpose.isEmpty()) ? purpose : "REGISTER");
        req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();

        if ("/resend-otp".equals(servletPath)) {
            handleResendOtp(req, resp);
        } else {
            handleVerifyOtp(req, resp);
        }
    }

    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String purpose = req.getParameter("purpose");

        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        email = email.trim().toLowerCase();
        OtpManager.OtpSession session = OtpManager.getSession(email);

        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            req.setAttribute("alert", "Vui lòng đợi " + cooldown + " giây nữa để gửi lại mã OTP!");
            req.setAttribute("alertType", "warning");
            req.setAttribute("email", email);
            req.setAttribute("maskedEmail", OtpManager.maskEmail(email));
            req.setAttribute("purpose", purpose);
            req.setAttribute("cooldown", cooldown);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        try {
            String newOtp = OtpManager.generateOtp();
            Object payload = (session != null) ? session.getPayload() : null;
            String sessPurpose = (session != null) ? session.getPurpose() : (purpose != null ? purpose : "REGISTER");

            OtpManager.createOtpSession(email, newOtp, sessPurpose, payload);

            String recipientName = "Khách Hàng";
            if (payload instanceof User) {
                recipientName = ((User) payload).getFullName();
            }

            emailService.sendOtpEmail(email, recipientName, newOtp, "GỬI LẠI MÃ KÍCH HOẠT");

            req.setAttribute("alert", "Mã OTP mới đã được gửi lại vào email của bạn!");
            req.setAttribute("alertType", "success");
            req.setAttribute("email", email);
            req.setAttribute("maskedEmail", OtpManager.maskEmail(email));
            req.setAttribute("purpose", sessPurpose);
            req.setAttribute("cooldown", 60);
            req.setAttribute("remainingAttempts", OtpManager.MAX_FAILED_ATTEMPTS);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("alert", "Lỗi gửi email: " + e.getMessage());
            req.setAttribute("alertType", "danger");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
        }
    }

    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");
        String purpose = req.getParameter("purpose");

        if (email == null || otp == null || email.trim().isEmpty() || otp.trim().isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập đầy đủ mã OTP 6 chữ số!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();
        otp = otp.trim().replaceAll("\\s+", "");

        OtpManager.OtpSession session = OtpManager.getSession(email);
        if (session == null) {
            req.setAttribute("alert", "Phiên xác thực đã hết hạn hoặc không tồn tại. Vui lòng đăng ký lại!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("email", email);
            req.setAttribute("purpose", purpose);
            req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
            return;
        }

        String expectedPurpose = (purpose != null && !purpose.isEmpty()) ? purpose : session.getPurpose();
        OtpManager.VerifyResult result = OtpManager.verifyOtp(email, otp, expectedPurpose);

        switch (result) {
            case SUCCESS:
                if ("REGISTER".equalsIgnoreCase(session.getPurpose())) {
                    // Kích hoạt tài khoản và lưu chính thức vào cơ sở dữ liệu
                    User pendingUser = (User) session.getPayload();
                    if (pendingUser != null) {
                        pendingUser.setStatus(1); // 1: Đã kích hoạt
                        userService.insert(pendingUser);
                    }
                    OtpManager.removeSession(email);
                    resp.sendRedirect(req.getContextPath() + "/login?registered=1");
                } else if ("RESET_PASSWORD".equalsIgnoreCase(session.getPurpose()) || "FORGOT_PASSWORD".equalsIgnoreCase(session.getPurpose())) {
                    String resetToken = UUID.randomUUID().toString();
                    HttpSession httpSession = req.getSession(true);
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
                req.setAttribute("alert", "Mã OTP không chính xác! Bạn còn " + remaining + " lần nhập thử.");
                req.setAttribute("alertType", "danger");
                req.setAttribute("email", email);
                req.setAttribute("maskedEmail", OtpManager.maskEmail(email));
                req.setAttribute("purpose", session.getPurpose());
                req.setAttribute("remainingAttempts", remaining);
                req.setAttribute("cooldown", OtpManager.getRemainingCooldownSeconds(email));
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case EXCEEDED_ATTEMPTS:
                req.setAttribute("alert", "Bạn đã nhập sai mã OTP quá 5 lần. Mã đã bị hủy vì lý do an toàn! Vui lòng đăng ký lại.");
                req.setAttribute("alertType", "danger");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case EXPIRED:
                req.setAttribute("alert", "Mã OTP đã hết hạn sử dụng (quá 5 phút). Vui lòng yêu cầu gửi lại mã mới!");
                req.setAttribute("alertType", "warning");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;

            case PURPOSE_MISMATCH:
            case NOT_FOUND:
            default:
                req.setAttribute("alert", "Mã xác thực không hợp lệ!");
                req.setAttribute("alertType", "danger");
                req.setAttribute("email", email);
                req.setAttribute("purpose", purpose);
                req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
                break;
        }
    }
}
