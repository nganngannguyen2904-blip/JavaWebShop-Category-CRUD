-- =======================================================
-- SCRIPT KHỞI TẠO CƠ SỞ DỮ LIỆU ĐỒ ÁN LẬP TRÌNH WEB
-- Giảng viên: ThS. Nguyễn Hữu Trung (HCMUTE)
-- Đề tài: Shopping Service MVC (Servlet - JDBC - 3 Tier Architecture)
-- =======================================================

USE master;
GO

-- 1. Tạo database BanHang (nếu chưa có)
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'BanHang')
BEGIN
    CREATE DATABASE BanHang;
END
GO

USE BanHang;
GO

-- 2. Xóa bảng cũ nếu tồn tại (để reset dữ liệu sạch sẽ)
IF OBJECT_ID(N'dbo.Category', N'U') IS NOT NULL
    DROP TABLE dbo.Category;
GO

IF OBJECT_ID(N'dbo.[User]', N'U') IS NOT NULL
    DROP TABLE dbo.[User];
GO

-- 3. Tạo bảng Category (Theo tài liệu File 1: 14_HD_Servlet_JDBC_CRUD.pdf)
CREATE TABLE Category (
    [cate_id] [int] IDENTITY(1,1) NOT NULL,
    [cate_name] [nvarchar](255) NOT NULL,
    [icons] [nvarchar](255) NULL,
    CONSTRAINT PK_Category PRIMARY KEY CLUSTERED ([cate_id] ASC)
);
GO

-- 4. Tạo bảng [User] (Theo tài liệu File 2: 06_MVC_3tier.pdf)
CREATE TABLE [User] (
    [id] [int] IDENTITY(1,1) NOT NULL,
    [email] [nvarchar](255) NOT NULL,
    [username] [nvarchar](50) NOT NULL UNIQUE,
    [fullname] [nvarchar](255) NOT NULL,
    [password] [nvarchar](255) NOT NULL,
    [avatar] [nvarchar](255) NULL,
    [roleid] [int] NOT NULL DEFAULT 5, -- 1: Admin, 2: Manager, 5: User
    [phone] [nvarchar](20) NULL,
    [createddate] [date] NULL,
    CONSTRAINT PK_User PRIMARY KEY CLUSTERED ([id] ASC)
);
GO

-- 5. Chèn dữ liệu mẫu cho bảng [User]
-- Tài khoản quản trị và người dùng thử nghiệm
INSERT INTO [User] (email, username, fullname, password, avatar, roleid, phone, createddate)
VALUES 
    (N'admin@shopping.iotstar.vn', N'admin', N'Quản Trị Viên Hệ Thống', N'123456', N'admin-avatar.png', 1, N'0908888888', GETDATE()),
    (N'manager@shopping.iotstar.vn', N'manager', N'Quản Lý Cửa Hàng', N'123456', N'manager-avatar.png', 2, N'0907777777', GETDATE()),
    (N'trungnh@hcmute.edu.vn', N'trungnh', N'ThS. Nguyễn Hữu Trung', N'123456', N'teacher.png', 1, N'0908617108', GETDATE()),
    (N'user@gmail.com', N'khachhang', N'Nguyễn Văn A', N'123456', N'user-avatar.png', 5, N'0901234567', GETDATE());
GO

-- 6. Chèn dữ liệu mẫu cho bảng Category (Theo slide mẫu: Quần Áo Nam, Quần Áo Nữ,...)
INSERT INTO Category (cate_name, icons)
VALUES 
    (N'Quần Áo Nam', N'ao-nam.png'),
    (N'Quần Áo Nữ', N'ao-nu.png'),
    (N'Giày Dép Thời Trang', N'giay-dep.png'),
    (N'Đồng Hồ & Phụ Kiện', N'dong-ho.png'),
    (N'Thiết Bị Điện Tử', N'dien-tu.png'),
    (N'Mỹ Phẩm & Làm Đẹp', N'my-pham.png');
GO

-- 7. Kiểm tra dữ liệu sau khi khởi tạo
SELECT * FROM [User];
SELECT * FROM Category;
GO
