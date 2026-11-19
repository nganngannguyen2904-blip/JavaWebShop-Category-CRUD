<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Footer Cửa Hàng Thời Trang -->
<footer class="fashion-footer bg-dark text-light pt-5 pb-3 mt-5 border-top border-secondary" style="font-size: 0.9rem;">
    <div class="container">
        <div class="row g-4">
            <div class="col-lg-4 col-md-6">
                <h5 class="fw-bold text-white mb-3">SHOPPING SERVICE FASHION</h5>
                <p class="text-secondary small mb-3" style="line-height: 1.8;">
                    Hệ thống thương mại điện tử mua sắm thời trang hàng đầu dành cho giới trẻ. Phong cách thanh lịch, hiện đại, luôn cập nhật những xu hướng thời trang mới nhất.
                </p>
                <div class="d-flex flex-column gap-2 text-secondary small">
                    <div><i class="fa fa-location-dot text-primary me-2"></i>Trường ĐH Sư Phạm Kỹ Thuật TP.HCM (HCMUTE)</div>
                    <div><i class="fa fa-phone text-primary me-2"></i>Hotline CSKH: 1900 6868 (8:00 - 22:00)</div>
                    <div><i class="fa fa-envelope text-primary me-2"></i>Email: hotro@shoppingservice.vn</div>
                </div>
            </div>

            <div class="col-lg-2 col-md-6 col-6">
                <h6 class="fw-bold text-white mb-3">DANH MỤC HOT</h6>
                <ul class="list-unstyled mb-0">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/products?cateid=1" class="text-secondary text-decoration-none small">Áo sơ mi & Polo</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/products?cateid=2" class="text-secondary text-decoration-none small">Đầm & Váy nữ</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/products?cateid=3" class="text-secondary text-decoration-none small">Giày sneaker nam nữ</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/products?cateid=4" class="text-secondary text-decoration-none small">Đồng hồ cao cấp</a></li>
                    <li><a href="${pageContext.request.contextPath}/video" class="text-secondary text-decoration-none small">Lookbook Video</a></li>
                </ul>
            </div>

            <div class="col-lg-3 col-md-6 col-6">
                <h6 class="fw-bold text-white mb-3">CHÍNH SÁCH BÁN HÀNG</h6>
                <ul class="list-unstyled mb-0">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/home" class="text-secondary text-decoration-none small"><i class="fa fa-rotate-left me-1"></i>Đổi trả miễn phí 30 ngày</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/home" class="text-secondary text-decoration-none small"><i class="fa fa-truck-fast me-1"></i>Giao hàng hỏa tốc 2h</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/home" class="text-secondary text-decoration-none small"><i class="fa fa-ruler-combined me-1"></i>Bảng quy đổi kích cỡ (Size)</a></li>
                    <li><a href="${pageContext.request.contextPath}/profile" class="text-secondary text-decoration-none small"><i class="fa fa-shield-halved me-1"></i>Bảo mật thông tin khách hàng</a></li>
                </ul>
            </div>

            <div class="col-lg-3 col-md-6">
                <h6 class="fw-bold text-white mb-3">KẾT NỐI & THANH TOÁN</h6>
                <p class="text-secondary small mb-3">Đăng ký nhận thông tin khuyến mãi và voucher độc quyền.</p>
                <div class="input-group mb-3">
                    <input type="email" class="form-control form-control-sm rounded-start-pill" placeholder="Email của bạn...">
                    <button class="btn btn-primary btn-sm rounded-end-pill px-3" type="button">Đăng ký</button>
                </div>
                <div class="d-flex gap-2 fs-5 text-secondary">
                    <i class="fa-brands fa-cc-visa text-primary"></i>
                    <i class="fa-brands fa-cc-mastercard text-danger"></i>
                    <i class="fa-solid fa-qrcode text-warning"></i>
                    <i class="fa-solid fa-money-bill-wave text-success"></i>
                </div>
            </div>
        </div>

        <div class="border-top border-secondary mt-4 pt-3 d-flex flex-column flex-md-row justify-content-between align-items-center gap-2 small text-secondary">
            <div>
                &copy; 2026 <strong>Shopping Service MVC</strong> - Môn Lập Trình Web (WEBPR330479) - GVHD: ThS. Nguyễn Hữu Trung.
            </div>
            <div class="d-flex gap-3">
                <a href="#" class="text-secondary"><i class="fa-brands fa-facebook"></i></a>
                <a href="#" class="text-secondary"><i class="fa-brands fa-tiktok"></i></a>
                <a href="#" class="text-secondary"><i class="fa-brands fa-instagram"></i></a>
                <a href="#" class="text-secondary"><i class="fa-brands fa-youtube"></i></a>
            </div>
        </div>
    </div>
</footer>
