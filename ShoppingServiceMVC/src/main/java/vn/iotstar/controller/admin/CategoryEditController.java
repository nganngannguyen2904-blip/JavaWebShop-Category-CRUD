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
 * Controller chỉnh sửa danh mục
 * URL: /admin/category/edit
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/admin/category/edit" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 25    // 25MB
)
public class CategoryEditController extends HttpServlet {

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
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Category category = cateService.get(id);
            if (category == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/category/list");
                return;
            }
            req.setAttribute("category", category);
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
        }
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

        String idStr = req.getParameter("id");
        String catename = req.getParameter("catename");

        if (idStr == null || idStr.trim().isEmpty() || catename == null || catename.trim().isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập đầy đủ thông tin danh mục!");
            req.setAttribute("alertType", "danger");
            if (idStr != null) {
                Category c = cateService.get(Integer.parseInt(idStr));
                req.setAttribute("category", c);
            }
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
            return;
        }

        int cateid = Integer.parseInt(idStr);
        Category category = new Category();
        category.setId(cateid);
        category.setName(catename.trim());

        // Lấy thông tin hiện tại để giữ hình cũ nếu không upload hình mới
        Category cateOld = cateService.get(cateid);
        String fileOld = (cateOld != null && cateOld.getIcon() != null) ? cateOld.getIcon() : "default.png";

        String uploadPath = Constant.UPLOAD_DIR + File.separator + "category";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            Part part = req.getPart("icon");
            if (part != null && part.getSize() > 0) {
                String submittedFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                int index = submittedFileName.lastIndexOf(".");
                String ext = (index > 0) ? submittedFileName.substring(index) : ".png";
                String fname = "cate_" + System.currentTimeMillis() + ext;
                part.write(uploadPath + File.separator + fname);
                category.setIcon(fname);
            } else {
                category.setIcon(fileOld);
            }
        } catch (Exception e) {
            e.printStackTrace();
            category.setIcon(fileOld);
        }

        cateService.edit(category);

        String msg = URLEncoder.encode("Cập nhật danh mục thành công!", StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/admin/category/list?msg=" + msg);
    }
}
