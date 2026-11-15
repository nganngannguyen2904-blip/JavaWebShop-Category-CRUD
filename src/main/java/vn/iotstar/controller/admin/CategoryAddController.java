package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Category;
import vn.iotstar.model.User;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Controller thêm mới danh mục
 * URL: /admin/category/add
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/admin/category/add" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 25    // 25MB
)
public class CategoryAddController extends HttpServlet {

    private final CategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Kiểm tra quyền Admin/Manager
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

        req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String catename = req.getParameter("catename");

        // Validate
        if (catename == null || catename.trim().isEmpty()) {
            req.setAttribute("alert", "Tên danh mục không được để trống!");
            req.setAttribute("alertType", "danger");
            req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
            return;
        }

        Category category = new Category();
        category.setName(catename.trim());

        // Thư mục upload
        String uploadPath = Constant.UPLOAD_DIR + File.separator + "category";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fname = "default.png";
        try {
            Part part = req.getPart("icon");
            if (part != null && part.getSize() > 0) {
                String submittedFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                int index = submittedFileName.lastIndexOf(".");
                String ext = (index > 0) ? submittedFileName.substring(index) : ".png";
                fname = "cate_" + System.currentTimeMillis() + ext;
                part.write(uploadPath + File.separator + fname);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fname = "default.png";
        }

        category.setIcon(fname);
        cateService.insert(category);

        String msg = URLEncoder.encode("Thêm danh mục thành công!", StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/admin/category/list?msg=" + msg);
    }
}
