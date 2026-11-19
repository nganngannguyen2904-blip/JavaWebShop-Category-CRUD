<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ Hàng Của Bạn - Shopping Service MVC</title>
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
        .cart-card {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            border: 1px solid #e9ecef;
            padding: 24px;
        }
        .cart-item-img {
            width: 70px;
            height: 70px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #eef0f2;
        }
        .qty-btn {
            width: 32px;
            height: 32px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 0;
            font-weight: bold;
        }
        .footer {
            background: #1e242b;
            color: #adb5bd;
            padding: 40px 0 20px;
            margin-top: 60px;
        }
    </style>
</head>
<body>

<!-- Breadcrumb -->
<div class="bg-white border-bottom py-2">
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-0 small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Trang chủ</a></li>
                <li class="breadcrumb-item active text-primary fw-semibold" aria-current="page">Giỏ hàng của bạn</li>
            </ol>
        </nav>
    </div>
</div>

<!-- Main Content -->
<div class="container py-4">
    <h3 class="fw-bold mb-4 text-dark"><i class="fa fa-shopping-cart text-primary me-2"></i>Giỏ Hàng Mua Sắm</h3>

    <c:choose>
        <c:when test="${cart != null && !cart.isEmpty()}">
            <div class="row g-4">
                <!-- Bảng danh sách sản phẩm trong giỏ -->
                <div class="col-lg-8">
                    <div class="cart-card">
                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead class="table-light small text-muted">
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Đơn giá</th>
                                        <th style="width: 140px;" class="text-center">Số lượng</th>
                                        <th>Thành tiền</th>
                                        <th style="width: 50px;"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${cart.items}" var="item">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center gap-3">
                                                    <img src="${pageContext.request.contextPath}/image?fname=${item.product.image}" 
                                                         alt="${item.product.name}" class="cart-item-img"
                                                         onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/product/detail?id=${item.product.id}" 
                                                           class="fw-semibold text-dark text-decoration-none line-clamp-2 small">
                                                            ${item.product.name}
                                                        </a>
                                                        <div class="text-muted small">Mã SP: #${item.product.id}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="fw-semibold text-secondary">${item.product.formattedPrice}</td>
                                            <td class="text-center">
                                                <div class="d-inline-flex align-items-center border rounded">
                                                    <a href="${pageContext.request.contextPath}/cart/update?id=${item.product.id}&mode=dec" 
                                                       class="btn btn-sm btn-light qty-btn border-0 text-muted">-</a>
                                                    <span class="px-2 small fw-bold">${item.quantity}</span>
                                                    <a href="${pageContext.request.contextPath}/cart/update?id=${item.product.id}&mode=inc" 
                                                       class="btn btn-sm btn-light qty-btn border-0 text-muted">+</a>
                                                </div>
                                            </td>
                                            <td class="fw-bold text-danger">${item.formattedSubTotal}</td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/cart/remove?id=${item.product.id}" 
                                                   class="btn btn-sm btn-outline-danger" title="Xóa khỏi giỏ"
                                                   onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?');">
                                                    <i class="fa fa-trash-alt"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm">
                                <i class="fa fa-arrow-left me-1"></i>Tiếp tục mua hàng
                            </a>
                            <a href="${pageContext.request.contextPath}/cart/clear" class="btn btn-outline-danger btn-sm"
                               onclick="return confirm('Bạn có chắc muốn xóa sạch toàn bộ giỏ hàng?');">
                                <i class="fa fa-trash-alt me-1"></i>Xóa sạch giỏ hàng
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Tóm tắt thanh toán -->
                <div class="col-lg-4">
                    <div class="cart-card">
                        <h5 class="fw-bold mb-3 pb-2 border-bottom">Tóm Tắt Đơn Hàng</h5>
                        
                        <div class="d-flex justify-content-between mb-2 small text-muted">
                            <span>Tổng số sản phẩm:</span>
                            <span class="fw-bold text-dark">${cart.totalQuantity} món</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2 small text-muted">
                            <span>Tạm tính tiền hàng:</span>
                            <span class="fw-bold text-dark">${cart.formattedTotalAmount}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-3 small text-muted">
                            <span>Phí giao hàng:</span>
                            <span class="text-success fw-bold">MIỄN PHÍ</span>
                        </div>

                        <hr>

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <span class="fw-bold">Tổng thanh toán:</span>
                            <span class="fs-4 fw-bold text-danger">${cart.formattedTotalAmount}</span>
                        </div>

                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-danger btn-lg fw-bold py-3">
                                <i class="fa fa-credit-card me-2"></i>Tiến Hành Đặt Hàng & Thanh Toán
                            </a>
                        </div>

                        <div class="mt-4 pt-3 border-top text-muted small">
                            <div class="mb-2"><i class="fa fa-shield-alt text-success me-2"></i>Bảo mật thanh toán 100%</div>
                            <div class="mb-2"><i class="fa fa-truck text-primary me-2"></i>Giao hàng nhanh nội thành 2h</div>
                            <div><i class="fa fa-sync-alt text-warning me-2"></i>Hỗ trợ đổi trả trong vòng 7 ngày</div>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <!-- Giỏ hàng trống -->
            <div class="cart-card text-center py-5">
                <i class="fa fa-cart-arrow-down text-muted fa-4x mb-3"></i>
                <h4 class="fw-bold text-secondary mb-2">Giỏ hàng của bạn đang trống!</h4>
                <p class="text-muted small mb-4">Hãy lựa chọn các sản phẩm ưng ý để thêm vào giỏ hàng ngay hôm nay.</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4">
                    <i class="fa fa-shopping-bag me-2"></i>Khám phá sản phẩm ngay
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
