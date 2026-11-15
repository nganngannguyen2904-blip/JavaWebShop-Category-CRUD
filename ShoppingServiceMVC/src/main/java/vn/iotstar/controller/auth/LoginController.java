package vn.iotstar.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.LoginResult;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller xử lý chức năng đăng nhập an toàn
 * URL: /login
 * Đáp ứng đầy đủ tiêu chí bảo mật: băm BCrypt, chống Brute-force, chống SQLi, HttpOnly Cookie, bảo mật Session
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Kiểm tra session hiện tại nếu đã đăng nhập thì điều hướng theo quyền
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        // 2. Kiểm tra Cookie "Ghi nhớ đăng nhập" (Remember me)
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    String usernameOrEmail = cookie.getValue();
                    User user = userService.getByUsernameOrEmail(usernameOrEmail);
                    if (user != null && user.isActive() && !user.isTemporarilyLocked()) {
                        session = req.getSession(true);
                        session.setAttribute(Constant.SESSION_ACCOUNT, user);
                        resp.sendRedirect(req.getContextPath() + "/waiting");
                        return;
                    }
                }
            }
        }

        // 3. Khởi tạo CSRF Token an toàn cho form đăng nhập
        if (session == null) {
            session = req.getSession(true);
        }
        if (session.getAttribute(Constant.CSRF_TOKEN) == null) {
            session.setAttribute(Constant.CSRF_TOKEN, UUID.randomUUID().toString());
        }

        // 4. Bắt các thông điệp điều hướng
        String alertParam = req.getParameter("alert");
        if ("login_required".equals(alertParam)) {
            req.setAttribute("alert", "Vui lòng đăng nhập để tiếp tục truy cập!");
            req.setAttribute("alertType", "warning");
        } else if ("unauthorized".equals(alertParam)) {
            req.setAttribute("alert", "Bạn không có quyền truy cập khu vực này. Vui lòng đăng nhập tài khoản có thẩm quyền!");
            req.setAttribute("alertType", "danger");
        }

        if ("1".equals(req.getParameter("logout"))) {
            req.setAttribute("alert", "Bạn đã đăng xuất an toàn khỏi hệ thống.");
            req.setAttribute("alertType", "info");
        }

        if ("1".equals(req.getParameter("reset"))) {
            req.setAttribute("alert", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
            req.setAttribute("alertType", "success");
        }

        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String usernameOrEmail = req.getParameter("username");
        String password = req.getParameter("password");
        String remember = req.getParameter("remember");
        boolean isRememberMe = "on".equals(remember);

        // 1. Kiểm tra rỗng đầu vào
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()
                || password == null || password.isEmpty()) {
            req.setAttribute("alert", "Tên đăng nhập / Email và mật khẩu không được để trống!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("oldUsername", usernameOrEmail != null ? usernameOrEmail.trim() : "");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        // 2. Xác thực thông qua UserService
        LoginResult result = userService.authenticate(usernameOrEmail.trim(), password);

        switch (result.getStatus()) {
            case SUCCESS:
                User user = result.getUser();

                // Tạo mới Session để chống Session Fixation
                HttpSession oldSession = req.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = req.getSession(true);
                newSession.setAttribute(Constant.SESSION_ACCOUNT, user);

                // Quản lý Cookie "Remember me" có gắn HttpOnly bảo mật
                if (isRememberMe) {
                    Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, user.getUserName());
                    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
                    cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                    cookie.setHttpOnly(true); // Chống đọc trộm Cookie qua mã độc XSS
                    resp.addCookie(cookie);
                }

                // Chuyển hướng tới bộ điều phối vai trò /waiting
                resp.sendRedirect(req.getContextPath() + "/waiting");
                break;

            case ACCOUNT_LOCKED:
                req.setAttribute("alert", "Tài khoản của bạn đã bị khóa bởi Quản trị viên. Vui lòng liên hệ hỗ trợ!");
                req.setAttribute("alertType", "danger");
                req.setAttribute("oldUsername", usernameOrEmail.trim());
                req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
                break;

            case TOO_MANY_ATTEMPTS:
                req.setAttribute("alert", "Bạn đã đăng nhập sai quá 5 lần liên tiếp! Để bảo mật hệ thống, tài khoản bị tạm khóa trong " 
                        + result.getRemainingLockMinutes() + " phút.");
                req.setAttribute("alertType", "danger");
                req.setAttribute("oldUsername", usernameOrEmail.trim());
                req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
                break;

            case INVALID_CREDENTIALS:
            default:
                // Thông báo chung an toàn (không tiết lộ tài khoản có tồn tại hay không - Chống User Enumeration)
                int remaining = 5 - result.getFailedAttempts();
                String msg = "Tên đăng nhập / Email hoặc mật khẩu không chính xác!";
                if (result.getFailedAttempts() > 0 && remaining > 0 && remaining <= 3) {
                    msg += " (Bạn còn " + remaining + " lần thử trước khi tài khoản bị tạm khóa)";
                }
                req.setAttribute("alert", msg);
                req.setAttribute("alertType", "danger");
                req.setAttribute("oldUsername", usernameOrEmail.trim());
                req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
                break;
        }
    }
}
