<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Thực Mã Gmail OTP</title>
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
        .otp-card {
            max-width: 460px;
            width: 100%;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            border: none;
        }
        .otp-header {
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
            color: #ffffff;
            padding: 30px 24px;
            text-align: center;
        }
        .otp-body {
            padding: 32px 28px;
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
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
            border: none;
            padding: 12px;
            font-weight: 600;
            border-radius: 10px;
            color: white;
        }
        .btn-verify:hover {
            background: linear-gradient(135deg, #0b5ed7 0%, #094cb0 100%);
            color: white;
        }
    </style>
</head>
<body>

<div class="otp-card">
    <div class="otp-header">
        <div class="mb-2"><i class="fa fa-envelope-open-text fa-3x"></i></div>
        <h4 class="fw-bold mb-1">Xác Thực Mã OTP</h4>
        <p class="mb-0 small text-white-50">Nhập mã 6 chữ số đã được gửi tới Gmail của bạn</p>
    </div>

    <div class="otp-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fa fa-exclamation-triangle me-2"></i>
                <div class="small">${error}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fa fa-check-circle me-2"></i>
                <div class="small">${message}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="text-center mb-3">
            <span class="text-muted small">Mã xác thực đã được gửi tới:</span>
            <div class="fw-bold text-dark mt-1">${email}</div>
        </div>

        <!-- Đồng hồ đếm ngược 5 phút -->
        <div class="text-center mb-4">
            <div class="timer-badge">
                <i class="fa fa-clock me-1"></i>
                Mã hết hạn sau: <span id="countdown">05:00</span>
            </div>
            <c:if test="${not empty remainingAttempts}">
                <div class="text-muted small mt-2">
                    Số lần thử còn lại: <strong class="text-danger">${remainingAttempts}</strong>/5
                </div>
            </c:if>
        </div>

        <!-- Form nhập OTP -->
        <form action="${pageContext.request.contextPath}/verify-otp" method="POST" id="verifyForm">
            <input type="hidden" name="email" value="${email}">
            <input type="hidden" name="purpose" value="${purpose}">

            <div class="mb-4">
                <input type="text" class="form-control otp-input" id="otp" name="otp" 
                       maxlength="6" placeholder="------" pattern="[0-9]{6}" required autofocus autocomplete="one-time-code">
            </div>

            <button type="submit" class="btn btn-verify w-100 shadow-sm mb-3">
                <i class="fa fa-check-circle me-2"></i>Xác Minh Mã OTP
            </button>
        </form>

        <!-- Form gửi lại mã OTP -->
        <div class="text-center pt-2 border-top">
            <span class="text-muted small">Không nhận được mã?</span>
            <form action="${pageContext.request.contextPath}/resend-otp" method="POST" class="d-inline" id="resendForm">
                <input type="hidden" name="email" value="${email}">
                <input type="hidden" name="purpose" value="${purpose}">
                <button type="submit" id="resendBtn" class="btn btn-link p-0 text-decoration-none fw-semibold small ms-1" disabled>
                    Gửi lại mã (<span id="cooldownTimer">${cooldown > 0 ? cooldown : 60}</span>s)
                </button>
            </form>
        </div>

        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/login" class="text-muted small text-decoration-none">
                <i class="fa fa-arrow-left me-1"></i>Quay lại trang Đăng nhập
            </a>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // 1. Đồng hồ đếm ngược 5 phút (300 giây) cho OTP
    let totalSeconds = 300;
    const countdownEl = document.getElementById('countdown');
    const timerInterval = setInterval(function() {
        if (totalSeconds <= 0) {
            clearInterval(timerInterval);
            countdownEl.textContent = "HẾT HẠN";
            countdownEl.classList.add("text-danger");
        } else {
            totalSeconds--;
            const mins = Math.floor(totalSeconds / 60);
            const secs = totalSeconds % 60;
            countdownEl.textContent = (mins < 10 ? '0' : '') + mins + ':' + (secs < 10 ? '0' : '') + secs;
        }
    }, 1000);

    // 2. Cooldown gửi lại OTP (mặc định 60 giây)
    let cooldown = parseInt('${cooldown > 0 ? cooldown : 60}');
    const resendBtn = document.getElementById('resendBtn');
    const cooldownTimerEl = document.getElementById('cooldownTimer');

    if (cooldown <= 0) {
        resendBtn.removeAttribute('disabled');
        resendBtn.textContent = 'Gửi lại mã OTP';
    } else {
        const cooldownInterval = setInterval(function() {
            cooldown--;
            if (cooldown <= 0) {
                clearInterval(cooldownInterval);
                resendBtn.removeAttribute('disabled');
                resendBtn.textContent = 'Gửi lại mã OTP';
            } else {
                cooldownTimerEl.textContent = cooldown;
            }
        }, 1000);
    }

    // Tự động chỉ cho phép nhập số
    document.getElementById('otp').addEventListener('input', function(e) {
        this.value = this.value.replace(/[^0-9]/g, '');
    });
</script>
</body>
</html>
