package vn.iotstar;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Comparator;

import vn.iotstar.connection.DBConnection;

/**
 * ================================================================================
 * TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)
 * KHOA CÔNG NGHỆ THÔNG TIN - BỘ MÔN CÔNG NGHỆ PHẦN MỀM
 * ĐỒ ÁN CUỐI KỲ: LẬP TRÌNH WEB (WEBPR330479) - GVHD: ThS. NGUYỄN HỮU TRUNG
 * ================================================================================
 * HỆ THỐNG SHOPPING SERVICE MVC (SERVLET - JDBC - 3 TIER)
 * LỚP KHỞI CHẠY CHÍNH (APPLICATION ENTRY POINT) TRONG VISUAL STUDIO CODE
 * ================================================================================
 * Cấu trúc kiến trúc 3 tầng:
 * - Controller:  vn.iotstar.controller (admin, auth, common, web)
 * - Service:     vn.iotstar.service & service.impl
 * - DAO / Repo:  vn.iotstar.dao & dao.impl
 * - Model:       vn.iotstar.model (User, Category, Product, Cart, CartItem, LoginResult)
 * - Connection:  vn.iotstar.connection.DBConnection (SQL Server: Windows Auth & sa)
 * - Security:    vn.iotstar.util.BCrypt, AdminSecurityFilter, CharacterEncodingFilter
 * - Views:       src/main/webapp/views (JSP & JSTL)
 * ================================================================================
 */
public class Application {

    private static Process tomcatProcess = null;

