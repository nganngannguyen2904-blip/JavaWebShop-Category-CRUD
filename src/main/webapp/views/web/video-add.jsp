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
                    <p class="mb-0 text-white-50 small">Upload file video bằng Servlet Multipart (chuẩn Jakarta EE 10)</p>
                </div>

                <div class="p-4">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
                            <i class="fa fa-exclamation-triangle me-2"></i>
                            <div>${error}</div>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Form Upload Multipart theo đúng yêu cầu đề bài và tài liệu PDF -->
                    <form action="${pageContext.request.contextPath}/video/add" method="post" enctype="multipart/form-data">
                        
                        <!-- Tiêu đề Video -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small text-secondary">Tiêu đề Video <span class="text-danger">*</span></label>
                            <input type="text" 
                                   class="form-control" 
                                   name="title" 
                                   id="title" 
                                   value="<c:out value='${oldTitle}'/>" 
                                   placeholder="Nhập tiêu đề video mô tả sản phẩm, hướng dẫn,..." 
                                   required />
                        </div>

                        <!-- Chọn file Video (Multipart File) -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small text-secondary">Chọn File Video <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa fa-video"></i></span>
                                <input type="file" 
                                       class="form-control" 
                                       name="videoFile" 
                                       id="videoFile" 
                                       accept="video/mp4,video/webm,video/ogg,video/quicktime" 
                                       required />
                            </div>
                            <div class="form-text small">Định dạng hỗ trợ: MP4, WebM, OGG, MOV. Kích thước tối đa: 100MB.</div>
                        </div>

                        <!-- Chọn file Poster (Thumbnail) -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small text-secondary">Ảnh Poster Đại Diện (Thumbnail)</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa fa-image"></i></span>
                                <input type="file" 
                                       class="form-control" 
                                       name="posterFile" 
                                       id="posterFile" 
                                       accept="image/png,image/jpeg,image/webp" />
                            </div>
                            <div class="form-text small">Nếu bỏ trống, hệ thống sẽ sử dụng ảnh mặc định. Định dạng hỗ trợ: PNG, JPG, JPEG, WEBP.</div>
                        </div>

                        <!-- Mô tả Video -->
                        <div class="mb-4">
                            <label class="form-label fw-bold small text-secondary">Mô tả chi tiết</label>
                            <textarea class="form-control" 
                                      name="description" 
                                      id="description" 
                                      rows="4" 
                                      placeholder="Mô tả thông tin chi tiết về nội dung của video..."><c:out value='${oldDescription}'/></textarea>
                        </div>

                        <!-- Buttons -->
                        <div class="d-flex justify-content-end gap-2 border-top pt-3">
                            <a href="${pageContext.request.contextPath}/video" class="btn btn-outline-secondary">
                                <i class="fa fa-times me-1"></i>Hủy bỏ
                            </a>
                            <button type="submit" class="btn btn-primary px-4 fw-bold">
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

</body>
</html>
