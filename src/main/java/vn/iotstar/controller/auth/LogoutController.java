package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.util.Constant;

import java.io.IOException;

/**
 * Controller xử lý đăng xuất an toàn
 * URL: /logout
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Hủy phiên làm việc Session trên Server
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute(Constant.SESSION_ACCOUNT);
            session.invalidate();
        }

        // 2. Thu hồi toàn bộ Cookie Remember Me
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                    cookie.setHttpOnly(true);
                    resp.addCookie(cookie);
                }
            }
        }

        // 3. Chuyển hướng về trang đăng nhập kèm thông báo
        resp.sendRedirect(req.getContextPath() + "/login?logout=1");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
