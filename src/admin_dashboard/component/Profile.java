package admin_dashboard.component;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import java.awt.*;
import java.io.InputStream;

public class Profile extends javax.swing.JPanel {

    public Profile() {
        initComponents();
        setOpaque(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    SVGUniverse universe = new SVGUniverse();
                    InputStream svgStream = getClass().getResourceAsStream("/admin_dashboard/icon/logo.svg");
                    if (svgStream == null) {
                        System.err.println("SVG file not found!");
                        return;
                    }
                    SVGDiagram diagram = universe.getDiagram(universe.loadSVG(svgStream, "logo"));
                    Graphics2D g2 = (Graphics2D) g;

                    // Vẽ logo SVG ở vị trí (10,10)
                    int logoX = 5, logoY = 10;
                    g2.translate(logoX, logoY);
                    diagram.render(g2);
                    g2.translate(-logoX, -logoY); // Reset vị trí vẽ

                    // Tính toán vị trí vẽ chữ (cách logo một khoảng)
                    int textX = logoX + 45;  // Dịch sang phải 50px (tùy chỉnh theo kích thước logo)
                    int textY = logoY + 28;  // Căn giữa logo (tùy chỉnh theo font)

                    // Vẽ chữ METROPOLE
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
                    g2.drawString("METROPOLE", textX, textY);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }


    private javax.swing.JLabel jLabel1;

}
