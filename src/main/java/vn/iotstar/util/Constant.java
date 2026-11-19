package vn.iotstar.util;

import java.io.File;

/**
 * Lớp hằng số toàn cục của ứng dụng
 */
public class Constant {

    // Thư mục upload ảnh (Tự động thích ứng linh hoạt theo đường dẫn hệ thống)
    public static final String UPLOAD_DIR;

    static {
        String uploadPath = System.getProperty("upload.dir");
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            uploadPath = System.getenv("UPLOAD_DIR");
        }
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            File currentWorkspace = new File("d:/LT_Web/BTVN_9-11/upload");
            if (currentWorkspace.exists()) {
                uploadPath = currentWorkspace.getAbsolutePath();
            } else {
                File relParent = new File("../upload");
                if (relParent.exists()) {
                    uploadPath = relParent.getAbsolutePath();
                } else {
                    File relCur = new File("upload");
                    if (relCur.exists()) {
                        uploadPath = relCur.getAbsolutePath();
                    } else {
                        uploadPath = System.getProperty("user.dir") + "/upload";
                    }
                }
            }
        }
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        UPLOAD_DIR = dir.getAbsolutePath().replace('\\', '/');
    }

    // Cookie & Session
    public static final String COOKIE_REMEMBER = "username";
    public static final String SESSION_ACCOUNT = "account";
    public static final String CSRF_TOKEN = "csrf_token";

    // Các đường dẫn view
    public static class Path {
        public static final String LOGIN = "/views/auth/login.jsp";
        public static final String REGISTER = "/views/auth/register.jsp";
        public static final String FORGOT_PASSWORD = "/views/auth/forgot-password.jsp";
        public static final String RESET_PASSWORD = "/views/auth/reset-password.jsp";
        public static final String CHANGE_PASSWORD = "/views/auth/change-password.jsp";
        public static final String HOME = "/views/common/home.jsp";
        public static final String ADMIN_HOME = "/views/admin/home.jsp";
        public static final String ACCESS_DENIED = "/views/common/access-denied.jsp";
        public static final String PROFILE = "/views/web/profile.jsp";

        // Category views
        public static final String CATEGORY_LIST = "/views/admin/category/list-category.jsp";
        public static final String CATEGORY_ADD = "/views/admin/category/add-category.jsp";
        public static final String CATEGORY_EDIT = "/views/admin/category/edit-category.jsp";
    }

    // Role IDs
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_MANAGER = 2;
    public static final int ROLE_USER = 5;
}
