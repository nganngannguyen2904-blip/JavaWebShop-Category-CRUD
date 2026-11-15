package vn.iotstar.dao.impl;

import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.UserDao;
import vn.iotstar.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Lớp triển khai UserDao
 * Sử dụng 100% PreparedStatement tham số hóa chống triệt để tấn công SQL Injection
 */
public class UserDaoImpl extends DBConnection implements UserDao {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setUserName(rs.getString("username"));
        user.setFullName(rs.getString("fullname"));
        user.setPassWord(rs.getString("password"));
        user.setAvatar(rs.getString("avatar"));
        user.setRoleid(rs.getInt("roleid"));
        user.setPhone(rs.getString("phone"));

        // Các trường mở rộng trạng thái & bảo mật
        try {
            user.setStatus(rs.getInt("status"));
        } catch (SQLException ignored) {
            user.setStatus(1);
        }
        try {
            user.setFailedAttempts(rs.getInt("failed_attempts"));
        } catch (SQLException ignored) {
            user.setFailedAttempts(0);
        }
        try {
            user.setLockTime(rs.getTimestamp("lock_time"));
        } catch (SQLException ignored) {
            user.setLockTime(null);
        }
        try {
            user.setResetToken(rs.getString("reset_token"));
        } catch (SQLException ignored) {
            user.setResetToken(null);
        }
        try {
            user.setResetTokenExpiry(rs.getTimestamp("reset_token_expiry"));
        } catch (SQLException ignored) {
            user.setResetTokenExpiry(null);
        }

        user.setCreatedDate(rs.getDate("createddate"));
        return user;
    }

    @Override
    public User get(String username) {
        String sql = "SELECT * FROM [User] WHERE username = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT * FROM [User] WHERE email = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getByUsernameOrEmail(String identifier) {
        String sql = "SELECT * FROM [User] WHERE username = ? OR email = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM [User] WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(User user) {
        String sql = "INSERT INTO [User](email, username, fullname, password, avatar, roleid, phone, status, failed_attempts, createddate) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPassWord());
            ps.setString(5, user.getAvatar() != null ? user.getAvatar() : "avata.png");
            ps.setInt(6, user.getRoleid());
            ps.setString(7, user.getPhone());
            ps.setInt(8, user.getStatus());
            ps.setInt(9, user.getFailedAttempts());
            ps.setDate(10, user.getCreatedDate() != null ? user.getCreatedDate() : new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        String sql = "SELECT 1 FROM [User] WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkExistUsername(String username) {
        String sql = "SELECT 1 FROM [User] WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkExistPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String sql = "SELECT 1 FROM [User] WHERE phone = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE [User] SET email=?, fullname=?, avatar=?, phone=? WHERE id=?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getAvatar());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE [User] SET password = ? WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFailedAttempts(int userId, int failedAttempts, Timestamp lockTime) {
        String sql = "UPDATE [User] SET failed_attempts = ?, lock_time = ? WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, failedAttempts);
            ps.setTimestamp(2, lockTime);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetFailedAttempts(int userId) {
        String sql = "UPDATE [User] SET failed_attempts = 0, lock_time = NULL WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveResetToken(int userId, String token, Timestamp expiry) {
        String sql = "UPDATE [User] SET reset_token = ?, reset_token_expiry = ? WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expiry);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getByResetToken(String token) {
        String sql = "SELECT * FROM [User] WHERE reset_token = ? AND reset_token_expiry > GETDATE()";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clearResetToken(int userId) {
        String sql = "UPDATE [User] SET reset_token = NULL, reset_token_expiry = NULL WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
