package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.BCrypt;
import vn.iotstar.util.Constant;
import vn.iotstar.util.OtpManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.regex.Pattern;

/**
 * Controller xử lý chức năng đăng ký tài khoản khách hàng kèm mã xác thực OTP qua email
 * URL: /register
 * Đáp ứng đầy đủ quy chuẩn nghiệp vụ thực tế, mã hóa mật khẩu BCrypt và kích hoạt OTP
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();
    private final EmailService emailService = EmailService.getInstance();

    // Biểu thức chính quy kiểm tra định dạng
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0[3|5|7|8|9])[0-9]{8}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Nếu đã đăng nhập, chuyển hướng về trang chủ
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmpassword");

        // Lưu lại dữ liệu cũ để hiển thị lại nếu có lỗi
        req.setAttribute("oldFullname", fullname != null ? fullname.trim() : "");
        req.setAttribute("oldEmail", email != null ? email.trim() : "");
        req.setAttribute("oldPhone", phone != null ? phone.trim() : "");
        req.setAttribute("oldUsername", username != null ? username.trim() : "");

        // 1. Kiểm tra không được để trống các trường bắt buộc
        if (fullname == null || fullname.trim().isEmpty()) {
            sendError(req, resp, "Vui lòng nhập họ và tên của bạn!");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            sendError(req, resp, "Vui lòng nhập địa chỉ email!");
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            sendError(req, resp, "Vui lòng nhập tên đăng nhập!");
            return;
        }
        if (password == null || password.isEmpty()) {
            sendError(req, resp, "Vui lòng nhập mật khẩu!");
            return;
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            sendError(req, resp, "Vui lòng xác nhận lại mật khẩu!");
            return;
        }

        String cleanFullname = fullname.trim();
        String cleanEmail = email.trim().toLowerCase();
        String cleanPhone = phone != null ? phone.trim() : "";
        String cleanUsername = username.trim();

        // 2. Kiểm tra định dạng Email hợp lệ
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            sendError(req, resp, "Địa chỉ email không đúng định dạng (Ví dụ: user@gmail.com)!");
            return;
        }

        // 3. Kiểm tra định dạng Số điện thoại (nếu có nhập)
        if (!cleanPhone.isEmpty() && !PHONE_PATTERN.matcher(cleanPhone).matches()) {
            sendError(req, resp, "Số điện thoại không hợp lệ (Phải là 10 chữ số đầu 03, 05, 07, 08, 09)!");
            return;
        }

        // 4. Kiểm tra định dạng Username
        if (!USERNAME_PATTERN.matcher(cleanUsername).matches()) {
            sendError(req, resp, "Tên đăng nhập từ 3 đến 30 ký tự, chỉ gồm chữ cái, số và dấu gạch dưới (_), không chứa dấu cách hay ký tự đặc biệt!");
            return;
        }

        // 5. Kiểm tra chính sách mật khẩu (Tối thiểu 6 ký tự)
        if (password.length() < 6) {
            sendError(req, resp, "Mật khẩu bảo mật phải có độ dài tối thiểu từ 6 ký tự trở lên!");
            return;
        }

        // 6. Kiểm tra mật khẩu xác nhận phải trùng khớp
        if (!password.equals(confirmPassword)) {
            sendError(req, resp, "Mật khẩu xác nhận không trùng khớp với mật khẩu đã nhập!");
            return;
        }

        // 7. Kiểm tra trùng lặp Email trong cơ sở dữ liệu
        if (userService.checkExistEmail(cleanEmail)) {
            sendError(req, resp, "Địa chỉ email '" + cleanEmail + "' đã được sử dụng bởi tài khoản khác!");
            return;
        }

        // 8. Kiểm tra trùng lặp Tên đăng nhập trong cơ sở dữ liệu
        if (userService.checkExistUsername(cleanUsername)) {
            sendError(req, resp, "Tên đăng nhập '" + cleanUsername + "' đã tồn tại! Vui lòng chọn tên đăng nhập khác.");
            return;
        }

        // 9. Kiểm tra trùng lặp Số điện thoại (nếu có)
        if (!cleanPhone.isEmpty() && userService.checkExistPhone(cleanPhone)) {
            sendError(req, resp, "Số điện thoại '" + cleanPhone + "' đã được đăng ký!");
            return;
        }

        // 10. Kiểm tra cooldown gửi lại OTP nếu có phiên cũ đang chờ
        long cooldown = OtpManager.getRemainingCooldownSeconds(cleanEmail);
        if (cooldown > 0) {
            sendError(req, resp, "Thao tác quá nhanh! Vui lòng đợi " + cooldown + " giây trước khi đăng ký lại.");
            return;
        }

        // 11. Chuẩn bị đối tượng User tạm thời (chưa kích hoạt, băm BCrypt mật khẩu)
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        User pendingUser = new User();
        pendingUser.setEmail(cleanEmail);
        pendingUser.setUserName(cleanUsername);
        pendingUser.setFullName(cleanFullname);
        pendingUser.setPassWord(hashedPassword);
        pendingUser.setAvatar("avata.png");
        pendingUser.setRoleid(Constant.ROLE_USER);
        pendingUser.setPhone(cleanPhone);
        pendingUser.setStatus(1); // Sẽ được kích hoạt khi xác minh OTP
        pendingUser.setFailedAttempts(0);
        pendingUser.setCreatedDate(new Date(System.currentTimeMillis()));

        try {
            // 12. Sinh mã OTP 6 chữ số ngẫu nhiên
            String otp = OtpManager.generateOtp();

            // 13. Lưu phiên OTP với mục đích "REGISTER" và dữ liệu user chờ kích hoạt
            OtpManager.createOtpSession(cleanEmail, otp, "REGISTER", pendingUser);

            // 14. Gửi email xác thực OTP qua Gmail SMTP
            emailService.sendOtpEmail(cleanEmail, cleanFullname, otp, "KÍCH HOẠT TÀI KHOẢN MỚI");

            // 15. Chuyển hướng người dùng sang trang xác minh OTP
            resp.sendRedirect(req.getContextPath() + "/verify-otp?email=" 
                    + URLEncoder.encode(cleanEmail, StandardCharsets.UTF_8) + "&purpose=REGISTER");

        } catch (Exception e) {
            e.printStackTrace();
            sendError(req, resp, "Không thể gửi email OTP: " + e.getMessage() + ". Vui lòng kiểm tra lại cấu hình email!");
        }
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.setAttribute("alertType", "danger");
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }
}
