package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

/**
 * Controller xem chi tiết thông tin và phát Video trực tiếp
 * URL: /video/detail?id=VID001
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video/detail")
public class VideoDetailController extends HttpServlet {

    private final VideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String videoId = req.getParameter("id");
        if (videoId == null || videoId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Video video = videoService.findById(videoId.trim());
        if (video == null) {
            req.setAttribute("error", "Video không tồn tại hoặc đã bị gỡ bỏ!");
            req.getRequestDispatcher("/views/web/video-detail.jsp").forward(req, resp);
            return;
        }

        // Tự động tăng số lượt xem
        videoService.increaseViews(videoId.trim());
        video.setViews(video.getViews() + 1);

        // Lấy danh sách 5 video gợi ý mới nhất
        List<Video> relatedVideos = videoService.getTop10Latest();

        req.setAttribute("video", video);
        req.setAttribute("relatedVideos", relatedVideos);
        req.getRequestDispatcher("/views/web/video-detail.jsp").forward(req, resp);
    }
}
