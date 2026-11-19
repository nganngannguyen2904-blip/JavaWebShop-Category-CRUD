-- =====================================================================
-- BÀI TẬP VỀ NHÀ: NÂNG CẤP BẢNG [User] LƯU TRỮ URL CLOUDINARY
-- Cập nhật ngày: 19/11/2026
-- Mô tả: Mở rộng kích thước cột avatar từ NVARCHAR(255) lên NVARCHAR(500)
--        để lưu trữ trọn vẹn URL ảnh đại diện đám mây Cloudinary
-- =====================================================================

USE BanHang;
GO

-- Kiểm tra và mở rộng độ dài cột avatar
IF EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'dbo.[User]') AND name = N'avatar'
)
BEGIN
    ALTER TABLE dbo.[User] ALTER COLUMN avatar NVARCHAR(500) NULL;
    PRINT N'[OK] Đã mở rộng độ dài cột avatar thành NVARCHAR(500) thành công.';
END
ELSE
BEGIN
    ALTER TABLE dbo.[User] ADD avatar NVARCHAR(500) NULL DEFAULT 'avata.png';
    PRINT N'[OK] Đã tạo mới cột avatar NVARCHAR(500) trong bảng [User].';
END
GO

-- Kiểm tra dữ liệu mẫu của bảng [User]
SELECT TOP 5 id, username, fullname, email, phone, avatar, roleid, status
FROM dbo.[User];
GO
