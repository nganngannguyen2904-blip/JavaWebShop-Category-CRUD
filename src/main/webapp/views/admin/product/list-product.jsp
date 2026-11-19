<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Sản Phẩm & Tồn Kho - Admin Dashboard</title>
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
            width: 60px;
            height: 60px;
            border-radius: 50%;
            border: 2px solid #0088cc;
            object-fit: cover;
        }
        .sidebar-menu {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .sidebar-menu li a {
            display: block;
            padding: 12px 20px;
            color: #b8c7ce;
            text-decoration: none;
            border-left: 3px solid transparent;
            transition: all 0.2s;
        }
        .sidebar-menu li a:hover, .sidebar-menu li.active a {
            color: #ffffff;
            background: #1e282c;
            border-left-color: #0088cc;
        }
        .product-thumb {
            width: 48px;
            height: 48px;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #dee2e6;
        }
        .stat-card-sm {
            background: #ffffff;
            border-radius: 10px;
            padding: 16px 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            border-left: 4px solid #0088cc;
        }
    </style>
</head>
<body>

<div>
    <!-- Thống kê kho hàng -->
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="stat-card-sm" style="border-left-color: #0d6efd;">
                        <div class="text-muted small">Tổng Sản Phẩm Trong CSDL</div>
                        <div class="fs-4 fw-bold text-primary">${totalProducts} sản phẩm</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card-sm" style="border-left-color: #198754;">
                        <div class="text-muted small">Số Danh Mục Đang Hoạt Động</div>
                        <div class="fs-4 fw-bold text-success">${categoryList.size()} danh mục</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card-sm" style="border-left-color: #dc3545;">
                        <div class="text-muted small">Cảnh Báo Hết Hàng (Tồn = 0)</div>
                        <div class="fs-4 fw-bold text-danger">${outOfStockCount} sản phẩm</div>
                    </div>
                </div>
            </div>

            <c:if test="${successMessage != null}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fa fa-check-circle me-2"></i>${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Card danh sách sản phẩm -->
            <div class="card border-0 shadow-sm rounded-3">
                <div class="card-header bg-white py-3">
                    <div class="row align-items-center g-2">
                        <div class="col-md-4">
                            <h5 class="fw-bold mb-0 text-dark">
                                <i class="fa fa-list text-primary me-2"></i>Danh Sách Sản Phẩm & Kho
                            </h5>
                        </div>
                        <div class="col-md-8">
                            <form class="d-flex justify-content-md-end gap-2" action="${pageContext.request.contextPath}/admin/product/list" method="get">
                                <select name="cateid" class="form-select form-select-sm" style="width: 180px;" onchange="this.form.submit()">
                                    <option value="">-- Tất cả danh mục --</option>
                                    <c:forEach items="${categoryList}" var="c">
                                        <option value="${c.id}" ${selectedCateId == c.id ? 'selected' : ''}>${c.name}</option>
                                    </c:forEach>
                                </select>
                                <input type="text" name="keyword" value="${keyword}" class="form-control form-control-sm" 
                                       placeholder="Tìm theo tên..." style="width: 200px;">
                                <button type="submit" class="btn btn-sm btn-primary"><i class="fa fa-search"></i></button>
                                <a href="${pageContext.request.contextPath}/admin/product/list" class="btn btn-sm btn-outline-secondary" title="Reset">
                                    <i class="fa fa-redo"></i>
                                </a>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light small text-muted">
                                <tr>
                                    <th style="width: 60px;" class="text-center">Mã</th>
                                    <th style="width: 70px;">Hình ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Danh mục</th>
                                    <th>Giá niêm yết</th>
                                    <th style="width: 220px;">Số Lượng Trong Kho (Sửa nhanh)</th>
                                    <th style="width: 90px;" class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${productList}" var="p">
                                    <tr>
                                        <td class="text-center fw-bold text-secondary">#${p.id}</td>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/image?fname=${p.image}" 
                                                 alt="${p.name}" class="product-thumb"
                                                 onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                                        </td>
                                        <td>
                                            <div class="fw-semibold text-dark">${p.name}</div>
                                            <small class="text-muted text-truncate d-inline-block" style="max-width: 320px;">${p.description}</small>
                                        </td>
                                        <td>
                                            <span class="badge bg-primary bg-opacity-10 text-primary">${p.categoryName}</span>
                                        </td>
                                        <td class="fw-bold text-danger">${p.formattedPrice}</td>
                                        <td>
                                            <!-- Form cập nhật số lượng tồn kho nhanh 1-Click -->
                                            <form action="${pageContext.request.contextPath}/admin/product/update-stock" method="post" class="d-flex align-items-center gap-1">
                                                <input type="hidden" name="id" value="${p.id}">
                                                <input type="number" name="quantity" value="${p.quantity}" min="0" 
                                                       class="form-control form-control-sm text-center fw-bold ${p.quantity <= 0 ? 'border-danger text-danger' : 'text-dark'}" 
                                                       style="width: 80px;" required>
                                                <button type="submit" class="btn btn-sm btn-outline-success" title="Lưu số lượng mới">
                                                    <i class="fa fa-check"></i> Lưu
                                                </button>
                                                <c:choose>
                                                    <c:when test="${p.quantity <= 0}">
                                                        <span class="badge bg-danger ms-1">Hết hàng</span>
                                                    </c:when>
                                                    <c:when test="${p.quantity < 10}">
                                                        <span class="badge bg-warning text-dark ms-1">Sắp hết</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-success ms-1">Đủ hàng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/admin/product/delete?id=${p.id}" 
                                               class="btn btn-sm btn-outline-danger" title="Xóa sản phẩm"
                                               onclick="return confirm('Bạn có chắc muốn xóa sản phẩm #${p.id} - ${p.name}?');">
                                                <i class="fa fa-trash-alt"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
</div>
</body>
</html>
