package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quản lý vòng đời và bảo mật mã OTP cho Shopping Service MVC
 */
public class OtpManager {

    public static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000L; // 5 phút
    public static final long RESEND_COOLDOWN_MS = 60 * 1000L;        // 60 giây
    public static final int MAX_FAILED_ATTEMPTS = 5;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static class OtpSession {
        private final String email;
        private final String hashedOtp;
        private final long createdAt;
        private final long expiresAt;
        private final String purpose;
        private int failedAttempts = 0;
        private long lastResendTime;
        private Object payload;

        public OtpSession(String email, String hashedOtp, String purpose, Object payload) {
            this.email = email;
            this.hashedOtp = hashedOtp;
            this.purpose = purpose;
            this.payload = payload;
            this.createdAt = System.currentTimeMillis();
            this.expiresAt = this.createdAt + OTP_VALID_DURATION_MS;
            this.lastResendTime = this.createdAt;
            this.failedAttempts = 0;
        }

        public String getEmail() { return email; }
        public String getPurpose() { return purpose; }
        public Object getPayload() { return payload; }
        public void setPayload(Object payload) { this.payload = payload; }
        public long getExpiresAt() { return expiresAt; }
        public long getLastResendTime() { return lastResendTime; }
        public void setLastResendTime(long lastResendTime) { this.lastResendTime = lastResendTime; }
        public int getFailedAttempts() { return failedAttempts; }
        public void incrementFailedAttempts() { this.failedAttempts++; }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }

        public boolean isExceededAttempts() {
            return failedAttempts >= MAX_FAILED_ATTEMPTS;
        }
    }

    private static final Map<String, OtpSession> OTP_STORE = new ConcurrentHashMap<>();

    public static String generateOtp() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    public static String hashOtp(String email, String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String combined = (email != null ? email.toLowerCase() : "") + ":" + otp;
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi thuật toán mã hóa SHA-256", e);
        }
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 3) {
            return username.charAt(0) + "***" + domain;
        } else if (username.length() <= 6) {
            return username.substring(0, 2) + "***" + username.substring(username.length() - 1) + domain;
        } else {
            return username.substring(0, 3) + "***" + username.substring(username.length() - 2) + domain;
        }
    }

    public static OtpSession createOtpSession(String email, String rawOtp, String purpose, Object payload) {
        String cleanEmail = email.trim().toLowerCase();
        OTP_STORE.remove(cleanEmail);

        String hashed = hashOtp(cleanEmail, rawOtp);
        OtpSession session = new OtpSession(cleanEmail, hashed, purpose, payload);
        OTP_STORE.put(cleanEmail, session);
        return session;
    }

    public static OtpSession getSession(String email) {
        if (email == null) return null;
        String cleanEmail = email.trim().toLowerCase();
        OtpSession session = OTP_STORE.get(cleanEmail);
        if (session != null && session.isExpired()) {
            OTP_STORE.remove(cleanEmail);
            return null;
        }
        return session;
    }

    public static long getRemainingCooldownSeconds(String email) {
        OtpSession session = getSession(email);
        if (session == null) return 0;
        long elapsed = System.currentTimeMillis() - session.getLastResendTime();
        if (elapsed < RESEND_COOLDOWN_MS) {
            return (long) Math.ceil((RESEND_COOLDOWN_MS - elapsed) / 1000.0);
        }
        return 0;
    }

    public enum VerifyResult {
        SUCCESS,
        EXPIRED,
        INVALID,
        EXCEEDED_ATTEMPTS,
        PURPOSE_MISMATCH,
        NOT_FOUND
    }

    public static synchronized VerifyResult verifyOtp(String email, String inputOtp, String expectedPurpose) {
        if (email == null || inputOtp == null) {
            return VerifyResult.NOT_FOUND;
        }
        String cleanEmail = email.trim().toLowerCase();
        OtpSession session = OTP_STORE.get(cleanEmail);

        if (session == null) {
            return VerifyResult.NOT_FOUND;
        }

        if (session.isExpired()) {
            OTP_STORE.remove(cleanEmail);
            return VerifyResult.EXPIRED;
        }

        if (session.isExceededAttempts()) {
            OTP_STORE.remove(cleanEmail);
            return VerifyResult.EXCEEDED_ATTEMPTS;
        }

        if (expectedPurpose != null && !expectedPurpose.equalsIgnoreCase(session.getPurpose())) {
            return VerifyResult.PURPOSE_MISMATCH;
        }

        String inputHash = hashOtp(cleanEmail, inputOtp.trim());
        if (session.hashedOtp.equals(inputHash)) {
            return VerifyResult.SUCCESS;
        } else {
            session.incrementFailedAttempts();
            if (session.isExceededAttempts()) {
                OTP_STORE.remove(cleanEmail);
                return VerifyResult.EXCEEDED_ATTEMPTS;
            }
            return VerifyResult.INVALID;
        }
    }

    public static synchronized VerifyResult verifyOtp(String email, String inputOtp) {
        return verifyOtp(email, inputOtp, null);
    }

    public static void removeSession(String email) {
        if (email != null) {
            OTP_STORE.remove(email.trim().toLowerCase());
        }
    }
}
