package vn.iotstar.dao.impl;

import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.ProductDao;
import vn.iotstar.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai ProductDao truy vấn SQL Server
 */
public class ProductDaoImpl extends DBConnection implements ProductDao {

    private Product mapResultSetToProduct(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("product_id"));
        p.setName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setImage(rs.getString("image"));
        p.setCategoryId(rs.getInt("cate_id"));
        try {
            p.setCategoryName(rs.getString("cate_name"));
        } catch (Exception ignored) {
        }
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getInt("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.status = 1 ORDER BY p.product_id DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByCategoryId(int cateId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.cate_id = ? AND p.status = 1 ORDER BY p.product_id DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.product_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE (p.product_name LIKE ? OR p.description LIKE ?) AND p.status = 1 "
                + "ORDER BY p.product_id DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> searchByCateAndKeyword(int cateId, String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.cate_id = ? AND (p.product_name LIKE ? OR p.description LIKE ?) AND p.status = 1 "
                + "ORDER BY p.product_id DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setInt(1, cateId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findFeatured(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.status = 1 ORDER BY p.product_id ASC";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findRelated(int cateId, int excludeId, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.*, c.cate_name FROM Product p "
                + "JOIN Category c ON p.cate_id = c.cate_id "
                + "WHERE p.cate_id = ? AND p.product_id <> ? AND p.status = 1 "
                + "ORDER BY NEWID()";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, cateId);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insert(Product product) {
        String sql = "INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getImage());
            ps.setInt(5, product.getCategoryId());
            ps.setInt(6, product.getQuantity());
            ps.setInt(7, product.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void edit(Product product) {
        String sql = "UPDATE Product SET product_name = ?, description = ?, price = ?, "
                + "image = ?, cate_id = ?, quantity = ?, status = ? WHERE product_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getImage());
            ps.setInt(5, product.getCategoryId());
            ps.setInt(6, product.getQuantity());
            ps.setInt(7, product.getStatus());
            ps.setInt(8, product.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Product WHERE product_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Product WHERE status = 1";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countByCategoryId(int cateId) {
        String sql = "SELECT COUNT(*) FROM Product WHERE cate_id = ? AND status = 1";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateQuantity(int productId, int quantity) {
        String sql = "UPDATE Product SET quantity = ? WHERE product_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(0, quantity));
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deductQuantity(int productId, int quantitySold) {
        String sql = "UPDATE Product SET quantity = CASE WHEN quantity >= ? THEN quantity - ? ELSE 0 END WHERE product_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantitySold);
            ps.setInt(2, quantitySold);
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countOutOfStock() {
        String sql = "SELECT COUNT(*) FROM Product WHERE quantity <= 0";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
