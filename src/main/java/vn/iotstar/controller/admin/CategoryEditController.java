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
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        int cateid = 0;
        try {
            cateid = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        Category cateOld = cateService.get(cateid);
        if (cateOld == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        // Validation Server-side cho Edit Danh Mục
        if (catename == null || catename.trim().isEmpty()) {
            errors.put("catename", "Tên danh mục không được để trống!");
        } else if (catename.trim().length() < 2 || catename.trim().length() > 100) {
            errors.put("catename", "Tên danh mục phải có độ dài từ 2 đến 100 ký tự!");
        } else if (cateService.checkExistNameExcept(catename.trim(), cateid)) {
            errors.put("catename", "Tên danh mục '" + catename.trim() + "' đã được sử dụng bởi danh mục khác!");
        }

        Part part = null;
        try {
            part = req.getPart("icon");
            if (part != null && part.getSize() > 0) {
                String submittedFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString().toLowerCase();
                if (!submittedFileName.endsWith(".png") && !submittedFileName.endsWith(".jpg") 
                        && !submittedFileName.endsWith(".jpeg") && !submittedFileName.endsWith(".webp")) {
                    errors.put("icon", "Chỉ chấp nhận tệp tin hình ảnh định dạng PNG, JPG, JPEG hoặc WEBP!");
                } else if (part.getSize() > 5 * 1024 * 1024) {
                    errors.put("icon", "Dung lượng hình ảnh không được vượt quá 5MB!");
                }
            }
        } catch (Exception e) {
            errors.put("icon", "Lỗi đọc tệp tin tải lên: " + e.getMessage());
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", errors.containsKey("catename") ? errors.get("catename") : errors.get("icon"));
            req.setAttribute("alertType", "danger");
            cateOld.setName(catename != null ? catename.trim() : "");
            req.setAttribute("category", cateOld);
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
            return;
        }

        Category category = new Category();
        category.setId(cateid);
        category.setName(catename.trim());
        String fileOld = (cateOld != null && cateOld.getIcon() != null) ? cateOld.getIcon() : "default.png";

        String uploadPath = Constant.UPLOAD_DIR + File.separator + "category";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            if (part == null) {
                part = req.getPart("icon");
            }
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
