<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu Mới</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            padding: 30px 15px;
        }
        .auth-card {
            max-width: 440px;
            width: 100%;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            border: none;
        }
        .auth-header {
            background: linear-gradient(135deg, #198754 0%, #157347 100%);
            color: #ffffff;
            padding: 30px 24px;
            text-align: center;
        }
        .auth-body {
            padding: 32px 28px;
        }
        .btn-success {
            background: linear-gradient(135deg, #198754 0%, #157347 100%);
            border: none;
            padding: 12px;
            font-weight: 600;
            border-radius: 10px;
        }
        .btn-success:hover {
            background: linear-gradient(135deg, #157347 0%, #115c38 100%);
        }
    </style>
</head>
<body>

<div class="auth-card">
    <div class="auth-header">
        <div class="mb-2"><i class="fa fa-shield-alt fa-3x"></i></div>
        <h4 class="fw-bold mb-1">Đặt Lại Mật Khẩu Mới</h4>
        <p class="mb-0 small text-white-50">Xác minh OTP thành công! Thiết lập mật khẩu mới</p>
    </div>

    <div class="auth-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fa fa-exclamation-triangle me-2"></i>
                <div class="small">${error}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/reset-password" method="POST">
            <div class="mb-3">
                <label class="form-label fw-semibold text-secondary small">TÀI KHOẢN XÁC NHẬN</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-success"><i class="fa fa-check-circle"></i></span>
                    <input type="text" class="form-control bg-light" value="${email}" readonly>
                </div>
            </div>

            <div class="mb-3">
                <label for="newPassword" class="form-label fw-semibold text-secondary small">MẬT KHẨU MỚI</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-lock"></i></span>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" 
                           placeholder="Tối thiểu 6 ký tự" minlength="6" required autofocus>
                </div>
            </div>

            <div class="mb-4">
                <label for="confirmPassword" class="form-label fw-semibold text-secondary small">XÁC NHẬN MẬT KHẨU MỚI</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-lock"></i></span>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                           placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                </div>
            </div>

            <button type="submit" class="btn btn-success w-100 shadow-sm mb-3">
                <i class="fa fa-save me-2"></i>Lưu Mật Khẩu Mới
            </button>
        </form>

        <div class="text-center pt-2 border-top">
            <a href="${pageContext.request.contextPath}/login" class="text-muted small text-decoration-none">
                <i class="fa fa-arrow-left me-1"></i>Hủy thao tác và quay lại Đăng nhập
            </a>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
