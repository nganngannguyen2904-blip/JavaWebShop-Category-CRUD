package vn.iotstar.dao;

import vn.iotstar.model.User;

import java.sql.Timestamp;

/**
 * Interface DAO cho User
 * Định nghĩa đầy đủ các phương thức truy xuất dữ liệu an toàn (chống SQL Injection)
 */
public interface UserDao {

    /**
     * Lấy user theo username
     */
    User get(String username);

    /**
     * Lấy user theo email
     */
    User getByEmail(String email);

    /**
     * Lấy user theo username HOẶC email (phục vụ đăng nhập linh hoạt)
     */
    User getByUsernameOrEmail(String identifier);

    /**
     * Lấy user theo ID
     */
    User getById(int id);

    /**
     * Thêm user mới vào database
     */
    void insert(User user);

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean checkExistEmail(String email);

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean checkExistUsername(String username);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     */
    boolean checkExistPhone(String phone);

    /**
     * Cập nhật thông tin cơ bản của user
     */
    void update(User user);

    /**
     * Cập nhật mật khẩu mới (đã hash bằng BCrypt)
     */
    void updatePassword(int userId, String newHashedPassword);

    /**
     * Cập nhật số lần đăng nhập sai và thời gian khóa (Chống brute-force)
     */
    void updateFailedAttempts(int userId, int failedAttempts, Timestamp lockTime);

    /**
     * Đặt lại số lần đăng nhập sai về 0 khi đăng nhập thành công
     */
    void resetFailedAttempts(int userId);

    /**
     * Lưu mã token xác nhận đặt lại mật khẩu và thời hạn hết hạn
     */
    void saveResetToken(int userId, String token, Timestamp expiry);

    /**
     * Tìm user theo mã token đặt lại mật khẩu
     */
    User getByResetToken(String token);

    /**
     * Xóa token đặt lại mật khẩu sau khi sử dụng thành công
     */
    void clearResetToken(int userId);
}
