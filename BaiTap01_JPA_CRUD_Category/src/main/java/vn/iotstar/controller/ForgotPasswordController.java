package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpManager;

/**
 * Controller xử lý quên mật khẩu qua Gmail OTP
 */
@WebServlet(urlPatterns = {"/forgot-password"})
public class ForgotPasswordController extends HttpServlet {

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
        req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        req.setAttribute("email", email);

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập địa chỉ Gmail đã đăng ký!");
            req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();

        // 1. Kiểm tra email có tồn tại trong hệ thống không
        User user = userService.findByEmail(email);
        if (user == null) {
            req.setAttribute("error", "Địa chỉ Gmail này chưa được đăng ký trong hệ thống!");
            req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        // 2. Kiểm tra cooldown gửi lại (tối thiểu 60s)
        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            req.setAttribute("error", "Vui lòng đợi " + cooldown + " giây nữa trước khi gửi lại yêu cầu!");
            req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        try {
            // 3. Sinh mã OTP 6 chữ số ngẫu nhiên
            String otp = OtpManager.generateOtp();

            // Lưu phiên OTP với mục đích RESET_PASSWORD
            OtpManager.createOtpSession(email, otp, "RESET_PASSWORD", user);

            // 4. Gửi email mã OTP
            emailService.sendOtpEmail(email, user.getFullname(), otp, "ĐẶT LẠI MẬT KHẨU");

            // 5. Chuyển sang màn hình nhập OTP
            resp.sendRedirect(req.getContextPath() + "/verify-otp?email=" + email + "&purpose=RESET_PASSWORD");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể gửi email OTP: " + e.getMessage());
            req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
        }
    }
}
