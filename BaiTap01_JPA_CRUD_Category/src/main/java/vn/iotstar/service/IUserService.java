package vn.iotstar.service;

import java.util.List;
import vn.iotstar.entity.User;

/**
 * Interface Service quản lý nghiệp vụ người dùng, đăng ký, đăng nhập và bảo mật mật khẩu
 */
public interface IUserService {

    User login(String email, String password);

    void register(User user);

    void updatePassword(String email, String newPassword);

    User findByEmail(String email);

    User findById(int id);

    void update(User user);

    boolean checkExistEmail(String email);

    List<User> findAll();
}
