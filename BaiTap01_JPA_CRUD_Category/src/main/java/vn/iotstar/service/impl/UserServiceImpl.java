package vn.iotstar.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

/**
 * Lớp triển khai IUserService
 * Quản lý logic nghiệp vụ và mã hóa mật khẩu an toàn với BCrypt
 */
public class UserServiceImpl implements IUserService {

    private final IUserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String email, String password) {
        if (email == null || password == null) return null;
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return null;
        }

        // Kiểm tra mật khẩu (hỗ trợ BCrypt và fallback linh hoạt)
        String storedPassword = user.getPassword();
        boolean match = false;
        try {
            if (storedPassword != null && storedPassword.startsWith("$2")) {
                match = BCrypt.checkpw(password, storedPassword);
            } else {
                match = password.equals(storedPassword);
            }
        } catch (Exception e) {
            match = password.equals(storedPassword);
        }

        if (match && user.getStatus() == 1) {
            return user;
        }
        return null;
    }

    @Override
    public void register(User user) {
        if (user == null) return;
        // Chuẩn hóa email
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        // Băm mật khẩu bằng BCrypt độ mạnh 12 vòng muối
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
        }
        userDao.insert(user);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        if (email == null || newPassword == null) return;
        User user = userDao.findByEmail(email.trim());
        if (user != null) {
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            user.setPassword(hashed);
            userDao.update(user);
        }
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        return userDao.findByEmail(email.trim());
    }

    @Override
    public User findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public boolean checkExistEmail(String email) {
        if (email == null) return false;
        return userDao.checkExistEmail(email.trim());
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }
}
