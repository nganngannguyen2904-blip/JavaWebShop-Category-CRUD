<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi Mật Khẩu - Shopping Service MVC</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background-color: #f6f8fa;
            font-family: 'Be Vietnam Pro', sans-serif;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .change-pass-card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.06);
            border: 1px solid #eef0f2;
            padding: 35px 30px;
            max-width: 520px;
            margin: 40px auto;
            width: 100%;
        }
    </style>
</head>
<body>

<!-- Header tóm tắt -->
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/home">
            <i class="fa fa-shopping-bag me-2"></i>ShoppingService
        </a>
        <div class="d-flex align-items-center gap-2">
            <span class="text-secondary small">Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-danger">
                <i class="fa fa-sign-out me-1"></i>Đăng xuất
            </a>
        </div>
    </div>
</nav>

<div class="container flex-grow-1 d-flex align-items-center justify-content-center">
    <div class="change-pass-card">
        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center bg-primary-subtle text-primary rounded-circle mb-3" style="width: 60px; height: 60px;">
                <i class="fa fa-shield-alt fs-3"></i>
            </div>
            <h4 class="fw-bold mb-1">Đổi Mật Khẩu Tài Khoản</h4>
            <p class="text-muted small">Cập nhật mật khẩu định kỳ để nâng cao bảo mật</p>
        </div>

        <c:if test="${not empty alert}">
            <div class="alert alert-${not empty alertType ? alertType : 'danger'} alert-dismissible fade show" role="alert">
                <div class="small">${alert}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/change-password" method="post">
            <div class="mb-3">
                <label class="form-label text-secondary small fw-bold">Mật khẩu hiện tại</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                    <input type="password" class="form-control" name="oldpassword" required placeholder="Nhập mật khẩu đang dùng">
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label text-secondary small fw-bold">Mật khẩu mới (Tối thiểu 6 ký tự)</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-key text-muted"></i></span>
                    <input type="password" class="form-control" name="newpassword" required placeholder="Nhập mật khẩu mới">
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label text-secondary small fw-bold">Xác nhận mật khẩu mới</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-check-double text-muted"></i></span>
                    <input type="password" class="form-control" name="confirmpassword" required placeholder="Nhập lại mật khẩu mới">
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
                <i class="fa fa-save me-1"></i>Cập Nhật Mật Khẩu
            </button>
        </form>

        <div class="text-center mt-3">
            <c:choose>
                <c:when test="${sessionScope.account.canAccessAdmin()}">
                    <a href="${pageContext.request.contextPath}/admin/home" class="text-secondary small text-decoration-none">
                        <i class="fa fa-arrow-left me-1"></i>Quay lại Trang Quản Trị
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/home" class="text-secondary small text-decoration-none">
                        <i class="fa fa-arrow-left me-1"></i>Quay lại Trang Chủ Mua Sắm
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
