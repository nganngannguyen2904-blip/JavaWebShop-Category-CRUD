package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.model.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.OtpManager;

import java.io.IOException;

/**
 * Controller xử lý Quên mật khẩu bằng Gmail OTP thật
 * URL: /forgot-password
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();
    private final EmailService emailService = EmailService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập địa chỉ email tài khoản của bạn!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        String cleanEmail = email.trim().toLowerCase();
        User user = userService.getByEmail(cleanEmail);
        if (user == null) {
            req.setAttribute("alert", "Không tìm thấy tài khoản nào gắn với Gmail '" + cleanEmail + "'!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("oldEmail", cleanEmail);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        // Kiểm tra cooldown rate limit 60s
        long cooldown = OtpManager.getRemainingCooldownSeconds(cleanEmail);
        if (cooldown > 0) {
            req.setAttribute("alert", "Vui lòng đợi " + cooldown + " giây nữa trước khi gửi lại mã OTP!");
            req.setAttribute("alertType", "warning");
            req.setAttribute("oldEmail", cleanEmail);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        try {
            // 1. Sinh mã OTP 6 chữ số ngẫu nhiên thật
            String otp = OtpManager.generateOtp();

            // 2. Lưu phiên OTP với mục đích RESET_PASSWORD
            OtpManager.createOtpSession(cleanEmail, otp, "RESET_PASSWORD", user);

            // 3. Gửi email mã OTP thật qua Gmail SMTP
            emailService.sendOtpEmail(cleanEmail, user.getFullName(), otp, "ĐẶT LẠI MẬT KHẨU");

            // 4. Chuyển sang giao diện nhập 6 ô OTP kèm bộ đếm 60 giây
            req.setAttribute("step", "2");
            req.setAttribute("currentEmail", cleanEmail);
            req.setAttribute("maskedEmail", OtpManager.maskEmail(cleanEmail));
            req.setAttribute("cooldown", 60);
            req.setAttribute("alert", "Mã xác thực OTP 6 chữ số đã được gửi đến Gmail của bạn. Vui lòng kiểm tra hộp thư!");
            req.setAttribute("alertType", "success");
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("alert", "Không thể gửi email OTP qua Gmail: " + e.getMessage());
            req.setAttribute("alertType", "danger");
            req.setAttribute("oldEmail", cleanEmail);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
        }
    }
}
