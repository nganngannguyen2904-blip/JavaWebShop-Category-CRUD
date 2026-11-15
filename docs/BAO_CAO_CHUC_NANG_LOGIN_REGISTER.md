# BÁO CÁO CHUYÊN SÂU CHỨC NĂNG ĐĂNG NHẬP (LOGIN) & ĐĂNG KÝ (REGISTER)
**TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)**  
**KHOA CÔNG NGHỆ THÔNG TIN - BỘ MÔN CÔNG NGHỆ PHẦN MỀM**  
**MÔN HỌC:** LẬP TRÌNH WEB (WEBPR330479)  
**GIẢNG VIÊN HƯỚNG DẪN:** ThS. NGUYỄN HỮU TRUNG  
**ĐỀ TÀI:** XÂY DỰNG HỆ THỐNG SHOPPING SERVICE MVC (SERVLET - JDBC - 3 TIER)

---

## 1. TỔNG QUAN & MỤC TIÊU HỆ THỐNG XÁC THỰC

Chức năng Xác thực người dùng (Authentication) và Phân quyền truy cập (Authorization) là hạt nhân then chốt trong các ứng dụng thương mại điện tử. Đồ án xây dựng hệ thống **Login & Register** đáp ứng nghiêm ngặt các tiêu chuẩn kỹ thuật công nghiệp và bảo mật ứng dụng web theo khuyến nghị OWASP:

1. **Đăng ký an toàn (Register):** Kiểm tra chặt chẽ dữ liệu đầu vào (họ tên, email, số điện thoại, username, độ dài và độ phức tạp mật khẩu). Mật khẩu được băm (hash) 100% bằng thuật toán **BCrypt** với Salt ngẫu nhiên trước khi lưu vào CSDL.
2. **Đăng nhập linh hoạt (Login):** Cho phép đăng nhập bằng cả **Tên đăng nhập (Username)** hoặc **Địa chỉ Email**, phân quyền tự động theo vai trò (Admin, Manager, User).
3. **Phòng chống Brute-Force:** Tự động đếm số lần đăng nhập sai; nếu vượt quá 5 lần liên tiếp, tài khoản tự động bị khóa tạm thời trong 15 phút.
4. **Chống lộ lọt thông tin (Anti-User Enumeration & Timing Attacks):** Thông báo lỗi chung đồng nhất, không tiết lộ tài khoản có tồn tại hay không, chuẩn hóa thời gian phản hồi.
5. **Phân quyền tầng Máy chủ (Server-side RBAC Filter):** Sử dụng `AdminSecurityFilter` kiểm tra phiên làm việc phía server, tuyệt đối không phụ thuộc vào giao diện hay tham số gửi từ trình duyệt; chặn mã lỗi **403 Forbidden** đối với tài khoản Khách hàng (User) cố tình truy cập `/admin/*`.
6. **Quên mật khẩu & Đổi mật khẩu:** Mã xác thực đặt lại mật khẩu ngẫu nhiên 6 chữ số có thời hạn 15 phút, đổi mật khẩu an toàn kiểm tra mật khẩu cũ.

---

## 2. SƠ ĐỒ QUY TRÌNH NGHIỆP VỤ (FLOWCHART)

### 2.1. Quy trình Đăng Ký Tài Khoản (Register Flow)
```
[ Khách hàng ] --> Nhập Form Đăng Ký (Họ tên, Email, Phone, Username, Password, ConfirmPassword)
      |
      v
[ Frontend Validation ] --(Dữ liệu sai định dạng)--> [ Hiển thị cảnh báo màu đỏ tại ô tương ứng ]
      | (Hợp lệ)
      v
[ POST /register ] --> [ RegisterController ]
      |
      v
[ Backend Validation ]
   + Kiểm tra không rỗng
   + Regex Email: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$
   + Regex Phone: ^(0[3|5|7|8|9])[0-9]{8}$
   + Regex Username: ^[a-zA-Z0-9_]{3,30}$
   + Độ dài Mật khẩu >= 6 ký tự
   + Password == ConfirmPassword
      | (Thất bại) --> Gửi thông báo lỗi + Điền lại dữ liệu cũ -> [ Trang Register ]
      | (Hợp lệ)
      v
[ Database Unique Check (SQL Server) ]
   + checkExistEmail(email) == true? ----(Có)----> Báo lỗi: Email đã được sử dụng
   + checkExistUsername(username) == true? -(Có)-> Báo lỗi: Tên đăng nhập đã tồn tại
   + checkExistPhone(phone) == true? ----(Có)----> Báo lỗi: Số điện thoại đã đăng ký
      | (Duy nhất 100%)
      v
[ BCrypt Password Hashing ] --> BCrypt.hashpw(password, BCrypt.gensalt(10))
      |
      v
[ Cố định Role = 5 (USER) ] --> Tránh leo thang quyền hạn
      |
      v
[ INSERT INTO dbo.[User] ] --> status=1, failed_attempts=0, createddate=GETDATE()
      |
      v
[ Redirect /login?registered=1 ] --> Hiển thị thông báo màu xanh "Đăng ký thành công!"
```

