<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${video.title}"/> - Shopping Service MVC</title>
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
        .video-player-wrapper {
            background-color: #000;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            position: relative;
        }
        video {
            width: 100%;
            max-height: 540px;
            display: block;
            outline: none;
        }
        .side-video-card {
            border: 1px solid #eef0f2;
            border-radius: 10px;
            overflow: hidden;
            background: #ffffff;
            transition: all 0.2s ease;
            text-decoration: none;
            color: inherit;
            display: flex;
            margin-bottom: 12px;
        }
        .side-video-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.12);
            border-color: #b6d4fe;
            color: #0d6efd;
        }
        .side-video-thumb {
            width: 128px;
            aspect-ratio: 16 / 9;
            object-fit: cover;
            border-radius: 4px;
            flex-shrink: 0;
            background: #000;
        }
        .side-video-title {
            font-size: 0.9rem;
            font-weight: 600;
            line-height: 1.3;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
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
            <div class="d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary btn-sm rounded-pill px-3 fw-bold">
                    <i class="fa fa-cloud-upload-alt me-1"></i>Đăng Video Mới
                </a>
            </div>
        </div>
    </div>
</nav>

<div class="container py-4">

    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb small">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Trang chủ</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/video" class="text-decoration-none">Video</a></li>
            <li class="breadcrumb-item active" aria-current="page"><c:out value="${video.title}"/></li>
        </ol>
    </nav>

    <c:choose>
        <c:when test="${not empty error}">
            <div class="alert alert-danger py-4 text-center my-5 rounded-3">
                <i class="fa fa-exclamation-circle fa-3x mb-3 text-danger"></i>
                <h4 class="fw-bold">${error}</h4>
                <a href="${pageContext.request.contextPath}/video" class="btn btn-primary mt-3">Quay lại danh sách Video</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <!-- Cột chính: Trình phát Video & Chi tiết -->
                <div class="col-lg-8">
                    <!-- Trình phát Video HTML5 theo yêu cầu (Phát file thật qua URL stream) -->
                    <div class="video-player-wrapper mb-3">
                        <video id="player" controls autoplay preload="metadata" 
                               poster="${pageContext.request.contextPath}/image?fname=${video.poster}">
                            <source src="${pageContext.request.contextPath}/video/stream?fname=${video.videoUrl}" type="video/mp4">
                            <source src="${pageContext.request.contextPath}/video/stream?fname=${video.videoUrl}" type="video/webm">
                            Trình duyệt của bạn không hỗ trợ phát thẻ video HTML5.
                        </video>
                    </div>

                    <!-- Tiêu đề & Thông tin Video -->
                    <div class="bg-white p-4 rounded-3 shadow-sm border mb-4">
                        <!-- Thẻ Sản Phẩm Thời Trang Xuất Hiện Trong Video (Nếu có liên kết) -->
                        <c:if test="${video.hasLinkedProduct()}">
                            <div class="card border-primary-subtle bg-light shadow-sm mb-4 rounded-3">
                                <div class="card-body p-3">
                                    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3">
                                        <div class="d-flex align-items-center gap-3">
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${video.productId}" target="_blank">
                                                <img src="${pageContext.request.contextPath}/image?fname=${video.productImage}" 
                                                     alt="${video.productName}" 
                                                     class="rounded shadow-sm" 
                                                     style="width: 70px; height: 70px; object-fit: cover; border: 1px solid #dee2e6;" 
                                                     onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';" />
                                            </a>
                                            <div>
                                                <span class="badge bg-primary-subtle text-primary small mb-1">
                                                    <i class="fa fa-tshirt me-1"></i>Sản phẩm thời trang trong video
                                                </span>
                                                <h6 class="fw-bold mb-1">
                                                    <a href="${pageContext.request.contextPath}/product/detail?id=${video.productId}" class="text-dark text-decoration-none" target="_blank">
                                                        ${video.productName}
                                                    </a>
                                                </h6>
                                                <div class="text-danger fw-bold fs-6">
                                                    <fmt:formatNumber value="${video.productPrice}" pattern="#,###" /> ₫
                                                </div>
                                            </div>
                                        </div>
                                        <div>
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${video.productId}" class="btn btn-primary btn-sm px-3 fw-bold" target="_blank">
                                                <i class="fa fa-shopping-bag me-1"></i>Xem Sản Phẩm & Mua
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <h3 class="fw-bold text-dark mb-2">${video.title}</h3>

                        <div class="d-flex flex-wrap justify-content-between align-items-center text-secondary small pb-3 mb-3 border-bottom">
                            <div class="d-flex align-items-center gap-3">
                                <span><i class="fa fa-eye text-primary me-1"></i><strong><fmt:formatNumber value="${video.views}" /></strong> lượt xem</span>
                                <span><i class="fa fa-calendar-alt text-info me-1"></i>Đăng ngày <fmt:formatDate value="${video.uploadDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                <c:if test="${not empty video.userFullName}">
                                    <span><i class="fa fa-user-circle text-success me-1"></i>Đăng bởi: <strong>${video.userFullName}</strong></span>
                                </c:if>
                            </div>

                            <c:if test="${video.userId == sessionScope.account.id || sessionScope.account.canAccessAdmin()}">
                                <div class="d-flex gap-2 mt-2 mt-sm-0">
                                    <a href="${pageContext.request.contextPath}/video/edit?id=${video.videoId}" class="btn btn-outline-secondary btn-sm">
                                        <i class="fa fa-edit me-1"></i>Sửa Video
                                    </a>
                                </div>
                            </c:if>
                        </div>

                        <!-- Mô tả chi tiết Video -->
                        <h6 class="fw-bold text-dark mb-2"><i class="fa fa-align-left me-2 text-primary"></i>Mô tả video:</h6>
                        <div class="text-secondary" style="white-space: pre-line; line-height: 1.6;">
                            <c:choose>
                                <c:when test="${not empty video.description}">
                                    ${video.description}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted fst-italic">Người đăng tải không cung cấp thêm mô tả chi tiết cho video này.</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Cột phụ: Video đề xuất & Danh sách mới nhất -->
                <div class="col-lg-4">
                    <div class="bg-white p-3 rounded-3 shadow-sm border mb-4">
                        <h5 class="fw-bold text-dark mb-3">
                            <i class="fa fa-fire text-danger me-2"></i>Video Mới Nhất
                        </h5>

                        <c:forEach var="rv" items="${relatedVideos}">
                            <c:if test="${rv.videoId != video.videoId}">
                                <a href="${pageContext.request.contextPath}/video/detail?id=${rv.videoId}" class="side-video-card">
                                    <img src="${pageContext.request.contextPath}/image?fname=${rv.poster}" alt="${rv.title}" class="side-video-thumb" onerror="this.src='${pageContext.request.contextPath}/image?fname=cat_dientu.png';">
                                    <div class="p-2 d-flex flex-column justify-content-between">
                                        <div class="side-video-title" title="${rv.title}">${rv.title}</div>
                                        <div class="text-muted small">
                                            <i class="fa fa-eye me-1"></i><fmt:formatNumber value="${rv.views}" /> lượt xem
                                        </div>
                                    </div>
                                </a>
                            </c:if>
                        </c:forEach>

                        <div class="text-center pt-2">
                            <a href="${pageContext.request.contextPath}/video" class="btn btn-outline-primary btn-sm w-100">
                                <i class="fa fa-th-list me-1"></i>Xem Tất Cả Video
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

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
