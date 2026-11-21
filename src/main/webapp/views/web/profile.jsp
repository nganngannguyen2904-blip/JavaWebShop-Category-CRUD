<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<title>Hồ Sơ Tài Khoản - Shopping Service MVC</title>

<!-- Container Nội Dung Trang Profile -->
<div class="container py-4">

    <!-- Breadcrumb điều hướng -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb bg-white p-3 rounded-3 shadow-sm border mb-0">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none"><i class="fa fa-home me-1"></i>Trang Chủ</a></li>
            <li class="breadcrumb-item active" aria-current="page">Hồ Sơ Tài Khoản</li>
        </ol>
    </nav>

    <!-- Thông báo Alert toàn cục -->
    <c:if test="${not empty alert}">
        <div class="alert alert-${empty alertType ? 'info' : alertType} alert-dismissible fade show shadow-sm mb-4" role="alert">
            <div class="d-flex align-items-center">
                <i class="fa ${alertType == 'success' ? 'fa-circle-check text-success' : (alertType == 'danger' ? 'fa-circle-exclamation text-danger' : 'fa-circle-info text-info')} fs-4 me-3"></i>
                <div>${alert}</div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <!-- Cột Trái: Thông tin tổng quan và Thay đổi ảnh đại diện (Cloudinary) -->
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm rounded-4 text-center p-4 h-100">
                <div class="position-relative d-inline-block mx-auto mb-3">
                    <c:choose>
                        <c:when test="${user.avatar != null && (user.avatar.startsWith('http://') || user.avatar.startsWith('https://'))}">
                            <img id="avatarPreview" src="${user.avatar}" alt="${user.fullName}" class="rounded-circle shadow" style="width: 140px; height: 140px; object-fit: cover; border: 4px solid #3b82f6;">
                        </c:when>
                        <c:when test="${user.avatar != null && !user.avatar.isEmpty()}">
                            <img id="avatarPreview" src="${pageContext.request.contextPath}/image?fname=${user.avatar}" alt="${user.fullName}" class="rounded-circle shadow" style="width: 140px; height: 140px; object-fit: cover; border: 4px solid #3b82f6;" onerror="this.src='${pageContext.request.contextPath}/upload/category/avata.png'">
                        </c:when>
                        <c:otherwise>
                            <img id="avatarPreview" src="${pageContext.request.contextPath}/upload/category/avata.png" alt="Avatar" class="rounded-circle shadow" style="width: 140px; height: 140px; object-fit: cover; border: 4px solid #3b82f6;">
                        </c:otherwise>
                    </c:choose>
                    <span class="position-absolute bottom-0 end-0 bg-success border border-white border-2 rounded-circle p-2" title="Tài khoản đang hoạt động">
                        <span class="visually-hidden">Online</span>
                    </span>
                    <!-- Loading Overlay khi đang upload lên Cloudinary -->
                    <div id="avatarLoadingOverlay" class="position-absolute top-0 start-0 w-100 h-100 bg-dark bg-opacity-75 rounded-circle d-none flex-column align-items-center justify-content-center text-white" style="backdrop-filter: blur(2px);">
                        <div class="spinner-border spinner-border-sm text-primary mb-1" role="status"></div>
                        <span class="fw-semibold" style="font-size: 0.7rem;">Đang tải lên...</span>
                    </div>
                </div>

                <!-- Huy hiệu nhận diện nguồn lưu trữ Cloudinary -->
                <div class="mb-2">
                    <c:choose>
                        <c:when test="${user.avatar != null && user.avatar.contains('cloudinary')}">
                            <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-1 rounded-pill small" title="Ảnh đại diện được lưu trữ trực tuyến an toàn trên máy chủ Cloudinary CDN">
                                <i class="fa-solid fa-cloud-arrow-up me-1"></i> Lưu trữ Cloudinary CDN
                            </span>
                            <div class="mt-1">
                                <a href="${user.avatar}" target="_blank" rel="noopener noreferrer" class="small text-decoration-none text-muted" style="font-size: 0.72rem;">
                                    <i class="fa-solid fa-arrow-up-right-from-square me-1"></i>Xem ảnh gốc Cloudinary
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-3 py-1 rounded-pill small">
                                <i class="fa-solid fa-image me-1"></i> Ảnh đại diện cục bộ
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <h4 class="fw-bold text-dark mb-1">${user.fullName}</h4>
                <p class="text-muted small mb-2">@${user.userName}</p>
                <div class="mb-3">
                    <c:choose>
                        <c:when test="${user.roleid == 1}">
                            <span class="badge bg-danger px-3 py-2 rounded-pill"><i class="fa fa-shield-halved me-1"></i>Quản trị viên (Admin)</span>
                        </c:when>
                        <c:when test="${user.roleid == 2}">
                            <span class="badge bg-primary px-3 py-2 rounded-pill"><i class="fa fa-user-tie me-1"></i>Quản lý (Manager)</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-success px-3 py-2 rounded-pill"><i class="fa fa-user me-1"></i>Khách hàng thành viên</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <hr class="my-3 text-secondary opacity-25">

                <!-- Form Upload Avatar lên Cloudinary bằng Multipart -->
                <form id="avatarForm" action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data" class="mt-2">
                    <input type="hidden" name="action" value="upload_avatar">
                    <label class="btn btn-outline-primary btn-sm rounded-pill px-3 mb-2 w-100" for="avatarInput">
                        <i class="fa fa-camera me-1"></i> Chọn ảnh đại diện mới
                    </label>
                    <input type="file" id="avatarInput" name="avatar" class="d-none" accept="image/png, image/jpeg, image/jpg, image/webp" onchange="previewAndSubmitAvatar(this);">
                    <small class="text-muted d-block" style="font-size: 0.75rem;">Hỗ trợ PNG, JPG, WEBP (Tối đa 10MB)<br>Upload tự động đồng bộ lên Cloudinary</small>
                </form>

                <div class="card bg-light border-0 rounded-3 text-start p-3 mt-3 small">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted"><i class="fa fa-calendar-alt me-1"></i>Ngày tham gia:</span>
                        <strong class="text-dark">${user.createdDate != null ? user.createdDate : '09/11/2026'}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted"><i class="fa fa-shield me-1"></i>Trạng thái:</span>
                        <span class="text-success fw-bold"><i class="fa fa-check-circle me-1"></i>Bình thường</span>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="text-muted"><i class="fa fa-envelope me-1"></i>Email:</span>
                        <div class="d-flex align-items-center">
                            <span class="text-dark text-truncate ms-2" style="max-width: 130px;" title="${user.email}">${user.email}</span>
                            <button type="button" class="btn btn-link btn-sm p-0 ms-1 text-muted" onclick="navigator.clipboard.writeText('${user.email}'); this.innerHTML='<i class=\'fa fa-check text-success\'></i>'; setTimeout(() => this.innerHTML='<i class=\'fa-regular fa-copy\'></i>', 2000);" title="Sao chép email">
                                <i class="fa-regular fa-copy"></i>
                            </button>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="text-muted"><i class="fa fa-phone me-1"></i>Số điện thoại:</span>
                        <strong class="text-dark">${not empty user.phone ? user.phone : 'Chưa cập nhật'}</strong>
                    </div>
                </div>

                <!-- Thống kê nhanh hoạt động tài khoản -->
                <div class="row g-2 mt-2 text-center">
                    <div class="col-6">
                        <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none d-block p-2 border rounded-3 bg-white shadow-sm h-100">
                            <i class="fa-solid fa-cart-shopping text-primary mb-1"></i>
                            <div class="text-muted" style="font-size: 0.72rem;">Giỏ hàng</div>
                            <div class="fw-bold text-dark small">${sessionScope.cart != null ? sessionScope.cart.totalQuantity : 0} món</div>
                        </a>
                    </div>
                    <div class="col-6">
                        <div class="p-2 border rounded-3 bg-white shadow-sm h-100">
                            <i class="fa-solid fa-shield-halved text-success mb-1"></i>
                            <div class="text-muted" style="font-size: 0.72rem;">Bảo mật OTP</div>
                            <div class="fw-bold text-success small">Đã bật</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Cột Phải: Các Tab Cập Nhật Thông Tin, Email OTP và Mật Khẩu -->
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm rounded-4 p-4 h-100">
                <!-- Nav Tabs -->
                <ul class="nav nav-pills mb-4 border-bottom pb-3 gap-2" id="profileTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link rounded-pill px-4 fw-semibold ${empty tab || tab == 'info' ? 'active' : ''}" id="info-tab" data-bs-toggle="tab" data-bs-target="#tab-info" type="button" role="tab">
                            <i class="fa fa-user-pen me-2"></i>Thông Tin Cá Nhân
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link rounded-pill px-4 fw-semibold ${tab == 'email' ? 'active' : ''}" id="email-tab" data-bs-toggle="tab" data-bs-target="#tab-email" type="button" role="tab">
                            <i class="fa fa-envelope-open-text me-2"></i>Thay Đổi Email (OTP)
                            <c:if test="${otpPending}">
                                <span class="badge bg-danger ms-1">Đang chờ OTP</span>
                            </c:if>
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link rounded-pill px-4 fw-semibold ${tab == 'security' ? 'active' : ''}" id="security-tab" data-bs-toggle="tab" data-bs-target="#tab-security" type="button" role="tab">
                            <i class="fa fa-key me-2"></i>Đổi Mật Khẩu
                        </button>
                    </li>
                </ul>

                <!-- Tab Panes -->
                <div class="tab-content" id="profileTabContent">

                    <!-- TAB 1: CẬP NHẬT THÔNG TIN CÁ NHÂN -->
                    <div class="tab-pane fade ${empty tab || tab == 'info' ? 'show active' : ''}" id="tab-info" role="tabpanel">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-address-card text-primary me-2"></i>Cập nhật thông tin tài khoản</h5>
                        <p class="text-muted small mb-4">Bạn có thể chỉnh sửa Họ tên hiển thị và Số điện thoại liên hệ nhận hàng.</p>

                        <form id="infoForm" action="${pageContext.request.contextPath}/profile" method="post" novalidate>
                            <input type="hidden" name="action" value="update_info">

                            <!-- Tên đăng nhập (Chỉ đọc) -->
                            <div class="mb-3">
                                <label class="form-label fw-semibold text-secondary small">Tên đăng nhập (Username)</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="fa fa-user-lock text-muted"></i></span>
                                    <input type="text" class="form-control bg-light" value="${user.userName}" readonly disabled>
                                </div>
                                <small class="text-muted">Tên đăng nhập là duy nhất và không thể thay đổi.</small>
                            </div>

                            <!-- Họ và tên -->
                            <div class="mb-3">
                                <label for="fullnameInput" class="form-label fw-semibold text-dark small">Họ và tên <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-user text-primary"></i></span>
                                    <input type="text" id="fullnameInput" name="fullname" class="form-control ${not empty errors.fullname ? 'is-invalid' : ''}" value="${empty param.fullname ? user.fullName : param.fullname}" placeholder="Nhập họ và tên đầy đủ..." required minlength="2" maxlength="100">
                                    <div class="invalid-feedback" id="fullnameError">
                                        ${not empty errors.fullname ? errors.fullname : 'Họ và tên bắt buộc nhập (từ 2 đến 100 ký tự)!'}
                                    </div>
                                </div>
                            </div>

                            <!-- Số điện thoại -->
                            <div class="mb-4">
                                <label for="phoneInput" class="form-label fw-semibold text-dark small">Số điện thoại <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-phone text-primary"></i></span>
                                    <input type="tel" id="phoneInput" name="phone" class="form-control ${not empty errors.phone ? 'is-invalid' : ''}" value="${empty param.phone ? user.phone : param.phone}" placeholder="Nhập số điện thoại (ví dụ: 0987654321)..." required pattern="^(0[35789])[0-9]{8}$">
                                    <div class="invalid-feedback" id="phoneError">
                                        ${not empty errors.phone ? errors.phone : 'Số điện thoại phải gồm 10 chữ số hợp lệ (bắt đầu bằng 03, 05, 07, 08, 09)!'}
                                    </div>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary rounded-pill px-4 fw-semibold">
                                <i class="fa fa-floppy-disk me-1"></i> Lưu Thay Đổi
                            </button>
                        </form>
                    </div>

                    <!-- TAB 2: THAY ĐỔI EMAIL THÔNG QUA OTP GMAIL -->
                    <div class="tab-pane fade ${tab == 'email' ? 'show active' : ''}" id="tab-email" role="tabpanel">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-envelope-circle-check text-primary me-2"></i>Cập nhật địa chỉ Email qua OTP</h5>
                        <p class="text-muted small mb-4">
                            Vì lý do bảo mật, địa chỉ email chỉ được cập nhật sau khi bạn xác minh thành công mã OTP 6 chữ số gửi đến chính email mới.
                        </p>

                        <!-- Email Hiện Tại -->
                        <div class="card bg-light border-0 rounded-3 p-3 mb-4">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <small class="text-muted d-block">Địa chỉ Email hiện tại</small>
                                    <strong class="text-dark fs-6">${user.email}</strong>
                                </div>
                                <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 rounded-pill">
                                    <i class="fa fa-circle-check me-1"></i> Đã kích hoạt
                                </span>
                            </div>
                        </div>

                        <!-- Bước 1: Form Gửi Mã OTP Đến Email Mới -->
                        <form id="sendOtpForm" action="${pageContext.request.contextPath}/profile" method="post" class="mb-4" novalidate>
                            <input type="hidden" name="action" value="send_email_otp">

                            <div class="mb-3">
                                <label for="newEmailInput" class="form-label fw-semibold text-dark small">Địa chỉ Email mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-at text-primary"></i></span>
                                    <input type="email" id="newEmailInput" name="newEmail" class="form-control ${not empty errors.newEmail ? 'is-invalid' : ''}" value="${not empty pendingEmail ? pendingEmail : param.newEmail}" placeholder="Nhập địa chỉ email mới cần cập nhật..." required pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$">
                                    <button class="btn btn-outline-primary fw-semibold px-3" type="submit" id="btnSendOtp">
                                        <i class="fa fa-paper-plane me-1"></i> Gửi Mã OTP
                                    </button>
                                    <div class="invalid-feedback" id="newEmailError">
                                        ${not empty errors.newEmail ? errors.newEmail : 'Vui lòng nhập định dạng email hợp lệ (ví dụ: example@gmail.com)!'}
                                    </div>
                                </div>
                                <small class="text-muted mt-1 d-block">Hệ thống sẽ gửi mã OTP 6 chữ số tới hộp thư này.</small>
                            </div>
                        </form>

                        <!-- Bước 2: Form Xác Minh Mã OTP (Hiển thị khi đang chờ OTP) -->
                        <c:if test="${otpPending}">
                            <div class="card border-primary border-opacity-25 rounded-3 p-4 bg-primary bg-opacity-10 mt-3">
                                <div class="d-flex align-items-center justify-content-between mb-3">
                                    <h6 class="fw-bold text-primary mb-0">
                                        <i class="fa fa-key me-2"></i>Nhập mã OTP xác nhận
                                    </h6>
                                    <span class="badge bg-danger rounded-pill px-3 py-2" id="otpTimerBadge">
                                        <i class="fa fa-stopwatch me-1"></i>Hiệu lực: <span id="countdownTimer">05:00</span>
                                    </span>
                                </div>

                                <p class="small text-dark mb-3">
                                    Mã xác thực đã được gửi tới: <strong>${pendingEmail}</strong>. Mã có thời hạn trong <strong>5 phút</strong>.
                                </p>

                                <form id="verifyOtpForm" action="${pageContext.request.contextPath}/profile" method="post" novalidate>
                                    <input type="hidden" name="action" value="verify_email_otp">
                                    <input type="hidden" name="pendingEmail" value="${pendingEmail}">

                                    <div class="mb-3">
                                        <label for="otpCodeInput" class="form-label fw-semibold text-dark small">Mã xác thực OTP (6 chữ số) <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fa fa-shield-cat text-primary"></i></span>
                                            <input type="text" id="otpCodeInput" name="otpCode" class="form-control form-control-lg text-center fw-bold letter-spacing-lg ${not empty errors.otpCode ? 'is-invalid' : ''}" placeholder="• • • • • •" maxlength="6" pattern="^[0-9]{6}$" required style="letter-spacing: 8px; font-size: 1.5rem;">
                                            <div class="invalid-feedback">
                                                ${not empty errors.otpCode ? errors.otpCode : 'Vui lòng nhập chính xác mã OTP 6 chữ số!'}
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-success rounded-pill px-4 fw-semibold">
                                            <i class="fa fa-check-circle me-1"></i> Xác Nhận Cập Nhật Email
                                        </button>
                                        <button type="button" class="btn btn-outline-secondary rounded-pill px-3" onclick="document.getElementById('sendOtpForm').submit();">
                                            <i class="fa fa-rotate-right me-1"></i> Gửi Lại Mã
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </c:if>
                    </div>

                    <!-- TAB 3: ĐỔI MẬT KHẨU TÀI KHOẢN -->
                    <div class="tab-pane fade ${tab == 'security' ? 'show active' : ''}" id="tab-security" role="tabpanel">
                        <h5 class="fw-bold text-dark mb-3"><i class="fa fa-lock text-primary me-2"></i>Đổi mật khẩu tài khoản</h5>
                        <p class="text-muted small mb-4">Để bảo vệ tài khoản, hãy sử dụng mật khẩu mạnh gồm tối thiểu 6 ký tự kết hợp chữ và số.</p>

                        <form id="passwordForm" action="${pageContext.request.contextPath}/profile" method="post" novalidate>
                            <input type="hidden" name="action" value="change_password">

                            <div class="mb-3">
                                <label for="oldPasswordInput" class="form-label fw-semibold text-dark small">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-lock text-secondary"></i></span>
                                    <input type="password" id="oldPasswordInput" name="oldPassword" class="form-control ${not empty errors.oldPassword ? 'is-invalid' : ''}" placeholder="Nhập mật khẩu hiện tại..." required>
                                    <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('oldPasswordInput')"><i class="fa fa-eye"></i></button>
                                    <div class="invalid-feedback">
                                        ${not empty errors.oldPassword ? errors.oldPassword : 'Vui lòng nhập mật khẩu hiện tại!'}
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="newPasswordInput" class="form-label fw-semibold text-dark small">Mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-key text-primary"></i></span>
                                    <input type="password" id="newPasswordInput" name="newPassword" class="form-control ${not empty errors.newPassword ? 'is-invalid' : ''}" placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)..." required minlength="6">
                                    <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('newPasswordInput')"><i class="fa fa-eye"></i></button>
                                    <div class="invalid-feedback">
                                        ${not empty errors.newPassword ? errors.newPassword : 'Mật khẩu mới phải có tối thiểu 6 ký tự!'}
                                    </div>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="confirmPasswordInput" class="form-label fw-semibold text-dark small">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa fa-check-double text-success"></i></span>
                                    <input type="password" id="confirmPasswordInput" name="confirmPassword" class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}" placeholder="Nhập lại mật khẩu mới..." required minlength="6">
                                    <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('confirmPasswordInput')"><i class="fa fa-eye"></i></button>
                                    <div class="invalid-feedback" id="confirmPasswordError">
                                        ${not empty errors.confirmPassword ? errors.confirmPassword : 'Mật khẩu xác nhận không trùng khớp!'}
                                    </div>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary rounded-pill px-4 fw-semibold">
                                <i class="fa fa-shield-check me-1"></i> Cập Nhật Mật Khẩu
                            </button>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript Client-Side Validation & Xử Lý Tiện Ích -->
