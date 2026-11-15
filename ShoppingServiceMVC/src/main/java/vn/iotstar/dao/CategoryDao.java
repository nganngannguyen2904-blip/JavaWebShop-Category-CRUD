package vn.iotstar.dao;

import vn.iotstar.model.Category;
import java.util.List;

/**
 * Interface DAO cho Category
 * Định nghĩa các phương thức CRUD truy cập dữ liệu cho Category
 */
public interface CategoryDao {

    /**
     * Thêm một danh mục mới
     */
    void insert(Category category);

    /**
     * Cập nhật danh mục
     */
    void edit(Category category);

    /**
     * Xóa danh mục theo ID
     */
    void delete(int id);

    /**
     * Lấy danh mục theo ID
     */
    Category get(int id);

    /**
     * Lấy danh mục theo tên
     */
    Category get(String name);

    /**
     * Lấy tất cả danh mục
     */
    List<Category> getAll();

    /**
     * Tìm kiếm danh mục theo từ khóa
     */
    List<Category> search(String keyword);
}
