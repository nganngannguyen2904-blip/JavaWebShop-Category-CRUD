package vn.iotstar.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.iotstar.config.EnvConfig;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Dịch vụ gửi email sử dụng Jakarta Mail và Angus Mail Provider
 * Nạp cấu hình tự động từ .env thông qua EnvConfig
 */
public class EmailService {

    private static EmailService instance;

    private EmailService() {}

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    /**
     * Tạo Mail Session kết nối tới Gmail SMTP server
     */
    private Session createMailSession() {
        EnvConfig.validateMailConfig();

        String host = EnvConfig.getMailHost();
        int port = EnvConfig.getMailPort();
        final String username = EnvConfig.getMailUsername();
        final String password = EnvConfig.getMailAppPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.connectiontimeout", "10000"); // 10s
        props.put("mail.smtp.timeout", "10000");

        if (port == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            // Port 587 hoặc mặc định dùng STARTTLS
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Gửi email mã OTP xác minh (Đăng ký hoặc Quên mật khẩu)
     *
     * @param toEmail Email người nhận
     * @param recipientName Tên người nhận
     * @param otp Mã OTP 6 chữ số
     * @param purpose Mục đích ("ĐĂNG KÝ TÀI KHOẢN" hoặc "ĐẶT LẠI MẬT KHẨU")
     * @throws Exception khi gửi thất bại
     */
    public void sendOtpEmail(String toEmail, String recipientName, String otp, String purpose) throws Exception {
        Session session = createMailSession();

        MimeMessage message = new MimeMessage(session);
        String fromEmail = EnvConfig.getMailFrom();
        message.setFrom(new InternetAddress(fromEmail, "Hệ Thống Xác Thực OTP", "UTF-8"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

        String subject = "[MÃ XÁC THỰC OTP] " + purpose + " - " + otp;
        message.setSubject(subject, "UTF-8");

        String htmlContent = buildOtpEmailHtml(recipientName, otp, purpose);
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    /**
     * Tạo nội dung email HTML chuyên nghiệp, hiển thị mã OTP nổi bật
     */
    private String buildOtpEmailHtml(String recipientName, String otp, String purpose) {
        String displayName = (recipientName != null && !recipientName.trim().isEmpty()) ? recipientName.trim() : "Quý khách";
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:0;background-color:#f4f6f9;font-family:Arial,Helvetica,sans-serif;color:#333;'>" +
                "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' border='0' style='background-color:#f4f6f9;padding:40px 0;'>" +
                "  <tr><td align='center'>" +
                "    <table role='presentation' width='560' cellspacing='0' cellpadding='0' border='0' style='background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 6px 20px rgba(0,0,0,0.08);'>" +
                "      <!-- Header -->" +
                "      <tr><td style='background:linear-gradient(135deg, #0d6efd, #0b5ed7);padding:30px;text-align:center;color:#ffffff;'>" +
                "        <h1 style='margin:0;font-size:24px;font-weight:700;letter-spacing:1px;'>XÁC THỰC TÀI KHOẢN</h1>" +
                "        <p style='margin:6px 0 0 0;font-size:14px;opacity:0.9;'>Hệ thống dịch vụ bảo mật OTP</p>" +
                "      </td></tr>" +
                "      <!-- Body -->" +
                "      <tr><td style='padding:35px 30px;'>" +
                "        <p style='font-size:16px;margin:0 0 15px 0;'>Xin chào <strong>" + displayName + "</strong>,</p>" +
                "        <p style='font-size:14px;line-height:1.6;color:#555;margin:0 0 25px 0;'>" +
                "          Hệ thống vừa nhận được yêu cầu <strong>" + purpose + "</strong> cho địa chỉ email này. " +
                "          Vui lòng sử dụng mã xác thực OTP 6 chữ số dưới đây để hoàn tất thao tác:" +
                "        </p>" +
                "        <!-- OTP Box -->" +
                "        <div style='background:#f0f5ff;border:2px dashed #0d6efd;border-radius:10px;padding:20px;text-align:center;margin:0 0 25px 0;'>" +
                "          <span style='font-size:36px;font-weight:800;letter-spacing:8px;color:#0d6efd;font-family:Courier New,monospace;display:inline-block;'>" + otp + "</span>" +
                "        </div>" +
                "        <!-- Notice -->" +
                "        <div style='background:#fff3cd;border-left:4px solid #ffc107;padding:12px 15px;border-radius:4px;margin-bottom:20px;'>" +
                "          <p style='margin:0;font-size:13px;color:#856404;line-height:1.5;'>" +
                "            <strong>Lưu ý quan trọng:</strong><br/>" +
                "            • Mã OTP này có hiệu lực trong vòng <strong>5 phút</strong>.<br/>" +
                "            • Tuyệt đối <strong>không chia sẻ</strong> mã này cho bất kỳ ai, kể cả nhân viên hỗ trợ." +
                "          </p>" +
                "        </div>" +
                "        <p style='font-size:13px;color:#888;margin:0;line-height:1.5;'>" +
                "          Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email hoặc đổi mật khẩu ngay để bảo vệ tài khoản." +
                "        </p>" +
                "      </td></tr>" +
                "      <!-- Footer -->" +
                "      <tr><td style='background:#f8f9fa;border-top:1px solid #e9ecef;padding:20px 30px;text-align:center;font-size:12px;color:#6c757d;'>" +
                "        Đây là email tự động, vui lòng không trả lời thư này.<br/>" +
                "        &copy; " + java.time.Year.now().getValue() + " Hệ thống Shopping Service - Bảo lưu mọi quyền." +
                "      </td></tr>" +
                "    </table>" +
                "  </td></tr>" +
                "</table>" +
                "</body></html>";
    }
}
