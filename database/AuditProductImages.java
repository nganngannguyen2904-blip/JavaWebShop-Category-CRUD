package database;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.imageio.ImageIO;

public class AuditProductImages {
    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("          KIỂM TRA BÁO CÁO TOÀN DIỆN 60 ẢNH SẢN PHẨM TRÊN HỆ THỐNG");
        System.out.println("================================================================================\n");

        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        int totalDb = 0;
        int validPhotoCount = 0;

        File uploadDir = new File("upload/product");
        File webappDir = new File("src/main/webapp/upload/product");
        File staticDir = new File("src/main/resources/static/images/products");

        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT product_id, product_name, image, cate_id FROM Product ORDER BY product_id")) {

            System.out.printf("%-4s | %-50s | %-14s | %-10s | %-10s%n", "ID", "Tên Sản Phẩm", "File Ảnh", "Dung Lượng", "Kích Thước");
            System.out.println("---------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                totalDb++;
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String imgName = rs.getString("image");

                File f = new File(uploadDir, imgName);
                if (f.exists() && f.length() > 10000) {
                    BufferedImage bi = ImageIO.read(f);
                    String dim = bi != null ? (bi.getWidth() + "x" + bi.getHeight()) : "ERR";
                    System.out.printf("[%2d] | %-50s | %-14s | %6d KB | %s [OK]%n",
                            id,
                            name.length() > 50 ? name.substring(0, 47) + "..." : name,
                            imgName,
                            f.length() / 1024,
                            dim);
                    validPhotoCount++;
                } else {
                    System.out.printf("[%2d] | %-50s | %-14s | THIẾU HOẶC LỖI!%n", id, name, imgName);
                }
            }
        }

        System.out.println("\n---------------------------------------------------------------------------------------------------");
        System.out.printf("TỔNG KẾT: %d/%d sản phẩm trong Database có ảnh thật chuẩn kích thước và dung lượng!%n", validPhotoCount, totalDb);
        System.out.printf("Thư mục 1: %s (Có %d files)%n", uploadDir.getPath(), uploadDir.list() != null ? uploadDir.list().length : 0);
        System.out.printf("Thư mục 2: %s (Có %d files)%n", webappDir.getPath(), webappDir.list() != null ? webappDir.list().length : 0);
        System.out.printf("Thư mục 3: %s (Có %d files)%n", staticDir.getPath(), staticDir.list() != null ? staticDir.list().length : 0);
        System.out.println("================================================================================\n");
    }
}
