package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

/**
 * Controller hiển thị danh sách Video và phân trang 6 Video/trang theo yêu cầu
 * URL: /video
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video")
public class VideoListController extends HttpServlet {

    private final VideoService videoService = new VideoServiceImpl();
    private static final int PAGE_SIZE = 6; // Phân trang cố định 6 Video/trang

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        // Đọc số trang hiện tại từ query param (?page=1)
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam.trim());
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Tùy chọn xem: Mặc định xem video của chính User. Admin có thể xem toàn bộ hệ thống bằng ?scope=all
        String scope = req.getParameter("scope");
        boolean viewAll = "all".equalsIgnoreCase(scope) && currentUser.canAccessAdmin();

        int totalVideos;
        List<Video> videoList;

        if (viewAll) {
            totalVideos = videoService.countAll();
            int totalPages = (int) Math.ceil((double) totalVideos / PAGE_SIZE);
            if (page > totalPages && totalPages > 0) page = totalPages;
            videoList = videoService.getAllPaged(page, PAGE_SIZE);
            req.setAttribute("scope", "all");
            req.setAttribute("totalPages", totalPages);
        } else {
            totalVideos = videoService.countByUserId(currentUser.getId());
            int totalPages = (int) Math.ceil((double) totalVideos / PAGE_SIZE);
            if (page > totalPages && totalPages > 0) page = totalPages;
            videoList = videoService.getByUserId(currentUser.getId(), page, PAGE_SIZE);
            req.setAttribute("scope", "user");
            req.setAttribute("totalPages", totalPages);
        }

        req.setAttribute("videoList", videoList);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalVideos", totalVideos);
        req.setAttribute("pageSize", PAGE_SIZE);

        req.getRequestDispatcher("/views/web/video-list.jsp").forward(req, resp);
    }
}
