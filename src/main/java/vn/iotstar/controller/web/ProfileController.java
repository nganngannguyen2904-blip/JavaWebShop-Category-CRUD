package vn.iotstar.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.service.CloudinaryService;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.OtpManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controller quản lý Hồ Sơ Cá Nhân (User Profile)
 * URL: /profile hoặc /user/profile
 * Hỗ trợ đầy đủ các tính năng theo yêu cầu đồ án:
 * 1. Hiển thị thông tin tài khoản hiện tại
 * 2. Cập nhật Họ tên (Fullname) và Số điện thoại (Phone)
 * 3. Cập nhật Email mới thông qua xác thực OTP gửi đến email mới
 * 4. Upload ảnh đại diện lên Cloudinary bằng Multipart và lưu URL vào database
 * 5. Đổi mật khẩu tài khoản an toàn
 * 6. Kiểm soát quyền truy cập: người dùng chỉ được chỉnh sửa thông tin của chính mình
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/profile", "/user/profile" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,       // 1MB
    maxFileSize = 1024 * 1024 * 10,        // 10MB
    maxRequestSize = 1024 * 1024 * 30      // 30MB
)
public class ProfileController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();
    private final CloudinaryService cloudinaryService = CloudinaryService.getInstance();
    private final EmailService emailService = EmailService.getInstance();

    // Regex kiểm tra số điện thoại Việt Nam (10 chữ số bắt đầu bằng 03, 05, 07, 08, 09)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0[35789])[0-9]{8}$");
    // Regex kiểm tra địa chỉ Email chuẩn quốc tế RFC 5322 đơn giản
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Kiểm tra xác thực phiên đăng nhập
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        User sessionUser = (User) session.getAttribute(Constant.SESSION_ACCOUNT);

        // 2. Tải thông tin tài khoản tươi mới nhất từ database (tránh cache cũ)
        User freshUser = userService.getById(sessionUser.getId());
        if (freshUser == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        // Đồng bộ lại phiên session
        session.setAttribute(Constant.SESSION_ACCOUNT, freshUser);
        req.setAttribute("user", freshUser);

        req.getRequestDispatcher(Constant.Path.PROFILE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 1. Kiểm tra quyền truy cập bảo mật
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?alert=login_required");
            return;
        }

        User sessionUser = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        int userId = sessionUser.getId();

        String action = req.getParameter("action");
        if (action == null) {
            action = "update_info";
        }

        switch (action) {
            case "update_info":
                handleUpdateInfo(req, resp, userId);
                break;

            case "send_email_otp":
                handleSendEmailOtp(req, resp, userId, sessionUser);
                break;

            case "verify_email_otp":
                handleVerifyEmailOtp(req, resp, userId);
                break;

            case "upload_avatar":
                handleUploadAvatar(req, resp, userId, sessionUser);
                break;

            case "change_password":
                handleChangePassword(req, resp, userId);
                break;

            default:
                doGet(req, resp);
                break;
        }
    }

    /**
     * Xử lý cập nhật Họ và tên, Số điện thoại (Validation Client & Server)
     */
    private void handleUpdateInfo(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");

        Map<String, String> errors = new HashMap<>();

        // Kiểm tra Họ và tên
        if (fullname == null || fullname.trim().isEmpty()) {
            errors.put("fullname", "Họ và tên không được để trống!");
        } else if (fullname.trim().length() < 2 || fullname.trim().length() > 100) {
            errors.put("fullname", "Họ và tên phải có độ dài từ 2 đến 100 ký tự!");
        }

        // Kiểm tra Số điện thoại
        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Số điện thoại không được để trống!");
        } else if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            errors.put("phone", "Số điện thoại không đúng định dạng Việt Nam (10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09)!");
        } else if (userService.checkExistPhoneExceptUser(phone.trim(), userId)) {
            errors.put("phone", "Số điện thoại này đã được sử dụng bởi một tài khoản khác!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", "Vui lòng kiểm tra lại thông tin vừa nhập!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "info");
            doGet(req, resp);
            return;
        }

        // Cập nhật vào CSDL
        boolean success = userService.updateProfile(userId, fullname.trim(), phone.trim());
        if (success) {
            req.setAttribute("alert", "Cập nhật thông tin cá nhân thành công!");
            req.setAttribute("alertType", "success");
        } else {
            req.setAttribute("alert", "Cập nhật thất bại. Vui lòng thử lại sau!");
            req.setAttribute("alertType", "danger");
        }
        req.setAttribute("tab", "info");
        doGet(req, resp);
    }

    /**
     * Gửi mã OTP xác thực tới Email mới
     */
    private void handleSendEmailOtp(HttpServletRequest req, HttpServletResponse resp, int userId, User currentUser)
            throws ServletException, IOException {
        String newEmail = req.getParameter("newEmail");

        Map<String, String> errors = new HashMap<>();

        if (newEmail == null || newEmail.trim().isEmpty()) {
            errors.put("newEmail", "Địa chỉ email mới không được để trống!");
        } else {
            String cleanEmail = newEmail.trim().toLowerCase();
            if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
                errors.put("newEmail", "Địa chỉ email không đúng định dạng!");
            } else if (currentUser.getEmail() != null && cleanEmail.equalsIgnoreCase(currentUser.getEmail())) {
                errors.put("newEmail", "Địa chỉ email mới phải khác với địa chỉ email hiện tại!");
            } else if (userService.checkExistEmailExceptUser(cleanEmail, userId)) {
                errors.put("newEmail", "Địa chỉ email này đã được sử dụng bởi một tài khoản khác trong hệ thống!");
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", "Email mới không hợp lệ!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "email");
            doGet(req, resp);
            return;
        }

        String targetEmail = newEmail.trim().toLowerCase();

        // Kiểm tra thời gian hồi chiêu (Cooldown 60 giây)
        long cooldown = OtpManager.getRemainingCooldownSeconds(targetEmail);
        if (cooldown > 0) {
            req.setAttribute("alert", "Vui lòng đợi " + cooldown + " giây trước khi yêu cầu gửi lại mã OTP mới!");
            req.setAttribute("alertType", "warning");
            req.setAttribute("tab", "email");
            req.setAttribute("otpPending", true);
            req.setAttribute("pendingEmail", targetEmail);
            doGet(req, resp);
            return;
        }

        // Sinh mã OTP 6 số ngẫu nhiên
        String rawOtp = OtpManager.generateOtp();

        // Tạo phiên xác thực trong OtpManager có hạn 5 phút
        OtpManager.createOtpSession(targetEmail, rawOtp, "CHANGE_EMAIL", userId);

        // Gửi email OTP tới chính địa chỉ email mới
        boolean mailSent = false;
        try {
            emailService.sendOtpEmail(targetEmail, currentUser.getFullName(), rawOtp, "Xác minh thay đổi địa chỉ Email");
            mailSent = true;
            System.out.println("[ProfileController] Đã gửi mã OTP thay đổi email tới: " + targetEmail);
        } catch (Exception e) {
            System.err.println("[ProfileController - Cảnh báo gửi Mail] " + e.getMessage() 
                    + " -> Mã OTP phục vụ kiểm thử là: " + rawOtp);
            // Ghi nhận mã OTP ra log để nhà phát triển kiểm thử ngay cả khi máy không có mạng ngoài
        }

        if (mailSent) {
            req.setAttribute("alert", "Mã xác thực OTP đã được gửi đến email mới: <strong>" 
                    + OtpManager.maskEmail(targetEmail) + "</strong>. Vui lòng kiểm tra hộp thư (kể cả mục Spam) để lấy mã.");
            req.setAttribute("alertType", "info");
        } else {
            req.setAttribute("alert", "Hệ thống đã tạo mã OTP xác minh cho <strong>" 
                    + targetEmail + "</strong> (Kiểm tra console/log máy chủ nếu đang ở môi trường cục bộ). Mã có hiệu lực trong 5 phút.");
            req.setAttribute("alertType", "warning");
        }

        req.setAttribute("otpPending", true);
        req.setAttribute("pendingEmail", targetEmail);
        req.setAttribute("tab", "email");
        doGet(req, resp);
    }

    /**
     * Xác minh mã OTP và cập nhật Email mới vào database
     */
    private void handleVerifyEmailOtp(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        String pendingEmail = req.getParameter("pendingEmail");
        String otpCode = req.getParameter("otpCode");

        Map<String, String> errors = new HashMap<>();

        if (pendingEmail == null || pendingEmail.trim().isEmpty()) {
            errors.put("otpCode", "Thiếu thông tin địa chỉ email xác thực!");
        }
        if (otpCode == null || otpCode.trim().isEmpty()) {
            errors.put("otpCode", "Vui lòng nhập mã OTP 6 chữ số!");
        } else if (!otpCode.trim().matches("^[0-9]{6}$")) {
            errors.put("otpCode", "Mã OTP phải gồm chính xác 6 chữ số!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", "Mã xác thực không hợp lệ!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("otpPending", true);
            req.setAttribute("pendingEmail", pendingEmail);
            req.setAttribute("tab", "email");
            doGet(req, resp);
            return;
        }

        String targetEmail = pendingEmail.trim().toLowerCase();

        // Kiểm tra mã OTP với OtpManager
        OtpManager.VerifyResult result = OtpManager.verifyOtp(targetEmail, otpCode.trim(), "CHANGE_EMAIL");

        switch (result) {
            case SUCCESS:
                // Cập nhật CSDL
                boolean updated = userService.updateEmail(userId, targetEmail);
                if (updated) {
                    OtpManager.removeSession(targetEmail);
                    req.setAttribute("alert", "Chúc mừng! Bạn đã cập nhật địa chỉ email mới thành công: <strong>" + targetEmail + "</strong>");
                    req.setAttribute("alertType", "success");
                    req.setAttribute("otpPending", false);
                } else {
                    req.setAttribute("alert", "Lỗi cập nhật cơ sở dữ liệu. Vui lòng thử lại!");
                    req.setAttribute("alertType", "danger");
                }
                break;

            case EXPIRED:
                req.setAttribute("alert", "Mã OTP đã hết hiệu lực (quá thời hạn 5 phút). Vui lòng yêu cầu gửi lại mã mới!");
                req.setAttribute("alertType", "danger");
                req.setAttribute("otpPending", false);
                break;

            case INVALID:
                OtpManager.OtpSession session = OtpManager.getSession(targetEmail);
                int remainingAttempts = session != null ? (OtpManager.MAX_FAILED_ATTEMPTS - session.getFailedAttempts()) : 0;
                req.setAttribute("alert", "Mã OTP không chính xác! Bạn còn " + remainingAttempts + " lần thử.");
                req.setAttribute("alertType", "danger");
                req.setAttribute("otpPending", true);
                req.setAttribute("pendingEmail", targetEmail);
                break;

            case EXCEEDED_ATTEMPTS:
                req.setAttribute("alert", "Bạn đã nhập sai mã OTP quá 5 lần liên tiếp. Phiên xác thực bị hủy để bảo đảm an toàn!");
                req.setAttribute("alertType", "danger");
                req.setAttribute("otpPending", false);
                break;

            default:
                req.setAttribute("alert", "Phiên xác thực không tồn tại hoặc đã hết hạn. Vui lòng yêu cầu gửi lại mã!");
                req.setAttribute("alertType", "danger");
                req.setAttribute("otpPending", false);
                break;
        }

        req.setAttribute("tab", "email");
        doGet(req, resp);
    }

    /**
     * Upload ảnh đại diện lên Cloudinary bằng Multipart và lưu URL vào database
     */
    private void handleUploadAvatar(HttpServletRequest req, HttpServletResponse resp, int userId, User currentUser)
            throws ServletException, IOException {
        Part avatarPart = null;
        try {
            avatarPart = req.getPart("avatar");
        } catch (Exception e) {
            req.setAttribute("alert", "Tệp tin tải lên vượt quá giới hạn cho phép (tối đa 10MB)!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "avatar");
            doGet(req, resp);
            return;
        }

        if (avatarPart == null || avatarPart.getSize() <= 0) {
            req.setAttribute("alert", "Vui lòng chọn một tệp hình ảnh để tải lên!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "avatar");
            doGet(req, resp);
            return;
        }

        String submittedFileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
        String lowerName = submittedFileName.toLowerCase();

        // Kiểm tra định dạng ảnh cho phép
        if (!lowerName.endsWith(".png") && !lowerName.endsWith(".jpg") 
                && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".webp")) {
            req.setAttribute("alert", "Định dạng tệp không được hỗ trợ! Vui lòng chọn ảnh định dạng PNG, JPG, JPEG hoặc WEBP.");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "avatar");
            doGet(req, resp);
            return;
        }

        try {
            // Upload lên Cloudinary qua dịch vụ CloudinaryService
            String avatarUrl = cloudinaryService.uploadAvatar(avatarPart, currentUser.getUserName());

            // Lưu URL vào CSDL bảng [User]
            boolean updated = userService.updateAvatar(userId, avatarUrl);
            if (updated) {
                // Cập nhật lại session
                currentUser.setAvatar(avatarUrl);
                req.getSession().setAttribute(Constant.SESSION_ACCOUNT, currentUser);

                req.setAttribute("alert", "Cập nhật ảnh đại diện lên Cloudinary thành công!");
                req.setAttribute("alertType", "success");
            } else {
                req.setAttribute("alert", "Lỗi lưu thông tin ảnh đại diện vào cơ sở dữ liệu!");
                req.setAttribute("alertType", "danger");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("alert", "Có lỗi xảy ra khi tải ảnh lên: " + e.getMessage());
            req.setAttribute("alertType", "danger");
        }

        req.setAttribute("tab", "avatar");
        doGet(req, resp);
    }

    /**
     * Đổi mật khẩu tài khoản an toàn
     */
    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        Map<String, String> errors = new HashMap<>();

        if (oldPassword == null || oldPassword.isEmpty()) {
            errors.put("oldPassword", "Vui lòng nhập mật khẩu hiện tại!");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            errors.put("newPassword", "Vui lòng nhập mật khẩu mới!");
        } else if (newPassword.length() < 6) {
            errors.put("newPassword", "Mật khẩu mới phải có tối thiểu 6 ký tự!");
        }
        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            errors.put("confirmPassword", "Mật khẩu xác nhận không trùng khớp!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", "Vui lòng kiểm tra lại thông tin mật khẩu!");
            req.setAttribute("alertType", "danger");
            req.setAttribute("tab", "security");
            doGet(req, resp);
            return;
        }

        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (success) {
            req.setAttribute("alert", "Đổi mật khẩu thành công! Hãy ghi nhớ mật khẩu mới của bạn.");
            req.setAttribute("alertType", "success");
        } else {
            req.setAttribute("alert", "Mật khẩu hiện tại không chính xác!");
            req.setAttribute("alertType", "danger");
        }

        req.setAttribute("tab", "security");
        doGet(req, resp);
    }
}
