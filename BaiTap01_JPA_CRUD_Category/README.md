# BÀI TẬP: XÂY DỰNG CRUD CATEGORY BẰNG JPA 3.0 & HIBERNATE ORM

**Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)**  
**Khoa Công nghệ Thông tin - Bộ môn Công nghệ Phần mềm**  
**Môn học:** Lập trình Web (`WEBPR330479`)  
**Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung  
**Sinh viên thực hiện:** [Họ và Tên Sinh Viên] - [MSSV]  

---

## 1. Giới Thiệu Bài Tập

Dự án này là bài tập thực hành môn Lập trình Web nhằm nắm vững kỹ thuật lập trình hướng đối tượng kết hợp **JPA (Jakarta Persistence API 3.0)** và **Hibernate ORM** kết nối hệ quản trị cơ sở dữ liệu **Microsoft SQL Server**.

Dự án triển khai mô hình đa tầng chuẩn công nghiệp (3-Tier Architecture):
- **Controller Layer:** Jakarta Servlet 6.1 xử lý routing và điều hướng luồng dữ liệu.
- **Service Layer:** Xử lý logic nghiệp vụ, ràng buộc dữ liệu, chống trùng lặp.
- **DAO Layer:** Thao tác dữ liệu thông qua `EntityManager`, `EntityTransaction` và các câu truy vấn `JPQL / NamedQuery`.
- **Entity Layer:** Ánh xạ bảng cơ sở dữ liệu với quan hệ hai chiều `One-to-Many` và `Many-to-One` giữa `Category` và `Video`.
- **View Layer:** Giao diện web tương tác bằng JSP, JSTL 3.0, Bootstrap 5.3 và FontAwesome 6.

---

## 2. Mục Tiêu Đạt Được

- Hiểu và cấu hình thành công file `src/main/resources/META-INF/persistence.xml` với Persistence Unit `jpa-hibernate-mysql`.
- Quản lý vòng đời Entity (`Persist`, `Merge`, `Remove`, `Find`, `Detach`) thông qua `EntityManager`.
- Sử dụng `EntityTransaction` an toàn (tự động commit khi thành công, rollback khi có ngoại lệ phát sinh).
- Viết và thực thi câu truy vấn `NamedQuery` và `JPQL` có tham số (`setParameter`) chống SQL Injection.
- Triển khai chức năng Upload file hình ảnh đa phương thức (multipart/form-data) và liên kết online URL.
- Xây dựng bộ kiểm thử tự động toàn diện kiểm tra 12/12 tiêu chí chất lượng.

---

## 3. Công Nghệ & Thư Viện Sử Dụng

| Công nghệ / Thư viện | Phiên bản | Ghi chú |
|---|---|---|
| **Java JDK** | 21 LTS | Ngôn ngữ phát triển chính |
| **JPA Specification** | Jakarta Persistence 3.1 | Chuẩn ORM của Jakarta EE 10 |
| **Hibernate ORM Core** | `6.6.1.Final` | Bản ổn định tương thích hoàn hảo JPA 3.1 & JDK 21 (Thay thế cho 7.4.6.Final) |
| **Hibernate Validator** | `8.0.1.Final` | Kiểm tra ràng buộc dữ liệu Bean Validation |
| **Jakarta Validation API** | `3.0.2` | Annotation `@NotEmpty`, `@NotNull` |
| **Microsoft SQL Server** | 2019 / 2022 | Hệ quản trị cơ sở dữ liệu (Database: `webst2`) |
| **MSSQL JDBC Driver** | `12.8.1.jre11` | Driver kết nối SQL Server (Hỗ trợ Windows Auth DLL & sa) |
| **Jakarta Servlet API** | `6.1.0` | Container Servlet (Scope: provided) |
| **Jakarta JSP & JSTL** | `4.0.0` & `3.0.2` | Giao diện động JSP và thư viện thẻ taglib JSTL |
| **Apache Tomcat** | 11.0.27 | Máy chủ Web Servlet container |
| **Apache Maven** | 3.10.0 | Quản lý vòng đời build và thư viện |
| **Giao diện Web** | Bootstrap 5.3 & FontAwesome 6 | Responsive, thân thiện trên mobile và desktop |

---

## 4. Cấu Trúc Mã Nguồn (Directory Structure)

