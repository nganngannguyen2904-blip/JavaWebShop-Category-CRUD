<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập - Hệ Thống CRUD Category JPA</title>
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
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
            color: #ffffff;
            padding: 30px 24px;
            text-align: center;
        }
        .auth-body {
            padding: 32px 28px;
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
        
        /* Modal Popup Styling */
        .modal-content {
            border-radius: 18px;
            border: none;
            box-shadow: 0 15px 40px rgba(0,0,0,0.2);
            overflow: hidden;
        }
        .modal-header-custom {
            padding: 24px;
            color: #ffffff;
            text-align: center;
            position: relative;
        }
        .modal-header-custom.bg-step1 {
            background: linear-gradient(135deg, #dc3545 0%, #bb2d3b 100%);
        }
        .modal-header-custom.bg-step2 {
            background: linear-gradient(135deg, #0d6efd 0%, #0b5ed7 100%);
        }
        .modal-header-custom.bg-step3 {
            background: linear-gradient(135deg, #198754 0%, #157347 100%);
        }
        .modal-body {
            padding: 28px 24px;
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

<div class="auth-card">
    <div class="auth-header">
        <div class="mb-2"><i class="fa fa-user-circle fa-3x"></i></div>
        <h4 class="fw-bold mb-1">Đăng Nhập Hệ Thống</h4>
        <p class="mb-0 small text-white-50">Bài tập JPA 3.0 CRUD Category & Xác thực OTP</p>
    </div>

    <div class="auth-body">
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

        <form action="${pageContext.request.contextPath}/login" method="POST" id="mainLoginForm">
            <div class="mb-3">
                <label for="email" class="form-label fw-semibold text-secondary small">ĐỊA CHỈ GMAIL</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-envelope"></i></span>
                    <input type="email" class="form-control" id="loginEmail" name="email" 
                           value="${email}" placeholder="example@gmail.com" required autofocus>
                </div>
            </div>

            <div class="mb-3">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <label for="password" class="form-label fw-semibold text-secondary small mb-0">MẬT KHẨU</label>
                    <!-- Nút mở Popup Quên Mật Khẩu -->
                    <a href="javascript:void(0)" onclick="openForgotPasswordModal()" class="text-decoration-none small text-primary fw-semibold">
                        <i class="fa fa-question-circle me-1"></i>Quên mật khẩu?
                    </a>
                </div>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted"><i class="fa fa-lock"></i></span>
                    <input type="password" class="form-control" id="loginPassword" name="password" 
                           placeholder="Nhập mật khẩu" required>
                </div>
            </div>

            <div class="mb-4 form-check">
                <input type="checkbox" class="form-check-input" id="rememberMe">
                <label class="form-check-label text-muted small" for="rememberMe">Ghi nhớ đăng nhập</label>
            </div>

            <button type="submit" class="btn btn-primary w-100 shadow-sm mb-3">
                <i class="fa fa-sign-in-alt me-2"></i>Đăng Nhập
            </button>
        </form>

        <div class="text-center pt-2 border-top">
            <span class="text-muted small">Chưa có tài khoản?</span>
            <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-semibold ms-1 small">
                Đăng ký tài khoản mới
            </a>
        </div>

        <div class="text-center mt-3 pt-2">
            <a href="${pageContext.request.contextPath}/admin/category" class="btn btn-sm btn-outline-secondary">
                <i class="fa fa-folder-open me-1"></i>Vào thẳng Quản lý Category (JPA)
            </a>
        </div>
    </div>
</div>

<!-- ========================================================================================= -->
<!-- MODAL POPUP QUÊN MẬT KHẨU BẰNG GMAIL OTP (3 BƯỚC THỰC TẾ) -->
<!-- ========================================================================================= -->
<div class="modal fade" id="forgotPasswordModal" tabindex="-1" aria-labelledby="forgotPasswordModalLabel" 
     aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            
            <!-- BƯỚC 1: NHẬP ĐỊA CHỈ GMAIL -->
            <div id="modalStep1">
                <div class="modal-header-custom bg-step1">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" 
                            data-bs-dismiss="modal" aria-label="Close"></button>
                    <div class="mb-2"><i class="fa fa-key fa-3x"></i></div>
                    <h5 class="fw-bold mb-1" id="forgotPasswordModalLabel">Quên Mật Khẩu</h5>
                    <p class="mb-0 small text-white-50">Nhập Gmail đã đăng ký để nhận mã xác thực OTP</p>
                </div>
                <div class="modal-body">
                    <div id="step1Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
                        <i class="fa fa-exclamation-triangle me-2"></i>
                        <span id="step1AlertMsg"></span>
                    </div>

                    <form id="formStep1" onsubmit="handleSendOtp(event)">
                        <div class="mb-4">
                            <label for="forgotEmail" class="form-label fw-semibold text-secondary small">ĐỊA CHỈ GMAIL ĐÃ ĐĂNG KÝ</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light text-muted"><i class="fa fa-envelope"></i></span>
                                <input type="email" class="form-control" id="forgotEmail" placeholder="example@gmail.com" required>
                            </div>
                            <div class="form-text text-muted small mt-1">
                                Hệ thống sẽ kiểm tra tài khoản và gửi mã OTP 6 chữ số đến Gmail này.
                            </div>
                        </div>

                        <button type="submit" id="btnSendOtp" class="btn btn-danger w-100 shadow-sm py-2 fw-semibold">
                            <span id="btnSendOtpSpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
                            <span id="btnSendOtpText"><i class="fa fa-paper-plane me-2"></i>Gửi Mã OTP</span>
                        </button>
                    </form>
                </div>
            </div>

            <!-- BƯỚC 2: NHẬP 6 Ô MÃ OTP & BỘ ĐẾM 60 GIÂY -->
            <div id="modalStep2" class="d-none">
                <div class="modal-header-custom bg-step2">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" 
                            data-bs-dismiss="modal" aria-label="Close"></button>
                    <div class="mb-2"><i class="fa fa-shield-alt fa-3x"></i></div>
                    <h5 class="fw-bold mb-1">Xác Nhận Mã OTP</h5>
                    <p class="mb-0 small text-white-50">Mã 6 chữ số đã được gửi qua Gmail</p>
                </div>
                <div class="modal-body">
                    <div id="step2Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
                        <i class="fa fa-exclamation-triangle me-2"></i>
                        <span id="step2AlertMsg"></span>
                    </div>

                    <div class="text-center mb-2">
                        <span class="text-muted small">Đã gửi mã đến: </span>
                        <strong id="displayMaskedEmail" class="text-dark"></strong>
                        <button type="button" class="btn btn-sm btn-link text-decoration-none py-0 ps-1" onclick="backToStep1()">
                            <i class="fa fa-edit"></i> Sửa email
                        </button>
                    </div>

                    <!-- Thông báo kiểm tra cả thư mục Spam -->
                    <div class="alert alert-warning py-2 px-3 small border-0 mb-3" style="background-color: #fff8e1; color: #856404;">
                        <i class="fa fa-info-circle me-1 text-warning"></i>
                        <strong>Lưu ý:</strong> Vui lòng kiểm tra hộp thư đến (và cả mục <strong>Spam / Thư rác</strong> nếu không thấy email).
                    </div>

                    <!-- 6 Ô NHẬP MÃ OTP RIÊNG BIỆT -->
                    <div class="otp-box-container" id="otpBoxContainer">
                        <input type="text" class="otp-box" maxlength="1" data-index="0" inputmode="numeric" pattern="[0-9]*" autocomplete="off" autofocus>
                        <input type="text" class="otp-box" maxlength="1" data-index="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
                        <input type="text" class="otp-box" maxlength="1" data-index="2" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
                        <input type="text" class="otp-box" maxlength="1" data-index="3" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
                        <input type="text" class="otp-box" maxlength="1" data-index="4" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
                        <input type="text" class="otp-box" maxlength="1" data-index="5" inputmode="numeric" pattern="[0-9]*" autocomplete="off">
                    </div>

                    <!-- Thời hạn OTP & Bộ đếm gửi lại 60s -->
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

                    <button type="button" id="btnVerifyOtp" class="btn btn-primary w-100 shadow-sm py-2 fw-semibold" onclick="handleVerifyOtp()">
                        <span id="btnVerifySpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
                        <span id="btnVerifyText"><i class="fa fa-check-circle me-2"></i>Xác Nhận OTP</span>
                    </button>
                </div>
            </div>

            <!-- BƯỚC 3: ĐẶT LẠI MẬT KHẨU MỚI -->
            <div id="modalStep3" class="d-none">
                <div class="modal-header-custom bg-step3">
                    <button type="button" class="btn-close btn-close-white position-absolute top-0 end-0 m-3" 
                            data-bs-dismiss="modal" aria-label="Close"></button>
                    <div class="mb-2"><i class="fa fa-lock fa-3x"></i></div>
                    <h5 class="fw-bold mb-1">Đặt Lại Mật Khẩu Mới</h5>
                    <p class="mb-0 small text-white-50">Xác thực thành công! Hãy tạo mật khẩu mới an toàn</p>
                </div>
                <div class="modal-body">
                    <div id="step3Alert" class="alert alert-danger d-none small d-flex align-items-center" role="alert">
                        <i class="fa fa-exclamation-triangle me-2"></i>
                        <span id="step3AlertMsg"></span>
                    </div>

                    <form id="formStep3" onsubmit="handleResetPassword(event)">
                        <div class="mb-3">
                            <label for="newPassword" class="form-label fw-semibold text-secondary small">MẬT KHẨU MỚI</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light text-muted"><i class="fa fa-lock"></i></span>
                                <input type="password" class="form-control" id="newPassword" placeholder="Tối thiểu 6 ký tự" minlength="6" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('newPassword', this)">
                                    <i class="fa fa-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="confirmPassword" class="form-label fw-semibold text-secondary small">XÁC NHẬN MẬT KHẨU MỚI</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light text-muted"><i class="fa fa-shield-alt"></i></span>
                                <input type="password" class="form-control" id="confirmPassword" placeholder="Nhập lại mật khẩu mới" minlength="6" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('confirmPassword', this)">
                                    <i class="fa fa-eye"></i>
                                </button>
                            </div>
                        </div>

                        <button type="submit" id="btnResetPassword" class="btn btn-success w-100 shadow-sm py-2 fw-semibold">
                            <span id="btnResetSpinner" class="spinner-border spinner-border-sm me-2 d-none" role="status"></span>
                            <span id="btnResetText"><i class="fa fa-save me-2"></i>Đặt Lại Mật Khẩu</span>
                        </button>
                    </form>
                </div>
            </div>

            <!-- BƯỚC 4: THÀNH CÔNG HOÀN TẤT -->
            <div id="modalStep4" class="d-none">
                <div class="modal-body text-center py-5">
                    <div class="mb-3 text-success">
                        <i class="fa fa-check-circle fa-4x"></i>
                    </div>
                    <h4 class="fw-bold text-dark mb-2">Đặt Lại Mật Khẩu Thành Công!</h4>
                    <p class="text-muted small mb-4">Mật khẩu của bạn đã được cập nhật an toàn với chuẩn mã hóa BCrypt.<br>Vui lòng đăng nhập bằng mật khẩu mới.</p>
                    <button type="button" class="btn btn-primary px-4 py-2 fw-semibold shadow-sm" onclick="finishAndLogin()">
                        <i class="fa fa-sign-in-alt me-2"></i>Đăng Nhập Ngay
                    </button>
                </div>
            </div>

        </div>
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
    let forgotModal = null;

    document.addEventListener("DOMContentLoaded", function() {
        forgotModal = new bootstrap.Modal(document.getElementById('forgotPasswordModal'));
        setupOtpBoxEvents();
    });

    function openForgotPasswordModal() {
        // Reset về Step 1
        showStep(1);
        hideAlert(1);
        hideAlert(2);
        hideAlert(3);
        const loginEmailVal = document.getElementById('loginEmail').value.trim();
        if (loginEmailVal) {
            document.getElementById('forgotEmail').value = loginEmailVal;
        }
        forgotModal.show();
    }

    function showStep(stepNum) {
        document.getElementById('modalStep1').classList.toggle('d-none', stepNum !== 1);
        document.getElementById('modalStep2').classList.toggle('d-none', stepNum !== 2);
        document.getElementById('modalStep3').classList.toggle('d-none', stepNum !== 3);
        document.getElementById('modalStep4').classList.toggle('d-none', stepNum !== 4);
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
                showStep(2);
                clearOtpInputs();
                startCooldownTimer(data.cooldown || 60);
                startExpiryTimer(300); // 5 phút
                // Focus vào ô OTP đầu tiên
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
            setLoading('btnSendOtp', 'btnSendOtpSpinner', 'btnSendOtpText', false, '<i class="fa fa-paper-plane me-2"></i>Gửi Mã OTP');
        }
    }

    function backToStep1() {
        showStep(1);
        clearInterval(cooldownTimerInterval);
        clearInterval(expiryTimerInterval);
    }

    // ==================== BƯỚC 2: XỬ LÝ 6 Ô OTP & BỘ ĐẾM 60S ====================
    function setupOtpBoxEvents() {
        const boxes = document.querySelectorAll('.otp-box');

        boxes.forEach((box, index) => {
            // Chỉ cho phép nhập số
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

            // Xử lý phím Backspace để lùi về ô trước
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

            // Xử lý Paste/Dán mã 6 chữ số
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

                    // Nếu dán đủ 6 số, tự động kích hoạt xác nhận
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

    // Đếm ngược 60 giây gửi lại OTP
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

    // Đếm ngược 5 phút thời hạn OTP
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

    // Gửi lại mã OTP
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
                // Báo thành công nhẹ
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

    // Xác nhận mã OTP
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
                showStep(3);
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
            setLoading('btnVerifyOtp', 'btnVerifySpinner', 'btnVerifyText', false, '<i class="fa fa-check-circle me-2"></i>Xác Nhận OTP');
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
                showStep(4);
                // Điền email vào form login chính
                if (currentEmail) {
                    document.getElementById('loginEmail').value = currentEmail;
                    document.getElementById('loginPassword').value = '';
                    document.getElementById('loginPassword').focus();
                }
            } else {
                showAlert(3, data.message || 'Đặt lại mật khẩu thất bại!');
                if (data.invalidSession) {
                    setTimeout(() => showStep(1), 2500);
                }
            }
        } catch (err) {
            showAlert(3, 'Lỗi kết nối máy chủ: ' + err.message);
        } finally {
            setLoading('btnResetPassword', 'btnResetSpinner', 'btnResetText', false, '<i class="fa fa-save me-2"></i>Đặt Lại Mật Khẩu');
        }
    }

    function finishAndLogin() {
        forgotModal.hide();
        document.getElementById('loginPassword').focus();
    }

    // ==================== TIỆN ÍCH ====================
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
