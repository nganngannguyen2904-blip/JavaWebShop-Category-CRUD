package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.Cart;
import vn.iotstar.model.User;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller xử lý quy trình Đặt hàng & Thanh toán (Checkout)
 * URLs: /checkout, /checkout/success
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/checkout", "/checkout/success"})
public class CheckoutController extends HttpServlet {

    private final ProductService productService = new vn.iotstar.service.impl.ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        // Tự động điền thông tin nếu người dùng đã đăng nhập
        User currentUser = (User) session.getAttribute("account");
        if (currentUser != null) {
            req.setAttribute("defaultName", currentUser.getFullName());
            req.setAttribute("defaultEmail", currentUser.getEmail());
            req.setAttribute("defaultPhone", currentUser.getPhone());
        }

        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/views/web/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        // 1. Lấy thông tin người nhận hàng từ form
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String paymentMethod = req.getParameter("paymentMethod");
        String note = req.getParameter("note");

        // 2. Sinh mã đơn hàng ngẫu nhiên chuyên nghiệp
        String orderId = "DH" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String orderDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String totalFormatted = cart.getFormattedTotalAmount();
        int totalItems = cart.getTotalQuantity();

        // 3. Giảm trừ số lượng sản phẩm trong kho (Database) theo số lượng mua
        for (vn.iotstar.model.CartItem item : cart.getItems()) {
            if (item.getProduct() != null) {
                productService.deductQuantity(item.getProduct().getId(), item.getQuantity());
            }
        }

        // 4. Làm sạch giỏ hàng sau khi đặt hàng thành công
        cart.clear();

        // 4. Truyền thông tin sang trang thông báo thành công
        req.setAttribute("orderId", orderId);
        req.setAttribute("orderDate", orderDate);
        req.setAttribute("fullName", fullName);
        req.setAttribute("phone", phone);
        req.setAttribute("email", email);
        req.setAttribute("address", address);
        req.setAttribute("paymentMethod", paymentMethod != null && paymentMethod.equals("BANK") ? "Chuyển khoản Ngân hàng (QR Code)" : "Thanh toán khi nhận hàng (COD)");
        req.setAttribute("note", note);
        req.setAttribute("totalAmount", totalFormatted);
        req.setAttribute("totalItems", totalItems);

        req.getRequestDispatcher("/views/web/order-success.jsp").forward(req, resp);
    }
}
