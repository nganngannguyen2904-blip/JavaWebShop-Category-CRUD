package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Category;
import vn.iotstar.model.User;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

import java.io.IOException;
import java.util.List;

/**
 * Controller danh sách danh mục
 * URL: /admin/category/list
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admin/category/list")
public class CategoryListController extends HttpServlet {

    private final CategoryService categoryService = new CategoryServiceImpl();

    /**
     * GET: Hiển thị danh sách tất cả danh mục
     * Hỗ trợ tìm kiếm theo từ khóa
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
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

        // Xử lý tìm kiếm
        String keyword = req.getParameter("keyword");
        List<Category> cateList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            cateList = categoryService.search(keyword.trim());
            req.setAttribute("keyword", keyword.trim());
        } else {
            cateList = categoryService.getAll();
        }

        req.setAttribute("cateList", cateList);

        // Hiển thị thông báo thành công sau thêm/sửa/xóa
        String msg = req.getParameter("msg");
        if (msg != null) {
            req.setAttribute("successMsg", msg);
        }

        req.getRequestDispatcher(Constant.Path.CATEGORY_LIST).forward(req, resp);
    }
}
