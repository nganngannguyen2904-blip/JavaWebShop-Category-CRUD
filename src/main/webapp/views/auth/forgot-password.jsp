<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - Shopping Service MVC</title>
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
        /* 6 ô OTP riêng biệt */
        .otp-box-container {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin: 20px 0;
        }
        .otp-box {
            width: 48px;
            height: 56px;
            font-size: 24px;
            font-weight: 700;
            text-align: center;
            border: 2px solid #dee2e6;
            border-radius: 10px;
            background-color: #f8f9fa;
            color: #0d6efd;
            font-family: 'Courier New', Courier, monospace;
            transition: all 0.2s ease-in-out;
        }
        .otp-box:focus {
            border-color: #0d6efd;
            background-color: #ffffff;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
            outline: none;
            transform: scale(1.05);
        }
        .otp-box.filled {
            border-color: #0d6efd;
            background-color: #e7f1ff;
        }
    </style>
</head>
<body>

<div class="auth-card p-4 p-sm-5">

    <!-- BƯỚC 1: NHẬP GMAIL -->
    <div id="panelStep1">
        <div class="text-center mb-4">
            <div class="icon-circle">
                <i class="fa fa-key"></i>
            </div>
            <h3 class="fw-bold text-dark mb-1">Quên Mật Khẩu</h3>
            <p class="text-muted small mb-0">Nhập email đăng ký để nhận mã OTP khôi phục thật qua Gmail</p>
        </div>

        <div id="step1Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
            <i class="fa fa-exclamation-triangle me-2"></i>
            <span id="step1AlertMsg"></span>
        </div>

        <form id="formStep1" onsubmit="handleSendOtp(event)">
            <div class="mb-4">
                <label for="forgotEmail" class="form-label text-secondary small fw-bold">ĐỊA CHỈ EMAIL TÀI KHOẢN (GMAIL, EMAIL HCMUTE,...)</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-envelope text-muted"></i></span>
                    <input type="email" class="form-control" id="forgotEmail" placeholder="user@gmail.com hoặc sinhvien@student.hcmute.edu.vn" required autofocus>
                </div>
                <div class="form-text text-muted small mt-1">
                    Hệ thống sẽ gửi mã OTP 6 chữ số đến địa chỉ email này để xác thực.
                </div>
            </div>

            <button type="submit" id="btnSendOtp" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm mb-3">
                <span id="btnSendOtpSpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
                <span id="btnSendOtpText"><i class="fa fa-paper-plane me-1"></i>Gửi Mã OTP Đến Gmail</span>
            </button>
        </form>

        <div class="text-center pt-2 border-top">
            <a href="${pageContext.request.contextPath}/login" class="text-secondary small text-decoration-none">
                <i class="fa fa-arrow-left me-1"></i>Quay lại trang Đăng nhập
            </a>
        </div>
    </div>

    <!-- BƯỚC 2: NHẬP 6 Ô OTP & BỘ ĐẾM 60 GIÂY -->
    <div id="panelStep2" class="d-none">
        <div class="text-center mb-3">
            <div class="icon-circle" style="background: rgba(13, 110, 253, 0.1); color: #0d6efd;">
                <i class="fa fa-envelope-open-text"></i>
            </div>
            <h3 class="fw-bold text-dark mb-1">Xác Nhận Mã OTP</h3>
            <p class="text-muted small mb-0">Nhập mã 6 chữ số vừa nhận trong Gmail</p>
        </div>

        <div id="step2Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
            <i class="fa fa-exclamation-triangle me-2"></i>
            <span id="step2AlertMsg"></span>
        </div>

        <div class="text-center mb-2">
            <span class="text-muted small">Mã OTP đã gửi đến: </span>
            <strong id="displayMaskedEmail" class="text-primary"></strong>
            <button type="button" class="btn btn-sm btn-link text-decoration-none py-0 ps-1" onclick="backToStep1()">
                <i class="fa fa-edit"></i> Sửa email
            </button>
        </div>

        <!-- Lưu ý kiểm tra cả Spam -->
        <div class="alert alert-warning py-2 px-3 small border-0 mb-3" style="background-color: #fff8e1; color: #856404;">
            <i class="fa fa-info-circle me-1 text-warning"></i>
            <strong>Lưu ý:</strong> Vui lòng kiểm tra hộp thư đến (và cả mục <strong>Spam / Thư rác</strong> nếu không thấy email).
        </div>

        <!-- 6 Ô NHẬP OTP -->
        <div class="otp-box-container" id="otpBoxContainer">
            <input type="text" class="otp-box" maxlength="1" data-index="0" inputmode="numeric" pattern="[0-9]*" autocomplete="off" autofocus>
            <input type="text" class="otp-box" maxlength="1" data-index="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
            <input type="text" class="otp-box" maxlength="1" data-index="2" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
            <input type="text" class="otp-box" maxlength="1" data-index="3" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
            <input type="text" class="otp-box" maxlength="1" data-index="4" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
            <input type="text" class="otp-box" maxlength="1" data-index="5" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
        </div>

        <!-- Timer & Cooldown -->
        <div class="d-flex justify-content-between align-items-center small text-muted mb-4 px-1">
            <div>
                <i class="fa fa-clock me-1 text-primary"></i>Hết hạn: <span id="otpExpiryTimer" class="fw-semibold">05:00</span>
            </div>
            <div id="cooldownSection">
                <span>Gửi lại sau: </span>
                <span id="cooldownSeconds" class="fw-bold text-danger">60</span>s
            </div>
            <div id="resendSection" class="d-none">
                <button type="button" id="btnResendOtp" class="btn btn-link p-0 text-decoration-none fw-semibold small" onclick="handleResendOtp()">
                    <i class="fa fa-redo me-1"></i>Gửi lại mã OTP
                </button>
            </div>
        </div>

        <button type="button" id="btnVerifyOtp" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm mb-3" onclick="handleVerifyOtp()">
            <span id="btnVerifySpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
            <span id="btnVerifyText"><i class="fa fa-check-circle me-1"></i>Xác Nhận OTP</span>
        </button>

        <div class="text-center pt-2 border-top">
            <button type="button" class="btn btn-link text-secondary small text-decoration-none p-0" onclick="backToStep1()">
                <i class="fa fa-arrow-left me-1"></i>Quay lại bước trước
            </button>
        </div>
    </div>

    <!-- BƯỚC 3: ĐẶT LẠI MẬT KHẨU MỚI -->
    <div id="panelStep3" class="d-none">
        <div class="text-center mb-3">
            <div class="icon-circle" style="background: rgba(25, 135, 84, 0.1); color: #198754;">
                <i class="fa fa-shield-alt"></i>
            </div>
            <h3 class="fw-bold text-dark mb-1">Đặt Lại Mật Khẩu</h3>
            <p class="text-muted small mb-0">Xác thực OTP thành công! Thiết lập mật khẩu mới</p>
        </div>

        <div id="step3Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
            <i class="fa fa-exclamation-triangle me-2"></i>
            <span id="step3AlertMsg"></span>
        </div>

        <form id="formStep3" onsubmit="handleResetPassword(event)">
            <div class="mb-3">
                <label for="newPassword" class="form-label text-secondary small fw-bold">MẬT KHẨU MỚI</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                    <input type="password" class="form-control" id="newPassword" placeholder="Tối thiểu 6 ký tự" minlength="6" required>
                    <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('newPassword', this)">
                        <i class="fa fa-eye"></i>
                    </button>
                </div>
            </div>

            <div class="mb-4">
                <label for="confirmPassword" class="form-label text-secondary small fw-bold">XÁC NHẬN MẬT KHẨU MỚI</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="fa fa-lock text-muted"></i></span>
                    <input type="password" class="form-control" id="confirmPassword" placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                    <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('confirmPassword', this)">
                        <i class="fa fa-eye"></i>
                    </button>
                </div>
            </div>

            <button type="submit" id="btnResetPassword" class="btn btn-success w-100 py-2 fw-semibold shadow-sm mb-3">
                <span id="btnResetSpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
                <span id="btnResetText"><i class="fa fa-save me-1"></i>Lưu Mật Khẩu Mới</span>
            </button>
        </form>
    </div>

    <!-- BƯỚC 4: THÀNH CÔNG -->
    <div id="panelStep4" class="d-none text-center py-4">
        <div class="mb-3 text-success">
            <i class="fa fa-check-circle fa-4x"></i>
        </div>
        <h4 class="fw-bold text-dark mb-2">Đặt Lại Mật Khẩu Thành Công!</h4>
        <p class="text-muted small mb-4">Mật khẩu mới đã được cập nhật an toàn vào hệ thống.<br>Vui lòng đăng nhập bằng mật khẩu mới.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary px-4 py-2 fw-semibold shadow-sm">
            <i class="fa fa-sign-in-alt me-2"></i>Đến Trang Đăng Nhập
        </a>
    </div>

