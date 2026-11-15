-- ============================================================================
-- TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)
-- MÔN HỌC: LẬP TRÌNH WEB (WEBPR330479) - GVHD: ThS. NGUYỄN HỮU TRUNG
-- CƠ SỞ DỮ LIỆU: BanHang (SHOPPING SERVICE MVC)
-- BẢNG MÃ: UTF-8 / UNICODE NVARCHAR
-- ============================================================================

USE master;
GO

-- 1. Tạo Database BanHang nếu chưa có
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'BanHang')
BEGIN
    CREATE DATABASE BanHang COLLATE Vietnamese_CI_AS;
END
GO

USE BanHang;
GO

-- 2. Xóa các bảng cũ nếu đã tồn tại để tạo mới đồng bộ
IF OBJECT_ID('dbo.Product', 'U') IS NOT NULL DROP TABLE dbo.Product;
IF OBJECT_ID('dbo.Category', 'U') IS NOT NULL DROP TABLE dbo.Category;
IF OBJECT_ID('dbo.[User]', 'U') IS NOT NULL DROP TABLE dbo.[User];
GO

-- 3. Tạo bảng Category (Danh mục sản phẩm - Slide 14)
CREATE TABLE Category (
    cate_id INT IDENTITY(1,1) PRIMARY KEY,
    cate_name NVARCHAR(255) NOT NULL,
    icons NVARCHAR(255) NULL
);
GO

-- 4. Tạo bảng [User] (Tài khoản người dùng - Phân quyền, BCrypt, chống Brute-force)
CREATE TABLE [User] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(100) NOT NULL UNIQUE,
    username NVARCHAR(50) NOT NULL UNIQUE,
    fullname NVARCHAR(100) NOT NULL,
    password NVARCHAR(255) NOT NULL, -- Chuỗi băm BCrypt ($2a$10$...)
    avatar NVARCHAR(255) NULL DEFAULT 'avata.png',
    roleid INT NOT NULL DEFAULT 5, -- 1: Admin, 2: Manager, 5: User
    phone NVARCHAR(20) NULL,
    status INT NOT NULL DEFAULT 1, -- 1: Hoạt động, 0: Bị khóa
    failed_attempts INT NOT NULL DEFAULT 0, -- Số lần đăng nhập sai
    lock_time DATETIME NULL, -- Thời điểm bị khóa tạm thời 15 phút
    reset_token NVARCHAR(100) NULL, -- Token đặt lại mật khẩu
    reset_token_expiry DATETIME NULL, -- Thời hạn token (15 phút)
    createddate DATETIME DEFAULT GETDATE()
);
GO

-- 5. Tạo bảng Product (Sản phẩm thuộc danh mục)
CREATE TABLE Product (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    price DECIMAL(18,2) NOT NULL DEFAULT 0,
    image NVARCHAR(255) NULL,
    cate_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Product_Category FOREIGN KEY (cate_id) REFERENCES Category(cate_id) ON DELETE CASCADE
);
GO

-- 6. Chèn dữ liệu mẫu 6 Danh mục sản phẩm (Tiếng Việt có dấu chuẩn 100%)
SET IDENTITY_INSERT Category ON;
INSERT INTO Category (cate_id, cate_name, icons) VALUES
(1, N'Quần Áo Nam', N'cat_nam.png'),
(2, N'Quần Áo Nữ', N'cat_nu.png'),
(3, N'Giày Dép Thời Trang', N'cat_giay.png'),
(4, N'Đồng Hồ & Phụ Kiện', N'cat_dongho.png'),
(5, N'Thiết Bị Điện Tử', N'cat_dientu.png'),
(6, N'Mỹ Phẩm & Làm Đẹp', N'cat_mypham.png');
SET IDENTITY_INSERT Category OFF;
GO

