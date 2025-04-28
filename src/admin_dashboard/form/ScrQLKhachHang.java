package admin_dashboard.form;

import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import admin_dashboard.model.ModelCard;
import admin_dashboard.component.Card;
import admin_dashboard.swing.scrollbar.ScrollBarCustom;
import admin_dashboard.swing.table.EventAction;
import admin_dashboard.swing.table.ModelAction;
import admin_dashboard.swing.table.Table;
import screen_utils.MySQLConn;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import screen_utils.ScrLogin;

public class ScrQLKhachHang extends JPanel {
    private JTextField txtFullName, txtPhone, txtEmail, txtIdentityNumber, txtNationality, txtAddress, txtSearch;
    private JComboBox<String> cbGender, cbFilterAttribute, cbFilterValue;
    private JButton btnAdd, btnViewDetails, btnExportExcel, btnImportExcel, btnClear, btnExit;
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelCustomer> customerList = new ArrayList<>();
    private Card card1, card2, card3;
    private JLabel lblImagePreview;
    private String selectedImagePath;

    private static class ModelCustomer {
        private int id;
        private String fullName, gender, phone, email, identityNumber, nationality, address, image;
        private Timestamp createdAt;

        public ModelCustomer(int id, String fullName, String gender, String phone, String email, String identityNumber,
                             String nationality, String address, Timestamp createdAt, String image) {
            this.id = id;
            this.fullName = fullName;
            this.gender = gender;
            this.phone = phone;
            this.email = email;
            this.identityNumber = identityNumber;
            this.nationality = nationality;
            this.address = address;
            this.createdAt = createdAt;
            this.image = image;
        }

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getIdentityNumber() { return identityNumber; }
        public String getNationality() { return nationality; }
        public String getAddress() { return address; }
        public Timestamp getCreatedAt() { return createdAt; }
        public String getImage() { return image; }
    }

