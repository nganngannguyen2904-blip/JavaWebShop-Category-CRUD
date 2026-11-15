package database;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class DownloadRemaining {

    private static final Map<String, String> REMAINING = new LinkedHashMap<>();

    static {
        // 46. Pin Sạc Dự Phòng 20000mAh
        REMAINING.put("sp_dt_06.png", "https://images.unsplash.com/photo-1593118247619-e2d6f056869e?w=600&h=600&fit=crop&q=85");
        // 52. Nước Tẩy Trang Dịu Nhẹ
        REMAINING.put("sp_mp_02.png", "https://images.unsplash.com/photo-1601049541289-9b1b7bbbfe19?w=600&h=600&fit=crop&q=85");
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        File[] targetDirs = {
            new File("upload/product"),
            new File("src/main/webapp/upload/product"),
            new File("ShoppingServiceMVC/src/main/webapp/upload/product"),
            new File("src/main/resources/static/images/products")
        };

        for (File dir : targetDirs) {
            if (!dir.exists()) dir.mkdirs();
        }

        for (Map.Entry<String, String> entry : REMAINING.entrySet()) {
            String fileName = entry.getKey();
            String url = entry.getValue();

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(Duration.ofSeconds(15))
                        .build();

                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                System.out.printf("Tải %s: status=%d, size=%d bytes%n", fileName, response.statusCode(), response.body().length);

                if (response.statusCode() == 200 && response.body().length > 5000) {
                    byte[] bytes = response.body();
                    BufferedImage rawImg = ImageIO.read(new ByteArrayInputStream(bytes));
                    if (rawImg != null) {
                        BufferedImage formattedImg = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = formattedImg.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g.drawImage(rawImg, 0, 0, 600, 600, null);
                        g.dispose();

                        for (File dir : targetDirs) {
                            File dest = new File(dir, fileName);
                            ImageIO.write(formattedImg, "png", dest);
                        }
                        System.out.println("  [✓ ĐÃ LƯU THÀNH CÔNG]: " + fileName);
                    }
                }
            } catch (Exception ex) {
                System.err.println("  [!] Lỗi " + fileName + ": " + ex.getMessage());
            }
        }
    }
}
