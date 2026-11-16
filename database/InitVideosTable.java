import java.sql.*;

public class InitVideosTable {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement()) {

            System.out.println("[*] Kiểm tra và tạo bảng Videos trong database BanHang...");

            String createTableSql = 
                "IF OBJECT_ID('dbo.Videos', 'U') IS NULL " +
                "BEGIN " +
                "    CREATE TABLE dbo.Videos ( " +
                "        VideoId VARCHAR(50) NOT NULL PRIMARY KEY, " +
                "        Title NVARCHAR(255) NOT NULL, " +
                "        Description NVARCHAR(MAX) NULL, " +
                "        VideoUrl NVARCHAR(255) NOT NULL, " +
                "        Poster NVARCHAR(255) NULL, " +
                "        Views INT NOT NULL DEFAULT 0, " +
                "        Active INT NOT NULL DEFAULT 1, " +
                "        UploadDate DATETIME NOT NULL DEFAULT GETDATE(), " +
                "        UserId INT NOT NULL, " +
                "        CONSTRAINT FK_Videos_User FOREIGN KEY (UserId) REFERENCES dbo.[User](id) ON DELETE CASCADE " +
                "    ); " +
                "    PRINT 'Bang Videos da duoc tao thanh cong!'; " +
                "END " +
                "ELSE " +
                "BEGIN " +
                "    PRINT 'Bang Videos da ton tai san!'; " +
                "END";

            st.executeUpdate(createTableSql);
            System.out.println("[+] Tạo/xác nhận bảng Videos thành công!");

            // Kiểm tra số lượng video hiện có
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Videos");
            rs.next();
            int count = rs.getInt(1);
            System.out.println("[*] Số lượng Video hiện có trong DB BanHang: " + count);

            if (count == 0) {
                System.out.println("[*] Đang chèn dữ liệu mẫu 12 Video phong phú...");

                // Lấy userId của admin, trungnh, khachhang
                int adminId = 1;
                int gvhdId = 2;
                int khId = 4;
                try (ResultSet uRs = st.executeQuery("SELECT id, username FROM dbo.[User]")) {
                    while (uRs.next()) {
                        String un = uRs.getString("username");
                        int id = uRs.getInt("id");
                        if ("admin".equalsIgnoreCase(un)) adminId = id;
                        else if ("trungnh".equalsIgnoreCase(un)) gvhdId = id;
                        else if ("khachhang".equalsIgnoreCase(un)) khId = id;
                    }
                }

                String insertSql = 
                    "INSERT INTO dbo.Videos (VideoId, Title, Description, VideoUrl, Poster, Views, Active, UploadDate, UserId) VALUES " +
                    "('VID001', N'Hướng dẫn phối đồ công sở nam lịch lãm 2026', N'Tổng hợp các mẹo phối áo sơ mi Oxford cùng quần tây chuẩn form công sở giúp bạn luôn tự tin.', 'video_demo_01.mp4', 'cat_nam.png', 1250, 1, DATEADD(hour, -2, GETDATE()), " + adminId + "), " +
                    "('VID002', N'Review Bộ sưu tập Đầm dạ hội nữ thiết kế hè thu', N'Trải nghiệm chi tiết chất vải tuyết mưa và đường may tỉ mỉ của bộ sưu tập đầm dạ hội sang trọng.', 'video_demo_02.mp4', 'cat_nu.png', 890, 1, DATEADD(hour, -5, GETDATE()), " + gvhdId + "), " +
                    "('VID003', N'Đập hộp & Trên tay Giày Sneaker Retro phong cách đường phố', N'Đánh giá đế đệm êm ái, độ thoáng khí và độ bám đường vượt trội của đôi sneaker hot nhất tháng.', 'video_demo_03.mp4', 'cat_giay.png', 2150, 1, DATEADD(hour, -10, GETDATE()), " + adminId + "), " +
                    "('VID004', N'Top 5 Đồng hồ cơ lộ máy Skeleton bán chạy nhất', N'Cận cảnh bộ máy cơ tự động tinh xảo và mặt kính sapphire chống xước tuyệt đối.', 'video_demo_04.mp4', 'cat_dongho.png', 1780, 1, DATEADD(day, -1, GETDATE()), " + khId + "), " +
                    "('VID005', N'Trải nghiệm Tai nghe Bluetooth True Wireless chống ồn ANC', N'Thử nghiệm khả năng chống ồn chủ động 45dB và thời lượng pin 40 tiếng liên tục.', 'video_demo_05.mp4', 'cat_dientu.png', 3400, 1, DATEADD(day, -2, GETDATE()), " + adminId + "), " +
                    "('VID006', N'Quy trình Skincare dưỡng ẩm phục hồi da chuyên sâu', N'Hướng dẫn các bước chăm sóc da buổi tối với serum HA và kem dưỡng B5 chính hãng.', 'video_demo_06.mp4', 'cat_mypham.png', 960, 1, DATEADD(day, -3, GETDATE()), " + gvhdId + "), " +
                    "('VID007', N'Cẩm nang bảo quản Áo khoác gió 2 lớp luôn mới', N'Những lưu ý quan trọng khi giặt và bảo quản áo khoác dù chống nước tránh bong tróc.', 'video_demo_07.mp4', 'sp_nam_04.png', 640, 1, DATEADD(day, -4, GETDATE()), " + khId + "), " +
                    "('VID008', N'Cách vệ sinh Giày da bò thật không làm xước bề mặt', N'Chia sẻ phương pháp đánh xi bóng và bảo dưỡng da bò thật bền đẹp theo năm tháng.', 'video_demo_08.mp4', 'sp_giay_02.png', 1120, 1, DATEADD(day, -5, GETDATE()), " + adminId + "), " +
                    "('VID009', N'Trên tay Bàn phím cơ không dây Switch Silent gõ êm', N'Đánh giá cảm giác gõ êm ái, kết nối đa thiết bị Bluetooth 5.3 cực nhạy cho dân văn phòng.', 'video_demo_09.mp4', 'sp_dt_04.png', 2890, 1, DATEADD(day, -6, GETDATE()), " + gvhdId + "), " +
                    "('VID010', N'Phân biệt Son môi Matte chính hãng và hàng kém chất lượng', N'Các dấu hiệu nhận biết son kem lì chính hãng qua vỏ hộp, mã vạch và chất son mịn lì.', 'video_demo_10.mp4', 'sp_mp_01.png', 1450, 1, DATEADD(day, -7, GETDATE()), " + khId + "), " +
                    "('VID011', N'Hướng dẫn chọn Size Quần Jean nam chuẩn vóc dáng', N'Cách đo vòng bụng và đùi để chọn đúng form quần Slimfit vừa vặn nhất.', 'video_demo_11.mp4', 'sp_nam_03.png', 730, 1, DATEADD(day, -8, GETDATE()), " + adminId + "), " +
                    "('VID012', N'Bí quyết chọn Đồng hồ dây da thanh lịch cho phái nữ', N'Tư vấn kích thước mặt đồng hồ và màu dây da hài hòa với màu da tay người châu Á.', 'video_demo_12.mp4', 'sp_dh_02.png', 510, 1, DATEADD(day, -9, GETDATE()), " + khId + ")";

                int inserted = st.executeUpdate(insertSql);
                System.out.println("[+] Đã chèn " + inserted + " video mẫu vào database BanHang!");
            }
        }
    }
}
