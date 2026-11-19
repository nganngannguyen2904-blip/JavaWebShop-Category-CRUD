<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Main Navigation Bar -->
<nav class="fashion-navbar navbar navbar-expand-lg py-0 bg-white border-bottom border-light shadow-sm">
    <div class="container">
        <button class="navbar-toggler my-2" type="button" data-bs-toggle="collapse" data-bs-target="#fashionNavContent">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="fashionNavContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link py-3 px-3 fw-semibold text-dark ${pageContext.request.requestURI.endsWith('/home') || pageContext.request.requestURI.endsWith('/') ? 'text-primary border-bottom border-primary border-2' : ''}" href="${pageContext.request.contextPath}/home">
                        <i class="fa fa-house me-1"></i> Trang Chủ
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle py-3 px-3 fw-semibold text-dark" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fa fa-layer-group me-1"></i> Danh Mục Thời Trang
                    </a>
                    <ul class="dropdown-menu shadow border-0 py-2">
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=1"><i class="fa fa-shirt me-2 text-primary"></i>Áo Nam Thanh Lịch</a></li>
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=2"><i class="fa fa-person-dress me-2 text-danger"></i>Thời Trang Nữ Xu Hướng</a></li>
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=3"><i class="fa fa-shoe-prints me-2 text-warning"></i>Giày Dép Sneaker & Cao Gót</a></li>
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=4"><i class="fa fa-clock me-2 text-info"></i>Đồng Hồ Cao Cấp</a></li>
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=5"><i class="fa fa-wand-magic-sparkles me-2 text-success"></i>Mỹ Phẩm & Phụ Kiện</a></li>
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/products?cateid=6"><i class="fa fa-headphones me-2 text-secondary"></i>Thiết Bị Công Nghệ Cá Tính</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item py-2 fw-semibold text-primary" href="${pageContext.request.contextPath}/products"><i class="fa fa-grid-2 me-2"></i>Xem Tất Cả Sản Phẩm</a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a class="nav-link py-3 px-3 fw-semibold text-dark" href="${pageContext.request.contextPath}/products">
                        <i class="fa fa-tag me-1"></i> Tất Cả Sản Phẩm
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link py-3 px-3 fw-semibold text-dark" href="${pageContext.request.contextPath}/video">
                        <i class="fa fa-clapperboard text-danger me-1"></i> Lookbook Video <span class="badge bg-danger ms-1" style="font-size: 0.65rem;">MỚI</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link py-3 px-3 fw-semibold text-dark" href="${pageContext.request.contextPath}/profile">
                        <i class="fa fa-user-circle text-primary me-1"></i> Tài Khoản Profile
                    </a>
                </li>
            </ul>

            <div class="d-none d-xl-flex align-items-center gap-2 text-muted small py-2">
                <i class="fa fa-shield-halved text-success"></i> 100% Hàng chính hãng &bull; Đổi trả 30 ngày
            </div>
        </div>
    </div>
</nav>
