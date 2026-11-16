import java.sql.*;

public class CheckVideosSchema {
    public static void main(String[] args) throws Exception {
        String base = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        try (Connection conn = DriverManager.getConnection(base + "databaseName=webst2;")) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("--- COLUMNS IN webst2.Videos ---");
            try (ResultSet rs = meta.getColumns(null, null, "Videos", "%")) {
                while (rs.next()) {
                    System.out.println("  " + rs.getString("COLUMN_NAME") + " (" + rs.getString("TYPE_NAME") + " " + rs.getInt("COLUMN_SIZE") + ")");
                }
            }
            System.out.println("--- FOREIGN KEYS IN webst2.Videos ---");
            try (ResultSet rs = meta.getImportedKeys(null, null, "Videos")) {
                while (rs.next()) {
                    System.out.println("  FK: " + rs.getString("FKCOLUMN_NAME") + " -> " + rs.getString("PKTABLE_NAME") + "(" + rs.getString("PKCOLUMN_NAME") + ")");
                }
            }
            System.out.println("--- DATA IN webst2.Videos ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Videos")) {
                while (rs.next()) {
                    System.out.println("  Video: id=" + rs.getString("VideoId") + ", title=" + rs.getString("Title"));
                }
            }
        }
    }
}
