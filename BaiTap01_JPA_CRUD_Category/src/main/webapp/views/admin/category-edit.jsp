<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập nhật Category - JPA Hibernate</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border: none; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
        .img-preview { border-radius: 8px; border: 1px solid #dee2e6; object-fit: cover; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/categories">
            <i class="fa fa-layer-group text-primary me-2"></i>JPA 3.0 - HIBERNATE CRUD CATEGORY
        </a>
    </div>
</nav>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-7 col-md-9">
            <div class="card card-custom p-4 bg-white">
                <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
                    <h4 class="fw-bold text-dark mb-0">
                        <i class="fa fa-edit text-primary me-2"></i>Cập Nhật Category #${cate.categoryid}
                    </h4>
                    <a href="<c:url value='/admin/categories'/>" class="btn btn-outline-secondary btn-sm">
                        <i class="fa fa-arrow-left me-1"></i>Quay lại danh sách
                    </a>
                </div>

                <!-- Form theo chuẩn Section 6 tài liệu mẫu -->
                <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data">
                    <!-- Trường ID ẩn -->
                    <input type="hidden" name="categoryid" value="${cate.categoryid}">

                    <div class="mb-3">
                        <label for="categoryname" class="form-label fw-semibold">
                            Tên Category: <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="categoryname" name="categoryname" 
                               value="${cate.categoryname}" required>
                    </div>

                    <div class="mb-3">
                        <label for="images" class="form-label fw-semibold">Link Hình Ảnh (URL):</label>
                        <input type="text" class="form-control" id="images" name="images" 
                               value="${cate.images}">
                    </div>

                    <!-- Hiển thị ảnh hiện tại -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold d-block">Hình ảnh hiện tại:</label>
                        <c:choose>
                            <c:when test="${cate.images != null && cate.images.startsWith('http')}">
                                <c:url value="${cate.images}" var="imgUrl"></c:url>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/image?fname=${cate.images}" var="imgUrl"></c:url>
                            </c:otherwise>
                        </c:choose>
                        <img height="150" width="200" src="${imgUrl}" class="img-preview shadow-sm" alt="${cate.categoryname}"
                             onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';" /><br>
                        <span class="small text-muted mt-1 d-inline-block">File: <code>${cate.images}</code></span>
                    </div>

                    <div class="mb-3">
                        <label for="images1" class="form-label fw-semibold">Thay Đổi Hình Ảnh Mới:</label>
                        <input type="file" class="form-control" id="images1" name="images1" accept="image/*">
                        <div class="form-text">Nếu không chọn file mới, hệ thống sẽ giữ nguyên ảnh hiện tại.</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold d-block">Trạng Thái (Status):</label>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" id="ston" name="status" value="1" 
                                   ${cate.status == 1 ? 'checked' : ''}>
                            <label class="form-check-label text-success fw-semibold" for="ston">
                                <i class="fa fa-check me-1"></i>Hoạt động
                            </label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" id="stoff" name="status" value="0" 
                                   ${cate.status != 1 ? 'checked' : ''}>
                            <label class="form-check-label text-danger fw-semibold" for="stoff">
                                <i class="fa fa-lock me-1"></i>Khóa
                            </label>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary px-4 fw-semibold">
                            <i class="fa fa-sync-alt me-1"></i>Update
                        </button>
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary px-4">
                            Hủy
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<footer class="text-center py-4 text-muted small">
    &copy; 2026 BaiTap01_JPA_CRUD_Category - Đồ án Lập trình Web - HCMUTE
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
