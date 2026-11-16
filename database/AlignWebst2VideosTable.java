import java.sql.*;

public class AlignWebst2VideosTable {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=webst2;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement()) {

            System.out.println("[*] Đồng bộ cột bảng Videos trong database webst2...");

            // Thêm các cột nếu chưa tồn tại
            String[] alterCols = {
                "IF COL_LENGTH('dbo.Videos', 'VideoUrl') IS NULL ALTER TABLE dbo.Videos ADD VideoUrl NVARCHAR(255) NULL;",
                "IF COL_LENGTH('dbo.Videos', 'UploadDate') IS NULL ALTER TABLE dbo.Videos ADD UploadDate DATETIME NULL DEFAULT GETDATE();",
                "IF COL_LENGTH('dbo.Videos', 'UserId') IS NULL ALTER TABLE dbo.Videos ADD UserId INT NULL;"
            };

            for (String sql : alterCols) {
                st.executeUpdate(sql);
            }

            // Gán giá trị mặc định cho VideoUrl và UserId nếu NULL
            st.executeUpdate("UPDATE dbo.Videos SET VideoUrl = 'video_demo_01.mp4' WHERE VideoUrl IS NULL");
            st.executeUpdate("UPDATE dbo.Videos SET UploadDate = GETDATE() WHERE UploadDate IS NULL");
            st.executeUpdate("UPDATE dbo.Videos SET UserId = (SELECT TOP 1 id FROM dbo.[User]) WHERE UserId IS NULL");

            // Thêm Foreign Key nếu chưa có
            String fkSql = 
                "IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_Videos_User') " +
                "BEGIN " +
                "    ALTER TABLE dbo.Videos ADD CONSTRAINT FK_Videos_User FOREIGN KEY (UserId) REFERENCES dbo.[User](id); " +
                "    PRINT 'Da them FK_Videos_User trong webst2!'; " +
                "END";
            st.executeUpdate(fkSql);

            System.out.println("[+] Đồng bộ database webst2 thành công!");
        }
    }
}
