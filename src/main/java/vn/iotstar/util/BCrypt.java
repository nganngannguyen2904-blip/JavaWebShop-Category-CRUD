package vn.iotstar.util;

/**
 * Lớp tiện ích băm và xác thực mật khẩu chuẩn an toàn BCrypt (OpenBSD Blowfish)
 * Sử dụng thư viện jBCrypt chính thức (org.mindrot:jbcrypt:0.4)
 * Mật khẩu luôn được băm với Salt ngẫu nhiên chống Rainbow Table và Brute Force
 */
public class BCrypt {

    /**
     * Sinh chuỗi Salt ngẫu nhiên với độ phức tạp cost
     * @param logRounds Chi phí băm (mặc định 10 - 12)
     */
    public static String gensalt(int logRounds) {
        return org.mindrot.jbcrypt.BCrypt.gensalt(logRounds);
    }

    /**
     * Sinh chuỗi Salt ngẫu nhiên mặc định (cost = 10)
     */
    public static String gensalt() {
        return org.mindrot.jbcrypt.BCrypt.gensalt(10);
    }

    /**
     * Băm mật khẩu dạng văn bản thuần với Salt đã sinh
     */
    public static String hashpw(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Mật khẩu và Salt không được để trống");
        }
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, salt);
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập vào so với chuỗi hash BCrypt lưu trong database
     * @param plaintext Mật khẩu thô (VD: "123456")
     * @param hashed Chuỗi mã hóa BCrypt trong CSDL (bắt đầu bằng $2a$)
     * @return true nếu mật khẩu chính xác, false nếu sai
     */
    public static boolean checkpw(String plaintext, String hashed) {
        if (plaintext == null || hashed == null || !hashed.startsWith("$2a$")) {
            return false;
        }
        try {
            return org.mindrot.jbcrypt.BCrypt.checkpw(plaintext, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
