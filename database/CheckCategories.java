package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckCategories {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        try (Connection c = DriverManager.getConnection(url); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT cate_id, cate_name, icons FROM Category");
            System.out.println("=== CATEGORIES ===");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
            }
            rs = s.executeQuery("SELECT p.product_id, p.product_name, p.image, p.cate_id, c.cate_name, p.status FROM Product p JOIN Category c ON p.cate_id = c.cate_id WHERE c.cate_name LIKE N'%nam%' OR c.cate_name LIKE N'%nữ%'");
            System.out.println("\n=== PRODUCTS IN NAM / NU ===");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " | " + rs.getString(2) + " | img: [" + rs.getString(3) + "] | cate: " + rs.getInt(4) + " - " + rs.getString(5) + " | status: " + rs.getInt(6));
            }
        }
    }
}