-- 7. Chèn dữ liệu mẫu các tài khoản người dùng (Mật khẩu mặc định '123456' đã được băm an toàn bằng BCrypt)
INSERT INTO [User] (email, username, fullname, password, avatar, roleid, phone, status, failed_attempts) VALUES
('admin@shoppingservice.vn', 'admin', N'Quản Trị Viên Hệ Thống', '$2a$10$zWnXQo0VcNCShoOWkW6mKuue/CgIOEu4hXPuW06oOFbGbeecR8NZu', 'avata.png', 1, '0901234567', 1, 0),
('trungnh@hcmute.edu.vn', 'trungnh', N'ThS. Nguyễn Hữu Trung', '$2a$10$zWnXQo0VcNCShoOWkW6mKuue/CgIOEu4hXPuW06oOFbGbeecR8NZu', 'avata.png', 1, '0912345678', 1, 0),
('manager@shoppingservice.vn', 'manager', N'Quản Lý Cửa Hàng', '$2a$10$zWnXQo0VcNCShoOWkW6mKuue/CgIOEu4hXPuW06oOFbGbeecR8NZu', 'avata.png', 2, '0923456789', 1, 0),
('khachhang@gmail.com', 'khachhang', N'Nguyễn Văn A', '$2a$10$zWnXQo0VcNCShoOWkW6mKuue/CgIOEu4hXPuW06oOFbGbeecR8NZu', 'avata.png', 5, '0934567890', 1, 0);
GO

-- 8. Chèn 60 Sản phẩm thực tế phân bổ 6 danh mục (10 sản phẩm / danh mục)
-- Danh mục 1: Quần Áo Nam
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Áo Sơ Mi Nam Oxford Trắng Dài Tay Cao Cấp', N'Áo sơ mi nam chất liệu vải Oxford 100% cotton dày dặn, thấm hút mồ hôi tốt, chống nhăn, form dáng Regular Fit trẻ trung lịch lãm phù hợp đi làm và dự tiệc.', 280000, 'sp_nam_01.png', 1, 50, 1),
(N'Áo Thun Nam Cổ Tròn Cotton Compact 250gsm', N'Áo thun basic nam sử dụng sợi cotton compact chải kỹ, co giãn 4 chiều mềm mịn, bề mặt vải không xù lông sau nhiều lần giặt.', 150000, 'sp_nam_02.png', 1, 120, 1),
(N'Quần Jean Nam Slimfit Co Giãn Màu Xanh Xước', N'Quần jean nam dáng ôm nhẹ tôn dáng, chất liệu denim pha spandex co giãn thoải mái khi vận động cả ngày dài.', 350000, 'sp_nam_03.png', 1, 45, 1),
(N'Áo Khoác Gió Nam 2 Lớp Chống Nước Chống Gió', N'Áo khoác dù nam 2 lớp cao cấp, trượt nước hiệu quả, lót lưới thoáng khí, mũ tháo rời linh hoạt cho mùa mưa gió.', 320000, 'sp_nam_04.png', 1, 35, 1),
(N'Quần Tây Nam Công Sở Ống Đứng Chống Nhăn', N'Quần tây nam phong cách Hàn Quốc, đường ly sắc nét, chất liệu tuyết mưa cao cấp không bai xù, tôn dáng chuẩn công sở.', 310000, 'sp_nam_05.png', 1, 60, 1),
(N'Áo Polo Nam Phối Bo Cổ Dệt Họa Tiết Thể Thao', N'Áo thun có cổ polo nam dệt kim pique thoáng mát, logo thêu tinh tế trước ngực, mang đến phong cách năng động hiện đại.', 220000, 'sp_nam_06.png', 1, 80, 1),
(N'Áo Hoodie Nam Form Rộng Unisex Nỉ Bông Ấm Áp', N'Áo nỉ chui đầu có mũ rộng rãi, vải nỉ chân cua trần bông giữ ấm hoàn hảo cho những ngày se lạnh cuối năm.', 290000, 'sp_nam_07.png', 1, 40, 1),
(N'Quần Kaki Nam Túi Hộp Phong Cách Cargo Nam Tính', N'Quần túi hộp cargo pants nam chất kaki dày dặn, form ống suông bo gấu trẻ trung, phong cách đường phố cá tính.', 275000, 'sp_nam_08.png', 1, 30, 1),
(N'Áo Blazer Nam Form Suông Hiện Đại Nhẹ Nhàng', N'Áo vest blazer nam 1 nút dáng suông trẻ trung, dễ phối với áo thun và quần jean hoặc quần tây.', 550000, 'sp_nam_09.png', 1, 25, 1),
(N'Bộ Quần Áo Thể Thao Nam Thun Lạnh Thoáng Khí', N'Set đồ bộ thể thao nam chất liệu thun mè thể thao co giãn 4 chiều, nhanh khô, kháng khuẩn khi chạy bộ và tập gym.', 240000, 'sp_nam_10.png', 1, 70, 1);

