package vn.iotstar.config;

import java.io.File;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Lớp cấu hình quản lý EntityManager và EntityManagerFactory cho JPA
 * Đảm bảo EntityManagerFactory được khởi tạo Singleton một lần duy nhất, tránh rò rỉ tài nguyên.
 */
public class JPAConfig {

    private static EntityManagerFactory factory = null;
    private static boolean authDllLoaded = false;

    static {
        loadNativeAuthDll();
    }

    /**
     * Nạp native DLL xác thực Windows Authentication cho SQL Server JDBC Driver
     */
    private static synchronized void loadNativeAuthDll() {
        if (authDllLoaded) return;
        String[] candidatePaths = {
            "lib/mssql-jdbc_auth-12.8.1.x64.dll",
            "./lib/mssql-jdbc_auth-12.8.1.x64.dll",
            "../lib/mssql-jdbc_auth-12.8.1.x64.dll",
            "d:/LT_Web/BTVN_9-11/lib/mssql-jdbc_auth-12.8.1.x64.dll",
            System.getProperty("catalina.home") != null ? System.getProperty("catalina.home") + "/bin/mssql-jdbc_auth-12.8.1.x64.dll" : null
        };

        for (String path : candidatePaths) {
            if (path == null) continue;
            try {
                File file = new File(path);
                if (file.exists()) {
                    System.load(file.getAbsolutePath());
                    authDllLoaded = true;
                    System.out.println("[JPAConfig] Đã nạp thành công native auth DLL: " + file.getAbsolutePath());
                    break;
                }
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Khởi tạo Singleton EntityManagerFactory an toàn đa luồng
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (factory == null || !factory.isOpen()) {
            loadNativeAuthDll();
            System.out.println("[JPAConfig] Đang khởi tạo EntityManagerFactory (Persistence Unit: jpa-hibernate-mysql)...");
            factory = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
            System.out.println("[JPAConfig] Khởi tạo EntityManagerFactory thành công!");
        }
        return factory;
    }

    /**
     * Lấy EntityManager mới phục vụ các thao tác CRUD
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi tắt ứng dụng
     */
    public static synchronized void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
            System.out.println("[JPAConfig] Đã đóng EntityManagerFactory an toàn.");
        }
    }
}
