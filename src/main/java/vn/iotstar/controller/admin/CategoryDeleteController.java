package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller xóa danh mục
 * URL: /admin/category/delete
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/admin/category/delete" })
public class CategoryDeleteController extends HttpServlet {

    private final CategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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

        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                cateService.delete(id);
                String msg = URLEncoder.encode("Đã xóa danh mục thành công!", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/category/list?msg=" + msg);
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }
}
