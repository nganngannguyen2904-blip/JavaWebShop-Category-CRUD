package vn.iotstar.service;

import vn.iotstar.model.Category;
import java.util.List;

/**
 * Interface Service cho Category
 * Business Logic Layer - xử lý logic nghiệp vụ liên quan đến Category
 */
public interface CategoryService {

    /**
     * Thêm danh mục mới
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

    default List<Category> findAll() {
        return getAll();
    }

    default Category findById(int id) {
        return get(id);
    }

    /**
     * Tìm kiếm danh mục
     */
    List<Category> search(String keyword);
}