```
BaiTap01_JPA_CRUD_Category/
├── pom.xml                                   # Cấu hình Maven dependencies & plugins
├── run_server.bat                            # Script khởi động tự động một chạm Tomcat 11
├── README.md                                 # Hướng dẫn chi tiết báo cáo đồ án
├── .gitignore                                # Cấu hình bỏ qua file build & nhạy cảm
├── .vscode/
│   └── launch.json                           # Cấu hình chạy & debug trong VS Code
├── src/
│   ├── main/
│   │   ├── java/vn/iotstar/
│   │   │   ├── config/
│   │   │   │   ├── JPAConfig.java            # Quản lý Singleton EntityManagerFactory & EntityManager
│   │   │   │   └── Test.java                 # Class test khởi tạo cơ bản theo Section 5 tài liệu
│   │   │   ├── entity/
│   │   │   │   ├── Category.java             # Entity categories (@OneToMany với Video)
│   │   │   │   └── Video.java                # Entity Videos (@ManyToOne với Category)
│   │   │   ├── dao/
│   │   │   │   ├── ICategoryDao.java         # Interface DAO các thao tác JPA
│   │   │   │   └── impl/CategoryDao.java     # Triển khai EntityManager & JPQL
│   │   │   ├── service/
│   │   │   │   ├── ICategoryService.java     # Interface tầng nghiệp vụ
│   │   │   │   └── impl/CategoryServiceImpl.java # Xử lý validation & nghiệp vụ
│   │   │   ├── controller/
│   │   │   │   ├── CategoryController.java   # Servlet CRUD điều hướng người dùng
│   │   │   │   └── DownloadImageController.java # Servlet stream ảnh upload /image?fname=...
│   │   │   ├── util/
│   │   │   │   ├── Constant.java             # Cấu hình hằng số & thư mục upload
│   │   │   │   └── constants.java            # Alias tương thích tài liệu mẫu
│   │   │   └── test/
│   │   │       └── JpaTest.java              # Bộ kiểm thử 12 chức năng CRUD hoàn chỉnh
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml           # Cấu hình JPA Persistence Unit (jpa-hibernate-mysql)
│   │   └── webapp/
│   │       ├── upload/category/              # Thư mục lưu trữ hình ảnh upload
│   │       └── views/admin/
│   │           ├── category-list.jsp         # Giao diện danh sách danh mục & tìm kiếm
│   │           ├── category-add.jsp          # Giao diện thêm danh mục mới
│   │           └── category-edit.jsp         # Giao diện cập nhật danh mục
```

---

## 5. Hướng Dẫn Cấu Hình Cơ Sở Dữ Liệu SQL Server

1. **Khởi động SQL Server** trên máy tính (Cổng mặc định `1433`).
2. Mở **SQL Server Management Studio (SSMS)** hoặc công cụ query, tạo cơ sở dữ liệu `webst2`:
   ```sql
   CREATE DATABASE webst2 COLLATE Latin1_General_100_CI_AI_SC_UTF8;
   GO
   ```
3. **Cơ chế tự động tạo bảng (Hibernate DDL Auto):**
   Trong file `persistence.xml`, thuộc tính `hibernate.hbm2ddl.auto` được đặt là `update`. Khi ứng dụng hoặc class test chạy lần đầu, Hibernate sẽ **tự động sinh bảng `categories` và `Videos`** kèm theo khóa ngoại `CategoryId` mà không cần phải gõ lệnh DDL thủ công!

---

## 6. Hướng Dẫn Cấu Hình JPA (`persistence.xml`)

File cấu hình đặt tại: `src/main/resources/META-INF/persistence.xml`

```xml
<persistence-unit name="jpa-hibernate-mysql" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <class>vn.iotstar.entity.Category</class>
    <class>vn.iotstar.entity.Video</class>

    <properties>
        <property name="jakarta.persistence.jdbc.driver" value="com.microsoft.sqlserver.jdbc.SQLServerDriver" />
        <property name="jakarta.persistence.jdbc.url" 
                  value="jdbc:sqlserver://localhost:1433;databaseName=webst2;integratedSecurity=true;encrypt=true;trustServerCertificate=true;" />
        <property name="jakarta.persistence.jdbc.user" value="sa" />
        <property name="jakarta.persistence.jdbc.password" value="1234567@a$" />
        <property name="hibernate.dialect" value="org.hibernate.dialect.SQLServerDialect" />
        <property name="hibernate.hbm2ddl.auto" value="update" />
        <property name="hibernate.show_sql" value="true" />
        <property name="hibernate.format_sql" value="true" />
    </properties>
</persistence-unit>
```

> **Ghi chú bảo mật:** Chuỗi kết nối hỗ trợ cơ chế Windows Authentication tích hợp (`integratedSecurity=true`) nạp native DLL `mssql-jdbc_auth-12.8.1.x64.dll` giúp kết nối an toàn mà không cần lưu lộ mật khẩu thật lên GitHub.

---

## 7. Hướng Dẫn Khởi Chạy Ứng Dụng

Dự án hỗ trợ 2 phương thức khởi chạy song song:

### Cách 1: Chạy Bằng File Batch (Khuyên Dùng)
1. Vào thư mục dự án `BaiTap01_JPA_CRUD_Category`.
2. Nhấn đúp chuột vào file **`run_server.bat`**.
3. File batch sẽ tự động:
   - Kiểm tra JDK 21 và Tomcat 11.
   - Giải phóng cổng 8080 và 8005 nếu có ứng dụng cũ chiếm dụng.
   - Đóng gói file WAR và đồng bộ vào Tomcat.
   - Tự động mở trình duyệt web tại địa chỉ:  
     👉 **`http://localhost:8080/BaiTap01_JPA_CRUD_Category/admin/categories`**

