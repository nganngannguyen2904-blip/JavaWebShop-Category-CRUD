package vn.iotstar.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Category;
import vn.iotstar.model.Product;
import vn.iotstar.model.Video;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.ProductServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;

import java.io.IOException;
import java.util.List;

/**
 * Controller trang chủ cho người dùng
 * URL: /home
 * Hiển thị danh mục sản phẩm, sản phẩm nổi bật và 10 Video mới nhất từ CSDL
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/home")
public class HomeController extends HttpServlet {

    private final CategoryService categoryService = new CategoryServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private final VideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 1. Lấy danh sách danh mục để hiển thị lên trang chủ
            List<Category> cateList = categoryService.getAll();
            req.setAttribute("cateList", cateList);

            // 2. Lấy danh sách 8 sản phẩm nổi bật
            List<Product> featuredProducts = productService.findFeatured(8);
            req.setAttribute("featuredProducts", featuredProducts);

            // 3. Lấy 10 Video mới nhất từ database (sắp xếp theo ngày đăng tải giảm dần)
            List<Video> latestVideos = videoService.getTop10Latest();
            req.setAttribute("latestVideos", latestVideos);

            java.nio.file.Files.writeString(java.nio.file.Path.of("D:/LT_Web/BTVN_9-11/home-debug.txt"), 
                "cates=" + (cateList != null ? cateList.size() : "null") + 
                ", prods=" + (featuredProducts != null ? featuredProducts.size() : "null") + 
                ", vids=" + (latestVideos != null ? latestVideos.size() : "null"));

            req.getRequestDispatcher("/views/common/home.jsp").forward(req, resp);
        } catch (Throwable t) {
            java.io.StringWriter sw = new java.io.StringWriter();
            t.printStackTrace(new java.io.PrintWriter(sw));
            java.nio.file.Files.writeString(java.nio.file.Path.of("D:/LT_Web/BTVN_9-11/home-debug.txt"), "ERROR: " + sw.toString());
            throw new ServletException(t);
        }
    }
}

