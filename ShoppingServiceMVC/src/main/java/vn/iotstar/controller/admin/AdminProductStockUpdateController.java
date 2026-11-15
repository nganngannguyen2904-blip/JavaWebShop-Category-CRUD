package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

import java.io.IOException;

/**
 * Controller cập nhật số lượng tồn kho nhanh chóng 1-click cho Admin
 * URL: /admin/product/update-stock
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admin/product/update-stock")
public class AdminProductStockUpdateController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        vn.iotstar.model.User user = (vn.iotstar.model.User) session.getAttribute("account");
        if (user == null || (user.getRoleid() != 1 && user.getRoleid() != 2)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idStr = req.getParameter("id");
        String qtyStr = req.getParameter("quantity");

        if (idStr != null && qtyStr != null) {
            try {
                int productId = Integer.parseInt(idStr);
                int quantity = Math.max(0, Integer.parseInt(qtyStr));
                productService.updateQuantity(productId, quantity);
            } catch (NumberFormatException ignored) {
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/product/list?message=updated");
    }
}
