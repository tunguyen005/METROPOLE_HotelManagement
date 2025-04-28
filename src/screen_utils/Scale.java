package screen_utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Scale {
    private final BufferedImage originalImage;

    public Scale(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public BufferedImage scaleTo(int newWidth, int newHeight) {
        // Tạo BufferedImage mới với kích thước yêu cầu
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // Bật các tùy chọn để giữ độ nét
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Scale và vẽ ảnh gốc lên ảnh mới
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

        g2d.dispose();
        return scaledImage;
    }

    public static void main(String[] args) {
        try {
            // Đọc ảnh PNG gốc
            BufferedImage originalImage = ImageIO.read(new File("input.png"));

            // Scale ảnh sang 256x256
            Scale scaler = new Scale(originalImage);
            BufferedImage scaledImage = scaler.scaleTo(256, 256);

            // Render ảnh scaled với chất lượng cao
            Render renderer = new Render(scaledImage);
            BufferedImage renderedImage = renderer.render(256, 256);

            // Lưu ảnh kết quả
            ImageIO.write(scaledImage, "PNG", new File("scaled_output.png"));
            ImageIO.write(renderedImage, "PNG", new File("rendered_output.png"));

            System.out.println("Scale và render ảnh PNG thành công!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}