-- Danh mục 2: Quần Áo Nữ
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Đầm Nữ Dáng Xòe Voan Hoa Nhí Vintage Dễ Thương', N'Đầm hoa nhí tay bồng nhẹ nhàng, chất liệu tơ voan 2 lớp mềm mại bay bổng, tôn vòng eo và vóc dáng thon gọn.', 320000, 'sp_nu_01.png', 2, 40, 1),
(N'Áo Kiểu Nữ Cổ Vuông Tay Phồng Tiểu Thư', N'Áo croptop nữ cổ vuông quyến rũ, chất kate lụa thoáng mát, thiết kế nhún chun sau lưng ôm khít vòng eo.', 190000, 'sp_nu_02.png', 2, 65, 1),
(N'Quần Ống Rộng Nữ Cạp Cao Hack Dáng Chuẩn', N'Quần suông ống rộng culottes cạp cao khóa trước, chất tuyết mưa rủ dáng mượt mà, kéo dài đôi chân thanh thoát.', 260000, 'sp_nu_03.png', 2, 90, 1),
(N'Chân Váy Chữ A Xếp Ly Ngắn Phong Cách Hàn Quốc', N'Chân váy tennis xếp ly đều đặn có kèm quần bảo hộ bên trong, năng động trẻ trung khi phối cùng sơ mi và áo thun.', 180000, 'sp_nu_04.png', 2, 85, 1),
(N'Áo Cardigan Len Nữ Dệt Kim Cúc Cài Nhẹ Nhàng', N'Áo khoác len cardigan mỏng nhẹ, họa tiết dệt kim tinh tế pastel ngọt ngào, phù hợp khoác nhẹ mùa thu đông.', 240000, 'sp_nu_05.png', 2, 55, 1),
(N'Set Đồ Nữ Công Sở Áo Vest Kèm Quần Tây Cao Cấp', N'Bộ vest nữ phong cách công sở sang trọng lịch thiệp, đường may tỉ mỉ, đệm vai đứng phom chuyên nghiệp.', 590000, 'sp_nu_06.png', 2, 30, 1),
(N'Áo Thun Nữ Oversize In Hình Nghệ Thuật', N'Áo phông nữ form rộng giấu quần cá tính, chất thun cotton 100% thoáng mát in lụa sắc nét không bong tróc.', 140000, 'sp_nu_07.png', 2, 110, 1),
(N'Đầm Body Nữ Cổ Yếm Ôm Dáng Quyến Rũ Dự Tiệc', N'Váy dạ hội ôm sát body cổ yếm gợi cảm, chất thun gân tăm co giãn ôm trọn đường cong quyến rũ cho nàng tiệc đêm.', 380000, 'sp_nu_08.png', 2, 35, 1),
(N'Áo Sơ Mi Lụa Nữ Cổ Đức Dáng Suông Nhẹ Nhàng', N'Áo sơ mi nữ công sở chất lụa satin ngọc trai mềm óng, không nhăn, tạo cảm giác quý phái thanh lịch nơi làm việc.', 230000, 'sp_nu_09.png', 2, 60, 1),
(N'Quần Jean Nữ Ống Loe Cạp Cao Phong Cách Cổ Điển', N'Quần bò loe nữ lưng cao nâng mông thon đùi, màu xanh denim cổ điển chất bò co giãn mạnh mẽ.', 340000, 'sp_nu_10.png', 2, 45, 1);

