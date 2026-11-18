package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import vn.iotstar.model.Product;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.ProductServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

/**
 * Controller hiển thị danh sách Video thời trang thương mại điện tử
 * URL: /video
 * Hỗ trợ tìm kiếm theo tiêu đề, lọc theo sản phẩm, sắp xếp theo ngày đăng/lượt xem và phân trang 6 video/trang
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video")
public class VideoListController extends HttpServlet {

    private final VideoService videoService = new VideoServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private static final int PAGE_SIZE = 6; // Phân trang cố định 6 Video/trang

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        // 1. Đọc số trang hiện tại từ query param (?page=1)
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

        // 2. Đọc các tham số tìm kiếm và bộ lọc
        String keyword = req.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }

        String sort = req.getParameter("sort");
        if (sort == null || sort.trim().isEmpty()) {
            sort = "newest";
        } else {
            sort = sort.trim().toLowerCase();
        }

        Integer productId = null;
        String productIdParam = req.getParameter("productId");
        if (productIdParam != null && !productIdParam.trim().isEmpty()) {
            try {
                productId = Integer.parseInt(productIdParam.trim());
                if (productId <= 0) productId = null;
            } catch (NumberFormatException ignored) {
            }
        }

        // 3. Tùy chọn phạm vi xem: Mặc định xem video của chính User. Admin có thể xem toàn bộ hệ thống bằng ?scope=all
        String scope = req.getParameter("scope");
        boolean viewAll = "all".equalsIgnoreCase(scope) && currentUser.canAccessAdmin();

        int totalVideos = videoService.countSearchAndFilter(currentUser.getId(), viewAll, keyword, productId);
        int totalPages = (int) Math.ceil((double) totalVideos / PAGE_SIZE);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<Video> videoList = videoService.searchAndFilter(
                currentUser.getId(), viewAll, keyword, sort, productId, page, PAGE_SIZE
        );

        // 4. Lấy danh sách sản phẩm thời trang để phục vụ bộ lọc
        List<Product> productList = productService.findAll();

        // 5. Bắt các thông điệp thông báo thao tác
        String alert = req.getParameter("alert");
        if ("add_success".equals(alert)) {
            req.setAttribute("msgSuccess", "Đăng tải video thời trang mới thành công!");
        } else if ("update_success".equals(alert)) {
            req.setAttribute("msgSuccess", "Cập nhật video thời trang thành công!");
        } else if ("delete_success".equals(alert)) {
            req.setAttribute("msgSuccess", "Đã xóa video và dọn dẹp tập tin thành công!");
        } else if ("unauthorized".equals(alert)) {
            req.setAttribute("msgError", "Bạn không có quyền thực hiện thao tác trên video này!");
        } else if ("not_found".equals(alert)) {
            req.setAttribute("msgError", "Không tìm thấy video yêu cầu trong hệ thống!");
        }

        req.setAttribute("videoList", videoList);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalVideos", totalVideos);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.setAttribute("keyword", keyword);
        req.setAttribute("sort", sort);
        req.setAttribute("productId", productId);
        req.setAttribute("scope", viewAll ? "all" : "user");
        req.setAttribute("productList", productList);

        req.getRequestDispatcher("/views/web/video-list.jsp").forward(req, resp);
    }
}
