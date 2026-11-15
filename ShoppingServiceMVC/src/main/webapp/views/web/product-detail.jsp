<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - Chi Tiết Sản Phẩm</title>
    <!-- Google Fonts -->
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
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            color: #212529;
        }
        .main-navbar {
            background: #ffffff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.06);
        }
        .detail-card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.05);
            padding: 30px;
        }
        .detail-img-box {
            background: #f8f9fa;
            border-radius: 12px;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px solid #e9ecef;
            aspect-ratio: 1/1;
        }
        .detail-img-box img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .price-badge {
            background: #fff5f5;
            border: 1px dashed #f5c2c7;
            border-radius: 8px;
            padding: 16px 20px;
            margin: 20px 0;
        }
        .price-val {
            font-size: 32px;
            font-weight: 800;
            color: #dc3545;
        }
        .policy-box {
            border: 1px solid #edf2f7;
            background: #fcfdfe;
            border-radius: 8px;
            padding: 12px;
            text-align: center;
            height: 100%;
        }
        .related-card {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e9ecef;
            overflow: hidden;
            transition: all 0.25s ease;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .related-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            border-color: #0d6efd;
        }
        .related-card img {
            width: 100%;
            aspect-ratio: 1/1;
            object-fit: cover;
        }
        .footer {
            background: #1e242b;
            color: #adb5bd;
            padding: 45px 0 20px;
            margin-top: 60px;
        }
    </style>
</head>
<body>

<!-- 1. Topbar Navigation -->
<jsp:include page="/views/common/topbar.jsp" />

<!-- 2. Main Navbar -->
<nav class="navbar navbar-expand-lg main-navbar sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary fs-4" href="${pageContext.request.contextPath}/home">
            <i class="fa fa-shopping-bag me-2"></i>Shopping<span class="text-dark">Service</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                <li class="nav-item">
                    <a class="nav-link fw-semibold" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active fw-semibold text-primary" href="${pageContext.request.contextPath}/products">Sản phẩm</a>
                </li>
                <c:if test="${sessionScope.account != null && (sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2)}">
                    <li class="nav-item">
                        <a class="nav-link fw-semibold text-danger" href="${pageContext.request.contextPath}/admin/category/list">
                            <i class="fa fa-cog me-1"></i>Quản trị Danh mục
                        </a>
                    </li>
                </c:if>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <form class="d-flex" action="${pageContext.request.contextPath}/products" method="get">
                    <div class="input-group">
                        <input class="form-control form-control-sm" type="search" name="keyword" placeholder="Tìm kiếm sản phẩm..." aria-label="Search">
                        <button class="btn btn-outline-primary btn-sm" type="submit"><i class="fa fa-search"></i></button>
                    </div>
                </form>

                <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary btn-sm position-relative ms-2" title="Xem giỏ hàng">
                    <i class="fa fa-shopping-cart"></i>
                    <c:if test="${sessionScope.cart != null && sessionScope.cart.totalQuantity > 0}">
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">${sessionScope.cart.totalQuantity}</span>
                    </c:if>
                </a>

                <!-- User Auth Profile -->
                <c:choose>
                    <c:when test="${sessionScope.account != null}">
                        <div class="dropdown ms-2">
                            <button class="btn btn-outline-secondary btn-sm dropdown-toggle fw-semibold d-flex align-items-center" type="button" data-bs-toggle="dropdown">
                                <i class="fa fa-user-circle me-1 text-primary"></i>
                                <span class="d-none d-md-inline"><c:out value="${sessionScope.account.fullName}"/></span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                                <li class="dropdown-header small text-muted">
                                    <strong><c:out value="${sessionScope.account.userName}"/></strong>
                                    (<span class="badge ${sessionScope.account.roleid == 1 ? 'bg-danger' : 'bg-primary'}">${sessionScope.account.roleid == 1 ? 'Admin' : 'Khách hàng'}</span>)
                                </li>
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                    <li><a class="dropdown-item text-danger fw-semibold" href="${pageContext.request.contextPath}/admin/home"><i class="fa fa-cogs me-2"></i>Trang Quản Trị</a></li>
                                </c:if>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/change-password"><i class="fa fa-key me-2 text-warning"></i>Đổi mật khẩu</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger fw-semibold" href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex align-items-center gap-1 ms-2">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-outline-primary fw-semibold">
                                <i class="fa fa-sign-in me-1"></i>Đăng nhập
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-sm btn-primary fw-semibold text-white">
                                <i class="fa fa-user-plus me-1"></i>Đăng ký
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<!-- 3. Breadcrumb -->
<div class="bg-white border-bottom py-2">
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-0 small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Trang chủ</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products" class="text-decoration-none">Sản phẩm</a></li>
                <c:if test="${category != null}">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/products?cateid=${category.id}" class="text-decoration-none">${category.name}</a>
                    </li>
                </c:if>
                <li class="breadcrumb-item active text-primary fw-semibold" aria-current="page">${product.name}</li>
            </ol>
        </nav>
    </div>
