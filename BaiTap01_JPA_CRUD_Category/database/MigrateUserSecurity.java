import java.sql.*;
import vn.iotstar.util.BCrypt;

public class MigrateUserSecurity {

    public static void main(String[] args) {
        String baseParams = ";databaseName=BanHang;encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;sendStringParametersAsUnicode=true;";
        String winUrl = "jdbc:sqlserver://localhost:1433;integratedSecurity=true" + baseParams;
        String saUrl = "jdbc:sqlserver://localhost:1433;user=sa;password=123456" + baseParams;

        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try {
                conn = DriverManager.getConnection(winUrl);
                System.out.println("[*] Kết nối SQL Server thành công bằng Windows Authentication.");
            } catch (Exception eWin) {
                System.out.println("[!] Thử Windows Auth thất bại, chuyển sang SQL Auth sa/123456...");
                conn = DriverManager.getConnection(saUrl);
                System.out.println("[*] Kết nối SQL Server thành công bằng tài khoản sa.");
            }

            // 1. Nâng cấp cấu trúc bảng [User]
            try (Statement st = conn.createStatement()) {
                System.out.println("[*] Đang cập nhật cấu trúc bảng [User]...");

                // Mở rộng cột password lên NVARCHAR(255) cho chuỗi hash BCrypt
                try {
                    st.execute("ALTER TABLE dbo.[User] ALTER COLUMN password NVARCHAR(255) NOT NULL;");
                    System.out.println("  + Mở rộng cột password thành NVARCHAR(255) thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột password: " + e.getMessage());
                }

                // Cột status (1: hoạt động, 0: bị khóa)
                try {
                    st.execute("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.[User]') AND name = 'status') " +
                               "ALTER TABLE dbo.[User] ADD status INT NOT NULL DEFAULT 1;");
                    System.out.println("  + Bổ sung cột status thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột status: " + e.getMessage());
                }

                // Cột failed_attempts (đếm số lần đăng nhập sai)
                try {
                    st.execute("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.[User]') AND name = 'failed_attempts') " +
                               "ALTER TABLE dbo.[User] ADD failed_attempts INT NOT NULL DEFAULT 0;");
                    System.out.println("  + Bổ sung cột failed_attempts thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột failed_attempts: " + e.getMessage());
                }

                // Cột lock_time (thời gian tạm khóa khi brute-force)
                try {
                    st.execute("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.[User]') AND name = 'lock_time') " +
                               "ALTER TABLE dbo.[User] ADD lock_time DATETIME NULL;");
                    System.out.println("  + Bổ sung cột lock_time thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột lock_time: " + e.getMessage());
                }

                // Cột reset_token
                try {
                    st.execute("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.[User]') AND name = 'reset_token') " +
                               "ALTER TABLE dbo.[User] ADD reset_token NVARCHAR(100) NULL;");
                    System.out.println("  + Bổ sung cột reset_token thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột reset_token: " + e.getMessage());
                }

                // Cột reset_token_expiry
                try {
                    st.execute("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.[User]') AND name = 'reset_token_expiry') " +
                               "ALTER TABLE dbo.[User] ADD reset_token_expiry DATETIME NULL;");
                    System.out.println("  + Bổ sung cột reset_token_expiry thành công.");
                } catch (Exception e) {
                    System.out.println("  - Cột reset_token_expiry: " + e.getMessage());
                }
            }

            // 2. Mã hóa mật khẩu BCrypt cho các tài khoản mặc định
            System.out.println("[*] Đang băm mật khẩu chuẩn BCrypt cho các tài khoản hệ thống (Mật khẩu '123456')...");
            String defaultHash = BCrypt.hashpw("123456", BCrypt.gensalt(10));
            System.out.println("  + Chuỗi hash BCrypt mẫu: " + defaultHash);

            String[] accounts = {"admin", "trungnh", "manager", "khachhang"};
            for (String acc : accounts) {
                // Kiểm tra xem tài khoản đã tồn tại chưa
                try (PreparedStatement psCheck = conn.prepareStatement("SELECT id, password FROM dbo.[User] WHERE username = ?")) {
                    psCheck.setString(1, acc);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt("id");
                            String currentPass = rs.getString("password");
                            // Băm lại bằng BCrypt mới
                            String newHash = BCrypt.hashpw("123456", BCrypt.gensalt(10));
                            try (PreparedStatement psUp = conn.prepareStatement("UPDATE dbo.[User] SET password = ?, status = 1, failed_attempts = 0, lock_time = NULL WHERE id = ?")) {
                                psUp.setString(1, newHash);
                                psUp.setInt(2, userId);
                                psUp.executeUpdate();
                                System.out.println("  -> [CẬP NHẬT] Đã cập nhật BCrypt hash cho tài khoản '" + acc + "' (ID: " + userId + ")");
                            }
                        } else {
                            // Tạo mới nếu chưa có
                            String newHash = BCrypt.hashpw("123456", BCrypt.gensalt(10));
                            int roleId = "admin".equals(acc) || "trungnh".equals(acc) ? 1 : ("manager".equals(acc) ? 2 : 5);
                            String fullname = "admin".equals(acc) ? "Quản Trị Viên Hệ Thống" : ("trungnh".equals(acc) ? "ThS. Nguyễn Hữu Trung" : ("manager".equals(acc) ? "Quản Lý Cửa Hàng" : "Nguyễn Văn A"));
                            String email = acc + "@" + ("khachhang".equals(acc) ? "gmail.com" : "shoppingservice.vn");
                            if ("trungnh".equals(acc)) email = "trungnh@hcmute.edu.vn";

                            try (PreparedStatement psIns = conn.prepareStatement("INSERT INTO dbo.[User](email, username, fullname, password, avatar, roleid, phone, status, failed_attempts, createddate) VALUES (?, ?, ?, ?, 'avata.png', ?, '0901234567', 1, 0, GETDATE())")) {
                                psIns.setString(1, email);
                                psIns.setString(2, acc);
                                psIns.setString(3, fullname);
                                psIns.setString(4, newHash);
                                psIns.setInt(5, roleId);
                                psIns.executeUpdate();
                                System.out.println("  -> [THÊM MỚI] Đã thêm tài khoản '" + acc + "' với BCrypt hash thành công.");
                            }
                        }
                    }
                }
            }

            System.out.println("[HOÀN TẤT] Nâng cấp cơ sở dữ liệu và bảo mật tài khoản BCrypt thành công 100%!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }
}