    public static void main(String[] args) {
        printBanner();

        // 1. Kiểm tra kết nối CSDL SQL Server trước khi khởi động
        checkDatabaseConnection();

        // 2. Đăng ký Hook tắt tiến trình Tomcat sạch sẽ khi dừng debug
        Runtime.getRuntime().addShutdownHook(new Thread(Application::stopTomcat));

        try {
            // 3. Xác định thư mục dự án
            File projectRoot = findProjectRoot();
            System.out.println("[*] Thư mục dự án: " + projectRoot.getAbsolutePath());

            // 4. Kiểm tra Java JDK
            String javaHome = detectJavaHome();
            System.out.println("[*] Java JDK: " + javaHome);

            // 5. Kiểm tra Apache Tomcat 11
            File tomcatDir = detectTomcat(projectRoot);
            System.out.println("[*] Apache Tomcat 11: " + tomcatDir.getAbsolutePath());

            // 6. Kiểm tra & giải phóng cổng 8080 và 8005 để tránh xung đột cổng
            System.out.println("[*] Kiểm tra cổng 8080 và 8005 (giải phóng nếu đang bị chiếm dụng)...");
            freePort(8080);
            freePort(8005);

            // 7. Đồng bộ WAR và deploy
            deployApplication(projectRoot, tomcatDir);

            // 8. Cấu hình biến môi trường và thư mục upload
            File uploadDir = new File(projectRoot, "upload");
            if (!uploadDir.exists()) uploadDir.mkdirs();
            String uploadPath = uploadDir.getAbsolutePath().replace('\\', '/');
            String catalinaOpts = "-Dfile.encoding=UTF-8 -Dupload.dir=" + uploadPath;

            // 9. Tự động mở trình duyệt sau 4 giây
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                    String url = "http://localhost:8080/ShoppingServiceMVC/home";
                    System.out.println("\n[URL] Đang mở trình duyệt tại: " + url + "\n");
                    openBrowser(url);
                } catch (Exception ignored) { }
            }).start();

            // 10. Khởi động máy chủ Apache Tomcat
            System.out.println("================================================================================");
            System.out.println(" [THÀNH CÔNG] TẤT CẢ ĐÃ SẴN SÀNG! ĐANG KHỞI ĐỘNG TOMCAT 11 QUA VS CODE...");
            System.out.println(" Trình duyệt sẽ TỰ ĐỘNG MỞ tại: http://localhost:8080/ShoppingServiceMVC/home");
            System.out.println(" Nhấn nút Stop (■) trên thanh công cụ Debug của VS Code để dừng ứng dụng.");
            System.out.println("================================================================================\n");

            runTomcat(tomcatDir, javaHome, catalinaOpts);

        } catch (Exception e) {
            System.err.println("[LỖI NGOẠI LỆ]: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("   TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)");
        System.out.println("   MÔN HỌC: LẬP TRÌNH WEB (WEBPR330479) - GVHD: ThS. NGUYỄN HỮU TRUNG");
        System.out.println("   ĐỒ ÁN CUỐI KỲ: SHOPPING SERVICE MVC - VS CODE JAVA RUNNER");
        System.out.println("================================================================================\n");
    }

    /**
     * Kiểm tra trực tiếp kết nối SQL Server và đếm bản ghi trong cơ sở dữ liệu
     */
    public static void checkDatabaseConnection() {
        System.out.println("[*] Đang kiểm tra kết nối CSDL SQL Server (Database: BanHang)...");
        try {
            DBConnection db = new DBConnection();
            try (Connection conn = db.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("[✓] Kết nối SQL Server THÀNH CÔNG!");
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Users")) {
                        if (rs.next()) {
                            System.out.println("[✓] Xác thực dữ liệu bảng Users: Tìm thấy " + rs.getInt(1) + " tài khoản.");
                        }
                    } catch (Exception ignore) {
                        System.out.println("[✓] Kết nối CSDL mở thành công.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[!] Cảnh báo kết nối CSDL: " + e.getMessage());
            System.err.println("    Vui lòng đảm bảo SQL Server đang chạy và database 'BanHang' đã được khởi tạo.");
        }
        System.out.println();
    }

    private static File findProjectRoot() {
        File current = new File(".").getAbsoluteFile();
        while (current != null) {
            if (new File(current, "run_server.bat").exists() &&
                (new File(current, "src").exists() || new File(current, "ShoppingServiceMVC").exists())) {
                return current;
            }
            current = current.getParentFile();
        }
        return new File(".").getAbsoluteFile();
    }

    private static String detectJavaHome() {
        String envJava = System.getenv("JAVA_HOME");
        if (envJava != null && new File(envJava, "bin/java.exe").exists()) {
            return envJava;
        }

        String[] paths = {
            "C:\\Program Files\\Java\\jdk-21",
            "C:\\Program Files\\Java\\jdk-17",
            "C:\\Program Files\\Java\\jdk-22",
            "C:\\Program Files\\Java\\jdk-23"
        };
        for (String p : paths) {
            if (new File(p, "bin/java.exe").exists()) return p;
        }

        File javaFolder = new File("C:\\Program Files\\Java");
        if (javaFolder.exists() && javaFolder.isDirectory()) {
            File[] files = javaFolder.listFiles(f -> f.getName().startsWith("jdk"));
            if (files != null && files.length > 0) return files[0].getAbsolutePath();
        }

        return System.getProperty("java.home");
    }

    private static File detectTomcat(File projectRoot) {
        String envTomcat = System.getenv("CATALINA_HOME");
        if (envTomcat != null && new File(envTomcat, "bin/catalina.bat").exists()) {
            return new File(envTomcat);
        }

        File projectTomcat = new File(projectRoot, "tomcat");
        if (new File(projectTomcat, "bin/catalina.bat").exists()) {
            return projectTomcat;
        }

        String userHome = System.getProperty("user.home");
        String[] candidates = {
            userHome + "\\Downloads\\apache-tomcat-11.0.27-windows-x64\\apache-tomcat-11.0.27",
            "C:\\Users\\Ngan Dang Yeu\\Downloads\\apache-tomcat-11.0.27-windows-x64\\apache-tomcat-11.0.27",
            "C:\\Program Files\\Apache Software Foundation\\Tomcat 11.0",
            "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1"
        };

        for (String c : candidates) {
            File f = new File(c);
            if (new File(f, "bin/catalina.bat").exists()) return f;
        }

        File downloads = new File(userHome, "Downloads");
        if (downloads.exists()) {
            File[] tomcats = downloads.listFiles(f -> f.getName().toLowerCase().contains("tomcat"));
            if (tomcats != null) {
                for (File t : tomcats) {
                    if (new File(t, "bin/catalina.bat").exists()) return t;
                    File[] sub = t.listFiles(f -> f.getName().toLowerCase().contains("tomcat"));
                    if (sub != null) {
                        for (File s : sub) {
                            if (new File(s, "bin/catalina.bat").exists()) return s;
                        }
                    }
                }
            }
        }

        throw new RuntimeException("Không tìm thấy Apache Tomcat 11! Vui lòng kiểm tra thư mục Tomcat.");
    }

    private static void freePort(int port) {
        try {
            Process p = Runtime.getRuntime().exec("netstat -aon");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String search = ":" + port + " ";
            while ((line = reader.readLine()) != null) {
                if (line.contains(search) && line.contains("LISTENING")) {
                    String[] tokens = line.trim().split("\\s+");
                    if (tokens.length >= 5) {
                        String pid = tokens[tokens.length - 1];
                        if (!"0".equals(pid)) {
                            System.out.println("[*] Phát hiện tiến trình (PID: " + pid + ") đang dùng port " + port + ". Đang giải phóng...");
                            Runtime.getRuntime().exec("taskkill /F /PID " + pid).waitFor();
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception ignored) { }
    }

    private static void deployApplication(File projectRoot, File tomcatDir) throws Exception {
        // Tìm file WAR tại root target hoặc ShoppingServiceMVC target
        File warFile = new File(projectRoot, "target/ShoppingServiceMVC-1.0-SNAPSHOT.war");
        if (!warFile.exists()) {
            warFile = new File(projectRoot, "ShoppingServiceMVC/target/ShoppingServiceMVC-1.0-SNAPSHOT.war");
        }

        if (!warFile.exists()) {
            System.out.println("[!] Chưa có file WAR trong target. Đang thực hiện build Maven package...");
            buildWar(projectRoot);
            warFile = new File(projectRoot, "target/ShoppingServiceMVC-1.0-SNAPSHOT.war");
            if (!warFile.exists()) {
                warFile = new File(projectRoot, "ShoppingServiceMVC/target/ShoppingServiceMVC-1.0-SNAPSHOT.war");
            }
        }

        if (!warFile.exists()) {
            throw new RuntimeException("Không tìm thấy file WAR để deploy sau khi build!");
        }

        // Xóa cache cũ trong Tomcat
        File uncompressedWebapp = new File(tomcatDir, "webapps/ShoppingServiceMVC");
        if (uncompressedWebapp.exists()) deleteDirectory(uncompressedWebapp.toPath());

        File workCache = new File(tomcatDir, "work/Catalina/localhost/ShoppingServiceMVC");
        if (workCache.exists()) deleteDirectory(workCache.toPath());

        // Copy WAR vào webapps
        File warDst = new File(tomcatDir, "webapps/ShoppingServiceMVC.war");
        System.out.println("[*] Đồng bộ file WAR vào Tomcat webapps: " + warDst.getName());
        Files.copy(warFile.toPath(), warDst.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Copy DLL Windows Auth và JDBC JARs
        File libDir = new File(projectRoot, "lib");
        File authDll = new File(libDir, "mssql-jdbc_auth-12.8.1.x64.dll");
        if (authDll.exists()) {
            File targetDll = new File(tomcatDir, "bin/mssql-jdbc_auth-12.8.1.x64.dll");
            Files.copy(authDll.toPath(), targetDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        String[] jars = { "mssql-jdbc-12.8.1.jre11.jar", "jbcrypt-0.4.jar" };
        for (String jar : jars) {
            File srcJar = new File(libDir, jar);
            if (srcJar.exists()) {
                File dstJar = new File(tomcatDir, "lib/" + jar);
                Files.copy(srcJar.toPath(), dstJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void buildWar(File projectRoot) {
        try {
            File buildBat = new File(projectRoot, "scripts/build_project.bat");
            if (buildBat.exists()) {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", buildBat.getAbsolutePath(), "nopause");
                pb.directory(projectRoot);
                pb.inheritIO();
                Process p = pb.start();
                p.waitFor();
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "mvn", "clean", "package");
            pb.directory(projectRoot);
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            System.err.println("[Cảnh báo build]: " + e.getMessage());
        }
    }

    private static void runTomcat(File tomcatDir, String javaHome, String catalinaOpts) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "catalina.bat", "run");
        pb.directory(new File(tomcatDir, "bin"));
        pb.environment().put("JAVA_HOME", javaHome);
        pb.environment().put("JRE_HOME", javaHome);
        pb.environment().put("CATALINA_HOME", tomcatDir.getAbsolutePath());
        pb.environment().put("CATALINA_OPTS", catalinaOpts);
        pb.environment().put("PATH", javaHome + "\\bin;" + System.getenv("PATH"));

        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        tomcatProcess = pb.start();
        tomcatProcess.waitFor();
    }

    private static void stopTomcat() {
        if (tomcatProcess != null && tomcatProcess.isAlive()) {
            try {
                System.out.println("\n[*] Đang dừng máy chủ Apache Tomcat...");
                long pid = tomcatProcess.pid();
                Runtime.getRuntime().exec("taskkill /F /T /PID " + pid).waitFor();
            } catch (Exception ignored) {
            } finally {
                tomcatProcess = null;
            }
        }
    }

    private static void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        } catch (Exception ignored) { }
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
            Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", "", url });
        } catch (Exception e) {
            try {
                Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", "", url });
            } catch (Exception ignored) { }
        }
    }
}
