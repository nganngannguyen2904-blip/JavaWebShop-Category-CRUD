package vn.iotstar.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener khởi tạo và kiểm tra toàn diện cấu hình môi trường (.env) khi webapp khởi động
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("================================================================================");
        System.out.println("[AppContextListener] Ứng dụng JPA đang khởi động. Đang kiểm tra cấu hình .env...");
        try {
            EnvConfig.init();
            EnvConfig.validateMailConfig();
            System.out.println("[AppContextListener] [THÀNH CÔNG] Cấu hình Gmail SMTP đã nạp đầy đủ!");
            System.out.println("  -> " + EnvConfig.getSafeConfigSummary());
        } catch (Exception e) {
            System.err.println("[AppContextListener] [LỖI CẤU HÌNH MAIL] " + e.getMessage());
        }
        System.out.println("================================================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
