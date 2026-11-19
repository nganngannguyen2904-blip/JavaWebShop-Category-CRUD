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
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        // Validation Server-side cho Insert Danh Mục
        if (catename == null || catename.trim().isEmpty()) {
            errors.put("catename", "Tên danh mục không được để trống!");
        } else if (catename.trim().length() < 2 || catename.trim().length() > 100) {
            errors.put("catename", "Tên danh mục phải có độ dài từ 2 đến 100 ký tự!");
        } else if (cateService.checkExistName(catename.trim())) {
            errors.put("catename", "Tên danh mục '" + catename.trim() + "' đã tồn tại! Vui lòng đặt tên khác.");
        }

        // Kiểm tra tệp tin ảnh upload (nếu có chọn)
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
            req.setAttribute("oldCatename", catename != null ? catename.trim() : "");
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
            if (part == null) {
                part = req.getPart("icon");
            }
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
