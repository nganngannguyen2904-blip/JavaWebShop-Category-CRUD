package vn.iotstar.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User;

import java.io.IOException;

/**
 * Filter bảo vệ phân quyền tầng Backend cho các URL quản trị /admin/*
 * Đảm bảo người dùng thường (Role 5 - USER) không thể truy cập trái phép dù đổi giao diện hay gõ trực tiếp URL
 */
@WebFilter(urlPatterns = "/admin/*")
public class AdminSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        // 1. Nếu chưa đăng nhập -> Chuyển hướng sang trang Login kèm thông báo
        if (user == null) {
            String requestUri = req.getRequestURI();
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        // 2. Nếu đã đăng nhập nhưng KHÔNG CÓ QUYỀN QUẢN TRỊ (Chỉ là Role 5 - USER) -> Chặn 403 Forbidden
        if (!user.canAccessAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("deniedUser", user.getUserName());
            req.setAttribute("userRole", "USER (Role 5)");
            req.getRequestDispatcher(Constant.Path.ACCESS_DENIED).forward(req, resp);
            return;
        }

        // 3. Đúng quyền Admin (Role 1) hoặc Manager (Role 2) -> Cho phép đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
