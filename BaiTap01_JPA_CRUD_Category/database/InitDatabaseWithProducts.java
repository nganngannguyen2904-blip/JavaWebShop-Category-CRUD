import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class InitDatabaseWithProducts {

    static class ProdItem {
        int cateId;
        String name;
        double price;
        int qty;
        String image;
        String desc;

        public ProdItem(int cateId, String name, double price, int qty, String image, String desc) {
            this.cateId = cateId;
            this.name = name;
            this.price = price;
            this.qty = qty;
            this.image = image;
            this.desc = desc;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== KHOI TAO DATABASE & DU LIEU SAN PHAM TIENG VIET ===");

        String url = "jdbc:sqlserver://localhost:1433;databaseName=BanHang;integratedSecurity=true;encrypt=true;trustServerCertificate=true;sendStringParametersAsUnicode=true;characterEncoding=UTF-8;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("1. Ket noi database BanHang thanh cong!");

            // 1. Kiem tra va tao bang Product neu chua co
            try (Statement st = conn.createStatement()) {
                // Drop Foreign key or table if needed
                st.executeUpdate("IF OBJECT_ID('dbo.Product', 'U') IS NOT NULL DROP TABLE dbo.Product;");
                
                String createProductSql = "CREATE TABLE Product ("
                        + "product_id INT IDENTITY(1,1) PRIMARY KEY, "
                        + "product_name NVARCHAR(255) NOT NULL, "
                        + "description NVARCHAR(MAX), "
                        + "price DECIMAL(18,2) NOT NULL DEFAULT 0, "
                        + "image NVARCHAR(255), "
                        + "cate_id INT NOT NULL, "
                        + "quantity INT NOT NULL DEFAULT 0, "
                        + "status INT NOT NULL DEFAULT 1, "
                        + "created_at DATETIME DEFAULT GETDATE(), "
                        + "CONSTRAINT FK_Product_Category FOREIGN KEY (cate_id) REFERENCES Category(cate_id) ON DELETE CASCADE"
                        + ");";
                st.executeUpdate(createProductSql);
                System.out.println("2. Tao bang Product thanh cong!");
            }

            // 2. Cap nhat lai bang Category tieng Viet chuan UTF-8
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM Category;");
                st.executeUpdate("DBCC CHECKIDENT ('Category', RESEED, 0);");
            }

            String insertCateSql = "SET IDENTITY_INSERT Category ON; "
                    + "INSERT INTO Category (cate_id, cate_name, icons) VALUES (?, ?, ?); "
                    + "SET IDENTITY_INSERT Category OFF;";

            try (PreparedStatement ps = conn.prepareStatement(insertCateSql)) {
                Object[][] categories = {
                    {1, "Quần Áo Nam", "cat_nam.png"},
                    {2, "Quần Áo Nữ", "cat_nu.png"},
                    {3, "Giày Dép Thời Trang", "cat_giay.png"},
                    {4, "Đồng Hồ & Phụ Kiện", "cat_dongho.png"},
                    {5, "Thiết Bị Điện Tử", "cat_dientu.png"},
                    {6, "Mỹ Phẩm & Làm Đẹp", "cat_mypham.png"}
                };

                for (Object[] cat : categories) {
                    ps.setInt(1, (Integer) cat[0]);
                    ps.setString(2, (String) cat[1]);
                    ps.setString(3, (String) cat[2]);
                    ps.executeUpdate();
                }
                System.out.println("3. Cap nhat 6 Danh muc tieng Viet chuan UTF-8 thanh cong!");
            }

            // 3. Chen 60 san pham mau tieng Viet
            List<ProdItem> products = getSampleProducts();
            String insertProdSql = "INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, 1);";

            try (PreparedStatement ps = conn.prepareStatement(insertProdSql)) {
                for (ProdItem p : products) {
                    ps.setString(1, p.name);
                    ps.setString(2, p.desc);
                    ps.setBigDecimal(3, BigDecimal.valueOf(p.price));
                    ps.setString(4, p.image);
                    ps.setInt(5, p.cateId);
                    ps.setInt(6, p.qty);
                    ps.executeUpdate();
                }
                System.out.println("4. Da chen " + products.size() + " san pham vao CSDL thanh cong!");
            }

            // 4. Cap nhat va bo sung tai khoan [User] voi tieng Viet chuan
            String checkUserSql = "SELECT COUNT(*) FROM [User] WHERE username = ?";
            String insertUserSql = "INSERT INTO [User] (email, username, fullname, password, avatar, roleid, phone, createddate) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE());";
            String updateUserSql = "UPDATE [User] SET fullname = ?, password = ?, roleid = ?, email = ?, phone = ?, avatar = ? WHERE username = ?;";

            Object[][] users = {
                {"admin@shoppingservice.vn", "admin", "Quản Trị Viên Hệ Thống", "123456", "avata.png", 1, "0901234567"},
                {"trungnh@hcmute.edu.vn", "trungnh", "ThS. Nguyễn Hữu Trung", "123456", "avata.png", 1, "0912345678"},
                {"manager@shoppingservice.vn", "manager", "Quản Lý Cửa Hàng", "123456", "avata.png", 2, "0923456789"},
                {"khachhang@gmail.com", "khachhang", "Nguyễn Văn A", "123456", "avata.png", 5, "0934567890"}
            };

            for (Object[] u : users) {
                String email = (String) u[0];
                String uname = (String) u[1];
                String fname = (String) u[2];
                String pass = (String) u[3];
                String avt = (String) u[4];
                int role = (Integer) u[5];
                String phone = (String) u[6];

                boolean exists = false;
                try (PreparedStatement psCheck = conn.prepareStatement(checkUserSql)) {
                    psCheck.setString(1, uname);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) exists = true;
                    }
                }

                if (exists) {
                    try (PreparedStatement psUp = conn.prepareStatement(updateUserSql)) {
                        psUp.setString(1, fname);
                        psUp.setString(2, pass);
                        psUp.setInt(3, role);
                        psUp.setString(4, email);
                        psUp.setString(5, phone);
                        psUp.setString(6, avt);
                        psUp.setString(7, uname);
                        psUp.executeUpdate();
                    }
                } else {
                    try (PreparedStatement psIn = conn.prepareStatement(insertUserSql)) {
                        psIn.setString(1, email);
                        psIn.setString(2, uname);
                        psIn.setString(3, fname);
                        psIn.setString(4, pass);
                        psIn.setString(5, avt);
                        psIn.setInt(6, role);
                        psIn.setString(7, phone);
                        psIn.executeUpdate();
                    }
                }
            }
            System.out.println("5. Cap nhat thong tin nguoi dung [User] tieng Viet UTF-8 thanh cong!");

            // 5. Tao hinh anh san pham thuc te trong d:/LT_Web/BTVN_9-11/upload/product
            generateProductImages(products);
            System.out.println("6. Da sinh toan bo file anh san pham thanh cong!");
        }

        System.out.println("=== HOAN TAT KHOI TAO CSDL VA DU LIEU SAN PHAM ===");
    }

    private static List<ProdItem> getSampleProducts() {
        List<ProdItem> list = new ArrayList<>();

        // Danh muc 1: Quần Áo Nam
        list.add(new ProdItem(1, "Áo Sơ Mi Nam Oxford Trắng Dài Tay Cao Cấp", 280000, 50, "sp_nam_01.png",
                "Áo sơ mi nam chất liệu vải Oxford 100% cotton dày dặn, thấm hút mồ hôi tốt, chống nhăn, form dáng Regular Fit trẻ trung lịch lãm phù hợp đi làm và dự tiệc."));
        list.add(new ProdItem(1, "Áo Thun Nam Cổ Tròn Cotton Compact 250gsm", 150000, 120, "sp_nam_02.png",
                "Áo thun basic nam sử dụng sợi cotton compact chải kỹ, co giãn 4 chiều mềm mịn, bề mặt vải không xù lông sau nhiều lần giặt."));
        list.add(new ProdItem(1, "Quần Jean Nam Slimfit Co Giãn Màu Xanh Xước", 350000, 45, "sp_nam_03.png",
                "Quần jean nam dáng ôm nhẹ tôn dáng, chất liệu denim pha spandex co giãn thoải mái khi vận động cả ngày dài."));
        list.add(new ProdItem(1, "Áo Khoác Gió Nam 2 Lớp Chống Nước Chống Gió", 320000, 35, "sp_nam_04.png",
                "Áo khoác dù nam 2 lớp cao cấp, trượt nước hiệu quả, lót lưới thoáng khí, mũ tháo rời linh hoạt cho mùa mưa gió."));
        list.add(new ProdItem(1, "Quần Tây Nam Công Sở Ống Đứng Chống Nhăn", 310000, 60, "sp_nam_05.png",
                "Quần tây nam phong cách Hàn Quốc, đường ly sắc nét, chất liệu tuyết mưa cao cấp không bai xù, tôn dáng chuẩn công sở."));
        list.add(new ProdItem(1, "Áo Polo Nam Phối Bo Cổ Dệt Họa Tiết Thể Thao", 220000, 80, "sp_nam_06.png",
                "Áo thun có cổ polo nam dệt kim pique thoáng mát, logo thêu tinh tế trước ngực, mang đến phong cách năng động hiện đại."));
        list.add(new ProdItem(1, "Áo Hoodie Nam Form Rộng Unisex Nỉ Bông Ấm Áp", 290000, 40, "sp_nam_07.png",
                "Áo nỉ chui đầu có mũ rộng rãi, vải nỉ chân cua trần bông giữ ấm hoàn hảo cho những ngày se lạnh cuối năm."));
        list.add(new ProdItem(1, "Quần Kaki Nam Túi Hộp Phong Cách Cargo Nam Tính", 275000, 30, "sp_nam_08.png",
                "Quần túi hộp cargo pants nam chất kaki dày dặn, form ống suông bo gấu trẻ trung, phong cách đường phố cá tính."));
        list.add(new ProdItem(1, "Áo Blazer Nam Form Suông Hiện Đại Nhẹ Nhàng", 550000, 25, "sp_nam_09.png",
                "Áo vest blazer nam 1 nút dáng suông trẻ trung, dễ phối với áo thun và quần jean hoặc quần tây."));
        list.add(new ProdItem(1, "Bộ Quần Áo Thể Thao Nam Thun Lạnh Thoáng Khí", 240000, 70, "sp_nam_10.png",
                "Set đồ bộ thể thao nam chất liệu thun mè thể thao co giãn 4 chiều, nhanh khô, kháng khuẩn khi chạy bộ và tập gym."));

        // Danh muc 2: Quần Áo Nữ
        list.add(new ProdItem(2, "Đầm Nữ Dáng Xòe Voan Hoa Nhí Vintage Dễ Thương", 320000, 40, "sp_nu_01.png",
                "Đầm hoa nhí tay bồng nhẹ nhàng, chất liệu tơ voan 2 lớp mềm mại bay bổng, tôn vòng eo và vóc dáng thon gọn."));
        list.add(new ProdItem(2, "Áo Kiểu Nữ Cổ Vuông Tay Phồng Tiểu Thư", 190000, 65, "sp_nu_02.png",
                "Áo croptop nữ cổ vuông quyến rũ, chất kate lụa thoáng mát, thiết kế nhún chun sau lưng ôm khít vòng eo."));
        list.add(new ProdItem(2, "Quần Ống Rộng Nữ Cạp Cao Hack Dáng Chuẩn", 260000, 90, "sp_nu_03.png",
                "Quần suông ống rộng culottes cạp cao khóa trước, chất tuyết mưa rủ dáng mượt mà, kéo dài đôi chân thanh thoát."));
        list.add(new ProdItem(2, "Chân Váy Chữ A Xếp Ly Ngắn Phong Cách Hàn Quốc", 180000, 85, "sp_nu_04.png",
                "Chân váy tennis xếp ly đều đặn có kèm quần bảo hộ bên trong, năng động trẻ trung khi phối cùng sơ mi và áo thun."));
        list.add(new ProdItem(2, "Áo Cardigan Len Nữ Dệt Kim Cúc Cài Nhẹ Nhàng", 240000, 55, "sp_nu_05.png",
                "Áo khoác len cardigan mỏng nhẹ, họa tiết dệt kim tinh tế pastel ngọt ngào, phù hợp khoác nhẹ mùa thu đông."));
        list.add(new ProdItem(2, "Set Đồ Nữ Công Sở Áo Vest Kèm Quần Tây Cao Cấp", 590000, 30, "sp_nu_06.png",
                "Bộ vest nữ phong cách công sở sang trọng lịch thiệp, đường may tỉ mỉ, đệm vai đứng phom chuyên nghiệp."));
        list.add(new ProdItem(2, "Áo Thun Nữ Oversize In Hình Nghệ Thuật", 140000, 110, "sp_nu_07.png",
                "Áo phông nữ form rộng giấu quần cá tính, chất thun cotton 100% thoáng mát in lụa sắc nét không bong tróc."));
        list.add(new ProdItem(2, "Đầm Body Nữ Cổ Yếm Ôm Dáng Quyến Rũ Dự Tiệc", 380000, 35, "sp_nu_08.png",
                "Váy dạ hội ôm sát body cổ yếm gợi cảm, chất thun gân tăm co giãn ôm trọn đường cong quyến rũ cho nàng tiệc đêm."));
        list.add(new ProdItem(2, "Áo Sơ Mi Lụa Nữ Cổ Đức Dáng Suông Nhẹ Nhàng", 230000, 60, "sp_nu_09.png",
                "Áo sơ mi nữ công sở chất lụa satin ngọc trai mềm óng, không nhăn, tạo cảm giác quý phái thanh lịch nơi làm việc."));
        list.add(new ProdItem(2, "Quần Jean Nữ Ống Loe Cạp Cao Phong Cách Cổ Điển", 340000, 45, "sp_nu_10.png",
                "Quần bò loe nữ lưng cao nâng mông thon đùi, màu xanh denim cổ điển chất bò co giãn mạnh mẽ."));

        // Danh muc 3: Giày Dép Thời Trang
        list.add(new ProdItem(3, "Giày Sneaker Nam Nữ Trắng Cổ Thấp Đế Độn 4cm", 390000, 60, "sp_giay_01.png",
                "Giày sneaker thể thao phong cách đường phố, da PU chống bám bẩn, đế cao su non êm ái tăng chiều cao 4cm."));
        list.add(new ProdItem(3, "Giày Lười Nam Da Bò Thật Khâu Đế Thủ Công", 580000, 30, "sp_giay_02.png",
                "Giày mọi lười loafer nam chất da bò nguyên tấm êm ái, đế đúc cao su nguyên khối bám đường chống trơn trượt."));
        list.add(new ProdItem(3, "Giày Cao Gót Nữ Mũi Nhọn 7cm Sang Trọng", 360000, 45, "sp_giay_03.png",
                "Giày cao gót nữ gót nhọn thanh mảnh bọc da mờ cao cấp, lót mút êm chân không đau mỏi khi đứng lâu."));
        list.add(new ProdItem(3, "Giày Chạy Bộ Running Siêu Nhẹ Thoáng Khí", 450000, 55, "sp_giay_04.png",
                "Giày chạy bộ thể thao đệm khí Air Zoom êm ái, thân giày dệt flyknit thông thoáng giảm sốc tối ưu khi luyện tập."));
        list.add(new ProdItem(3, "Dép Quai Ngang Unisex Đúc Nguyên Khối Chống Trượt", 120000, 150, "sp_giay_05.png",
                "Dép bánh mì quai ngang chất liệu EVA siêu nhẹ chống nước chống trơn trợt cực tốt, êm chân cho đi học đi chơi."));
        list.add(new ProdItem(3, "Sandal Nữ Quai Mảnh Đế Bệt Đi Học Dạo Phố", 190000, 70, "sp_giay_06.png",
                "Giày sandal nữ dây quai mảnh khóa gài ôm cổ chân, đế cao su chống trượt tạo phong cách nhẹ nhàng trẻ trung."));
        list.add(new ProdItem(3, "Giày Boot Nữ Cổ Cao Da Lì Khóa Kéo Thời Trang", 420000, 35, "sp_giay_07.png",
                "Boot cổ lửng nữ đế vuông 5cm dễ đi, da lì chống trầy xước, phong cách sành điệu cá tính cho mùa đông."));
        list.add(new ProdItem(3, "Giày Oxford Nam Da Bóng Phong Cách Lịch Lãm", 620000, 25, "sp_giay_08.png",
                "Giày tây nam buộc dây Oxford đế gỗ sang trọng, da bóng cao cấp chuẩn phong cách quý ông tiệc cưới sự kiện."));
        list.add(new ProdItem(3, "Sandal Nam Quai Dù Đi Mưa Dã Ngoại Bền Bỉ", 230000, 80, "sp_giay_09.png",
                "Sandal quai hậu nam dù dệt may chắc chắn, đế đúc gợn sóng bám đất cực tốt khi leo núi và đi phượt."));
        list.add(new ProdItem(3, "Giày Búp Bê Nữ Đính Nơ Đế Bệt Mềm Mại", 210000, 65, "sp_giay_10.png",
                "Giày búp bê nữ da microfiber mềm không đau gót, đính nơ kim loại xinh xắn phối đầm váy duyên dáng."));

        // Danh muc 4: Đồng Hồ & Phụ Kiện
        list.add(new ProdItem(4, "Đồng Hồ Nam Dây Thép Không Gỉ Chống Nước 5ATM", 750000, 30, "sp_dh_01.png",
                "Đồng hồ nam máy quartz Nhật Bản kính sapphire chống xước, vỏ và dây thép không gỉ 316L mạ PVD bền màu."));
        list.add(new ProdItem(4, "Đồng Hồ Nữ Dây Da Mặt Tròn Đính Đá Tinh Xảo", 520000, 40, "sp_dh_02.png",
                "Đồng hồ nữ mặt số xà cừ đính viền đá lấp lánh, dây da bò mềm mại ôm cổ tay xinh xắn của quý cô."));
        list.add(new ProdItem(4, "Đồng Hồ Thông Minh Smartwatch Đo Nhịp Tim Huyết Áp", 890000, 50, "sp_dh_03.png",
                "Smartwatch màn hình AMOLED tràn viền, hỗ trợ nghe gọi Bluetooth, theo dõi 100+ chế độ thể thao pin 10 ngày."));
        list.add(new ProdItem(4, "Kính Mát Unisex Mắt Kính Phân Cực Polarized Chống UV400", 250000, 90, "sp_dh_04.png",
                "Kính râm phi công gọng kim loại titan siêu nhẹ, tròng kính polarized chống chói lóa và cản 100% tia tử ngoại UV."));
        list.add(new ProdItem(4, "Ví Nam Da Bò Thật Dáng Ngang Nhiều Ngăn Đựng Tiền", 290000, 75, "sp_dh_05.png",
                "Ví bóp nam da bò sáp tự nhiên may tay tỉ mỉ, có ngăn khóa kéo an toàn và nhiều khe cắm thẻ ngân hàng."));
        list.add(new ProdItem(4, "Thắt Lưng Nam Da Khóa Tự Động Hợp Kim Cao Cấp", 220000, 85, "sp_dh_06.png",
                "Dây nịt nam da bò 2 lớp dẻo dai, mặt khóa tự động trượt êm không lo đục lỗ, phù hợp quần jean và quần tây."));
        list.add(new ProdItem(4, "Vòng Tay Bạc Nữ Ý 925 Đính Đá Pha Lê May Mắn", 340000, 60, "sp_dh_07.png",
                "Lắc tay nữ bạc S925 chuẩn kiểm định, mặt hoa đính đá lấp lánh có chuỗi điều chỉnh kích cỡ cổ tay."));
        list.add(new ProdItem(4, "Balo Laptop Chống Nước Có Cổng Sạc USB Tiện Lợi", 380000, 45, "sp_dh_08.png",
                "Balo công sở đựng laptop 15.6 inch vải Oxford chống thấm trượt nước, quai đeo đệm xốp thoáng khí trợ lực."));
        list.add(new ProdItem(4, "Nón Lưỡi Trai Unisex Vải Kaki Thêu Chữ Nổi", 110000, 120, "sp_dh_09.png",
                "Mũ lưỡi trai phong cách nón kết cá tính, khóa kim loại điều chỉnh độ rộng chóp đầu chuẩn phom cứng cáp."));
        list.add(new ProdItem(4, "Túi Xách Nữ Đeo Chéo Phối Dây Xích Sang Chảnh", 330000, 55, "sp_dh_10.png",
                "Túi đeo chéo nữ trần trám nắp gập khóa vặn kim loại mạ vàng, bên trong có 3 ngăn rộng rãi tiện dụng."));

        // Danh muc 5: Thiết Bị Điện Tử
        list.add(new ProdItem(5, "Tai Nghe Bluetooth Không Dây TWS Chống Ồn ANC", 480000, 65, "sp_dt_01.png",
                "Tai nghe True Wireless chuẩn Bluetooth 5.3, độ trễ cực thấp 45ms cho chơi game, chống ồn chủ động ANC sâu 35dB."));
        list.add(new ProdItem(5, "Loa Bluetooth Mini Âm Bass Trầm Chống Nước IPX7", 390000, 45, "sp_dt_02.png",
                "Loa không dây di động công suất 15W âm thanh vòm 360 độ, dải LED RGB nhảy theo nhịp nhạc, pin liên tục 12h."));
        list.add(new ProdItem(5, "Bàn Phím Cơ Không Dây 3 Chế Độ Kết Nối RGB", 850000, 35, "sp_dt_03.png",
                "Bàn phím cơ layout 75% gọn gàng, switch gõ êm mượt, hotswap đổi phím tiện lợi, đèn LED RGB 16.8 triệu màu."));
        list.add(new ProdItem(5, "Chuột Gaming Không Dây Siêu Nhẹ DPI 16000", 420000, 55, "sp_dt_04.png",
                "Chuột chơi game không dây trọng lượng siêu nhẹ 59g, cảm biến quang học chính xác cao, switch bấm 80 triệu lần."));
        list.add(new ProdItem(5, "Củ Sạc Nhanh GaN 65W 3 Cổng Type-C và USB", 360000, 80, "sp_dt_05.png",
                "Củ sạc công nghệ Gallium Nitride siêu nhỏ gọn, sạc nhanh cho Laptop Macbook, iPhone, Samsung cùng lúc."));
        list.add(new ProdItem(5, "Pin Sạc Dự Phòng 20000mAh Sạc Nhanh PD 22.5W", 320000, 70, "sp_dt_06.png",
                "Sạc dự phòng dung lượng chuẩn 20000mAh lõi polymer an toàn, màn hình LED hiển thị % pin chuẩn xác."));
        list.add(new ProdItem(5, "Đèn Bàn LED Chống Cận Thị Tích Hợp Sạc Không Dây", 290000, 40, "sp_dt_07.png",
                "Đèn học chống cận ánh sáng tự nhiên dịu mắt không gây chói, cảm ứng đổi 3 nhiệt độ màu và hẹn giờ tắt."));
        list.add(new ProdItem(5, "Camera Giám Sát Gia Đình Xoay 360 Độ Full HD 1080P", 490000, 50, "sp_dt_08.png",
                "Camera IP WiFi trong nhà hồng ngoại ban đêm rõ nét, phát hiện chuyển động thông minh gửi cảnh báo điện thoại."));
        list.add(new ProdItem(5, "Tay Cầm Chơi Game Không Dây Rung Kép Cho PC & Điện Thoại", 370000, 40, "sp_dt_09.png",
                "Gamepad đa nền tảng kết nối Bluetooth và 2.4G, cần gạt analog độ nhạy cao, mô tơ rung phản hồi chân thực."));
        list.add(new ProdItem(5, "Hub Chuyển Đổi Type-C 8 Trong 1 Ra HDMI 4K", 350000, 60, "sp_dt_10.png",
                "Bộ chia cổng USB-C vỏ nhôm tản nhiệt tốt, hỗ trợ xuất hình HDMI 4K 60Hz, 3 cổng USB 3.0 và khe đọc thẻ SD."));

        // Danh muc 6: Mỹ Phẩm & Làm Đẹp
        list.add(new ProdItem(6, "Kem Chống Nắng Nâng Tông Kiềm Dầu SPF50+ PA++++", 280000, 90, "sp_mp_01.png",
                "Kem chống nắng vật lý lai hóa học nâng tông tự nhiên sáng hồng, kiềm dầu 8 tiếng không gây nhờn dính bết rít."));
        list.add(new ProdItem(6, "Nước Tẩy Trang Dịu Nhẹ Không Cồn Cho Da Nhạy Cảm", 160000, 110, "sp_mp_02.png",
                "Micellar cleansing water làm sạch sâu cặn trang điểm và bụi mịn PM2.5, công thức pH 5.5 an toàn cho da mụn."));
        list.add(new ProdItem(6, "Serum Dưỡng Sáng Da Mờ Thâm Vitamin C & Niacinamide", 350000, 75, "sp_mp_03.png",
                "Tinh chất dưỡng da tái tạo bề mặt làm đều màu da, mờ thâm mụn chỉ sau 14 ngày sử dụng, chống oxy hóa mạnh."));
        list.add(new ProdItem(6, "Kem Dưỡng Ẩm Phục Hồi Da B5 Rau Má Làm Dịu Kích Ứng", 290000, 85, "sp_mp_04.png",
                "Kem dưỡng dạng gel cream mỏng nhẹ chứa Panthenol B5 và chiết xuất rau má Centella khóa ẩm phục hồi da treatment."));
        list.add(new ProdItem(6, "Sữa Rửa Mặt Tạo Bọt Chiết Xuất Tràm Trà Ngừa Mụn", 140000, 120, "sp_mp_05.png",
                "Sữa rửa mặt sạch sâu bã nhờn tinh chất tràm trà Tea Tree kháng viêm ngừa mụn, tạo bọt mịn màng không khô căng."));
        list.add(new ProdItem(6, "Son Kem Lì Kháng Nước Lâu Trôi Màu Đỏ Đất Thời Thượng", 190000, 130, "sp_mp_06.png",
                "Son môi kem lì velvet mềm mướt không lộ vân môi, độ bám màu lên đến 12h, sắc đỏ đất tôn da trắng răng."));
        list.add(new ProdItem(6, "Nước Hoa Nữ Hương Hoa Cỏ Dịu Ngọt Quyến Rũ 50ml", 650000, 40, "sp_mp_07.png",
                "Hương thơm nồng nàn thanh lịch từ hoa hồng Pháp kết hợp gỗ tuyết tùng và hổ phách, độ lưu hương trên 8 tiếng."));
        list.add(new ProdItem(6, "Mặt Nạ Dưỡng Da Cấp Ẩm Chuyên Sâu Hộp 10 Miếng", 180000, 95, "sp_mp_08.png",
                "Mặt nạ giấy sợi sinh học cellulose chứa hàm lượng Hyaluronic Acid gấp 3 lần, cấp nước tức thì cho làn da căng bóng."));
        list.add(new ProdItem(6, "Nước Hoa Hồng Toner Cân Bằng Độ Ẩm Se Khít Lỗ Chân Lông", 210000, 80, "sp_mp_09.png",
                "Toner hoa cúc Calendula làm dịu da tức thì, cân bằng độ pH chuẩn và se khít lỗ chân lông sau bước rửa mặt."));
        list.add(new ProdItem(6, "Phấn Nước Cushion Che Khuyết Điểm Kiềm Dầu Mịn Màng", 320000, 60, "sp_mp_10.png",
                "Phấn nước trang điểm mỏng nhẹ tạo hiệu ứng da phát sáng sương mai dewy, độ che phủ khuyết điểm hoàn hảo."));

        return list;
    }

    private static void generateProductImages(List<ProdItem> products) {
        File dir = new File("d:/LT_Web/BTVN_9-11/upload/product");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Color[] bgColors = {
            new Color(230, 242, 255), // Nam - xanh nhe
            new Color(255, 235, 240), // Nu - hong nhe
            new Color(255, 243, 224), // Giay - vang cam nhe
            new Color(243, 235, 255), // Dong ho - tim nhe
            new Color(230, 250, 240), // Dien tu - xanh ngoc nhe
            new Color(255, 230, 245)  // My pham - hong sen nhe
        };

        Color[] borderColors = {
            new Color(13, 110, 253),
            new Color(220, 53, 69),
            new Color(211, 84, 0),
            new Color(111, 66, 193),
            new Color(25, 135, 84),
            new Color(214, 51, 132)
        };

        String[] catLabels = {
            "QUẦN ÁO NAM", "QUẦN ÁO NỮ", "GIÀY DÉP THỜI TRANG",
            "ĐỒNG HỒ & PHỤ KIỆN", "THIẾT BỊ ĐIỆN TỬ", "MỸ PHẨM & LÀM ĐẸP"
        };

        DecimalFormat df = new DecimalFormat("#,###");

        for (ProdItem p : products) {
            int w = 500;
            int h = 500;
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int cIndex = p.cateId - 1;
            Color bg = bgColors[cIndex % bgColors.length];
            Color accent = borderColors[cIndex % borderColors.length];

            // Nen gradient
            GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, h, bg);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // Vien card
            g2.setColor(new Color(220, 225, 230));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(10, 10, w - 20, h - 20, 20, 20);

            // Badge Category
            g2.setColor(accent);
            g2.fillRoundRect(30, 30, 220, 36, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString(catLabels[cIndex], 45, 53);

            // Price badge
            String priceStr = df.format(p.price) + " đ";
            g2.setColor(new Color(220, 53, 69));
            g2.fillRoundRect(w - 180, 30, 150, 36, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.drawString(priceStr, w - 165, 54);

            // Center illustration box
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(50, 90, w - 100, 240, 24, 24);
            g2.setColor(new Color(230, 235, 240));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(50, 90, w - 100, 240, 24, 24);

            // Inner circle with category initial / icon
            g2.setColor(accent);
            g2.fillOval(w / 2 - 50, 140, 100, 100);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 42));
            String initial = "#" + p.cateId;
            FontMetrics fmInit = g2.getFontMetrics();
            int initX = (w - fmInit.stringWidth(initial)) / 2;
            g2.drawString(initial, initX, 205);

            // Product name text inside box
            g2.setColor(accent);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            String boxTag = "SHOPPING SERVICE - CHÍNH HÃNG";
            FontMetrics fmTag = g2.getFontMetrics();
            g2.drawString(boxTag, (w - fmTag.stringWidth(boxTag)) / 2, 280);

            // Product Title (Bottom area)
            g2.setColor(new Color(33, 37, 41));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fmTitle = g2.getFontMetrics();

            // Word wrap title into 2 lines if needed
            String[] words = p.name.split(" ");
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            for (String word : words) {
                if (fmTitle.stringWidth(line1 + " " + word) < w - 60 && line2.length() == 0) {
                    if (line1.length() > 0) line1.append(" ");
                    line1.append(word);
                } else {
                    if (line2.length() > 0) line2.append(" ");
                    line2.append(word);
                }
            }
            g2.drawString(line1.toString(), (w - fmTitle.stringWidth(line1.toString())) / 2, 380);
            if (line2.length() > 0) {
                g2.drawString(line2.toString(), (w - fmTitle.stringWidth(line2.toString())) / 2, 410);
            }

            // Bottom star rating & stock
            g2.setColor(new Color(255, 193, 7));
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            String stars = "★ ★ ★ ★ ★";
            FontMetrics fmStars = g2.getFontMetrics();
            g2.drawString(stars, (w - fmStars.stringWidth(stars)) / 2, 450);

            g2.setColor(new Color(108, 117, 125));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            String stockStr = "Kho: " + p.qty + " sản phẩm • Đã bán 200+";
            FontMetrics fmStock = g2.getFontMetrics();
            g2.drawString(stockStr, (w - fmStock.stringWidth(stockStr)) / 2, 475);

            g2.dispose();

            File outFile = new File(dir, p.image);
            try {
                ImageIO.write(img, "png", outFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
