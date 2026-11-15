import vn.iotstar.model.LoginResult;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.dao.ProductDao;
import vn.iotstar.dao.impl.ProductDaoImpl;

public class TestLogin {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        String[] users = {"trungnh", "admin", "khachhang", "manager"};

        for (String uName : users) {
            User u = userService.get(uName);
            if (u == null) {
                System.out.println("[-] Khong tim thay user: " + uName);
            } else {
                System.out.println("[+] User: " + uName + " | Fullname: " + u.getFullName() 
                    + " | Role: " + u.getRoleid() + " | Status: " + u.getStatus()
                    + " | PassHash: " + u.getPassWord());

                LoginResult r = userService.authenticate(uName, "123456");
                System.out.println("    -> Ket qua authenticate(\"" + uName + "\", \"123456\"): " + r.getStatus() 
                    + " | Success: " + (r.getStatus() == LoginResult.Status.SUCCESS));
            }
        }

        ProductDao productDao = new ProductDaoImpl();
        int pCount = productDao.findAll().size();
        System.out.println("[*] Tong so san pham trong CSDL: " + pCount);
    }
}