---

### 2.2. Quy trình Đăng Nhập & Phân Quyền (Login Flow)
```
[ Người dùng ] --> Nhập Tài khoản (Username hoặc Email) + Mật khẩu + [Ghi nhớ]
      |
      v
[ POST /login ] --> [ LoginController ]
      |
      v
[ UserService.authenticate(identifier, password) ]
      |
      +---> Kiểm tra user trong SQL Server bằng PreparedStatement:
      |        SELECT * FROM [User] WHERE username = ? OR email = ?
      |
      +---> [TH1] Tài khoản không tồn tại?
      |        --> Chạy hàm băm giả định (chống Timing Attack)
      |        --> Trả về INVALID_CREDENTIALS: "Tên đăng nhập / Email hoặc mật khẩu không đúng!"
      |
      +---> [TH2] Tài khoản bị quản trị viên khóa (status = 0)?
      |        --> Trả về ACCOUNT_LOCKED: "Tài khoản của bạn đã bị khóa bởi Quản trị viên!"
      |
      +---> [TH3] Tài khoản đang bị tạm khóa do Brute-force (isTemporarilyLocked())?
      |        --> Trả về TOO_MANY_ATTEMPTS: "Tài khoản bị tạm khóa trong X phút."
      |
      +---> [TH4] Kiểm tra Mật khẩu với BCrypt.checkpw(password, storedHash):
               |
               +--(SAI MẬT KHẨU)--> Tăng failed_attempts + 1
               |      Nếu failed_attempts >= 5: ghi nhận lock_time = GETDATE() (Khóa 15 phút)
               |      Trả về thông báo lỗi + số lần thử còn lại
               |
               +--(ĐÚNG MẬT KHẨU)--> ĐẶT LẠI failed_attempts = 0, lock_time = NULL
                      |
                      v
            [ Tái tạo Session an toàn ] --> Hủy session cũ, cấp session mới
            [ Lưu session.setAttribute("account", user) ]
            [ Thiết lập Cookie 'username' có HttpOnly=true nếu chọn Remember me ]
                      |
                      v
            [ Chuyển hướng /waiting ] --> Điều phối theo vai trò:
                      |
                      +---> Role 1 (Admin)   ==> /admin/home
                      +---> Role 2 (Manager) ==> /admin/home
                      +---> Role 5 (User)    ==> /home
```

---

## 3. THIẾT KẾ CƠ SỞ DỮ LIỆU BẢNG `[User]`

Cơ sở dữ liệu: **`BanHang`** trên Microsoft SQL Server.  
Bảng mã: **`Vietnamese_CI_AS`** (Hỗ trợ Unicode tiếng Việt đầy đủ).

```sql
CREATE TABLE [User] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(100) NOT NULL UNIQUE,       -- Khóa duy nhất, chống đăng ký trùng
    username NVARCHAR(50) NOT NULL UNIQUE,      -- Khóa duy nhất, tên đăng nhập
    fullname NVARCHAR(100) NOT NULL,            -- Họ và tên người dùng tiếng Việt
    password NVARCHAR(255) NOT NULL,           -- Chuỗi hash BCrypt độ dài 60 ký tự ($2a$10$...)
    avatar NVARCHAR(255) NULL DEFAULT 'avata.png',
    roleid INT NOT NULL DEFAULT 5,              -- 1: Admin, 2: Manager, 5: User
    phone NVARCHAR(20) NULL,                    -- Số điện thoại liên hệ
    status INT NOT NULL DEFAULT 1,              -- 1: Đang hoạt động, 0: Bị khóa
    failed_attempts INT NOT NULL DEFAULT 0,     -- Đếm số lần nhập sai liên tiếp
    lock_time DATETIME NULL,                    -- Thời điểm bắt đầu tạm khóa do Brute-force
    reset_token NVARCHAR(100) NULL,             -- Token mã xác thực quên mật khẩu (6 chữ số)
    reset_token_expiry DATETIME NULL,           -- Hạn hiệu lực của mã xác thực (15 phút)
    createddate DATETIME DEFAULT GETDATE()
);
```

