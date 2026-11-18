<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Tải Video Mới - Shopping Service MVC</title>
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
            background-color: #f6f8fa;
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, sans-serif;
            color: #212529;
        }
        .form-card {
            border: 1px solid #eef0f2;
            border-radius: 16px;
            background: #ffffff;
            box-shadow: 0 10px 30px rgba(0,0,0,0.06);
            overflow: hidden;
        }
        .form-header {
            background: linear-gradient(135deg, #0d6efd 0%, #002b66 100%);
            color: #ffffff;
            padding: 24px 30px;
        }
        .preview-box {
            border: 2px dashed #dee2e6;
            border-radius: 12px;
            background: #f8f9fa;
            padding: 16px;
            transition: all 0.3s ease;
        }
        .preview-box:hover {
            border-color: #0d6efd;
            background: #f1f7ff;
        }
        .poster-preview-frame {
            width: 100%;
            max-width: 440px;
            aspect-ratio: 16 / 9;
            background: #111;
            border-radius: 8px;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto;
            box-shadow: 0 4px 14px rgba(0,0,0,0.15);
        }
        .poster-preview-frame img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }
        .video-preview-player {
            width: 100%;
            max-height: 320px;
            background: #000;
            border-radius: 8px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.15);
        }
    </style>
</head>
<body>

<!-- Topbar Navigation -->
<jsp:include page="/views/common/topbar.jsp" />

