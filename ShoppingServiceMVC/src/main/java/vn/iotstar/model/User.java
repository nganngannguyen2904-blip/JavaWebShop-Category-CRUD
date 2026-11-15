package vn.iotstar.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model đại diện cho bảng [User] trong database
 * Hỗ trợ đầy đủ phân quyền, trạng thái tài khoản, chống brute-force và quên mật khẩu
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    private int id;
    private String email;
    private String userName;
    private String fullName;
    private String passWord; // Mật khẩu được băm bằng BCrypt
    private String avatar;
    private int roleid;       // 1: Admin, 2: Manager, 5: User
    private String phone;
    private int status;       // 1: Hoạt động, 0: Bị khóa
    private int failedAttempts; // Đếm số lần đăng nhập sai
    private Timestamp lockTime; // Thời điểm bị khóa tạm thời do nhập sai quá 5 lần
    private String resetToken;  // Mã/Token đặt lại mật khẩu khi quên
    private Timestamp resetTokenExpiry; // Thời hạn mã đặt lại (15 phút)
    private Date createdDate;

    // ===================== Constructors =====================

    public User() {
        this.status = 1;
        this.failedAttempts = 0;
    }

    public User(String email, String userName, String fullName, String passWord,
                String avatar, int roleid, String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.status = 1;
        this.failedAttempts = 0;
        this.createdDate = createdDate;
    }

    // ===================== Helper Methods =====================

    /**
     * Kiểm tra tài khoản có đang hoạt động không
     */
    public boolean isActive() {
        return this.status == 1;
    }

    /**
     * Kiểm tra có phải là Admin không
     */
    public boolean isAdmin() {
        return this.roleid == 1;
    }

    /**
     * Kiểm tra có phải Manager không
     */
    public boolean isManager() {
        return this.roleid == 2;
    }

    /**
     * Kiểm tra tài khoản có quyền vào trang quản trị không (Admin hoặc Manager)
     */
    public boolean canAccessAdmin() {
        return this.roleid == 1 || this.roleid == 2;
    }

    /**
     * Kiểm tra tài khoản có bị khóa tạm thời do đăng nhập sai liên tục không
     * Thời gian khóa: 15 phút (900.000 ms)
     */
    public boolean isTemporarilyLocked() {
        if (lockTime == null) {
            return false;
        }
        long lockDurationMs = 15 * 60 * 1000L; // 15 phút
        long timeSinceLock = System.currentTimeMillis() - lockTime.getTime();
        return timeSinceLock < lockDurationMs;
    }

    /**
     * Lấy số phút còn lại bị khóa
     */
    public int getRemainingLockMinutes() {
        if (lockTime == null) return 0;
        long lockDurationMs = 15 * 60 * 1000L;
        long elapsed = System.currentTimeMillis() - lockTime.getTime();
        if (elapsed >= lockDurationMs) return 0;
        return (int) Math.ceil((lockDurationMs - elapsed) / 60000.0);
    }

    // ===================== Getters & Setters =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public int getRoleid() { return roleid; }
    public void setRoleid(int roleid) { this.roleid = roleid; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public Timestamp getLockTime() { return lockTime; }
    public void setLockTime(Timestamp lockTime) { this.lockTime = lockTime; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public Timestamp getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(Timestamp resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', userName='" + userName
                + "', fullName='" + fullName + "', roleid=" + roleid + ", status=" + status + "}";
    }
}