---

## 4. CHI TIẾT CÁC GIẢI PHÁP BẢO MẬT CHUYÊN SÂU

### 4.1. Băm mật khẩu chuẩn công nghiệp BCrypt (Blowfish)
- Hệ thống tích hợp thư viện chuẩn `org.mindrot:jbcrypt:0.4`.
- Mật khẩu khi lưu vào CSDL có dạng:  
  `$2a$10$zWnXQo0VcNCShoOWkW6mKuue/CgIOEu4hXPuW06oOFbGbeecR8NZu`
  - `$2a$`: Chuẩn định dạng thuật toán OpenBSD BCrypt.
  - `$10$`: Độ phức tạp tính toán (Work factor / Cost = $2^{10} = 1024$ vòng lặp biến đổi Blowfish).
  - Phần còn lại: 22 ký tự Salt ngẫu nhiên + chuỗi mã hóa 31 ký tự.
- **Tính ưu việt:** Dù hai người dùng đặt cùng một mật khẩu `"123456"`, chuỗi băm sinh ra trong database hoàn toàn khác nhau nhờ Salt ngẫu nhiên, vô hiệu hóa hoàn toàn kỹ thuật tấn công Rainbow Table.

### 4.2. Chống tấn công SQL Injection 100%
- Toàn bộ truy vấn trong `UserDaoImpl` đều sử dụng đối tượng `java.sql.PreparedStatement` với tham số hóa `?`:
  ```java
  String sql = "SELECT * FROM [User] WHERE username = ? OR email = ?";
  PreparedStatement ps = conn.prepareStatement(sql);
  ps.setString(1, identifier);
  ps.setString(2, identifier);
  ```
- Không sử dụng nối chuỗi câu lệnh SQL ở bất kỳ vị trí nào, ngăn chặn mọi nỗ lực khai thác kiểu `' OR '1'='1`.

### 4.3. Cơ chế Chống Tấn Công Dò Quét Mật Khẩu (Brute-Force & Rate Limiting)
- Khi người dùng nhập sai mật khẩu, biến `failed_attempts` tăng thêm 1.
- Nếu `failed_attempts >= 5`:
  - Hệ thống ghi nhận `lock_time = NOW()`.
  - Khóa tài khoản tạm thời trong vòng **15 phút**.
  - Dù kẻ tấn công tiếp tục thử đúng mật khẩu trong thời gian này, hệ thống vẫn từ chối đăng nhập và thông báo thời gian còn lại cần chờ.
- Khi đăng nhập thành công với mật khẩu chính xác, `failed_attempts` tự động đặt lại về 0.

### 4.4. Chống Thu Thập Danh Sách Tài Khoản (Anti-User Enumeration)
- Khi nhập sai tài khoản hoặc sai mật khẩu, hệ thống trả về thông báo lỗi chung đồng nhất:  
  *"Tên đăng nhập / Email hoặc mật khẩu không chính xác!"*
- Hệ thống không phản hồi kiểu *"Tài khoản không tồn tại"* hay *"Email chưa được đăng ký"*, ngăn chặn kẻ xấu thăm dò danh sách tài khoản hợp lệ của hệ thống.

