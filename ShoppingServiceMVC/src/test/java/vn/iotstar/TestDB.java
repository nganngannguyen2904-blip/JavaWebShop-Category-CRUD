package vn.iotstar;

import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.CategoryDao;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.CategoryDaoImpl;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.Category;
import vn.iotstar.model.User;

import java.sql.Connection;
import java.util.List;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("=== KIEM TRA KET NOI CSDL BAN HANG ===");
        try {
            DBConnection db = new DBConnection();
            Connection conn = db.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println(">>> KET NOI THANH CONG! <<<");

                // Test CategoryDao
                CategoryDao cateDao = new CategoryDaoImpl();
                List<Category> list = cateDao.getAll();
                System.out.println("So luong danh muc: " + list.size());
                for (Category c : list) {
                    System.out.println(" - [" + c.getId() + "] " + c.getName() + " | Icon: " + c.getIcon());
                }

                // Test UserDao
                UserDao userDao = new UserDaoImpl();
                User u = userDao.get("admin");
                if (u != null) {
                    System.out.println("User admin tim thay: " + u.getFullName() + " | Role: " + u.getRoleid());
                }

                conn.close();
            }
        } catch (Exception e) {
            System.err.println(">>> LOI KET NOI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
