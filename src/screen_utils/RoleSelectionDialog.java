package screen_utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RoleSelectionDialog extends JDialog {
    private boolean roleSelected = false;
    private String selectedRole = null;

    public RoleSelectionDialog(JFrame parent) {
        super(parent, "PHÂN QUYỀN", true); // Modal dialog
        setSize(300, 200);
        setLocationRelativeTo(parent); // Hiển thị giữa màn hình cha
        setLayout(null);
        getContentPane().setBackground(new Color(255, 250, 250));

        // Nút Admin
        ImageIcon adminIcon = new ImageIcon(getClass().getResource("/image/admin.png"));
        JButton btnAdmin = new JButton("Admin", adminIcon);
        btnAdmin.setBackground(new Color(0, 0, 0));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFont(new Font("Calistoga", Font.BOLD, 16));
        btnAdmin.setBounds(50, 40, 200, 40);
        btnAdmin.setHorizontalAlignment(SwingConstants.LEFT);
        btnAdmin.setBorder(null);
        btnAdmin.setIconTextGap(15);
        btnAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRole = "Admin";
                roleSelected = true;
                dispose(); // Đóng dialog
            }
        });
        add(btnAdmin);

        // Nút Reception
        ImageIcon receptionIcon = new ImageIcon(getClass().getResource("/image/reception.png"));
        JButton btnReception = new JButton("Tiếp tân", receptionIcon);
        btnReception.setBackground(new Color(0, 0, 0));
        btnReception.setForeground(Color.WHITE);
        btnReception.setFont(new Font("Calistoga", Font.BOLD, 16));
        btnReception.setBounds(50, 100, 200, 40);
        btnReception.setHorizontalAlignment(SwingConstants.LEFT);
        btnReception.setIconTextGap(15);
        btnReception.setBorder(null);
        btnReception.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReception.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRole = "Reception";
                roleSelected = true;
                dispose(); // Đóng dialog
            }
        });
        add(btnReception);
    }

    public boolean isRoleSelected() {
        return roleSelected;
    }

    public String getSelectedRole() {
        return selectedRole;
    }
}