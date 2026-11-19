package vn.iotstar.service;

import jakarta.servlet.http.Part;
import vn.iotstar.config.EnvConfig;
import vn.iotstar.util.Constant;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dịch vụ upload và quản lý hình ảnh đại diện qua Cloudinary API
 * Sử dụng giao thức HTTP Multipart/form-data chuẩn RESTful
 * Tự động tính toán chữ ký bảo mật SHA-1 (Signed Upload) và hỗ trợ Unsigned Preset
 * Tự động dự phòng (fallback) lưu ảnh cục bộ nếu môi trường offline không có kết nối internet
 */
public class CloudinaryService {

    private static CloudinaryService instance;

    private CloudinaryService() {}

    public static synchronized CloudinaryService getInstance() {
        if (instance == null) {
            instance = new CloudinaryService();
        }
        return instance;
    }

    /**
     * Upload ảnh đại diện từ Multipart Part lên Cloudinary
     * 
     * @param part Part ảnh được gửi từ form enctype="multipart/form-data"
     * @param username Tên tài khoản để gắn nhãn định danh ảnh
     * @return URL ảnh trực tiếp trên Cloudinary (https://res.cloudinary.com/...) hoặc URL fallback
     * @throws Exception nếu có lỗi đọc dữ liệu tệp tin
     */
    public String uploadAvatar(Part part, String username) throws Exception {
        if (part == null || part.getSize() <= 0) {
            throw new IllegalArgumentException("Tệp tin hình ảnh đại diện không hợp lệ hoặc rỗng!");
        }

        // Lấy tên file gốc và phần mở rộng
        String submittedName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String extension = ".png";
        int dotIndex = submittedName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = submittedName.substring(dotIndex).toLowerCase();
        }

        byte[] fileBytes;
        try (InputStream is = part.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[8192];
            int read;
            while ((read = is.read(temp)) != -1) {
                buffer.write(temp, 0, read);
            }
            fileBytes = buffer.toByteArray();
        }

        // Đọc cấu hình Cloudinary từ file .env qua EnvConfig
        String cloudName = EnvConfig.getCloudinaryCloudName();
        String apiKey = EnvConfig.getCloudinaryApiKey();
        String apiSecret = EnvConfig.getCloudinaryApiSecret();
        String uploadPreset = EnvConfig.getCloudinaryUploadPreset();

        // 1. Thử upload trực tiếp lên Cloudinary qua HTTP Multipart POST
        try {
            String cloudinaryUrl = uploadToCloudinaryApi(fileBytes, submittedName, cloudName, apiKey, apiSecret, uploadPreset);
            if (cloudinaryUrl != null && !cloudinaryUrl.trim().isEmpty()) {
                System.out.println("[CloudinaryService] Upload thành công lên Cloudinary: " + cloudinaryUrl);
                return cloudinaryUrl;
            }
        } catch (Exception e) {
            System.err.println("[CloudinaryService - Cảnh báo] Không thể kết nối tới Cloudinary API (" + e.getMessage() 
                    + "). Kích hoạt cơ chế Fallback lưu trữ cục bộ bảo toàn dữ liệu.");
        }

        // 2. Cơ chế Fallback an toàn: Lưu vào thư mục upload cục bộ của ứng dụng
        return saveAvatarLocally(fileBytes, username, extension);
    }

    /**
     * Gửi request HTTP Multipart form-data trực tiếp tới endpoint Cloudinary
     */
    private String uploadToCloudinaryApi(byte[] fileBytes, String originalFileName,
                                         String cloudName, String apiKey, String apiSecret, String uploadPreset) throws Exception {
        String boundary = "---CloudinaryUploadBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";
        String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";

        URL url = new URL(uploadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(8000); // 8 giây timeout
        conn.setReadTimeout(12000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("User-Agent", "ShoppingServiceMVC-CloudinaryClient/1.0");

        long timestamp = System.currentTimeMillis() / 1000L;
        String folder = "avatars";

        try (OutputStream outputStream = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

            // Field: folder
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"folder\"").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(folder).append(LINE_FEED);
            writer.flush();

            // Field: timestamp
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"timestamp\"").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(String.valueOf(timestamp)).append(LINE_FEED);
            writer.flush();

            // Field: api_key và signature (Signed Upload) hoặc upload_preset
            if (apiKey != null && !apiKey.isEmpty() && apiSecret != null && !apiSecret.isEmpty()) {
                // Tính chữ ký SHA-1 theo định dạng của Cloudinary: parameter alphabetically + api_secret
                String toSign = "folder=" + folder + "&timestamp=" + timestamp + apiSecret;
                String signature = sha1Hex(toSign);

                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"api_key\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(apiKey).append(LINE_FEED);
                writer.flush();

                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"signature\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(signature).append(LINE_FEED);
                writer.flush();
            } else if (uploadPreset != null && !uploadPreset.isEmpty()) {
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"upload_preset\"").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(uploadPreset).append(LINE_FEED);
                writer.flush();
            }

            // Field: file (Binary Stream)
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(originalFileName).append("\"").append(LINE_FEED);
            writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            outputStream.write(fileBytes);
            outputStream.flush();

            writer.append(LINE_FEED);
            writer.flush();

            // Kết thúc multipart
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.flush();
        }

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder responseBuilder = new StringBuilder();
        if (is != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }

        String jsonResponse = responseBuilder.toString();
        if (responseCode >= 200 && responseCode < 300) {
            // Trích xuất secure_url hoặc url từ phản hồi JSON của Cloudinary
            Pattern pattern = Pattern.compile("\"secure_url\"\\s*:\\s*\"(https?:\\\\?/\\\\?[^\"]+)\"");
            Matcher matcher = pattern.matcher(jsonResponse);
            if (matcher.find()) {
                return matcher.group(1).replace("\\/", "/");
            }

            Pattern patternUrl = Pattern.compile("\"url\"\\s*:\\s*\"(https?:\\\\?/\\\\?[^\"]+)\"");
            Matcher matcherUrl = patternUrl.matcher(jsonResponse);
            if (matcherUrl.find()) {
                return matcherUrl.group(1).replace("\\/", "/");
            }
        }

        throw new IOException("Cloudinary phản hồi mã lỗi HTTP " + responseCode + ": " + jsonResponse);
    }

    /**
     * Lưu ảnh đại diện cục bộ khi offline và trả về tên file / URL phục vụ qua DownloadImageController
     */
    private String saveAvatarLocally(byte[] fileBytes, String username, String extension) throws IOException {
        String avatarDir = Constant.UPLOAD_DIR + File.separator + "category";
        File dir = new File(avatarDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String safeUsername = (username != null && !username.trim().isEmpty()) 
                ? username.replaceAll("[^a-zA-Z0-9_]", "_") 
                : "user";
        String fileName = "avatar_" + safeUsername + "_" + System.currentTimeMillis() + extension;
        File destination = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(fileBytes);
        }

        System.out.println("[CloudinaryService - Fallback Local] Đã lưu ảnh đại diện tại: " + destination.getAbsolutePath());
        return fileName;
    }

    /**
     * Tính toán mã băm SHA-1 theo định dạng Hexadecimal chữ thường
     */
    private String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán SHA-1 không khả dụng", e);
        }
    }
}
