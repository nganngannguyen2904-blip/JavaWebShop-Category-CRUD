package vn.iotstar.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

/**
 * Controller phục vụ file ảnh upload: /image?fname=ten_anh.png
 */
@WebServlet(urlPatterns = "/image")
public class DownloadImageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("fname");

        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "avatar.png";
        }

        // Bảo vệ chống path traversal
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

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                IOUtils.copy(fis, os);
                os.flush();
            }
        } else {
            renderPlaceholder(fileName, resp);
        }
    }

    private File findImageFile(HttpServletRequest req, String fileName) {
        // 1. Tìm trong Constant.DIR
        File f1 = new File(Constant.DIR, fileName);
        if (f1.exists() && f1.isFile()) return f1;

        // 2. Tìm trong thư mục upload webapp
        try {
            String realPath = req.getServletContext().getRealPath("/upload/category/" + fileName);
            if (realPath != null) {
                File f2 = new File(realPath);
                if (f2.exists() && f2.isFile()) return f2;
            }
        } catch (Exception ignored) {
        }

        // 3. Tìm trong thư mục dự án tương đối
        String[] relativePaths = {
            "upload/category/" + fileName,
            "src/main/webapp/upload/category/" + fileName,
            "../upload/category/" + fileName
        };
        for (String p : relativePaths) {
            File f3 = new File(p);
            if (f3.exists() && f3.isFile()) return f3;
        }

        return null;
    }

    private void renderPlaceholder(String fileName, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.setHeader("Cache-Control", "no-cache");

        int w = 240;
        int h = 160;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(240, 244, 248));
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(13, 110, 253));
        g.drawRect(5, 5, w - 10, h - 10);

        g.setColor(new Color(13, 110, 253));
        g.fillOval(w / 2 - 25, 30, 50, 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("JPA", w / 2 - 20, 62);

        g.setColor(new Color(33, 37, 41));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String brand = "Category Image";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(brand, (w - fm.stringWidth(brand)) / 2, 105);

        g.setColor(new Color(108, 117, 125));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String fn = fileName.length() > 20 ? fileName.substring(0, 17) + "..." : fileName;
        FontMetrics fmFn = g.getFontMetrics();
        g.drawString(fn, (w - fmFn.stringWidth(fn)) / 2, 130);

        g.dispose();
        ImageIO.write(img, "png", resp.getOutputStream());
    }
}
