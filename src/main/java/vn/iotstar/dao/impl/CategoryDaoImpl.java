package vn.iotstar.dao.impl;

import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.CategoryDao;
import vn.iotstar.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai CategoryDao
 * Thực hiện các thao tác CRUD với bảng Category
 */
public class CategoryDaoImpl extends DBConnection implements CategoryDao {

    /**
     * Thêm danh mục mới
     */
    @Override
    public void insert(Category category) {
        String sql = "INSERT INTO Category(cate_name, icons) VALUES (?, ?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getIcon());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật danh mục
     */
    @Override
    public void edit(Category category) {
        String sql = "UPDATE Category SET cate_name = ?, icons = ? WHERE cate_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getIcon());
            ps.setInt(3, category.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa danh mục theo ID
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Category WHERE cate_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh mục theo ID
     */
    @Override
    public Category get(int id) {
        String sql = "SELECT * FROM Category WHERE cate_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("cate_id"));
                    category.setName(rs.getString("cate_name"));
                    category.setIcon(rs.getString("icons"));
                    return category;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy danh mục theo tên
     */
    @Override
    public Category get(String name) {
        String sql = "SELECT * FROM Category WHERE cate_name = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("cate_id"));
                    category.setName(rs.getString("cate_name"));
                    category.setIcon(rs.getString("icons"));
                    return category;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả danh mục
     */
    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category ORDER BY cate_id";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("cate_id"));
                category.setName(rs.getString("cate_name"));
                category.setIcon(rs.getString("icons"));
                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Tìm kiếm danh mục theo từ khóa
     */
    @Override
    public List<Category> search(String keyword) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category WHERE cate_name LIKE ? ORDER BY cate_id";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("cate_id"));
                    category.setName(rs.getString("cate_name"));
                    category.setIcon(rs.getString("icons"));
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}