<script>
    // 1. Preview và tự động nộp ảnh đại diện Cloudinary
    function previewAndSubmitAvatar(input) {
        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Client Validation kích thước và loại tệp
            const validTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp'];
            if (!validTypes.includes(file.type)) {
                alert('Vui lòng chọn hình ảnh định dạng PNG, JPG, JPEG hoặc WEBP!');
                input.value = '';
                return;
            }

            if (file.size > 10 * 1024 * 1024) {
                alert('Kích thước ảnh không được vượt quá 10MB!');
                input.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('avatarPreview').src = e.target.result;
            };
            reader.readAsDataURL(file);

            // Tự động submit form tải ảnh lên Cloudinary
            if (confirm('Bạn có chắc chắn muốn cập nhật ảnh đại diện này lên Cloudinary?')) {
                const overlay = document.getElementById('avatarLoadingOverlay');
                if (overlay) {
                    overlay.classList.remove('d-none');
                    overlay.classList.add('d-flex');
                }
                document.getElementById('avatarForm').submit();
            }
        }
    }

    // 2. Client-side Validation Form Cập Nhật Họ Tên & Số Điện Thoại
    document.getElementById('infoForm')?.addEventListener('submit', function(e) {
        let valid = true;
        const fullname = document.getElementById('fullnameInput');
        const phone = document.getElementById('phoneInput');

        if (!fullname.value || fullname.value.trim().length < 2) {
            fullname.classList.add('is-invalid');
            valid = false;
        } else {
            fullname.classList.remove('is-invalid');
            fullname.classList.add('is-valid');
        }

        const phoneRegex = /^(0[35789])[0-9]{8}$/;
        if (!phone.value || !phoneRegex.test(phone.value.trim())) {
            phone.classList.add('is-invalid');
            valid = false;
        } else {
            phone.classList.remove('is-invalid');
            phone.classList.add('is-valid');
        }

        if (!valid) {
            e.preventDefault();
        }
    });

    // 3. Client-side Validation Form Đổi Email
    document.getElementById('sendOtpForm')?.addEventListener('submit', function(e) {
        const newEmail = document.getElementById('newEmailInput');
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        if (!newEmail.value || !emailRegex.test(newEmail.value.trim())) {
            newEmail.classList.add('is-invalid');
            e.preventDefault();
        } else {
            newEmail.classList.remove('is-invalid');
            newEmail.classList.add('is-valid');
        }
    });

    // 4. Client-side Validation Form Đổi Mật Khẩu
    document.getElementById('passwordForm')?.addEventListener('submit', function(e) {
        let valid = true;
        const oldPass = document.getElementById('oldPasswordInput');
        const newPass = document.getElementById('newPasswordInput');
        const confirmPass = document.getElementById('confirmPasswordInput');

        if (!oldPass.value) {
            oldPass.classList.add('is-invalid');
            valid = false;
        } else {
            oldPass.classList.remove('is-invalid');
        }

        if (!newPass.value || newPass.value.length < 6) {
            newPass.classList.add('is-invalid');
            valid = false;
        } else {
            newPass.classList.remove('is-invalid');
        }

        if (!confirmPass.value || confirmPass.value !== newPass.value) {
            confirmPass.classList.add('is-invalid');
            valid = false;
        } else {
            confirmPass.classList.remove('is-invalid');
        }

        if (!valid) {
            e.preventDefault();
        }
    });

    // 5. Đồng hồ đếm ngược 5 phút cho mã OTP
    <c:if test="${otpPending}">
        let timeLeft = 300; // 5 phút = 300 giây
        const countdownEl = document.getElementById('countdownTimer');
        const timerInterval = setInterval(() => {
            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                if (countdownEl) {
                    countdownEl.innerHTML = "HẾT HẠN";
                    countdownEl.parentElement.classList.replace('bg-danger', 'bg-secondary');
                }
            } else {
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                if (countdownEl) {
                    countdownEl.textContent = (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
                }
                timeLeft--;
            }
        }, 1000);
    </c:if>

    // 6. Tiện ích ẩn/hiện mật khẩu
    function togglePasswordVisibility(inputId) {
        const input = document.getElementById(inputId);
        if (input) {
            input.type = input.type === 'password' ? 'text' : 'password';
        }
    }
</script>
