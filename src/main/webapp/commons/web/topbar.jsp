<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Topbar Announcement & Hotline -->
<div class="top-promo-bar bg-dark text-light py-1 border-bottom border-secondary d-none d-md-block" style="font-size: 0.825rem;">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <span class="badge bg-danger me-2">HOT</span>
                <span>Miễn phí vận chuyển toàn quốc cho đơn hàng từ <strong>500.000₫</strong></span>
            </div>
            <div class="d-flex gap-3 align-items-center">
                <a href="tel:19006868" class="text-light text-decoration-none">
                    <i class="fa fa-phone-alt me-1 text-primary"></i>Hotline: 1900 6868
                </a>
                <span class="text-secondary">|</span>
                <a href="${pageContext.request.contextPath}/home" class="text-light text-decoration-none">
                    <i class="fa fa-store me-1 text-danger"></i>Hệ thống cửa hàng
                </a>
                <span class="text-secondary">|</span>
                <c:choose>
                    <c:when test="${sessionScope.account == null}">
                        <a href="${pageContext.request.contextPath}/login" class="text-light text-decoration-none">
                            <i class="fa fa-sign-in-alt me-1 text-primary"></i>Đăng nhập
                        </a>
                        <span class="text-secondary">/</span>
                        <a href="${pageContext.request.contextPath}/register" class="text-light text-decoration-none">
                            Đăng ký
                        </a>
                    </c:when>
                    <c:otherwise>
                        <span class="text-light">
                            Xin chào, <strong>${sessionScope.account.fullName}</strong>
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
