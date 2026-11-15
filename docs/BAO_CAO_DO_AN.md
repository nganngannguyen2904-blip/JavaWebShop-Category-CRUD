# BÁO CÁO ĐỒ ÁN CUỐI KỲ
# MÔN HỌC: LẬP TRÌNH WEB (MÃ HỌC PHẦN: WEBPR330479)

**ĐỀ TÀI:** XÂY DỰNG ỨNG DỤNG SHOPPING SERVICE MVC VỚI SERVLET, JDBC VÀ KIẾN TRÚC 3 TẦNG  
**GIẢNG VIÊN HƯỚNG DẪN:** ThS. NGUYỄN HỮU TRUNG  
**KHOA:** CÔNG NGHỆ THÔNG TIN - BỘ MÔN CÔNG NGHỆ PHẦN MỀM  
**TRƯỜNG:** ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)  

---

## MỤC LỤC
1. [Giới thiệu đề tài & Mục tiêu](#1-giới-thiệu-đề-tài--mục-tiêu)
2. [Cơ sở lý thuyết](#2-cơ-sở-lý-thuyết)
   - 2.1. Kiến trúc 3 tầng (Three-Tier Architecture)
   - 2.2. Mô hình MVC (Model - View - Controller)
   - 2.3. Công nghệ sử dụng
3. [Thiết kế Cơ sở dữ liệu](#3-thiết-kế-cơ-sở-dữ-liệu)
   - 3.1. Bảng Category (Danh mục sản phẩm)
   - 3.2. Bảng User (Tài khoản người dùng)
4. [Thiết kế và Hiện thực phần mềm](#4-thiết-kế-và-hiện-thực-phần-mềm)
   - 4.1. Tầng Data Access (DAO Layer)
   - 4.2. Tầng Business Logic (Service Layer)
   - 4.3. Tầng Presentation (Controller & View Layer)
5. [Kết quả thực hiện & Kiểm thử chức năng](#5-kết-quả-thực-hiện--kiểm-thử-chức-năng)
   - 5.1. Chức năng Đăng nhập & Remember Me
   - 5.2. Chức năng Đăng ký tài khoản
   - 5.3. Chức năng Điều hướng phân quyền (Waiting Controller)
   - 5.4. Chức năng Quản lý Danh mục (CRUD Category)
   - 5.5. Phục vụ hình ảnh đa phương tiện (DownloadImageController)
6. [Kết luận & Đánh giá](#6-kết-luận--đánh-giá)

---

## 1. GIỚI THIỆU ĐỀ TÀI & MỤC TIÊU

Trong khuôn khổ học phần **Lập Trình Web (WEBPR330479)**, đề tài **Shopping Service MVC** được xây dựng nhằm hiện thực hóa các kiến thức nền tảng và cốt lõi nhất của lập trình web trên nền tảng Java:
- Áp dụng triệt để nguyên lý phân tách trách nhiệm (Separation of Concerns) thông qua **Kiến trúc 3 tầng (Three-Tier Architecture)**.
- Xây dựng hệ thống điều khiển luồng dữ liệu theo đúng chuẩn **mô hình MVC2 (Model-View-Controller)** sử dụng Jakarta Servlet và JavaServer Pages (JSP).
- Tương tác với hệ quản trị cơ sở dữ liệu quan hệ **Microsoft SQL Server** thông qua JDBC thuần (`PreparedStatement`, `ResultSet`, `Connection`).
- Xử lý các nghiệp vụ thực tế: Xác thực người dùng (Authentication), phân quyền (Authorization - Role 1 Admin, Role 2 Manager, Role 5 User), quản lý phiên làm việc (`HttpSession`), lưu vết bằng `Cookie`, upload tệp tin đa phương tiện (`@MultipartConfig`), và tải dữ liệu nhị phân (`DownloadImageController`).

---

## 2. CƠ SỞ LÝ THUYẾT

### 2.1. Kiến trúc 3 tầng (Three-Tier Architecture)
Hệ thống được chia tách thành 3 tầng độc lập:
1. **Presentation Layer (Tầng hiển thị):** Gồm JSP và các Servlet Controller. Chịu trách nhiệm nhận yêu cầu HTTP từ phía Client, kiểm tra tính hợp lệ sơ bộ của tham số, chuyển giao xử lý cho tầng Business Logic, và điều hướng View (JSP) để render dữ liệu trả về cho người dùng.
2. **Business Logic Layer (Tầng nghiệp vụ - Service):** Đóng gói toàn bộ logic nghiệp vụ của ứng dụng: kiểm tra tính logic của dữ liệu (kiểm tra tài khoản tồn tại, kiểm tra trùng email, xác thực mật khẩu, xử lý xóa ảnh cũ khi cập nhật ảnh mới). Tầng này độc lập hoàn toàn với giao diện và database.
3. **Data Access Layer (Tầng truy cập dữ liệu - DAO):** Chịu trách nhiệm trực tiếp mở kết nối CSDL, thực thi các câu lệnh truy vấn SQL (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) thông qua JDBC Driver và ánh xạ dữ liệu kết quả thành các đối tượng Java (Model).

### 2.2. Mô hình MVC2 (Model - View - Controller)
- **Model:** Các lớp Java Bean (`User.java`, `Category.java`) chứa thuộc tính và getter/setter để biểu diễn dữ liệu thực thể.
- **View:** Các trang JSP (`login.jsp`, `register.jsp`, `list-category.jsp`, `add-category.jsp`, `edit-category.jsp`, `home.jsp`) chỉ làm nhiệm vụ hiển thị dữ liệu thông qua thư viện thẻ chuẩn JSTL (`<c:forEach>`, `<c:if>`, `<c:choose>`, `<c:url>`) và Expression Language (`${...}`). Hoàn toàn không dùng Java scriptlet (`<% %>`) trong view theo đúng chuẩn thiết kế hiện đại.
- **Controller:** Các lớp Java kế thừa `HttpServlet` với chú thích `@WebServlet` tiếp nhận và điều phối luồng ứng dụng.

### 2.3. Công nghệ sử dụng
- **Ngôn ngữ:** Java SE 21 (LTS)
- **Web Container:** Apache Tomcat 11.0.27 (hỗ trợ Jakarta EE 11 / Servlet 6.1)
- **Build Tool:** Apache Maven 3.10.0
- **Database:** Microsoft SQL Server (kết nối qua JDBC Driver `com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11` và thư viện native `mssql-jdbc_auth-12.8.1.x64.dll` cho Windows Authentication)
- **Giao diện:** HTML5, CSS3, Bootstrap 5.3.3, FontAwesome 6.5.1 (Responsive trên mọi thiết bị).

---

## 3. THIẾT KẾ CƠ SỞ DỮ LIỆU

Cơ sở dữ liệu mang tên **`BanHang`** gồm hai bảng chính:

### 3.1. Bảng `Category` (Danh mục sản phẩm)
| Tên cột | Kiểu dữ liệu | Khóa | Ràng buộc | Ý nghĩa |
| :--- | :--- | :---: | :--- | :--- |
| `cate_id` | `INT` | PK | `IDENTITY(1,1)`, `NOT NULL` | Mã định danh danh mục (tự tăng) |
| `cate_name`| `NVARCHAR(255)`| | `NOT NULL` | Tên danh mục sản phẩm |
| `icons` | `NVARCHAR(255)`| | `NULL` | Tên tệp tin ảnh đại diện lưu trữ trên ổ đĩa |

### 3.2. Bảng `[User]` (Tài khoản người dùng)
| Tên cột | Kiểu dữ liệu | Khóa | Ràng buộc | Ý nghĩa |
| :--- | :--- | :---: | :--- | :--- |
| `id` | `INT` | PK | `IDENTITY(1,1)`, `NOT NULL` | Mã định danh người dùng (tự tăng) |
| `email` | `NVARCHAR(255)`| | `NOT NULL` | Địa chỉ email người dùng |
| `username` | `NVARCHAR(50)` | | `NOT NULL`, `UNIQUE` | Tên đăng nhập duy nhất |
| `fullname` | `NVARCHAR(255)`| | `NOT NULL` | Họ và tên đầy đủ |
| `password` | `NVARCHAR(255)`| | `NOT NULL` | Mật khẩu tài khoản |
| `avatar` | `NVARCHAR(255)`| | `NULL` | Tên file ảnh đại diện |
| `roleid` | `INT` | | `NOT NULL`, `DEFAULT 5` | Quyền hạn (1: Admin, 2: Manager, 5: User)|
| `phone` | `NVARCHAR(20)` | | `NULL` | Số điện thoại liên hệ |
| `createddate`| `DATE` | | `NULL` | Ngày đăng ký tài khoản |

---

## 4. THIẾT KẾ VÀ HIỆN THỰC PHẦN MỀM

### 4.1. Tầng Kết Nối CSDL & DAO
- **`DBConnection.java`**: Thiết lập chuỗi kết nối:
  `jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;integratedSecurity=true;databaseName=BanHang`
- **`CategoryDao` / `CategoryDaoImpl`**:
  - `insert(Category category)`: Thực thi `INSERT INTO Category(cate_name, icons) VALUES (?, ?)`
  - `edit(Category category)`: Thực thi `UPDATE Category SET cate_name = ?, icons = ? WHERE cate_id = ?`
  - `delete(int id)`: Thực thi `DELETE FROM Category WHERE cate_id = ?`
  - `get(int id)`: Truy vấn danh mục theo khóa chính ID
  - `getAll()`: Truy vấn toàn bộ danh sách danh mục
  - `search(String keyword)`: Tìm kiếm danh mục theo từ khóa với mệnh đề `LIKE ?`
- **`UserDao` / `UserDaoImpl`**:
  - `get(String username)`: Tìm kiếm thông tin tài khoản theo tên đăng nhập
  - `insert(User user)`: Thêm mới tài khoản vào CSDL
  - `checkExistEmail(String email)`: Kiểm tra email đã có người sử dụng chưa
  - `checkExistUsername(String username)`: Kiểm tra tên tài khoản đã tồn tại chưa

### 4.2. Tầng Nghiệp Vụ (Service Layer)
- **`CategoryServiceImpl`**: Hiện thực các quy tắc nghiệp vụ danh mục:
  - Khi cập nhật danh mục có hình ảnh mới: tự động kiểm tra và xóa bỏ hình ảnh cũ khỏi đĩa cứng để tránh chiếm dụng dung lượng lưu trữ.
  - Khi xóa danh mục: tự động xóa cả tệp tin hình ảnh đại diện đi kèm.
- **`UserServiceImpl`**: Hiện thực nghiệp vụ xác thực:
  - `login(username, password)`: Kiểm tra tài khoản tồn tại và so khớp mật khẩu.
  - `register(...)`: Xác thực username/email chưa từng tồn tại, gán quyền mặc định `ROLE_USER` (mã 5), tự động lấy ngày giờ hệ thống hiện tại làm `createdDate`.

### 4.3. Tầng Điều Khiển (Controller Layer)
- **`LoginController`**: Hỗ trợ GET (hiển thị form, kiểm tra Session, kiểm tra Cookie tự động đăng nhập) và POST (kiểm tra rỗng, xác thực, ghi nhớ Cookie trong 30 phút).
- **`RegisterController`**: Hỗ trợ GET (form đăng ký) và POST (kiểm tra trùng username/email, kiểm tra khớp mật khẩu xác nhận).
- **`WaitingController`**: Nhận phiên đăng nhập thành công và chuyển tiếp:
  - `roleid == 1` (Admin) $\rightarrow$ Chuyển hướng tới `/admin/home`
  - `roleid == 2` (Manager) $\rightarrow$ Chuyển hướng tới `/admin/home`
  - `roleid == 5` (Khách hàng) $\rightarrow$ Chuyển hướng tới `/home`
- **`CategoryListController`**: Hiển thị bảng danh sách danh mục kèm thanh tìm kiếm từ khóa.
- **`CategoryAddController`**: Nhận dữ liệu đa phần `enctype="multipart/form-data"`, đổi tên ảnh theo timestamp (`System.currentTimeMillis()`) và lưu vào đĩa cứng.
- **`CategoryEditController`**: Load dữ liệu cũ lên form, cho phép thay đổi tên và chọn ảnh mới (hoặc giữ ảnh cũ nếu không chọn file mới).
- **`CategoryDeleteController`**: Xóa bản ghi danh mục khỏi database và xóa ảnh vật lý.
- **`DownloadImageController`**: Nhận tham số `?fname=...`, đọc file nhị phân và copy vào `response.getOutputStream()` với Content-Type phù hợp.

---

## 5. KẾT QUẢ THỰC HIỆN & KIỂM THỬ CHỨC NĂNG

| STT | Chức năng kiểm thử | Dữ liệu đầu vào | Kết quả mong đợi | Trạng thái thực tế |
| :-: | :--- | :--- | :--- | :-: |
| 1 | Đăng nhập tài khoản Admin | `admin` / `123456` | Đăng nhập thành công, chuyển hướng vào `/admin/home` | **ĐẠT (PASS)** |
| 2 | Đăng nhập tài khoản User | `khachhang` / `123456` | Đăng nhập thành công, chuyển hướng vào `/home` | **ĐẠT (PASS)** |
| 3 | Đăng nhập sai mật khẩu | `admin` / `sai_mat_khau` | Hiển thị thông báo: "Tài khoản hoặc mật khẩu không đúng!" | **ĐẠT (PASS)** |
| 4 | Đăng nhập bỏ trống trường | Để trống `username` hoặc `password` | Hiển thị thông báo: "Tài khoản hoặc mật khẩu không được để trống!" | **ĐẠT (PASS)** |
| 5 | Ghi nhớ đăng nhập (Remember Me)| Tích chọn "Nhớ tôi" khi đăng nhập | Tạo Cookie `username` tồn tại trong 30 phút | **ĐẠT (PASS)** |
| 6 | Đăng xuất hệ thống | Nhấp nút "Đăng xuất" | Hủy `session.invalidate()`, xóa Cookie, về trang login | **ĐẠT (PASS)** |
| 7 | Đăng ký tài khoản mới | Điền đầy đủ thông tin hợp lệ | Tạo bản ghi User mới role 5, chuyển về trang login kèm thông báo thành công | **ĐẠT (PASS)** |
| 8 | Đăng ký trùng Username | Nhập username `admin` | Báo lỗi: "Tên tài khoản 'admin' đã tồn tại!" | **ĐẠT (PASS)** |
| 9 | Đăng ký trùng Email | Nhập email đã tồn tại | Báo lỗi: "Email đã được sử dụng!" | **ĐẠT (PASS)** |
| 10 | Xác nhận mật khẩu không khớp | Password $\neq$ ConfirmPassword | Báo lỗi: "Mật khẩu xác nhận không khớp!" | **ĐẠT (PASS)** |
| 11 | Xem danh sách danh mục | Truy cập `/admin/category/list` | Hiển thị danh sách bảng kèm hình ảnh tải qua `/image?fname=...` | **ĐẠT (PASS)** |
| 12 | Thêm danh mục mới có ảnh | Tên danh mục + chọn tệp ảnh | Upload ảnh vào thư mục upload, thêm bản ghi vào CSDL, chuyển hướng về list | **ĐẠT (PASS)** |
| 13 | Chỉnh sửa danh mục | Đổi tên danh mục, thay ảnh mới | Cập nhật tên mới, xóa file ảnh cũ, lưu ảnh mới | **ĐẠT (PASS)** |
| 14 | Xóa danh mục | Nhấp nút Xóa trên dòng danh mục | Bật popup xác nhận JavaScript, xóa bản ghi và xóa ảnh | **ĐẠT (PASS)** |
| 15 | Tìm kiếm danh mục | Nhập từ khóa (vd: "Áo") | Lọc danh sách chỉ hiển thị các danh mục chứa chữ "Áo" | **ĐẠT (PASS)** |
| 16 | Phục vụ hình ảnh tĩnh/động | Truy cập `/image?fname=ao-nam.png` | Trả về stream ảnh chuẩn JPEG/PNG mã HTTP 200 | **ĐẠT (PASS)** |

---

## 6. KẾT LUẬN & ĐÁNH GIÁ

- Đồ án đã **hoàn thành 100% tất cả các yêu cầu và chỉ dẫn kỹ thuật** được nêu trong cả hai tài liệu bài giảng của ThS. Nguyễn Hữu Trung.
- Cấu trúc thư mục chuẩn mực theo định dạng dự án Maven Java Web hiện đại (`jakarta.servlet 6.1`).
- Mã nguồn được tổ chức khoa học, tuân thủ nguyên tắc Clean Code: tách bạch rõ ràng giữa Controller, Service, DAO và Model.
- Giao diện người dùng được nâng cấp hiện đại với Bootstrap 5, FontAwesome, bố cục trang quản trị chuyên nghiệp, hiển thị trực quan và tương thích mọi kích thước màn hình.
- Hệ thống sẵn sàng để triển khai, chạy thực tế và phục vụ cho việc chấm điểm cuối kỳ môn Lập Trình Web.
