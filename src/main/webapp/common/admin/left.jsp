<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Admin Left Sidebar Menu -->
<div class="bg-dark text-light p-3 min-vh-100 border-end border-secondary">
    <div class="user-panel text-center py-3 border-bottom border-secondary mb-3">
        <c:choose>
            <c:when test="${sessionScope.account.avatar != null && (sessionScope.account.avatar.startsWith('http://') || sessionScope.account.avatar.startsWith('https://'))}">
                <img src="${sessionScope.account.avatar}" alt="Avatar" class="rounded-circle shadow mb-2" style="width: 60px; height: 60px; object-fit: cover; border: 2px solid #0d6efd;">
            </c:when>
            <c:when test="${sessionScope.account.avatar != null && !sessionScope.account.avatar.isEmpty()}">
                <img src="${pageContext.request.contextPath}/image?fname=${sessionScope.account.avatar}" alt="Avatar" class="rounded-circle shadow mb-2" style="width: 60px; height: 60px; object-fit: cover; border: 2px solid #0d6efd;" onerror="this.src='${pageContext.request.contextPath}/upload/category/avata.png'">
            </c:when>
            <c:otherwise>
                <img src="${pageContext.request.contextPath}/upload/category/avata.png" alt="Avatar" class="rounded-circle shadow mb-2" style="width: 60px; height: 60px; object-fit: cover; border: 2px solid #0d6efd;">
            </c:otherwise>
        </c:choose>
        <div class="fw-bold text-white small">${sessionScope.account.fullName}</div>
        <span class="badge bg-danger rounded-pill" style="font-size: 0.65rem;">Quản Trị Viên</span>
    </div>

    <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/admin/home" class="nav-link text-light ${pageContext.request.requestURI.endsWith('/admin/home') ? 'active bg-primary' : ''}">
                <i class="fa fa-tachometer-alt me-2"></i> Bảng điều khiển
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/admin/category/list" class="nav-link text-light ${pageContext.request.requestURI.contains('/category/list') ? 'active bg-primary' : ''}">
                <i class="fa fa-layer-group me-2"></i> Quản lý danh mục
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/admin/category/add" class="nav-link text-light ${pageContext.request.requestURI.contains('/category/add') ? 'active bg-primary' : ''}">
                <i class="fa fa-plus-circle me-2"></i> Thêm danh mục mới
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/admin/product/list" class="nav-link text-light ${pageContext.request.requestURI.contains('/product/list') ? 'active bg-primary' : ''}">
                <i class="fa fa-box-open me-2"></i> Quản lý sản phẩm
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/video" class="nav-link text-light">
                <i class="fa fa-video me-2 text-danger"></i> Lookbook Video
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/profile" class="nav-link text-light">
                <i class="fa fa-user-circle me-2 text-info"></i> Hồ sơ của tôi
            </a>
        </li>
        <li class="nav-item mt-3 pt-2 border-top border-secondary">
            <a href="${pageContext.request.contextPath}/logout" class="nav-link text-danger">
                <i class="fa fa-power-off me-2"></i> Đăng xuất an toàn
            </a>
        </li>
    </ul>
</div>
