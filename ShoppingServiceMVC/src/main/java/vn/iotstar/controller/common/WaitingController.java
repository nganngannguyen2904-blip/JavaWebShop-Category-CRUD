package vn.iotstar.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.util.Constant;

import java.io.IOException;

/**
 * Controller điều hướng sau đăng nhập dựa trên Role
 * URL: /waiting
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/waiting")
public class WaitingController extends HttpServlet {

    /**
     * GET: Kiểm tra role và redirect đến trang tương ứng
     * - Role 1 (Admin) → /admin/home
     * - Role 2 (Manager) → /manager/home
     * - Khác (User) → /home
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            User user = (User) session.getAttribute(Constant.SESSION_ACCOUNT);

            if (user.getRoleid() == Constant.ROLE_ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
            } else if (user.getRoleid() == Constant.ROLE_MANAGER) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
