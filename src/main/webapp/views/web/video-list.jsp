<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Video Thời Trang & Lookbook - Shopping Service</title>
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
        .fashion-header {
            background: linear-gradient(135deg, #0d6efd 0%, #002b66 100%);
            color: #ffffff;
            border-radius: 16px;
            padding: 28px 32px;
            margin-bottom: 24px;
            box-shadow: 0 8px 24px rgba(13, 110, 253, 0.12);
        }
        .filter-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e9ecef;
            box-shadow: 0 4px 15px rgba(0,0,0,0.03);
            padding: 20px;
            margin-bottom: 24px;
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
            box-shadow: 0 12px 28px rgba(13, 110, 253, 0.14);
            border-color: #b6d4fe;
        }
        .video-thumb-container {
            position: relative;
            width: 100%;
            aspect-ratio: 16 / 9;
            background-color: #0b1320;
            overflow: hidden;
            display: block;
        }
        .video-thumb {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.4s ease;
            display: block;
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
            background: rgba(13, 110, 253, 0.9);
            color: #ffffff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            opacity: 0.92;
            transition: all 0.25s ease;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        }
        .video-card:hover .play-overlay {
            background: #0d6efd;
            transform: translate(-50%, -50%) scale(1.15);
            opacity: 1;
        }
        .badge-views {
            position: absolute;
            bottom: 8px;
            left: 8px;
            background: rgba(0, 0, 0, 0.72);
            color: #ffffff;
            font-size: 0.75rem;
            padding: 3px 8px;
            border-radius: 6px;
            backdrop-filter: blur(4px);
        }
        .badge-date {
            position: absolute;
            bottom: 8px;
            right: 8px;
            background: rgba(0, 0, 0, 0.72);
            color: #ffffff;
            font-size: 0.75rem;
            padding: 3px 8px;
            border-radius: 6px;
            backdrop-filter: blur(4px);
        }
        .video-title-link {
            color: #212529;
            text-decoration: none;
            font-weight: 600;
            font-size: 1rem;
            line-height: 1.35;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.2s ease;
        }
        .video-title-link:hover {
            color: #0d6efd;
        }
        .video-desc {
            font-size: 0.83rem;
            color: #6c757d;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
        }
        .linked-product-box {
            background: #f8faff;
            border: 1px dashed #b6d4fe;
            border-radius: 10px;
            padding: 10px 12px;
            margin-top: auto;
            transition: all 0.2s ease;
        }
        .linked-product-box:hover {
            background: #eef5ff;
            border-color: #0d6efd;
        }
        .product-mini-thumb {
            width: 48px;
            height: 48px;
            border-radius: 8px;
            object-fit: cover;
            flex-shrink: 0;
            background: #ffffff;
            border: 1px solid #e9ecef;
        }
        .page-link {
            border-radius: 8px;
            margin: 0 3px;
            color: #495057;
            font-weight: 500;
        }
        .page-item.active .page-link {
            background-color: #0d6efd;
            border-color: #0d6efd;
            font-weight: 700;
        }
    </style>
</head>
<body>

