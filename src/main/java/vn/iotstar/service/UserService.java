package vn.iotstar.service;

import vn.iotstar.model.LoginResult;
import vn.iotstar.model.User;

/**
 * Interface Service cho User
 * Business Logic Layer - xử lý toàn bộ logic nghiệp vụ xác thực và quản lý tài khoản
 */
public interface UserService {

    /**
     * Đăng nhập chuẩn bảo mật: hỗ trợ username HOẶC email, băm BCrypt, chống brute force
     */
    LoginResult authenticate(String identifier, String password);

    /**
     * Đăng nhập tương thích ngược phương thức cũ
     */
    User login(String username, String password);

    /**
     * Lấy user theo username
     */
    User get(String username);

    /**
     * Lấy user theo email
     */
    User getByEmail(String email);

    /**
     * Lấy user theo username hoặc email
     */
    User getByUsernameOrEmail(String identifier);

    /**
     * Lấy user theo ID
     */
    User getById(int id);

    /**
     * Đăng ký tài khoản mới:
     * - Mật khẩu được băm bằng BCrypt
     * - Role luôn được cố định là USER (5)
     */
    boolean register(String username, String password, String email, String fullname, String phone);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean checkExistEmail(String email);

    /**
     * Kiểm tra username đã tồn tại
     */
    boolean checkExistUsername(String username);

    /**
     * Kiểm tra phone đã tồn tại
     */
    boolean checkExistPhone(String phone);

    /**
     * Thêm user mới
     */
    void insert(User user);

    /**
     * Đổi mật khẩu cho người dùng đã đăng nhập
     */
    boolean changePassword(int userId, String oldPassword, String newPassword);

    /**
     * Yêu cầu đặt lại mật khẩu: sinh mã xác thực 6 chữ số và lưu hạn 15 phút
     */
    String createPasswordResetToken(String email);

    /**
     * Xác thực và đặt lại mật khẩu mới theo token
     */
    boolean resetPasswordWithToken(String token, String newPassword);

    /**
     * Cập nhật mật khẩu mới theo email (sau khi xác thực OTP thành công)
     */
    boolean changePassword(String email, String newPassword);

    /**
     * Cập nhật thông tin Họ tên và Số điện thoại cho Profile
     */
    boolean updateProfile(int userId, String fullname, String phone);

    /**
     * Cập nhật Email sau khi xác thực OTP thành công
     */
    boolean updateEmail(int userId, String newEmail);

    /**
     * Cập nhật URL ảnh đại diện (Cloudinary hoặc local)
     */
    boolean updateAvatar(int userId, String avatarUrl);

    /**
     * Kiểm tra email đã được người khác sử dụng chưa (loại trừ userId hiện tại)
     */
    boolean checkExistEmailExceptUser(String email, int userId);

    /**
     * Kiểm tra số điện thoại đã được người khác sử dụng chưa (loại trừ userId hiện tại)
     */
    boolean checkExistPhoneExceptUser(String phone, int userId);
}
