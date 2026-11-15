package database;

import vn.iotstar.dao.impl.ProductDaoImpl;
import vn.iotstar.model.Product;

import java.util.List;

public class TestFindProduct {
    public static void main(String[] args) {
        ProductDaoImpl dao = new ProductDaoImpl();
        System.out.println("=== PRODUCTS FOR CATE 1 (Nam) ===");
        List<Product> list1 = dao.findByCategoryId(1);
        System.out.println("Count: " + list1.size());
        for (Product p : list1) {
            System.out.println(p.getId() + " | " + p.getName() + " | img: [" + p.getImage() + "] | price: " + p.getPrice());
        }

        System.out.println("\n=== PRODUCTS FOR CATE 2 (Nữ) ===");
        List<Product> list2 = dao.findByCategoryId(2);
        System.out.println("Count: " + list2.size());
        for (Product p : list2) {
            System.out.println(p.getId() + " | " + p.getName() + " | img: [" + p.getImage() + "] | price: " + p.getPrice());
        }
    }
}
