package vn.iotstar.service;

import vn.iotstar.model.Product;
import java.util.List;

/**
 * Interface Service cho Product
 */
public interface ProductService {

    List<Product> findAll();

    List<Product> findByCategoryId(int cateId);

    Product findById(int id);

    List<Product> searchByName(String keyword);

    List<Product> searchByCateAndKeyword(int cateId, String keyword);

    List<Product> findFeatured(int limit);

    List<Product> findRelated(int cateId, int excludeId, int limit);

    void insert(Product product);

    void edit(Product product);

    void delete(int id);

    int countAll();

    int countByCategoryId(int cateId);

    boolean updateQuantity(int productId, int quantity);

    boolean deductQuantity(int productId, int quantitySold);

    int countOutOfStock();
}
