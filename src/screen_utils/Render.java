package screen_utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Render {
    private final BufferedImage image;

    public Render(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage render(int width, int height) {
        // Tạo BufferedImage mới với kích thước yêu cầu
        BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = renderedImage.createGraphics();

        // Tối ưu chất lượng render
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ ảnh lên kích thước mới
        g2d.drawImage(image, 0, 0, width, height, null);

        g2d.dispose();
        return renderedImage;
    }
}
