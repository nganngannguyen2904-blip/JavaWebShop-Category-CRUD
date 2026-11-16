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
 * Đồng thời chống duyệt lại bằng nút Back sau khi đăng xuất bằng Cache-Control Headers
 */
@WebFilter(urlPatterns = {"/admin/*", "/video/add", "/video/edit", "/video/delete"})
public class AdminSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Vô hiệu hóa cache trình duyệt đối với các trang quản trị và thao tác dữ liệu
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        // 1. Nếu chưa đăng nhập -> Chuyển hướng sang trang Login kèm thông báo
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        String uri = req.getRequestURI();

        // 2. Nếu là URL quản trị /admin/* -> Yêu cầu quyền Admin (Role 1) hoặc Manager (Role 2)
        if (uri.contains("/admin/")) {
            if (!user.canAccessAdmin()) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                req.setAttribute("deniedUser", user.getUserName());
                req.setAttribute("userRole", "USER (Role 5)");
                req.getRequestDispatcher(Constant.Path.ACCESS_DENIED).forward(req, resp);
                return;
            }
        }

        // 3. Đúng quyền -> Cho phép đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
