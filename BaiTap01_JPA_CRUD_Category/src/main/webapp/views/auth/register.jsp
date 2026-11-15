<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Tài Khoản - Xác Thực Gmail OTP</title>
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
            max-width: 460px;
            width: 100%;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            border: none;
        }
        .auth-header {
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
            color: #ffffff;
            padding: 30px 24px;
            text-align: center;
        }
        .auth-body {
            padding: 32px 28px;
        }
        .form-floating > .form-control:focus ~ label {
            color: #0d6efd;
        }
        .btn-primary {
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
            border: none;
            padding: 12px;
            font-weight: 600;
            border-radius: 10px;
        }
        .btn-primary:hover {
            background: linear-gradient(135deg, #0b5ed7 0%, #094cb0 100%);
        }
    </style>
</head>
<body>

<div class="auth-card">
    <div class="auth-header">
        <h4 class="fw-bold mb-1"><i class="fa fa-user-plus me-2"></i>Đăng Ký Tài Khoản</h4>
        <p class="mb-0 small text-white-50">Xác minh danh tính qua mã Gmail OTP bảo mật</p>
    </div>

    <div class="auth-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fa fa-exclamation-triangle me-2"></i>
                <div>${error}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fa fa-check-circle me-2"></i>
                <div>${message}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <div class="mb-3">
                <label for="fullname" class="form-label fw-semibold text-secondary small">HỌ VÀ TÊN</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-user"></i></span>
                    <input type="text" class="form-control" id="fullname" name="fullname" 
                           value="${fullname}" placeholder="Nguyễn Văn A" required autofocus>
                </div>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label fw-semibold text-secondary small">ĐỊA CHỈ GMAIL</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-envelope"></i></span>
                    <input type="email" class="form-control" id="email" name="email" 
                           value="${email}" placeholder="example@gmail.com" required>
                </div>
                <div class="form-text text-muted small">Hệ thống sẽ gửi mã OTP xác nhận tới Gmail này.</div>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label fw-semibold text-secondary small">MẬT KHẨU</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-lock"></i></span>
                    <input type="password" class="form-control" id="password" name="password" 
                           placeholder="Tối thiểu 6 ký tự" minlength="6" required>
                </div>
            </div>

            <div class="mb-4">
                <label for="confirmPassword" class="form-label fw-semibold text-secondary small">XÁC NHẬN MẬT KHẨU</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-shield-alt"></i></span>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                           placeholder="Nhập lại mật khẩu trên" minlength="6" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 shadow-sm mb-3">
                <i class="fa fa-paper-plane me-2"></i>Gửi Mã OTP Đến Gmail
            </button>
        </form>

        <div class="text-center pt-2 border-top">
            <span class="text-muted small">Đã có tài khoản?</span>
            <a href="${pageContext.request.contextPath}/login" class="text-decoration-none fw-semibold ms-1 small">
                Đăng nhập ngay
            </a>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
