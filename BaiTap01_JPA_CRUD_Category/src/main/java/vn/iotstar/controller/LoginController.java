package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;

/**
 * Controller xử lý đăng nhập và đăng xuất
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IUserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/logout")) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Lấy thông báo từ redirect flash message nếu có
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("message") != null) {
            req.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }

        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        req.setAttribute("email", email);

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ Email và Mật khẩu!");
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();
        User user = userService.login(email, password);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("account", user);
            // Chuyển hướng tới trang quản lý Category
            resp.sendRedirect(req.getContextPath() + "/admin/category");
        } else {
            req.setAttribute("error", "Email hoặc Mật khẩu không chính xác, hoặc tài khoản chưa kích hoạt!");
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }
}
