<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Danh Mục - Admin Dashboard</title>
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
        /* Topbar Header (Slide 34/File 1) */
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
        .sidebar-menu li.active > a.bg-danger-custom {
            background: #e74c3c !important;
            border-left-color: #c0392b !important;
            color: #ffffff !important;
        }
        .sidebar-menu li a i {
            width: 26px;
            font-size: 16px;
        }
        .sub-menu {
            list-style: none;
            padding-left: 46px;
            background: #2c3b41;
        }
        .sub-menu li a {
            padding: 8px 10px;
            font-size: 14px;
            border-left: none !important;
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
        .table-category img {
            border-radius: 6px;
            border: 1px solid #dee2e6;
            object-fit: cover;
            background: #fafafa;
        }
        .badge-total {
            background: #e3f2fd;
            color: #0d6efd;
            font-weight: 600;
            padding: 6px 12px;
            border-radius: 20px;
        }
    </style>
</head>
<body>

<!-- Admin Header Bar (Theo giao diện mẫu Slide 34) -->
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
        <!-- Sidebar Menu (Theo Slide 34) -->
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
                    <ul class="sub-menu">
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/category/add" class="text-white">
                                <i class="fa fa-plus me-1"></i> Thêm danh mục mới
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/category/list" class="text-warning fw-bold">
                                <i class="fa fa-list me-1"></i> Danh sách danh mục
                            </a>
                        </li>
                    </ul>
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
            
            <!-- Page Title -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold text-dark mb-1">Quản lý danh mục</h2>
                    <p class="text-muted mb-0">Nơi bạn có thể quản lý danh mục của mình (Xem, Thêm, Sửa, Xóa)</p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-success shadow-sm">
                        <i class="fa fa-plus-circle me-1"></i> Thêm danh mục mới
                    </a>
                </div>
            </div>

            <!-- Success Alert Notification -->
            <c:if test="${successMsg != null || param.msg != null}">
                <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                    <i class="fa fa-check-circle me-2 fs-5"></i>
                    <div>${successMsg != null ? successMsg : param.msg}</div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Card Box List (Theo Slide 30 & 34) -->
            <div class="card card-custom">
                <div class="card-header bg-white py-3 border-bottom d-flex flex-wrap justify-content-between align-items-center">
                    <div class="d-flex align-items-center mb-2 mb-md-0">
                        <span class="fw-bold text-secondary me-2"><i class="fa fa-table me-1"></i>Danh sách danh mục</span>
                        <span class="badge-total">Tổng số: ${cateList != null ? cateList.size() : 0} danh mục</span>
                    </div>

                    <!-- Search Form -->
                    <form action="${pageContext.request.contextPath}/admin/category/list" method="get" class="d-flex align-items-center">
                        <div class="input-group">
                            <input type="text" 
                                   name="keyword" 
                                   class="form-control form-control-sm" 
                                   placeholder="Tìm kiếm danh mục..." 
                                   value="${keyword != null ? keyword : ''}">
                            <button class="btn btn-outline-primary btn-sm" type="submit">
                                <i class="fa fa-search"></i> Tìm
                            </button>
                            <c:if test="${keyword != null && !keyword.isEmpty()}">
                                <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-outline-secondary btn-sm" title="Xóa tìm kiếm">
                                    <i class="fa fa-times"></i>
                                </a>
                            </c:if>
                        </div>
                    </form>
                </div>

                <div class="card-body p-0">
                    <div class="table-responsive">
                        <!-- Table theo đúng Slide 29 - File 1 -->
                        <table class="table table-hover table-striped align-middle mb-0 table-category">
                            <thead class="table-light text-secondary">
                                <tr>
                                    <th class="text-center" style="width: 80px;">STT</th>
                                    <th style="width: 220px;">Hình ảnh</th>
                                    <th>Tên danh mục</th>
                                    <th class="text-center" style="width: 180px;">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${cateList != null && !cateList.isEmpty()}">
                                        <c:forEach items="${cateList}" var="cate" varStatus="STT">
                                            <tr class="odd gradeX">
                                                <td class="text-center fw-bold text-muted">${STT.index + 1}</td>
                                                <td>
                                                    <c:url value="/image?fname=${cate.icon}" var="imgUrl"></c:url>
                                                    <img height="100" width="140" src="${imgUrl}" alt="${cate.name}" onerror="this.src='https://placehold.co/140x100?text=No+Image';">
                                                </td>
                                                <td>
                                                    <span class="fw-semibold text-dark fs-6">${cate.name}</span>
                                                    <div class="small text-muted">ID: #${cate.id} | File: <code>${cate.icon}</code></div>
                                                </td>
                                                <td class="text-center">
                                                    <a href="<c:url value='/admin/category/edit?id=${cate.id}'/>" class="btn btn-sm btn-outline-primary me-1">
                                                        <i class="fa fa-edit me-1"></i>Sửa
                                                    </a>
                                                    <a href="<c:url value='/admin/category/delete?id=${cate.id}'/>" 
                                                       class="btn btn-sm btn-outline-danger" 
                                                       onclick="return confirm('Bạn có chắc chắn muốn xóa danh mục \"${cate.name}\" (ID: ${cate.id}) không?');">
                                                        <i class="fa fa-trash-alt me-1"></i>Xóa
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="4" class="text-center py-5 text-muted">
                                                <i class="fa fa-folder-open fs-1 d-block mb-3 text-secondary opacity-50"></i>
                                                Chưa có danh mục nào hoặc không tìm thấy danh mục phù hợp.
                                                <div class="mt-2">
                                                    <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-primary btn-sm">
                                                        <i class="fa fa-plus me-1"></i>Thêm danh mục mới ngay
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="card-footer bg-white text-muted small py-3 d-flex justify-content-between align-items-center">
                    <span>Hiển thị <strong>${cateList != null ? cateList.size() : 0}</strong> danh mục</span>
                    <span>Hệ thống Quản lý Bán hàng Servlet JDBC &copy; 2026</span>
                </div>
            </div>

        </main>
    </div>
</div>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
