package vn.iotstar.controller.admin;

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
 * Controller hiển thị danh sách sản phẩm và quản lý tồn kho cho Admin
 * URL: /admin/product/list
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admin/product/list")
public class AdminProductListController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Kiểm tra quyền Admin hoặc Manager
        HttpSession session = req.getSession();
        vn.iotstar.model.User user = (vn.iotstar.model.User) session.getAttribute("account");
        if (user == null || (user.getRoleid() != 1 && user.getRoleid() != 2)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String cateIdStr = req.getParameter("cateid");
        String keyword = req.getParameter("keyword");

        List<Product> productList;
        if (cateIdStr != null && !cateIdStr.trim().isEmpty()) {
            try {
                int cateId = Integer.parseInt(cateIdStr);
                productList = productService.findByCategoryId(cateId);
                req.setAttribute("selectedCateId", cateId);
            } catch (NumberFormatException e) {
                productList = productService.findAll();
            }
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            productList = productService.searchByName(keyword.trim());
            req.setAttribute("keyword", keyword.trim());
        } else {
            productList = productService.findAll();
        }

        List<Category> categoryList = categoryService.getAll();
        int totalProducts = productService.countAll();
        int outOfStockCount = productService.countOutOfStock();

        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", categoryList);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("outOfStockCount", outOfStockCount);

        String message = req.getParameter("message");
        if ("updated".equals(message)) {
            req.setAttribute("successMessage", "Cập nhật số lượng kho hàng thành công!");
        } else if ("deleted".equals(message)) {
            req.setAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        }

        req.getRequestDispatcher("/views/admin/product/list-product.jsp").forward(req, resp);
    }
}
