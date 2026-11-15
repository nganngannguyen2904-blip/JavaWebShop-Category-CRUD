# BÀI TẬP VỀ NHÀ: LẬP TRÌNH WEB (WEBPR330479)

**Môn học:** Lập trình Web (`WEBPR330479`)  
**Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung  
**Trường:** Đại học Sư Phạm Kỹ Thuật TP. Hồ Chí Minh (HCMUTE)  
**Thời gian thực hiện:** Buổi học 09/11/2026  

---

## I. NỘI DUNG CÁC BÀI TẬP THỰC HÀNH

### 1. Bài tập 01: CRUD Category bằng JPA 3.0 & Hibernate ORM
- **Thư mục:** `BaiTap01_JPA_CRUD_Category/`
- **Công nghệ:** JPA 3.0 / Jakarta Persistence, Hibernate ORM, SQL Server (database: `webst2`), Servlet & JSP (không dùng Spring Boot theo đúng yêu cầu).
- **Nội dung thực hiện:**
  - Cấu hình file `src/main/resources/META-INF/persistence.xml` chuẩn với persistence-unit `jpa-hibernate-mysql`.
  - Ánh xạ Entity `Category` và `Video` hai chiều `@OneToMany` / `@ManyToOne`, `@NamedQuery(name="Category.findAll")`.
  - Quản lý transaction bằng `EntityManager` và `EntityTransaction` (`persist`, `merge`, `remove`, `find`, `createQuery`).
  - Thao tác CRUD đầy đủ: Thêm mới, Sửa, Xóa, Xem danh sách, Tìm kiếm, Phân trang và Upload ảnh.
  - Class kiểm thử JPA: `vn.iotstar.config.Test` và bộ test tự động 12 tiêu chí `vn.iotstar.test.JpaTest`.

### 2. Bài tập 02: Quản lý Danh mục, Sản phẩm & Xác thực Tài khoản
- **Thư mục:** `ShoppingServiceMVC/`
- **Công nghệ:** Servlet, JSP, JSTL, JDBC SQL Server (mô hình 3-tier MVC).
- **Nội dung thực hiện:**
  - Quản lý danh mục và sản phẩm hiển thị tiếng Việt UTF-8 chuẩn.
  - Chức năng đăng ký, đăng nhập và phân quyền người dùng.
  - Tích hợp chức năng Quên mật khẩu gửi mã OTP 6 chữ số thật qua Gmail SMTP (cấu hình bảo mật qua file `.env`, mật khẩu mã hóa BCrypt).

---

## II. HƯỚNG DẪN CHẠY BÀI TẬP

1. **Chạy bài tập JPA CRUD Category:**
   - Mở thư mục `BaiTap01_JPA_CRUD_Category/` và nhấp đúp file `run_server.bat`.
   - Trình duyệt tự động mở tại địa chỉ: `http://localhost:8080/BaiTap01_JPA_CRUD_Category/admin/categories`

2. **Chạy bài tập Servlet JDBC MVC:**
   - Nhấp đúp file `run_server.bat` tại thư mục gốc.
   - Trình duyệt tự động mở tại địa chỉ: `http://localhost:8080/ShoppingServiceMVC/home`