### 4.5. Phân quyền chặt chẽ tầng Backend (Role-Based Access Control)
- Tạo lớp bộ lọc `AdminSecurityFilter` chặn toàn bộ đường dẫn `/admin/*`:
  ```java
  @WebFilter(urlPatterns = "/admin/*")
  public class AdminSecurityFilter implements Filter {
      public void doFilter(...) {
          User user = (session != null) ? (User) session.getAttribute("account") : null;
          if (user == null) {
              resp.sendRedirect("/login?alert=login_required");
              return;
          }
          if (!user.canAccessAdmin()) { // Chỉ chấp nhận Role 1 (Admin) hoặc Role 2 (Manager)
              resp.setStatus(403);
              req.getRequestDispatcher("/views/common/access-denied.jsp").forward(req, resp);
              return;
          }
          chain.doFilter(req, resp);
      }
  }
  ```
- Khách hàng (Role 5) khi cố tình gõ URL `/admin/home` hoặc `/admin/category/list` sẽ bị chặn ngay lập tức với trang thông báo **403 Forbidden - Truy Cập Bị Từ Chối**.

### 4.6. Bảo mật Cookie & Session
- Cookie ghi nhớ đăng nhập (`remember`) được gắn cờ `HttpOnly = true`, ngăn chặn mã độc JavaScript phía client (XSS) đọc trộm hoặc đánh cắp phiên đăng nhập.
- Sau khi đăng nhập thành công, phiên làm việc cũ bị hủy (`oldSession.invalidate()`) và phiên mới được cấp phát nhằm chống tấn công **Session Fixation**.

---

## 5. BẢNG TỔNG HỢP KIỂM THỬ THỰC TẾ 23 TEST CASES

Tất cả 23 Test Case đã được kiểm thử tự động trực tiếp trên mã nguồn và cơ sở dữ liệu SQL Server `BanHang` thông qua lớp kiểm thử: `vn.iotstar.test.AuthTestSuite`.

### 5.1. Nhóm Kiểm Thử ĐĂNG KÝ (REGISTER) - 12 Test Cases

| Mã Test | Tên Test Case | Dữ liệu đầu vào | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-REG-01** | Đăng ký thông tin hợp lệ | Họ tên, email mới, username mới, pass>=6 | Lưu CSDL, băm BCrypt, gán Role 5 | Đăng ký thành công, hash `$2a$`, role=5 | **PASS** |
| **TC-REG-02** | Bỏ trống toàn bộ thông tin | Rỗng tất cả các trường | Báo lỗi trường bắt buộc | Chặn từ Client và Backend | **PASS** |
| **TC-REG-03** | Thiếu trường họ và tên | Họ tên rỗng, các trường khác có | Báo lỗi: Vui lòng nhập họ tên | Báo lỗi họ tên bị thiếu | **PASS** |
| **TC-REG-04** | Email sai định dạng | Email: `myemail_invalid_format` | Báo lỗi định dạng email | Regex chặn, không cho gửi | **PASS** |
| **TC-REG-05** | Email đã tồn tại | Email: `admin@shoppingservice.vn` | Báo lỗi email đã được sử dụng | Phát hiện trùng lặp CSDL, báo lỗi | **PASS** |
| **TC-REG-06** | Username đã tồn tại | Username: `admin` | Báo lỗi tên đăng nhập đã tồn tại | Phát hiện trùng lặp CSDL, báo lỗi | **PASS** |
| **TC-REG-07** | Mật khẩu quá ngắn (<6 ký tự) | Password: `12345` | Báo lỗi mật khẩu tối thiểu 6 ký tự | Chặn do độ dài < 6 | **PASS** |
| **TC-REG-08** | Mật khẩu không đạt chính sách | Password rỗng | Báo lỗi bắt buộc nhập mật khẩu | Chặn bởi bộ lọc kiểm tra | **PASS** |
| **TC-REG-09** | Xác nhận mật khẩu không khớp | Pass: `Password123`, Confirm: `Pass999` | Báo lỗi xác nhận mật khẩu | Phát hiện không khớp, chặn lưu | **PASS** |
| **TC-REG-10** | Đăng ký liên tiếp cùng 1 email | Lần 1 thành công, lần 2 dùng lại email | Lần 1 PASS, Lần 2 báo lỗi trùng | Lần 1 tạo mới, Lần 2 chặn trùng lặp | **PASS** |
| **TC-REG-11** | Gửi dữ liệu bẩn trực tiếp Backend | Gọi trực tiếp Service bỏ qua Frontend | Backend trả về false, không ghi CSDL | Backend từ chối thành công | **PASS** |
| **TC-REG-12** | Thử truyền quyền ADMIN khi đăng ký | Gửi tham số request `role=1` | Backend cố định Role=5 (USER) | Tài khoản tạo ra có Role=5 | **PASS** |