</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const CONTEXT_PATH = '${pageContext.request.contextPath}';
    let currentEmail = '';
    let currentResetToken = '';
    let cooldownTimerInterval = null;
    let expiryTimerInterval = null;

    document.addEventListener("DOMContentLoaded", function() {
        setupOtpBoxEvents();
    });

    function showPanel(stepNum) {
        document.getElementById('panelStep1').classList.toggle('d-none', stepNum !== 1);
        document.getElementById('panelStep2').classList.toggle('d-none', stepNum !== 2);
        document.getElementById('panelStep3').classList.toggle('d-none', stepNum !== 3);
        document.getElementById('panelStep4').classList.toggle('d-none', stepNum !== 4);
    }

    function showAlert(stepNum, msg) {
        const el = document.getElementById('step' + stepNum + 'Alert');
        const textEl = document.getElementById('step' + stepNum + 'AlertMsg');
        if (el && textEl) {
            textEl.textContent = msg;
            el.classList.remove('d-none');
        }
    }

    function hideAlert(stepNum) {
        const el = document.getElementById('step' + stepNum + 'Alert');
        if (el) el.classList.add('d-none');
    }

    // ==================== BƯỚC 1: GỬI MÃ OTP ====================
    async function handleSendOtp(e) {
        e.preventDefault();
        hideAlert(1);

        const email = document.getElementById('forgotEmail').value.trim();
        if (!email) return;

        setLoading('btnSendOtp', 'btnSendOtpSpinner', 'btnSendOtpText', true, 'Đang gửi mã...');

        try {
            const formData = new URLSearchParams();
            formData.append('email', email);

            const res = await fetch(CONTEXT_PATH + '/api/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                currentEmail = email;
                document.getElementById('displayMaskedEmail').textContent = data.maskedEmail || email;
                showPanel(2);
                clearOtpInputs();
                startCooldownTimer(data.cooldown || 60);
                startExpiryTimer(300);
                setTimeout(() => {
                    const firstBox = document.querySelector('.otp-box[data-index="0"]');
                    if (firstBox) firstBox.focus();
                }, 300);
            } else {
                showAlert(1, data.message || 'Không thể gửi mã OTP.');
            }
        } catch (err) {
            showAlert(1, 'Lỗi kết nối máy chủ: ' + err.message);
        } finally {
            setLoading('btnSendOtp', 'btnSendOtpSpinner', 'btnSendOtpText', false, '<i class="fa fa-paper-plane me-1"></i>Gửi Mã OTP Đến Gmail');
        }
    }

    function backToStep1() {
        showPanel(1);
        clearInterval(cooldownTimerInterval);
        clearInterval(expiryTimerInterval);
    }

    // ==================== BƯỚC 2: 6 Ô OTP & BỘ ĐẾM 60S ====================
    function setupOtpBoxEvents() {
        const boxes = document.querySelectorAll('.otp-box');

        boxes.forEach((box, index) => {
            box.addEventListener('input', function(e) {
                const val = this.value.replace(/[^0-9]/g, '');
                this.value = val ? val.charAt(0) : '';

                if (this.value) {
                    this.classList.add('filled');
                    if (index < boxes.length - 1) {
                        boxes[index + 1].focus();
                    }
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
                const pastedData = (e.clipboardData || window.clipboardData).getData('text').trim();
                const digits = pastedData.replace(/[^0-9]/g, '').slice(0, 6);

                if (digits) {
                    digits.split('').forEach((digit, i) => {
                        if (boxes[i]) {
                            boxes[i].value = digit;
                            boxes[i].classList.add('filled');
                        }
                    });
                    const targetFocus = Math.min(digits.length, boxes.length - 1);
                    boxes[targetFocus].focus();

                    if (digits.length === 6) {
                        handleVerifyOtp();
                    }
                }
            });
        });
    }

    function clearOtpInputs() {
        const boxes = document.querySelectorAll('.otp-box');
        boxes.forEach(b => {
            b.value = '';
            b.classList.remove('filled');
        });
    }

    function getEnteredOtp() {
        const boxes = document.querySelectorAll('.otp-box');
        let code = '';
        boxes.forEach(b => code += b.value);
        return code.trim();
    }

    function startCooldownTimer(seconds) {
        clearInterval(cooldownTimerInterval);
        let remain = seconds;
        const cooldownSection = document.getElementById('cooldownSection');
        const resendSection = document.getElementById('resendSection');
        const cooldownSecondsEl = document.getElementById('cooldownSeconds');

        cooldownSection.classList.remove('d-none');
        resendSection.classList.add('d-none');
        cooldownSecondsEl.textContent = remain;

        cooldownTimerInterval = setInterval(() => {
            remain--;
            if (remain <= 0) {
                clearInterval(cooldownTimerInterval);
                cooldownSection.classList.add('d-none');
                resendSection.classList.remove('d-none');
            } else {
                cooldownSecondsEl.textContent = remain;
            }
        }, 1000);
    }

    function startExpiryTimer(totalSecs) {
        clearInterval(expiryTimerInterval);
        let remain = totalSecs;
        const timerEl = document.getElementById('otpExpiryTimer');

        expiryTimerInterval = setInterval(() => {
            remain--;
            if (remain <= 0) {
                clearInterval(expiryTimerInterval);
                timerEl.textContent = 'HẾT HẠN';
                timerEl.classList.add('text-danger');
                showAlert(2, 'Mã OTP đã hết hạn sau 5 phút. Vui lòng nhấn gửi lại mã!');
            } else {
                const mins = Math.floor(remain / 60);
                const secs = remain % 60;
                timerEl.textContent = (mins < 10 ? '0' : '') + mins + ':' + (secs < 10 ? '0' : '') + secs;
            }
        }, 1000);
    }

    async function handleResendOtp() {
        hideAlert(2);
        const resendBtn = document.getElementById('btnResendOtp');
        resendBtn.disabled = true;
        resendBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang gửi lại...';

        try {
            const formData = new URLSearchParams();
            formData.append('email', currentEmail);

            const res = await fetch(CONTEXT_PATH + '/api/auth/resend-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                clearOtpInputs();
                startCooldownTimer(data.cooldown || 60);
                startExpiryTimer(300);
                const alertEl = document.getElementById('step2Alert');
                alertEl.className = 'alert alert-success small d-flex align-items-center';
                document.getElementById('step2AlertMsg').textContent = data.message;
                alertEl.classList.remove('d-none');
                setTimeout(() => alertEl.classList.add('d-none'), 4000);
            } else {
                showAlert(2, data.message || 'Không thể gửi lại mã OTP.');
                if (data.cooldown) {
                    startCooldownTimer(data.cooldown);
                }
            }
        } catch (err) {
            showAlert(2, 'Lỗi kết nối máy chủ: ' + err.message);
        } finally {
            resendBtn.disabled = false;
            resendBtn.innerHTML = '<i class="fa fa-redo me-1"></i>Gửi lại mã OTP';
        }
    }

    async function handleVerifyOtp() {
        hideAlert(2);
        const otp = getEnteredOtp();
        if (otp.length < 6) {
            showAlert(2, 'Vui lòng nhập đủ 6 chữ số mã OTP!');
            return;
        }

        setLoading('btnVerifyOtp', 'btnVerifySpinner', 'btnVerifyText', true, 'Đang xác nhận...');

        try {
            const formData = new URLSearchParams();
            formData.append('email', currentEmail);
            formData.append('otp', otp);

            const res = await fetch(CONTEXT_PATH + '/api/auth/verify-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                currentResetToken = data.resetToken;
                clearInterval(cooldownTimerInterval);
                clearInterval(expiryTimerInterval);
                showPanel(3);
                hideAlert(3);
                document.getElementById('newPassword').value = '';
                document.getElementById('confirmPassword').value = '';
                setTimeout(() => document.getElementById('newPassword').focus(), 300);
            } else {
                showAlert(2, data.message || 'Mã OTP không hợp lệ!');
                if (data.exceeded || data.expired) {
                    clearOtpInputs();
                }
            }
        } catch (err) {
            showAlert(2, 'Lỗi kết nối máy chủ: ' + err.message);
        } finally {
            setLoading('btnVerifyOtp', 'btnVerifySpinner', 'btnVerifyText', false, '<i class="fa fa-check-circle me-1"></i>Xác Nhận OTP');
        }
    }

    // ==================== BƯỚC 3: ĐẶT LẠI MẬT KHẨU MỚI ====================
    async function handleResetPassword(e) {
        e.preventDefault();
        hideAlert(3);

        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword').value;

        if (newPass.length < 6) {
            showAlert(3, 'Mật khẩu mới phải có tối thiểu 6 ký tự!');
            return;
        }

        if (newPass !== confirmPass) {
            showAlert(3, 'Mật khẩu mới và xác nhận mật khẩu không trùng khớp!');
            return;
        }

        setLoading('btnResetPassword', 'btnResetSpinner', 'btnResetText', true, 'Đang lưu...');

        try {
            const formData = new URLSearchParams();
            formData.append('resetToken', currentResetToken);
            formData.append('newPassword', newPass);
            formData.append('confirmPassword', confirmPass);

            const res = await fetch(CONTEXT_PATH + '/api/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: formData
            });
            const data = await res.json();

            if (data.success) {
                showPanel(4);
            } else {
                showAlert(3, data.message || 'Đặt lại mật khẩu thất bại!');
                if (data.invalidSession) {
                    setTimeout(() => showPanel(1), 2500);
                }
            }
        } catch (err) {
            showAlert(3, 'Lỗi kết nối máy chủ: ' + err.message);
        } finally {
            setLoading('btnResetPassword', 'btnResetSpinner', 'btnResetText', false, '<i class="fa fa-save me-1"></i>Lưu Mật Khẩu Mới');
        }
    }

    function togglePasswordVisibility(inputId, btn) {
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

    function setLoading(btnId, spinnerId, textId, isLoading, loadingText) {
        const btn = document.getElementById(btnId);
        const spinner = document.getElementById(spinnerId);
        const text = document.getElementById(textId);

        if (!btn || !spinner || !text) return;

        btn.disabled = isLoading;
        if (isLoading) {
            spinner.classList.remove('d-none');
            text.innerHTML = loadingText;
        } else {
            spinner.classList.add('d-none');
            text.innerHTML = loadingText;
        }
    }
</script>
</body>
</html>
