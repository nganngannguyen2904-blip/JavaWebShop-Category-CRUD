package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;

/**
 * Lớp triển khai ICategoryService xử lý logic nghiệp vụ Category
 */
public class CategoryServiceImpl implements ICategoryService {

    public ICategoryDao cateDao = new CategoryDao();

    @Override
    public List<Category> findAll() {
        return cateDao.findAll();
    }

    @Override
    public Category findById(int id) {
        return cateDao.findById(id);
    }

    @Override
    public List<Category> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return cateDao.findAll();
        }
        return cateDao.searchByName(keyword.trim());
    }

    @Override
    public void insert(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Dữ liệu Category không được để null!");
        }
        if (category.getCategoryname() == null || category.getCategoryname().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên Category không được phép rỗng!");
        }

        Category existing = this.findByCategoryname(category.getCategoryname().trim());
        if (existing == null) {
            cateDao.insert(category);
        } else {
            throw new IllegalArgumentException("Category Name '" + category.getCategoryname() + "' đã tồn tại trong hệ thống!");
        }
    }

    @Override
    public void update(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Dữ liệu Category không được để null!");
        }
        if (category.getCategoryname() == null || category.getCategoryname().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên Category không được phép rỗng!");
        }

        Category existing = this.findById(category.getCategoryid());
        if (existing != null) {
            cateDao.update(category);
        } else {
            throw new IllegalArgumentException("Không tìm thấy Category với mã ID = " + category.getCategoryid() + " để cập nhật!");
        }
    }

    @Override
    public void delete(int id) throws Exception {
        Category existing = this.findById(id);
        if (existing != null) {
            cateDao.delete(id);
        } else {
            throw new Exception("Không tìm thấy Category với mã ID = " + id + " để xóa!");
        }
    }

    @Override
    public int count() {
        return cateDao.count();
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        if (page < 0) page = 0;
        if (pagesize <= 0) pagesize = 5;
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public Category findByCategoryname(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            return cateDao.findByCategoryname(name.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
