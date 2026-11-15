<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng Điều Khiển Quản Trị - Admin Dashboard</title>
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
        .stat-card {
            border: none;
            border-radius: 12px;
            color: #ffffff;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s;
        }
        .stat-card:hover {
            transform: translateY(-4px);
        }
        .stat-card .stat-icon {
            font-size: 40px;
            opacity: 0.8;
        }
        .stat-blue { background: linear-gradient(135deg, #0088cc, #005580); }
        .stat-green { background: linear-gradient(135deg, #2ecc71, #27ae60); }
        .stat-orange { background: linear-gradient(135deg, #e67e22, #d35400); }
        .stat-purple { background: linear-gradient(135deg, #9b59b6, #8e44ad); }
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/home">
                        <i class="fa fa-gauge"></i> <span>Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/category/list">
                        <i class="fa fa-folder-open"></i> <span>Quản lý Danh mục</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/product/list">
                        <i class="fa fa-boxes"></i> <span>Quản lý Sản phẩm & Kho</span>
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
                <h2 class="fw-bold text-dark mb-1">Tổng quan quản trị hệ thống</h2>
                <p class="text-muted">Chào mừng trở lại! Dưới đây là các chỉ số hoạt động và phím tắt nhanh.</p>
            </div>

            <!-- Stats Grid -->
            <div class="row g-4 mb-4">
                <div class="col-md-6 col-lg-3">
                    <div class="stat-card stat-blue">
                        <div>
                            <div class="small text-uppercase opacity-75 fw-bold">Danh mục sản phẩm</div>
                            <h3 class="fw-bold mb-0 mt-1">CRUD</h3>
                            <a href="${pageContext.request.contextPath}/admin/category/list" class="text-white small text-decoration-none mt-2 d-inline-block">
                                Quản lý ngay <i class="fa fa-arrow-right ms-1"></i>
                            </a>
                        </div>
                        <div class="stat-icon"><i class="fa fa-th-large"></i></div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-3">
                    <div class="stat-card stat-green">
                        <div>
                            <div class="small text-uppercase opacity-75 fw-bold">Kiến trúc</div>
                            <h3 class="fw-bold mb-0 mt-1">MVC 3-Tier</h3>
                            <span class="small opacity-75 mt-2 d-inline-block">Controller - Service - DAO</span>
                        </div>
                        <div class="stat-icon"><i class="fa fa-layer-group"></i></div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-3">
                    <div class="stat-card stat-orange">
                        <div>
                            <div class="small text-uppercase opacity-75 fw-bold">Cơ sở dữ liệu</div>
                            <h3 class="fw-bold mb-0 mt-1">SQL Server</h3>
                            <span class="small opacity-75 mt-2 d-inline-block">Database: BanHang</span>
                        </div>
                        <div class="stat-icon"><i class="fa fa-database"></i></div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-3">
                    <div class="stat-card stat-purple">
                        <div>
                            <div class="small text-uppercase opacity-75 fw-bold">Tài khoản</div>
                            <h3 class="fw-bold mb-0 mt-1">Admin / User</h3>
                            <span class="small opacity-75 mt-2 d-inline-block">Role-based Access</span>
                        </div>
                        <div class="stat-icon"><i class="fa fa-users-cog"></i></div>
                    </div>
                </div>
            </div>

            <!-- Quick Action Cards -->
            <div class="row g-4 mb-4">
                <div class="col-lg-6">
                    <div class="card border-0 shadow-sm p-4 h-100 bg-white rounded-3">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-boxes text-primary me-2"></i>Quản lý Sản Phẩm & Kho Hàng</h5>
                        <p class="text-muted small">Kiểm soát số lượng tồn kho của từng sản phẩm, phát hiện sản phẩm sắp hết hoặc hết hàng và cập nhật nhanh 1-Click:</p>
                        <div class="d-flex flex-wrap gap-2">
                            <a href="${pageContext.request.contextPath}/admin/product/list" class="btn btn-primary">
                                <i class="fa fa-list me-1"></i>Xem & Quản lý Kho Hàng
                            </a>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6">
                    <div class="card border-0 shadow-sm p-4 h-100 bg-white rounded-3">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-bolt text-warning me-2"></i>Thao tác nhanh Danh mục</h5>
                        <p class="text-muted small">Hệ thống hỗ trợ quản lý danh mục đầy đủ các chức năng theo tài liệu giảng dạy:</p>
                        <div class="d-flex flex-wrap gap-2">
                            <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-outline-primary">
                                <i class="fa fa-list me-1"></i>Xem danh sách danh mục
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-outline-success">
                                <i class="fa fa-plus-circle me-1"></i>Thêm danh mục mới
                            </a>
                        </div>
                    </div>
                </div>
            </div>

                <div class="col-lg-6">
                    <div class="card border-0 shadow-sm p-4 h-100 bg-white rounded-3">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-user-shield text-primary me-2"></i>Thông tin phiên làm việc</h5>
                        <ul class="list-unstyled mb-0 small" style="line-height: 2;">
                            <li><strong>Tên hiển thị:</strong> ${sessionScope.account.fullName}</li>
                            <li><strong>Tên tài khoản:</strong> <code>${sessionScope.account.userName}</code></li>
                            <li><strong>Email:</strong> ${sessionScope.account.email}</li>
                            <li><strong>Vai trò:</strong> <span class="badge bg-danger">Administrator (Role 1)</span></li>
                        </ul>
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
