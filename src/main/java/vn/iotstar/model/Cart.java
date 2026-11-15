package vn.iotstar.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Quản lý giỏ hàng của người dùng (lưu trong Session)
 */
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    public Cart() {
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void add(Product product, int quantity) {
        if (product == null || quantity <= 0) return;

        CartItem item = items.get(product.getId());
        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    public void update(int productId, int quantity) {
        if (quantity <= 0) {
            remove(productId);
        } else {
            CartItem item = items.get(productId);
            if (item != null) {
                item.setQuantity(quantity);
            }
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public void remove(int productId) {
        items.remove(productId);
    }

    /**
     * Xóa sạch giỏ hàng
     */
    public void clear() {
        items.clear();
    }

    /**
     * Lấy danh sách các mục trong giỏ
     */
    public Collection<CartItem> getItems() {
        return items.values();
    }

    /**
     * Kiểm tra giỏ hàng có trống không
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Tổng số lượng sản phẩm trong giỏ
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * Tổng số tiền thanh toán của giỏ hàng
     */
    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getSubTotal());
        }
        return total;
    }

    /**
     * Định dạng tổng tiền VND
     */
    public String getFormattedTotalAmount() {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getTotalAmount()) + " đ";
    }
}
