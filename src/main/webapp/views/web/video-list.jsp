<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Video - Shopping Service MVC</title>
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
        .video-card {
            border: 1px solid #eef0f2;
            border-radius: 14px;
            overflow: hidden;
            background: #ffffff;
            box-shadow: 0 4px 15px rgba(0,0,0,0.04);
            transition: all 0.3s ease;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .video-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(13, 110, 253, 0.15);
            border-color: #b6d4fe;
        }
        .video-thumb-container {
            position: relative;
            width: 100%;
            padding-top: 56.25%; /* 16:9 Aspect Ratio */
            background-color: #000;
            overflow: hidden;
        }
        .video-thumb {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.4s ease;
        }
        .video-card:hover .video-thumb {
            transform: scale(1.05);
        }
        .play-overlay {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 48px;
            height: 48px;
            background: rgba(13, 110, 253, 0.85);
            color: #ffffff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            opacity: 0.9;
            transition: all 0.25s ease;
        }
        .video-card:hover .play-overlay {
            background: #0d6efd;
            transform: translate(-50%, -50%) scale(1.15);
            opacity: 1;
        }
        .video-title-link {
            color: #212529;
            text-decoration: none;
            font-weight: 600;
            font-size: 1.05rem;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.2s ease;
        }
        .video-title-link:hover {
            color: #0d6efd;
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
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>
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

    <!-- Header & Breadcrumb -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h3 class="fw-bold text-dark mb-1">
                <i class="fa fa-video text-primary me-2"></i>
                <c:choose>
                    <c:when test="${scope == 'all'}">Tất Cả Video Hệ Thống</c:when>
                    <c:otherwise>Video Của Tôi</c:otherwise>
                </c:choose>
            </h3>
            <p class="text-muted small mb-0">Quản lý và theo dõi danh sách video đăng tải (Phân trang 6 Video/trang)</p>
        </div>
        <div class="mt-2 mt-sm-0 d-flex gap-2">
            <c:if test="${sessionScope.account.canAccessAdmin()}">
                <c:choose>
                    <c:when test="${scope == 'all'}">
                        <a href="${pageContext.request.contextPath}/video" class="btn btn-outline-secondary btn-sm">
                            <i class="fa fa-user me-1"></i>Xem Video Của Tôi
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/video?scope=all" class="btn btn-outline-primary btn-sm">
                            <i class="fa fa-globe me-1"></i>Xem Toàn Bộ Video (Admin)
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <a href="${pageContext.request.contextPath}/video/add" class="btn btn-success btn-sm">
                <i class="fa fa-plus me-1"></i>Thêm Video Mới
            </a>
        </div>
    </div>

    <!-- Thông báo kết quả thao tác CRUD -->
    <c:if test="${param.alert == 'add_success'}">
        <div class="alert alert-success alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-check-circle me-2 flex-shrink-0"></i>
            <div>Đăng tải video mới thành công! Video đã được thêm vào hệ thống.</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.alert == 'update_success'}">
        <div class="alert alert-info alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-check-circle me-2 flex-shrink-0"></i>
            <div>Cập nhật thông tin video thành công!</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.alert == 'delete_success'}">
        <div class="alert alert-warning alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-trash-alt me-2 flex-shrink-0"></i>
            <div>Đã xóa video và các file vật lý liên quan trên server thành công!</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.alert == 'unauthorized'}">
        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa fa-shield-alt me-2 flex-shrink-0"></i>
            <div>Bạn không có quyền chỉnh sửa hoặc xóa video của người khác!</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Danh Sách Video Grid -->
    <c:choose>
        <c:when test="${empty videoList}">
            <div class="text-center py-5 bg-white rounded-3 shadow-sm my-4">
                <div class="mb-3 text-secondary">
                    <i class="fa fa-film fa-4x opacity-50"></i>
                </div>
                <h4 class="fw-bold text-dark">Chưa Có Video Nào Được Đăng Tải</h4>
                <p class="text-muted small">Bạn chưa tải lên video nào. Hãy bắt đầu chia sẻ video đầu tiên của bạn!</p>
                <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary px-4 rounded-pill">
                    <i class="fa fa-cloud-upload-alt me-1"></i>Đăng Tải Video Ngay
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
                <c:forEach var="v" items="${videoList}">
                    <div class="col">
                        <div class="video-card">
                            <!-- Thumbnail & Play Link -->
                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="video-thumb-container d-block">
                                <c:choose>
                                    <c:when test="${not empty v.poster}">
                                        <img src="${pageContext.request.contextPath}/image?fname=${v.poster}" alt="${v.title}" class="video-thumb" onerror="this.src='${pageContext.request.contextPath}/image?fname=cat_dientu.png';">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/image?fname=cat_dientu.png" alt="${v.title}" class="video-thumb">
                                    </c:otherwise>
                                </c:choose>
                                <div class="play-overlay">
                                    <i class="fa fa-play"></i>
                                </div>
                            </a>

                            <!-- Card Body -->
                            <div class="p-3 d-flex flex-column flex-grow-1">
                                <!-- Tiêu đề Video (Bấm được và mở trang chi tiết theo yêu cầu đề bài) -->
                                <h5 class="mb-2">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="video-title-link" title="${v.title}">
                                        ${v.title}
                                    </a>
                                </h5>

                                <p class="text-muted small mb-3 flex-grow-1" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                                    ${not empty v.description ? v.description : 'Chưa có mô tả cho video này.'}
                                </p>

                                <!-- Meta Info -->
                                <div class="d-flex justify-content-between align-items-center text-secondary small pt-2 border-top mb-3">
                                    <span>
                                        <i class="fa fa-eye me-1 text-primary"></i><strong><fmt:formatNumber value="${v.views}" /></strong> lượt xem
                                    </span>
                                    <span>
                                        <i class="fa fa-calendar-alt me-1"></i><fmt:formatDate value="${v.uploadDate}" pattern="dd/MM/yyyy" />
                                    </span>
                                </div>

                                <!-- Action Buttons (Chỉ chính chủ hoặc Admin mới có nút Sửa/Xóa) -->
                                <div class="d-flex gap-2">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="btn btn-outline-primary btn-sm flex-grow-1">
                                        <i class="fa fa-play me-1"></i>Phát Video
                                    </a>
                                    <c:if test="${v.userId == sessionScope.account.id || sessionScope.account.canAccessAdmin()}">
                                        <a href="${pageContext.request.contextPath}/video/edit?id=${v.videoId}" class="btn btn-outline-secondary btn-sm" title="Chỉnh sửa">
                                            <i class="fa fa-edit"></i>
                                        </a>
                                        <a href="javascript:void(0);" onclick="confirmDelete('${v.videoId}', '${v.title}')" class="btn btn-outline-danger btn-sm" title="Xóa video">
                                            <i class="fa fa-trash"></i>
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Phân Trang Database Chuẩn (6 Video/trang) -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="mt-4">
                    <ul class="pagination justify-content-center">
                        <!-- Nút Trang Trước -->
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/video?page=${currentPage - 1}${scope == 'all' ? '&scope=all' : ''}">
                                <i class="fa fa-chevron-left"></i> Trước
                            </a>
                        </li>

                        <!-- Các Số Trang -->
                        <c:forEach var="p" begin="1" end="${totalPages}">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/video?page=${p}${scope == 'all' ? '&scope=all' : ''}">
                                    ${p}
                                </a>
                            </li>
                        </c:forEach>

                        <!-- Nút Trang Sau -->
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/video?page=${currentPage + 1}${scope == 'all' ? '&scope=all' : ''}">
                                Sau <i class="fa fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:otherwise>
    </c:choose>

</div>

<!-- Modal Xác Nhận Xóa Video -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold"><i class="fa fa-exclamation-triangle me-2"></i>Xác Nhận Xóa Video</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p class="mb-2">Bạn có chắc chắn muốn xóa video sau đây không?</p>
                <p class="fw-bold text-dark fs-6" id="deleteVideoTitle"></p>
                <div class="alert alert-warning small mb-0">
                    <i class="fa fa-info-circle me-1"></i>Hành động này sẽ xóa vĩnh viễn video khỏi cơ sở dữ liệu và dọn sạch file trên máy chủ.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Hủy bỏ</button>
                <a href="#" id="btnConfirmDelete" class="btn btn-danger btn-sm">
                    <i class="fa fa-trash me-1"></i>Xác Nhận Xóa
                </a>
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
<script>
    function confirmDelete(videoId, title) {
        document.getElementById('deleteVideoTitle').innerText = title;
        document.getElementById('btnConfirmDelete').href = '${pageContext.request.contextPath}/video/delete?id=' + videoId;
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>

</body>
</html>