    public ScrQLKhachHang() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        table.fixTable(jScrollPane1);
        loadTableData();
        updateCards();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ KHÁCH HÀNG");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setBackground(new Color(238, 130, 238));
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("TỔNG KHÁCH HÀNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("KHÁCH NAM", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ACCOUNT_CIRCLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card3 = new Card();
        card3.setBackground(new Color(194, 85, 1));
        card3.setColorGradient(new Color(255, 212, 99));
        card3.setData(new ModelCard("KHÁCH NỮ", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ACCOUNT_BOX, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin khách hàng
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder("THÔNG TIN KHÁCH HÀNG"));
        GridBagConstraints gbcPanel2 = new GridBagConstraints();
        gbcPanel2.insets = new Insets(5, 10, 5, 10);
        gbcPanel2.fill = GridBagConstraints.HORIZONTAL;

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 0;
        gbcPanel2.gridheight = 5;
        gbcPanel2.anchor = GridBagConstraints.NORTHWEST;
        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(120, 120));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
        lblImagePreview.setVerticalAlignment(JLabel.CENTER);
        lblImagePreview.setText("Chọn ảnh");
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(Color.LIGHT_GRAY);
        lblImagePreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseImage();
            }
        });
        jPanel2.add(lblImagePreview, gbcPanel2);

        gbcPanel2.gridheight = 1;
        gbcPanel2.anchor = GridBagConstraints.WEST;

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 0;
        JLabel lblFullName = new JLabel("HỌ TÊN:");
        lblFullName.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblFullName, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtFullName = new JTextField(15);
        txtFullName.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtFullName, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 1;
        JLabel lblGender = new JLabel("GIỚI TÍNH:");
        lblGender.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblGender, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbGender, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 2;
        JLabel lblPhone = new JLabel("ĐIỆN THOẠI:");
        lblPhone.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblPhone, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtPhone = new JTextField(12);
        txtPhone.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtPhone, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 3;
        JLabel lblEmail = new JLabel("EMAIL:");
        lblEmail.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblEmail, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtEmail = new JTextField(20);
        txtEmail.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtEmail, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 4;
        JLabel lblIdentityNumber = new JLabel("CMND/CCCD:");
        lblIdentityNumber.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblIdentityNumber, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtIdentityNumber = new JTextField(15);
        txtIdentityNumber.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtIdentityNumber, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 5;
        JLabel lblNationality = new JLabel("QUỐC TỊCH:");
        lblNationality.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblNationality, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtNationality = new JTextField(15);
        txtNationality.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtNationality, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 6;
        JLabel lblAddress = new JLabel("ĐỊA CHỈ:");
        lblAddress.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblAddress, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtAddress = new JTextField();
        txtAddress.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtAddress, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 7;
        gbcPanel2.gridwidth = 2;
        gbcPanel2.fill = GridBagConstraints.NONE;
        gbcPanel2.anchor = GridBagConstraints.CENTER;
        btnClear = new JButton("CLEAR");
        btnClear.setBackground(new Color(255, 165, 0));
        btnClear.setForeground(Color.BLACK);
        btnClear.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnClear.addActionListener(e -> clearFields());
        jPanel2.add(btnClear, gbcPanel2);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 5));
        buttonPanel.setPreferredSize(new Dimension(510, 100));

        JPanel row1 = new JPanel();
        row1.setBackground(new Color(255, 255, 255));
        row1.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        btnAdd = new JButton("THÊM");
        btnAdd.setBackground(new Color(127, 255, 212));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnAdd.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ADD, 35, Color.BLACK));
        btnAdd.addActionListener(e -> addCustomer());
        row1.add(btnAdd);

        btnViewDetails = new JButton("CHI TIẾT");
        btnViewDetails.setBackground(new Color(0, 102, 204));
        btnViewDetails.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PERM_DEVICE_INFORMATION, 35, Color.BLACK));
        btnViewDetails.addActionListener(e -> viewDetails());
        row1.add(btnViewDetails);

        btnExportExcel = new JButton("XUẤT EXCEL");
        btnExportExcel.setBackground(new Color(16, 124, 65));
        btnExportExcel.setForeground(Color.BLACK);
        btnExportExcel.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExportExcel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.FILE_DOWNLOAD, 35, Color.BLACK));
        btnExportExcel.addActionListener(e -> exportToExcel());
        row1.add(btnExportExcel);

        JPanel row2 = new JPanel();
        row2.setBackground(new Color(255, 255, 255));
        row2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        btnImportExcel = new JButton("NHẬP EXCEL");
        btnImportExcel.setBackground(new Color(16, 124, 65));
        btnImportExcel.setForeground(Color.BLACK);
        btnImportExcel.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnImportExcel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.FILE_UPLOAD, 35, Color.BLACK));
        btnImportExcel.addActionListener(e -> importFromExcel());
        row2.add(btnImportExcel);

        btnExit = new JButton("ĐĂNG XUẤT");
        btnExit.setBackground(new Color(255, 69, 0));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EXIT_TO_APP, 35, Color.BLACK));
        btnExit.addActionListener(e -> logout());
        row2.add(btnExit);

        buttonPanel.add(row1);
        buttonPanel.add(row2);

        // Panel tìm kiếm và lọc
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(255, 255, 255));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblSearch = new JLabel("TÌM KIẾM:");
        lblSearch.setFont(new Font("Calistoga", Font.PLAIN, 14));
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 25));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        JLabel lblFilterAttribute = new JLabel("LỌC THEO:");
        lblFilterAttribute.setFont(new Font("Calistoga", Font.PLAIN, 14));
        cbFilterAttribute = new JComboBox<>(new String[]{"Giới tính", "Quốc tịch"});
        cbFilterAttribute.setPreferredSize(new Dimension(120, 25));

        cbFilterValue = new JComboBox<>();
        cbFilterValue.setPreferredSize(new Dimension(120, 25));

        cbFilterValue.addItem("Tất cả");
        cbFilterValue.addItem("Male");
        cbFilterValue.addItem("Female");
        cbFilterValue.addItem("Other");
        cbFilterValue.setSelectedIndex(0);

        cbFilterAttribute.addActionListener(e -> {
            cbFilterValue.removeAllItems();
            String selectedAttribute = (String) cbFilterAttribute.getSelectedItem();
            switch (selectedAttribute) {
                case "Giới tính":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("Male");
                    cbFilterValue.addItem("Female");
                    cbFilterValue.addItem("Other");
                    break;
                case "Quốc tịch":
                    cbFilterValue.addItem("Tất cả");
                    try (ResultSet rs = dbConn.executeQuery("SELECT DISTINCT nationality FROM customer WHERE nationality IS NOT NULL")) {
                        while (rs.next()) {
                            cbFilterValue.addItem(rs.getString("nationality"));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
            cbFilterValue.setSelectedIndex(0);
            filterTable();
        });

        cbFilterValue.addActionListener(e -> filterTable());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblFilterAttribute);
        searchPanel.add(cbFilterAttribute);
        searchPanel.add(cbFilterValue);

        // Bảng hiển thị
        String[] columnNames = {"ID", "Ảnh", "Họ tên", "Giới tính", "Điện thoại", "Email", "CMND/CCCD", "Quốc tịch", "Địa chỉ", "Ngày tạo", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // Cột "Action"
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        table.getColumnModel().getColumn(1).setCellRenderer(new ImageCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(10).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(10).setCellEditor(new ActionCellEditor());
        table.getColumnModel().getColumn(10).setPreferredWidth(80);

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setVerticalScrollBar(new ScrollBarCustom());
        jScrollPane1.setHorizontalScrollBar(new ScrollBarCustom());
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new Dimension(800, 400));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);

        // Bố cục chính
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(jLabel1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(card1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGap(7, 7, 7)
                                                .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(7, 7, 7)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(card1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(card2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(card3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        setPreferredSize(new Dimension(1454, 768));
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh khách hàng");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            try {
                ImageIcon imageIcon = new ImageIcon(selectedImagePath);
                Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(image));
                lblImagePreview.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải ảnh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Chọn ảnh");
                selectedImagePath = null;
            }
        }
    }

    private boolean isPhoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer WHERE phone = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, phone);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private void addCustomer() {
        try {
            String fullName = txtFullName.getText().trim();
            String gender = (String) cbGender.getSelectedItem();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String identityNumber = txtIdentityNumber.getText().trim();
            String nationality = txtNationality.getText().trim();
            String address = txtAddress.getText().trim();

            if (fullName.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ tên, giới tính và điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isPhoneExists(phone)) {
                JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO customer (full_name, gender, phone, email, identity_number, nationality, address, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = dbConn.executeUpdate(sql,
                    fullName,
                    gender,
                    phone,
                    email.isEmpty() ? null : email,
                    identityNumber.isEmpty() ? null : identityNumber,
                    nationality.isEmpty() ? null : nationality,
                    address.isEmpty() ? null : address,
                    selectedImagePath != null ? selectedImagePath : "N/A"
            );

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xem chi tiết!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedId = (int) tableModel.getValueAt(selectedRow, 0);
        ModelCustomer selectedCustomer = null;
        for (ModelCustomer customer : customerList) {
            if (customer.getId() == selectedId) {
                selectedCustomer = customer;
                break;
            }
        }

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT KHÁCH HÀNG", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel lblImageDetail = new JLabel();
        lblImageDetail.setPreferredSize(new Dimension(120, 120));
        lblImageDetail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImageDetail.setHorizontalAlignment(JLabel.CENTER);
        lblImageDetail.setVerticalAlignment(JLabel.CENTER);
        if (selectedCustomer.getImage() != null && !selectedCustomer.getImage().isEmpty() && !selectedCustomer.getImage().equals("N/A")) {
            try {
                ImageIcon imageIcon = new ImageIcon(selectedCustomer.getImage());
                Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblImageDetail.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                lblImageDetail.setText("Không tải được ảnh");
            }
        } else {
            lblImageDetail.setText("Không có ảnh");
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("ẢNH:"), gbc);
        gbc.gridx = 1;
        dialog.add(lblImageDetail, gbc);

        String[] labels = {"ID:", "Họ tên:", "Giới tính:", "Điện thoại:", "Email:", "CMND/CCCD:", "Quốc tịch:", "Địa chỉ:", "Ngày tạo:"};
        String[] values = {
                String.valueOf(selectedCustomer.getId()),
                selectedCustomer.getFullName(),
                selectedCustomer.getGender(),
                selectedCustomer.getPhone(),
                selectedCustomer.getEmail() != null ? selectedCustomer.getEmail() : "",
                selectedCustomer.getIdentityNumber() != null ? selectedCustomer.getIdentityNumber() : "",
                selectedCustomer.getNationality() != null ? selectedCustomer.getNationality() : "",
                selectedCustomer.getAddress() != null ? selectedCustomer.getAddress() : "",
                selectedCustomer.getCreatedAt() != null ? selectedCustomer.getCreatedAt().toString() : ""
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            dialog.add(new JLabel(values[i]), gbc);
        }

        JButton btnClose = new JButton("ĐÓNG");
        btnClose.addActionListener(e -> dialog.dispose());
        gbc.gridx = 1;
        gbc.gridy = labels.length + 1;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(btnClose, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void exportToExcel() {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách khách hàng");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount() - 1; j++) {
                    Object value = tableModel.getValueAt(i, j);
                    Cell cell = row.createCell(j);
                    if (j == 1) { // Cột "Ảnh"
                        cell.setCellValue(value instanceof ImageIcon ? "Có ảnh" : "Không có ảnh");
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
                sheet.autoSizeColumn(i);
            }

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            FileDialog fileDialog = new FileDialog(parent, "Chọn nơi lưu file Excel", FileDialog.SAVE);
            fileDialog.setFile("Danh_sach_khach_hang.xls");
            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (fileName != null && directory != null) {
                File file = new File(directory + fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    JOptionPane.showMessageDialog(this, "Xuất Excel thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void importFromExcel() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FileDialog fileDialog = new FileDialog(parent, "Chọn file Excel để nhập", FileDialog.LOAD);
        fileDialog.setFile("*.xls;*.xlsx");
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        String directory = fileDialog.getDirectory();

        if (fileName != null && directory != null) {
            File file = new File(directory + fileName);
            try (FileInputStream fis = new FileInputStream(file)) {
                Workbook workbook;
                if (fileName.endsWith(".xls")) {
                    workbook = new HSSFWorkbook(fis);
                } else if (fileName.endsWith(".xlsx")) {
                    workbook = new HSSFWorkbook(fis);
                } else {
                    JOptionPane.showMessageDialog(this, "Định dạng file không hỗ trợ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                rowIterator.next(); // Bỏ qua hàng tiêu đề

                String sql = "INSERT INTO customer (full_name, gender, phone, email, identity_number, nationality, address, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                int rowsAffected = 0;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        String fullName = row.getCell(0).getStringCellValue();
                        String gender = row.getCell(1).getStringCellValue();
                        String phone = row.getCell(2).getStringCellValue();
                        String email = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
                        String identityNumber = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null;
                        String nationality = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
                        String address = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
                        String image = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "N/A";

                        if (fullName.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
                            continue;
                        }

                        if (isPhoneExists(phone)) {
                            continue;
                        }

                        int rowsAffectedCustomer = dbConn.executeUpdate(sql,
                                fullName, gender, phone, email, identityNumber, nationality, address, image);

                        if (rowsAffectedCustomer > 0) {
                            rowsAffected++;
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dòng: " + e.getMessage());
                    }
                }

                workbook.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Nhập " + rowsAffected + " khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    updateCards();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu nào được nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi nhập Excel: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtFullName.setText("");
        cbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtEmail.setText("");
        txtIdentityNumber.setText("");
        txtNationality.setText("");
        txtAddress.setText("");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Chọn ảnh");
        selectedImagePath = null;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
            ScrLogin loginScreen = new ScrLogin();
            JFrame loginFrame = new JFrame("Đăng nhập");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.add(loginScreen);
            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        }
    }

    private void updateCards() {
        int totalCount = 0, maleCount = 0, femaleCount = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT gender, COUNT(*) as count FROM customer GROUP BY gender")) {
            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt("count");
                totalCount += count;
                switch (gender) {
                    case "Male":
                        maleCount = count;
                        break;
                    case "Female":
                        femaleCount = count;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        card1.setData(new ModelCard("TỔNG KHÁCH HÀNG", totalCount, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("KHÁCH NAM", maleCount, maleCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ACCOUNT_CIRCLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card3.setData(new ModelCard("KHÁCH NỮ", femaleCount, femaleCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ACCOUNT_BOX, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editCustomer(ModelCustomer customer) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa khách hàng", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel editImagePreview = new JLabel();
        editImagePreview.setPreferredSize(new Dimension(120, 120));
        editImagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        editImagePreview.setHorizontalAlignment(JLabel.CENTER);
        editImagePreview.setVerticalAlignment(JLabel.CENTER);
        String[] editImagePath = {customer.getImage()};
        if (customer.getImage() != null && !customer.getImage().isEmpty() && !customer.getImage().equals("N/A")) {
            try {
                ImageIcon imageIcon = new ImageIcon(customer.getImage());
                Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                editImagePreview.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                editImagePreview.setText("Không tải được ảnh");
            }
        } else {
            editImagePreview.setText("Chọn ảnh");
        }
        editImagePreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn ảnh khách hàng");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (jpg, jpeg, png)", "jpg", "jpeg", "png");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(ScrQLKhachHang.this));
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    editImagePath[0] = selectedFile.getAbsolutePath();
                    try {
                        ImageIcon imageIcon = new ImageIcon(editImagePath[0]);
                        Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        editImagePreview.setIcon(new ImageIcon(image));
                        editImagePreview.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ScrQLKhachHang.this),
                                "Lỗi khi tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        editImagePreview.setIcon(null);
                        editImagePreview.setText("Chọn ảnh");
                        editImagePath[0] = null;
                    }
                }
            }
        });

        JTextField editFullName = new JTextField(customer.getFullName(), 15);
        JComboBox<String> editGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        editGender.setSelectedItem(customer.getGender());
        JTextField editPhone = new JTextField(customer.getPhone(), 12);
        JTextField editEmail = new JTextField(customer.getEmail() != null ? customer.getEmail() : "", 20);
        JTextField editIdentityNumber = new JTextField(customer.getIdentityNumber() != null ? customer.getIdentityNumber() : "", 15);
        JTextField editNationality = new JTextField(customer.getNationality() != null ? customer.getNationality() : "", 15);
        JTextField editAddress = new JTextField(customer.getAddress() != null ? customer.getAddress() : "", 20);

        String[] labels = {"Ảnh:", "Họ tên:", "Giới tính:", "Điện thoại:", "Email:", "CMND/CCCD:", "Quốc tịch:", "Địa chỉ:"};
        Component[] fields = {editImagePreview, editFullName, editGender, editPhone, editEmail, editIdentityNumber, editNationality, editAddress};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            dialog.add(fields[i], gbc);
        }

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                String fullName = editFullName.getText().trim();
                String gender = (String) editGender.getSelectedItem();
                String phone = editPhone.getText().trim();
                String email = editEmail.getText().trim();
                String identityNumber = editIdentityNumber.getText().trim();
                String nationality = editNationality.getText().trim();
                String address = editAddress.getText().trim();

                if (fullName.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Họ tên, giới tính và điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sqlCheckPhone = "SELECT COUNT(*) FROM customer WHERE phone = ? AND id != ?";
                try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckPhone, phone, customer.getId());
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Số điện thoại đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sql = "UPDATE customer SET full_name = ?, gender = ?, phone = ?, email = ?, identity_number = ?, nationality = ?, address = ?, image = ? WHERE id = ?";
                int rowsAffected = dbConn.executeUpdate(sql,
                        fullName,
                        gender,
                        phone,
                        email.isEmpty() ? null : email,
                        identityNumber.isEmpty() ? null : identityNumber,
                        nationality.isEmpty() ? null : nationality,
                        address.isEmpty() ? null : address,
                        editImagePath[0] != null ? editImagePath[0] : "N/A",
                        customer.getId()
                );

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                    updateCards();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 1;
        dialog.add(btnSave, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteCustomer(ModelCustomer customer) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng " + customer.getFullName() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM customer WHERE id = ?";
            int rowsAffected = dbConn.executeUpdate(sql, customer.getId());

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTableData() {
        String sql = "SELECT * FROM customer";
        tableModel.setRowCount(0);
        customerList.clear();

        EventAction<ModelCustomer> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelCustomer customer) throws SQLException {
                deleteCustomer(customer);
            }

            @Override
            public void update(ModelCustomer customer) {
                editCustomer(customer);
            }

            @Override
            public void view(ModelCustomer data) {

            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelCustomer customer = new ModelCustomer(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("gender"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("identity_number"),
                            rs.getString("nationality"),
                            rs.getString("address"),
                            rs.getTimestamp("created_at"),
                            rs.getString("image")
                    );
                    customerList.add(customer);

                    ImageIcon imageIcon = null;
                    String imagePath = customer.getImage();
                    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("N/A")) {
                        try {
                            ImageIcon originalIcon = new ImageIcon(imagePath);
                            Image image = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            imageIcon = new ImageIcon(image);
                        } catch (Exception e) {
                            System.err.println("Lỗi khi tải ảnh cho khách hàng " + customer.getId() + ": " + e.getMessage());
                        }
                    }

                    Object[] row = {
                            customer.getId(),
                            imageIcon,
                            customer.getFullName(),
                            customer.getGender(),
                            customer.getPhone(),
                            customer.getEmail(),
                            customer.getIdentityNumber(),
                            customer.getNationality(),
                            customer.getAddress(),
                            customer.getCreatedAt(),
                            new ModelAction<>(customer, eventAction)
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterTable() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        String filterAttribute = (String) cbFilterAttribute.getSelectedItem();
        String filterValue = (String) cbFilterValue.getSelectedItem();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 2, 4, 5, 6, 7, 8)); // Tìm kiếm trên các cột: Họ tên, Điện thoại, Email, CMND/CCCD, Quốc tịch, Địa chỉ
        }

        if (filterValue != null && !filterValue.equals("Tất cả")) {
            int columnIndex = -1;
            switch (filterAttribute) {
                case "Giới tính":
                    columnIndex = 3; // Cột "Giới tính"
                    break;
                case "Quốc tịch":
                    columnIndex = 7; // Cột "Quốc tịch"
                    break;
            }
            if (columnIndex != -1) {
                filters.add(RowFilter.regexFilter("(?i)^" + filterValue + "$", columnIndex));
            }
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private class ImageCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
            } else {
                label.setText("");
            }
            return label;
        }
    }

    private class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setBackground(table.getBackground());

            if (value instanceof ModelAction) {
                JButton btnEdit = new JButton();
                btnEdit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 13));
                btnEdit.setBackground(new Color(0, 102, 204));
                btnEdit.setForeground(Color.BLACK);
                btnEdit.setPreferredSize(new Dimension(30, 25));

                JButton btnDelete = new JButton();
                btnDelete.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 13));
                btnDelete.setBackground(new Color(204, 0, 0));
                btnDelete.setForeground(Color.BLACK);
                btnDelete.setPreferredSize(new Dimension(30, 25));

                panel.add(btnEdit);
                panel.add(btnDelete);
            }

            return panel;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private ModelAction<ModelCustomer> action;

        private final JPanel panel;
        private final JButton btnEdit;
        private final JButton btnDelete;

        public ActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            btnEdit = new JButton();
            btnEdit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 13));
            btnEdit.setBackground(new Color(0, 102, 204));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setPreferredSize(new Dimension(30, 25));

            btnDelete = new JButton();
            btnDelete.setBackground(new Color(204, 0, 0));
            btnDelete.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 13));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setPreferredSize(new Dimension(30, 25));

            btnEdit.addActionListener(e -> {
                if (action != null) {
                    action.getEvent().update(action.getData());
                    fireEditingStopped();
                }
            });

            btnDelete.addActionListener(e -> {
                if (action != null) {
                    try {
                        action.getEvent().delete(action.getData());
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    fireEditingStopped();
                }
            });

            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof ModelAction) {
                this.action = (ModelAction<ModelCustomer>) value;
            }
            panel.setBackground(table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return action;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("QUẢN LÝ KHÁCH HÀNG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 600));
        ScrQLKhachHang scrQLKhachHang = new ScrQLKhachHang();
        frame.add(scrQLKhachHang);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrQLKhachHang.dbConn.close();
            }
        });
    }
}