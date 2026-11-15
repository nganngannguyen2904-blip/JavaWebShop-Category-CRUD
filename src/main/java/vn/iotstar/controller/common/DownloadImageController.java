package vn.iotstar.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.io.IOUtils;
import vn.iotstar.util.Constant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

/**
 * Controller phục vụ file ảnh đã upload
 * URL: /image?fname=filename.png
 * Tìm kiếm ảnh đa tầng: thư mục upload ngoài, thư mục webapp, tự động sinh ảnh nếu thiếu
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/image")
public class DownloadImageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("fname");

        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "default.png";
        }

        // Bảo vệ path traversal
        fileName = new File(fileName).getName();

        File file = findImageFile(req, fileName);

        if (file != null && file.exists() && file.isFile()) {
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                if (fileName.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "image/jpeg";
                }
            }
            resp.setContentType(contentType);
            resp.setContentLengthLong(file.length());
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                IOUtils.copy(fis, os);
                os.flush();
            }
        } else {
            // Tự động vẽ ảnh placeholder cục bộ chất lượng cao (Không phụ thuộc mạng internet)
            renderFallbackPlaceholder(fileName, resp);
        }
    }

    private File findImageFile(HttpServletRequest req, String fileName) {
        // 1. Thư mục Constant.UPLOAD_DIR
        String[] subDirs = {"/product/", "/category/", "/", ""};
        for (String sub : subDirs) {
            File f = new File(Constant.UPLOAD_DIR + sub + fileName);
            if (f.exists() && f.isFile()) {
                return f;
            }
        }

        // 2. Thư mục static images và resource path
        String[] staticPaths = {
            "upload/product/" + fileName,
            "src/main/webapp/upload/product/" + fileName,
            "src/main/resources/static/images/products/" + fileName,
            "upload/category/" + fileName,
            "src/main/webapp/upload/category/" + fileName
        };
        for (String sp : staticPaths) {
            File f = new File(sp);
            if (f.exists() && f.isFile()) {
                return f;
            }
        }

        // 3. Thư mục webapp thực tế bên trong Tomcat (nếu có)
        try {
            for (String sub : subDirs) {
                String realPath = req.getServletContext().getRealPath("/upload" + sub + fileName);
                if (realPath != null) {
                    File f = new File(realPath);
                    if (f.exists() && f.isFile()) {
                        return f;
                    }
                }
            }
            String staticRealPath = req.getServletContext().getRealPath("/images/products/" + fileName);
            if (staticRealPath != null) {
                File f = new File(staticRealPath);
                if (f.exists() && f.isFile()) {
                    return f;
                }
            }
        } catch (Exception ignored) {
        }

        // 4. Thư mục fallback mặc định
        File def = new File(Constant.UPLOAD_DIR + "/category/default.png");
        if (def.exists() && def.isFile()) {
            return def;
        }

        return null;
    }

    private void renderFallbackPlaceholder(String fileName, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.setHeader("Cache-Control", "no-cache");

        int w = 400;
        int h = 400;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Nền gradient xanh nhẹ
        GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(230, 242, 255));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Viền thẻ
        g2.setColor(new Color(13, 110, 253));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 20, 20);

        // Vòng tròn trung tâm
        g2.setColor(new Color(13, 110, 253));
        g2.fillOval(w / 2 - 50, 120, 100, 100);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 36));
        String txt = "SP";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, (w - fm.stringWidth(txt)) / 2, 185);

        // Nhãn hệ thống
        g2.setColor(new Color(33, 37, 41));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String brand = "Shopping Service MVC";
        FontMetrics fmBrand = g2.getFontMetrics();
        g2.drawString(brand, (w - fmBrand.stringWidth(brand)) / 2, 260);

        // Tên file
        g2.setColor(new Color(108, 117, 125));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fmFile = g2.getFontMetrics();
        g2.drawString(fileName, (w - fmFile.stringWidth(fileName)) / 2, 290);

        g2.dispose();
        ImageIO.write(img, "png", resp.getOutputStream());
    }
}