-- Danh mục 3: Giày Dép Thời Trang
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Giày Sneaker Nam Nữ Trắng Cổ Thấp Đế Độn 4cm', N'Giày sneaker thể thao phong cách đường phố, da PU chống bám bẩn, đế cao su non êm ái tăng chiều cao 4cm.', 390000, 'sp_giay_01.png', 3, 60, 1),
(N'Giày Lười Nam Da Bò Thật Khâu Đế Thủ Công', N'Giày mọi lười loafer nam chất da bò nguyên tấm êm ái, đế đúc cao su nguyên khối bám đường chống trơn trượt.', 580000, 'sp_giay_02.png', 3, 30, 1),
(N'Giày Cao Gót Nữ Mũi Nhọn 7cm Sang Trọng', N'Giày cao gót nữ gót nhọn thanh mảnh bọc da mờ cao cấp, lót mút êm chân không đau mỏi khi đứng lâu.', 360000, 'sp_giay_03.png', 3, 45, 1),
(N'Giày Chạy Bộ Running Siêu Nhẹ Thoáng Khí', N'Giày chạy bộ thể thao đệm khí Air Zoom êm ái, thân giày dệt flyknit thông thoáng giảm sốc tối ưu khi luyện tập.', 450000, 'sp_giay_04.png', 3, 55, 1),
(N'Dép Quai Ngang Unisex Đúc Nguyên Khối Chống Trượt', N'Dép bánh mì quai ngang chất liệu EVA siêu nhẹ chống nước chống trơn trợt cực tốt, êm chân cho đi học đi chơi.', 120000, 'sp_giay_05.png', 3, 150, 1),
(N'Sandal Nữ Quai Mảnh Đế Bệt Đi Học Dạo Phố', N'Giày sandal nữ dây quai mảnh khóa gài ôm cổ chân, đế cao su chống trượt tạo phong cách nhẹ nhàng trẻ trung.', 190000, 'sp_giay_06.png', 3, 70, 1),
(N'Giày Boot Nữ Cổ Cao Da Lì Khóa Kéo Thời Trang', N'Boot cổ lửng nữ đế vuông 5cm dễ đi, da lì chống trầy xước, phong cách sành điệu cá tính cho mùa đông.', 420000, 'sp_giay_07.png', 3, 35, 1),
(N'Giày Oxford Nam Da Bóng Phong Cách Lịch Lãm', N'Giày tây nam buộc dây Oxford đế gỗ sang trọng, da bóng cao cấp chuẩn phong cách quý ông tiệc cưới sự kiện.', 620000, 'sp_giay_08.png', 3, 25, 1),
(N'Sandal Nam Quai Dù Đi Mưa Dã Ngoại Bền Bỉ', N'Sandal quai hậu nam dù dệt may chắc chắn, đế đúc gợn sóng bám đất cực tốt khi leo núi và đi phượt.', 230000, 'sp_giay_09.png', 3, 80, 1),
(N'Giày Búp Bê Nữ Đính Nơ Đế Bệt Mềm Mại', N'Giày búp bê nữ da microfiber mềm không đau gót, đính nơ kim loại xinh xắn phối đầm váy duyên dáng.', 210000, 'sp_giay_10.png', 3, 65, 1);

-- Danh mục 4: Đồng Hồ & Phụ Kiện
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Đồng Hồ Nam Dây Thép Không Gỉ Chống Nước 5ATM', N'Đồng hồ nam máy quartz Nhật Bản kính sapphire chống xước, vỏ và dây thép không gỉ 316L mạ PVD bền màu.', 750000, 'sp_dh_01.png', 4, 30, 1),
(N'Đồng Hồ Nữ Dây Da Mặt Tròn Đính Đá Tinh Xảo', N'Đồng hồ nữ mặt số xà cừ đính viền đá lấp lánh, dây da bò mềm mại ôm cổ tay xinh xắn của quý cô.', 520000, 'sp_dh_02.png', 4, 40, 1),
(N'Đồng Hồ Thông Minh Smartwatch Đo Nhịp Tim Huyết Áp', N'Smartwatch màn hình AMOLED tràn viền, hỗ trợ nghe gọi Bluetooth, theo dõi 100+ chế độ thể thao pin 10 ngày.', 890000, 'sp_dh_03.png', 4, 50, 1),
(N'Kính Mát Unisex Mắt Kính Phân Cực Polarized Chống UV400', N'Kính râm phi công gọng kim loại titan siêu nhẹ, tròng kính polarized chống chói lóa và cản 100% tia tử ngoại UV.', 250000, 'sp_dh_04.png', 4, 90, 1),
(N'Ví Nam Da Bò Thật Dáng Ngang Nhiều Ngăn Đựng Tiền', N'Ví bóp nam da bò sáp tự nhiên may tay tỉ mỉ, có ngăn khóa kéo an toàn và nhiều khe cắm thẻ ngân hàng.', 290000, 'sp_dh_05.png', 4, 75, 1),
(N'Thắt Lưng Nam Da Khóa Tự Động Hợp Kim Cao Cấp', N'Dây nịt nam da bò 2 lớp dẻo dai, mặt khóa tự động trượt êm không lo đục lỗ, phù hợp quần jean và quần tây.', 220000, 'sp_dh_06.png', 4, 85, 1),
(N'Vòng Tay Bạc Nữ Ý 925 Đính Đá Pha Lê May Mắn', N'Lắc tay nữ bạc S925 chuẩn kiểm định, mặt hoa đính đá lấp lánh có chuỗi điều chỉnh kích cỡ cổ tay.', 340000, 'sp_dh_07.png', 4, 60, 1),
(N'Balo Laptop Chống Nước Có Cổng Sạc USB Tiện Lợi', N'Balo công sở đựng laptop 15.6 inch vải Oxford chống thấm trượt nước, quai đeo đệm xốp thoáng khí trợ lực.', 380000, 'sp_dh_08.png', 4, 45, 1),
(N'Nón Lưỡi Trai Unisex Vải Kaki Thêu Chữ Nổi', N'Mũ lưỡi trai phong cách nón kết cá tính, khóa kim loại điều chỉnh độ rộng chóp đầu chuẩn phom cứng cáp.', 110000, 'sp_dh_09.png', 4, 120, 1),
(N'Túi Xách Nữ Đeo Chéo Phối Dây Xích Sang Chảnh', N'Túi đeo chéo nữ trần trám nắp gập khóa vặn kim loại mạ vàng, bên trong có 3 ngăn rộng rãi tiện dụng.', 330000, 'sp_dh_10.png', 4, 55, 1);

