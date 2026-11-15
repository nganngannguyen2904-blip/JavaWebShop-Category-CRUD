<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập Hệ Thống - Shopping Service MVC</title>
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
            padding: 25px 15px;
        }
        .login-card {
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
        .btn-login {
            background: #0d6efd;
            border: none;
            border-radius: 8px;
            padding: 12px;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.25s ease;
        }
        .btn-login:hover {
            background: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 6px 18px rgba(13, 110, 253, 0.35);
        }
        .quick-accounts {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 14px;
            font-size: 0.85rem;
            border: 1px dashed #ced4da;
        }
    </style>
</head>
<body>

<div class="login-card p-4 p-sm-5">
    <!-- Header -->
    <div class="text-center mb-4">
        <div class="icon-circle">
            <i class="fa fa-shield-halved"></i>
        </div>
        <h3 class="fw-bold text-dark mb-1">Đăng Nhập</h3>
        <p class="text-muted small mb-0">Hệ thống Mua sắm & Quản trị Shopping Service MVC</p>
    </div>

    <!-- Thông báo Alert -->
    <c:if test="${not empty alert}">
        <div class="alert alert-${not empty alertType ? alertType : 'danger'} alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-exclamation-circle me-2 flex-shrink-0"></i>
            <div class="small">${alert}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${param.registered == '1'}">
        <div class="alert alert-success alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-check-circle me-2 flex-shrink-0"></i>
            <div class="small">Đăng ký tài khoản thành công! Mật khẩu đã được mã hóa an toàn bằng BCrypt. Mời bạn đăng nhập.</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Form Đăng Nhập -->
    <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm" class="mt-2" novalidate>
        
        <!-- Identifier: Username hoặc Email -->
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Tài khoản / Email</label>
            <div class="input-group">
                <span class="input-group-text"><i class="fa fa-user"></i></span>
                <input type="text" 
                       class="form-control form-control-custom" 
                       id="username" 
                       name="username" 
                       value="<c:out value='${oldUsername}'/>"
                       placeholder="Nhập tên đăng nhập hoặc email" 
                       required 
                       autofocus>
            </div>
            <div class="invalid-feedback small d-none" id="usernameFeedback">Vui lòng nhập tài khoản hoặc email!</div>
        </div>

        <!-- Mật khẩu kèm nút Ẩn/Hiện -->
        <div class="mb-3">
            <div class="d-flex justify-content-between align-items-center">
                <label class="form-label text-secondary small fw-bold mb-1">Mật khẩu</label>
                <a href="${pageContext.request.contextPath}/forgot-password" class="text-primary small text-decoration-none" onclick="openForgotPasswordModal(event)">
                    Quên mật khẩu?
                </a>
            </div>
            <div class="input-group">
                <span class="input-group-text"><i class="fa fa-lock"></i></span>
                <input type="password" 
                       class="form-control form-control-custom" 
                       id="password" 
                       name="password" 
                       placeholder="Nhập mật khẩu" 
                       required>
                <button type="button" class="btn btn-outline-secondary btn-toggle-pass" id="btnTogglePassword" tabindex="-1">
                    <i class="fa fa-eye" id="toggleIcon"></i>
                </button>
            </div>
            <div class="invalid-feedback small d-none" id="passwordFeedback">Vui lòng nhập mật khẩu!</div>
        </div>

        <!-- Ghi nhớ đăng nhập -->
        <div class="mb-4 d-flex justify-content-between align-items-center">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="remember" id="remember">
                <label class="form-check-label text-secondary small" for="remember">
                    Ghi nhớ đăng nhập
                </label>
            </div>
            <span class="badge bg-light text-secondary border small">
                <i class="fa fa-lock me-1 text-success"></i>BCrypt Hash
            </span>
        </div>

        <!-- Nút Đăng nhập -->
        <button type="submit" class="btn btn-primary btn-login w-100 text-white" id="btnLoginSubmit">
            <i class="fa fa-sign-in me-2"></i>Đăng Nhập
        </button>

        <!-- Đăng ký tài khoản -->
        <div class="text-center mt-4 pt-2 border-top">
            <p class="text-secondary small mb-0">
                Chưa có tài khoản? 
                <a href="${pageContext.request.contextPath}/register" class="text-primary fw-semibold text-decoration-none">
                    Đăng ký tài khoản mới <i class="fa fa-arrow-right ms-1"></i>
                </a>
            </p>
        </div>
    </form>

    <!-- Thẻ gợi ý tài khoản mẫu phục vụ Giảng viên chấm bài -->
    <div class="quick-accounts mt-4">
        <div class="fw-bold text-dark mb-2 d-flex justify-content-between align-items-center">
            <span><i class="fa fa-key me-1 text-warning"></i>Tài khoản kiểm thử nhanh (Mật khẩu: <code>123456</code>):</span>
        </div>
        <div class="d-flex flex-wrap gap-2">
            <button type="button" class="btn btn-sm btn-outline-danger py-1 px-2" onclick="fillAccount('admin', '123456')">
                <i class="fa fa-crown me-1"></i>admin (Admin)
            </button>
            <button type="button" class="btn btn-sm btn-outline-primary py-1 px-2" onclick="fillAccount('trungnh', '123456')">
                <i class="fa fa-chalkboard-user me-1"></i>trungnh (GVHD)
            </button>
            <button type="button" class="btn btn-sm btn-outline-success py-1 px-2" onclick="fillAccount('khachhang', '123456')">
                <i class="fa fa-user me-1"></i>khachhang (User)
            </button>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 1. Nút Ẩn / Hiện mật khẩu
    const btnTogglePassword = document.getElementById('btnTogglePassword');
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.getElementById('toggleIcon');

    btnTogglePassword.addEventListener('click', function () {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        toggleIcon.classList.toggle('fa-eye');
        toggleIcon.classList.toggle('fa-eye-slash');
    });

    // 2. Điền nhanh tài khoản phục vụ chấm điểm
    function fillAccount(username, password) {
        document.getElementById('username').value = username;
        document.getElementById('password').value = password;
    }

    // 3. Client-side Validation & Trạng thái tải
    const form = document.getElementById('loginForm');
    const btnSubmit = document.getElementById('btnLoginSubmit');

    form.addEventListener('submit', function (e) {
        const usernameVal = document.getElementById('username').value.trim();
        const passwordVal = document.getElementById('password').value;
        let isValid = true;

        if (!usernameVal) {
            document.getElementById('usernameFeedback').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('usernameFeedback').classList.add('d-none');
        }

        if (!passwordVal) {
            document.getElementById('passwordFeedback').classList.remove('d-none');
            isValid = false;
        } else {
            document.getElementById('passwordFeedback').classList.add('d-none');
        }

        if (!isValid) {
            e.preventDefault();
        } else {
            btnSubmit.disabled = true;
            btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xác thực bảo mật...';
        }
    });

    // =========================================================================
    // MODAL POPUP QUÊN MẬT KHẨU GMAIL OTP THẬT
    // =========================================================================
    let forgotModal = null;
    let currentForgotEmail = '';
    let currentResetToken = '';
    let cooldownTimerInterval = null;
    let expiryTimerInterval = null;

    function openForgotPasswordModal(e) {
        if (e) e.preventDefault();
        if (!forgotModal) {
            forgotModal = new bootstrap.Modal(document.getElementById('forgotPasswordModal'));
            setupOtpBoxEvents();
        }
        showModalStep(1);
        hideModalAlert(1);
        hideModalAlert(2);
        hideModalAlert(3);
        const loginVal = document.getElementById('username').value.trim();
        if (loginVal && loginVal.includes('@')) {
            document.getElementById('modalForgotEmail').value = loginVal;
        }
        forgotModal.show();
    }

    function showModalStep(step) {
        document.getElementById('modalStep1').classList.toggle('d-none', step !== 1);
        document.getElementById('modalStep2').classList.toggle('d-none', step !== 2);
        document.getElementById('modalStep3').classList.toggle('d-none', step !== 3);
        document.getElementById('modalStep4').classList.toggle('d-none', step !== 4);
    }

    function showModalAlert(step, msg) {
        const alertEl = document.getElementById('modalStep' + step + 'Alert');
        const msgEl = document.getElementById('modalStep' + step + 'AlertMsg');
        if (alertEl && msgEl) {
            msgEl.textContent = msg;
            alertEl.classList.remove('d-none');
        }
    }

    function hideModalAlert(step) {
        const alertEl = document.getElementById('modalStep' + step + 'Alert');
        if (alertEl) alertEl.classList.add('d-none');
    }

    async function handleModalSendOtp(e) {
        e.preventDefault();
        hideModalAlert(1);
        const email = document.getElementById('modalForgotEmail').value.trim();
        if (!email) return;

        const btn = document.getElementById('btnModalSendOtp');
        const spinner = document.getElementById('btnModalSendOtpSpinner');
        const text = document.getElementById('btnModalSendOtpText');
        btn.disabled = true;
        spinner.classList.remove('d-none');
        text.innerHTML = 'Đang gửi mã...';

        try {
            const formData = new URLSearchParams();
            formData.append('email', email);

            const res = await fetch('${pageContext.request.contextPath}/api/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                currentForgotEmail = email;
                document.getElementById('displayModalMaskedEmail').textContent = data.maskedEmail || email;
                showModalStep(2);
                clearModalOtpInputs();
                startModalCooldownTimer(data.cooldown || 60);
                startModalExpiryTimer(300);
                setTimeout(() => {
                    const first = document.querySelector('.modal-otp-box[data-index="0"]');
                    if (first) first.focus();
                }, 300);
            } else {
                showModalAlert(1, data.message || 'Không thể gửi mã OTP.');
            }
        } catch (err) {
            showModalAlert(1, 'Lỗi kết nối: ' + err.message);
        } finally {
            btn.disabled = false;
            spinner.classList.add('d-none');
            text.innerHTML = '<i class="fa fa-paper-plane me-1"></i>Gửi Mã OTP';
        }
    }

    function setupOtpBoxEvents() {
        const boxes = document.querySelectorAll('.modal-otp-box');
        boxes.forEach((box, index) => {
            box.addEventListener('input', function() {
                const val = this.value.replace(/[^0-9]/g, '');
                this.value = val ? val.charAt(0) : '';
                if (this.value) {
                    this.classList.add('filled');
                    if (index < boxes.length - 1) boxes[index + 1].focus();
                } else {
                    this.classList.remove('filled');
                }
            });

            box.addEventListener('keydown', function(e) {
                if (e.key === 'Backspace') {
                    if (!this.value && index > 0) {
                        boxes[index - 1].focus();
                        boxes[index - 1].value = '';
                        boxes[index - 1].classList.remove('filled');
                    } else {
                        this.value = '';
                        this.classList.remove('filled');
                    }
                } else if (e.key === 'ArrowLeft' && index > 0) {
                    boxes[index - 1].focus();
                } else if (e.key === 'ArrowRight' && index < boxes.length - 1) {
                    boxes[index + 1].focus();
                }
            });

            box.addEventListener('paste', function(e) {
                e.preventDefault();
                const pasted = (e.clipboardData || window.clipboardData).getData('text').trim();
                const digits = pasted.replace(/[^0-9]/g, '').slice(0, 6);
                if (digits) {
                    digits.split('').forEach((d, i) => {
                        if (boxes[i]) {
                            boxes[i].value = d;
                            boxes[i].classList.add('filled');
                        }
                    });
                    const target = Math.min(digits.length, boxes.length - 1);
                    boxes[target].focus();
                    if (digits.length === 6) handleModalVerifyOtp();
                }
            });
        });
    }

    function clearModalOtpInputs() {
        document.querySelectorAll('.modal-otp-box').forEach(b => {
            b.value = '';
            b.classList.remove('filled');
        });
    }

    function getModalEnteredOtp() {
        let code = '';
        document.querySelectorAll('.modal-otp-box').forEach(b => code += b.value);
        return code.trim();
    }

    function startModalCooldownTimer(seconds) {
        clearInterval(cooldownTimerInterval);
        let remain = seconds;
        const coolSection = document.getElementById('modalCooldownSection');
        const resendSection = document.getElementById('modalResendSection');
        const coolSecEl = document.getElementById('modalCooldownSeconds');

        coolSection.classList.remove('d-none');
        resendSection.classList.add('d-none');
        coolSecEl.textContent = remain;

        cooldownTimerInterval = setInterval(() => {
            remain--;
            if (remain <= 0) {
                clearInterval(cooldownTimerInterval);
                coolSection.classList.add('d-none');
                resendSection.classList.remove('d-none');
            } else {
                coolSecEl.textContent = remain;
            }
        }, 1000);
    }

    function startModalExpiryTimer(totalSecs) {
        clearInterval(expiryTimerInterval);
        let remain = totalSecs;
        const timerEl = document.getElementById('modalOtpExpiryTimer');

        expiryTimerInterval = setInterval(() => {
            remain--;
            if (remain <= 0) {
                clearInterval(expiryTimerInterval);
                timerEl.textContent = 'HẾT HẠN';
                timerEl.classList.add('text-danger');
                showModalAlert(2, 'Mã OTP đã hết hạn sau 5 phút. Vui lòng gửi lại!');
            } else {
                const mins = Math.floor(remain / 60);
                const secs = remain % 60;
                timerEl.textContent = (mins < 10 ? '0' : '') + mins + ':' + (secs < 10 ? '0' : '') + secs;
            }
        }, 1000);
    }

    async function handleModalResendOtp() {
        hideModalAlert(2);
        const btn = document.getElementById('btnModalResendOtp');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang gửi lại...';

        try {
            const formData = new URLSearchParams();
            formData.append('email', currentForgotEmail);

            const res = await fetch('${pageContext.request.contextPath}/api/auth/resend-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                clearModalOtpInputs();
                startModalCooldownTimer(data.cooldown || 60);
                startModalExpiryTimer(300);
                const alertEl = document.getElementById('modalStep2Alert');
                alertEl.className = 'alert alert-success small d-flex align-items-center';
                document.getElementById('modalStep2AlertMsg').textContent = data.message;
                alertEl.classList.remove('d-none');
                setTimeout(() => alertEl.classList.add('d-none'), 4000);
            } else {
                showModalAlert(2, data.message || 'Không thể gửi lại mã OTP.');
                if (data.cooldown) startModalCooldownTimer(data.cooldown);
            }
        } catch (err) {
            showModalAlert(2, 'Lỗi kết nối: ' + err.message);
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<i class="fa fa-redo me-1"></i>Gửi lại mã OTP';
        }
    }

    async function handleModalVerifyOtp() {
        hideModalAlert(2);
        const otp = getModalEnteredOtp();
        if (otp.length < 6) {
            showModalAlert(2, 'Vui lòng nhập đủ 6 chữ số mã OTP!');
            return;
        }

        const btn = document.getElementById('btnModalVerifyOtp');
        const spinner = document.getElementById('btnModalVerifySpinner');
        const text = document.getElementById('btnModalVerifyText');
        btn.disabled = true;
        spinner.classList.remove('d-none');
        text.innerHTML = 'Đang xác nhận...';

        try {
            const formData = new URLSearchParams();
            formData.append('email', currentForgotEmail);
            formData.append('otp', otp);

            const res = await fetch('${pageContext.request.contextPath}/api/auth/verify-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                currentResetToken = data.resetToken;
                clearInterval(cooldownTimerInterval);
                clearInterval(expiryTimerInterval);
                showModalStep(3);
                hideModalAlert(3);
                document.getElementById('modalNewPassword').value = '';
                document.getElementById('modalConfirmPassword').value = '';
                setTimeout(() => document.getElementById('modalNewPassword').focus(), 300);
            } else {
                showModalAlert(2, data.message || 'Mã OTP không hợp lệ!');
                if (data.exceeded || data.expired) clearModalOtpInputs();
            }
        } catch (err) {
            showModalAlert(2, 'Lỗi kết nối: ' + err.message);
        } finally {
            btn.disabled = false;
            spinner.classList.add('d-none');
            text.innerHTML = '<i class="fa fa-check-circle me-1"></i>Xác Nhận OTP';
        }
    }

    async function handleModalResetPassword(e) {
        e.preventDefault();
        hideModalAlert(3);
        const newPass = document.getElementById('modalNewPassword').value;
        const confirmPass = document.getElementById('modalConfirmPassword').value;

        if (newPass.length < 6) {
            showModalAlert(3, 'Mật khẩu mới phải có tối thiểu 6 ký tự!');
            return;
        }
        if (newPass !== confirmPass) {
            showModalAlert(3, 'Mật khẩu mới và xác nhận mật khẩu không trùng khớp!');
            return;
        }

        const btn = document.getElementById('btnModalResetPassword');
        const spinner = document.getElementById('btnModalResetSpinner');
        const text = document.getElementById('btnModalResetText');
        btn.disabled = true;
        spinner.classList.remove('d-none');
        text.innerHTML = 'Đang lưu...';

        try {
            const formData = new URLSearchParams();
            formData.append('resetToken', currentResetToken);
            formData.append('newPassword', newPass);
            formData.append('confirmPassword', confirmPass);

            const res = await fetch('${pageContext.request.contextPath}/api/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                showModalStep(4);
                if (currentForgotEmail) {
                    document.getElementById('username').value = currentForgotEmail;
                    document.getElementById('password').value = '';
                }
            } else {
                showModalAlert(3, data.message || 'Đặt lại mật khẩu thất bại!');
                if (data.invalidSession) setTimeout(() => showModalStep(1), 2500);
            }
        } catch (err) {
            showModalAlert(3, 'Lỗi kết nối: ' + err.message);
        } finally {
            btn.disabled = false;
            spinner.classList.add('d-none');
            text.innerHTML = '<i class="fa fa-save me-1"></i>Lưu Mật Khẩu Mới';
        }
    }

    function toggleModalEye(inputId, btn) {
        const input = document.getElementById(inputId);
        const icon = btn.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    }
</script>

<!-- MODAL POPUP HTML -->
<div class="modal fade" id="forgotPasswordModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
            
            <!-- BƯỚC 1 -->
            <div id="modalStep1">
                <div class="p-4 text-center bg-danger text-white position-relative">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" data-bs-dismiss="modal"></button>
                    <div class="mb-2"><i class="fa fa-key fa-3x"></i></div>
                    <h5 class="fw-bold mb-1">Quên Mật Khẩu</h5>
                    <p class="mb-0 small text-white-50">Nhập email đăng ký để nhận mã OTP thật qua Gmail</p>
                </div>
                <div class="p-4">
                    <div id="modalStep1Alert" class="alert alert-danger d-none small d-flex align-items-center">
                        <i class="fa fa-exclamation-triangle me-2"></i><span id="modalStep1AlertMsg"></span>
                    </div>
                    <form onsubmit="handleModalSendOtp(event)">
                        <div class="mb-4">
                            <label class="form-label text-secondary small fw-bold">ĐỊA CHỈ EMAIL TÀI KHOẢN (GMAIL, EMAIL HCMUTE,...)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa fa-envelope text-muted"></i></span>
                                <input type="email" class="form-control" id="modalForgotEmail" placeholder="user@gmail.com hoặc sinhvien@student.hcmute.edu.vn" required>
                            </div>
                            <div class="form-text text-muted small mt-1">Hệ thống sẽ gửi mã OTP 6 chữ số đến địa chỉ email này.</div>
                        </div>
                        <button type="submit" id="btnModalSendOtp" class="btn btn-danger w-100 py-2 fw-semibold shadow-sm">
                            <span id="btnModalSendOtpSpinner" class="spinner-border spinner-border-sm me-2 d-none"></span>
                            <span id="btnModalSendOtpText"><i class="fa fa-paper-plane me-1"></i>Gửi Mã OTP Đến Gmail</span>
                        </button>
                    </form>
                </div>
            </div>

            <!-- BƯỚC 2: 6 Ô OTP -->
            <div id="modalStep2" class="d-none">
                <div class="p-4 text-center bg-primary text-white position-relative">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" data-bs-dismiss="modal"></button>
                    <div class="mb-2"><i class="fa fa-envelope-open-text fa-3x"></i></div>
                    <h5 class="fw-bold mb-1">Xác Nhận Mã OTP</h5>
                    <p class="mb-0 small text-white-50">Nhập mã 6 chữ số vừa nhận trong Gmail</p>
                </div>
                <div class="p-4">
                    <div id="modalStep2Alert" class="alert alert-danger d-none small d-flex align-items-center">
                        <i class="fa fa-exclamation-triangle me-2"></i><span id="modalStep2AlertMsg"></span>
                    </div>
                    <div class="text-center mb-2">
                        <span class="text-muted small">Mã OTP đã gửi đến: </span>
                        <strong id="displayModalMaskedEmail" class="text-primary"></strong>
                        <button type="button" class="btn btn-sm btn-link text-decoration-none py-0 ps-1" onclick="showModalStep(1)">
                            <i class="fa fa-edit"></i> Sửa email
                        </button>
                    </div>
                    <div class="alert alert-warning py-2 px-3 small border-0 mb-3" style="background-color: #fff8e1; color: #856404;">
                        <i class="fa fa-info-circle me-1 text-warning"></i>
                        <strong>Lưu ý:</strong> Vui lòng kiểm tra hộp thư đến (và cả mục <strong>Spam / Thư rác</strong> nếu không thấy email).
                    </div>
                    <div class="d-flex justify-content-center gap-2 mb-3">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="0" inputmode="numeric">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="1" inputmode="numeric">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="2" inputmode="numeric">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="3" inputmode="numeric">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="4" inputmode="numeric">
                        <input type="text" class="modal-otp-box form-control text-center fw-bold fs-4" style="width:46px;height:54px;" maxlength="1" data-index="5" inputmode="numeric">
                    </div>
                    <div class="d-flex justify-content-between align-items-center small text-muted mb-4 px-1">
                        <div><i class="fa fa-clock me-1 text-primary"></i>Hết hạn: <span id="modalOtpExpiryTimer" class="fw-semibold">05:00</span></div>
                        <div id="modalCooldownSection"><span>Gửi lại sau: </span><span id="modalCooldownSeconds" class="fw-bold text-danger">60</span>s</div>
                        <div id="modalResendSection" class="d-none">
                            <button type="button" id="btnModalResendOtp" class="btn btn-link p-0 text-decoration-none fw-semibold small" onclick="handleModalResendOtp()">
                                <i class="fa fa-redo me-1"></i>Gửi lại mã OTP
                            </button>
                        </div>
                    </div>
                    <button type="button" id="btnModalVerifyOtp" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm mb-2" onclick="handleModalVerifyOtp()">
                        <span id="btnModalVerifySpinner" class="spinner-border spinner-border-sm me-2 d-none"></span>
                        <span id="btnModalVerifyText"><i class="fa fa-check-circle me-1"></i>Xác Nhận OTP</span>
                    </button>
                </div>
            </div>

            <!-- BƯỚC 3: ĐẶT MẬT KHẨU MỚI -->
            <div id="modalStep3" class="d-none">
                <div class="p-4 text-center bg-success text-white position-relative">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" data-bs-dismiss="modal"></button>
                    <div class="mb-2"><i class="fa fa-shield-alt fa-3x"></i></div>
                    <h5 class="fw-bold mb-1">Đặt Lại Mật Khẩu</h5>
                    <p class="mb-0 small text-white-50">Xác thực OTP thành công! Thiết lập mật khẩu mới</p>
                </div>
                <div class="p-4">
                    <div id="modalStep3Alert" class="alert alert-danger d-none small d-flex align-items-center">
                        <i class="fa fa-exclamation-triangle me-2"></i><span id="modalStep3AlertMsg"></span>
                    </div>
                    <form onsubmit="handleModalResetPassword(event)">
                        <div class="mb-3">
                            <label class="form-label text-secondary small fw-bold">MẬT KHẨU MỚI</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                                <input type="password" class="form-control" id="modalNewPassword" placeholder="Tối thiểu 6 ký tự" minlength="6" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="toggleModalEye('modalNewPassword', this)"><i class="fa fa-eye"></i></button>
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label text-secondary small fw-bold">XÁC NHẬN MẬT KHẨU MỚI</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                                <input type="password" class="form-control" id="modalConfirmPassword" placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="toggleModalEye('modalConfirmPassword', this)"><i class="fa fa-eye"></i></button>
                            </div>
                        </div>
                        <button type="submit" id="btnModalResetPassword" class="btn btn-success w-100 py-2 fw-semibold shadow-sm">
                            <span id="btnModalResetSpinner" class="spinner-border spinner-border-sm me-2 d-none"></span>
                            <span id="btnModalResetText"><i class="fa fa-save me-1"></i>Lưu Mật Khẩu Mới</span>
                        </button>
                    </form>
                </div>
            </div>

            <!-- BƯỚC 4: THÀNH CÔNG -->
            <div id="modalStep4" class="d-none text-center p-5">
                <div class="mb-3 text-success"><i class="fa fa-check-circle fa-4x"></i></div>
                <h4 class="fw-bold text-dark mb-2">Đặt Lại Mật Khẩu Thành Công!</h4>
                <p class="text-muted small mb-4">Mật khẩu mới đã được cập nhật an toàn vào hệ thống.<br>Vui lòng đăng nhập bằng mật khẩu mới.</p>
                <button type="button" class="btn btn-primary px-4 py-2 fw-semibold shadow-sm" data-bs-dismiss="modal">
                    <i class="fa fa-sign-in-alt me-2"></i>Đăng Nhập Ngay
                </button>
            </div>

        </div>
    </div>
</div>


</body>
</html>
