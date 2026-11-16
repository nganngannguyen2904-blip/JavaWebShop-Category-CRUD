<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Service MVC - Cửa Hàng Mua Sắm Trực Tuyến</title>
    <!-- Google Fonts Be Vietnam Pro cho tiếng Việt sắc nét 100% -->
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
        .hero-banner {
            background: linear-gradient(135deg, #0d6efd 0%, #003366 100%);
            color: #ffffff;
            padding: 60px 0;
            border-radius: 0 0 24px 24px;
            margin-bottom: 40px;
        }
        .category-card {
            border: 1px solid #eef0f2;
            border-radius: 14px;
            overflow: hidden;
            background: #ffffff;
            box-shadow: 0 4px 15px rgba(0,0,0,0.04);
            transition: all 0.3s ease;
            text-align: center;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .category-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 12px 25px rgba(13, 110, 253, 0.15);
            border-color: #b6d4fe;
        }
        .category-card img {
            height: 160px;
            width: 100%;
            object-fit: cover;
            border-bottom: 1px solid #f1f3f5;
        }
        .category-card .card-body {
            padding: 18px 16px;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
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
            padding: 45px 0 20px 0;
            margin-top: 60px;
        }
        /* Style 10 Video mới nhất */
        .home-video-card {
            border: 1px solid #eef0f2;
            border-radius: 12px;
            overflow: hidden;
            background: #ffffff;
            box-shadow: 0 4px 12px rgba(0,0,0,0.04);
            transition: all 0.3s ease;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .home-video-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(13, 110, 253, 0.15);
            border-color: #b6d4fe;
        }
        .home-video-thumb-wrap {
            position: relative;
            width: 100%;
            padding-top: 56.25%;
            background-color: #000;
            overflow: hidden;
            display: block;
        }
        .home-video-thumb {
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 100%;
            object-fit: cover;
            transition: transform 0.3s ease;
        }
        .home-video-card:hover .home-video-thumb {
            transform: scale(1.06);
        }
        .home-play-badge {
            position: absolute;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            width: 40px; height: 40px;
            background: rgba(13, 110, 253, 0.85);
            color: white;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 16px;
            transition: all 0.2s ease;
        }
        .home-video-card:hover .home-play-badge {
            background: #0d6efd;
            transform: translate(-50%, -50%) scale(1.15);
        }
        .home-video-title {
            font-size: 0.92rem;
            font-weight: 600;
            color: #212529;
            text-decoration: none;
            line-height: 1.35;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.2s ease;
        }
        .home-video-title:hover {
            color: #0d6efd;
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
                    <a class="nav-link active fw-semibold text-primary" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-semibold" href="${pageContext.request.contextPath}/products">Sản phẩm</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-semibold" href="#categoriesSection">Danh mục</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-semibold text-danger" href="${pageContext.request.contextPath}/video">
                        <i class="fa fa-play-circle me-1"></i>Video Đa Phương Tiện
                    </a>
                </li>
                <c:if test="${sessionScope.account != null && (sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2)}">
                    <li class="nav-item">
                        <a class="nav-link fw-semibold text-danger" href="${pageContext.request.contextPath}/admin/category/list">
                            <i class="fa fa-cog me-1"></i>Quản trị Danh mục
                        </a>
                    </li>
                </c:if>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <form class="d-flex" action="${pageContext.request.contextPath}/products" method="get">
                    <div class="input-group">
                        <input class="form-control form-control-sm" type="search" name="keyword" placeholder="Tìm sản phẩm..." aria-label="Search">
                        <button class="btn btn-outline-primary btn-sm" type="submit"><i class="fa fa-search"></i></button>
                    </div>
                </form>
                <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary btn-sm position-relative ms-2" title="Giỏ hàng">
                    <i class="fa fa-shopping-cart"></i>
                    <c:if test="${sessionScope.cart != null && sessionScope.cart.totalQuantity > 0}">
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">${sessionScope.cart.totalQuantity}</span>
                    </c:if>
                </a>

                <!-- Khu vực Tài khoản Đăng nhập / Đăng ký / Đổi mật khẩu -->
                <c:choose>
                    <c:when test="${sessionScope.account != null}">
                        <div class="dropdown ms-2">
                            <button class="btn btn-outline-secondary btn-sm dropdown-toggle fw-semibold d-flex align-items-center" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fa fa-user-circle me-1 text-primary"></i>
                                <span class="d-none d-md-inline"><c:out value="${sessionScope.account.fullName}"/></span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                                <li class="dropdown-header small text-muted">
                                    Tài khoản: <strong><c:out value="${sessionScope.account.userName}"/></strong><br>
                                    Vai trò: <span class="badge ${sessionScope.account.roleid == 1 ? 'bg-danger' : (sessionScope.account.roleid == 2 ? 'bg-warning text-dark' : 'bg-primary')}">${sessionScope.account.roleid == 1 ? 'Admin' : (sessionScope.account.roleid == 2 ? 'Manager' : 'Khách hàng')}</span>
                                </li>
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                    <li><a class="dropdown-item text-danger fw-semibold" href="${pageContext.request.contextPath}/admin/home"><i class="fa fa-cogs me-2"></i>Trang Quản Trị</a></li>
                                </c:if>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/video"><i class="fa fa-video me-2 text-primary"></i>Video của tôi</a></li>
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
    </div>
</nav>

<!-- 3. Hero Banner -->
<section class="hero-banner">
    <div class="container text-center">
        <span class="badge bg-warning text-dark mb-2 px-3 py-2 fw-semibold">ĐỒ ÁN LẬP TRÌNH WEB (WEBPR330479)</span>
        <h1 class="display-5 fw-bold mb-3">Chào Mừng Đến Với Shopping Service MVC</h1>
        <p class="lead mb-4 mx-auto text-light opacity-90" style="max-width: 750px;">
            Hệ thống mua sắm ứng dụng mô hình Kiến trúc 3 tầng (Three Tier Architecture) kết hợp Servlet - JDBC kết nối cơ sở dữ liệu SQL Server.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-light btn-lg px-4 fw-semibold text-primary">
                <i class="fa fa-shopping-basket me-2"></i>Xem tất cả sản phẩm
            </a>
            <a href="#categoriesSection" class="btn btn-outline-light btn-lg px-4">
                <i class="fa fa-th-large me-2"></i>Khám phá danh mục
            </a>
        </div>
    </div>
</section>

<!-- 4. Danh Mục Sản Phẩm (Category Grid) -->
<section class="container mb-5" id="categoriesSection">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold text-dark mb-1"><i class="fa fa-tags text-primary me-2"></i>Danh Mục Sản Phẩm</h3>
            <p class="text-muted mb-0">Các nhóm sản phẩm nổi bật đang được phân phối trên hệ thống</p>
        </div>
        <c:if test="${sessionScope.account != null && (sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2)}">
            <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-sm btn-outline-primary">
                <i class="fa fa-edit me-1"></i>Quản lý danh mục
            </a>
        </c:if>
    </div>

    <div class="row g-4">
        <c:choose>
            <c:when test="${cateList != null && !cateList.isEmpty()}">
                <c:forEach items="${cateList}" var="c">
                    <div class="col-sm-6 col-md-4 col-lg-4">
                        <div class="category-card">
                            <c:url value="/image?fname=${c.icon}" var="imgUrl"></c:url>
                            <img src="${imgUrl}" alt="${c.name}" onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                            <div class="card-body">
                                <h5 class="fw-bold text-dark mb-1">${c.name}</h5>
                                <span class="badge bg-light text-secondary border">Mã danh mục: #${c.id}</span>
                                <div class="mt-auto pt-3">
                                    <a href="${pageContext.request.contextPath}/products?cateid=${c.id}" class="btn btn-sm btn-primary w-100 fw-semibold">
                                        <i class="fa fa-arrow-right me-1"></i>Xem sản phẩm
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="col-12 text-center py-5">
                    <div class="text-muted">Chưa có danh mục nào. Vui lòng đăng nhập quyền Admin để thêm danh mục!</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<!-- 5. Sản Phẩm Nổi Bật (Featured Products Grid) -->
<c:if test="${featuredProducts != null && !featuredProducts.isEmpty()}">
    <section class="container mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold text-dark mb-1"><i class="fa fa-fire text-danger me-2"></i>Sản Phẩm Nổi Bật</h3>
                <p class="text-muted mb-0">Tuyển chọn các sản phẩm bán chạy và được ưa chuộng nhất</p>
            </div>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-sm btn-outline-primary fw-semibold">
                Xem tất cả sản phẩm <i class="fa fa-arrow-right ms-1"></i>
            </a>
        </div>

        <div class="row g-3">
            <c:forEach items="${featuredProducts}" var="p">
                <div class="col-lg-3 col-md-4 col-sm-6">
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
        </div>
    </section>
</c:if>

<!-- 6. 10 Video Mới Nhất (Theo yêu cầu đề bài Bài tập 02) -->
<c:if test="${latestVideos != null && !latestVideos.isEmpty()}">
    <section class="container mb-5" id="videosSection">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold text-dark mb-1">
                    <i class="fa fa-play-circle text-danger me-2"></i>10 Video Mới Nhất
                </h3>
                <p class="text-muted mb-0">Các video mới nhất được người dùng đăng tải (Sắp xếp theo ngày đăng giảm dần)</p>
            </div>
            <a href="${pageContext.request.contextPath}/video" class="btn btn-sm btn-outline-primary fw-semibold">
                Xem tất cả video <i class="fa fa-arrow-right ms-1"></i>
            </a>
        </div>

        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-5 g-3">
            <c:forEach items="${latestVideos}" var="v">
                <div class="col">
                    <div class="home-video-card">
                        <!-- Poster Thumbnail & Play Link -->
                        <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="home-video-thumb-wrap">
                            <c:choose>
                                <c:when test="${not empty v.poster}">
                                    <img src="${pageContext.request.contextPath}/image?fname=${v.poster}" alt="${v.title}" class="home-video-thumb" onerror="this.src='${pageContext.request.contextPath}/image?fname=cat_dientu.png';">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/image?fname=cat_dientu.png" alt="${v.title}" class="home-video-thumb">
                                </c:otherwise>
                            </c:choose>
                            <div class="home-play-badge">
                                <i class="fa fa-play"></i>
                            </div>
                        </a>

                        <!-- Card Body -->
                        <div class="p-3 d-flex flex-column flex-grow-1">
                            <!-- Tiêu đề Video (Bấm được và mở trang chi tiết để phát video theo yêu cầu đề bài) -->
                            <h6 class="mb-2">
                                <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="home-video-title" title="${v.title}">
                                    ${v.title}
                                </a>
                            </h6>

                            <div class="mt-auto pt-2 border-top d-flex justify-content-between align-items-center text-muted" style="font-size: 0.78rem;">
                                <span><i class="fa fa-eye me-1 text-primary"></i>${v.views} xem</span>
                                <span><fmt:formatDate value="${v.uploadDate}" pattern="dd/MM/yyyy" /></span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</c:if>

<!-- 7. Footer -->
<footer class="footer">
    <div class="container">
        <div class="row g-4">
            <div class="col-md-5">
                <h5 class="text-white fw-bold mb-3"><i class="fa fa-shopping-bag text-primary me-2"></i>Shopping Service MVC</h5>
                <p class="small text-secondary">
                    Đồ án môn Lập Trình Web (WEBPR330479) - Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE).<br>
                    Triển khai đầy đủ:
                </p>
                <ul class="small text-secondary list-unstyled">
                    <li><i class="fa fa-check text-success me-2"></i>Kiến trúc 3 lớp MVC (Model - DAO - Service - Controller - View)</li>
                    <li><i class="fa fa-check text-success me-2"></i>Quản lý Đăng nhập, Đăng ký, Phân quyền Role (Admin / Manager / User)</li>
                    <li><i class="fa fa-check text-success me-2"></i>CRUD Danh mục, Upload ảnh đa phương thức & Streaming Servlet</li>
                    <li><i class="fa fa-check text-success me-2"></i>Danh mục sản phẩm, Tìm kiếm, Sắp xếp & Chi tiết sản phẩm</li>
                </ul>
            </div>
            <div class="col-md-3">
                <h6 class="text-white fw-bold mb-3">Công Nghệ Sử Dụng</h6>
                <ul class="list-unstyled small text-secondary">
                    <li>• Jakarta Servlet 6.1 & JSP 3.1</li>
                    <li>• JSTL 3.0 & Taglibs</li>
                    <li>• Microsoft SQL Server & JDBC</li>
                    <li>• Apache Tomcat 11.0</li>
                    <li>• Bootstrap 5.3 & FontAwesome 6</li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6 class="text-white fw-bold mb-3">Thông Tin Giảng Viên Hướng Dẫn</h6>
                <div class="bg-dark p-3 rounded border border-secondary small">
                    <p class="mb-1 text-light"><i class="fa fa-user-tie text-warning me-2"></i><strong>ThS. Nguyễn Hữu Trung</strong></p>
                    <p class="mb-1 text-secondary"><i class="fa fa-envelope text-info me-2"></i>Khoa CNTT - HCMUTE</p>
                    <p class="mb-0 text-secondary"><i class="fa fa-graduation-cap text-primary me-2"></i>Môn: Lập trình Web (WEBPR330479)</p>
                </div>
            </div>
        </div>
        <hr class="border-secondary mt-4">
        <div class="text-center small text-secondary">
            &copy; 2026 Shopping Service MVC. Dự án đáp ứng 100% chuẩn tài liệu Slide 06 & Slide 14.
        </div>
    </div>
</footer>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
