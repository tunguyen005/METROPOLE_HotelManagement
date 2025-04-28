package screen_utils;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class ExcelLogo {
    private final int width;
    private final int height;
    private BufferedImage image;

    public ExcelLogo(int width, int height) {
        this.width = width;
        this.height = height;
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Bật khử răng cưa
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền trắng
        g2d.setColor(new Color(16, 124, 65));
        g2d.fillRect(0, 0, width, height);

        double margin = width * 0.1;
        double foldSize = width * 0.2;
        double arcSize = width * 0.05;

        // Hình nền màu xanh (màu chính Excel)
        g2d.setColor(new Color(0, 0, 0));
        Path2D fileShape = new Path2D.Double();
        fileShape.moveTo(margin, margin + arcSize);
        fileShape.quadTo(margin, margin, margin + arcSize, margin); // góc trên trái
        fileShape.lineTo(width - margin - foldSize - arcSize, margin);
        fileShape.quadTo(width - margin - foldSize, margin, width - margin - foldSize, margin + arcSize); // góc trên phải
        fileShape.lineTo(width - margin - foldSize, height - margin - arcSize);
        fileShape.quadTo(width - margin - foldSize, height - margin, width - margin - foldSize - arcSize, height - margin); // góc dưới phải
        fileShape.lineTo(margin + arcSize, height - margin);
        fileShape.quadTo(margin, height - margin, margin, height - margin - arcSize); // góc dưới trái
        fileShape.closePath();
        g2d.fill(fileShape);

        // Phần gập (dùng gradient)
        GradientPaint foldGradient = new GradientPaint(
                (float) (width - margin - foldSize), (float) margin,
                new Color(0,0,0),
                (float) (width - margin), (float) (margin + foldSize),
                new Color(0,0,0)
        );
        g2d.setPaint(foldGradient);
        Path2D foldShape = new Path2D.Double();
        foldShape.moveTo(width - margin, margin);
        foldShape.lineTo(width - margin - foldSize, margin + foldSize);
        foldShape.lineTo(width - margin, margin + foldSize);
        foldShape.closePath();
        g2d.fill(foldShape);

        // Vẽ chữ "X"
        g2d.setColor(Color.WHITE);
        Font font = new Font("Segoe UI", Font.BOLD, (int) (width * 0.4));
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        String xText = "X";
        int textWidth = fm.stringWidth(xText);
        int textHeight = fm.getAscent();

        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - (int)(height * 0.05); // canh giữa theo chiều dọc
        g2d.drawString(xText, x, y);

        g2d.dispose();
    }

    public BufferedImage getImage() {
        return image;
    }
}
