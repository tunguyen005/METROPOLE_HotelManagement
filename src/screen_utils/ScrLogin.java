package screen_utils;

import admin_dashboard.form.ScrDashboard;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class ScrLogin extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private Preferences prefs;
    private JCheckBox chckbxSave;

    public ScrLogin() {
        prefs = Preferences.userRoot().node(this.getClass().getName());

        JLabel lbl1 = new JLabel("LOG IN");
        lbl1.setForeground(Color.BLACK);
        lbl1.setFont(new Font("Calistoga", Font.BOLD, 30));
        lbl1.setBounds(10, 90, 100, 30);
        add(lbl1);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(40, 140, 100, 30);
        lblUsername.setFont(new Font("Calistoga", Font.BOLD, 16));
        lblUsername.setForeground(Color.BLACK);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(60, 170, 420, 50);
        txtUsername.setFont(new Font("Calistoga", Font.PLAIN, 16));
        txtUsername.setForeground(Color.BLACK);
        txtUsername.setBackground(new Color(205,201,201));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtUsername.setOpaque(false);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(40, 240, 100, 30);
        lblPassword.setFont(new Font("Calistoga", Font.BOLD, 16));
        lblPassword.setForeground(Color.BLACK);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60, 270, 420, 50);
        txtPassword.setFont(new Font("Calistoga", Font.PLAIN, 16));
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setBackground(new Color(205,201,201));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtPassword.setOpaque(false);
        add(txtPassword);

        ImageIcon imageIcon = new ImageIcon(scaleImageWithAspectRatio("src/image/metropole_logo_1.png", 100, 100));
        JLabel lblLogo = new JLabel(imageIcon);
        lblLogo.setBounds(240, -10, 100, 100);
        add(lblLogo);

        ImageIcon imageIcon1 = new ImageIcon(scaleImageWithAspectRatio("src/image/hotel.jpg", 600, 600));
        JLabel lblHotel = new JLabel(imageIcon1);
        lblHotel.setBounds(600, 0, 600, 600);
        add(lblHotel);

        chckbxSave = new JCheckBox("Save your password");
        chckbxSave.setBounds(60, 340, 200, 30);
        chckbxSave.setFont(new Font("Calistoga", Font.BOLD, 14));
        chckbxSave.setForeground(Color.BLACK);
        chckbxSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(chckbxSave);

        btnLogin = new JButton("LOG IN");
        btnLogin.setBackground(new Color(0,0,0));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Calistoga", Font.BOLD, 20));
        btnLogin.setBorder(null);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(60, 400, 420, 50);
        btnLogin.addActionListener(this);
        add(btnLogin);


        btnCancel = new JButton("<");
        btnCancel.setBackground(new Color(0,0,0));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Calistoga", Font.BOLD, 14));
        btnCancel.setBorder(null);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(10, 550, 30, 30);
        btnCancel.addActionListener(this);
        add(btnCancel);

        getContentPane().setBackground(new Color(255, 250, 250));
        setLayout(null);
        setLocation(150, 80);
        setSize(1210, 620);
        setVisible(true);

        loadSavedCredentials();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            MySQLConn conn = new MySQLConn();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            String query = "SELECT * FROM admin WHERE username = '" + username + "' AND password = '" + password + "'";
            ResultSet rs = null;
            try {
                rs = conn.executeQuery(query);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                if (rs == null) {
                    JOptionPane.showMessageDialog(null, "Lỗi: Không nhận được dữ liệu từ CSDL!");
                } else if (rs.next()) {
                    // Lưu mật khẩu nếu checkbox được chọn
                    if (chckbxSave.isSelected()) {
                        saveCredentials(username, password);
                    } else {
                        clearSavedCredentials();
                    }
                    // Hiển thị dialog chọn vai trò tùy chỉnh
                    RoleSelectionDialog dialog = new RoleSelectionDialog(this);
                    dialog.setVisible(true);

                    // Kiểm tra vai trò sau khi dialog đóng
                    if (dialog.isRoleSelected()) {
                        String role = dialog.getSelectedRole();
                        if ("Admin".equals(role)) {
                            new ScrDashboard().setVisible(true);
                            dispose(); // Đóng ScrLogin
                        } else if ("Reception".equals(role)) {
                            new ScrReception().setVisible(true);
                            dispose(); // Đóng ScrLogin
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò để tiếp tục!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Sai mật khẩu hoặc tên đăng nhập!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Lỗi SQL: " + ex.getMessage());
            }
        } else if (e.getSource() == btnCancel) {
            System.exit(102);
        }
    }


    // 🔹 Lưu thông tin đăng nhập vào Preferences
    private void saveCredentials(String username, String password) {
        prefs.put("username", username);
        prefs.put("password", password);
    }

    // 🔹 Xóa thông tin đăng nhập khi người dùng bỏ chọn "Save Password"
    private void clearSavedCredentials() {
        prefs.remove("username");
        prefs.remove("password");
    }

    // 🔹 Load thông tin đăng nhập đã lưu
    private void loadSavedCredentials() {
        String savedUsername = prefs.get("username", "");
        String savedPassword = prefs.get("password", "");

        txtUsername.setText(savedUsername);
        txtPassword.setText(savedPassword);

        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            chckbxSave.setSelected(true);
        }
    }

    public static BufferedImage scaleImageWithAspectRatio(String imagePath, int maxWidth, int maxHeight) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            double scaleFactor = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaledImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            return scaledImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showRoleSelectionDialog() {
        String[] options = {"Admin", "Reception"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select your role:",
                "Role Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) { // Admin
            new ScrDashboard().setVisible(true); // Hiển thị ScrDashboard
            dispose(); // Đóng cửa sổ login
        } else if (choice == 1) { // Reception
            new ScrReception().setVisible(true); // Hiển thị ScrReception
            dispose(); // Đóng cửa sổ login
        } else { // Trường hợp đóng dialog
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò để tiếp tục!");
        }
    }

    public static void main(String[] args) {
        new ScrLogin().setVisible(true);

    }

}
