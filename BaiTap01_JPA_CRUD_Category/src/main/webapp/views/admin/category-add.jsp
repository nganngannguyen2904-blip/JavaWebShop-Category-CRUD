<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Category Mới - JPA Hibernate</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border: none; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
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
                        <i class="fa fa-plus-circle text-success me-2"></i>Thêm Category Mới
                    </h4>
                    <a href="<c:url value='/admin/categories'/>" class="btn btn-outline-secondary btn-sm">
                        <i class="fa fa-arrow-left me-1"></i>Quay lại danh sách
                    </a>
                </div>

                <!-- Form theo chuẩn Section 6 tài liệu mẫu -->
                <form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label for="categoryname" class="form-label fw-semibold">
                            Tên Category: <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="categoryname" name="categoryname" 
                               placeholder="Ví dụ: Laptop Gaming, Điện thoại..." required>
                        <div class="form-text">Tên danh mục không được phép để rỗng (@NotEmpty).</div>
                    </div>

                    <div class="mb-3">
                        <label for="images" class="form-label fw-semibold">Link Hình Ảnh (URL):</label>
                        <input type="text" class="form-control" id="images" name="images" 
                               placeholder="https://example.com/image.jpg hoặc để trống nếu upload file">
                    </div>

                    <div class="mb-3">
                        <label for="images1" class="form-label fw-semibold">Hoặc Upload Hình Ảnh:</label>
                        <input type="file" class="form-control" id="images1" name="images1" accept="image/*">
                        <div class="form-text">Hỗ trợ định dạng: PNG, JPG, JPEG, WEBP, GIF (Tối đa 10MB).</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold d-block">Trạng Thái (Status):</label>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" id="ston" name="status" value="1" checked>
                            <label class="form-check-label text-success fw-semibold" for="ston">
                                <i class="fa fa-check me-1"></i>Hoạt động
                            </label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" id="stoff" name="status" value="0">
                            <label class="form-check-label text-danger fw-semibold" for="stoff">
                                <i class="fa fa-lock me-1"></i>Khóa
                            </label>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-success px-4 fw-semibold">
                            <i class="fa fa-save me-1"></i>Insert
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
