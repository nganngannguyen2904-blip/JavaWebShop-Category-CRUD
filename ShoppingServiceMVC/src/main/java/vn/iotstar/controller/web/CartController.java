package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Cart;
import vn.iotstar.model.Product;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

import java.io.IOException;

/**
 * Controller quản lý giỏ hàng: xem, thêm, sửa số lượng, xóa mục hàng
 * URLs: /cart, /cart/add, /cart/update, /cart/remove, /cart/clear
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class CartController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    private Cart getCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        HttpSession session = req.getSession();
        Cart cart = getCartFromSession(session);

        if (uri.endsWith("/cart/add")) {
            handleAdd(req, resp, cart);
        } else if (uri.endsWith("/cart/update")) {
            handleUpdate(req, resp, cart);
        } else if (uri.endsWith("/cart/remove")) {
            handleRemove(req, resp, cart);
        } else if (uri.endsWith("/cart/clear")) {
            cart.clear();
            resp.sendRedirect(req.getContextPath() + "/cart");
        } else {
            // Hiển thị giao diện giỏ hàng
            req.setAttribute("cart", cart);
            req.getRequestDispatcher("/views/web/cart.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, Cart cart)
            throws IOException {
        String idStr = req.getParameter("id");
        String qtyStr = req.getParameter("quantity");
        String action = req.getParameter("action"); // "buynow" or null

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(idStr);
                int quantity = 1;
                if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                    quantity = Math.max(1, Integer.parseInt(qtyStr));
                }

                Product product = productService.findById(productId);
                if (product != null) {
                    cart.add(product, quantity);
                }

                // Nếu là bấm "Mua ngay" -> Nhảy thẳng sang bước Đặt hàng & Thanh toán (/checkout)
                if ("buynow".equalsIgnoreCase(action)) {
                    resp.sendRedirect(req.getContextPath() + "/checkout");
                    return;
                }

            } catch (NumberFormatException ignored) {
            }
        }

        // Chuyển hướng sang trang giỏ hàng
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, Cart cart)
            throws IOException {
        String idStr = req.getParameter("id");
        String qtyStr = req.getParameter("quantity");
        String mode = req.getParameter("mode"); // "inc", "dec"

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(idStr);
                if ("inc".equalsIgnoreCase(mode)) {
                    cart.add(productService.findById(productId), 1);
                } else if ("dec".equalsIgnoreCase(mode)) {
                    // Giảm 1 số lượng
                    for (var item : cart.getItems()) {
                        if (item.getProduct().getId() == productId) {
                            if (item.getQuantity() > 1) {
                                cart.update(productId, item.getQuantity() - 1);
                            } else {
                                cart.remove(productId);
                            }
                            break;
                        }
                    }
                } else if (qtyStr != null) {
                    int quantity = Integer.parseInt(qtyStr);
                    cart.update(productId, quantity);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, Cart cart)
            throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(idStr);
                cart.remove(productId);
            } catch (NumberFormatException ignored) {
            }
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
