<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Admin Topbar Header -->
<header class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top py-2 px-3 border-bottom border-secondary shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/admin/home">
            <i class="fa fa-shield-halved text-primary fs-4"></i>
            <span>ADMIN <span class="text-primary">PANEL</span></span>
        </a>
        <div class="d-flex align-items-center gap-3">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-light btn-sm rounded-pill px-3" target="_blank">
                <i class="fa fa-arrow-up-right-from-square me-1"></i> Xem Shop
            </a>
            <div class="dropdown">
                <button class="btn btn-light btn-sm rounded-pill px-3 dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
                    <i class="fa fa-user-circle text-primary"></i>
                    <strong>${sessionScope.account.userName}</strong>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow border-0 py-2">
                    <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile"><i class="fa fa-user-pen me-2 text-primary"></i>Hồ sơ cá nhân</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/logout"><i class="fa fa-power-off me-2"></i>Đăng xuất</a></li>
                </ul>
            </div>
        </div>
    </div>
</header>
