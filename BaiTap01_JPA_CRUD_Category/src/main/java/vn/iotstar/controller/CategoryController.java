package vn.iotstar.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

/**
 * Controller Servlet quản lý toàn bộ thao tác CRUD Category
 * URLs: /admin/categories, /admin/category/add, /admin/category/insert,
 *       /admin/category/edit, /admin/category/update, /admin/category/delete
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
@WebServlet(urlPatterns = {
    "/admin/categories",
    "/admin/category/add",
    "/admin/category/insert",
    "/admin/category/edit",
    "/admin/category/update",
    "/admin/category/delete"
})
public class CategoryController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.contains("/admin/categories")) {
            // Hỗ trợ tìm kiếm theo tên và hiển thị danh sách
            String keyword = req.getParameter("keyword");
            List<Category> list;
            if (keyword != null && !keyword.trim().isEmpty()) {
                list = cateService.searchByName(keyword.trim());
                req.setAttribute("keyword", keyword.trim());
            } else {
                list = cateService.findAll();
            }
            req.setAttribute("listcate", list);
            req.setAttribute("totalCount", cateService.count());
            req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/add")) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/edit")) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                Category category = cateService.findById(id);
                if (category != null) {
                    req.setAttribute("cate", category);
                    req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?error=notfound");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/admin/categories?error=invalid_id");
            }

        } else if (url.contains("/admin/category/delete")) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                Category cat = cateService.findById(id);
                if (cat != null && cat.getImages() != null && !cat.getImages().startsWith("http")) {
                    String uploadPath = getUploadDir(req);
                    deleteFile(uploadPath + File.separator + cat.getImages());
                }
                cateService.delete(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Chuyển hướng về trang danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.contains("/admin/category/insert")) {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");

            // Lấy dữ liệu từ form
            String categoryname = req.getParameter("categoryname");
            String statusStr = req.getParameter("status");
            int status = statusStr != null ? Integer.parseInt(statusStr) : 1;
            String images = req.getParameter("images");

            // Đưa dữ liệu vào entity
            Category category = new Category();
            category.setCategoryname(categoryname);
            category.setStatus(status);

            String uploadPath = getUploadDir(req);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                Part part = req.getPart("images1");
                if (part != null && part.getSize() > 0) {
                    String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    int index = filename.lastIndexOf(".");
                    String ext = index > 0 ? filename.substring(index + 1).toLowerCase() : "png";
                    
                    // Kiểm tra định dạng ảnh an toàn
                    if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("webp")) {
                        String fname = System.currentTimeMillis() + "." + ext;
                        part.write(uploadPath + File.separator + fname);
                        category.setImages(fname);

                        // Đồng bộ file vào thư mục webapp nếu có
                        syncToWebapp(req, fname, part);
                    } else {
                        category.setImages("avatar.png");
                    }
                } else if (images != null && !images.trim().isEmpty()) {
                    category.setImages(images.trim());
                } else {
                    category.setImages("avatar.png");
                }
            } catch (FileNotFoundException fne) {
                fne.printStackTrace();
            }

            try {
                // Đưa entity vào phương thức insert
                cateService.insert(category);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chuyển trang
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }

        if (url.contains("/admin/category/update")) {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");

            // Lấy dữ liệu từ form
            int categoryid = Integer.parseInt(req.getParameter("categoryid"));
            String categoryname = req.getParameter("categoryname");
            String statusStr = req.getParameter("status");
            int status = statusStr != null ? Integer.parseInt(statusStr) : 1;
            String images = req.getParameter("images");

            // Đưa dữ liệu vào model
            Category category = cateService.findById(categoryid);
            if (category != null) {
                String fileold = category.getImages();
                category.setCategoryname(categoryname);
                category.setStatus(status);

                String uploadPath = getUploadDir(req);
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                try {
                    Part part = req.getPart("images1");
                    if (part != null && part.getSize() > 0) {
                        // Xóa file cũ trên thư mục nếu không phải link online
                        if (fileold != null && !fileold.startsWith("http") && !fileold.equals("avatar.png") && !fileold.equals("default.png")) {
                            deleteFile(uploadPath + File.separator + fileold);
                        }

                        String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                        int index = filename.lastIndexOf(".");
                        String ext = index > 0 ? filename.substring(index + 1).toLowerCase() : "png";

                        if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("webp")) {
                            String fname = System.currentTimeMillis() + "." + ext;
                            part.write(uploadPath + File.separator + fname);
                            category.setImages(fname);

                            syncToWebapp(req, fname, part);
                        }
                    } else if (images != null && !images.trim().isEmpty()) {
                        category.setImages(images.trim());
                    } else {
                        category.setImages(fileold != null ? fileold : "avatar.png");
                    }
                } catch (FileNotFoundException fne) {
                    fne.printStackTrace();
                }

                try {
                    cateService.update(category);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Chuyển trang
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    private String getUploadDir(HttpServletRequest req) {
        String dir = Constant.DIR;
        if (dir == null || dir.isEmpty()) {
            dir = req.getServletContext().getRealPath("/upload/category");
        }
        return dir;
    }

    private void syncToWebapp(HttpServletRequest req, String fname, Part part) {
        try {
            String realPath = req.getServletContext().getRealPath("/upload/category");
            if (realPath != null) {
                File dir = new File(realPath);
                if (!dir.exists()) dir.mkdirs();
                File target = new File(dir, fname);
                part.write(target.getAbsolutePath());
            }
        } catch (Exception ignored) {
        }
    }

    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
