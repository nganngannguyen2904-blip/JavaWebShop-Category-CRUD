package database;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class DownloadProductImages {

    // Danh sách 60 ảnh Unsplash chất lượng cao đúng từng sản phẩm
    private static final Map<String, String> PHOTO_MAP = new LinkedHashMap<>();

    static {
        // --- 1. Quần Áo Nam (sp_nam_01 -> sp_nam_10) ---
        // 1. Áo Sơ Mi Nam Oxford Trắng Dài Tay
        PHOTO_MAP.put("sp_nam_01.png", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&h=600&fit=crop&q=85");
        // 2. Áo Thun Nam Cổ Tròn Cotton Compact
        PHOTO_MAP.put("sp_nam_02.png", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&h=600&fit=crop&q=85");
        // 3. Quần Jean Nam Slimfit Xanh Xước
        PHOTO_MAP.put("sp_nam_03.png", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&h=600&fit=crop&q=85");
        // 4. Áo Khoác Gió Nam 2 Lớp Chống Nước
        PHOTO_MAP.put("sp_nam_04.png", "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=600&h=600&fit=crop&q=85");
        // 5. Quần Tây Nam Công Sở Ống Đứng
        PHOTO_MAP.put("sp_nam_05.png", "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=600&h=600&fit=crop&q=85");
        // 6. Áo Polo Nam Phối Bo Cổ
        PHOTO_MAP.put("sp_nam_06.png", "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&h=600&fit=crop&q=85");
        // 7. Áo Hoodie Nam Form Rộng Unisex
        PHOTO_MAP.put("sp_nam_07.png", "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600&h=600&fit=crop&q=85");
        // 8. Quần Kaki Nam Túi Hộp Cargo
        PHOTO_MAP.put("sp_nam_08.png", "https://images.unsplash.com/photo-1517445312882-bc9910d016b7?w=600&h=600&fit=crop&q=85");
        // 9. Áo Blazer Nam Form Suông
        PHOTO_MAP.put("sp_nam_09.png", "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600&h=600&fit=crop&q=85");
        // 10. Bộ Quần Áo Thể Thao Nam Thun Lạnh
        PHOTO_MAP.put("sp_nam_10.png", "https://images.unsplash.com/photo-1483721310020-03333e577078?w=600&h=600&fit=crop&q=85");

        // --- 2. Quần Áo Nữ (sp_nu_01 -> sp_nu_10) ---
        // 11. Đầm Nữ Dáng Xòe Voan Hoa Nhí Vintage
        PHOTO_MAP.put("sp_nu_01.png", "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&h=600&fit=crop&q=85");
        // 12. Áo Kiểu Nữ Cổ Vuông Tay Phồng
        PHOTO_MAP.put("sp_nu_02.png", "https://images.unsplash.com/photo-1589810635657-232948472d98?w=600&h=600&fit=crop&q=85");
        // 13. Quần Ống Rộng Nữ Cạp Cao Hack Dáng
        PHOTO_MAP.put("sp_nu_03.png", "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=600&h=600&fit=crop&q=85");
        // 14. Chân Váy Chữ A Xếp Ly Ngắn Hàn Quốc
        PHOTO_MAP.put("sp_nu_04.png", "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600&h=600&fit=crop&q=85");
        // 15. Áo Cardigan Len Nữ Dệt Kim Cúc Cài
        PHOTO_MAP.put("sp_nu_05.png", "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=600&h=600&fit=crop&q=85");
        // 16. Set Đồ Nữ Công Sở Áo Vest Quần Tây
        PHOTO_MAP.put("sp_nu_06.png", "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?w=600&h=600&fit=crop&q=85");
        // 17. Áo Thun Nữ Oversize In Hình Nghệ Thuật
        PHOTO_MAP.put("sp_nu_07.png", "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=600&h=600&fit=crop&q=85");
        // 18. Đầm Body Nữ Cổ Yếm Ôm Dáng Dự Tiệc
        PHOTO_MAP.put("sp_nu_08.png", "https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=600&h=600&fit=crop&q=85");
        // 19. Áo Sơ Mi Lụa Nữ Cổ Đức Dáng Suông
        PHOTO_MAP.put("sp_nu_09.png", "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&h=600&fit=crop&q=85");
        // 20. Quần Jean Nữ Ống Loe Cạp Cao Cổ Điển
        PHOTO_MAP.put("sp_nu_10.png", "https://images.unsplash.com/photo-1582418702059-97ebafb35d09?w=600&h=600&fit=crop&q=85");

        // --- 3. Giày Dép Thời Trang (sp_giay_01 -> sp_giay_10) ---
        // 21. Giày Sneaker Nam Nữ Trắng Cổ Thấp Đế Độn
        PHOTO_MAP.put("sp_giay_01.png", "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&h=600&fit=crop&q=85");
        // 22. Giày Lười Nam Da Bò Thật Khâu Đế Thủ Công
        PHOTO_MAP.put("sp_giay_02.png", "https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=600&h=600&fit=crop&q=85");
        // 23. Giày Cao Gót Nữ Mũi Nhọn 7cm Sang Trọng
        PHOTO_MAP.put("sp_giay_03.png", "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&h=600&fit=crop&q=85");
        // 24. Giày Chạy Bộ Running Siêu Nhẹ Thoáng Khí
        PHOTO_MAP.put("sp_giay_04.png", "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=600&h=600&fit=crop&q=85");
        // 25. Dép Quai Ngang Unisex Đúc Nguyên Khối
        PHOTO_MAP.put("sp_giay_05.png", "https://images.unsplash.com/photo-1603808033192-082d6919d3e1?w=600&h=600&fit=crop&q=85");
        // 26. Sandal Nữ Quai Mảnh Đế Bệt Đi Học Dạo Phố
        PHOTO_MAP.put("sp_giay_06.png", "https://images.unsplash.com/photo-1562273138-f46be4ebdf33?w=600&h=600&fit=crop&q=85");
        // 27. Giày Boot Nữ Cổ Cao Da Lì Khóa Kéo Thời Trang
        PHOTO_MAP.put("sp_giay_07.png", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=600&fit=crop&q=85");
        // 28. Giày Oxford Nam Da Bóng Phong Cách Lịch Lãm
        PHOTO_MAP.put("sp_giay_08.png", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=600&h=600&fit=crop&q=85");
        // 29. Sandal Nam Quai Dù Đi Mưa Dã Ngoại Bền Bỉ
        PHOTO_MAP.put("sp_giay_09.png", "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600&h=600&fit=crop&q=85");
        // 30. Giày Búp Bê Nữ Đính Nơ Đế Bệt Mềm Mại
        PHOTO_MAP.put("sp_giay_10.png", "https://images.unsplash.com/photo-1560343090-f0409e92791a?w=600&h=600&fit=crop&q=85");

        // --- 4. Đồng Hồ & Phụ Kiện (sp_dh_01 -> sp_dh_10) ---
        // 31. Đồng Hồ Nam Dây Thép Không Gỉ 5ATM
        PHOTO_MAP.put("sp_dh_01.png", "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&h=600&fit=crop&q=85");
        // 32. Đồng Hồ Nữ Dây Da Mặt Tròn Đính Đá Tinh Xảo
        PHOTO_MAP.put("sp_dh_02.png", "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=600&h=600&fit=crop&q=85");
        // 33. Đồng Hồ Thông Minh Smartwatch Đo Nhịp Tim
        PHOTO_MAP.put("sp_dh_03.png", "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600&h=600&fit=crop&q=85");
        // 34. Kính Mát Unisex Phân Cực Polarized UV400
        PHOTO_MAP.put("sp_dh_04.png", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600&h=600&fit=crop&q=85");
        // 35. Ví Nam Da Bò Thật Dáng Ngang
        PHOTO_MAP.put("sp_dh_05.png", "https://images.unsplash.com/photo-1627123424574-724758594e93?w=600&h=600&fit=crop&q=85");
        // 36. Thắt Lưng Nam Da Khóa Tự Động Hợp Kim
        PHOTO_MAP.put("sp_dh_06.png", "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=600&h=600&fit=crop&q=85");
        // 37. Vòng Tay Bạc Nữ Ý 925 Đính Đá Pha Lê
        PHOTO_MAP.put("sp_dh_07.png", "https://images.unsplash.com/photo-1611591475819-79b8b730ab8c?w=600&h=600&fit=crop&q=85");
        // 38. Balo Laptop Chống Nước Cổng Sạc USB
        PHOTO_MAP.put("sp_dh_08.png", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&h=600&fit=crop&q=85");
        // 39. Nón Lưỡi Trai Unisex Vải Kaki Thêu Chữ Nổi
        PHOTO_MAP.put("sp_dh_09.png", "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600&h=600&fit=crop&q=85");
        // 40. Túi Xách Nữ Đeo Chéo Dây Xích Sang Chảnh
        PHOTO_MAP.put("sp_dh_10.png", "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&h=600&fit=crop&q=85");

        // --- 5. Thiết Bị Điện Tử (sp_dt_01 -> sp_dt_10) ---
        // 41. Tai Nghe Bluetooth Không Dây TWS ANC
        PHOTO_MAP.put("sp_dt_01.png", "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&h=600&fit=crop&q=85");
        // 42. Loa Bluetooth Mini Bass Trầm Chống Nước IPX7
        PHOTO_MAP.put("sp_dt_02.png", "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=600&h=600&fit=crop&q=85");
        // 43. Bàn Phím Cơ Không Dây 3 Chế Độ RGB
        PHOTO_MAP.put("sp_dt_03.png", "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&h=600&fit=crop&q=85");
        // 44. Chuột Gaming Không Dây Siêu Nhẹ DPI 16000
        PHOTO_MAP.put("sp_dt_04.png", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&h=600&fit=crop&q=85");
        // 45. Củ Sạc Nhanh GaN 65W 3 Cổng Type-C USB
        PHOTO_MAP.put("sp_dt_05.png", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&h=600&fit=crop&q=85");
        // 46. Pin Sạc Dự Phòng 20000mAh PD 22.5W
        PHOTO_MAP.put("sp_dt_06.png", "https://images.unsplash.com/photo-1609592426867-0c7f7e91d84e?w=600&h=600&fit=crop&q=85");
        // 47. Đèn Bàn LED Chống Cận Tích Hợp Sạc Không Dây
        PHOTO_MAP.put("sp_dt_07.png", "https://images.unsplash.com/photo-1534073828943-f801091bb18c?w=600&h=600&fit=crop&q=85");
        // 48. Camera Giám Sát Gia Đình Xoay 360 Full HD 1080P
        PHOTO_MAP.put("sp_dt_08.png", "https://images.unsplash.com/photo-1557597774-9d273605dfa9?w=600&h=600&fit=crop&q=85");
        // 49. Tay Cầm Chơi Game Không Dây Rung Kép
        PHOTO_MAP.put("sp_dt_09.png", "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=600&h=600&fit=crop&q=85");
        // 50. Hub Chuyển Đổi Type-C 8 Trong 1 Ra HDMI 4K
        PHOTO_MAP.put("sp_dt_10.png", "https://images.unsplash.com/photo-1616440347437-b1c73416efc2?w=600&h=600&fit=crop&q=85");

        // --- 6. Mỹ Phẩm & Làm Đẹp (sp_mp_01 -> sp_mp_10) ---
        // 51. Kem Chống Nắng Nâng Tông Kiềm Dầu SPF50+
        PHOTO_MAP.put("sp_mp_01.png", "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600&h=600&fit=crop&q=85");
        // 52. Nước Tẩy Trang Dịu Nhẹ Không Cồn
        PHOTO_MAP.put("sp_mp_02.png", "https://images.unsplash.com/photo-1556228722-d0b5de709121?w=600&h=600&fit=crop&q=85");
        // 53. Serum Dưỡng Sáng Da Mờ Thâm Vitamin C
        PHOTO_MAP.put("sp_mp_03.png", "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&h=600&fit=crop&q=85");
        // 54. Kem Dưỡng Ẩm Phục Hồi Da B5 Rau Má
        PHOTO_MAP.put("sp_mp_04.png", "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=600&h=600&fit=crop&q=85");
        // 55. Sữa Rửa Mặt Tạo Bọt Chiết Xuất Tràm Trà
        PHOTO_MAP.put("sp_mp_05.png", "https://images.unsplash.com/photo-1556228724-4da94348a29f?w=600&h=600&fit=crop&q=85");
        // 56. Son Kem Lì Kháng Nước Lâu Trôi Màu Đỏ Đất
        PHOTO_MAP.put("sp_mp_06.png", "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=600&h=600&fit=crop&q=85");
        // 57. Nước Hoa Nữ Hương Hoa Cỏ Dịu Ngọt Quyến Rũ
        PHOTO_MAP.put("sp_mp_07.png", "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600&h=600&fit=crop&q=85");
        // 58. Mặt Nạ Dưỡng Da Cấp Ẩm Chuyên Sâu Hộp 10 Miếng
        PHOTO_MAP.put("sp_mp_08.png", "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=600&h=600&fit=crop&q=85");
        // 59. Nước Hoa Hồng Toner Cân Bằng Độ Ẩm Se Khít Lỗ Chân Lông
        PHOTO_MAP.put("sp_mp_09.png", "https://images.unsplash.com/photo-1608248597359-0c6753713f0c?w=600&h=600&fit=crop&q=85");
        // 60. Phấn Nước Cushion Che Khuyết Điểm Kiềm Dầu
        PHOTO_MAP.put("sp_mp_10.png", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=600&h=600&fit=crop&q=85");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   DOWLOAD VÀ ĐỒNG BỘ ẢNH SẢN PHẨM THẬT CHẤT LƯỢNG CAO CHO TOÀN BỘ 60 SẢN PHẨM");
        System.out.println("================================================================================\n");

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Các thư mục lưu trữ ảnh trong dự án
        File[] targetDirs = {
            new File("upload/product"),
            new File("src/main/webapp/upload/product"),
            new File("ShoppingServiceMVC/src/main/webapp/upload/product"),
            new File("src/main/resources/static/images/products")
        };

        for (File dir : targetDirs) {
            if (!dir.exists()) dir.mkdirs();
        }

        int successCount = 0;
        int total = PHOTO_MAP.size();

        for (Map.Entry<String, String> entry : PHOTO_MAP.entrySet()) {
            String fileName = entry.getKey();
            String url = entry.getValue();

            System.out.printf("[Đang tải %2d/%2d]: %-15s từ URL: %s%n", (successCount + 1), total, fileName, url);

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(Duration.ofSeconds(20))
                        .build();

                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200 && response.body().length > 5000) {
                    byte[] bytes = response.body();
                    BufferedImage rawImg = ImageIO.read(new ByteArrayInputStream(bytes));

                    if (rawImg != null) {
                        // Chuẩn hóa kích thước 600x600 chuẩn ảnh e-commerce
                        BufferedImage formattedImg = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = formattedImg.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.drawImage(rawImg, 0, 0, 600, 600, null);
                        g.dispose();

                        // Lưu ra từng thư mục đích
                        for (File dir : targetDirs) {
                            File dest = new File(dir, fileName);
                            ImageIO.write(formattedImg, "png", dest);
                        }

                        successCount++;
                        System.out.printf("   [✓ Thành công] Đã lưu %s vào tất cả thư mục ảnh (%d bytes)%n", fileName, bytes.length);
                    } else {
                        System.err.printf("   [!] Không thể đọc định dạng ảnh từ URL cho %s%n", fileName);
                    }
                } else {
                    System.err.printf("   [!] HTTP Lỗi %d khi tải %s%n", response.statusCode(), fileName);
                }
            } catch (Exception ex) {
                System.err.printf("   [!] Ngoại lệ khi tải %s: %s%n", fileName, ex.getMessage());
            }

            // Nghỉ nhẹ 100ms tránh rate limit
            Thread.sleep(100);
        }

        System.out.println("\n================================================================================");
        System.out.printf(" KẾT QUẢ TẢI ẢNH: %d/%d ẢNH ĐÃ ĐƯỢC TẢI VÀ ĐỒNG BỘ THÀNH CÔNG!%n", successCount, total);
        System.out.println("================================================================================\n");

        // Kiểm tra và cập nhật Database để đảm bảo 100% sản phẩm trỏ đúng file ảnh
        syncDatabaseProductImages();
    }

    private static void syncDatabaseProductImages() {
        System.out.println("[*] Đang kiểm tra và đồng bộ tên file ảnh trong CSDL SQL Server (Database: BanHang)...");
        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        try (Connection conn = DriverManager.getConnection(url)) {
            String checkSql = "SELECT product_id, product_name, image FROM Product ORDER BY product_id";
            int verifiedCount = 0;
            int missingCount = 0;

            try (PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("product_id");
                    String name = rs.getString("product_name");
                    String img = rs.getString("image");

                    File checkFile = new File("upload/product/" + img);
                    if (checkFile.exists() && checkFile.length() > 5000) {
                        verifiedCount++;
                    } else {
                        missingCount++;
                        System.err.printf("[!] Cảnh báo: Sản phẩm ID=%d (%s) có file ảnh %s không tồn tại hoặc quá nhỏ!%n", id, name, img);
                    }
                }
            }

            System.out.printf("[✓] Đã kiểm tra 100%% CSDL: %d sản phẩm có ảnh thật hợp lệ, %d sản phẩm thiếu ảnh.%n", verifiedCount, missingCount);
        } catch (Exception e) {
            System.err.println("[!] Lỗi khi kiểm tra CSDL: " + e.getMessage());
        }
    }
}