</div>

<!-- 4. Chi tiết sản phẩm -->
<div class="container py-4">
    <div class="detail-card">
        <div class="row g-5">
            <!-- Cột hình ảnh sản phẩm -->
            <div class="col-lg-5">
                <div class="detail-img-box mb-3">
                    <img src="${pageContext.request.contextPath}/image?fname=${product.image}" 
                         alt="${product.name}"
                         onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                </div>
                <div class="d-flex align-items-center justify-content-center gap-3 text-muted small">
                    <span><i class="fa fa-check-circle text-success me-1"></i>Hình ảnh thực tế</span>
                    <span>•</span>
                    <span><i class="fa fa-shield-alt text-primary me-1"></i>Hàng chính hãng 100%</span>
                </div>
            </div>

            <!-- Cột thông tin chi tiết sản phẩm -->
            <div class="col-lg-7">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <span class="badge bg-primary">${category.name}</span>
                    <span class="badge bg-light text-secondary border">Mã SP: #${product.id}</span>
                </div>

                <h2 class="fw-bold text-dark mb-2">${product.name}</h2>

                <div class="d-flex align-items-center gap-3 mb-3 small">
                    <div class="text-warning">
                        <i class="fa fa-star"></i>
                        <i class="fa fa-star"></i>
                        <i class="fa fa-star"></i>
                        <i class="fa fa-star"></i>
                        <i class="fa fa-star"></i>
                        <span class="text-dark fw-bold ms-1">5.0</span>
                    </div>
                    <span class="text-muted">|</span>
                    <span class="text-muted">Đã bán <strong>240+</strong></span>
                    <span class="text-muted">|</span>
                    <c:choose>
                        <c:when test="${product.quantity > 0}">
                            <span class="text-success fw-semibold"><i class="fa fa-check me-1"></i>Còn hàng (${product.quantity} sản phẩm)</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-danger fs-6"><i class="fa fa-times-circle me-1"></i>TẠM HẾT HÀNG</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Khung giá -->
                <div class="price-badge">
                    <div class="small text-muted mb-1">Giá bán niêm yết:</div>
                    <div class="price-val">${product.formattedPrice}</div>
                    <div class="small text-success mt-1"><i class="fa fa-bolt me-1"></i>Ưu đãi giảm giá trực tiếp từ hệ thống Shopping Service</div>
                </div>

                <!-- Mô tả tóm tắt -->
                <div class="mb-4">
                    <h6 class="fw-bold text-dark mb-2">Mô tả sản phẩm:</h6>
                    <p class="text-secondary leading-relaxed">${product.description}</p>
                </div>

                <!-- Chọn số lượng & Nút Mua -->
                <div class="mb-4">
                    <c:choose>
                        <c:when test="${product.quantity > 0}">
                            <label class="form-label fw-semibold small text-muted">Số lượng mua:</label>
                            <div class="d-flex align-items-center gap-3 mb-4">
                                <div class="input-group" style="width: 130px;">
                                    <button class="btn btn-outline-secondary btn-sm" type="button" onclick="changeQty(-1)">-</button>
                                    <input type="number" id="buyQty" class="form-control form-control-sm text-center" value="1" min="1" max="${product.quantity}">
                                    <button class="btn btn-outline-secondary btn-sm" type="button" onclick="changeQty(1)">+</button>
                                </div>
                                <span class="small text-muted">${product.quantity} sản phẩm có sẵn trong kho</span>
                            </div>

                            <div class="d-flex flex-wrap gap-3">
                                <button type="button" class="btn btn-primary btn-lg px-4 fw-semibold" onclick="handleAddToCart()">
                                    <i class="fa fa-cart-plus me-2"></i>Thêm vào giỏ hàng
                                </button>
                                <button type="button" class="btn btn-danger btn-lg px-4 fw-semibold" onclick="handleBuyNow()">
                                    <i class="fa fa-bolt me-2"></i>Mua ngay
                                </button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning border-warning d-flex align-items-center mb-4" role="alert">
                                <i class="fa fa-exclamation-triangle fa-2x me-3 text-warning"></i>
                                <div>
                                    <h6 class="fw-bold mb-1">Sản phẩm này tạm thời hết hàng!</h6>
                                    <div class="small text-muted">Kho hàng đang được nhập thêm, quý khách vui lòng chọn các sản phẩm tương tự bên dưới.</div>
                                </div>
                            </div>
                            <div class="d-flex flex-wrap gap-3">
                                <button type="button" class="btn btn-secondary btn-lg px-4 fw-semibold" disabled>
                                    <i class="fa fa-ban me-2"></i>Tạm Hết Hàng
                                </button>
                                <a href="${pageContext.request.contextPath}/products?cateid=${category.id}" class="btn btn-outline-primary btn-lg px-4 fw-semibold">
                                    <i class="fa fa-th-large me-2"></i>Xem sản phẩm cùng loại
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Chính sách cửa hàng -->
                <div class="row g-2 mt-4 pt-3 border-top">
                    <div class="col-sm-3 col-6">
                        <div class="policy-box">
                            <i class="fa fa-truck text-primary fa-lg mb-2"></i>
                            <div class="fw-semibold small">Giao hàng 2h</div>
                            <div class="text-muted" style="font-size: 11px;">Nội thành siêu tốc</div>
                        </div>
                    </div>
                    <div class="col-sm-3 col-6">
                        <div class="policy-box">
                            <i class="fa fa-sync-alt text-success fa-lg mb-2"></i>
                            <div class="fw-semibold small">Đổi trả 7 ngày</div>
                            <div class="text-muted" style="font-size: 11px;">Miễn phí đổi hàng</div>
                        </div>
                    </div>
                    <div class="col-sm-3 col-6">
                        <div class="policy-box">
                            <i class="fa fa-shield-alt text-warning fa-lg mb-2"></i>
                            <div class="fw-semibold small">Bảo hành 12T</div>
                            <div class="text-muted" style="font-size: 11px;">Chính hãng uy tín</div>
                        </div>
                    </div>
                    <div class="col-sm-3 col-6">
                        <div class="policy-box">
                            <i class="fa fa-headset text-info fa-lg mb-2"></i>
                            <div class="fw-semibold small">Hỗ trợ 24/7</div>
                            <div class="text-muted" style="font-size: 11px;">Tư vấn tận tâm</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Sản phẩm liên quan cùng danh mục -->
    <c:if test="${relatedProducts != null && !relatedProducts.isEmpty()}">
        <div class="mt-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="fw-bold text-dark mb-0">
                    <i class="fa fa-tags text-primary me-2"></i>Sản phẩm tương tự trong danh mục ${category.name}
                </h4>
                <a href="${pageContext.request.contextPath}/products?cateid=${category.id}" class="btn btn-outline-primary btn-sm">
                    Xem tất cả <i class="fa fa-arrow-right ms-1"></i>
                </a>
            </div>

            <div class="row g-3">
                <c:forEach items="${relatedProducts}" var="rp">
                    <div class="col-md-3 col-sm-6">
                        <div class="related-card">
                            <a href="${pageContext.request.contextPath}/product/detail?id=${rp.id}">
                                <img src="${pageContext.request.contextPath}/image?fname=${rp.image}" 
                                     alt="${rp.name}"
                                     onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                            </a>
                            <div class="p-3 d-flex flex-column flex-grow-1">
                                <a href="${pageContext.request.contextPath}/product/detail?id=${rp.id}" 
                                   class="fw-semibold text-dark text-decoration-none small mb-2 text-truncate" 
                                   title="${rp.name}">
                                    ${rp.name}
                                </a>
                                <div class="mt-auto d-flex justify-content-between align-items-center">
                                    <span class="text-danger fw-bold">${rp.formattedPrice}</span>
                                    <a href="${pageContext.request.contextPath}/product/detail?id=${rp.id}" class="btn btn-sm btn-outline-primary">
                                        Chi tiết
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>
</div>

