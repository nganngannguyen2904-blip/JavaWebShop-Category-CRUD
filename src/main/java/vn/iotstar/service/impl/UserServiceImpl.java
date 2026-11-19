package vn.iotstar.service.impl;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.LoginResult;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.BCrypt;
import vn.iotstar.util.Constant;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Lớp triển khai UserService
 * Thực thi nghiệp vụ xác thực, bảo mật BCrypt, kiểm soát chống brute-force và quên mật khẩu
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public LoginResult authenticate(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.isEmpty()) {
            return LoginResult.invalid(0);
        }

        User user = userDao.getByUsernameOrEmail(identifier.trim());

        // Nếu tài khoản không tồn tại trong hệ thống
        if (user == null) {
            // Thực hiện băm giả định để đồng đều thời gian phản hồi (chống Timing Attack)
            BCrypt.checkpw("dummy_password", "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012");
            return LoginResult.invalid(0);
        }

        // Kiểm tra tài khoản có bị quản trị viên khóa vĩnh viễn không (status = 0)
        if (!user.isActive()) {
            return LoginResult.accountLocked();
        }

        // Kiểm tra tài khoản có đang trong thời gian tạm khóa (15 phút) do đăng nhập sai liên tục
        if (user.isTemporarilyLocked()) {
            return LoginResult.tooManyAttempts(user.getRemainingLockMinutes());
        }

        // Kiểm tra mật khẩu (hỗ trợ hash BCrypt và cơ chế tự động nâng cấp mật khẩu cũ dạng plaintext)
        boolean isPasswordMatch = false;
        String storedHash = user.getPassWord();

        if (storedHash != null) {
            if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$")) {
                isPasswordMatch = BCrypt.checkpw(password, storedHash);
            } else if (password.equals(storedHash)) {
                // Tương thích ngược: nếu tài khoản cũ chưa băm, đối chiếu đúng và tự động nâng cấp lên BCrypt
                isPasswordMatch = true;
                String upgradedHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
                userDao.updatePassword(user.getId(), upgradedHash);
                user.setPassWord(upgradedHash);
            }
        }

        if (isPasswordMatch) {
            // Đăng nhập thành công -> Xóa bộ đếm đăng nhập sai
            if (user.getFailedAttempts() > 0 || user.getLockTime() != null) {
                userDao.resetFailedAttempts(user.getId());
                user.setFailedAttempts(0);
                user.setLockTime(null);
            }
            return LoginResult.success(user);
        } else {
            // Đăng nhập thất bại -> Tăng số lần sai
            int newFailedAttempts = user.getFailedAttempts() + 1;
            Timestamp lockTime = null;

            if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
                lockTime = new Timestamp(System.currentTimeMillis());
                userDao.updateFailedAttempts(user.getId(), newFailedAttempts, lockTime);
                return LoginResult.tooManyAttempts(15);
            } else {
                userDao.updateFailedAttempts(user.getId(), newFailedAttempts, null);
                return LoginResult.invalid(newFailedAttempts);
            }
        }
    }

    @Override
    public User login(String username, String password) {
        LoginResult result = authenticate(username, password);
        return result.getStatus() == LoginResult.Status.SUCCESS ? result.getUser() : null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public User getByUsernameOrEmail(String identifier) {
        return userDao.getByUsernameOrEmail(identifier);
    }

    @Override
    public User getById(int id) {
        return userDao.getById(id);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        // Kiểm tra ràng buộc duy nhất (Unique)
        if (userDao.checkExistUsername(username) || userDao.checkExistEmail(email)) {
            return false;
        }

        // Băm mật khẩu bằng BCrypt với Salt ngẫu nhiên
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        long millis = System.currentTimeMillis();
        Date date = new Date(millis);

        // BẢO MẬT: Bắt buộc gán vai trò mặc định là USER (5), không nhận role từ bên ngoài
        User newUser = new User();
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setUserName(username.trim());
        newUser.setFullName(fullname.trim());
        newUser.setPassWord(hashedPassword);
        newUser.setAvatar("avata.png");
        newUser.setRoleid(Constant.ROLE_USER); // Luôn là Role 5 (Khách hàng)
        newUser.setPhone(phone != null ? phone.trim() : "");
        newUser.setStatus(1); // 1: Đang hoạt động
        newUser.setFailedAttempts(0);
        newUser.setCreatedDate(date);

        userDao.insert(newUser);
        return true;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDao.getById(userId);
        if (user == null || oldPassword == null || newPassword == null) {
            return false;
        }

        boolean oldPassMatches = false;
        String storedHash = user.getPassWord();
        if (storedHash != null) {
            if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$")) {
                oldPassMatches = BCrypt.checkpw(oldPassword, storedHash);
            } else if (oldPassword.equals(storedHash)) {
                oldPassMatches = true;
            }
        }

        if (!oldPassMatches) {
            return false;
        }

        // Băm mật khẩu mới bằng BCrypt và cập nhật database
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));
        userDao.updatePassword(userId, newHash);
        return true;
    }

    @Override
    public String createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        User user = userDao.getByEmail(email.trim().toLowerCase());
        if (user == null) {
            return null;
        }

        // Sinh mã xác thực ngẫu nhiên 6 chữ số an toàn
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        String token = String.valueOf(code);

        // Hiệu lực trong 15 phút
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000L);
        userDao.saveResetToken(user.getId(), token, expiry);

        return token;
    }

    @Override
    public boolean resetPasswordWithToken(String token, String newPassword) {
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.length() < 6) {
            return false;
        }

        User user = userDao.getByResetToken(token.trim());
        if (user == null) {
            return false;
        }

        // Băm mật khẩu mới bằng BCrypt
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));
        userDao.updatePassword(user.getId(), newHash);
        userDao.clearResetToken(user.getId());
        return true;
    }

    @Override
    public boolean changePassword(String email, String newPassword) {
        if (email == null || newPassword == null || newPassword.length() < 6) {
            return false;
        }
        User user = userDao.getByEmail(email.trim().toLowerCase());
        if (user == null) {
            return false;
        }
        String newHash = org.mindrot.jbcrypt.BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt(10));
        userDao.updatePassword(user.getId(), newHash);
        userDao.clearResetToken(user.getId());
        return true;
    }

    @Override
    public boolean updateProfile(int userId, String fullname, String phone) {
        if (userId <= 0 || fullname == null || fullname.trim().isEmpty()) {
            return false;
        }
        userDao.updateProfile(userId, fullname.trim(), phone != null ? phone.trim() : "");
        return true;
    }

    @Override
    public boolean updateEmail(int userId, String newEmail) {
        if (userId <= 0 || newEmail == null || newEmail.trim().isEmpty()) {
            return false;
        }
        userDao.updateEmail(userId, newEmail.trim().toLowerCase());
        return true;
    }

    @Override
    public boolean updateAvatar(int userId, String avatarUrl) {
        if (userId <= 0 || avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return false;
        }
        userDao.updateAvatar(userId, avatarUrl.trim());
        return true;
    }

    @Override
    public boolean checkExistEmailExceptUser(String email, int userId) {
        return userDao.checkExistEmailExceptUser(email, userId);
    }

    @Override
    public boolean checkExistPhoneExceptUser(String phone, int userId) {
        return userDao.checkExistPhoneExceptUser(phone, userId);
    }
}
