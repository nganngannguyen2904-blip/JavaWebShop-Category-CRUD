package database;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class DownloadCategoryAndCheckProducts {

    private static final Map<String, String> CATEGORY_MAP = new LinkedHashMap<>();

    static {
        // Ảnh đại diện danh mục thực tế chuẩn phong cách e-commerce thời trang
        CATEGORY_MAP.put("cat_nam.png", "https://images.unsplash.com/photo-1490578474895-699cd4e2cf59?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("cat_nu.png", "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("cat_giay.png", "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("cat_dongho.png", "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("cat_dientu.png", "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("dien-tu.png", "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("cat_mypham.png", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=600&h=450&fit=crop&q=85");

        // Các alias dự phòng
        CATEGORY_MAP.put("ao-nam.png", "https://images.unsplash.com/photo-1490578474895-699cd4e2cf59?w=600&h=450&fit=crop&q=85");
        CATEGORY_MAP.put("ao-nu.png", "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=600&h=450&fit=crop&q=85");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== BẮT ĐẦU TẢI ẢNH ĐẠI DIỆN DANH MỤC THỜI TRANG THỰC TẾ ===");

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        File[] catDirs = {
            new File("upload/category"),
            new File("src/main/webapp/upload/category"),
            new File("ShoppingServiceMVC/src/main/webapp/upload/category"),
            new File("C:/Users/Ngan Dang Yeu/Downloads/apache-tomcat-11.0.27-windows-x64/apache-tomcat-11.0.27/webapps/ShoppingServiceMVC/upload/category")
        };

        for (File dir : catDirs) {
            if (!dir.exists()) dir.mkdirs();
        }

        for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
            String fileName = entry.getKey();
            String url = entry.getValue();

            System.out.println("Đang tải: " + fileName + " <- " + url);
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(Duration.ofSeconds(20))
                        .GET()
                        .build();

                HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
                if (resp.statusCode() == 200 && resp.body().length > 5000) {
                    BufferedImage raw = ImageIO.read(new ByteArrayInputStream(resp.body()));
                    if (raw != null) {
                        BufferedImage converted = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = converted.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.drawImage(raw, 0, 0, 600, 450, null);
                        g.dispose();

                        for (File targetDir : catDirs) {
                            File targetFile = new File(targetDir, fileName);
                            ImageIO.write(converted, "png", targetFile);
                        }
                        System.out.println("  [OK] Đã lưu " + fileName + " (600x450)");
                    }
                } else {
                    System.err.println("  [ERR] Mã HTTP: " + resp.statusCode());
                }
            } catch (Exception e) {
                System.err.println("  [EXC] " + e.getMessage());
            }
        }

        System.out.println("\n=== HOÀN TẤT TẢI ẢNH ĐẠI DIỆN DANH MỤC THỜI TRANG ===");
    }
}
