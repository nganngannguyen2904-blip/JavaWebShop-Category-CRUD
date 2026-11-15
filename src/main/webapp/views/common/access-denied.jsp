<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Truy Cập Bị Từ Chối - 403 Forbidden</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background: #f8f9fa;
            font-family: 'Be Vietnam Pro', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .error-card {
            max-width: 550px;
            width: 100%;
            background: #ffffff;
            border-radius: 16px;
            padding: 40px 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
            text-align: center;
            border-top: 5px solid #dc3545;
        }
        .error-icon {
            width: 80px;
            height: 80px;
            background: rgba(220, 53, 69, 0.1);
            color: #dc3545;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 38px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<div class="error-card">
    <div class="error-icon">
        <i class="fa fa-shield-halved"></i>
    </div>
    <h2 class="fw-bold text-danger mb-2">403 - TRUY CẬP BỊ TỪ CHỐI</h2>
    <p class="text-secondary mb-3">
        Tài khoản <strong><c:out value="${sessionScope.account.userName}"/></strong> (Vai trò: <span class="badge bg-secondary">Khách hàng / USER</span>) không có đặc quyền truy cập khu vực Quản trị hệ thống (<strong>/admin/*</strong>).
    </p>

    <div class="alert alert-warning text-start small mb-4">
        <i class="fa fa-info-circle me-1"></i>
        <strong>Bảo mật phân quyền phía Máy chủ (Backend Security):</strong><br>
        Hệ thống kiểm tra vai trò người dùng trực tiếp trên session và cơ sở dữ liệu. Mọi hành vi sửa đổi tham số trên trình duyệt hoặc truy cập thẳng URL quản trị đều bị ngăn chặn tuyệt đối.
    </div>

    <div class="d-flex justify-content-center gap-3">
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary fw-semibold px-4">
            <i class="fa fa-home me-2"></i>Về Trang Mua Sắm
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger fw-semibold px-3">
            <i class="fa fa-sign-out me-1"></i>Đăng Xuất
        </a>
    </div>
</div>

</body>
</html>
