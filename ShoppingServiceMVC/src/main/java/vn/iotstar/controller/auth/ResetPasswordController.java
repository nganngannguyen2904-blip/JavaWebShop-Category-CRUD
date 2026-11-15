package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;

/**
 * Controller xử lý Đặt lại mật khẩu với Token
 * URL: /reset-password
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/reset-password")
public class ResetPasswordController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token != null) {
            req.setAttribute("token", token.trim());
        }
        req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String token = req.getParameter("token");
        String newPassword = req.getParameter("newpassword");
        String confirmPassword = req.getParameter("confirmpassword");

        req.setAttribute("token", token != null ? token.trim() : "");

        if (token == null || token.trim().isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập mã xác thực token!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("alert", "Mật khẩu mới phải có tối thiểu 6 ký tự!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu xác nhận không trùng khớp!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        boolean success = userService.resetPasswordWithToken(token.trim(), newPassword);
        if (success) {
            resp.sendRedirect(req.getContextPath() + "/login?reset=1");
        } else {
            req.setAttribute("alert", "Mã xác thực không đúng hoặc đã hết hạn hiệu lực (quá 15 phút). Vui lòng thử lại!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
        }
    }
}
