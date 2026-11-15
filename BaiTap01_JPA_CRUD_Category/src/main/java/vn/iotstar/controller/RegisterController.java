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
 * Controller xử lý đăng ký tài khoản và gửi mã xác thực Gmail OTP
 */
@WebServlet(urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

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
        req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        req.setAttribute("fullname", fullname);
        req.setAttribute("email", email);

        // 1. Kiểm tra đầu vào
        if (fullname == null || fullname.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin!");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();

        // 2. Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            req.setAttribute("error", "Địa chỉ email không đúng định dạng!");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // 3. Kiểm tra độ dài mật khẩu và khớp xác nhận
        if (password.length() < 6) {
            req.setAttribute("error", "Mật khẩu phải chứa ít nhất 6 ký tự!");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp nhau!");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // 4. Kiểm tra email đã đăng ký trong hệ thống chưa
        if (userService.checkExistEmail(email)) {
            req.setAttribute("error", "Email [" + email + "] đã được đăng ký trong hệ thống. Vui lòng đăng nhập hoặc dùng email khác!");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // 5. Kiểm tra rate limit gửi lại OTP (cooldown tối thiểu 60s)
        long cooldown = OtpManager.getRemainingCooldownSeconds(email);
        if (cooldown > 0) {
            req.setAttribute("error", "Thao tác quá nhanh! Vui lòng đợi " + cooldown + " giây trước khi yêu cầu mã OTP mới.");
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
            return;
        }

        try {
            // 6. Sinh mã OTP 6 chữ số ngẫu nhiên bảo mật
            String otp = OtpManager.generateOtp();

            // Tạo đối tượng User tạm thời (chưa lưu vào DB khi chưa xác minh OTP)
            User pendingUser = new User();
            pendingUser.setFullname(fullname.trim());
            pendingUser.setEmail(email);
            pendingUser.setPassword(password); // Sẽ được băm BCrypt khi xác minh thành công
            pendingUser.setStatus(1); // Trạng thái kích hoạt

            // Lưu phiên OTP trong bộ nhớ đệm
            OtpManager.createOtpSession(email, otp, "REGISTER", pendingUser);

            // 7. Gửi email xác thực OTP qua Gmail SMTP
            emailService.sendOtpEmail(email, fullname, otp, "ĐĂNG KÝ TÀI KHOẢN MỚI");

            // 8. Chuyển hướng người dùng sang trang xác minh OTP
            resp.sendRedirect(req.getContextPath() + "/verify-otp?email=" + email + "&purpose=REGISTER");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể gửi email OTP: " + e.getMessage());
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
        }
    }
}
