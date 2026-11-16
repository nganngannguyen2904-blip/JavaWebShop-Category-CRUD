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
 * URLs: /video/add, /video/edit, /video/delete
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/video/add", "/video/edit", "/video/delete"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,       // 2MB (ngưỡng lưu bộ nhớ)
    maxFileSize = 1024 * 1024 * 100,          // 100MB (kích thước tối đa file video)
    maxRequestSize = 1024 * 1024 * 110        // 110MB (tổng kích thước request)
)
public class VideoCrudController extends HttpServlet {

    private final VideoService videoService = new VideoServiceImpl();

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

    // ================= XỬ LÝ CREATE (THÊM VIDEO) =================
    private void handlePostAdd(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");

        req.setAttribute("oldTitle", title);
        req.setAttribute("oldDescription", description);

        // 1. Kiểm tra tiêu đề bắt buộc
        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập tiêu đề cho video!");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        // 2. Nhận file Video qua Multipart
        Part videoPart = req.getPart("videoFile");
        if (videoPart == null || videoPart.getSize() <= 0) {
            req.setAttribute("error", "Vui lòng chọn file video cần đăng tải!");
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        String originalVideoName = getFileName(videoPart);
        String videoExt = getFileExtension(originalVideoName);
        if (!ALLOWED_VIDEO_EXTS.contains(videoExt.toLowerCase())) {
            req.setAttribute("error", "Định dạng file video không hợp lệ! Chỉ chấp nhận: " + ALLOWED_VIDEO_EXTS);
            req.getRequestDispatcher("/views/web/video-add.jsp").forward(req, resp);
            return;
        }

        // Lưu file video an toàn, chống ghi đè bằng UUID
        String savedVideoFileName = "vid_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + videoExt;
        savePartToFile(videoPart, savedVideoFileName, "video");

        // 3. Nhận file Poster (Thumbnail) nếu có
        Part posterPart = req.getPart("posterFile");
        String savedPosterFileName = "cat_dientu.png"; // Mặc định nếu không upload
        if (posterPart != null && posterPart.getSize() > 0) {
            String origPosterName = getFileName(posterPart);
            String posterExt = getFileExtension(origPosterName);
            if (ALLOWED_IMAGE_EXTS.contains(posterExt.toLowerCase())) {
                savedPosterFileName = "poster_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + posterExt;
                savePartToFile(posterPart, savedPosterFileName, "video");
            }
        }

        // 4. Lưu đối tượng Video vào Database
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
        req.getRequestDispatcher("/views/web/video-edit.jsp").forward(req, resp);
    }

    private void handlePostEdit(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String videoId = req.getParameter("videoId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String activeStr = req.getParameter("active");

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

        // Kiểm tra nếu có upload file video mới thay thế
        Part videoPart = req.getPart("videoFile");
        if (videoPart != null && videoPart.getSize() > 0) {
            String origName = getFileName(videoPart);
            String ext = getFileExtension(origName);
            if (ALLOWED_VIDEO_EXTS.contains(ext.toLowerCase())) {
                String newFileName = "vid_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + ext;
                savePartToFile(videoPart, newFileName, "video");
                existingVideo.setVideoUrl(newFileName);
            }
        }

        // Kiểm tra nếu có upload poster mới thay thế
        Part posterPart = req.getPart("posterFile");
        if (posterPart != null && posterPart.getSize() > 0) {
            String origPoster = getFileName(posterPart);
            String ext = getFileExtension(origPoster);
            if (ALLOWED_IMAGE_EXTS.contains(ext.toLowerCase())) {
                String newPosterName = "poster_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + ext;
                savePartToFile(posterPart, newPosterName, "video");
                existingVideo.setPoster(newPosterName);
            }
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

        boolean success = videoService.delete(videoId.trim(), currentUser.getId(), currentUser.canAccessAdmin());

        if (success) {
            resp.sendRedirect(req.getContextPath() + "/video?alert=delete_success");
        } else {
            resp.sendRedirect(req.getContextPath() + "/video?alert=unauthorized");
        }
    }

    // ================= TIỆN ÍCH FILE MULTIPART =================
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

    private void savePartToFile(Part part, String fileName, String subFolder) throws IOException {
        String uploadDirStr = Constant.UPLOAD_DIR + File.separator + subFolder;
        File uploadDir = new File(uploadDirStr);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File targetFile = new File(uploadDir, fileName);
        part.write(targetFile.getAbsolutePath());
    }
}
