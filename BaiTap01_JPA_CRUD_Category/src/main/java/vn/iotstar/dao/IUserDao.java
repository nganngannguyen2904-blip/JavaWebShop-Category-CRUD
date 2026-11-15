package vn.iotstar.dao;

import java.util.List;
import vn.iotstar.entity.User;

/**
 * Interface DAO quản lý Entity User qua JPA 3.0 / Hibernate ORM
 */
public interface IUserDao {

    void insert(User user);

    void update(User user);

    void delete(int id) throws Exception;

    User findById(int id);

    User findByEmail(String email);

    boolean checkExistEmail(String email);

    List<User> findAll();
}
