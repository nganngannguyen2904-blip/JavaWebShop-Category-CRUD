package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Category;
import vn.iotstar.model.Product;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.util.List;

/**
 * Controller xem chi tiết sản phẩm và các sản phẩm liên quan
 * URL: /product/detail?id=123
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/product/detail")
public class ProductDetailController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            Product product = productService.findById(productId);

            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }

            // Lấy thông tin danh mục tương ứng
            Category category = categoryService.findById(product.getCategoryId());
            if (category == null) {
                category = new Category();
                category.setId(product.getCategoryId());
                category.setName(product.getCategoryName() != null ? product.getCategoryName() : "Sản phẩm");
            }

            // Lấy 4 sản phẩm liên quan cùng danh mục
            List<Product> relatedProducts = productService.findRelated(product.getCategoryId(), product.getId(), 4);

            req.setAttribute("product", product);
            req.setAttribute("category", category);
            req.setAttribute("relatedProducts", relatedProducts);

            req.getRequestDispatcher("/views/web/product-detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }
}
