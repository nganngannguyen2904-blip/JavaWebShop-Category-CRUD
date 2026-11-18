-- =====================================================================
-- BÀI TẬP 02: BỔ SUNG LIÊN KẾT GIỮA BẢNG Videos VÀ BẢNG Product
-- Cập nhật ngày: 18/11/2026
-- Mô tả: Thêm cột ProductId vào bảng Videos và thiết lập khóa ngoại
--        tham chiếu đến bảng Product (product_id).
-- =====================================================================

USE BanHang;
GO

-- 1. Bổ sung cột ProductId nếu chưa tồn tại
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'dbo.Videos') AND name = N'ProductId'
)
BEGIN
    ALTER TABLE dbo.Videos ADD ProductId INT NULL;
    PRINT N'[OK] Đã bổ sung cột ProductId vào bảng Videos thành công.';
END
ELSE
BEGIN
    PRINT N'[INFO] Cột ProductId đã tồn tại trong bảng Videos.';
END
GO

-- 2. Bổ sung ràng buộc khóa ngoại FK_Videos_Product nếu chưa tồn tại
IF NOT EXISTS (
    SELECT 1 FROM sys.foreign_keys 
    WHERE object_id = OBJECT_ID(N'dbo.FK_Videos_Product')
)
BEGIN
    ALTER TABLE dbo.Videos ADD CONSTRAINT FK_Videos_Product 
        FOREIGN KEY (ProductId) REFERENCES dbo.Product(product_id)
        ON DELETE SET NULL 
        ON UPDATE CASCADE;
    PRINT N'[OK] Đã tạo ràng buộc khóa ngoại FK_Videos_Product thành công.';
END
ELSE
BEGIN
    PRINT N'[INFO] Khóa ngoại FK_Videos_Product đã tồn tại.';
END
GO

-- 3. Kiểm tra liên kết bằng truy vấn JOIN mẫu
SELECT TOP 10 
    v.VideoId, 
    v.Title, 
    v.Poster, 
    v.ProductId, 
    p.product_name, 
    p.price AS product_price, 
    c.cate_name AS category_name
FROM dbo.Videos v
LEFT JOIN dbo.Product p ON v.ProductId = p.product_id
LEFT JOIN dbo.Category c ON p.cate_id = c.cate_id
ORDER BY v.UploadDate DESC;
GO
