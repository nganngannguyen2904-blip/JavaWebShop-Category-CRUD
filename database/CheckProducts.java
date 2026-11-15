import java.sql.*;

public class CheckProducts {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT TOP 5 product_id, product_name, image, price FROM Product")) {
            while (rs.next()) {
                System.out.println("ID=" + rs.getInt("product_id") + " | Name=" + rs.getString("product_name") + " | Image=" + rs.getString("image") + " | Price=" + rs.getBigDecimal("price"));
            }
        }
    }
}
