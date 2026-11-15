package vn.iotstar.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Đại diện cho 1 mục hàng trong giỏ hàng
 */
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Tính thành tiền của mục hàng
     */
    public BigDecimal getSubTotal() {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Định dạng thành tiền VND
     */
    public String getFormattedSubTotal() {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getSubTotal()) + " đ";
    }
}
