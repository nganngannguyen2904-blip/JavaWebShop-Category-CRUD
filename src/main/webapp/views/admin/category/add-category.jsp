<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Danh Mục Mới - Shopping Service MVC</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background-color: #f4f6f9;
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
        .admin-topbar {
            background: #0088cc;
            color: #ffffff;
            padding: 12px 24px;
        }
        .admin-topbar a {
            color: #ffffff;
            text-decoration: none;
        }
        .sidebar {
            min-height: calc(100vh - 60px);
            background: #222d32;
            color: #b8c7ce;
            padding: 0;
        }
        .sidebar .user-panel {
            padding: 20px 15px;
            background: #1a2226;
            text-align: center;
        }
        .sidebar .user-panel img {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            border: 3px solid #0088cc;
            object-fit: cover;
        }
        .sidebar-menu {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .sidebar-menu li a {
            display: flex;
            align-items: center;
            padding: 14px 20px;
            color: #b8c7ce;
            text-decoration: none;
            border-left: 3px solid transparent;
            transition: all 0.3s;
        }
        .sidebar-menu li a:hover, .sidebar-menu li.active > a {
            color: #ffffff;
            background: #1e282c;
            border-left-color: #0088cc;
        }
        .content-wrapper {
            padding: 25px 30px;
        }
        .card-custom {
            border: none;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
            background: #ffffff;
        }
        .image-preview {
            max-width: 200px;
            max-height: 150px;
            border: 2px dashed #ced4da;
            border-radius: 8px;
            padding: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #fafafa;
            margin-top: 10px;
        }
        .image-preview img {
            max-width: 100%;
            max-height: 140px;
            border-radius: 6px;
        }
    </style>
</head>
<body>

<!-- Admin Header Bar -->
<header class="admin-topbar d-flex justify-content-between align-items-center shadow-sm">
    <div class="d-flex align-items-center">
        <h4 class="mb-0 fw-bold"><i class="fa fa-tachometer-alt me-2"></i>Dashboard</h4>
        <span class="ms-3 badge bg-light text-primary">Shopping Service MVC</span>
    </div>
    <div class="d-flex align-items-center">
        <span class="me-3">
            <i class="fa fa-user-circle me-1"></i>
            Xin chào, <strong>${sessionScope.account != null ? sessionScope.account.fullName : 'Admin'}</strong>
        </span>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-danger px-3">
            <i class="fa fa-sign-out-alt me-1"></i>Đăng xuất
        </a>
    </div>
</header>

<div class="container-fluid">
    <div class="row">
        <!-- Sidebar Menu -->
        <aside class="col-md-3 col-lg-2 sidebar">
            <div class="user-panel">
                <img src="https://ui-avatars.com/api/?name=${sessionScope.account != null ? sessionScope.account.fullName : 'Admin'}&background=0088cc&color=fff&size=128" alt="User Image">
                <div class="mt-2 text-white fw-bold">${sessionScope.account != null ? sessionScope.account.fullName : 'Nguyễn Hữu Trung'}</div>
                <div class="small text-success"><i class="fa fa-circle text-success me-1" style="font-size: 10px;"></i>Bạn là Admin</div>
            </div>

            <ul class="sidebar-menu">
                <li>
                    <a href="${pageContext.request.contextPath}/admin/home">
                        <i class="fa fa-gauge"></i> <span>Dashboard</span>
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/category/list" class="bg-primary text-white">
                        <i class="fa fa-folder-open"></i> <span>Quản lý Danh mục</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/home" target="_blank">
                        <i class="fa fa-desktop"></i> <span>Xem Trang Bán Hàng</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/logout">
                        <i class="fa fa-power-off text-danger"></i> <span>Đăng xuất</span>
                    </a>
                </li>
            </ul>
        </aside>

        <!-- Main Content Area -->
        <main class="col-md-9 col-lg-10 content-wrapper">
            
            <div class="mb-4">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/home">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/category/list">Danh mục</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Thêm mới</li>
                    </ol>
                </nav>
                <h2 class="fw-bold text-dark mb-1">Thêm danh mục mới</h2>
                <p class="text-muted">Nhập thông tin để tạo danh mục sản phẩm mới vào hệ thống</p>
            </div>

            <c:if test="${alert != null}">
                <div class="alert alert-${alertType != null ? alertType : 'danger'} alert-dismissible fade show d-flex align-items-center" role="alert">
                    <i class="fa fa-exclamation-circle me-2 flex-shrink-0"></i>
                    <div>${alert}</div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="row">
                <div class="col-lg-8">
                    <div class="card card-custom p-4">
                        
                        <!-- Form Thêm Danh Mục (Theo Slide 30 - File 1) -->
                        <form role="form" action="${pageContext.request.contextPath}/admin/category/add" method="post" enctype="multipart/form-data">
                            
                            <!-- Tên danh mục -->
                            <div class="mb-4">
                                <label class="form-label fw-bold text-dark">Tên danh mục <span class="text-danger">*</span></label>
                                <input type="text" 
                                       class="form-control form-control-lg" 
                                       placeholder="Please enter category Name (Ví dụ: Thời trang Nam, Điện thoại...)" 
                                       name="catename" 
                                       required 
                                       autofocus />
                                <div class="form-text text-muted">Tên danh mục không được để trống và nên ngắn gọn, rõ ràng.</div>
                            </div>

                            <!-- Ảnh đại diện -->
                            <div class="mb-4">
                                <label class="form-label fw-bold text-dark">Ảnh đại diện danh mục</label>
                                <input type="file" 
                                       class="form-control" 
                                       name="icon" 
                                       id="iconInput"
                                       accept="image/*" />
                                <div class="form-text text-muted">Hỗ trợ định dạng: .jpg, .png, .jpeg, .webp. Nếu không chọn sẽ dùng ảnh mặc định.</div>
                                
                                <!-- Image Preview Container -->
                                <div class="image-preview mt-3" id="previewContainer">
                                    <span class="text-muted small" id="previewPlaceholder"><i class="fa fa-image me-1"></i>Xem trước ảnh</span>
                                    <img src="" alt="Preview" id="previewImage" style="display: none;" />
                                </div>
                            </div>

                            <!-- Action Buttons (Theo Slide 30: Thêm & Hủy) -->
                            <div class="d-flex align-items-center gap-2 pt-3 border-top">
                                <button type="submit" class="btn btn-primary px-4 py-2">
                                    <i class="fa fa-plus-circle me-1"></i> Thêm danh mục
                                </button>
                                <button type="reset" class="btn btn-outline-secondary px-4 py-2" id="resetBtn">
                                    <i class="fa fa-undo me-1"></i> Nhập lại
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-link text-decoration-none ms-auto text-secondary">
                                    <i class="fa fa-arrow-left me-1"></i> Quay lại danh sách
                                </a>
                            </div>

                        </form>

                    </div>
                </div>

                <!-- Info Box -->
                <div class="col-lg-4 mt-4 mt-lg-0">
                    <div class="card card-custom p-4 bg-light border">
                        <h5 class="fw-bold text-primary mb-3"><i class="fa fa-info-circle me-2"></i>Hướng dẫn</h5>
                        <ul class="text-muted small ps-3 mb-0" style="line-height: 1.8;">
                            <li>Tên danh mục là bắt buộc để hiển thị trên menu mua hàng.</li>
                            <li>Tập tin hình ảnh sẽ tự động được lưu trữ và phục vụ qua Servlet <code>/image?fname=...</code>.</li>
                            <li>Hệ thống lưu trữ ảnh an toàn và quản lý file bằng <code>Jakarta Servlet 6.1</code>.</li>
                        </ul>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Image Preview Logic
    const iconInput = document.getElementById('iconInput');
    const previewImage = document.getElementById('previewImage');
    const previewPlaceholder = document.getElementById('previewPlaceholder');
    const resetBtn = document.getElementById('resetBtn');

    iconInput.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                previewPlaceholder.style.display = 'none';
            }
            reader.readAsDataURL(file);
        }
    });

    resetBtn.addEventListener('click', function () {
        previewImage.src = '';
        previewImage.style.display = 'none';
        previewPlaceholder.style.display = 'block';
    });
</script>
</body>
</html>
