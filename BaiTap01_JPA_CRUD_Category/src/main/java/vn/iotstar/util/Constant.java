package vn.iotstar.util;

import java.io.File;

/**
 * Lớp định nghĩa hằng số toàn cục cho hệ thống
 */
public class Constant {

    // Thư mục lưu trữ hình ảnh upload
    public static final String UPLOAD_DIR;

    static {
        String path = System.getProperty("upload.dir");
        if (path == null || path.trim().isEmpty()) {
            path = System.getenv("UPLOAD_DIR");
        }
        if (path == null || path.trim().isEmpty()) {
            File localUpload = new File("d:/LT_Web/BTVN_9-11/BaiTap01_JPA_CRUD_Category/upload/category");
            if (localUpload.exists()) {
                path = localUpload.getAbsolutePath();
            } else {
                path = System.getProperty("user.dir") + "/upload/category";
            }
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        UPLOAD_DIR = dir.getAbsolutePath().replace('\\', '/');
    }

    public static final String DIR = UPLOAD_DIR;
}
