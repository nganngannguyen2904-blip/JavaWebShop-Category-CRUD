package vn.iotstar.service.impl;

import vn.iotstar.dao.CategoryDao;
import vn.iotstar.dao.impl.CategoryDaoImpl;
import vn.iotstar.model.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.util.Constant;

import java.io.File;
import java.util.List;

/**
 * Lớp triển khai CategoryService
 * Thực hiện logic nghiệp vụ liên quan đến Category
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao = new CategoryDaoImpl();

    /**
     * Thêm danh mục mới
     */
    @Override
    public void insert(Category category) {
        categoryDao.insert(category);
    }

    /**
     * Cập nhật danh mục
     * Nếu có ảnh mới thì xóa ảnh cũ
     */
    @Override
    public void edit(Category newCategory) {
        Category oldCategory = categoryDao.get(newCategory.getId());
        if (oldCategory == null) return;

        oldCategory.setName(newCategory.getName());

        // Nếu có ảnh mới (không phải null và không rỗng) thì xóa ảnh cũ
        if (newCategory.getIcon() != null && !newCategory.getIcon().isEmpty()) {
            // Xóa ảnh cũ nếu không phải ảnh mặc định
            String oldIcon = oldCategory.getIcon();
            if (oldIcon != null && !oldIcon.isEmpty() && !oldIcon.equals("default.png")) {
                File oldFile = new File(Constant.UPLOAD_DIR + "/category/" + oldIcon);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            oldCategory.setIcon(newCategory.getIcon());
        }

        categoryDao.edit(oldCategory);
    }

    /**
     * Xóa danh mục theo ID
     * Đồng thời xóa file ảnh nếu có
     */
    @Override
    public void delete(int id) {
        // Lấy thông tin category trước khi xóa để xóa file ảnh
        Category category = categoryDao.get(id);
        if (category != null && category.getIcon() != null
                && !category.getIcon().isEmpty()
                && !category.getIcon().equals("default.png")) {
            File file = new File(Constant.UPLOAD_DIR + "/category/" + category.getIcon());
            if (file.exists()) {
                file.delete();
            }
        }
        categoryDao.delete(id);
    }

    /**
     * Lấy danh mục theo ID
     */
    @Override
    public Category get(int id) {
        return categoryDao.get(id);
    }

    /**
     * Lấy danh mục theo tên
     */
    @Override
    public Category get(String name) {
        return categoryDao.get(name);
    }

    /**
     * Lấy tất cả danh mục
     */
    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    /**
     * Tìm kiếm danh mục theo từ khóa
     */
    @Override
    public List<Category> search(String keyword) {
        return categoryDao.search(keyword);
    }

    @Override
    public boolean checkExistName(String name) {
        return categoryDao.checkExistName(name);
    }

    @Override
    public boolean checkExistNameExcept(String name, int id) {
        return categoryDao.checkExistNameExcept(name, id);
    }
}
