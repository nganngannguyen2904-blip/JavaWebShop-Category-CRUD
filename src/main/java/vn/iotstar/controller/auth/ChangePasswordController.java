package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;

/**
 * Controller xử lý chức năng Đổi mật khẩu cho người dùng đang đăng nhập
 * URL: /change-password
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/change-password")
public class ChangePasswordController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }
        req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        String oldPassword = req.getParameter("oldpassword");
        String newPassword = req.getParameter("newpassword");
        String confirmPassword = req.getParameter("confirmpassword");

        if (oldPassword == null || oldPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập đầy đủ các trường thông tin!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
            return;
        }

        if (newPassword.length() < 6) {
            req.setAttribute("alert", "Mật khẩu mới phải có tối thiểu 6 ký tự!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
            return;
        }

        if (newPassword.equals(oldPassword)) {
            req.setAttribute("alert", "Mật khẩu mới không được trùng với mật khẩu cũ!");
            req.setAttribute("alertType", "warning");
            req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu xác nhận không trùng khớp!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
            return;
        }

        boolean success = userService.changePassword(currentUser.getId(), oldPassword, newPassword);
        if (success) {
            req.setAttribute("alert", "Đổi mật khẩu thành công! Mật khẩu mới của bạn đã được cập nhật an toàn.");
            req.setAttribute("alertType", "success");
        } else {
            req.setAttribute("alert", "Mật khẩu cũ không chính xác!");
            req.setAttribute("alertType", "danger");
        }
        req.getRequestDispatcher(Constant.Path.CHANGE_PASSWORD).forward(req, resp);
    }
}
