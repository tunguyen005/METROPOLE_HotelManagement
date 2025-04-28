package admin_dashboard.form;

import admin_dashboard.component.Card;
import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import admin_dashboard.model.ModelCard;
import admin_dashboard.swing.scrollbar.ScrollBarCustom;
import admin_dashboard.swing.table.EventAction;
import admin_dashboard.swing.table.ModelAction;
import admin_dashboard.swing.table.Table;
import screen_utils.*;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ScrQLTaiXe extends JPanel {
    private JTextField txtName, txtAge, txtCompany, txtCarName, txtLicensePlate, txtLocation, txtPhone, txtNote, txtSearch;
    private JComboBox<String> cbGender, cbCarType, cbAvailable, cbFilterAttribute, cbFilterValue;
    private JButton btnAdd, btnViewDetails, btnExportExcel, btnImportExcel, btnClear, btnExit;
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelDriver> driverList = new ArrayList<>();
    private Card card1, card2, card3;
    private JLabel lblImagePreview;
    private String selectedImagePath;

    private static class ModelDriver {
        private int id;
        private String name, age, gender, company, carName, carType, licensePlate, location, available, phone, image, note;
        private String job; // Thêm thuộc tính job
        private String departmentName; // Thêm thuộc tính departmentName

        public ModelDriver(int id, String name, String age, String gender, String company, String carName, String carType,
                           String licensePlate, String location, String available, String phone, String image, String note,
                           String job, String departmentName) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.company = company;
            this.carName = carName;
            this.carType = carType;
            this.licensePlate = licensePlate;
            this.location = location;
            this.available = available;
            this.phone = phone;
            this.image = image;
            this.note = note;
            this.job = job;
            this.departmentName = departmentName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getAge() { return age; }
        public String getGender() { return gender; }
        public String getCompany() { return company; }
        public String getCarName() { return carName; }
        public String getCarType() { return carType; }
        public String getLicensePlate() { return licensePlate; }
        public String getLocation() { return location; }
        public String getAvailable() { return available; }
        public String getPhone() { return phone; }
        public String getImage() { return image; }
        public String getNote() { return note; }
        public String getJob() { return job; } // Getter cho job
        public String getDepartmentName() { return departmentName; } // Getter cho departmentName
    }

    public ScrQLTaiXe() {
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
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ TÀI XẾ");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setBackground(new Color(238, 130, 238));
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("TỔNG TÀI XẾ", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("SẴN SÀNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card3 = new Card();
        card3.setBackground(new Color(194, 85, 1));
        card3.setColorGradient(new Color(255, 212, 99));
        card3.setData(new ModelCard("KHÔNG SẴN SÀNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CANCEL, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin tài xế
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder("THÔNG TIN TÀI XẾ"));
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
        JLabel lblName = new JLabel("TÊN:");
        lblName.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblName, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtName = new JTextField(15);
        txtName.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtName, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 1;
        JLabel lblAge = new JLabel("TUỔI:");
        lblAge.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblAge, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtAge = new JTextField(5);
        txtAge.setPreferredSize(new Dimension(80, 25));
        jPanel2.add(txtAge, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 2;
        JLabel lblGender = new JLabel("GIỚI TÍNH:");
        lblGender.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblGender, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Others"});
        cbGender.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbGender, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 3;
        JLabel lblCompany = new JLabel("CÔNG TY:");
        lblCompany.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCompany, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtCompany = new JTextField(15);
        txtCompany.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtCompany, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 4;
        JLabel lblCarName = new JLabel("TÊN XE:");
        lblCarName.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCarName, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtCarName = new JTextField(15);
        txtCarName.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtCarName, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 5;
        JLabel lblCarType = new JLabel("LOẠI XE:");
        lblCarType.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCarType, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbCarType = new JComboBox<>(new String[]{"4-Seater", "7-Seater", "Limousine", "Bus", "Other"});
        cbCarType.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbCarType, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 6;
        JLabel lblLicensePlate = new JLabel("BIỂN SỐ:");
        lblLicensePlate.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblLicensePlate, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtLicensePlate = new JTextField(10);
        txtLicensePlate.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtLicensePlate, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 7;
        JLabel lblLocation = new JLabel("VỊ TRÍ:");
        lblLocation.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblLocation, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtLocation = new JTextField(15);
        txtLocation.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtLocation, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 8;
        JLabel lblAvailable = new JLabel("TRẠNG THÁI:");
        lblAvailable.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblAvailable, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbAvailable = new JComboBox<>(new String[]{"Yes", "No"});
        cbAvailable.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbAvailable, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 9;
        JLabel lblPhone = new JLabel("ĐIỆN THOẠI:");
        lblPhone.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblPhone, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtPhone = new JTextField(12);
        txtPhone.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtPhone, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 10;
        JLabel lblNote = new JLabel("GHI CHÚ:");
        lblNote.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblNote, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtNote = new JTextField(20);
        txtNote.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtNote, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 11;
        gbcPanel2.gridwidth = 2;
        gbcPanel2.fill = GridBagConstraints.NONE;
        gbcPanel2.anchor = GridBagConstraints.CENTER;
        btnClear = new JButton("CLEAR");
        btnClear.setBackground(new Color(255, 165, 0));
        btnClear.setForeground(Color.BLACK);
        btnClear.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnClear.addActionListener(e -> clearFields());
        jPanel2.add(btnClear, gbcPanel2);

        // Panel nút chức năng (2 hàng)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 5));
        buttonPanel.setPreferredSize(new Dimension(510, 100));

        // Hàng 1: btnAdd, btnViewDetails, btnExportExcel
        JPanel row1 = new JPanel();
        row1.setBackground(new Color(255, 255, 255));
        row1.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        btnAdd = new JButton("THÊM");
        btnAdd.setBackground(new Color(127, 255, 212));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnAdd.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ADD, 35, Color.BLACK));
        btnAdd.addActionListener(e -> addDriver());
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

        // Hàng 2: btnImportExcel, btnExit
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
        cbFilterAttribute = new JComboBox<>(new String[]{"Trạng thái", "Loại xe"});
        cbFilterAttribute.setPreferredSize(new Dimension(120, 25));

        cbFilterValue = new JComboBox<>();
        cbFilterValue.setPreferredSize(new Dimension(120, 25));

        cbFilterValue.addItem("Tất cả");
        cbFilterValue.addItem("Yes");
        cbFilterValue.addItem("No");
        cbFilterValue.setSelectedIndex(0);

        cbFilterAttribute.addActionListener(e -> {
            cbFilterValue.removeAllItems();
            String selectedAttribute = (String) cbFilterAttribute.getSelectedItem();
            switch (selectedAttribute) {
                case "Trạng thái":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("Yes");
                    cbFilterValue.addItem("No");
                    break;
                case "Loại xe":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("4-Seater");
                    cbFilterValue.addItem("7-Seater");
                    cbFilterValue.addItem("Limousine");
                    cbFilterValue.addItem("Bus");
                    cbFilterValue.addItem("Other");
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

        String[] columnNames = {"ID", "Ảnh", "Tên", "Tuổi", "Giới tính", "Công ty", "Tên xe", "Loại xe", "Biển số", "Vị trí", "Trạng thái", "Điện thoại", "Ghi chú", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 13; // Cột "Action" là cột 13
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) { // Cột "Ảnh" là cột 1
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 2; i++) {
            if (i != 10) { // Cột "Trạng thái" là cột 10
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        table.getColumnModel().getColumn(1).setCellRenderer(new ImageCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(60);

        table.getColumnModel().getColumn(10).setCellRenderer(new StatusCellRenderer());

        table.getColumnModel().getColumn(13).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(13).setCellEditor(new ActionCellEditor());
        table.getColumnModel().getColumn(13).setPreferredWidth(80);

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

        // Bố cục chính với GroupLayout
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
        fileChooser.setDialogTitle("Chọn ảnh tài xế");
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

    private void addDriver() {
        try {
            String name = txtName.getText().trim();
            String age = txtAge.getText().trim();
            String gender = (String) cbGender.getSelectedItem();
            String company = txtCompany.getText().trim();
            String carName = txtCarName.getText().trim();
            String carType = (String) cbCarType.getSelectedItem();
            String licensePlate = txtLicensePlate.getText().trim();
            String location = txtLocation.getText().trim();
            String available = (String) cbAvailable.getSelectedItem();
            String phone = txtPhone.getText().trim();
            String note = txtNote.getText().trim();

            if (name.isEmpty() || age.isEmpty() || carName.isEmpty() || licensePlate.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên, tuổi, tên xe, biển số và điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Thêm vào bảng driver
            String sqlDriver = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffectedDriver = dbConn.executeUpdate(sqlDriver, name, age, gender, company.isEmpty() ? null : company, carName, carType, licensePlate, location.isEmpty() ? null : location, available, phone, selectedImagePath, note.isEmpty() ? null : note);

            // Thêm vào bảng employee
            int departmentId = getDriverDepartmentId();
            String sqlEmployee = "INSERT INTO employee (name, age, gender, job, department_id, salary, phone, email, address, start_date, status, shift, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffectedEmployee = dbConn.executeUpdate(sqlEmployee,
                    name,
                    age,
                    gender,
                    "Driver", // job
                    departmentId, // department_id
                    0, // salary: để mặc định là 0
                    phone,
                    null, // email: không có thông tin
                    location.isEmpty() ? null : location, // address
                    new java.sql.Date(System.currentTimeMillis()), // start_date: ngày hiện tại
                    available.equals("Yes") ? "Active" : "Inactive", // status
                    null, // shift: không có thông tin
                    selectedImagePath
            );

            if (rowsAffectedDriver > 0 && rowsAffectedEmployee > 0) {
                JOptionPane.showMessageDialog(this, "Thêm tài xế thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm tài xế thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm tài xế: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        ModelDriver selectedDriver = null;
        for (ModelDriver driver : driverList) {
            if (driver.getId() == selectedId) {
                selectedDriver = driver;
                break;
            }
        }

        if (selectedDriver == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài xế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT TÀI XẾ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel lblImageDetail = new JLabel();
        lblImageDetail.setPreferredSize(new Dimension(120, 120));
        lblImageDetail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImageDetail.setHorizontalAlignment(JLabel.CENTER);
        lblImageDetail.setVerticalAlignment(JLabel.CENTER);
        if (selectedDriver.getImage() != null && !selectedDriver.getImage().isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(selectedDriver.getImage());
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

        String[] labels = {"ID:", "Tên:", "Tuổi:", "Giới tính:", "Công ty:", "Tên xe:", "Loại xe:", "Biển số:", "Vị trí:", "Trạng thái:", "Điện thoại:", "Ghi chú:"};
        String[] values = {
                String.valueOf(selectedDriver.getId()),
                selectedDriver.getName(),
                selectedDriver.getAge(),
                selectedDriver.getGender(),
                selectedDriver.getCompany() != null ? selectedDriver.getCompany() : "",
                selectedDriver.getCarName(),
                selectedDriver.getCarType(),
                selectedDriver.getLicensePlate(),
                selectedDriver.getLocation() != null ? selectedDriver.getLocation() : "",
                selectedDriver.getAvailable(),
                selectedDriver.getPhone(),
                selectedDriver.getNote() != null ? selectedDriver.getNote() : ""
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
            Sheet sheet = workbook.createSheet("Danh sách tài xế");

            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                if (i == 1) continue; // Bỏ qua cột "Ảnh"
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(tableModel.getColumnName(i));
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                colIndex = 0;
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    if (j == 1) continue; // Bỏ qua cột "Ảnh"
                    Object value = tableModel.getValueAt(i, j);
                    Cell cell = row.createCell(colIndex++);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
                sheet.autoSizeColumn(i);
            }

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            FileDialog fileDialog = new FileDialog(parent, "Chọn nơi lưu file Excel", FileDialog.SAVE);
            fileDialog.setFile("Danh_sach_tai_xe.xls");
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
                rowIterator.next();

                String sql = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int rowsAffected = 0;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        String name = row.getCell(0).getStringCellValue();
                        String age = String.valueOf((int) row.getCell(1).getNumericCellValue());
                        String gender = row.getCell(2).getStringCellValue();
                        String company = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
                        String carName = row.getCell(4).getStringCellValue();
                        String carType = row.getCell(5).getStringCellValue();
                        String licensePlate = row.getCell(6).getStringCellValue();
                        String location = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : null;
                        String available = row.getCell(8).getStringCellValue();
                        String phone = row.getCell(9).getStringCellValue();
                        String image = row.getCell(10) != null ? row.getCell(10).getStringCellValue() : null;
                        String note = row.getCell(11) != null ? row.getCell(11).getStringCellValue() : null;

                        if (name.isEmpty() || age.isEmpty() || carName.isEmpty() || licensePlate.isEmpty() || phone.isEmpty()) {
                            continue;
                        }

                        rowsAffected += dbConn.executeUpdate(sql, name, age, gender, company, carName, carType, licensePlate, location, available, phone, image, note);
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dòng: " + e.getMessage());
                    }
                }

                workbook.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Nhập " + rowsAffected + " tài xế thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
        txtName.setText("");
        txtAge.setText("");
        cbGender.setSelectedIndex(0);
        txtCompany.setText("");
        txtCarName.setText("");
        cbCarType.setSelectedIndex(0);
        txtLicensePlate.setText("");
        txtLocation.setText("");
        cbAvailable.setSelectedIndex(0);
        txtPhone.setText("");
        txtNote.setText("");
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
        int totalCount = 0, availableCount = 0, unavailableCount = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT available, COUNT(*) as count FROM driver GROUP BY available")) {
            while (rs.next()) {
                String available = rs.getString("available");
                int count = rs.getInt("count");
                totalCount += count;
                switch (available) {
                    case "Yes":
                        availableCount = count;
                        break;
                    case "No":
                        unavailableCount = count;
                        break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thẻ thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return; // Thoát phương thức để tránh cập nhật thẻ với dữ liệu sai
        }

        card1.setData(new ModelCard("TỔNG TÀI XẾ", totalCount, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("SẴN SÀNG", availableCount, availableCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card3.setData(new ModelCard("KHÔNG SẴN SÀNG", unavailableCount, unavailableCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CANCEL, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editDriver(ModelDriver driver) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa tài xế", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField editName = new JTextField(driver.getName(), 15);
        JTextField editAge = new JTextField(driver.getAge(), 5);
        JComboBox<String> editGender = new JComboBox<>(new String[]{"Male", "Female", "Others"});
        editGender.setSelectedItem(driver.getGender());
        JTextField editCompany = new JTextField(driver.getCompany(), 15);
        JTextField editCarName = new JTextField(driver.getCarName(), 15);
        JComboBox<String> editCarType = new JComboBox<>(new String[]{"4-Seater", "7-Seater", "Limousine", "Bus", "Other"});
        editCarType.setSelectedItem(driver.getCarType());
        JTextField editLicensePlate = new JTextField(driver.getLicensePlate(), 10);
        JTextField editLocation = new JTextField(driver.getLocation(), 15);
        JComboBox<String> editAvailable = new JComboBox<>(new String[]{"Yes", "No"});
        editAvailable.setSelectedItem(driver.getAvailable());
        JTextField editPhone = new JTextField(driver.getPhone(), 12);
        JTextField editNote = new JTextField(driver.getNote(), 20);

        JLabel editImagePreview = new JLabel();
        editImagePreview.setPreferredSize(new Dimension(120, 120));
        editImagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        editImagePreview.setHorizontalAlignment(JLabel.CENTER);
        editImagePreview.setVerticalAlignment(JLabel.CENTER);
        String[] editImagePath = {driver.getImage()};
        if (driver.getImage() != null && !driver.getImage().isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(driver.getImage());
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
                fileChooser.setDialogTitle("Chọn ảnh tài xế");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (jpg, jpeg, png)", "jpg", "jpeg", "png");
                fileChooser.setFileFilter(filter);
                fileChooser.setPreferredSize(new Dimension(800, 500));
                fileChooser.setAccessory(createPreviewAccessory(fileChooser));

                int result = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(ScrQLTaiXe.this));
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    editImagePath[0] = selectedFile.getAbsolutePath();

                    try {
                        ImageIcon imageIcon = new ImageIcon(editImagePath[0]);
                        Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        editImagePreview.setIcon(new ImageIcon(image));
                        editImagePreview.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ScrQLTaiXe.this),
                                "Lỗi khi tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        editImagePreview.setIcon(null);
                        editImagePreview.setText("Chọn ảnh");
                        editImagePath[0] = null;
                    }
                }
            }
        });

        String[] labels = {"ẢNH:", "Tên:", "Tuổi:", "Giới tính:", "Công ty:", "Tên xe:", "Loại xe:", "Biển số:", "Vị trí:", "Trạng thái:", "Điện thoại:", "Ghi chú:"};
        Component[] fields = {editImagePreview, editName, editAge, editGender, editCompany, editCarName, editCarType, editLicensePlate, editLocation, editAvailable, editPhone, editNote};
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
                String name = editName.getText().trim();
                String age = editAge.getText().trim();
                String gender = (String) editGender.getSelectedItem();
                String company = editCompany.getText().trim();
                String carName = editCarName.getText().trim();
                String carType = (String) editCarType.getSelectedItem();
                String licensePlate = editLicensePlate.getText().trim();
                String location = editLocation.getText().trim();
                String available = (String) editAvailable.getSelectedItem();
                String phone = editPhone.getText().trim();
                String note = editNote.getText().trim();

                if (name.isEmpty() || age.isEmpty() || carName.isEmpty() || licensePlate.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tên, tuổi, tên xe, biển số và điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cập nhật bảng driver
                String sqlUpdateDriver = "UPDATE driver SET name = ?, age = ?, gender = ?, company = ?, car_name = ?, car_type = ?, license_plate = ?, location = ?, available = ?, phone = ?, image = ?, note = ? WHERE id = ?";
                int rowsAffectedDriver = dbConn.executeUpdate(sqlUpdateDriver, name, age, gender, company.isEmpty() ? null : company, carName, carType, licensePlate, location.isEmpty() ? null : location, available, phone, editImagePath[0], note.isEmpty() ? null : note, driver.getId());

                // Cập nhật bảng employee
                String sqlUpdateEmployee = "UPDATE employee SET name = ?, age = ?, gender = ?, address = ?, status = ?, phone = ?, image = ? WHERE phone = ?";
                int rowsAffectedEmployee = dbConn.executeUpdate(sqlUpdateEmployee, name, age, gender, location.isEmpty() ? null : location, available.equals("Yes") ? "Active" : "Inactive", phone, editImagePath[0], driver.getPhone());

                if (rowsAffectedDriver > 0 || rowsAffectedEmployee > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật tài xế thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                    updateCards();
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Tuổi phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private JComponent createPreviewAccessory(JFileChooser fileChooser) {
        JPanel previewPanel = new JPanel(new BorderLayout());
        JLabel previewLabel = new JLabel();
        previewLabel.setPreferredSize(new Dimension(200, 200));
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewLabel.setBorder(BorderFactory.createTitledBorder("Xem trước"));
        previewLabel.setText("Không có ảnh");

        fileChooser.addPropertyChangeListener(e -> {
            if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())) {
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    try {
                        ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
                        Image image = imageIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                        previewLabel.setIcon(new ImageIcon(image));
                        previewLabel.setText("");
                    } catch (Exception ex) {
                        previewLabel.setIcon(null);
                        previewLabel.setText("Không tải được ảnh");
                    }
                } else {
                    previewLabel.setIcon(null);
                    previewLabel.setText("Không có ảnh");
                }
            }
        });

        previewPanel.add(previewLabel, BorderLayout.CENTER);
        return previewPanel;
    }

    private int getDriverDepartmentId() throws SQLException {
        String sql = "SELECT id FROM department WHERE name = 'Driver'";
        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Không tìm thấy phòng ban 'Driver' trong bảng department!");
            }
        }
    }

    private void deleteDriver(ModelDriver driver) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài xế " + driver.getName() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Xóa từ bảng driver
            String sqlDeleteDriver = "DELETE FROM driver WHERE id = ?";
            int rowsAffectedDriver = dbConn.executeUpdate(sqlDeleteDriver, driver.getId());

            // Xóa từ bảng employee
            String sqlDeleteEmployee = "DELETE FROM employee WHERE phone = ? AND job = 'Driver'";
            int rowsAffectedEmployee = dbConn.executeUpdate(sqlDeleteEmployee, driver.getPhone());

            if (rowsAffectedDriver > 0 || rowsAffectedEmployee > 0) {
                JOptionPane.showMessageDialog(this, "Xóa tài xế thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài xế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void loadTableData() {
        // Truy vấn lấy nhân viên có job = 'Driver' và department_name = 'Driver'
        String sql = "SELECT e.*, dpt.name as department_name " +
                "FROM employee e " +
                "JOIN department dpt ON e.department_id = dpt.id " +
                "WHERE e.job = 'Driver' AND dpt.name = 'Driver'";
        tableModel.setRowCount(0);
        driverList.clear();

        EventAction<ModelDriver> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelDriver driver) throws SQLException {
                deleteDriver(driver);
            }

            @Override
            public void update(ModelDriver driver) {
                editDriver(driver);
            }

            @Override
            public void view(ModelDriver driver){
                viewDetails();
            }

        };

        try {
            // Lấy danh sách nhân viên từ employee
            Map<String, ModelDriver> employeeDrivers = new HashMap<>();
            try (ResultSet rs = dbConn.executeQuery(sql)) {
                while (rs.next()) {
                    ModelDriver driver = new ModelDriver(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("age"),
                            rs.getString("gender"),
                            null, // company: không có trong employee, để null
                            null, // car_name: không có trong employee, để null
                            null, // car_type: không có trong employee, để null
                            null, // license_plate: không có trong employee, để null
                            rs.getString("address"), // location: ánh xạ từ address
                            rs.getString("status").equals("Active") ? "Yes" : "No", // available: ánh xạ từ status
                            rs.getString("phone"),
                            rs.getString("image"),
                            null, // note: không có trong employee, để null
                            rs.getString("job"),
                            rs.getString("department_name")
                    );
                    employeeDrivers.put(driver.getPhone(), driver);
                }
            }

            // Lấy danh sách tài xế hiện tại từ bảng driver
            Map<String, ModelDriver> currentDrivers = new HashMap<>();
            try (ResultSet rs = dbConn.executeQuery("SELECT * FROM driver")) {
                while (rs.next()) {
                    ModelDriver driver = new ModelDriver(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("age"),
                            rs.getString("gender"),
                            rs.getString("company"),
                            rs.getString("car_name"),
                            rs.getString("car_type"),
                            rs.getString("license_plate"),
                            rs.getString("location"),
                            rs.getString("available"),
                            rs.getString("phone"),
                            rs.getString("image"),
                            rs.getString("note"),
                            null, // job: sẽ được cập nhật sau
                            null  // department_name: sẽ được cập nhật sau
                    );
                    currentDrivers.put(driver.getPhone(), driver);
                }
            }

            // Đồng bộ dữ liệu từ employee sang driver
            for (String phone : employeeDrivers.keySet()) {
                ModelDriver employeeDriver = employeeDrivers.get(phone);
                ModelDriver currentDriver = currentDrivers.get(phone);

                if (currentDriver == null) {
                    // Thêm mới vào bảng driver
                    String insertSql = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    dbConn.executeUpdate(insertSql,
                            employeeDriver.getName(),
                            employeeDriver.getAge(),
                            employeeDriver.getGender(),
                            null, // company
                            null, // car_name
                            null, // car_type
                            null, // license_plate
                            employeeDriver.getLocation(),
                            employeeDriver.getAvailable(),
                            employeeDriver.getPhone(),
                            employeeDriver.getImage(),
                            null  // note
                    );
                } else {
                    // Cập nhật thông tin trong bảng driver
                    String updateSql = "UPDATE driver SET name = ?, age = ?, gender = ?, location = ?, available = ?, phone = ?, image = ? WHERE phone = ?";
                    dbConn.executeUpdate(updateSql,
                            employeeDriver.getName(),
                            employeeDriver.getAge(),
                            employeeDriver.getGender(),
                            employeeDriver.getLocation(),
                            employeeDriver.getAvailable(),
                            employeeDriver.getPhone(),
                            employeeDriver.getImage(),
                            employeeDriver.getPhone()
                    );
                }
            }

            // Xóa các tài xế trong driver không còn trong employee
            for (String phone : currentDrivers.keySet()) {
                if (!employeeDrivers.containsKey(phone)) {
                    String deleteSql = "DELETE FROM driver WHERE phone = ?";
                    dbConn.executeUpdate(deleteSql, phone);
                }
            }

            // Load lại dữ liệu từ bảng driver để hiển thị
            try (ResultSet rs = dbConn.executeQuery("SELECT d.*, e.job, dpt.name as department_name " +
                    "FROM driver d " +
                    "LEFT JOIN employee e ON d.phone = e.phone " +
                    "LEFT JOIN department dpt ON e.department_id = dpt.id")) {
                while (rs.next()) {
                    ModelDriver driver = new ModelDriver(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("age"),
                            rs.getString("gender"),
                            rs.getString("company"),
                            rs.getString("car_name"),
                            rs.getString("car_type"),
                            rs.getString("license_plate"),
                            rs.getString("location"),
                            rs.getString("available"),
                            rs.getString("phone"),
                            rs.getString("image"),
                            rs.getString("note"),
                            rs.getString("job"),
                            rs.getString("department_name")
                    );
                    driverList.add(driver);

                    ImageIcon imageIcon = null;
                    String imagePath = driver.getImage();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            ImageIcon originalIcon = new ImageIcon(imagePath);
                            Image image = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            imageIcon = new ImageIcon(image);
                        } catch (Exception e) {
                            System.err.println("Lỗi khi tải ảnh cho tài xế " + driver.getId() + ": " + e.getMessage());
                        }
                    }

                    Object[] row = {
                            driver.getId(),
                            imageIcon,
                            driver.getName(),
                            driver.getAge(),
                            driver.getGender(),
                            driver.getCompany(),
                            driver.getCarName(),
                            driver.getCarType(),
                            driver.getLicensePlate(),
                            driver.getLocation(),
                            driver.getAvailable(),
                            driver.getPhone(),
                            driver.getNote(),
                            new ModelAction<>(driver, eventAction)
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
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 2, 5, 6, 7, 8, 9, 11, 12)); // Tìm kiếm trên các cột: Tên, Công ty, Tên xe, Loại xe, Biển số, Vị trí, Điện thoại, Ghi chú
        }

        if (filterValue != null && !filterValue.equals("Tất cả")) {
            int columnIndex = -1;
            switch (filterAttribute) {
                case "Trạng thái":
                    columnIndex = 10; // Cột "Trạng thái"
                    break;
                case "Loại xe":
                    columnIndex = 7; // Cột "Loại xe"
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
                ImageIcon icon = (ImageIcon) value;
                label.setIcon(icon);
            } else {
                label.setText("No Image");
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            label.setOpaque(true);
            return label;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel();
            panel.setBackground(table.getBackground());
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

            String status = value != null ? value.toString() : "";
            switch (status) {
                case "Yes":
                    JLabel yesLabel = new JLabel();
                    yesLabel.setOpaque(true);
                    yesLabel.setBackground(Color.GREEN);
                    yesLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(yesLabel);
                    break;
                case "No":
                    JLabel noLabel = new JLabel();
                    noLabel.setOpaque(true);
                    noLabel.setBackground(Color.RED);
                    noLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(noLabel);
                    break;
                default:
                    panel.add(new JLabel(status));
                    break;
            }

            return panel;
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
        private ModelAction<ModelDriver> action;

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
                this.action = (ModelAction<ModelDriver>) value;
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
        JFrame frame = new JFrame("QUẢN LÝ TÀI XẾ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 600));
        ScrQLTaiXe scrQLTaiXe = new ScrQLTaiXe();
        frame.add(scrQLTaiXe);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrQLTaiXe.dbConn.close();
            }
        });
    }
}
