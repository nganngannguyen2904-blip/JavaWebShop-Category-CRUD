package vn.iotstar.connection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Lớp kết nối cơ sở dữ liệu SQL Server
 * Hỗ trợ Windows Authentication (integratedSecurity=true) và fallback SQL Server Auth (sa/123456)
 * Tự động nạp thư viện native DLL mssql-jdbc_auth để đảm bảo 100% hoạt động trong Tomcat
 * Database: BanHang
 */
public class DBConnection {

    private static boolean authDllLoaded = false;

    static {
        loadNativeAuthDll();
    }

    private static synchronized void loadNativeAuthDll() {
        if (authDllLoaded) return;
        String[] candidatePaths = {
            "d:/LT_Web/BTVN_9-11/lib/mssql-jdbc_auth-12.8.1.x64.dll",
            "D:\\LT_Web\\BTVN_9-11\\lib\\mssql-jdbc_auth-12.8.1.x64.dll",
            System.getProperty("catalina.home") != null ? System.getProperty("catalina.home") + "/bin/mssql-jdbc_auth-12.8.1.x64.dll" : null,
            System.getProperty("catalina.base") != null ? System.getProperty("catalina.base") + "/bin/mssql-jdbc_auth-12.8.1.x64.dll" : null,
            "lib/mssql-jdbc_auth-12.8.1.x64.dll"
        };

        for (String path : candidatePaths) {
            if (path == null) continue;
            try {
                File file = new File(path);
                if (file.exists()) {
                    System.load(file.getAbsolutePath());
                    authDllLoaded = true;
                    System.out.println("[DBConnection] Đã nạp thành công DLL xác thực Windows Auth: " + file.getAbsolutePath());
                    break;
                }
            } catch (Throwable t) {
                // Đã nạp hoặc lỗi không nghiêm trọng, tiếp tục thử
            }
        }
    }

    private final String serverName = "localhost";
    private final String dbName = "BanHang";
    private final String portNumber = "1433";

    /**
     * Lấy kết nối đến database hỗ trợ Unicode UTF-8
     */
    public Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        loadNativeAuthDll();

        String baseParams = ";encrypt=true;trustServerCertificate=true;databaseName=" 
                + dbName + ";sendStringParametersAsUnicode=true;characterEncoding=UTF-8;";

        // 1. Thử kết nối bằng Windows Authentication
        try {
            String winUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";integratedSecurity=true" + baseParams;
            return DriverManager.getConnection(winUrl);
        } catch (Exception eWin) {
            // 2. Fallback sang tài khoản sa nếu máy chủ yêu cầu SQL Server Auth
            try {
                String saUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";user=sa;password=123456" + baseParams;
                return DriverManager.getConnection(saUrl);
            } catch (Exception eSa) {
                // Thử thêm mật khẩu trống cho sa
                try {
                    String saNoPassUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";user=sa;password=" + baseParams;
                    return DriverManager.getConnection(saNoPassUrl);
                } catch (Exception ignored) {
                }
                System.err.println("[DBConnection LOI KET NOI CSDL BanHang]: " + eWin.getMessage());
                throw eWin;
            }
        }
    }
}
