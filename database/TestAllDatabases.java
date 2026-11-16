import java.sql.*;

public class TestAllDatabases {
    public static void main(String[] args) throws Exception {
        String base = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        System.out.println("--- CHECKING DATABASES ---");
        try (Connection conn = DriverManager.getConnection(base + "databaseName=master;");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sys.databases WHERE name IN ('BanHang', 'webst2')")) {
            while (rs.next()) {
                System.out.println("Database exists: " + rs.getString("name"));
            }
        }
        
        for (String db : new String[]{"BanHang", "webst2"}) {
            System.out.println("\n--- TABLES IN " + db + " ---");
            try (Connection conn = DriverManager.getConnection(base + "databaseName=" + db + ";")) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tbl = rs.getString("TABLE_NAME");
                        if (!tbl.startsWith("trace_xe_")) {
                            System.out.println("  Table: " + tbl);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error connecting to " + db + ": " + e.getMessage());
            }
        }
    }
}
