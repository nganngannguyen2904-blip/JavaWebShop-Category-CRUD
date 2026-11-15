<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - JPA Hibernate CRUD</title>
    <!-- Bootstrap 5 CSS & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border: none; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
        .table-category img { border-radius: 6px; object-fit: cover; border: 1px solid #dee2e6; }
        .badge-status { font-size: 0.85rem; padding: 6px 12px; border-radius: 20px; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/categories">
            <i class="fa fa-layer-group text-primary me-2"></i>JPA 3.0 - HIBERNATE CRUD CATEGORY
        </a>
        <div class="d-flex align-items-center gap-3">
            <c:choose>
                <c:when test="${not empty sessionScope.account}">
                    <span class="text-light small">
                        <i class="fa fa-user-circle text-warning me-1"></i>Xin chào, <strong>${sessionScope.account.fullname}</strong>
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-danger">
                        <i class="fa fa-sign-out-alt me-1"></i>Đăng xuất
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-outline-light">
                        <i class="fa fa-sign-in-alt me-1"></i>Đăng nhập
                    </a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-sm btn-primary">
                        <i class="fa fa-user-plus me-1"></i>Đăng ký
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<div class="container my-4">
    <!-- Header Controls -->
    <div class="card card-custom p-4 mb-4 bg-white">
        <div class="row align-items-center">
            <div class="col-md-6 mb-3 mb-md-0">
                <h3 class="fw-bold text-dark mb-1">
                    <i class="fa fa-list text-primary me-2"></i>Danh Sách Danh Mục (Category List)
                </h3>
                <p class="text-muted mb-0 small">Bài tập: Xây dựng CRUD Category bằng JPA 3.0 & Hibernate ORM</p>
            </div>
            <div class="col-md-6 text-md-end">
                <a href="<c:url value='/admin/category/add'/>" class="btn btn-success shadow-sm fw-semibold">
                    <i class="fa fa-plus-circle me-1"></i>Thêm Category Mới
                </a>
            </div>
        </div>

        <hr class="my-3">

        <!-- Search Bar -->
        <div class="row">
            <div class="col-md-6">
                <form action="<c:url value='/admin/categories'/>" method="get" class="d-flex gap-2">
                    <div class="input-group">
                        <input type="text" name="keyword" class="form-control" placeholder="Tìm theo tên Category..." value="${keyword != null ? keyword : ''}">
                        <button type="submit" class="btn btn-outline-primary">
                            <i class="fa fa-search me-1"></i>Tìm kiếm
                        </button>
                    </div>
                    <c:if test="${keyword != null && !keyword.isEmpty()}">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-outline-secondary">Xóa tìm</a>
                    </c:if>
                </form>
            </div>
            <div class="col-md-6 text-md-end align-self-center mt-2 mt-md-0">
                <span class="badge bg-secondary p-2">Tổng số: <strong>${listcate != null ? listcate.size() : 0}</strong> danh mục</span>
            </div>
        </div>
    </div>

    <!-- Table List (Theo chuẩn tài liệu và Bootstrap) -->
    <div class="card card-custom p-0 bg-white overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover table-striped align-middle mb-0 table-category">
                <thead class="table-primary text-secondary">
                    <tr>
                        <th class="text-center" style="width: 70px;">STT</th>
                        <th style="width: 140px;">Images</th>
                        <th>Category Name</th>
                        <th class="text-center" style="width: 140px;">Status</th>
                        <th class="text-center" style="width: 180px;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${listcate != null && !listcate.isEmpty()}">
                            <c:forEach items="${listcate}" var="cate" varStatus="STT">
                                <tr>
                                    <td class="text-center fw-bold text-muted">${STT.index + 1}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cate.images != null && cate.images.startsWith('http')}">
                                                <c:url value="${cate.images}" var="imgUrl"></c:url>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url value="/image?fname=${cate.images}" var="imgUrl"></c:url>
                                            </c:otherwise>
                                        </c:choose>
                                        <img height="80" width="120" src="${imgUrl}" alt="${cate.categoryname}" 
                                             onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';" />
                                    </td>
                                    <td>
                                        <span class="fw-bold text-dark fs-6">${cate.categoryname}</span>
                                        <div class="small text-muted">ID: #${cate.categoryid} | File: <code>${cate.images}</code></div>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${cate.status == 1}">
                                                <span class="badge bg-success bg-opacity-10 text-success badge-status border border-success">
                                                    <i class="fa fa-check-circle me-1"></i>Hoạt động
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger bg-opacity-10 text-danger badge-status border border-danger">
                                                    <i class="fa fa-lock me-1"></i>Khóa
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="<c:url value='/admin/category/edit?id=${cate.categoryid}'/>" class="btn btn-sm btn-outline-primary me-1">
                                            <i class="fa fa-edit me-1"></i>Sửa
                                        </a>
                                        <a href="<c:url value='/admin/category/delete?id=${cate.categoryid}'/>" 
                                           class="btn btn-sm btn-outline-danger" 
                                           onclick="return confirm('Bạn có chắc chắn muốn xóa Category: \"${cate.categoryname}\" (ID: ${cate.categoryid}) không?');">
                                            <i class="fa fa-trash-alt me-1"></i>Xóa
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="fa fa-folder-open fs-1 d-block mb-3 text-secondary opacity-50"></i>
                                    Chưa có danh mục nào hoặc không tìm thấy kết quả phù hợp.
                                    <div class="mt-2">
                                        <a href="<c:url value='/admin/category/add'/>" class="btn btn-sm btn-primary">
                                            <i class="fa fa-plus me-1"></i>Thêm Category Đầu Tiên
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
</div>

<footer class="text-center py-4 text-muted small">
    &copy; 2026 BaiTap01_JPA_CRUD_Category - Đồ án Lập trình Web - HCMUTE
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
