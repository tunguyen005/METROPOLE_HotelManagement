package admin_dashboard.form;

import admin_dashboard.ImageCellRenderer;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ScrQLNhanVien extends JPanel {
    private JTextField txtName, txtAge, txtSalary, txtPhone, txtEmail, txtAddress, txtSearch;
    private JComboBox<String> cbGender, cbJob, cbStatus, cbShift, cbDepartment, cbFilterAttribute, cbFilterValue;
    private JButton btnAdd, btnViewDetails, btnExportExcel, btnImportExcel, btnClear, btnExit;
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelEmployee> employeeList = new ArrayList<>();
    private Card card1, card2, card3, card4;
    private Map<Integer, String> departmentMap;
    private JLabel lblImagePreview;
    private String selectedImagePath;

    private static class ModelEmployee {
        private int id;
        private String name, age, gender, job, phone, email, address, shift, image;
        private float salary;
        private java.sql.Date startDate;
        private String status;
        private int departmentId;
        private String departmentName;

        public ModelEmployee(int id, String name, String age, String gender, String job, float salary, String phone,
                             String email, String address, java.sql.Date startDate, String status,
                             String shift, String image, int departmentId, String departmentName) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.job = job;
            this.salary = salary;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.startDate = startDate;
            this.status = status;
            this.shift = shift;
            this.image = image;
            this.departmentId = departmentId;
            this.departmentName = departmentName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getAge() { return age; }
        public String getGender() { return gender; }
        public String getJob() { return job; }
        public float getSalary() { return salary; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public java.sql.Date getStartDate() { return startDate; }
        public String getStatus() { return status; }
        public String getShift() { return shift; }
        public String getImage() { return image; }
        public int getDepartmentId() { return departmentId; }
        public String getDepartmentName() { return departmentName; }
    }

    public ScrQLNhanVien() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        departmentMap = new HashMap<>();
        loadDepartments();
        initComponents();
        setOpaque(false);
        table.fixTable(jScrollPane1);
        loadTableData();
        updateCards();
    }

    private void loadDepartments() {
        String sql = "SELECT id, name FROM department";
        try (ResultSet rs = dbConn.executeQuery(sql)) {
            departmentMap.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                if (name != null && !name.trim().isEmpty()) {
                    departmentMap.put(id, name);
                }
            }
            if (departmentMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có phòng ban nào trong cơ sở dữ liệu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phòng ban: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ NHÂN VIÊN");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setBackground(new Color(238, 130, 238));
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("TỔNG NHÂN VIÊN", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("ĐANG HOẠT ĐỘNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.WORK, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card3 = new Card();
        card3.setBackground(new Color(194, 85, 1));
        card3.setColorGradient(new Color(255, 212, 99));
        card3.setData(new ModelCard("NGHỈ PHÉP", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.OFFLINE_PIN, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card4 = new Card();
        card4.setBackground(new Color(60, 195, 0));
        card4.setColorGradient(new Color(208, 255, 90));
        card4.setData(new ModelCard("KHÔNG HOẠT ĐỘNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.STOP, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin nhân viên
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NHÂN VIÊN"));
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
        JLabel lblJob = new JLabel("CÔNG VIỆC:");
        lblJob.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblJob, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbJob = new JComboBox<>(new String[]{"Receptionist", "Security", "Manager", "Accountant", "Cleaner", "Driver"});
        cbJob.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbJob, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 4;
        JLabel lblDepartment = new JLabel("PHÒNG BAN:");
        lblDepartment.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblDepartment, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbDepartment = new JComboBox<>();
        cbDepartment.addItem("Chọn phòng ban");
        for (String deptName : departmentMap.values()) {
            cbDepartment.addItem(deptName);
        }
        cbDepartment.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbDepartment, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 5;
        JLabel lblSalary = new JLabel("LƯƠNG:");
        lblSalary.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblSalary, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtSalary = new JTextField(10);
        txtSalary.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtSalary, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 6;
        JLabel lblShift = new JLabel("CA LÀM:");
        lblShift.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblShift, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbShift = new JComboBox<>(new String[]{"Morning", "Afternoon", "Night"});
        cbShift.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbShift, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 7;
        JLabel lblStatus = new JLabel("TRẠNG THÁI:");
        lblStatus.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblStatus, gbcPanel2);
        gbcPanel2.gridx = 2;
        cbStatus = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});
        cbStatus.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbStatus, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 8;
        JLabel lblPhone = new JLabel("ĐIỆN THOẠI:");
        lblPhone.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblPhone, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtPhone = new JTextField(12);
        txtPhone.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtPhone, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 9;
        JLabel lblEmail = new JLabel("EMAIL:");
        lblEmail.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblEmail, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtEmail = new JTextField(20);
        txtEmail.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtEmail, gbcPanel2);

        gbcPanel2.gridx = 1;
        gbcPanel2.gridy = 10;
        JLabel lblAddress = new JLabel("ĐỊA CHỈ:");
        lblAddress.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblAddress, gbcPanel2);
        gbcPanel2.gridx = 2;
        txtAddress = new JTextField();
        txtAddress.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtAddress, gbcPanel2);

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
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 5)); // 2 hàng, 1 cột, khoảng cách 10px
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
        btnAdd.addActionListener(e -> addEmployee());
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
        ExcelLogo excelLogo = new ExcelLogo(512, 512);
        BufferedImage originalImage = excelLogo.getImage();
        Scale scaler = new Scale(originalImage);
        BufferedImage scaledImage = scaler.scaleTo(35, 35);
        Render renderer = new Render(scaledImage);
        BufferedImage renderedImage = renderer.render(35, 35);
        btnExportExcel.setIcon(new ImageIcon(renderedImage));
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

// Thêm 2 hàng vào buttonPanel
        buttonPanel.add(row1);
        buttonPanel.add(row2);

        // Panel tìm kiếm và lọc (gộp thành 2 combobox)
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
        cbFilterAttribute = new JComboBox<>(new String[]{"Trạng thái", "Công việc", "Ca làm", "Phòng ban"});
        cbFilterAttribute.setPreferredSize(new Dimension(120, 25));

// Combobox thứ 2: Lựa chọn giá trị lọc (dữ liệu)
        cbFilterValue = new JComboBox<>();
        cbFilterValue.setPreferredSize(new Dimension(120, 25));

// Khởi tạo giá trị mặc định cho cbFilterValue (dựa trên "Trạng thái")
        cbFilterValue.addItem("Tất cả");
        cbFilterValue.addItem("Active");
        cbFilterValue.addItem("Inactive");
        cbFilterValue.addItem("On Leave");
        cbFilterValue.setSelectedIndex(0); // Mặc định chọn "Tất cả"

// Cập nhật giá trị của cbFilterValue dựa trên lựa chọn của cbFilterAttribute
        cbFilterAttribute.addActionListener(e -> {
            cbFilterValue.removeAllItems(); // Xóa các giá trị cũ
            String selectedAttribute = (String) cbFilterAttribute.getSelectedItem();
            switch (selectedAttribute) {
                case "Trạng thái":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("Active");
                    cbFilterValue.addItem("Inactive");
                    cbFilterValue.addItem("On Leave");
                    break;
                case "Công việc":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("Receptionist");
                    cbFilterValue.addItem("Security");
                    cbFilterValue.addItem("Manager");
                    cbFilterValue.addItem("Accountant");
                    cbFilterValue.addItem("Cleaner");
                    cbFilterValue.addItem("Driver");
                    break;
                case "Ca làm":
                    cbFilterValue.addItem("Tất cả");
                    cbFilterValue.addItem("Morning");
                    cbFilterValue.addItem("Afternoon");
                    cbFilterValue.addItem("Night");
                    break;
                case "Phòng ban":
                    cbFilterValue.addItem("Tất cả");
                    for (String deptName : departmentMap.values()) {
                        cbFilterValue.addItem(deptName);
                    }
                    break;
            }
            cbFilterValue.setSelectedIndex(0); // Mặc định chọn "Tất cả"
            filterTable(); // Lọc lại bảng khi thay đổi thuộc tính
        });

// Lọc bảng khi thay đổi giá trị lọc
        cbFilterValue.addActionListener(e -> filterTable());

// Thêm các thành phần vào searchPanel
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblFilterAttribute);
        searchPanel.add(cbFilterAttribute);
        searchPanel.add(cbFilterValue);

        // Bảng hiển thị
        String[] columnNames = {"ID", "Ảnh", "Tên", "Tuổi", "Giới tính", "Công việc", "Phòng ban", "Lương", "Điện thoại", "Email", "Địa chỉ", "Ngày bắt đầu", "Trạng thái", "Ca làm", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 14; // Cột "Action" giờ là cột 14
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) { // Cột "Ảnh" là cột 1
                    return ImageIcon.class; // Đặt kiểu dữ liệu của cột "Ảnh" là ImageIcon
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 2; i++) { // Trừ cột "Ảnh" và "Action"
            if (i != 12) { // Cột "Trạng thái" giờ là cột 12
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

// Đặt renderer cho cột "Ảnh"
        table.getColumnModel().getColumn(1).setCellRenderer(new ImageCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(60); // Kích thước phù hợp cho cột ảnh

// Đặt renderer cho cột "Trạng thái"
        table.getColumnModel().getColumn(12).setCellRenderer(new StatusCellRenderer());

// Đặt renderer và editor cho cột "Action"
        table.getColumnModel().getColumn(14).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(14).setCellEditor(new ActionCellEditor());
        table.getColumnModel().getColumn(14).setPreferredWidth(80);

        jScrollPane1 = new JScrollPane(table);
// Sử dụng ScrollBarCustom cho thanh cuộn dọc và ngang
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
                                                .addComponent(card3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
                                        .addComponent(card3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(card4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
        fileChooser.setDialogTitle("Chọn ảnh nhân viên");
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

    private boolean isDriver(String job, String departmentName) {
        return job != null && job.equals("Driver") && departmentName != null && departmentName.equals("Driver");
    }

    private int getDepartmentId(String departmentName) throws SQLException {
        String sql = "SELECT id FROM department WHERE name = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, departmentName);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Không tìm thấy phòng ban '" + departmentName + "' trong bảng department!");
            }
        }
    }

    private boolean isPhoneExistsInDriver(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM driver WHERE phone = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, phone);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private void addEmployee() {
        try {
            String name = txtName.getText().trim();
            String age = txtAge.getText().trim();
            String gender = (String) cbGender.getSelectedItem();
            String job = (String) cbJob.getSelectedItem();
            String departmentName = (String) cbDepartment.getSelectedItem();
            String salary = txtSalary.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String shift = (String) cbShift.getSelectedItem();

            if (name.isEmpty() || age.isEmpty() || job.isEmpty() || phone.isEmpty() || departmentName.equals("Chọn phòng ban")) {
                JOptionPane.showMessageDialog(this, "Tên, tuổi, công việc, điện thoại và phòng ban không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trùng số điện thoại trong bảng employee
            String sqlCheckPhone = "SELECT COUNT(*) FROM employee WHERE phone = ?";
            try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckPhone, phone);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại trong bảng employee!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Kiểm tra trùng số điện thoại trong bảng driver
            if (isPhoneExistsInDriver(phone)) {
                JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại trong bảng driver!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int departmentId = getDepartmentId(departmentName);
            String sqlEmployee = "INSERT INTO employee (name, age, gender, job, department_id, salary, phone, email, address, start_date, status, shift, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffectedEmployee = dbConn.executeUpdate(sqlEmployee,
                    name,
                    age,
                    gender,
                    job,
                    departmentId,
                    salary.isEmpty() ? 0 : Double.parseDouble(salary),
                    phone,
                    email.isEmpty() ? "N/A" : email, // Provide default value for NOT NULL constraint
                    address.isEmpty() ? "N/A" : address, // Provide default value
                    new java.sql.Date(System.currentTimeMillis()),
                    status,
                    shift,
                    selectedImagePath != null ? selectedImagePath : "N/A" // Provide default value
            );

            // Nếu nhân viên là Driver và thuộc phòng ban Driver, thêm vào bảng driver
            int rowsAffectedDriver = 0;
            if (isDriver(job, departmentName)) {
                String sqlDriver = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                rowsAffectedDriver = dbConn.executeUpdate(sqlDriver,
                        name,
                        age,
                        gender,
                        "Unknown",
                        "Unknown",
                        "Unknown",
                        "Unknown",
                        address.isEmpty() ? "N/A" : address,
                        status.equals("Active") ? "Yes" : "No",
                        phone,
                        selectedImagePath != null ? selectedImagePath : "N/A",
                        null
                );
            }

            if (rowsAffectedEmployee > 0) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!" + (rowsAffectedDriver > 0 ? " Đã đồng bộ với bảng driver." : ""), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi và lương phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        ModelEmployee selectedEmployee = null;
        for (ModelEmployee employee : employeeList) {
            if (employee.getId() == selectedId) {
                selectedEmployee = employee;
                break;
            }
        }

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT NHÂN VIÊN", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel lblImageDetail = new JLabel();
        lblImageDetail.setPreferredSize(new Dimension(120, 120));
        lblImageDetail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImageDetail.setHorizontalAlignment(JLabel.CENTER);
        lblImageDetail.setVerticalAlignment(JLabel.CENTER);
        if (selectedEmployee.getImage() != null && !selectedEmployee.getImage().isEmpty() && !selectedEmployee.getImage().equals("N/A")) {
            try {
                ImageIcon imageIcon = new ImageIcon(selectedEmployee.getImage());
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

        String[] labels = {"ID:", "Tên:", "Tuổi:", "Giới tính:", "Công việc:", "Phòng ban:", "Lương:", "Điện thoại:", "Email:", "Địa chỉ:", "Ngày bắt đầu:", "Trạng thái:", "Ca làm:"};
        String[] values = {
                String.valueOf(selectedEmployee.getId()),
                selectedEmployee.getName(),
                selectedEmployee.getAge(),
                selectedEmployee.getGender(),
                selectedEmployee.getJob(),
                selectedEmployee.getDepartmentName(),
                String.valueOf(selectedEmployee.getSalary()),
                selectedEmployee.getPhone(),
                selectedEmployee.getEmail(),
                selectedEmployee.getAddress() != null ? selectedEmployee.getAddress() : "",
                selectedEmployee.getStartDate() != null ? selectedEmployee.getStartDate().toString() : "",
                selectedEmployee.getStatus(),
                selectedEmployee.getShift()
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
            Sheet sheet = workbook.createSheet("Danh sách nhân viên");

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
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
                sheet.autoSizeColumn(i);
            }

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            FileDialog fileDialog = new FileDialog(parent, "Chọn nơi lưu file Excel", FileDialog.SAVE);
            fileDialog.setFile("Danh_sach_nhan_vien.xls");
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

                String sqlEmployee = "INSERT INTO employee (name, age, gender, job, department_id, salary, phone, email, address, start_date, status, shift, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                String sqlDriver = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int rowsAffected = 0;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        String name = row.getCell(0).getStringCellValue();
                        String age = String.valueOf((int) row.getCell(1).getNumericCellValue());
                        String gender = row.getCell(2).getStringCellValue();
                        String job = row.getCell(3).getStringCellValue();
                        String departmentName = row.getCell(4).getStringCellValue();
                        float salary = (float) row.getCell(5).getNumericCellValue();
                        String phone = row.getCell(6).getStringCellValue();
                        String email = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "N/A";
                        String address = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "N/A";
                        String shift = row.getCell(9).getStringCellValue();
                        String status = row.getCell(10).getStringCellValue();
                        String image = row.getCell(11) != null ? row.getCell(11).getStringCellValue() : "N/A";

                        if (name.isEmpty() || age.isEmpty() || phone.isEmpty() || departmentName.isEmpty()) {
                            continue;
                        }

                        // Kiểm tra trùng số điện thoại
                        String sqlCheckPhone = "SELECT COUNT(*) FROM employee WHERE phone = ?";
                        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckPhone, phone);
                             ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                continue; // Bỏ qua nếu số điện thoại đã tồn tại
                            }
                        }
                        if (isPhoneExistsInDriver(phone)) {
                            continue; // Bỏ qua nếu số điện thoại đã tồn tại trong bảng driver
                        }

                        Integer departmentId = null;
                        for (Map.Entry<Integer, String> entry : departmentMap.entrySet()) {
                            if (entry.getValue().equals(departmentName)) {
                                departmentId = entry.getKey();
                                break;
                            }
                        }

                        if (departmentId == null) {
                            continue;
                        }

                        // Thêm vào bảng employee
                        int rowsAffectedEmployee = dbConn.executeUpdate(sqlEmployee,
                                name, age, gender, job, departmentId, salary, phone, email, address,
                                new java.sql.Date(System.currentTimeMillis()), status, shift, image);

                        // Nếu nhân viên là Driver, thêm vào bảng driver
                        int rowsAffectedDriver = 0;
                        if (isDriver(job, departmentName)) {
                            rowsAffectedDriver = dbConn.executeUpdate(sqlDriver,
                                    name, age, gender, "Unknown", "Unknown", "Unknown", "Unknown", address,
                                    status.equals("Active") ? "Yes" : "No", phone, image, null);
                        }

                        if (rowsAffectedEmployee > 0) {
                            rowsAffected++;
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dòng: " + e.getMessage());
                    }
                }

                workbook.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Nhập " + rowsAffected + " nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
        cbJob.setSelectedIndex(0);
        cbDepartment.setSelectedIndex(0);
        txtSalary.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        cbShift.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
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
        int totalCount = 0, activeCount = 0, onLeaveCount = 0, inactiveCount = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT status, COUNT(*) as count FROM employee GROUP BY status")) {
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                totalCount += count;
                switch (status) {
                    case "Active":
                        activeCount = count;
                        break;
                    case "On Leave":
                        onLeaveCount = count;
                        break;
                    case "Inactive":
                        inactiveCount = count;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        card1.setData(new ModelCard("TỔNG NHÂN VIÊN", totalCount, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("ĐANG HOẠT ĐỘNG", activeCount, activeCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.WORK, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card3.setData(new ModelCard("NGHỈ PHÉP", onLeaveCount, onLeaveCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.OFFLINE_PIN, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card4.setData(new ModelCard("KHÔNG HOẠT ĐỘNG", inactiveCount, inactiveCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.STOP, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editEmployee(ModelEmployee employee) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa nhân viên", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField editName = new JTextField(employee.getName(), 15);
        JTextField editAge = new JTextField(employee.getAge(), 5);
        JComboBox<String> editGender = new JComboBox<>(new String[]{"Male", "Female", "Others"});
        editGender.setSelectedItem(employee.getGender());
        JComboBox<String> editJob = new JComboBox<>(new String[]{"Receptionist", "Security", "Manager", "Accountant", "Cleaner", "Driver"});
        editJob.setSelectedItem(employee.getJob());
        JComboBox<String> editDepartment = new JComboBox<>();
        editDepartment.addItem("Chọn phòng ban");
        for (String deptName : departmentMap.values()) {
            editDepartment.addItem(deptName);
        }
        editDepartment.setSelectedItem(employee.getDepartmentName());
        JTextField editSalary = new JTextField(String.valueOf(employee.getSalary()), 10);
        JTextField editPhone = new JTextField(employee.getPhone(), 12);
        JTextField editEmail = new JTextField(employee.getEmail() != null ? employee.getEmail() : "", 20);
        JTextField editAddress = new JTextField(employee.getAddress() != null ? employee.getAddress() : "", 20);
        JTextField editStartDate = new JTextField(employee.getStartDate() != null ? employee.getStartDate().toString() : "", 10);
        JComboBox<String> editStatus = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});
        editStatus.setSelectedItem(employee.getStatus());
        JComboBox<String> editShift = new JComboBox<>(new String[]{"Morning", "Afternoon", "Night"});
        editShift.setSelectedItem(employee.getShift());

        JLabel editImagePreview = new JLabel();
        editImagePreview.setPreferredSize(new Dimension(120, 120));
        editImagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        editImagePreview.setHorizontalAlignment(JLabel.CENTER);
        editImagePreview.setVerticalAlignment(JLabel.CENTER);
        String[] editImagePath = {employee.getImage()};
        if (employee.getImage() != null && !employee.getImage().isEmpty() && !employee.getImage().equals("N/A")) {
            try {
                ImageIcon imageIcon = new ImageIcon(employee.getImage());
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
                fileChooser.setDialogTitle("Chọn ảnh nhân viên");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (jpg, jpeg, png)", "jpg", "jpeg", "png");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(ScrQLNhanVien.this));
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    editImagePath[0] = selectedFile.getAbsolutePath();
                    try {
                        ImageIcon imageIcon = new ImageIcon(editImagePath[0]);
                        Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        editImagePreview.setIcon(new ImageIcon(image));
                        editImagePreview.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ScrQLNhanVien.this),
                                "Lỗi khi tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        editImagePreview.setIcon(null);
                        editImagePreview.setText("Chọn ảnh");
                        editImagePath[0] = null;
                    }
                }
            }
        });

        String[] labels = {"ẢNH:", "Tên:", "Tuổi:", "Giới tính:", "Công việc:", "Phòng ban:", "Lương:", "Điện thoại:", "Email:", "Địa chỉ:", "Ngày bắt đầu:", "Trạng thái:", "Ca làm:"};
        Component[] fields = {editImagePreview, editName, editAge, editGender, editJob, editDepartment, editSalary, editPhone, editEmail, editAddress, editStartDate, editStatus, editShift};
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
                String job = (String) editJob.getSelectedItem();
                String departmentName = (String) editDepartment.getSelectedItem();
                String salary = editSalary.getText().trim();
                String phone = editPhone.getText().trim();
                String email = editEmail.getText().trim();
                String address = editAddress.getText().trim();
                String startDate = editStartDate.getText().trim();
                String status = (String) editStatus.getSelectedItem();
                String shift = (String) editShift.getSelectedItem();

                if (name.isEmpty() || age.isEmpty() || job.isEmpty() || phone.isEmpty() || departmentName.equals("Chọn phòng ban")) {
                    JOptionPane.showMessageDialog(dialog, "Tên, tuổi, công việc, điện thoại và phòng ban không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra trùng số điện thoại (trừ bản ghi hiện tại)
                String sqlCheckPhone = "SELECT COUNT(*) FROM employee WHERE phone = ? AND id != ?";
                try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckPhone, phone, employee.getId());
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Số điện thoại đã tồn tại trong bảng employee!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sqlCheckPhoneDriver = "SELECT COUNT(*) FROM driver WHERE phone = ? AND phone != ?";
                try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckPhoneDriver, phone, employee.getPhone());
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Số điện thoại đã tồn tại trong bảng driver!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                int departmentId = getDepartmentId(departmentName);
                String sqlUpdateEmployee = "UPDATE employee SET name = ?, age = ?, gender = ?, job = ?, department_id = ?, salary = ?, phone = ?, email = ?, address = ?, start_date = ?, status = ?, shift = ?, image = ? WHERE id = ?";
                int rowsAffectedEmployee = dbConn.executeUpdate(sqlUpdateEmployee,
                        name,
                        age,
                        gender,
                        job,
                        departmentId,
                        salary.isEmpty() ? 0 : Double.parseDouble(salary),
                        phone,
                        email.isEmpty() ? "N/A" : email,
                        address.isEmpty() ? "N/A" : address,
                        startDate.isEmpty() ? employee.getStartDate() : java.sql.Date.valueOf(startDate),
                        status,
                        shift,
                        editImagePath[0] != null ? editImagePath[0] : "N/A",
                        employee.getId()
                );

                // Kiểm tra trạng thái trước và sau khi chỉnh sửa
                boolean wasDriver = isDriver(employee.getJob(), employee.getDepartmentName());
                boolean isDriverNow = isDriver(job, departmentName);

                int rowsAffectedDriver = 0;
                if (wasDriver && !isDriverNow) {
                    // Nếu trước đây là Driver nhưng giờ không còn, xóa khỏi bảng driver
                    String sqlDeleteDriver = "DELETE FROM driver WHERE phone = ?";
                    rowsAffectedDriver = dbConn.executeUpdate(sqlDeleteDriver, employee.getPhone());
                } else if (!wasDriver && isDriverNow) {
                    // Nếu trước đây không phải Driver nhưng giờ là Driver, thêm vào bảng driver
                    String sqlInsertDriver = "INSERT INTO driver (name, age, gender, company, car_name, car_type, license_plate, location, available, phone, image, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    rowsAffectedDriver = dbConn.executeUpdate(sqlInsertDriver,
                            name,
                            age,
                            gender,
                            "Unknown",
                            "Unknown",
                            "Unknown",
                            "Unknown",
                            address.isEmpty() ? "N/A" : address,
                            status.equals("Active") ? "Yes" : "No",
                            phone,
                            editImagePath[0] != null ? editImagePath[0] : "N/A",
                            null
                    );
                } else if (wasDriver && isDriverNow) {
                    // Nếu vẫn là Driver, cập nhật thông tin trong bảng driver
                    String sqlUpdateDriver = "UPDATE driver SET name = ?, age = ?, gender = ?, location = ?, available = ?, phone = ?, image = ? WHERE phone = ?";
                    rowsAffectedDriver = dbConn.executeUpdate(sqlUpdateDriver,
                            name,
                            age,
                            gender,
                            address.isEmpty() ? "N/A" : address,
                            status.equals("Active") ? "Yes" : "No",
                            phone,
                            editImagePath[0] != null ? editImagePath[0] : "N/A",
                            employee.getPhone()
                    );
                }

                if (rowsAffectedEmployee > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật nhân viên thành công!" + (rowsAffectedDriver > 0 ? " Đã đồng bộ với bảng driver." : ""), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                    updateCards();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Tuổi và lương phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void deleteEmployee(ModelEmployee employee) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên " + employee.getName() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Xóa từ bảng employee
            String sqlDeleteEmployee = "DELETE FROM employee WHERE id = ?";
            int rowsAffectedEmployee = dbConn.executeUpdate(sqlDeleteEmployee, employee.getId());

            // Nếu nhân viên là Driver, xóa khỏi bảng driver
            int rowsAffectedDriver = 0;
            if (isDriver(employee.getJob(), employee.getDepartmentName())) {
                String sqlDeleteDriver = "DELETE FROM driver WHERE phone = ?";
                rowsAffectedDriver = dbConn.executeUpdate(sqlDeleteDriver, employee.getPhone());
            }

            if (rowsAffectedEmployee > 0) {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!" + (rowsAffectedDriver > 0 ? " Đã đồng bộ với bảng driver." : ""), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTableData() {
        String sql = "SELECT e.id, e.name, e.age, e.gender, e.job, e.department_id, e.salary, e.phone, e.email, e.address, e.start_date, e.status, e.shift, e.image, d.name as department_name " +
                "FROM employee e LEFT JOIN department d ON e.department_id = d.id";
        tableModel.setRowCount(0);
        employeeList.clear();

        EventAction<ModelEmployee> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelEmployee employee) throws SQLException {
                deleteEmployee(employee);
            }

            @Override
            public void update(ModelEmployee employee) {
                editEmployee(employee);
            }

            @Override
            public void view(ModelEmployee employee){
                viewDetails();
            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelEmployee employee = new ModelEmployee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("age"),
                            rs.getString("gender"),
                            rs.getString("job"),
                            rs.getFloat("salary"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getDate("start_date"),
                            rs.getString("status"),
                            rs.getString("shift"),
                            rs.getString("image"),
                            rs.getInt("department_id"),
                            rs.getString("department_name")
                    );
                    employeeList.add(employee);

                    // Tải hình ảnh và thu nhỏ kích thước
                    ImageIcon imageIcon = null;
                    String imagePath = employee.getImage();
                    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("N/A")) {
                        try {
                            ImageIcon originalIcon = new ImageIcon(imagePath);
                            Image image = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            imageIcon = new ImageIcon(image);
                        } catch (Exception e) {
                            System.err.println("Lỗi khi tải ảnh cho nhân viên " + employee.getId() + ": " + e.getMessage());
                        }
                    }

                    Object[] row = {
                            employee.getId(),
                            imageIcon,
                            employee.getName(),
                            employee.getAge(),
                            employee.getGender(),
                            employee.getJob(),
                            employee.getDepartmentName(),
                            employee.getSalary(),
                            employee.getPhone(),
                            employee.getEmail(),
                            employee.getAddress(),
                            employee.getStartDate(),
                            employee.getStatus(),
                            employee.getShift(),
                            new ModelAction<>(employee, eventAction)
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
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 2, 5, 6, 8, 9, 10));
        }

        if (filterValue != null && !filterValue.equals("Tất cả")) {
            int columnIndex = -1;
            switch (filterAttribute) {
                case "Trạng thái":
                    columnIndex = 12;
                    break;
                case "Công việc":
                    columnIndex = 5;
                    break;
                case "Ca làm":
                    columnIndex = 13;
                    break;
                case "Phòng ban":
                    columnIndex = 6;
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

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel();
            panel.setBackground(table.getBackground());
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

            String status = value != null ? value.toString() : "";
            switch (status) {
                case "Active":
                    JLabel activeLabel = new JLabel();
                    activeLabel.setOpaque(true);
                    activeLabel.setBackground(Color.GREEN);
                    activeLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(activeLabel);
                    break;
                case "Inactive":
                    JLabel inactiveLabel = new JLabel();
                    inactiveLabel.setOpaque(true);
                    inactiveLabel.setBackground(Color.RED);
                    inactiveLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(inactiveLabel);
                    break;
                case "On Leave":
                    JLabel leaveLabel = new JLabel();
                    leaveLabel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.OFFLINE_PIN, 20, Color.BLACK));
                    panel.add(leaveLabel);
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
        private ModelAction<ModelEmployee> action;

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
                this.action = (ModelAction<ModelEmployee>) value;
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
        JFrame frame = new JFrame("QUẢN LÝ NHÂN VIÊN");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 600));
        ScrQLNhanVien scrQLNhanVien = new ScrQLNhanVien();
        frame.add(scrQLNhanVien);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrQLNhanVien.dbConn.close();
            }
        });
    }
}