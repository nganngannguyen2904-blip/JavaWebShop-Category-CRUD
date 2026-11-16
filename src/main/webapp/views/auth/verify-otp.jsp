<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Thực Mã OTP - Shopping Service MVC</title>
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
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            padding: 30px 15px;
        }
        .otp-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.25);
            background: #ffffff;
            width: 100%;
            max-width: 480px;
            overflow: hidden;
        }
        .icon-circle {
            width: 72px;
            height: 72px;
            background: rgba(13, 110, 253, 0.1);
            color: #0d6efd;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            margin-bottom: 15px;
        }
        .otp-input {
            letter-spacing: 12px;
            font-size: 28px;
            font-weight: 700;
            text-align: center;
            border-radius: 10px;
            border: 2px solid #ced4da;
            color: #0d6efd;
            font-family: 'Courier New', Courier, monospace;
        }
        .otp-input:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
        }
        .timer-badge {
            background-color: #e7f1ff;
            color: #0d6efd;
            font-weight: 600;
            padding: 6px 14px;
            border-radius: 20px;
            display: inline-block;
        }
        .btn-verify {
            background: #0d6efd;
            border: none;
            border-radius: 8px;
            padding: 12px;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.25s ease;
            color: #ffffff;
        }
        .btn-verify:hover {
            background: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 6px 18px rgba(13, 110, 253, 0.35);
            color: #ffffff;
        }
    </style>
</head>
<body>

<div class="otp-card p-4 p-sm-5">
    <div class="text-center mb-4">
        <div class="icon-circle">
            <i class="fa fa-envelope-open-text"></i>
        </div>
        <h3 class="fw-bold text-dark mb-1">Xác Thực Mã OTP</h3>
        <p class="text-muted small mb-0">
            Mã xác thực 6 chữ số đã được gửi tới email:<br>
            <strong class="text-primary">${not empty maskedEmail ? maskedEmail : email}</strong>
        </p>
    </div>

    <!-- Thông báo Alert -->
    <c:if test="${not empty alert}">
        <div class="alert alert-${not empty alertType ? alertType : 'danger'} alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-info-circle me-2 flex-shrink-0"></i>
            <div class="small">${alert}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Form nhập mã OTP -->
    <form action="${pageContext.request.contextPath}/verify-otp" method="post" id="otpForm">
        <input type="hidden" name="email" value="<c:out value='${email}'/>" />
        <input type="hidden" name="purpose" value="<c:out value='${purpose}'/>" />

        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold text-center d-block">NHẬP MÃ OTP 6 CHỮ SỐ</label>
            <input type="text" 
                   class="form-control otp-input" 
                   name="otp" 
                   id="otp" 
                   maxlength="6" 
                   placeholder="------" 
                   autocomplete="one-time-code" 
                   required 
                   autofocus />
            <div class="text-center mt-2">
                <span class="text-muted small">
                    <i class="fa fa-clock me-1"></i>Mã có hiệu lực trong: <strong>5 phút</strong>
                </span>
                <c:if test="${not empty remainingAttempts}">
                    <span class="text-muted small ms-2">
                        | Còn lại: <strong class="text-danger">${remainingAttempts}</strong> lượt thử
                    </span>
                </c:if>
            </div>
        </div>

        <button type="submit" class="btn btn-verify w-100 mt-2">
            <i class="fa fa-check-circle me-1"></i>Xác Nhận & Kích Hoạt
        </button>
    </form>

    <!-- Gửi lại mã OTP -->
    <div class="text-center mt-4 pt-3 border-top">
        <form action="${pageContext.request.contextPath}/resend-otp" method="post" id="resendForm">
            <input type="hidden" name="email" value="<c:out value='${email}'/>" />
            <input type="hidden" name="purpose" value="<c:out value='${purpose}'/>" />
            
            <p class="text-muted small mb-2">Chưa nhận được mã xác thực?</p>
            <button type="submit" class="btn btn-outline-secondary btn-sm" id="btnResend" <c:if test="${cooldown > 0}">disabled</c:if>>
                <i class="fa fa-redo me-1"></i>
                <span id="resendText">
                    <c:choose>
                        <c:when test="${cooldown > 0}">Gửi lại mã sau (${cooldown}s)</c:when>
                        <c:otherwise>Gửi lại mã OTP</c:otherwise>
                    </c:choose>
                </span>
            </button>
        </form>
    </div>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/register" class="text-decoration-none small text-secondary">
            <i class="fa fa-arrow-left me-1"></i>Quay lại trang Đăng ký
        </a>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Đếm ngược gửi lại mã OTP (60s)
    let cooldown = parseInt("${cooldown != null ? cooldown : 0}");
    const btnResend = document.getElementById("btnResend");
    const resendText = document.getElementById("resendText");

    if (cooldown > 0) {
        btnResend.disabled = true;
        const interval = setInterval(() => {
            cooldown--;
            if (cooldown <= 0) {
                clearInterval(interval);
                btnResend.disabled = false;
                resendText.innerHTML = "Gửi lại mã OTP";
            } else {
                resendText.innerHTML = "Gửi lại mã sau (" + cooldown + "s)";
            }
        }, 1000);
    }

    // Tự động submit khi nhập đủ 6 ký tự
    const otpInput = document.getElementById("otp");
    otpInput.addEventListener("input", function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length === 6) {
            document.getElementById("otpForm").submit();
        }
    });
</script>

</body>
</html>
