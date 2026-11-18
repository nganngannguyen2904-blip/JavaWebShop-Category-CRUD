package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

/**
 * Controller xử lý đầy đủ nghiệp vụ CRUD Video và Upload File bằng Multipart Servlet chuẩn Jakarta
 * Hỗ trợ upload đồng thời Video và Ảnh Bìa (Thumbnail), kiểm tra định dạng & dung lượng,
 * lưu trữ vật lý độc lập và cập nhật cơ sở dữ liệu.
 * URLs: /video/add, /video/edit, /video/delete
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/video/add", "/video/edit", "/video/delete"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,       // 2MB (ngưỡng lưu bộ nhớ tạm)
    maxFileSize = 1024 * 1024 * 100,          // 100MB (kích thước tối đa file video)
    maxRequestSize = 1024 * 1024 * 115        // 115MB (tổng kích thước request bao gồm cả video và ảnh)
)
public class VideoCrudController extends HttpServlet {

    private final VideoService videoService = new VideoServiceImpl();
    private final vn.iotstar.service.ProductService productService = new vn.iotstar.service.impl.ProductServiceImpl();

    // Các định dạng file cho phép theo chuẩn HTML5
    private static final List<String> ALLOWED_VIDEO_EXTS = Arrays.asList(".mp4", ".webm", ".ogg", ".mov");
    private static final List<String> ALLOWED_IMAGE_EXTS = Arrays.asList(".png", ".jpg", ".jpeg", ".webp");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        String path = req.getServletPath();

        if ("/video/add".equals(path)) {
            req.setAttribute("productList", productService.findAll());
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
        } else if ("/video/edit".equals(path)) {
            handleGetEdit(req, resp, currentUser);
        } else if ("/video/delete".equals(path)) {
            handleDelete(req, resp, currentUser);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        String path = req.getServletPath();

        if ("/video/add".equals(path)) {
            handlePostAdd(req, resp, currentUser);
        } else if ("/video/edit".equals(path)) {
            handlePostEdit(req, resp, currentUser);
        } else if ("/video/delete".equals(path)) {
            handleDelete(req, resp, currentUser);
        }
    }

    // ================= XỬ LÝ CREATE (THÊM VIDEO MỚI) =================
    private void handlePostAdd(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String productIdStr = req.getParameter("productId");
        Integer productId = null;
        if (productIdStr != null && !productIdStr.trim().isEmpty()) {
            try {
                productId = Integer.parseInt(productIdStr.trim());
                if (productId <= 0) productId = null;
            } catch (NumberFormatException ignored) {
            }
        }

        req.setAttribute("oldTitle", title);
        req.setAttribute("oldDescription", description);
        req.setAttribute("oldProductId", productId);
        req.setAttribute("productList", productService.findAll());

        // 1. Kiểm tra tiêu đề bắt buộc
        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập tiêu đề cho video!");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        // 2. Nhận file Video qua Multipart (Bắt buộc)
        Part videoPart = null;
        try {
            videoPart = req.getPart("videoFile");
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi đọc file video tải lên: " + e.getMessage());
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        if (videoPart == null || videoPart.getSize() <= 0) {
            req.setAttribute("error", "Vui lòng chọn file video cần đăng tải!");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        if (videoPart.getSize() > 100 * 1024 * 1024L) {
            req.setAttribute("error", "Dung lượng file video vượt quá giới hạn cho phép (Tối đa 100MB)!");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        String originalVideoName = getFileName(videoPart);
        String videoExt = getFileExtension(originalVideoName);
        if (!ALLOWED_VIDEO_EXTS.contains(videoExt.toLowerCase())) {
            req.setAttribute("error", "Định dạng file video không hợp lệ! Hệ thống chỉ hỗ trợ MP4, WebM, OGG, MOV.");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        // 3. Nhận file Ảnh Bìa (Poster/Thumbnail) nếu người dùng chọn
        Part posterPart = null;
        try {
            posterPart = req.getPart("posterFile");
        } catch (Exception ignored) {
        }

        String savedPosterFileName = "cat_dientu.png"; // Mặc định nếu người dùng không chọn ảnh
        if (posterPart != null && posterPart.getSize() > 0) {
            // Kiểm tra dung lượng ảnh bìa (tối đa 10MB)
            if (posterPart.getSize() > 10 * 1024 * 1024L) {
                req.setAttribute("error", "Dung lượng ảnh bìa vượt quá giới hạn cho phép (Tối đa 10MB)!");
                req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
                return;
            }

            String origPosterName = getFileName(posterPart);
            String posterExt = getFileExtension(origPosterName);

            // Kiểm tra định dạng đuôi file ảnh
            if (!ALLOWED_IMAGE_EXTS.contains(posterExt.toLowerCase())) {
                req.setAttribute("error", "Định dạng ảnh bìa không hợp lệ! Hệ thống chỉ hỗ trợ JPG, JPEG, PNG, WEBP.");
                req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
                return;
            }

            // Kiểm tra MIME type chống giả mạo file
            String mimeType = posterPart.getContentType();
            if (mimeType != null && !mimeType.toLowerCase().startsWith("image/")) {
                req.setAttribute("error", "Tập tin ảnh bìa không hợp lệ (Không phải định dạng hình ảnh)!");
                req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
                return;
            }

            // Tạo tên file duy nhất chống trùng lặp và ghi đè
            savedPosterFileName = "poster_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + posterExt.toLowerCase();
            savePartToFile(posterPart, savedPosterFileName, "video", req);
        }

        // 4. Lưu file video vật lý vào thư mục upload/video
        String savedVideoFileName = "vid_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + videoExt.toLowerCase();
        savePartToFile(videoPart, savedVideoFileName, "video", req);

        // 5. Lưu đối tượng Video vào Database
        String videoId = "VID" + System.currentTimeMillis();
        Video newVideo = new Video();
        newVideo.setVideoId(videoId);
        newVideo.setTitle(title.trim());
        newVideo.setDescription(description != null ? description.trim() : "");
        newVideo.setVideoUrl(savedVideoFileName);
        newVideo.setPoster(savedPosterFileName);
        newVideo.setViews(0);
        newVideo.setActive(1);
        newVideo.setUploadDate(new Timestamp(System.currentTimeMillis()));
        newVideo.setUserId(currentUser.getId());
        newVideo.setProductId(productId);

        videoService.insert(newVideo);

        resp.sendRedirect(req.getContextPath() + "/video?alert=add_success");
    }

    // ================= XỬ LÝ UPDATE (SỬA VIDEO) =================
    private void handleGetEdit(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String videoId = req.getParameter("id");
        if (videoId == null || videoId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        Video video = videoService.findById(videoId.trim());
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/video?alert=not_found");
            return;
        }

        // Kiểm tra quyền: Chỉ chính chủ hoặc Quản trị viên mới được sửa
        if (video.getUserId() != currentUser.getId() && !currentUser.canAccessAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/video?alert=unauthorized");
            return;
        }

        req.setAttribute("video", video);
        req.setAttribute("productList", productService.findAll());
        req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
    }

    private void handlePostEdit(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String videoId = req.getParameter("videoId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String activeStr = req.getParameter("active");
        String productIdStr = req.getParameter("productId");
        Integer productId = null;
        if (productIdStr != null && !productIdStr.trim().isEmpty()) {
            try {
                productId = Integer.parseInt(productIdStr.trim());
                if (productId <= 0) productId = null;
            } catch (NumberFormatException ignored) {
            }
        }
        req.setAttribute("productList", productService.findAll());

        if (videoId == null || videoId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        Video existingVideo = videoService.findById(videoId.trim());
        if (existingVideo == null) {
            resp.sendRedirect(req.getContextPath() + "/video?alert=not_found");
            return;
        }

        // Kiểm tra quyền chính chủ hoặc Admin
        if (existingVideo.getUserId() != currentUser.getId() && !currentUser.canAccessAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/video?alert=unauthorized");
            return;
        }

        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("error", "Tiêu đề video không được để trống!");
            req.setAttribute("video", existingVideo);
            req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
            return;
        }

        existingVideo.setTitle(title.trim());
        existingVideo.setDescription(description != null ? description.trim() : "");
        existingVideo.setActive("1".equals(activeStr) ? 1 : 0);
        existingVideo.setProductId(productId);

        // 1. Kiểm tra nếu có upload ảnh poster mới thay thế
        Part posterPart = null;
        try {
            posterPart = req.getPart("posterFile");
        } catch (Exception ignored) {
        }

        if (posterPart != null && posterPart.getSize() > 0) {
            if (posterPart.getSize() > 10 * 1024 * 1024L) {
                req.setAttribute("error", "Dung lượng ảnh bìa mới không được vượt quá 10MB!");
                req.setAttribute("video", existingVideo);
                req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
                return;
            }

            String origPoster = getFileName(posterPart);
            String posterExt = getFileExtension(origPoster);

            if (!ALLOWED_IMAGE_EXTS.contains(posterExt.toLowerCase())) {
                req.setAttribute("error", "Định dạng file ảnh bìa không hợp lệ! Hệ thống chỉ hỗ trợ JPG, JPEG, PNG, WEBP.");
                req.setAttribute("video", existingVideo);
                req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
                return;
            }

            String mimeType = posterPart.getContentType();
            if (mimeType != null && !mimeType.toLowerCase().startsWith("image/")) {
                req.setAttribute("error", "Tập tin ảnh bìa mới không hợp lệ!");
                req.setAttribute("video", existingVideo);
                req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
                return;
            }

            // Xóa file ảnh cũ nếu là ảnh do hệ thống tạo (bắt đầu bằng poster_)
            String oldPoster = existingVideo.getPoster();
            if (oldPoster != null && oldPoster.startsWith("poster_")) {
                deletePhysicalFile(oldPoster, "video");
            }

            String newPosterName = "poster_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + posterExt.toLowerCase();
            savePartToFile(posterPart, newPosterName, "video", req);
            existingVideo.setPoster(newPosterName);
        }

        // 2. Kiểm tra nếu có upload file video mới thay thế
        Part videoPart = null;
        try {
            videoPart = req.getPart("videoFile");
        } catch (Exception ignored) {
        }

        if (videoPart != null && videoPart.getSize() > 0) {
            if (videoPart.getSize() > 100 * 1024 * 1024L) {
                req.setAttribute("error", "Dung lượng file video mới vượt quá giới hạn cho phép (Tối đa 100MB)!");
                req.setAttribute("video", existingVideo);
                req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
                return;
            }

            String origName = getFileName(videoPart);
            String ext = getFileExtension(origName);
            if (!ALLOWED_VIDEO_EXTS.contains(ext.toLowerCase())) {
                req.setAttribute("error", "Định dạng file video mới không hợp lệ! Chỉ chấp nhận: " + ALLOWED_VIDEO_EXTS);
                req.setAttribute("video", existingVideo);
                req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
                return;
            }

            // Xóa file video cũ nếu do hệ thống tạo (bắt đầu bằng vid_)
            String oldVideoUrl = existingVideo.getVideoUrl();
            if (oldVideoUrl != null && oldVideoUrl.startsWith("vid_")) {
                deletePhysicalFile(oldVideoUrl, "video");
            }

            String newFileName = "vid_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + ext.toLowerCase();
            savePartToFile(videoPart, newFileName, "video", req);
            existingVideo.setVideoUrl(newFileName);
        }

        videoService.update(existingVideo);

        resp.sendRedirect(req.getContextPath() + "/video?alert=update_success");
    }

    // ================= XỬ LÝ DELETE (XÓA VIDEO) =================
    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws IOException {
        String videoId = req.getParameter("id");
        if (videoId == null || videoId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        Video video = videoService.findById(videoId.trim());
        boolean success = videoService.delete(videoId.trim(), currentUser.getId(), currentUser.canAccessAdmin());

        if (success) {
            // Dọn dẹp tài nguyên vật lý trên ổ đĩa
            if (video != null) {
                if (video.getPoster() != null && video.getPoster().startsWith("poster_")) {
                    deletePhysicalFile(video.getPoster(), "video");
                }
                if (video.getVideoUrl() != null && video.getVideoUrl().startsWith("vid_")) {
                    deletePhysicalFile(video.getVideoUrl(), "video");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/video?alert=delete_success");
        } else {
            resp.sendRedirect(req.getContextPath() + "/video?alert=unauthorized");
        }
    }

    // ================= TIỆN ÍCH FILE MULTIPART & LƯU TRỮ =================
    private String getFileName(Part part) {
        String submitted = part.getSubmittedFileName();
        if (submitted != null && !submitted.trim().isEmpty()) {
            return new File(submitted).getName();
        }
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                String fname = content.substring(content.indexOf("=") + 2, content.length() - 1);
                return new File(fname).getName();
            }
        }
        return "file_" + System.currentTimeMillis();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex != -1) ? fileName.substring(lastIndex) : "";
    }

    private void savePartToFile(Part part, String fileName, String subFolder, HttpServletRequest req) throws IOException {
        // 1. Lưu vào thư mục cấu hình Constant.UPLOAD_DIR
        String uploadDirStr = Constant.UPLOAD_DIR + File.separator + subFolder;
        File uploadDir = new File(uploadDirStr);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File targetFile = new File(uploadDir, fileName);
        part.write(targetFile.getAbsolutePath());

        // 2. Đồng thời copy vào thư mục webapp thực tế nếu tồn tại để container phục vụ ngay
        try {
            String realPath = req.getServletContext().getRealPath("/upload/" + subFolder);
            if (realPath != null) {
                File realDir = new File(realPath);
                if (!realDir.exists()) {
                    realDir.mkdirs();
                }
                File realTarget = new File(realDir, fileName);
                Files.copy(targetFile.toPath(), realTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
        }
    }

    private void deletePhysicalFile(String fileName, String subFolder) {
        try {
            File f1 = new File(Constant.UPLOAD_DIR + File.separator + subFolder + File.separator + fileName);
            if (f1.exists() && f1.isFile()) {
                f1.delete();
            }
        } catch (Exception ignored) {
        }
    }
}
