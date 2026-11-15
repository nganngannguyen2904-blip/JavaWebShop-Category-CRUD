package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.util.Constant;

import java.io.IOException;

/**
 * Controller trang chủ Admin
 * URL: /admin/home
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admin/home")
public class AdminHomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập và quyền admin
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        if (user.getRoleid() != Constant.ROLE_ADMIN && user.getRoleid() != Constant.ROLE_MANAGER) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher(Constant.Path.ADMIN_HOME).forward(req, resp);
    }
}
