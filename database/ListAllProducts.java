import java.sql.*;

public class ListAllProducts {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT product_id, product_name, image, cate_id FROM Product ORDER BY product_id")) {
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("[%2d] Cate=%d | %-55s | Image: %s%n",
                        rs.getInt("product_id"),
                        rs.getInt("cate_id"),
                        rs.getString("product_name"),
                        rs.getString("image"));
            }
            System.out.println("TOTAL PRODUCTS: " + count);
        }
    }
}
