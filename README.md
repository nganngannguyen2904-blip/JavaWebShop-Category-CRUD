# BÀI TẬP VỀ NHÀ: LẬP TRÌNH WEB (WEBPR330479)

**Môn học:** Lập trình Web (`WEBPR330479`)  
**Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung  
**Trường:** Đại học Sư Phạm Kỹ Thuật TP. Hồ Chí Minh (HCMUTE)  
**Thời gian thực hiện:** Buổi học 09/11/2026  

---

## I. TỔNG QUAN BÀI TẬP

Bài tập được xây dựng dựa trên sự phân tích và đối chiếu toàn diện từ 2 tài liệu chuẩn của giảng viên:
1. **Tài liệu 1 (`14_HD_Servlet_JDBC_CRUD.pdf`):** Hướng dẫn xây dựng chức năng CRUD (Xem, Thêm, Sửa, Xóa, Tìm kiếm) cho bảng `Category` (Danh mục) sử dụng Servlet, JDBC, Upload file đa phương tiện (`@MultipartConfig`), và Servlet download ảnh (`/image?fname=...`).
2. **Tài liệu 2 (`06_MVC_3tier.pdf`):** Kiến trúc 3 tầng (Three Tier Architecture) và mô hình MVC trong Java; thực hiện các chức năng xác thực người dùng: Đăng nhập (`LoginController`), Đăng ký (`RegisterController`), Ghi nhớ đăng nhập (`Cookie`), Điều hướng phân quyền (`WaitingController`), Đăng xuất (`LogoutController`), và Quản lý phiên (`Session`) trên thanh điều hướng (`topbar.jsp`).
3. **Cải tiến & Hoàn thiện thực tế:**
   - **Hiển thị tiếng Việt 100% chuẩn UTF-8:** Toàn bộ cơ sở dữ liệu và giao diện sử dụng bảng mã UTF-8 cùng font chữ chuyên dụng `Be Vietnam Pro` sắc nét, không bị lỗi font hay ký tự lạ.
   - **Kho dữ liệu sản phẩm phong phú:** 60 sản phẩm thực tế (10 sản phẩm/danh mục) trải dài 6 ngành hàng với giá tiền VND, số lượng tồn kho, hình ảnh và mô tả chi tiết.
   - **Tính năng mua sắm hoàn chỉnh:** Lọc sản phẩm theo danh mục (`/products?cateid=...`), tìm kiếm sản phẩm theo từ khóa (`/products?keyword=...`), sắp xếp giá, xem chi tiết sản phẩm (`/product/detail?id=...`), xem sản phẩm tương tự cùng danh mục, thêm vào giỏ hàng và mua ngay.
   - **Tích hợp thêm module Bài tập JPA:** Thư mục `BaiTap01_JPA_CRUD_Category/` thực hiện bài tập CRUD Category bằng JPA 3.0 & Hibernate ORM độc lập.

---

## II. CẤU TRÚC BÀI TẬP

```text
D:\LT_Web\BTVN_9-11\
├── build_project.bat               # File chạy nhanh đóng gói Maven WAR (mvn clean package)
├── run_server.bat                  # File chạy nhanh đồng bộ & khởi động Tomcat 11
├── Hướng dẫn đăng nhập.docx        # Tài liệu Word hướng dẫn sử dụng và kiểm thử
├── Huong_Dan_Dang_Nhap.docx        # Bản sao lưu tài liệu Word
├── upload\                         # Thư mục lưu trữ hình ảnh
│   ├── category\                   # Ảnh đại diện 6 danh mục sản phẩm
│   └── product\                    # Ảnh 60 sản phẩm thực tế (.png)
│
├── BaiTap01_JPA_CRUD_Category\     # Bài tập CRUD Category bằng JPA 3.0 & Hibernate ORM
│   ├── pom.xml                     # Cấu hình Hibernate 6.6, Jakarta Persistence 3.1
│   ├── run_server.bat              # Script khởi chạy độc lập bài tập JPA
│   └── src\
│
└── ShoppingServiceMVC\             # Mã nguồn bài tập Maven Java Web MVC
    ├── pom.xml                     # Cấu hình Maven (Jakarta Servlet 6.1, JSTL, MSSQL JDBC)
    └── src\
        └── main\
            ├── java\vn\iotstar\
            │   ├── connection\
            │   │   └── DBConnection.java          # Kết nối CSDL SQL Server (Windows Auth & UTF-8)
            │   ├── model\
            │   │   ├── User.java                  # Model người dùng [User]
            │   │   ├── Category.java              # Model danh mục [Category]
            │   │   └── Product.java               # Model sản phẩm [Product]
            │   ├── dao\
            │   │   ├── UserDao.java / UserDaoImpl.java
            │   │   ├── CategoryDao.java / CategoryDaoImpl.java
            │   │   └── ProductDao.java / ProductDaoImpl.java
            │   ├── service\
            │   │   ├── UserService.java / UserServiceImpl.java
            │   │   ├── CategoryService.java / CategoryServiceImpl.java
            │   │   └── ProductService.java / ProductServiceImpl.java
            │   ├── controller\
            │   │   ├── auth\ (LoginController, RegisterController, LogoutController, ForgotPasswordController)
            │   │   ├── common\ (HomeController, WaitingController, DownloadImageController)
            │   │   ├── web\ (ProductListController, ProductDetailController)
            │   │   └── admin\ (AdminHomeController, Category CRUD Controllers)
            │   └── util\
            │       ├── Constant.java              # Đường dẫn upload, vai trò, cookie/session
            │       └── CharacterEncodingFilter.java # Bộ lọc mã hóa UTF-8 toàn cục
            └── webapp\
                ├── WEB-INF\web.xml                # Descriptor Jakarta EE 6.0
                └── views\
                    ├── auth\ (login.jsp, register.jsp, forgot-password.jsp)
                    ├── common\ (home.jsp, topbar.jsp)
                    ├── web\ (product-list.jsp, product-detail.jsp)
                    └── admin\ (home.jsp, list-category.jsp, add-category.jsp, edit-category.jsp)
```

