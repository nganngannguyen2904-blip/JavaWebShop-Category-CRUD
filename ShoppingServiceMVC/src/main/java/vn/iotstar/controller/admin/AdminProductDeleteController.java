package vn.iotstar.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

import java.io.IOException;

/**
 * Controller xóa sản phẩm trong hệ thống
 * URL: /admin/product/delete?id=X
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/admin/product/delete")
public class AdminProductDeleteController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        vn.iotstar.model.User user = (vn.iotstar.model.User) session.getAttribute("account");
        if (user == null || (user.getRoleid() != 1 && user.getRoleid() != 2)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                productService.delete(id);
            } catch (NumberFormatException ignored) {
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/product/list?message=deleted");
    }
}