<!-- Main Navbar -->
<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary fs-4" href="${pageContext.request.contextPath}/home">
            <i class="fa fa-shopping-bag me-2"></i>SHOPPING SERVICE
        </a>
        <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/home"><i class="fa fa-home me-1"></i>Trang Chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/products"><i class="fa fa-boxes me-1"></i>Sản Phẩm</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-bold text-primary" href="${pageContext.request.contextPath}/video"><i class="fa fa-play-circle me-1"></i>Video Đa Phương Tiện</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="form-card">
                <div class="form-header">
                    <h4 class="fw-bold mb-1"><i class="fa fa-cloud-upload-alt me-2"></i>Đăng Tải Video Mới</h4>
                    <p class="mb-0 text-white-50 small">Upload file video và chọn ảnh bìa đại diện (Thumbnail) chuẩn Multipart</p>
                </div>

                <div class="p-4">
                    <!-- Thông báo lỗi từ Server nếu có -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
                            <i class="fa fa-exclamation-triangle me-2 fs-5"></i>
                            <div><strong>Lỗi:</strong> ${error}</div>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Khung thông báo lỗi phía Client (JS Validation) -->
                    <div id="clientAlertBox" class="alert alert-danger alert-dismissible fade show d-none align-items-center mb-3" role="alert">
                        <i class="fa fa-exclamation-circle me-2 fs-5"></i>
                        <span id="clientAlertMsg"></span>
                        <button type="button" class="btn-close" type="button" onclick="hideClientAlert()"></button>
                    </div>

                    <!-- Form Upload Multipart -->
                    <form id="formVideoAdd" action="${pageContext.request.contextPath}/video/add" method="post" enctype="multipart/form-data">
                        
                        <!-- 1. Tiêu đề Video (Bắt buộc) -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small text-secondary">
                                Tiêu đề Video <span class="text-danger">*</span>
                            </label>
                            <input type="text" 
                                   class="form-control" 
                                   name="title" 
                                   id="title" 
                                   value="<c:out value='${oldTitle}'/>" 
                                   placeholder="Nhập tiêu đề video mô tả sản phẩm, hướng dẫn,..." 
                                   maxlength="255"
                                   required />
                        </div>

                        <!-- 2. Chọn File Video (Bắt buộc) & Xem trước -->
                        <div class="mb-4">
                            <label class="form-label fw-bold small text-secondary">
                                Chọn File Video <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="fa fa-video text-primary"></i></span>
                                <input type="file" 
                                       class="form-control" 
                                       name="videoFile" 
                                       id="videoFile" 
                                       accept="video/mp4,video/webm,video/ogg,video/quicktime" 
                                       required />
                            </div>
                            <div class="form-text small text-muted">
                                Hỗ trợ: <strong>MP4, WebM, OGG, MOV</strong>. Dung lượng tối đa: <strong>100MB</strong>.
                            </div>

                            <!-- Khung Xem Trước Video Sau Khi Chọn -->
                            <div id="videoPreviewWrapper" class="preview-box mt-3 d-none">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge bg-primary px-2 py-1"><i class="fa fa-play me-1"></i>Xem trước Video</span>
                                    <span id="videoFileInfo" class="small text-muted fw-semibold"></span>
                                </div>
                                <video id="videoPreviewPlayer" class="video-preview-player" controls preload="metadata"></video>
                                <div class="text-end mt-2">
                                    <button type="button" class="btn btn-outline-danger btn-sm" id="btnRemoveVideo">
                                        <i class="fa fa-trash me-1"></i>Xóa / Chọn lại video khác
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- 3. Chọn File Ảnh Bìa (Thumbnail) (Không bắt buộc) & Xem trước -->
                        <div class="mb-4">
                            <label class="form-label fw-bold small text-secondary">
                                Chọn Ảnh Bìa Đại Diện (Thumbnail Video)
                            </label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="fa fa-image text-success"></i></span>
                                <input type="file" 
                                       class="form-control" 
                                       name="posterFile" 
                                       id="posterFile" 
                                       accept="image/png,image/jpeg,image/webp,image/jpg" />
                            </div>
                            <div class="form-text small text-muted">
                                Tùy chọn (Nếu không chọn, hệ thống tự động gán ảnh mặc định). Định dạng: <strong>JPG, JPEG, PNG, WEBP</strong>. Tối đa: <strong>10MB</strong>.
                            </div>

                            <!-- Khung Xem Trước Ảnh Bìa Sau Khi Chọn -->
                            <div id="posterPreviewWrapper" class="preview-box mt-3 d-none text-center">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge bg-success px-2 py-1"><i class="fa fa-check me-1"></i>Xem trước Ảnh Bìa (Tỷ lệ 16:9)</span>
                                    <span id="posterFileInfo" class="small text-muted fw-semibold"></span>
                                </div>
                                <div class="poster-preview-frame">
                                    <img id="posterPreviewImg" src="" alt="Xem trước ảnh bìa" />
                                </div>
                                <div class="mt-2 d-flex justify-content-center gap-2">
                                    <label for="posterFile" class="btn btn-outline-primary btn-sm mb-0">
                                        <i class="fa fa-sync-alt me-1"></i>Thay đổi ảnh khác
                                    </label>
                                    <button type="button" class="btn btn-outline-danger btn-sm" id="btnRemovePoster">
                                        <i class="fa fa-trash me-1"></i>Xóa ảnh đã chọn
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- 4. Chọn Sản Phẩm Thời Trang Liên Kết (Tùy chọn) -->
                        <div class="mb-4">
                            <label class="form-label fw-bold small text-secondary">
                                <i class="fa fa-tags me-1 text-primary"></i>Sản Phẩm Thời Trang Liên Kết Trong Video
                            </label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="fa fa-shopping-bag text-primary"></i></span>
                                <select name="productId" id="productId" class="form-select">
                                    <option value="">-- Không liên kết sản phẩm (Tùy chọn) --</option>
                                    <c:forEach items="${productList}" var="p">
                                        <option value="${p.id}" ${oldProductId == p.id ? 'selected' : ''}>
                                            [#${p.id}] ${p.name} - ${p.formattedPrice}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-text small text-muted">
                                Chọn sản phẩm quần áo xuất hiện trong video để người xem có thể nhấn xem chi tiết và mua trực tiếp.
                            </div>
                        </div>

                        <!-- 5. Mô tả Video -->
                        <div class="mb-4">
                            <label class="form-label fw-bold small text-secondary">Mô tả chi tiết Video</label>
                            <textarea class="form-control" 
                                      name="description" 
                                      id="description" 
                                      rows="4" 
                                      placeholder="Mô tả thông tin chi tiết về nội dung của video..."><c:out value='${oldDescription}'/></textarea>
                        </div>

                        <!-- Action Buttons -->
                        <div class="d-flex justify-content-end gap-2 border-top pt-3">
                            <a href="${pageContext.request.contextPath}/video" class="btn btn-outline-secondary">
                                <i class="fa fa-times me-1"></i>Hủy bỏ
                            </a>
                            <button type="submit" id="btnSubmitForm" class="btn btn-primary px-4 fw-bold">
                                <i class="fa fa-upload me-1"></i>Đăng Tải Video
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="bg-dark text-white-50 py-4 mt-5">
    <div class="container text-center small">
        <p class="mb-1 text-white">Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)</p>
        <p class="mb-0">Môn học: Lập trình Web - GVHD: ThS. Nguyễn Hữu Trung</p>
    </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Script xử lý Preview Video, Preview Ảnh bìa & Client-side Validation -->
