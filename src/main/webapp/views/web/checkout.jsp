<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Hàng & Thanh Toán - Shopping Service MVC</title>
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
        .checkout-box {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            border: 1px solid #e9ecef;
            padding: 24px;
        }
        .payment-option {
            border: 1px solid #ced4da;
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 12px;
            cursor: pointer;
            transition: all 0.2s;
        }
        .payment-option:hover, .payment-option.active {
            border-color: #0d6efd;
            background-color: #f0f7ff;
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
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">Giỏ hàng</a></li>
                <li class="breadcrumb-item active text-primary fw-semibold" aria-current="page">Xác nhận đặt hàng & Thanh toán</li>
            </ol>
        </nav>
    </div>
</div>

<!-- Main Content -->
<div class="container py-4">
    <h3 class="fw-bold mb-4 text-dark"><i class="fa fa-credit-card text-primary me-2"></i>Tiến Hành Đặt Hàng & Thanh Toán</h3>

    <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
        <div class="row g-4">
            <!-- Cột trái: Thông tin nhận hàng & Thanh toán -->
            <div class="col-lg-7">
                <div class="checkout-box mb-4">
                    <h5 class="fw-bold mb-3 pb-2 border-bottom">
                        <i class="fa fa-map-marker-alt text-danger me-2"></i>1. Thông Tin Nhận Hàng
                    </h5>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Họ và tên người nhận <span class="text-danger">*</span></label>
                            <input type="text" name="fullName" class="form-control" 
                                   value="${defaultName != null ? defaultName : ''}" placeholder="Nhập họ và tên..." required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" name="phone" class="form-control" 
                                   value="${defaultPhone != null ? defaultPhone : ''}" placeholder="Nhập số điện thoại..." required>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Email nhận thông báo đơn hàng</label>
                            <input type="email" name="email" class="form-control" 
                                   value="${defaultEmail != null ? defaultEmail : ''}" placeholder="example@gmail.com">
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Địa chỉ giao hàng chi tiết <span class="text-danger">*</span></label>
                            <textarea name="address" class="form-control" rows="2" 
                                      placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố..." required>Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh</textarea>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Ghi chú cho shipper (tùy chọn)</label>
                            <input type="text" name="note" class="form-control" placeholder="Ví dụ: Giao giờ hành chính, gọi trước khi giao...">
                        </div>
                    </div>
                </div>

                <!-- Phương thức thanh toán -->
                <div class="checkout-box">
                    <h5 class="fw-bold mb-3 pb-2 border-bottom">
                        <i class="fa fa-wallet text-success me-2"></i>2. Phương Thức Thanh Toán
                    </h5>

                    <div class="payment-option d-flex align-items-center">
                        <input class="form-check-input me-3" type="radio" name="paymentMethod" id="payCOD" value="COD" checked>
                        <label class="form-check-label w-100 cursor-pointer" for="payCOD">
                            <div class="fw-bold text-dark"><i class="fa fa-hand-holding-usd text-success me-2"></i>Thanh toán khi nhận hàng (COD)</div>
                            <div class="small text-muted">Nhận hàng, kiểm tra sản phẩm rồi mới thanh toán tiền mặt cho shipper</div>
                        </label>
                    </div>

                    <div class="payment-option d-flex align-items-center">
                        <input class="form-check-input me-3" type="radio" name="paymentMethod" id="payBank" value="BANK">
                        <label class="form-check-label w-100 cursor-pointer" for="payBank">
                            <div class="fw-bold text-dark"><i class="fa fa-qrcode text-primary me-2"></i>Chuyển khoản Ngân hàng (Mã QR Siêu Tốc)</div>
                            <div class="small text-muted">Quét mã QR VietQR từ mọi ứng dụng ngân hàng và ví điện tử</div>
                        </label>
                    </div>
                </div>
            </div>

            <!-- Cột phải: Đơn hàng tóm tắt -->
            <div class="col-lg-5">
                <div class="checkout-box sticky-top" style="top: 90px;">
                    <h5 class="fw-bold mb-3 pb-2 border-bottom">Đơn Hàng Của Bạn (${cart.totalQuantity} sản phẩm)</h5>

                    <div class="overflow-auto mb-3" style="max-height: 280px;">
                        <c:forEach items="${cart.items}" var="item">
                            <div class="d-flex align-items-center justify-content-between py-2 border-bottom">
                                <div class="d-flex align-items-center gap-2">
                                    <img src="${pageContext.request.contextPath}/image?fname=${item.product.image}" 
                                         alt="${item.product.name}" 
                                         style="width: 38px; height: 38px; object-fit: cover; border-radius: 6px; border: 1px solid #e9ecef;"
                                         onerror="this.src='${pageContext.request.contextPath}/image?fname=default.png';">
                                    <span class="badge bg-secondary rounded-pill">${item.quantity}x</span>
                                    <span class="small fw-semibold text-dark text-truncate" style="max-width: 170px;">${item.product.name}</span>
                                </div>
                                <span class="small fw-bold text-danger">${item.formattedSubTotal}</span>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="d-flex justify-content-between mb-2 small text-muted">
                        <span>Tạm tính tiền hàng:</span>
                        <span class="fw-bold text-dark">${cart.formattedTotalAmount}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-3 small text-muted">
                        <span>Phí vận chuyển toàn quốc:</span>
                        <span class="text-success fw-bold">0 đ (Miễn phí)</span>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <span class="fw-bold">Tổng thanh toán:</span>
                        <span class="fs-4 fw-bold text-danger">${cart.formattedTotalAmount}</span>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-danger btn-lg fw-bold py-3 shadow">
                            <i class="fa fa-check-circle me-2"></i>Xác Nhận Đặt Hàng Ngay
                        </button>
                    </div>

                    <div class="text-center mt-3 small text-muted">
                        <i class="fa fa-lock text-success me-1"></i>Thông tin đơn hàng được mã hóa bảo mật 100%
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
</body>
</html>