-- Danh mục 5: Thiết Bị Điện Tử
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Tai Nghe Bluetooth Không Dây TWS Chống Ồn ANC', N'Tai nghe True Wireless chuẩn Bluetooth 5.3, độ trễ cực thấp 45ms cho chơi game, chống ồn chủ động ANC sâu 35dB.', 480000, 'sp_dt_01.png', 5, 65, 1),
(N'Loa Bluetooth Mini Âm Bass Trầm Chống Nước IPX7', N'Loa không dây di động công suất 15W âm thanh vòm 360 độ, dải LED RGB nhảy theo nhịp nhạc, pin liên tục 12h.', 390000, 'sp_dt_02.png', 5, 45, 1),
(N'Bàn Phím Cơ Không Dây 3 Chế Độ Kết Nối RGB', N'Bàn phím cơ layout 75% gọn gàng, switch gõ êm mượt, hotswap đổi phím tiện lợi, đèn LED RGB 16.8 triệu màu.', 850000, 'sp_dt_03.png', 5, 35, 1),
(N'Chuột Gaming Không Dây Siêu Nhẹ DPI 16000', N'Chuột chơi game không dây trọng lượng siêu nhẹ 59g, cảm biến quang học chính xác cao, switch bấm 80 triệu lần.', 420000, 'sp_dt_04.png', 5, 55, 1),
(N'Củ Sạc Nhanh GaN 65W 3 Cổng Type-C và USB', N'Củ sạc công nghệ Gallium Nitride siêu nhỏ gọn, sạc nhanh cho Laptop Macbook, iPhone, Samsung cùng lúc.', 360000, 'sp_dt_05.png', 5, 80, 1),
(N'Pin Sạc Dự Phòng 20000mAh Sạc Nhanh PD 22.5W', N'Sạc dự phòng dung lượng chuẩn 20000mAh lõi polymer an toàn, màn hình LED hiển thị % pin chuẩn xác.', 320000, 'sp_dt_06.png', 5, 70, 1),
(N'Đèn Bàn LED Chống Cận Thị Tích Hợp Sạc Không Dây', N'Đèn học chống cận ánh sáng tự nhiên dịu mắt không gây chói, cảm ứng đổi 3 nhiệt độ màu và hẹn giờ tắt.', 290000, 'sp_dt_07.png', 5, 40, 1),
(N'Camera Giám Sát Gia Đình Xoay 360 Độ Full HD 1080P', N'Camera IP WiFi trong nhà hồng ngoại ban đêm rõ nét, phát hiện chuyển động thông minh gửi cảnh báo điện thoại.', 490000, 'sp_dt_08.png', 5, 50, 1),
(N'Tay Cầm Chơi Game Không Dây Rung Kép Cho PC & Điện Thoại', N'Gamepad đa nền tảng kết nối Bluetooth và 2.4G, cần gạt analog độ nhạy cao, mô tơ rung phản hồi chân thực.', 370000, 'sp_dt_09.png', 5, 40, 1),
(N'Hub Chuyển Đổi Type-C 8 Trong 1 Ra HDMI 4K', N'Bộ chia cổng USB-C vỏ nhôm tản nhiệt tốt, hỗ trợ xuất hình HDMI 4K 60Hz, 3 cổng USB 3.0 và khe đọc thẻ SD.', 350000, 'sp_dt_10.png', 5, 60, 1);

