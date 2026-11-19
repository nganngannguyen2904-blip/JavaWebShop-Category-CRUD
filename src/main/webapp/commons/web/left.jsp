<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Sidebar Trái Danh Mục & Bộ Lọc Nhanh -->
<div class="card border-0 shadow-sm rounded-3 p-3 mb-4">
    <h6 class="fw-bold text-dark mb-3 border-bottom pb-2">
        <i class="fa fa-bars-staggered text-primary me-2"></i>Danh Mục Thời Trang
    </h6>
    <ul class="list-unstyled mb-0">
        <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products?cateid=1" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-shirt me-2 text-primary"></i>Áo Nam</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
        <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products?cateid=2" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-person-dress me-2 text-danger"></i>Thời Trang Nữ</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
        <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products?cateid=3" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-shoe-prints me-2 text-warning"></i>Giày Dép Sneaker</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
        <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products?cateid=4" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-clock me-2 text-info"></i>Đồng Hồ Cao Cấp</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
        <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products?cateid=5" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-wand-magic-sparkles me-2 text-success"></i>Mỹ Phẩm & Phụ Kiện</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/products?cateid=6" class="text-decoration-none text-secondary d-flex align-items-center justify-content-between py-1">
                <span><i class="fa fa-headphones me-2 text-secondary"></i>Thiết Bị Công Nghệ</span>
                <i class="fa fa-chevron-right small text-muted"></i>
            </a>
        </li>
    </ul>
</div>
