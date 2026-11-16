<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Topbar Navigation -->
<div class="bg-dark text-light py-2 border-bottom border-secondary">
    <div class="container">
        <div class="d-flex justify-content-end align-items-center">
            <!-- Right Session Auth (Theo Slide 20 - File 2) -->
            <div>
                <c:choose>
                    <c:when test="${sessionScope.account == null}">
                        <ul class="list-inline mb-0 small">
                            <li class="list-inline-item">
                                <a href="${pageContext.request.contextPath}/login" class="text-light text-decoration-none">
                                    <i class="fa fa-sign-in-alt me-1 text-primary"></i>Đăng nhập
                                </a>
                            </li>
                            <li class="list-inline-item text-secondary">|</li>
                            <li class="list-inline-item">
                                <a href="${pageContext.request.contextPath}/register" class="text-light text-decoration-none">
                                    <i class="fa fa-user-plus me-1 text-success"></i>Đăng ký
                                </a>
                            </li>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <div class="dropdown d-inline-block">
                            <a class="text-light text-decoration-none dropdown-toggle small" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fa fa-user-circle me-1 text-warning"></i>
                                Xin chào, <strong>${sessionScope.account.fullName}</strong>
                                <c:if test="${sessionScope.account.roleid == 1}">
                                    <span class="badge bg-danger ms-1">Admin</span>
                                </c:if>
                                <c:if test="${sessionScope.account.roleid == 2}">
                                    <span class="badge bg-primary ms-1">Manager</span>
                                </c:if>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/home">
                                            <i class="fa fa-tachometer-alt me-2 text-primary"></i>Trang Quản Trị
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/category/list">
                                            <i class="fa fa-th-list me-2 text-info"></i>Quản lý Danh mục
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider my-1"></li>
                                </c:if>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/video">
                                        <i class="fa fa-video me-2 text-primary"></i>Video của tôi
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/home">
                                        <i class="fa fa-home me-2 text-success"></i>Trang Mua Sắm
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider my-1"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                        <i class="fa fa-sign-out-alt me-2"></i>Đăng Xuất
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