---

### 5.2. Nhóm Kiểm Thử ĐĂNG NHẬP & BẢO MẬT (LOGIN) - 11 Test Cases

| Mã Test | Tên Test Case | Dữ liệu đầu vào | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-LOG-01** | Đăng nhập đúng Username & Email | `admin` / `admin@shoppingservice.vn` | Đăng nhập thành công, cấp Session | Thành công cả Username và Email | **PASS** |
| **TC-LOG-02** | Đăng nhập sai mật khẩu | `khachhang`, Mật khẩu: `SaiPass123` | Báo lỗi, tăng số lần thử sai | Trả về INVALID_CREDENTIALS | **PASS** |
| **TC-LOG-03** | Đăng nhập tài khoản không tồn tại | User: `khong_ton_tai_xyz` | Báo lỗi chung (chống Enumeration) | Thông báo lỗi chung bảo mật | **PASS** |
| **TC-LOG-04** | Để trống tài khoản và mật khẩu | User: `""`, Pass: `""` | Chặn đăng nhập, yêu cầu nhập đủ | Chặn ngay tầng Controller | **PASS** |
| **TC-LOG-05** | Tài khoản bị khóa (status = 0) | User bị khóa, pass đúng `123456` | Chặn đăng nhập, báo tài khoản bị khóa | Trả về ACCOUNT_LOCKED | **PASS** |
| **TC-LOG-06** | Đăng nhập sai liên tiếp >= 5 lần | Nhập sai 5 lần liên tiếp | Tạm khóa 15 phút, chặn lần thứ 6 | Kích hoạt TOO_MANY_ATTEMPTS | **PASS** |
| **TC-LOG-07** | USER truy cập khu vực ADMIN | Tài khoản `khachhang` vào `/admin/*` | Filter chặn lại, trả mã 403 | canAccessAdmin()=false, chặn 403 | **PASS** |
| **TC-LOG-08** | ADMIN truy cập trang quản trị | Tài khoản `admin` vào `/admin/*` | Cho phép truy cập bình thường | canAccessAdmin()=true, cấp quyền | **PASS** |
| **TC-LOG-09** | Đăng xuất và kiểm tra Session/Cookie | Gọi URL `/logout` | Hủy Session, xóa Cookie Remember | Session hủy, Cookie MaxAge=0 | **PASS** |
| **TC-LOG-10** | Truy cập trang bảo mật sau đăng xuất | Truy cập `/admin/home` khi chưa login | Chuyển hướng sang `/login` | Chuyển hướng sang trang đăng nhập | **PASS** |
| **TC-LOG-11** | Thử đổi role ở trình duyệt để hack | Sửa cookie/HTML cố chiếm Role Admin | Server kiểm tra từ DB, loại bỏ can thiệp | Phân quyền Server-side an toàn | **PASS** |

**TỔNG KẾT KẾT QUẢ KIỂM THỬ:** **23/23 Test Cases đạt yêu cầu (100% PASS).**

---

## 6. DANH SÁCH TÀI KHOẢN MẪU PHỤC VỤ GIẢNG VIÊN ĐÁNH GIÁ

| Tên đăng nhập | Email | Mật khẩu | Quyền hạn (Role) | Chức năng kiểm thử |
| :--- | :--- | :---: | :--- | :--- |
| **`admin`** | `admin@shoppingservice.vn` | `123456` | **Role 1 (Admin)** | Toàn quyền Quản trị hệ thống, Quản lý danh mục & Quản lý tồn kho (`/admin/*`) |
| **`trungnh`** | `trungnh@hcmute.edu.vn` | `123456` | **Role 1 (GVHD)** | Toàn quyền Quản trị hệ thống & Xem báo cáo thống kê |
| **`manager`** | `manager@shoppingservice.vn` | `123456` | **Role 2 (Manager)** | Quản lý danh mục & sản phẩm |
| **`khachhang`** | `khachhang@gmail.com` | `123456` | **Role 5 (User)** | Mua sắm, giỏ hàng, đặt hàng; **thử truy cập /admin/* sẽ bị chặn 403** |
