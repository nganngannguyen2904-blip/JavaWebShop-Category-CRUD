package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;

/**
 * Controller xử lý đặt lại mật khẩu mới sau khi xác thực OTP thành công
 * Ngăn chặn tuyệt đối việc truy cập trực tiếp URL mà chưa qua xác thực OTP
 */
@WebServlet(urlPatterns = {"/reset-password"})
public class ResetPasswordController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IUserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Boolean verified = (Boolean) session.getAttribute("RESET_PASSWORD_VERIFIED");
        String email = (String) session.getAttribute("RESET_PASSWORD_EMAIL");

        // Chặn truy cập trái phép nếu chưa xác thực OTP
        if (verified == null || !verified || email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        req.setAttribute("email", email);
        req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        Boolean verified = (Boolean) session.getAttribute("RESET_PASSWORD_VERIFIED");
        String email = (String) session.getAttribute("RESET_PASSWORD_EMAIL");

        // Chặn truy cập trái phép
        if (verified == null || !verified || email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập mật khẩu mới và xác nhận mật khẩu!");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        if (newPassword.length() < 6) {
            req.setAttribute("error", "Mật khẩu mới phải có tối thiểu 6 ký tự!");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        try {
            // Cập nhật mật khẩu băm BCrypt vào CSDL
            userService.updatePassword(email, newPassword);

            // Xóa cờ cấp quyền trong session (OTP chỉ dùng 1 lần)
            session.removeAttribute("RESET_PASSWORD_VERIFIED");
            session.removeAttribute("RESET_PASSWORD_EMAIL");

            session.setAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
            resp.sendRedirect(req.getContextPath() + "/login");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra khi cập nhật mật khẩu: " + e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
        }
    }
}