<script>
    const MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    const MAX_POSTER_SIZE = 10 * 1024 * 1024; // 10MB
    const VALID_VIDEO_EXTS = ['.mp4', '.webm', '.ogg', '.mov'];
    const VALID_POSTER_EXTS = ['.jpg', '.jpeg', '.png', '.webp'];

    const clientAlertBox = document.getElementById('clientAlertBox');
    const clientAlertMsg = document.getElementById('clientAlertMsg');

    function showClientAlert(message) {
        clientAlertMsg.innerText = message;
        clientAlertBox.classList.remove('d-none');
        clientAlertBox.classList.add('d-flex');
        window.scrollTo({ top: clientAlertBox.offsetTop - 80, behavior: 'smooth' });
    }

    function hideClientAlert() {
        clientAlertBox.classList.add('d-none');
        clientAlertBox.classList.remove('d-flex');
    }

    function formatBytes(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    function getFileExtension(filename) {
        const dotIndex = filename.lastIndexOf('.');
        return dotIndex !== -1 ? filename.substring(dotIndex).toLowerCase() : '';
    }

    // ====== XỬ LÝ PREVIEW VIDEO ======
    const videoFileInput = document.getElementById('videoFile');
    const videoPreviewWrapper = document.getElementById('videoPreviewWrapper');
    const videoPreviewPlayer = document.getElementById('videoPreviewPlayer');
    const videoFileInfo = document.getElementById('videoFileInfo');
    const btnRemoveVideo = document.getElementById('btnRemoveVideo');

    let currentVideoBlobUrl = null;

    videoFileInput.addEventListener('change', function() {
        hideClientAlert();
        const file = this.files[0];
        if (!file) {
            clearVideoPreview();
            return;
        }

        const ext = getFileExtension(file.name);
        if (!VALID_VIDEO_EXTS.includes(ext)) {
            showClientAlert("Định dạng file video '" + file.name + "' không hợp lệ! Vui lòng chỉ chọn: " + VALID_VIDEO_EXTS.join(', '));
            clearVideoPreview();
            return;
        }

        if (file.size > MAX_VIDEO_SIZE) {
            showClientAlert("File video vượt quá dung lượng tối đa 100MB (" + formatBytes(file.size) + ")!");
            clearVideoPreview();
            return;
        }

        if (currentVideoBlobUrl) {
            URL.revokeObjectURL(currentVideoBlobUrl);
        }

        currentVideoBlobUrl = URL.createObjectURL(file);
        videoPreviewPlayer.src = currentVideoBlobUrl;
        videoFileInfo.innerText = file.name + ' (' + formatBytes(file.size) + ')';
        videoPreviewWrapper.classList.remove('d-none');
    });

    btnRemoveVideo.addEventListener('click', function() {
        clearVideoPreview();
    });

    function clearVideoPreview() {
        videoFileInput.value = '';
        if (currentVideoBlobUrl) {
            URL.revokeObjectURL(currentVideoBlobUrl);
            currentVideoBlobUrl = null;
        }
        videoPreviewPlayer.removeAttribute('src');
        videoPreviewPlayer.load();
        videoFileInfo.innerText = '';
        videoPreviewWrapper.classList.add('d-none');
    }

    // ====== XỬ LÝ PREVIEW ẢNH BÌA (POSTER) ======
    const posterFileInput = document.getElementById('posterFile');
    const posterPreviewWrapper = document.getElementById('posterPreviewWrapper');
    const posterPreviewImg = document.getElementById('posterPreviewImg');
    const posterFileInfo = document.getElementById('posterFileInfo');
    const btnRemovePoster = document.getElementById('btnRemovePoster');

    let currentPosterBlobUrl = null;

    posterFileInput.addEventListener('change', function() {
        hideClientAlert();
        const file = this.files[0];
        if (!file) {
            clearPosterPreview();
            return;
        }

        const ext = getFileExtension(file.name);
        if (!VALID_POSTER_EXTS.includes(ext)) {
            showClientAlert("Định dạng ảnh bìa '" + file.name + "' không hợp lệ! Vui lòng chỉ chọn ảnh: " + VALID_POSTER_EXTS.join(', '));
            clearPosterPreview();
            return;
        }

        if (file.size > MAX_POSTER_SIZE) {
            showClientAlert("File ảnh bìa vượt quá dung lượng tối đa 10MB (" + formatBytes(file.size) + ")!");
            clearPosterPreview();
            return;
        }

        if (currentPosterBlobUrl) {
            URL.revokeObjectURL(currentPosterBlobUrl);
        }

        currentPosterBlobUrl = URL.createObjectURL(file);
        posterPreviewImg.src = currentPosterBlobUrl;
        posterFileInfo.innerText = file.name + ' (' + formatBytes(file.size) + ')';
        posterPreviewWrapper.classList.remove('d-none');
    });

    btnRemovePoster.addEventListener('click', function() {
        clearPosterPreview();
    });

    function clearPosterPreview() {
        posterFileInput.value = '';
        if (currentPosterBlobUrl) {
            URL.revokeObjectURL(currentPosterBlobUrl);
            currentPosterBlobUrl = null;
        }
        posterPreviewImg.removeAttribute('src');
        posterFileInfo.innerText = '';
        posterPreviewWrapper.classList.add('d-none');
    }

    // ====== VALIDATE TRƯỚC KHI SUBMIT FORM ======
    document.getElementById('formVideoAdd').addEventListener('submit', function(e) {
        const titleVal = document.getElementById('title').value.trim();
        if (!titleVal) {
            e.preventDefault();
            showClientAlert('Vui lòng nhập tiêu đề cho video!');
            return;
        }

        if (!videoFileInput.files || videoFileInput.files.length === 0) {
            e.preventDefault();
            showClientAlert('Vui lòng chọn file video cần tải lên!');
            return;
        }

        const vFile = videoFileInput.files[0];
        const vExt = getFileExtension(vFile.name);
        if (!VALID_VIDEO_EXTS.includes(vExt) || vFile.size > MAX_VIDEO_SIZE) {
            e.preventDefault();
            showClientAlert('File video không hợp lệ về định dạng hoặc vượt quá 100MB!');
            return;
        }

        if (posterFileInput.files && posterFileInput.files.length > 0) {
            const pFile = posterFileInput.files[0];
            const pExt = getFileExtension(pFile.name);
            if (!VALID_POSTER_EXTS.includes(pExt) || pFile.size > MAX_POSTER_SIZE) {
                e.preventDefault();
                showClientAlert('File ảnh bìa không hợp lệ về định dạng hoặc vượt quá 10MB!');
                return;
            }
        }
    });
</script>

</body>
</html>
