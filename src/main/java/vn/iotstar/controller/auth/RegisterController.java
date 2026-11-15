package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Controller xử lý chức năng đăng ký tài khoản khách hàng
 * URL: /register
 * Đáp ứng đầy đủ quy chuẩn nghiệp vụ thực tế và an toàn thông tin
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

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

        // 10. Tiến hành đăng ký (Backend cố định role = 5 USER, băm BCrypt an toàn)
        boolean isSuccess = userService.register(cleanUsername, password, cleanEmail, cleanFullname, cleanPhone);

        if (isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } else {
            sendError(req, resp, "Có lỗi xảy ra trong quá trình lưu dữ liệu. Vui lòng thử lại!");
        }
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.setAttribute("alertType", "danger");
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }
}
