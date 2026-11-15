package vn.iotstar.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model ánh xạ bảng Product
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private int categoryId;
    private String categoryName;
    private int quantity;
    private int status;
    private Timestamp createdAt;

    public Product() {
    }

    public Product(int id, String name, String description, BigDecimal price, String image, int categoryId, int quantity, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    /**
     * Định dạng giá VND đẹp mắt
     */
    public String getFormattedPrice() {
        if (price == null) return "0 đ";
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
        return df.format(price) + " đ";
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", quantity=" + quantity +
                '}';
    }
}
