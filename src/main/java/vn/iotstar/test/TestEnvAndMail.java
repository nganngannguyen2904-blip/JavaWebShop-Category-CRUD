package vn.iotstar.test;

import vn.iotstar.config.EnvConfig;
import vn.iotstar.service.EmailService;
import vn.iotstar.util.OtpManager;

/**
 * Kiểm thử xác thực nạp biến môi trường .env và gửi OTP thật qua Gmail SMTP
 */
public class TestEnvAndMail {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       KIỂM THỬ TỰ ĐỘNG NẠP FILE .ENV VÀ KẾT NỐI GMAIL SMTP THẬT");
        System.out.println("================================================================================");

        try {
            // 1. Kiểm tra nạp .env
            System.out.println("\n[1] Kiểm tra khởi tạo EnvConfig:");
            EnvConfig.init();
            EnvConfig.validateMailConfig();
            System.out.println("  -> [THÀNH CÔNG] Nạp file .env hoàn hảo!");
            System.out.println("  -> " + EnvConfig.getSafeConfigSummary());

            // 2. Kiểm tra sender Gmail
            String sender = EnvConfig.getMailUsername();
            System.out.println("\n[2] Kiểm tra Gmail gửi (MAIL_USERNAME): " + sender);
            if (sender == null || !sender.contains("@")) {
                throw new RuntimeException("MAIL_USERNAME không hợp lệ!");
            }

            // 3. Kiểm tra định dạng email người nhận (Gmail và email sinh viên HCMUTE)
            System.out.println("\n[3] Kiểm tra hỗ trợ các đuôi email nhận mã OTP:");
            String[] testEmails = {
                "nganngannguyen2904@gmail.com",
                "21110123@student.hcmute.edu.vn",
                "giaovien@hcmute.edu.vn"
            };
            for (String email : testEmails) {
                boolean valid = email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
                System.out.println("  -> Email: " + email + " | Định dạng hợp lệ: " + valid + " | Mask: " + OtpManager.maskEmail(email));
                if (!valid) throw new RuntimeException("Regex không nhận dạng được email: " + email);
            }

            // 4. Gửi email OTP thật tới hòm thư người dùng
            System.out.println("\n[4] Gửi mã OTP 6 chữ số thật qua Gmail SMTP tới: " + sender);
            String otp = OtpManager.generateOtp();
            System.out.println("  -> Mã OTP sinh ngẫu nhiên: " + otp);

            EmailService.getInstance().sendOtpEmail(
                sender,
                "Nguyễn Ngọc Ngân",
                otp,
                "KIỂM THỬ KẾT NỐI GMAIL SMTP THỰC TẾ"
            );

            System.out.println("  -> [THÀNH CÔNG RỰC RỠ] Đã gửi email OTP thực tế vào Gmail thành công!");
            System.out.println("  -> Hãy kiểm tra hộp thư (inbox / spam) tại: " + sender);

            System.out.println("\n================================================================================");
            System.out.println("                TẤT CẢ KIỂM THỬ ĐÃ ĐẠT CHUẨN 100%");
            System.out.println("================================================================================");

        } catch (Exception e) {
            System.err.println("\n[LỖI] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
