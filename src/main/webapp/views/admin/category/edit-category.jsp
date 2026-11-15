<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh Sửa Danh Mục - Shopping Service MVC</title>
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
        .current-image-box {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 8px;
            display: inline-block;
            background: #ffffff;
        }
        .current-image-box img {
            border-radius: 6px;
            object-fit: cover;
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

        <!-- Main Content Area (Theo Slide 31-32 & 36 - File 1) -->
        <main class="col-md-9 col-lg-10 content-wrapper">
            
            <div class="mb-4">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/home">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/category/list">Danh mục</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa</li>
                    </ol>
                </nav>
                <h2 class="fw-bold text-dark mb-1">Chỉnh sửa danh mục</h2>
                <p class="text-muted">Cập nhật thông tin và hình ảnh đại diện của danh mục #${category.id}</p>
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
                        
                        <!-- Form Edit Danh Mục (Theo Slide 31 - File 1) -->
                        <c:url value="/admin/category/edit" var="editUrl"></c:url>
                        <form role="form" action="${editUrl}" method="post" enctype="multipart/form-data">
                            
                            <!-- Hidden input category id -->
                            <input type="hidden" name="id" value="${category.id}" />

                            <!-- Tên danh mục -->
                            <div class="mb-4">
                                <label class="form-label fw-bold text-dark">Tên danh mục <span class="text-danger">*</span></label>
                                <input type="text" 
                                       class="form-control form-control-lg" 
                                       value="${category.name}" 
                                       name="catename" 
                                       required />
                            </div>

                            <!-- Ảnh đại diện hiện tại & Upload ảnh mới -->
                            <div class="mb-4">
                                <label class="form-label fw-bold text-dark d-block">Ảnh đại diện hiện tại</label>
                                <c:url value="/image?fname=${category.icon}" var="imgUrl"></c:url>
                                <div class="current-image-box mb-3 shadow-sm">
                                    <img class="img-responsive" 
                                         width="180" 
                                         height="130" 
                                         src="${imgUrl}" 
                                         alt="${category.name}"
                                         onerror="this.src='https://placehold.co/180x130?text=No+Image';">
                                    <div class="small text-muted mt-1 text-center">Tệp: <code>${category.icon}</code></div>
                                </div>

                                <label class="form-label fw-bold text-dark mt-2">Chọn ảnh mới thay thế (nếu muốn)</label>
                                <input type="file" 
                                       class="form-control" 
                                       name="icon" 
                                       id="editIconInput"
                                       accept="image/*" />
                                <div class="form-text text-muted">Nếu không chọn tệp mới, ảnh đại diện cũ sẽ được giữ nguyên.</div>
                            </div>

                            <!-- Action Buttons (Theo Slide 31: Edit & Reset) -->
                            <div class="d-flex align-items-center gap-2 pt-3 border-top">
                                <button type="submit" class="btn btn-primary px-4 py-2">
                                    <i class="fa fa-save me-1"></i> Cập nhật (Edit)
                                </button>
                                <button type="reset" class="btn btn-outline-secondary px-4 py-2">
                                    <i class="fa fa-undo me-1"></i> Khôi phục
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-link text-decoration-none ms-auto text-secondary">
                                    <i class="fa fa-arrow-left me-1"></i> Quay lại danh sách
                                </a>
                            </div>

                        </form>

                    </div>
                </div>

                <!-- Info Sidebar -->
                <div class="col-lg-4 mt-4 mt-lg-0">
                    <div class="card card-custom p-4 bg-light border">
                        <h5 class="fw-bold text-primary mb-3"><i class="fa fa-check-circle me-2"></i>Quy trình cập nhật</h5>
                        <ol class="text-muted small ps-3 mb-0" style="line-height: 1.8;">
                            <li>Hệ thống truy xuất thông tin danh mục theo ID: <code>${category.id}</code>.</li>
                            <li>Nếu có tải lên ảnh mới: hình ảnh cũ sẽ được tự động xóa khỏi đĩa cứng và lưu trữ tệp mới.</li>
                            <li>Dữ liệu tên và tên ảnh cập nhật vào SQL Server qua câu lệnh <code>UPDATE Category</code>.</li>
                            <li>Chuyển hướng về <code>/admin/category/list</code> với thông báo thành công.</li>
                        </ol>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
