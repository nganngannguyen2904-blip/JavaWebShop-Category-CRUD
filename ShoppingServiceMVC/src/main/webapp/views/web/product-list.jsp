<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${selectedCateName} - Shopping Service MVC</title>
    <!-- Google Fonts Inter / Be Vietnam Pro -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background-color: #f6f8fa;
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            color: #212529;
        }
        .main-navbar {
            background: #ffffff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.06);
        }
        .filter-sidebar {
            background: #ffffff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            position: sticky;
            top: 90px;
        }
        .category-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 14px;
            border-radius: 8px;
            color: #495057;
            text-decoration: none;
            transition: all 0.2s ease;
            font-weight: 500;
            margin-bottom: 4px;
        }
        .category-item:hover {
            background-color: #eef2ff;
            color: #0d6efd;
            padding-left: 18px;
        }
        .category-item.active {
            background-color: #0d6efd;
            color: #ffffff !important;
            font-weight: 600;
        }
        .product-card {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e9ecef;
            overflow: hidden;
            transition: all 0.3s ease;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 24px rgba(13, 110, 253, 0.12);
            border-color: #b6d4fe;
        }
        .product-img-wrapper {
            position: relative;
            background: #f8f9fa;
            overflow: hidden;
            aspect-ratio: 1/1;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .product-img-wrapper img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.4s ease;
        }
        .product-card:hover .product-img-wrapper img {
            transform: scale(1.05);
        }
        .badge-category {
            position: absolute;
            top: 10px;
            left: 10px;
            background: rgba(13, 110, 253, 0.85);
            backdrop-filter: blur(4px);
            color: #fff;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
        }
        .product-body {
            padding: 16px;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        .product-title {
            font-size: 15px;
            font-weight: 600;
            color: #212529;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            height: 44px;
            text-decoration: none;
            line-height: 1.4;
        }
        .product-title:hover {
            color: #0d6efd;
        }
        .product-price {
            font-size: 18px;
            font-weight: 700;
            color: #dc3545;
        }
        .stars {
            color: #ffc107;
            font-size: 12px;
        }
        .footer {
            background: #1e242b;
            color: #adb5bd;
            padding: 45px 0 20px;
            margin-top: 60px;
        }
    </style>
</head>
<body>

<!-- 1. Topbar Navigation (Theo Slide 20 - File 2) -->
<jsp:include page="/views/common/topbar.jsp" />

<!-- 2. Main Navbar -->
<nav class="navbar navbar-expand-lg main-navbar sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary fs-4" href="${pageContext.request.contextPath}/home">
            <i class="fa fa-shopping-bag me-2"></i>Shopping<span class="text-dark">Service</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                <li class="nav-item">
                    <a class="nav-link fw-semibold" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-semibold text-primary" href="${pageContext.request.contextPath}/products">Sản phẩm</a>
                </li>
                <c:if test="${sessionScope.account != null && (sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2)}">
                    <li class="nav-item">
                        <a class="nav-link fw-semibold text-danger" href="${pageContext.request.contextPath}/admin/category/list">
                            <i class="fa fa-cog me-1"></i>Quản trị Danh mục
                        </a>
                    </li>
                </c:if>
            </ul>

            <!-- Search Form -->
            <form class="d-flex" action="${pageContext.request.contextPath}/products" method="get">
                <c:if test="${selectedCateId != null}">
                    <input type="hidden" name="cateid" value="${selectedCateId}">
                </c:if>
                <div class="input-group">
                    <input class="form-control form-control-sm" type="search" name="keyword" 
                           value="${keyword}" placeholder="Tìm theo tên sản phẩm..." aria-label="Search">
                    <button class="btn btn-outline-primary btn-sm" type="submit">
                        <i class="fa fa-search"></i>
                    </button>
                </div>
            </form>
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary btn-sm position-relative ms-2" title="Xem giỏ hàng">
                <i class="fa fa-shopping-cart"></i>
                <c:if test="${sessionScope.cart != null && sessionScope.cart.totalQuantity > 0}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">${sessionScope.cart.totalQuantity}</span>
                </c:if>
            </a>

            <!-- User Auth Profile -->
            <c:choose>
                <c:when test="${sessionScope.account != null}">
                    <div class="dropdown ms-2">
                        <button class="btn btn-outline-secondary btn-sm dropdown-toggle fw-semibold d-flex align-items-center" type="button" data-bs-toggle="dropdown">
                            <i class="fa fa-user-circle me-1 text-primary"></i>
                            <span class="d-none d-md-inline"><c:out value="${sessionScope.account.fullName}"/></span>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                            <li class="dropdown-header small text-muted">
                                <strong><c:out value="${sessionScope.account.userName}"/></strong>
                                (<span class="badge ${sessionScope.account.roleid == 1 ? 'bg-danger' : 'bg-primary'}">${sessionScope.account.roleid == 1 ? 'Admin' : 'Khách hàng'}</span>)
                            </li>
                            <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                <li><a class="dropdown-item text-danger fw-semibold" href="${pageContext.request.contextPath}/admin/home"><i class="fa fa-cogs me-2"></i>Trang Quản Trị</a></li>
                            </c:if>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/change-password"><i class="fa fa-key me-2 text-warning"></i>Đổi mật khẩu</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger fw-semibold" href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out me-2"></i>Đăng xuất</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="d-flex align-items-center gap-1 ms-2">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-outline-primary fw-semibold">
                            <i class="fa fa-sign-in me-1"></i>Đăng nhập
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-sm btn-primary fw-semibold text-white">
                            <i class="fa fa-user-plus me-1"></i>Đăng ký
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<!-- 3. Breadcrumb -->
<div class="bg-white border-bottom py-2">
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-0 small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Trang chủ</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products" class="text-decoration-none">Sản phẩm</a></li>
                <li class="breadcrumb-item active text-primary fw-semibold" aria-current="page">${selectedCateName}</li>
            </ol>
        </nav>
    </div>
</div>

<!-- 4. Main Content (Sidebar + Product Grid) -->
<div class="container py-4">
    <div class="row g-4">
        <!-- Sidebar lọc danh mục -->
        <div class="col-lg-3">
            <div class="filter-sidebar">
                <h5 class="fw-bold mb-3 pb-2 border-bottom">
                    <i class="fa fa-bars-staggered text-primary me-2"></i>Danh Mục Sản Phẩm
                </h5>
                <div class="category-menu">
                    <a href="${pageContext.request.contextPath}/products" 
                       class="category-item ${selectedCateId == null ? 'active' : ''}">
                        <span><i class="fa fa-border-all me-2"></i>Tất cả sản phẩm</span>
                    </a>
                    <c:forEach items="${categoryList}" var="cat">
                        <a href="${pageContext.request.contextPath}/products?cateid=${cat.id}" 
                           class="category-item ${selectedCateId == cat.id ? 'active' : ''}">
                            <span><i class="fa fa-tag me-2"></i>${cat.name}</span>
                            <span class="badge ${selectedCateId == cat.id ? 'bg-light text-primary' : 'bg-secondary bg-opacity-10 text-secondary'} rounded-pill">#${cat.id}</span>
                        </a>
                    </c:forEach>
                </div>

                <div class="mt-4 pt-3 border-top">
                    <h6 class="fw-bold mb-2"><i class="fa fa-shield-alt text-success me-2"></i>Cam kết mua sắm</h6>
                    <ul class="list-unstyled small text-muted mb-0">
                        <li class="mb-2"><i class="fa fa-check text-success me-2"></i>100% Sản phẩm chính hãng</li>
                        <li class="mb-2"><i class="fa fa-truck text-primary me-2"></i>Giao hàng toàn quốc</li>
                        <li class="mb-2"><i class="fa fa-sync-alt text-warning me-2"></i>Đổi trả trong 7 ngày</li>
                        <li><i class="fa fa-headset text-info me-2"></i>Hỗ trợ 24/7</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Danh sách sản phẩm -->
        <div class="col-lg-9">
            <!-- Header bar -->
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 bg-white p-3 rounded-3 shadow-sm border">
                <div>
                    <h4 class="fw-bold text-dark mb-0">${selectedCateName}</h4>
                    <span class="text-muted small">Hiển thị ${totalCount} sản phẩm sẵn có</span>
                </div>
                <div class="d-flex align-items-center gap-2 mt-2 mt-sm-0">
                    <label class="text-muted small fw-semibold">Sắp xếp:</label>
                    <select class="form-select form-select-sm" style="width: auto;" onchange="location = this.value;">
                        <option value="${pageContext.request.contextPath}/products?cateid=${selectedCateId != null ? selectedCateId : ''}&keyword=${keyword}" ${empty sort ? 'selected' : ''}>Mặc định</option>
                        <option value="${pageContext.request.contextPath}/products?cateid=${selectedCateId != null ? selectedCateId : ''}&keyword=${keyword}&sort=asc" ${sort == 'asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                        <option value="${pageContext.request.contextPath}/products?cateid=${selectedCateId != null ? selectedCateId : ''}&keyword=${keyword}&sort=desc" ${sort == 'desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                    </select>
                </div>
            </div>

            <!-- Grid sản phẩm -->
            <div class="row g-3">
                <c:choose>
                    <c:when test="${productList != null && !productList.isEmpty()}">
                        <c:forEach items="${productList}" var="p">
                            <div class="col-md-4 col-sm-6">
                                <div class="product-card">
                                    <div class="product-img-wrapper">
                                        <span class="badge-category">${p.categoryName}</span>
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${p.id}" class="w-100 h-100">
                                            <img src="${pageContext.request.contextPath}/image?fname=${p.image}" 
                                                 alt="${p.name}"
                                                 onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                                        </a>
                                    </div>
                                    <div class="product-body">
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${p.id}" 
                                           class="product-title" title="${p.name}">${p.name}</a>
                                        
                                        <div class="d-flex align-items-center justify-content-between mb-2">
                                            <div class="stars">
                                                <i class="fa fa-star"></i>
                                                <i class="fa fa-star"></i>
                                                <i class="fa fa-star"></i>
                                                <i class="fa fa-star"></i>
                                                <i class="fa fa-star"></i>
                                            </div>
                                            <c:choose>
                                                <c:when test="${p.quantity > 0}">
                                                    <span class="badge bg-success bg-opacity-10 text-success small">Còn ${p.quantity} SP</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger small">Hết hàng</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="mt-auto">
                                            <div class="product-price mb-3">${p.formattedPrice}</div>
                                            <div class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/product/detail?id=${p.id}" 
                                                   class="btn btn-outline-primary btn-sm fw-semibold flex-grow-1">
                                                    <i class="fa fa-eye me-1"></i>Chi tiết
                                                </a>
                                                <c:choose>
                                                    <c:when test="${p.quantity > 0}">
                                                        <a href="${pageContext.request.contextPath}/cart/add?id=${p.id}&quantity=1" 
                                                           class="btn btn-primary btn-sm fw-semibold" title="Thêm vào giỏ">
                                                            <i class="fa fa-cart-plus"></i>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button class="btn btn-secondary btn-sm" disabled title="Tạm hết hàng">
                                                            <i class="fa fa-ban"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12 py-5 text-center">
                            <div class="p-5 bg-white rounded-3 shadow-sm border">
                                <i class="fa fa-box-open text-muted fa-4x mb-3"></i>
                                <h5 class="fw-bold text-secondary">Không tìm thấy sản phẩm nào!</h5>
                                <p class="text-muted small">Hãy thử tìm kiếm với từ khóa khác hoặc quay lại danh mục sản phẩm.</p>
                                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-sm">
                                    <i class="fa fa-arrow-left me-1"></i>Xem tất cả sản phẩm
                                </a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="footer">
    <div class="container">
        <div class="row g-4">
            <div class="col-md-4">
                <h5 class="text-white fw-bold"><i class="fa fa-shopping-bag me-2"></i>Shopping Service MVC</h5>
                <p class="small text-secondary">Hệ thống thương mại điện tử mô hình MVC 3 lớp theo chuẩn tài liệu môn học Lập trình Web - HCMUTE.</p>
            </div>
            <div class="col-md-4">
                <h6 class="text-white fw-bold mb-3">Danh Mục Nổi Bật</h6>
                <ul class="list-unstyled small">
                    <c:forEach items="${categoryList}" var="c" begin="0" end="3">
                        <li class="mb-1"><a href="${pageContext.request.contextPath}/products?cateid=${c.id}" class="text-secondary text-decoration-none hover-white">${c.name}</a></li>
                    </c:forEach>
                </ul>
            </div>
            <div class="col-md-4">
                <h6 class="text-white fw-bold mb-3">Thông Tin Giảng Viên</h6>
                <p class="small text-secondary mb-1">GVHD: <strong>ThS. Nguyễn Hữu Trung</strong></p>
                <p class="small text-secondary mb-1">Môn học: Lập trình Web (WEBPR330479)</p>
                <p class="small text-secondary">Trường: ĐH Sư Phạm Kỹ Thuật TP.HCM (HCMUTE)</p>
            </div>
        </div>
        <hr class="border-secondary mt-4">
        <div class="text-center small text-secondary">
            &copy; 2026 Shopping Service MVC. Hoàn thành theo chuẩn Slide 06 (MVC 3-tier) & Slide 14 (Servlet JDBC CRUD).
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
