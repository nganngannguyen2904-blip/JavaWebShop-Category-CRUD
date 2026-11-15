import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateImages {
    public static void main(String[] args) throws Exception {
        String dir = "d:/LT_Web/BTVN_9-11/upload/category";
        new File(dir).mkdirs();

        String[][] items = {
            {"default.png", "Danh Mục", "0x2563EB"},
            {"ao-nam.png", "Áo Nam", "0x1D4ED8"},
            {"ao-nu.png", "Áo Nữ", "0xDC2626"},
            {"giay-dep.png", "Giày Dép", "0xD97706"},
            {"dong-ho.png", "Đồng Hồ", "0x7C3AED"},
            {"dien-tu.png", "Điện Tử", "0x059669"},
            {"my-pham.png", "Mỹ Phẩm", "0xDB2777"},
            {"avata.png", "Avatar", "0x4B5563"}
        };

        for (String[] it : items) {
            int width = 200, height = 150;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Nền
            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, width, height);

            // Khung màu
            Color mainColor = Color.decode(it[2]);
            g.setColor(mainColor);
            g.fillRoundRect(15, 15, width - 30, height - 55, 12, 12);

            // Chữ tên
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            int x = (width - fm.stringWidth(it[1])) / 2;
            int y = 15 + ((height - 55) - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(it[1], x, y);

            // Chữ tên file
            g.setColor(new Color(100, 116, 139));
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            fm = g.getFontMetrics();
            x = (width - fm.stringWidth(it[0])) / 2;
            g.drawString(it[0], x, height - 15);

            g.dispose();
            File out = new File(dir, it[0]);
            ImageIO.write(img, "png", out);
            System.out.println("Created: " + out.getAbsolutePath());
        }
    }
}