<div class="container py-4">

    <!-- Fashion E-Commerce Banner -->
    <div class="fashion-header d-flex flex-wrap justify-content-between align-items-center">
        <div>
            <h3 class="fw-bold mb-1"><i class="fa fa-tshirt me-2"></i>Kênh Video Thời Trang & Lookbook Phối Đồ</h3>
            <p class="mb-0 text-white-50 small">Khám phá video trải nghiệm thực tế các mẫu quần áo hot trend, phong cách phối đồ và liên kết mua hàng trực tiếp.</p>
        </div>
        <div class="mt-3 mt-md-0 d-flex gap-2">
            <c:if test="${sessionScope.account.canAccessAdmin()}">
                <c:choose>
                    <c:when test="${scope == 'all'}">
                        <a href="${pageContext.request.contextPath}/video" class="btn btn-light btn-sm fw-semibold">
                            <i class="fa fa-user me-1 text-primary"></i>Xem Video Của Tôi
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/video?scope=all" class="btn btn-light btn-sm fw-semibold">
                            <i class="fa fa-globe me-1 text-primary"></i>Xem Toàn Bộ Video (Admin)
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <a href="${pageContext.request.contextPath}/video/add" class="btn btn-warning btn-sm fw-bold text-dark">
                <i class="fa fa-plus-circle me-1"></i>Đăng Video Lookbook
            </a>
        </div>
    </div>

    <!-- Thông báo kết quả thao tác nếu có -->
    <c:if test="${not empty msgSuccess}">
        <div class="alert alert-success alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
            <i class="fa fa-check-circle me-2 fs-5"></i>
            <div><strong>Thành công:</strong> ${msgSuccess}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty msgError}">
        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center mb-4" role="alert">
            <i class="fa fa-exclamation-triangle me-2 fs-5"></i>
            <div><strong>Thông báo:</strong> ${msgError}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Thanh Tìm Kiếm & Bộ Lọc Nâng Cao -->
    <div class="filter-card">
        <form action="${pageContext.request.contextPath}/video" method="get" class="row g-3 align-items-end">
            <c:if test="${scope == 'all'}">
                <input type="hidden" name="scope" value="all" />
            </c:if>

            <!-- 1. Tìm kiếm theo tiêu đề / mô tả -->
            <div class="col-lg-4 col-md-6">
                <label class="form-label small fw-bold text-secondary mb-1">
                    <i class="fa fa-search me-1 text-primary"></i>Tìm kiếm theo tiêu đề video
                </label>
                <div class="input-group input-group-sm">
                    <input type="text" 
                           class="form-control" 
                           name="keyword" 
                           value="<c:out value='${keyword}'/>" 
                           placeholder="Nhập tên video, áo sơ mi, đầm nữ, jean..." />
                    <c:if test="${not empty keyword}">
                        <a href="${pageContext.request.contextPath}/video${scope == 'all' ? '?scope=all' : ''}" class="btn btn-outline-secondary" title="Xóa từ khóa">
                            <i class="fa fa-times"></i>
                        </a>
                    </c:if>
                </div>
            </div>

            <!-- 2. Lọc theo sản phẩm quần áo liên kết -->
            <div class="col-lg-3 col-md-6">
                <label class="form-label small fw-bold text-secondary mb-1">
                    <i class="fa fa-tags me-1 text-success"></i>Sản phẩm thời trang liên kết
                </label>
                <select name="productId" class="form-select form-select-sm">
                    <option value="">-- Tất cả sản phẩm --</option>
                    <c:forEach items="${productList}" var="p">
                        <option value="${p.id}" ${productId == p.id ? 'selected' : ''}>
                            [#${p.id}] ${p.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- 3. Sắp xếp theo ngày đăng / lượt xem -->
            <div class="col-lg-3 col-md-6">
                <label class="form-label small fw-bold text-secondary mb-1">
                    <i class="fa fa-sort-amount-down me-1 text-info"></i>Sắp xếp video
                </label>
                <select name="sort" class="form-select form-select-sm">
                    <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Mới nhất (Ngày đăng giảm dần)</option>
                    <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Cũ nhất (Ngày đăng tăng dần)</option>
                    <option value="views" ${sort == 'views' ? 'selected' : ''}>Nhiều lượt xem nhất (Phổ biến)</option>
                    <option value="title" ${sort == 'title' ? 'selected' : ''}>Tiêu đề Video (A - Z)</option>
                </select>
            </div>

            <!-- 4. Buttons Submit & Reset -->
            <div class="col-lg-2 col-md-6 d-flex gap-2">
                <button type="submit" class="btn btn-primary btn-sm flex-grow-1 fw-bold">
                    <i class="fa fa-filter me-1"></i>Tìm & Lọc
                </button>
                <a href="${pageContext.request.contextPath}/video${scope == 'all' ? '?scope=all' : ''}" class="btn btn-outline-secondary btn-sm" title="Đặt lại bộ lọc">
                    <i class="fa fa-undo"></i>
                </a>
            </div>
        </form>

        <!-- Trạng thái lọc hiển thị tóm tắt -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mt-3 pt-2 border-top small text-muted">
            <div>
                Hiển thị <strong>${videoList != null ? videoList.size() : 0}</strong> trên tổng số <strong>${totalVideos}</strong> video
                <c:if test="${not empty keyword}">
                    &bull; Từ khóa: <span class="badge bg-primary-subtle text-primary">"${keyword}"</span>
                </c:if>
                <c:if test="${not empty productId}">
                    &bull; Lọc theo sản phẩm ID: <span class="badge bg-success-subtle text-success">#${productId}</span>
                </c:if>
            </div>
            <div>
                Phân trang cố định: <strong>${pageSize} Video / trang</strong> (Trang ${currentPage} / ${totalPages > 0 ? totalPages : 1})
            </div>
        </div>
    </div>

    <!-- Danh Sách Video Cards (Lưới 3 cột chuẩn Responsive) -->
    <c:choose>
        <c:when test="${empty videoList}">
            <!-- Trạng thái trống (Empty State) -->
            <div class="text-center py-5 bg-white rounded-4 shadow-sm my-4 border">
                <div class="mb-3 text-secondary">
                    <i class="fa fa-tshirt fa-4x opacity-50 text-primary"></i>
                </div>
                <h4 class="fw-bold text-dark">Chưa Có Video Thời Trang Nào Phù Hợp</h4>
                <p class="text-muted small mb-3">
                    <c:choose>
                        <c:when test="${not empty keyword || not empty productId}">
                            Không tìm thấy video nào phù hợp với bộ lọc tìm kiếm hiện tại. Vui lòng thử lại với từ khóa khác!
                        </c:when>
                        <c:otherwise>
                            Bạn chưa đăng tải video nào. Hãy bắt đầu chia sẻ video phối đồ thời trang đầu tiên của bạn!
                        </c:otherwise>
                    </c:choose>
                </p>
                <div class="d-flex justify-content-center gap-2">
                    <c:if test="${not empty keyword || not empty productId}">
                        <a href="${pageContext.request.contextPath}/video${scope == 'all' ? '?scope=all' : ''}" class="btn btn-outline-secondary btn-sm px-3 rounded-pill">
                            <i class="fa fa-undo me-1"></i>Xóa Bộ Lọc
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary btn-sm px-4 rounded-pill fw-bold">
                        <i class="fa fa-cloud-upload-alt me-1"></i>Đăng Tải Video Ngay
                    </a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
                <c:forEach var="v" items="${videoList}">
                    <div class="col">
                        <div class="video-card">
                            <!-- Thumbnail 16:9 Chuẩn & Nút Play -->
                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="video-thumb-container">
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
                                <span class="badge-views">
                                    <i class="fa fa-eye me-1"></i>${v.views} xem
                                </span>
                                <span class="badge-date">
                                    <fmt:formatDate value="${v.uploadDate}" pattern="dd/MM/yyyy" />
                                </span>
                            </a>

                            <!-- Nội Dung Card -->
                            <div class="p-3 d-flex flex-column flex-grow-1">
                                <!-- Tiêu đề Video (Bấm mở chi tiết) -->
                                <h5 class="mb-2">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="video-title-link" title="${v.title}">
                                        ${v.title}
                                    </a>
                                </h5>

                                <!-- Mô tả ngắn -->
                                <p class="video-desc mb-2">
                                    <c:choose>
                                        <c:when test="${not empty v.description}">${v.description}</c:when>
                                        <c:otherwise>Video chia sẻ phong cách thời trang và lookbook phối đồ.</c:otherwise>
                                    </c:choose>
                                </p>

                                <!-- Người đăng & Trạng thái -->
                                <div class="d-flex justify-content-between align-items-center small text-muted mb-2">
                                    <span>
                                        <i class="fa fa-user-circle me-1 text-primary"></i>
                                        <c:out value="${not empty v.userFullName ? v.userFullName : v.userUsername}"/>
                                    </span>
                                    <span>
                                        <c:choose>
                                            <c:when test="${v.active == 1}">
                                                <span class="badge bg-success-subtle text-success small">Công khai</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary-subtle text-secondary small">Đang ẩn</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <!-- Box Sản Phẩm Thời Trang Tương Ứng (Nếu có liên kết) -->
                                <c:choose>
                                    <c:when test="${v.hasLinkedProduct()}">
                                        <div class="linked-product-box mb-3 d-flex align-items-center gap-2">
                                            <img src="${pageContext.request.contextPath}/image?fname=${v.productImage}" 
                                                 alt="${v.productName}" 
                                                 class="product-mini-thumb" 
                                                 onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';" />
                                            <div class="flex-grow-1 text-truncate">
                                                <div class="small fw-bold text-dark text-truncate" title="${v.productName}">
                                                    ${v.productName}
                                                </div>
                                                <div class="text-danger small fw-semibold">
                                                    <fmt:formatNumber value="${v.productPrice}" pattern="#,###" /> ₫
                                                </div>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${v.productId}" 
                                               class="btn btn-sm btn-outline-primary px-2 py-1 text-nowrap" 
                                               title="Xem chi tiết sản phẩm thời trang này" target="_blank">
                                                <i class="fa fa-shopping-bag me-1"></i>Xem SP
                                            </a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-muted small fst-italic mb-3 py-1">
                                            <i class="fa fa-info-circle me-1"></i>Chưa liên kết sản phẩm thời trang
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <!-- Nút Chức Năng (CRUD & Xem chi tiết) -->
                                <div class="d-flex justify-content-between align-items-center border-top pt-2 mt-auto">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="btn btn-sm btn-outline-primary">
                                        <i class="fa fa-play me-1"></i>Xem Video
                                    </a>

                                    <!-- Chỉ chính chủ hoặc Admin/Manager mới được sửa/xóa -->
                                    <c:if test="${v.userId == sessionScope.account.id || sessionScope.account.canAccessAdmin()}">
                                        <div class="d-flex gap-1">
                                            <a href="${pageContext.request.contextPath}/video/edit?id=${v.videoId}" class="btn btn-sm btn-outline-secondary" title="Chỉnh sửa thông tin">
                                                <i class="fa fa-edit"></i> Sửa
                                            </a>
                                            <button type="button" 
                                                    class="btn btn-sm btn-outline-danger btn-open-delete" 
                                                    data-id="${v.videoId}" 
                                                    data-title="<c:out value='${v.title}'/>"
                                                    title="Xóa video">
                                                <i class="fa fa-trash"></i> Xóa
                                            </button>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Phân Trang Cố Định 6 Video/Trang (Giữ nguyên tham số filter) -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="mt-4 mb-4">
                    <ul class="pagination justify-content-center">
                        <!-- Nút Trang Trước -->
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/video?page=${currentPage - 1}${not empty keyword ? '&keyword=' : ''}${keyword}${not empty sort ? '&sort=' : ''}${sort}${not empty productId ? '&productId=' : ''}${productId}${scope == 'all' ? '&scope=all' : ''}">
                                <i class="fa fa-chevron-left me-1"></i>Trước
                            </a>
                        </li>

                        <!-- Các Số Trang -->
                        <c:forEach var="p" begin="1" end="${totalPages}">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/video?page=${p}${not empty keyword ? '&keyword=' : ''}${keyword}${not empty sort ? '&sort=' : ''}${sort}${not empty productId ? '&productId=' : ''}${productId}${scope == 'all' ? '&scope=all' : ''}">
                                    ${p}
                                </a>
                            </li>
                        </c:forEach>

                        <!-- Nút Trang Sau -->
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/video?page=${currentPage + 1}${not empty keyword ? '&keyword=' : ''}${keyword}${not empty sort ? '&sort=' : ''}${sort}${not empty productId ? '&productId=' : ''}${productId}${scope == 'all' ? '&scope=all' : ''}">
                                Sau <i class="fa fa-chevron-right ms-1"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:otherwise>
    </c:choose>

</div>

<!-- Modal Xác Nhận Xóa Video Chuyên Nghiệp -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold"><i class="fa fa-exclamation-triangle me-2"></i>Xác Nhận Xóa Video Thời Trang</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <p class="mb-2 text-secondary">Bạn có chắc chắn muốn xóa video sau đây khỏi hệ thống?</p>
                <div class="p-3 bg-light rounded border mb-3">
                    <div class="fw-bold text-dark fs-6" id="deleteVideoTitle"></div>
                    <div class="small text-muted mt-1" id="deleteVideoIdText"></div>
                </div>
                <div class="alert alert-warning small mb-0 d-flex align-items-center">
                    <i class="fa fa-info-circle me-2 fs-5 text-warning"></i>
                    <div>Hành động này sẽ xóa vĩnh viễn video khỏi cơ sở dữ liệu và dọn dẹp sạch tập tin video cùng ảnh bìa trên máy chủ. Không thể hoàn tác!</div>
                </div>
            </div>
            <div class="modal-footer border-top bg-light">
                <button type="button" class="btn btn-secondary btn-sm px-3" data-bs-dismiss="modal">Hủy bỏ</button>
                <a href="#" id="btnConfirmDelete" class="btn btn-danger btn-sm px-4 fw-bold">
                    <i class="fa fa-trash me-1"></i>Xác Nhận Xóa Ngay
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Script Xử Lý Modal Xóa Video -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        const deleteTitleEl = document.getElementById('deleteVideoTitle');
        const deleteIdEl = document.getElementById('deleteVideoIdText');
        const confirmBtn = document.getElementById('btnConfirmDelete');

        document.querySelectorAll('.btn-open-delete').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const videoId = this.getAttribute('data-id');
                const videoTitle = this.getAttribute('data-title');

                deleteTitleEl.textContent = videoTitle || 'Video không tên';
                deleteIdEl.textContent = 'Mã Video: ' + videoId;
                confirmBtn.href = '${pageContext.request.contextPath}/video/delete?id=' + encodeURIComponent(videoId);

                deleteModal.show();
            });
        });
    });
</script>

</body>
</html>