-- Danh mục 6: Mỹ Phẩm & Làm Đẹp
INSERT INTO Product (product_name, description, price, image, cate_id, quantity, status) VALUES
(N'Kem Chống Nắng Nâng Tông Kiềm Dầu SPF50+ PA++++', N'Kem chống nắng vật lý lai hóa học nâng tông tự nhiên sáng hồng, kiềm dầu 8 tiếng không gây nhờn dính bết rít.', 280000, 'sp_mp_01.png', 6, 90, 1),
(N'Nước Tẩy Trang Dịu Nhẹ Không Cồn Cho Da Nhạy Cảm', N'Micellar cleansing water làm sạch sâu cặn trang điểm và bụi mịn PM2.5, công thức pH 5.5 an toàn cho da mụn.', 160000, 'sp_mp_02.png', 6, 110, 1),
(N'Serum Dưỡng Sáng Da Mờ Thâm Vitamin C & Niacinamide', N'Tinh chất dưỡng da tái tạo bề mặt làm đều màu da, mờ thâm mụn chỉ sau 14 ngày sử dụng, chống oxy hóa mạnh.', 350000, 'sp_mp_03.png', 6, 75, 1),
(N'Kem Dưỡng Ẩm Phục Hồi Da B5 Rau Má Làm Dịu Kích Ứng', N'Kem dưỡng dạng gel cream mỏng nhẹ chứa Panthenol B5 và chiết xuất rau má Centella khóa ẩm phục hồi da treatment.', 290000, 'sp_mp_04.png', 6, 85, 1),
(N'Sữa Rửa Mặt Tạo Bọt Chiết Xuất Tràm Trà Ngừa Mụn', N'Sữa rửa mặt sạch sâu bã nhờn tinh chất tràm trà Tea Tree kháng viêm ngừa mụn, tạo bọt mịn màng không khô căng.', 140000, 'sp_mp_05.png', 6, 120, 1),
(N'Son Kem Lì Kháng Nước Lâu Trôi Màu Đỏ Đất Thời Thượng', N'Son môi kem lì velvet mềm mướt không lộ vân môi, độ bám màu lên đến 12h, sắc đỏ đất tôn da trắng răng.', 190000, 'sp_mp_06.png', 6, 130, 1),
(N'Nước Hoa Nữ Hương Hoa Cỏ Dịu Ngọt Quyến Rũ 50ml', N'Hương thơm nồng nàn thanh lịch từ hoa hồng Pháp kết hợp gỗ tuyết tùng và hổ phách, độ lưu hương trên 8 tiếng.', 650000, 'sp_mp_07.png', 6, 40, 1),
(N'Mặt Nạ Dưỡng Da Cấp Ẩm Chuyên Sâu Hộp 10 Miếng', N'Mặt nạ giấy sợi sinh học cellulose chứa hàm lượng Hyaluronic Acid gấp 3 lần, cấp nước tức thì cho làn da căng bóng.', 180000, 'sp_mp_08.png', 6, 95, 1),
(N'Nước Hoa Hồng Toner Cân Bằng Độ Ẩm Se Khít Lỗ Chân Lông', N'Toner hoa cúc Calendula làm dịu da tức thì, cân bằng độ pH chuẩn và se khít lỗ chân lông sau bước rửa mặt.', 210000, 'sp_mp_09.png', 6, 80, 1),
(N'Phấn Nước Cushion Che Khuyết Điểm Kiềm Dầu Mịn Màng', N'Phấn nước trang điểm mỏng nhẹ tạo hiệu ứng da phát sáng sương mai dewy, độ che phủ khuyết điểm hoàn hảo.', 320000, 'sp_mp_10.png', 6, 60, 1);
GO

PRINT N'=== KHỞI TẠO CƠ SỞ DỮ LIỆU BANHANG VÀ 60 SẢN PHẨM HOÀN TẤT THÀNH CÔNG ===';
GO
