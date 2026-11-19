<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Tài Khoản Mới - Shopping Service MVC</title>
    <!-- Google Fonts Be Vietnam Pro -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #0d6efd 0%, #002b66 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, sans-serif;
            padding: 35px 15px;
        }
        .register-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.25);
            background: #ffffff;
            width: 100%;
            max-width: 580px;
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
        .input-group-text {
            background-color: #f8f9fa;
            border-right: none;
            color: #6c757d;
        }
        .form-control-custom {
            border-left: none;
        }
        .form-control-custom:focus {
            border-color: #dee2e6;
            box-shadow: none;
        }
        .input-group:focus-within {
            box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.2);
            border-radius: 0.375rem;
        }
        .input-group:focus-within .input-group-text,
        .input-group:focus-within .form-control-custom,
        .input-group:focus-within .btn-toggle-pass {
            border-color: #86b7fe;
        }
        .btn-toggle-pass {
            background-color: #f8f9fa;
            border-left: none;
            color: #6c757d;
            cursor: pointer;
        }
        .btn-toggle-pass:hover {
            color: #0d6efd;
        }
        .btn-register {
            background: #0d6efd;
            border: none;
            border-radius: 8px;
            padding: 12px;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.25s ease;
        }
        .btn-register:hover {
            background: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 6px 18px rgba(13, 110, 253, 0.35);
        }
    </style>
</head>
<body>