---

## III. TÀI KHOẢN ĐĂNG NHẬP MẶC ĐỊNH

| Tên đăng nhập | Mật khẩu | Quyền (Role) | Chức năng truy cập |
|---|---|---|---|
| `admin` | `123456` | Role 1 (Admin) | Toàn quyền Quản trị & CRUD Danh mục (`/admin/*`) |
| `trungnh` | `123456` | Role 1 (GVHD) | Toàn quyền Quản trị & CRUD Danh mục |
| `manager` | `123456` | Role 2 (Manager) | Quản lý danh mục sản phẩm |
| `khachhang` | `123456` | Role 5 (User) | Giao diện mua sắm trực tuyến (`/home`, `/products`) |

---

## IV. HƯỚNG DẪN CHẠY VÀ KIỂM THỬ HỆ THỐNG

### 1. Khởi động nhanh bằng 1 click:
Nhấp đúp chuột vào file:
```text
run_server.bat
```
File này sẽ tự động:
1. Đồng bộ file `.war` mới nhất vào Apache Tomcat 11.
2. Kiểm tra thư viện DLL Windows Auth (`mssql-jdbc_auth-12.8.1.x64.dll`).
3. Khởi động Tomcat 11 và tự động mở trình duyệt web tại địa chỉ:
   ```text
   http://localhost:8080/ShoppingServiceMVC/home
   ```

*(Nếu muốn chạy riêng bài tập JPA Category, mở thư mục `BaiTap01_JPA_CRUD_Category` và chạy `run_server.bat` bên trong).*

### 2. Các kịch bản kiểm thử:
1. **Kiểm tra hiển thị tiếng Việt:**
   - Mở trang chủ: Kiểm tra tên 6 danh mục (`Quần Áo Nam`, `Quần Áo Nữ`, `Giày Dép Thời Trang`, `Đồng Hồ & Phụ Kiện`, `Thiết Bị Điện Tử`, `Mỹ Phẩm & Làm Đẹp`). Tất cả hiển thị tiếng Việt chuẩn 100%, không bị lỗi bảng mã.
2. **Kiểm tra lọc sản phẩm theo danh mục:**
   - Tại trang chủ, bấm **"Xem sản phẩm"** ở bất kỳ danh mục nào. Hệ thống chuyển tới `/products?cateid=X` hiển thị 10 sản phẩm chuẩn xác của danh mục đó.
3. **Kiểm tra xem chi tiết sản phẩm:**
   - Bấm vào bất kỳ thẻ sản phẩm nào. Hệ thống mở `/product/detail?id=X` hiển thị ảnh lớn, giá tiền, số lượng trong kho, mô tả chi tiết, chính sách giao hàng và danh sách sản phẩm tương tự cùng danh mục.
4. **Kiểm tra tìm kiếm & sắp xếp:**
   - Nhập từ khóa (ví dụ: `sơ mi`, `giày`, `bluetooth`) vào thanh tìm kiếm rồi nhấn Enter.
   - Chọn bộ lọc sắp xếp giá "Thấp đến Cao" hoặc "Cao đến Thấp".
5. **Kiểm tra quản trị danh mục (Admin):**
   - Đăng nhập với tài khoản `admin` / `123456`.
   - Bấm "Quản trị Danh mục" để thực hiện Xem, Thêm danh mục mới (có tải ảnh đại diện), Sửa danh mục và Xóa danh mục.
