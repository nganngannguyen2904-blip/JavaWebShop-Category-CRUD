package vn.iotstar.service.impl;

import vn.iotstar.dao.ProductDao;
import vn.iotstar.dao.impl.ProductDaoImpl;
import vn.iotstar.model.Product;
import vn.iotstar.service.ProductService;

import java.util.List;

/**
 * Triển khai ProductService gọi ProductDao
 */
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao = new ProductDaoImpl();

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public List<Product> findByCategoryId(int cateId) {
        return productDao.findByCategoryId(cateId);
    }

    @Override
    public Product findById(int id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> searchByName(String keyword) {
        return productDao.searchByName(keyword);
    }

    @Override
    public List<Product> searchByCateAndKeyword(int cateId, String keyword) {
        return productDao.searchByCateAndKeyword(cateId, keyword);
    }

    @Override
    public List<Product> findFeatured(int limit) {
        return productDao.findFeatured(limit);
    }

    @Override
    public List<Product> findRelated(int cateId, int excludeId, int limit) {
        return productDao.findRelated(cateId, excludeId, limit);
    }

    @Override
    public void insert(Product product) {
        productDao.insert(product);
    }

    @Override
    public void edit(Product product) {
        productDao.edit(product);
    }

    @Override
    public void delete(int id) {
        productDao.delete(id);
    }

    @Override
    public int countAll() {
        return productDao.countAll();
    }

    @Override
    public int countByCategoryId(int cateId) {
        return productDao.countByCategoryId(cateId);
    }

    @Override
    public boolean updateQuantity(int productId, int quantity) {
        return productDao.updateQuantity(productId, quantity);
    }

    @Override
    public boolean deductQuantity(int productId, int quantitySold) {
        return productDao.deductQuantity(productId, quantitySold);
    }

    @Override
    public int countOutOfStock() {
        return productDao.countOutOfStock();
    }
}