<div class="register-card p-4 p-sm-5">
    <!-- Header -->
    <div class="text-center mb-4">
        <div class="icon-circle">
            <i class="fa fa-user-plus"></i>
        </div>
        <h3 class="fw-bold text-dark mb-1">Đăng Ký Tài Khoản</h3>
        <p class="text-muted small mb-0">Tạo tài khoản để trải nghiệm mua sắm trực tuyến</p>
    </div>

    <!-- Alert Message -->
    <c:if test="${not empty alert}">
        <div class="alert alert-${not empty alertType ? alertType : 'danger'} alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-exclamation-triangle me-2 flex-shrink-0"></i>
            <div class="small">${alert}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Form Đăng Ký -->
    <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm" class="mt-3" novalidate>
        
        <!-- Họ và tên -->
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Họ và tên <span class="text-danger">*</span></label>
            <div class="input-group">
                <span class="input-group-text"><i class="fa fa-id-card"></i></span>
                <input type="text" 
                       class="form-control form-control-custom ${not empty errors.fullname ? 'is-invalid' : ''}" 
                       id="fullname" 
                       name="fullname" 
                       value="<c:out value='${oldFullname}'/>" 
                       placeholder="Ví dụ: Nguyễn Văn An" 
                       required>
            </div>
            <div class="text-danger small ${not empty errors.fullname ? 'd-block' : 'd-none'} mt-1" id="fullnameError">
                ${not empty errors.fullname ? errors.fullname : 'Vui lòng nhập họ và tên!'}
            </div>
        </div>

        <div class="row">
            <!-- Email -->
            <div class="col-md-6 mb-3">
                <label class="form-label text-secondary small fw-bold">Địa chỉ Email <span class="text-danger">*</span></label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fa fa-envelope"></i></span>
                    <input type="email" 
                           class="form-control form-control-custom ${not empty errors.email ? 'is-invalid' : ''}" 
                           id="email" 
                           name="email" 
                           value="<c:out value='${oldEmail}'/>" 
                           placeholder="user@example.com" 
                           required>
                </div>
                <div class="text-danger small ${not empty errors.email ? 'd-block' : 'd-none'} mt-1" id="emailError">
                    ${not empty errors.email ? errors.email : 'Email không đúng định dạng!'}
                </div>
            </div>

            <!-- Số điện thoại -->
            <div class="col-md-6 mb-3">
                <label class="form-label text-secondary small fw-bold">Số điện thoại</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fa fa-phone"></i></span>
                    <input type="tel" 
                           class="form-control form-control-custom ${not empty errors.phone ? 'is-invalid' : ''}" 
                           id="phone" 
                           name="phone" 
                           value="<c:out value='${oldPhone}'/>" 
                           placeholder="0901234567">
                </div>
                <div class="text-danger small ${not empty errors.phone ? 'd-block' : 'd-none'} mt-1" id="phoneError">
                    ${not empty errors.phone ? errors.phone : 'Số điện thoại phải có 10 chữ số (đầu 03, 05, 07, 08, 09)!'}
                </div>
            </div>
        </div>

        <!-- Tên đăng nhập -->
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Tên đăng nhập (Username) <span class="text-danger">*</span></label>
            <div class="input-group">
                <span class="input-group-text"><i class="fa fa-user"></i></span>
                <input type="text" 
                       class="form-control form-control-custom ${not empty errors.username ? 'is-invalid' : ''}" 
                       id="username" 
                       name="username" 
                       value="<c:out value='${oldUsername}'/>" 
                       placeholder="Từ 3-30 ký tự, không dấu cách" 
                       required>
            </div>
            <div class="text-danger small ${not empty errors.username ? 'd-block' : 'd-none'} mt-1" id="usernameError">
                ${not empty errors.username ? errors.username : 'Tên đăng nhập từ 3 đến 30 ký tự (chữ, số và _)!'}
            </div>
        </div>

        <div class="row">
            <!-- Mật khẩu -->
            <div class="col-md-6 mb-3">
                <label class="form-label text-secondary small fw-bold">Mật khẩu <span class="text-danger">*</span></label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fa fa-lock"></i></span>
                    <input type="password" 
                           class="form-control form-control-custom ${not empty errors.password ? 'is-invalid' : ''}" 
                           id="password" 
                           name="password" 
                           placeholder="Tối thiểu 6 ký tự" 
                           required>
                    <button type="button" class="btn btn-outline-secondary btn-toggle-pass" id="btnTogglePass1" tabindex="-1">
                        <i class="fa fa-eye" id="eye1"></i>
                    </button>
                </div>
                <div class="text-danger small ${not empty errors.password ? 'd-block' : 'd-none'} mt-1" id="passwordError">
                    ${not empty errors.password ? errors.password : 'Mật khẩu phải từ 6 ký tự trở lên!'}
                </div>
            </div>

            <!-- Xác nhận mật khẩu -->
            <div class="col-md-6 mb-3">
                <label class="form-label text-secondary small fw-bold">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fa fa-check-double"></i></span>
                    <input type="password" 
                           class="form-control form-control-custom ${not empty errors.confirmpassword ? 'is-invalid' : ''}" 
                           id="confirmpassword" 
                           name="confirmpassword" 
                           placeholder="Nhập lại mật khẩu" 
                           required>
                    <button type="button" class="btn btn-outline-secondary btn-toggle-pass" id="btnTogglePass2" tabindex="-1">
                        <i class="fa fa-eye" id="eye2"></i>
                    </button>
                </div>
                <div class="text-danger small ${not empty errors.confirmpassword ? 'd-block' : 'd-none'} mt-1" id="confirmError">
                    ${not empty errors.confirmpassword ? errors.confirmpassword : 'Mật khẩu xác nhận không khớp!'}
                </div>
            </div>
        </div>

        <!-- Lưu ý phân quyền an toàn -->
        <div class="alert alert-light border small text-muted mb-4">
            <i class="fa fa-shield-alt text-primary me-1"></i>
            <strong>Quy chuẩn bảo mật:</strong> Mọi tài khoản tạo mới sẽ được băm bảo mật bằng <code>BCrypt</code> và gán vai trò Khách hàng (Role 5 - USER). Quyền Quản trị viên chỉ được cấp bởi Ban Quản trị hệ thống.
        </div>

        <!-- Nút Đăng ký -->
        <button type="submit" class="btn btn-primary btn-register w-100 text-white" id="btnRegisterSubmit">
            <i class="fa fa-user-plus me-2"></i>Đăng Ký Tài Khoản
        </button>

        <!-- Chuyển sang Đăng nhập -->
        <div class="text-center mt-4 pt-2 border-top">
            <p class="text-secondary small mb-0">
                Đã có tài khoản? 
                <a href="${pageContext.request.contextPath}/login" class="text-primary fw-semibold text-decoration-none">
                    Đăng nhập tại đây <i class="fa fa-arrow-right ms-1"></i>
                </a>
            </p>
        </div>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Ẩn/Hiện mật khẩu
    function setupToggle(btnId, inputId, iconId) {
        document.getElementById(btnId).addEventListener('click', function () {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(iconId);
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        });
    }
    setupToggle('btnTogglePass1', 'password', 'eye1');
    setupToggle('btnTogglePass2', 'confirmpassword', 'eye2');

    // Validation phía Client
    const form = document.getElementById('registerForm');
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    const phoneRegex = /^(0[3|5|7|8|9])[0-9]{8}$/;
    const usernameRegex = /^[a-zA-Z0-9_]{3,30}$/;

    form.addEventListener('submit', function (e) {
        let isValid = true;

        const fullname = document.getElementById('fullname').value.trim();
        const email = document.getElementById('email').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const confirm = document.getElementById('confirmpassword').value;

        // Fullname
        if (!fullname) {
            document.getElementById('fullnameError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('fullnameError').classList.add('d-none');
        }

        // Email
        if (!email || !emailRegex.test(email)) {
            document.getElementById('emailError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('emailError').classList.add('d-none');
        }

        // Phone
        if (phone && !phoneRegex.test(phone)) {
            document.getElementById('phoneError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('phoneError').classList.add('d-none');
        }

        // Username
        if (!username || !usernameRegex.test(username)) {
            document.getElementById('usernameError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('usernameError').classList.add('d-none');
        }

        // Password
        if (!password || password.length < 6) {
            document.getElementById('passwordError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('passwordError').classList.add('d-none');
        }

        // Confirm
        if (!confirm || password !== confirm) {
            document.getElementById('confirmError').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('confirmError').classList.add('d-none');
        }

        if (!isValid) {
            e.preventDefault();
        } else {
            const btn = document.getElementById('btnRegisterSubmit');
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang khởi tạo tài khoản...';
        }
    });
</script>

</body>
</html>
