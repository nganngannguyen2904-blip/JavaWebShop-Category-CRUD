<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu - Shopping Service MVC</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #0d6efd 0%, #002b66 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Be Vietnam Pro', sans-serif;
            padding: 25px 15px;
        }
        .auth-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.25);
            background: #ffffff;
            width: 100%;
            max-width: 480px;
            overflow: hidden;
        }
        .icon-circle {
            width: 68px;
            height: 68px;
            background: rgba(13, 110, 253, 0.1);
            color: #0d6efd;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 30px;
            margin-bottom: 12px;
        }
    </style>
</head>
<body>

<div class="auth-card p-4 p-sm-5">
    <div class="text-center mb-4">
        <div class="icon-circle">
            <i class="fa fa-lock-rotate"></i>
        </div>
        <h3 class="fw-bold text-dark mb-1">Đặt Lại Mật Khẩu</h3>
        <p class="text-muted small mb-0">Nhập mã xác thực và thiết lập mật khẩu mới</p>
    </div>

    <!-- Alert -->
    <c:if test="${not empty alert}">
        <div class="alert alert-${not empty alertType ? alertType : 'danger'} alert-dismissible fade show" role="alert">
            <div class="small">${alert}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/reset-password" method="post">
        <!-- Token -->
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Mã xác thực (Token 6 chữ số)</label>
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="fa fa-shield-alt text-muted"></i></span>
                <input type="text" 
                       class="form-control" 
                       name="token" 
                       value="<c:out value='${token}'/>" 
                       placeholder="Ví dụ: 849302" 
                       required>
            </div>
        </div>

        <!-- Mật khẩu mới -->
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Mật khẩu mới (Tối thiểu 6 ký tự)</label>
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                <input type="password" 
                       class="form-control" 
                       id="newpassword" 
                       name="newpassword" 
                       placeholder="Nhập mật khẩu mới" 
                       required>
            </div>
        </div>

        <!-- Xác nhận mật khẩu mới -->
        <div class="mb-4">
            <label class="form-label text-secondary small fw-bold">Xác nhận mật khẩu mới</label>
            <div class="input-group">
                <span class="input-group-text bg-light"><i class="fa fa-check-double text-muted"></i></span>
                <input type="password" 
                       class="form-control" 
                       id="confirmpassword" 
                       name="confirmpassword" 
                       placeholder="Nhập lại mật khẩu mới" 
                       required>
            </div>
        </div>

        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
            <i class="fa fa-save me-1"></i>Lưu Mật Khẩu Mới
        </button>
    </form>

    <div class="text-center mt-4 pt-2 border-top">
        <a href="${pageContext.request.contextPath}/login" class="text-secondary small text-decoration-none">
            <i class="fa fa-arrow-left me-1"></i>Quay lại trang Đăng nhập
        </a>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
