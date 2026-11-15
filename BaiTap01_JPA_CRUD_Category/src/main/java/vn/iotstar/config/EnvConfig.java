package vn.iotstar.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp nạp và quản lý các biến môi trường từ file .env
 * Hỗ trợ đa dạng môi trường chạy (Tomcat Web Container, VS Code Runner, Terminal, Unit Test)
 * Tự động nạp từ Classpath (.env trong resources/WEB-INF/classes), thư mục dự án và System Properties
 * Tuyệt đối không in mật khẩu App Password ra log hoặc giao diện.
 */
public class EnvConfig {

    private static final Map<String, String> ENV_CACHE = new HashMap<>();
    private static boolean initialized = false;
    private static String loadedSource = "Chưa nạp";

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        // 1. Thử nạp từ Classpath (độ tin cậy cao nhất trong Web Server Tomcat / WAR)
        boolean loadedFromClasspath = loadFromClasspath();

        // 2. Thử nạp từ các thư mục vật lý phổ biến
        List<String> searchPaths = new ArrayList<>();
        
        // Thư mục chỉ định qua System Property
        String projectRootProp = System.getProperty("project.root");
        if (projectRootProp != null && !projectRootProp.trim().isEmpty()) {
            searchPaths.add(projectRootProp.trim());
        }

        // Thư mục gốc đồ án trên máy
        searchPaths.add("D:/LT_Web/BTVN_9-11");
        searchPaths.add("D:\\LT_Web\\BTVN_9-11");
        searchPaths.add("D:/LT_Web/BTVN_9-11/BaiTap01_JPA_CRUD_Category");

        // Thư mục làm việc hiện tại
        searchPaths.add(System.getProperty("user.dir", "."));
        searchPaths.add(".");
        searchPaths.add("..");
        searchPaths.add("../..");

        // Thư mục Tomcat catalina.base / catalina.home
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            searchPaths.add(catalinaBase + "/bin");
            searchPaths.add(catalinaBase);
            searchPaths.add(catalinaBase + "/webapps/ShoppingServiceMVC");
            searchPaths.add(catalinaBase + "/webapps/ShoppingServiceMVC/WEB-INF/classes");
            searchPaths.add(catalinaBase + "/webapps/BaiTap01_JPA_CRUD_Category");
            searchPaths.add(catalinaBase + "/webapps/BaiTap01_JPA_CRUD_Category/WEB-INF/classes");
        }

        for (String path : searchPaths) {
            try {
                File dir = new File(path);
                File envFile = new File(dir, ".env");
                if (envFile.exists() && envFile.isFile() && envFile.canRead()) {
                    boolean success = parseEnvFile(envFile);
                    if (success && (loadedSource.equals("Chưa nạp") || !loadedFromClasspath)) {
                        loadedSource = envFile.getAbsolutePath();
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // 3. Fallback lấy thêm từ System Properties & System Environment
        String[] knownKeys = {"MAIL_HOST", "MAIL_PORT", "MAIL_USERNAME", "MAIL_APP_PASSWORD", "MAIL_FROM"};
        for (String k : knownKeys) {
            if (!ENV_CACHE.containsKey(k)) {
                String val = System.getProperty(k);
                if (val != null && !val.trim().isEmpty()) {
                    ENV_CACHE.put(k, val.trim());
                } else {
                    val = System.getenv(k);
                    if (val != null && !val.trim().isEmpty()) {
                        ENV_CACHE.put(k, val.trim());
                    }
                }
            }
        }

        initialized = true;

        // In log thông báo khởi động an toàn (không lộ mật khẩu)
        System.out.println("================================================================================");
        System.out.println("[EnvConfig] Nạp cấu hình môi trường (.env): " + loadedSource);
        System.out.println("  -> " + getSafeConfigSummary());
        System.out.println("================================================================================");
    }

    private static boolean loadFromClasspath() {
        String[] resources = {"/.env", ".env", "META-INF/.env"};
        for (String res : resources) {
            try (InputStream is = EnvConfig.class.getResourceAsStream(res)) {
                if (is != null) {
                    parseInputStream(is);
                    loadedSource = "Classpath (" + res + ")";
                    return true;
                }
            } catch (Exception ignored) {
            }

            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(res.startsWith("/") ? res.substring(1) : res)) {
                if (is != null) {
                    parseInputStream(is);
                    loadedSource = "ContextClassLoader (" + res + ")";
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private static boolean parseEnvFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void parseInputStream(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        } catch (Exception ignored) {
        }
    }

    private static void parseLine(String line) {
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) return;

        int idx = line.indexOf('=');
        if (idx > 0) {
            String key = line.substring(0, idx).trim();
            String val = line.substring(idx + 1).trim();

            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                if (val.length() >= 2) {
                    val = val.substring(1, val.length() - 1);
                }
            }
            ENV_CACHE.put(key, val);
        }
    }

    public static String get(String key) {
        init();
        String val = ENV_CACHE.get(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = System.getProperty(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = System.getenv(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        return null;
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return val != null ? val : defaultValue;
    }

    public static String getMailHost() {
        return get("MAIL_HOST", "smtp.gmail.com");
    }

    public static int getMailPort() {
        String portStr = get("MAIL_PORT", "587");
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return 587;
        }
    }

    public static String getMailUsername() {
        return get("MAIL_USERNAME");
    }

    public static String getMailAppPassword() {
        String pass = get("MAIL_APP_PASSWORD");
        if (pass != null) {
            return pass.replace(" ", "").trim();
        }
        return null;
    }

    public static String getMailFrom() {
        String from = get("MAIL_FROM");
        if (from == null || from.trim().isEmpty()) {
            return getMailUsername();
        }
        return from;
    }

    public static void validateMailConfig() {
        init();
        List<String> missingKeys = new ArrayList<>();

        if (getMailUsername() == null || getMailUsername().trim().isEmpty()) {
            missingKeys.add("MAIL_USERNAME");
        }
        if (getMailAppPassword() == null || getMailAppPassword().trim().isEmpty()) {
            missingKeys.add("MAIL_APP_PASSWORD");
        }
        if (getMailHost() == null || getMailHost().trim().isEmpty()) {
            missingKeys.add("MAIL_HOST");
        }

        if (!missingKeys.isEmpty()) {
            throw new IllegalStateException("Thiếu cấu hình biến môi trường trong file .env: " 
                    + String.join(", ", missingKeys) 
                    + " (Nguồn nạp hiện tại: " + loadedSource + ")! "
                    + "Vui lòng kiểm tra file .env tại thư mục gốc dự án.");
        }
    }

    public static String getSafeConfigSummary() {
        return String.format("MailConfig[Host=%s, Port=%d, SenderUsername=%s, From=%s, AppPassword=%s]",
                getMailHost(),
                getMailPort(),
                getMailUsername() != null ? getMailUsername() : "CHƯA CÓ",
                getMailFrom() != null ? getMailFrom() : "CHƯA CÓ",
                getMailAppPassword() != null ? "****** (Đã nạp " + getMailAppPassword().length() + " ký tự)" : "CHƯA CÓ");
    }
}
