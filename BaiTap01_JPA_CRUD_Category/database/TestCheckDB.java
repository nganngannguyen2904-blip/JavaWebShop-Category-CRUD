import java.sql.*;

public class TestCheckDB {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connected to BanHang successfully!");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                System.out.println("Table: " + rs.getString("TABLE_NAME"));
            }
        }
    }
}
