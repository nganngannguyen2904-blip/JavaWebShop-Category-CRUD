<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Hàng Thành Công - Shopping Service MVC</title>
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
        .success-card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 6px 24px rgba(0,0,0,0.06);
            border: 1px solid #e9ecef;
            padding: 40px 30px;
            max-width: 680px;
            margin: 40px auto;
        }
        .order-info-box {
            background: #f8fafc;
            border-radius: 10px;
            padding: 20px;
            border: 1px solid #edf2f7;
            text-align: left;
            margin: 24px 0;
        }
    </style>
</head>
<body>

<!-- Topbar Navigation -->
<jsp:include page="/views/common/topbar.jsp" />

<div class="container py-4">
    <div class="success-card text-center">
        <div class="mb-3">
            <i class="fa fa-check-circle text-success" style="font-size: 72px;"></i>
        </div>

        <h2 class="fw-bold text-dark mb-2">ĐẶT HÀNG THÀNH CÔNG!</h2>
        <p class="text-muted small mb-3">Cảm ơn bạn đã mua sắm tại <strong>Shopping Service MVC</strong>. Đơn hàng của bạn đã được tiếp nhận và đang được xử lý.</p>

        <div class="badge bg-success bg-opacity-10 text-success fs-6 px-3 py-2 mb-3">
            Mã đơn hàng: <strong>${orderId}</strong>
        </div>

        <!-- Thông tin chi tiết đơn hàng -->
        <div class="order-info-box">
            <h6 class="fw-bold text-primary mb-3"><i class="fa fa-file-invoice me-2"></i>Thông Tin Đơn Hàng</h6>
            
            <div class="row g-2 small">
                <div class="col-sm-4 text-muted">Thời gian đặt:</div>
                <div class="col-sm-8 fw-semibold">${orderDate}</div>

                <div class="col-sm-4 text-muted">Người nhận hàng:</div>
                <div class="col-sm-8 fw-semibold">${fullName} - ${phone}</div>

                <c:if test="${email != null && !email.isEmpty()}">
                    <div class="col-sm-4 text-muted">Email:</div>
                    <div class="col-sm-8">${email}</div>
                </c:if>

                <div class="col-sm-4 text-muted">Địa chỉ nhận hàng:</div>
                <div class="col-sm-8">${address}</div>

                <div class="col-sm-4 text-muted">Phương thức TT:</div>
                <div class="col-sm-8 fw-semibold text-primary">${paymentMethod}</div>

                <c:if test="${note != null && !note.isEmpty()}">
                    <div class="col-sm-4 text-muted">Ghi chú:</div>
                    <div class="col-sm-8 text-secondary"><em>"${note}"</em></div>
                </c:if>

                <div class="col-12"><hr class="my-2"></div>

                <div class="col-sm-4 text-muted fs-6">Tổng thanh toán:</div>
                <div class="col-sm-8 fs-5 fw-bold text-danger">${totalAmount} (${totalItems} sản phẩm)</div>
            </div>
        </div>

        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary px-4 fw-semibold">
                <i class="fa fa-home me-2"></i>Về trang chủ
            </a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary px-4 fw-semibold">
                <i class="fa fa-shopping-bag me-2"></i>Tiếp tục mua hàng
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
