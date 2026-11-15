package vn.iotstar.server;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

/**
 * [HCMUTE] Shopping Service MVC - Pure Java Server Runner
 * Khởi chạy Apache Tomcat 11 trực tiếp từ VS Code (F5 / Run) hoặc terminal Java.
 * Không phụ thuộc vào C# hay Visual Studio màu tím.
 */
public class TomcatServerRunner {

    private static Process tomcatProcess = null;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   TRUONG DAI HOC SU PHAM KY THUAT TP. HO CHI MINH (HCMUTE)");
        System.out.println("   MON HOC: LAP TRINH WEB (WEBPR330479) - GVHD: ThS. NGUYEN HUU TRUNG");
        System.out.println("   DO AN CUOI KY: SHOPPING SERVICE MVC (VS CODE JAVA RUNNER)");
        System.out.println("================================================================================\n");

        // Đăng ký hook tắt tiến trình Tomcat khi dừng debug hoặc thoát ứng dụng
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopTomcat();
        }));

        try {
            // 1. Xác định thư mục gốc dự án
            File projectRoot = findProjectRoot();
            System.out.println("[*] Thu muc du an: " + projectRoot.getAbsolutePath());

            // 2. Kiểm tra Java JDK
            String javaHome = detectJavaHome();
            System.out.println("[*] Java JDK: " + javaHome);

            // 3. Kiểm tra Apache Tomcat 11
            File tomcatDir = detectTomcat(projectRoot);
            System.out.println("[*] Apache Tomcat 11: " + tomcatDir.getAbsolutePath());

            // 4. Giải phóng cổng 8080 và 8005 để tránh xung đột cổng
            System.out.println("[*] Kiem tra va giai phong port 8080 va 8005 neu co tien trinh chay ngam...");
            freePort(8080);
            freePort(8005);

            // 5. Kiểm tra file WAR, build nếu chưa có
            File warSrc = new File(projectRoot, "ShoppingServiceMVC/target/ShoppingServiceMVC-1.0-SNAPSHOT.war");
            File warDst = new File(tomcatDir, "webapps/ShoppingServiceMVC.war");

            if (!warSrc.exists()) {
                System.out.println("[!] Chua tim thay file WAR trong target. Dang tu dong goi Maven package...");
                buildWar(projectRoot);
            }

            if (!warSrc.exists()) {
                System.err.println("[LOI] Khong tim thay file WAR tai: " + warSrc.getAbsolutePath());
                System.exit(1);
            }

            // 6. Xóa cache uncompress và work cache cũ trong Tomcat
            File uncompressedWebapp = new File(tomcatDir, "webapps/ShoppingServiceMVC");
            if (uncompressedWebapp.exists()) {
                deleteDirectory(uncompressedWebapp.toPath());
            }

            File workCache = new File(tomcatDir, "work/Catalina/localhost/ShoppingServiceMVC");
            if (workCache.exists()) {
                deleteDirectory(workCache.toPath());
            }

            // Sao chép WAR mới nhất vào Tomcat webapps
            System.out.println("[*] Dong bo file WAR vao Tomcat webapps...");
            Files.copy(warSrc.toPath(), warDst.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 7. Sao chép DLL xác thực Windows Auth và JDBC JARs vào Tomcat
            File libDir = new File(projectRoot, "lib");
            File authDll = new File(libDir, "mssql-jdbc_auth-12.8.1.x64.dll");
            if (authDll.exists()) {
                File targetDll = new File(tomcatDir, "bin/mssql-jdbc_auth-12.8.1.x64.dll");
                Files.copy(authDll.toPath(), targetDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            String[] jarNames = { "mssql-jdbc-12.8.1.jre11.jar", "jbcrypt-0.4.jar" };
            for (String jar : jarNames) {
                File srcJar = new File(libDir, jar);
                if (srcJar.exists()) {
                    File dstJar = new File(tomcatDir, "lib/" + jar);
                    Files.copy(srcJar.toPath(), dstJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 7.1 Sao chép file .env vào Tomcat bin để nạp cấu hình môi trường Gmail SMTP
            File envSrc = new File(projectRoot, ".env");
            if (envSrc.exists()) {
                Files.copy(envSrc.toPath(), new File(tomcatDir, "bin/.env").toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(envSrc.toPath(), new File(tomcatDir, ".env").toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 8. Cấu hình CATALINA_OPTS
            String uploadPath = new File(projectRoot, "upload").getAbsolutePath().replace('\\', '/');
            String projectPath = projectRoot.getAbsolutePath().replace('\\', '/');
            String catalinaOpts = "-Dfile.encoding=UTF-8 -Dupload.dir=" + uploadPath + " -Dproject.root=" + projectPath;

            // 9. Tự động mở trình duyệt sau 4 giây
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                    String url = "http://localhost:8080/ShoppingServiceMVC/home";
                    System.out.println("\n[URL] Trinh duyet dang tu dong mo tai: " + url + "\n");
                    openBrowser(url);
                } catch (Exception ignored) { }
            }).start();

            // 10. Khởi động Tomcat 11
            System.out.println("================================================================================");
            System.out.println(" [THANH CONG] TAT CA DA SAN SANG! DANG KHOI DONG TOMCAT 11 QUA VS CODE...");
            System.out.println(" Trinh duyet se TU DONG MO tai: http://localhost:8080/ShoppingServiceMVC/home");
            System.out.println(" Nhap Ctrl+C hoac Stop trong VS Code de dung may chu.");
            System.out.println("================================================================================\n");

            runTomcat(tomcatDir, javaHome, catalinaOpts);

        } catch (Exception ex) {
            System.err.println("[LOI NGOAI LE]: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static File findProjectRoot() {
        File current = new File(".").getAbsoluteFile();
        while (current != null) {
            if (new File(current, "ShoppingServiceMVC").exists() &&
                new File(current, "run_server.bat").exists()) {
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
            if (new File(p, "bin/java.exe").exists()) {
                return p;
            }
        }

        File javaFolder = new File("C:\\Program Files\\Java");
        if (javaFolder.exists() && javaFolder.isDirectory()) {
            File[] files = javaFolder.listFiles(f -> f.getName().startsWith("jdk"));
            if (files != null && files.length > 0) {
                return files[0].getAbsolutePath();
            }
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
            if (new File(f, "bin/catalina.bat").exists()) {
                return f;
            }
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

        throw new RuntimeException("Khong tim thay Apache Tomcat 11! Vui long kiem tra thu muc Tomcat.");
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
                            System.out.println("[*] Phat hien tien trinh (PID: " + pid + ") chiem port " + port + ". Dang giai phong...");
                            Runtime.getRuntime().exec("taskkill /F /PID " + pid).waitFor();
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception ignored) { }
    }

    private static void buildWar(File projectRoot) {
        try {
            File buildBat = new File(projectRoot, "scripts/build_project.bat");
            if (buildBat.exists()) {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", buildBat.getAbsolutePath(), "nopause");
                pb.directory(new File(projectRoot, "ShoppingServiceMVC"));
                pb.inheritIO();
                Process p = pb.start();
                p.waitFor();
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "mvn", "clean", "package");
            pb.directory(new File(projectRoot, "ShoppingServiceMVC"));
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
                System.out.println("\n[*] Dang tat may chu Apache Tomcat...");
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
