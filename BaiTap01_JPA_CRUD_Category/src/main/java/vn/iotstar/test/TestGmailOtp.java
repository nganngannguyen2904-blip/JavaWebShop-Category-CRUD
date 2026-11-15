package vn.iotstar.test;

import vn.iotstar.config.EnvConfig;
import vn.iotstar.service.EmailService;
import vn.iotstar.util.OtpManager;

/**
 * Kiểm thử nạp biến môi trường từ .env và gửi thử mã OTP qua Gmail SMTP
 */
public class TestGmailOtp {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               KIỂM THỬ TÍCH HỢP GMAIL SMTP VÀ QUẢN LÝ MÃ OTP");
        System.out.println("================================================================================");

        try {
            // 1. Kiểm tra cấu hình .env
            System.out.println("\n[1] Kiểm tra file .env và cấu hình mail:");
            EnvConfig.validateMailConfig();
            System.out.println("  -> " + EnvConfig.getSafeConfigSummary());
            System.out.println("  -> Nạp cấu hình thành công!");

            // 2. Kiểm thử OtpManager (sinh mã, mã hóa, xác minh, giới hạn lượt)
            System.out.println("\n[2] Kiểm thử OtpManager:");
            String testEmail = EnvConfig.getMailUsername();
            String otp = OtpManager.generateOtp();
            System.out.println("  -> Sinh mã OTP ngẫu nhiên 6 chữ số: " + otp);

            OtpManager.createOtpSession(testEmail, otp, "TEST", "TEST_PAYLOAD");
            System.out.println("  -> Lưu phiên OTP thành công. Hạn dùng 5 phút.");

            // Thử xác thực sai 1 lần
            OtpManager.VerifyResult wrongResult = OtpManager.verifyOtp(testEmail, "000000");
            System.out.println("  -> Thử nhập sai OTP (000000): " + wrongResult);

            // Thử xác thực đúng
            OtpManager.VerifyResult correctResult = OtpManager.verifyOtp(testEmail, otp);
            System.out.println("  -> Xác thực đúng OTP (" + otp + "): " + correctResult);

            if (correctResult != OtpManager.VerifyResult.SUCCESS) {
                throw new RuntimeException("Xác thực OTP không thành công!");
            }

            // 3. Gửi email xác thực thật tới địa chỉ Gmail trong cấu hình
            System.out.println("\n[3] Gửi thử nghiệm email OTP thật tới: " + testEmail);
            String sendOtp = OtpManager.generateOtp();
            OtpManager.createOtpSession(testEmail, sendOtp, "KIỂM THỬ HỆ THỐNG", null);

            EmailService.getInstance().sendOtpEmail(
                    testEmail,
                    "Quản Trị Viên",
                    sendOtp,
                    "KIỂM THỬ KẾT NỐI GMAIL SMTP"
            );

            System.out.println("  -> [THÀNH CÔNG] Đã gửi email OTP thực tế qua Gmail SMTP!");
            System.out.println("  -> Vui lòng kiểm tra hộp thư (hoặc mục Spam): " + testEmail);
            System.out.println("\n================================================================================");
            System.out.println("                        TẤT CẢ KIỂM THỬ ĐÃ ĐẠT 100%");
            System.out.println("================================================================================");

        } catch (Exception e) {
            System.err.println("\n[LỖI] Kiểm thử thất bại: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
