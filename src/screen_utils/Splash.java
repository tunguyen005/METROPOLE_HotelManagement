package screen_utils;

import javax.swing.*;
import java.awt.*;


public class Splash extends JWindow {
    private JProgressBar progress;
    Splash(){

        setLocationRelativeTo(null);
        setSize(500, 300);
        setVisible(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        ImageIcon logo = new ImageIcon(ClassLoader.getSystemResource("image/metropole_logo.png"));
        Image img = logo.getImage().getScaledInstance(300, 204, Image.SCALE_SMOOTH);
        JLabel lbl1 = new JLabel(new ImageIcon(img));



        progress = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                int width = (int)(getWidth()*(getPercentComplete()));
                g2.fillRect(0, 0, width, getHeight());
            }
        };
        progress.setPreferredSize(new Dimension(500, 6));
        progress.setStringPainted(false);
        progress.setBorder(null);
        progress.setBackground(Color.BLACK);
        progress.setForeground(new Color(0,0,0));

        panel.add(lbl1, BorderLayout.CENTER);
        panel.add(progress, BorderLayout.SOUTH);
        add(panel);

    }
    public void showSplash() {
        setVisible(true);
        for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(30);
                progress.setValue(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose();
    }

    public static void main(String[] args) {
        Splash splashScreen = new Splash();
        splashScreen.showSplash();

        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Main Application");
            mainFrame.setSize(800, 600);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
