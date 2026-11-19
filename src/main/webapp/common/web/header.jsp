<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- 1. Topbar -->
<%@ include file="/common/web/topbar.jsp" %>

<!-- 2. Main Fashion Header -->
<header class="fashion-header py-3 bg-white border-bottom border-light">
    <div class="container">
        <div class="d-flex align-items-center justify-content-between gap-3">
            <!-- Brand Logo -->
            <a href="${pageContext.request.contextPath}/home" class="brand-logo text-decoration-none d-flex align-items-center gap-2">
                <i class="fa fa-bag-shopping text-primary fs-3"></i>
                <span class="fs-4 fw-bold text-dark">SHOPPING<span class="text-primary">SERVICE</span></span>
                <span class="badge bg-primary d-none d-sm-inline-block small" style="font-size: 0.65rem; letter-spacing: 1px;">FASHION</span>
            </a>

            <!-- Search Bar -->
            <div class="search-form-wrapper d-none d-lg-block flex-grow-1 mx-4" style="max-width: 500px;">
                <form action="${pageContext.request.contextPath}/products" method="get">
                    <div class="input-group">
                        <input type="text" name="keyword" class="form-control rounded-start-pill ps-3" placeholder="Tìm kiếm trang phục, áo khoác, phụ kiện..." value="${param.keyword}">
                        <button type="submit" class="btn btn-primary rounded-end-pill px-4">
                            <i class="fa fa-search me-1"></i> Tìm
                        </button>
                    </div>
                </form>
            </div>

            <!-- Right Actions: Giỏ Hàng & Tài Khoản -->
            <div class="d-flex align-items-center gap-2">
                <!-- Giỏ Hàng -->
                <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-light text-dark position-relative d-flex align-items-center gap-2 border rounded-pill px-3 py-2">
                    <i class="fa fa-shopping-cart text-primary fs-5"></i>
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.7rem;">
                        ${sessionScope.cart != null ? sessionScope.cart.totalQuantity : 0}
                    </span>
                    <span class="d-none d-xl-inline small fw-semibold">
                        <c:choose>
                            <c:when test="${sessionScope.cart != null && sessionScope.cart.totalAmount > 0}">
                                <fmt:formatNumber value="${sessionScope.cart.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </c:when>
                            <c:otherwise>0 ₫</c:otherwise>
                        </c:choose>
                    </span>
                </a>

                <!-- Tài Khoản Dropdown -->
                <c:choose>
                    <c:when test="${sessionScope.account == null}">
                        <div class="d-flex gap-2 ms-2">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary btn-sm rounded-pill px-3 fw-semibold">
                                <i class="fa fa-sign-in-alt me-1"></i>Đăng nhập
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm rounded-pill px-3 fw-semibold d-none d-sm-inline-block">
                                <i class="fa fa-user-plus me-1"></i>Đăng ký
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="dropdown ms-2">
                            <button class="btn btn-light border rounded-pill px-3 py-1 d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <c:choose>
                                    <c:when test="${sessionScope.account.avatar != null && (sessionScope.account.avatar.startsWith('http://') || sessionScope.account.avatar.startsWith('https://'))}">
                                        <img src="${sessionScope.account.avatar}" alt="Avatar" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;">
                                    </c:when>
                                    <c:when test="${sessionScope.account.avatar != null && !sessionScope.account.avatar.isEmpty()}">
                                        <img src="${pageContext.request.contextPath}/image?fname=${sessionScope.account.avatar}" alt="Avatar" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/upload/category/avata.png'">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/upload/category/avata.png" alt="Avatar" class="rounded-circle" style="width: 32px; height: 32px; object-fit: cover;">
                                    </c:otherwise>
                                </c:choose>
                                <span class="d-none d-md-inline fw-semibold text-truncate small" style="max-width: 110px;">
                                    ${sessionScope.account.fullName}
                                </span>
                                <i class="fa fa-chevron-down text-muted" style="font-size: 0.7rem;"></i>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow border-0 py-2 mt-2" style="border-radius: 12px; min-width: 200px;">
                                <li class="px-3 py-2 border-bottom bg-light">
                                    <div class="fw-bold small text-dark">${sessionScope.account.fullName}</div>
                                    <small class="text-muted d-block text-truncate" style="font-size: 0.75rem;">${sessionScope.account.email}</small>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">
                                        <i class="fa fa-user-circle me-2 text-primary"></i>Hồ sơ cá nhân (Profile)
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/video">
                                        <i class="fa fa-video me-2 text-danger"></i>Lookbook Video
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/cart">
                                        <i class="fa fa-shopping-bag me-2 text-warning"></i>Giỏ hàng của tôi
                                    </a>
                                </li>
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item py-2 text-primary fw-semibold" href="${pageContext.request.contextPath}/admin/home">
                                            <i class="fa fa-gauge-high me-2"></i>Trang Quản Trị Admin
                                        </a>
                                    </li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item py-2 text-danger fw-semibold" href="${pageContext.request.contextPath}/logout">
                                        <i class="fa fa-power-off me-2"></i>Đăng xuất
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>

<!-- 3. Navbar -->
<%@ include file="/common/web/navbar.jsp" %>
