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
import java.util.Comparator;
import java.util.List;

/**
 * Controller hiển thị danh sách sản phẩm, lọc theo danh mục và tìm kiếm
 * URLs: /products, /product/list
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/products", "/product/list"})
public class ProductListController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Lấy tham số truy vấn
        String cateIdStr = req.getParameter("cateid");
        String keyword = req.getParameter("keyword");
        String sort = req.getParameter("sort"); // "asc", "desc"

        List<Product> productList;
        Integer selectedCateId = null;
        String selectedCateName = "Tất cả sản phẩm";

        // 2. Lấy danh sách danh mục phục vụ sidebar
        List<Category> categoryList = categoryService.findAll();
        req.setAttribute("categoryList", categoryList);

        // 3. Xử lý lọc theo danh mục và từ khóa
        if (cateIdStr != null && !cateIdStr.trim().isEmpty()) {
            try {
                selectedCateId = Integer.parseInt(cateIdStr);
                Category currentCate = categoryService.findById(selectedCateId);
                if (currentCate != null) {
                    selectedCateName = currentCate.getName();
                }

                if (keyword != null && !keyword.trim().isEmpty()) {
                    productList = productService.searchByCateAndKeyword(selectedCateId, keyword.trim());
                } else {
                    productList = productService.findByCategoryId(selectedCateId);
                }
            } catch (NumberFormatException e) {
                productList = productService.findAll();
            }
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            productList = productService.searchByName(keyword.trim());
            selectedCateName = "Kết quả tìm kiếm cho: \"" + keyword.trim() + "\"";
        } else {
            productList = productService.findAll();
        }

        // 4. Sắp xếp giá nếu có yêu cầu
        if (sort != null && productList != null && !productList.isEmpty()) {
            if ("asc".equalsIgnoreCase(sort)) {
                productList.sort((p1, p2) -> {
                    if (p1 == null || p1.getPrice() == null) return -1;
                    if (p2 == null || p2.getPrice() == null) return 1;
                    return p1.getPrice().compareTo(p2.getPrice());
                });
            } else if ("desc".equalsIgnoreCase(sort)) {
                productList.sort((p1, p2) -> {
                    if (p1 == null || p1.getPrice() == null) return 1;
                    if (p2 == null || p2.getPrice() == null) return -1;
                    return p2.getPrice().compareTo(p1.getPrice());
                });
            }
        }

        // 5. Thiết lập thuộc tính cho JSP
        req.setAttribute("productList", productList);
        req.setAttribute("selectedCateId", selectedCateId);
        req.setAttribute("selectedCateName", selectedCateName);
        req.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        req.setAttribute("sort", sort != null ? sort : "");
        req.setAttribute("totalCount", productList.size());

        // 6. Forward tới view danh sách sản phẩm
        req.getRequestDispatcher("/views/web/product-list.jsp").forward(req, resp);
    }
}