<!-- Modal Thông báo Mua Hàng -->
<div class="modal fade" id="orderModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold"><i class="fa fa-check-circle me-2"></i>Đặt Hàng Thành Công</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center py-4">
                <i class="fa fa-box-check text-success fa-3x mb-3"></i>
                <h5 class="fw-bold mb-2">Cảm ơn bạn đã đặt hàng!</h5>
                <p class="text-muted small mb-0">Sản phẩm <strong>${product.name}</strong> đã được ghi nhận đơn hàng thành công. Bộ phận CSKH sẽ sớm liên hệ xác nhận và giao hàng!</p>
            </div>
            <div class="modal-footer justify-content-center">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm">Tiếp tục mua hàng</a>
                <button type="button" class="btn btn-success btn-sm" data-bs-dismiss="modal">Đã hiểu</button>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="footer">
    <div class="container text-center small">
        <p class="mb-1">&copy; 2026 Shopping Service MVC. Mô hình MVC 3 lớp kết nối SQL Server.</p>
        <p class="text-secondary mb-0">GVHD: ThS. Nguyễn Hữu Trung - Trường ĐH Sư Phạm Kỹ Thuật TP.HCM (HCMUTE)</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function changeQty(delta) {
        let input = document.getElementById('buyQty');
        let current = parseInt(input.value) || 1;
        let max = parseInt(input.getAttribute('max')) || 100;
        let next = current + delta;
        if (next >= 1 && next <= max) {
            input.value = next;
        }
    }

    function handleAddToCart() {
        let qty = document.getElementById('buyQty').value || 1;
        window.location.href = '${pageContext.request.contextPath}/cart/add?id=${product.id}&quantity=' + qty;
    }

    function handleBuyNow() {
        let qty = document.getElementById('buyQty').value || 1;
        window.location.href = '${pageContext.request.contextPath}/cart/add?id=${product.id}&quantity=' + qty + '&action=buynow';
    }
</script>
</body>
</html>