### Cách 2: Mở và Chạy Trực Tiếp trong Visual Studio Code
1. Mở thư mục `BaiTap01_JPA_CRUD_Category` bằng Visual Studio Code (File -> Open Folder).
2. Nhấn phím `F5` hoặc mở tab **Run & Debug** (Ctrl + Shift + D).
3. Chọn cấu hình:
   - **`JPA Test (Chạy kiểm thử 12 chức năng JPA)`**: Tự động chạy toàn bộ 12 test case trong `JpaTest.java`.
   - **`JPA Basic Test`**: Chạy class `Test.java` cơ bản theo tài liệu.

---

## 8. Kết Quả Kiểm Thử Toàn Diện (12 Tiêu Chí)

Dự án đã được kiểm thử tự động trực tiếp với cơ sở dữ liệu SQL Server thông qua class `vn.iotstar.test.JpaTest`:

| STT | Chức năng kiểm thử | Thao tác JPA / Hibernate | Kết quả | Trạng thái |
|:---:|---|---|---|:---:|
| **1** | **Kết nối JPA** | `Persistence.createEntityManagerFactory("jpa-hibernate-mysql")` | Kết nối SQL Server thành công, EntityManager mở | **ĐẠT (PASS)** |
| **2** | **INSERT Entity** | `EntityManager.persist()` với EntityTransaction | Thêm thành công Category và Video có quan hệ ngoại | **ĐẠT (PASS)** |
| **3** | **SELECT ALL** | `createNamedQuery("Category.findAll")` | Truy vấn lấy danh sách đầy đủ danh mục | **ĐẠT (PASS)** |
| **4** | **FIND BY ID** | `EntityManager.find(Category.class, id)` | Tìm đúng bản ghi theo khóa chính | **ĐẠT (PASS)** |
| **5** | **FIND BY NAME** | `JPQL: SELECT c FROM Category c WHERE c.categoryname = :catename` | Tìm chính xác Category theo tên | **ĐẠT (PASS)** |
| **6** | **SEARCH LIKE** | `JPQL: SELECT c FROM Category c WHERE c.categoryname LIKE :keyword` | Tìm kiếm mềm theo từ khóa đại diện | **ĐẠT (PASS)** |
| **7** | **UPDATE** | `EntityManager.merge()` với EntityTransaction | Cập nhật tên mới và đổi trạng thái thành công | **ĐẠT (PASS)** |
| **8** | **COUNT** | `JPQL: SELECT COUNT(c) FROM Category c` | Đếm chuẩn xác tổng số lượng danh mục | **ĐẠT (PASS)** |
| **9** | **PAGINATION** | `query.setFirstResult(page*size).setMaxResults(size)` | Phân trang chính xác không tải toàn bộ RAM | **ĐẠT (PASS)** |
| **10** | **VALIDATION** | Kiểm tra ràng buộc `@NotEmpty` và tên rỗng | Bắt lỗi hợp lệ và chặn dữ liệu rỗng | **ĐẠT (PASS)** |
| **11** | **UPLOAD DIR** | Kiểm tra thư mục upload ảnh `Constant.DIR` | Thư mục upload tồn tại và có quyền ghi | **ĐẠT (PASS)** |
| **12** | **DELETE** | `EntityManager.remove()` với EntityTransaction | Xóa sạch entity khỏi cơ sở dữ liệu | **ĐẠT (PASS)** |

**Tổng kết:** **12/12 tiêu chí đạt 100%**, đáp ứng trọn vẹn yêu cầu bài tập của giảng viên.

---

## 9. Hướng Dẫn Đưa Lên GitHub Để Nộp Bài

Để nộp link repository lên hệ thống UTExLMS, bạn thực hiện các bước sau trong terminal của VS Code:

1. **Khởi tạo Git Repository cục bộ:**
   ```bash
   cd BaiTap01_JPA_CRUD_Category
   git init
   git add .
   git commit -m "feat: Hoan thanh bai tap CRUD Category bang JPA 3.0 va Hibernate ORM"
   ```

2. **Tạo Repository mới trên GitHub:**
   - Đăng nhập tài khoản [GitHub](https://github.com/).
   - Nhấn **New Repository**, đặt tên ví dụ: `BaiTap01_JPA_CRUD_Category`.
   - Chọn chế độ **Public** để giảng viên có thể xem được source code.

3. **Liên kết và Push code lên GitHub:**
   ```bash
   git branch -M main
   git remote add origin https://github.com/<tai-khoan-github-cua-ban>/BaiTap01_JPA_CRUD_Category.git
   git push -u origin main
   ```

4. **Sao chép link repository** (Ví dụ: `https://github.com/<username>/BaiTap01_JPA_CRUD_Category`) và nộp vào bài tập trên hệ thống UTExLMS.
