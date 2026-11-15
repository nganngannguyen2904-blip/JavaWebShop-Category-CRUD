package vn.iotstar.model;

/**
 * Kết quả xử lý đăng nhập phân loại chi tiết và bảo mật
 */
public class LoginResult {

    public enum Status {
        SUCCESS,
        INVALID_CREDENTIALS, // Sai tài khoản hoặc mật khẩu (không tiết lộ tài khoản có tồn tại hay không)
        ACCOUNT_LOCKED,      // Tài khoản bị quản trị viên khóa (status = 0)
        TOO_MANY_ATTEMPTS    // Bị tạm khóa do đăng nhập sai liên tục >= 5 lần
    }

    private final Status status;
    private final User user;
    private final int remainingLockMinutes;
    private final int failedAttempts;

    public LoginResult(Status status, User user, int remainingLockMinutes, int failedAttempts) {
        this.status = status;
        this.user = user;
        this.remainingLockMinutes = remainingLockMinutes;
        this.failedAttempts = failedAttempts;
    }

    public static LoginResult success(User user) {
        return new LoginResult(Status.SUCCESS, user, 0, 0);
    }

    public static LoginResult invalid(int failedAttempts) {
        return new LoginResult(Status.INVALID_CREDENTIALS, null, 0, failedAttempts);
    }

    public static LoginResult accountLocked() {
        return new LoginResult(Status.ACCOUNT_LOCKED, null, 0, 0);
    }

    public static LoginResult tooManyAttempts(int remainingMinutes) {
        return new LoginResult(Status.TOO_MANY_ATTEMPTS, null, remainingMinutes, 5);
    }

    public Status getStatus() { return status; }
    public User getUser() { return user; }
    public int getRemainingLockMinutes() { return remainingLockMinutes; }
    public int getFailedAttempts() { return failedAttempts; }
}
