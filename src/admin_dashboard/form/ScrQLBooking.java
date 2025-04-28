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

import com.toedter.calendar.JDateChooser; // Thêm import cho JDateChooser

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ScrQLBooking extends JPanel {
    private JComboBox<String> cbCustomer, cbRoom, cbEmployee;
    private JTextField txtNote, txtSearch;
    private JComboBox<String> cbStatus, cbFilterAttribute, cbFilterValue;
    private JTextField txtCheckIn, txtCheckOut; // Thay JFormattedTextField bằng JTextField để dễ xử lý
    private JButton btnAdd, btnViewDetails, btnExportExcel, btnImportExcel, btnClear, btnExit;
    public MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelBooking> bookingList = new ArrayList<>();
    private Card card1, card2, card3, card4;
    private Map<Integer, String> customerMap, roomMap, employeeMap;
    private Map<String, Integer> customerIdMap, roomIdMap, employeeIdMap;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static class ModelBooking {
        private int id, customerId, roomId, employeeId;
        private String customerName, roomName, employeeName, status, note;
        private java.sql.Date checkIn, checkOut, createdAt;

        public ModelBooking(int id, int customerId, String customerName, int roomId, String roomName,
                            int employeeId, String employeeName, java.sql.Date checkIn, java.sql.Date checkOut,
                            String status, String note, java.sql.Date createdAt) {
            this.id = id;
            this.customerId = customerId;
            this.customerName = customerName;
            this.roomId = roomId;
            this.roomName = roomName;
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.status = status;
            this.note = note;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public int getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public int getRoomId() { return roomId; }
        public String getRoomName() { return roomName; }
        public int getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public java.sql.Date getCheckIn() { return checkIn; }
        public java.sql.Date getCheckOut() { return checkOut; }
        public String getStatus() { return status; }
        public String getNote() { return note; }
        public java.sql.Date getCreatedAt() { return createdAt; }
    }

    public ScrQLBooking() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        customerMap = new HashMap<>();
        roomMap = new HashMap<>();
        employeeMap = new HashMap<>();
        customerIdMap = new HashMap<>();
        roomIdMap = new HashMap<>();
        employeeIdMap = new HashMap<>();
        loadReferences();
        initComponents();
        setOpaque(false);
        table.fixTable(jScrollPane1);
        loadTableData();
        updateCards();
    }

    private void loadReferences() {
        // Load customers
        String sqlCustomer = "SELECT id, full_name FROM customer";
        try (ResultSet rs = dbConn.executeQuery(sqlCustomer)) {
            customerMap.clear();
            customerIdMap.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                customerMap.put(id, name);
                customerIdMap.put(name, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        // Load rooms
        String sqlRoom = "SELECT id, room_num FROM room";
        try (ResultSet rs = dbConn.executeQuery(sqlRoom)) {
            roomMap.clear();
            roomIdMap.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String roomNum = rs.getString("room_num");
                roomMap.put(id, roomNum);
                roomIdMap.put(roomNum, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        // Load employees (chỉ lấy nhân viên có job = 'Receptionist' và thuộc phòng ban 'Reception')
        String sqlEmployee = "SELECT e.id, e.name " +
                "FROM employee e " +
                "JOIN department d ON e.department_id = d.id " +
                "WHERE e.job = 'Receptionist' AND d.name = 'Reception'";
        try (ResultSet rs = dbConn.executeQuery(sqlEmployee)) {
            employeeMap.clear();
            employeeIdMap.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                employeeMap.put(id, name);
                employeeIdMap.put(name, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ ĐẶT PHÒNG");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setBackground(new Color(238, 130, 238));
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("TỔNG ĐẶT PHÒNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BOOK, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("ĐANG HOẠT ĐỘNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PLAY_ARROW, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card3 = new Card();
        card3.setBackground(new Color(194, 85, 1));
        card3.setColorGradient(new Color(255, 212, 99));
        card3.setData(new ModelCard("ĐÃ HỦY", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CANCEL, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card4 = new Card();
        card4.setBackground(new Color(60, 195, 0));
        card4.setColorGradient(new Color(208, 255, 90));
        card4.setData(new ModelCard("ĐÃ HOÀN TẤT", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin đặt phòng
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder("THÔNG TIN ĐẶT PHÒNG"));
        GridBagConstraints gbcPanel2 = new GridBagConstraints();
        gbcPanel2.insets = new Insets(5, 10, 5, 10);
        gbcPanel2.fill = GridBagConstraints.HORIZONTAL;

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 0;
        JLabel lblCustomer = new JLabel("KHÁCH HÀNG:");
        lblCustomer.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCustomer, gbcPanel2);
        gbcPanel2.gridx = 1;
        cbCustomer = new JComboBox<>();
        cbCustomer.addItem("Chọn khách hàng");
        for (String customerName : customerMap.values()) {
            cbCustomer.addItem(customerName);
        }
        cbCustomer.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbCustomer, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 1;
        JLabel lblRoom = new JLabel("PHÒNG:");
        lblRoom.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblRoom, gbcPanel2);
        gbcPanel2.gridx = 1;
        cbRoom = new JComboBox<>();
        cbRoom.addItem("Chọn phòng");
        for (String roomNum : roomMap.values()) {
            cbRoom.addItem(roomNum);
        }
        cbRoom.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbRoom, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 2;
        JLabel lblEmployee = new JLabel("NHÂN VIÊN:");
        lblEmployee.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblEmployee, gbcPanel2);
        gbcPanel2.gridx = 1;
        cbEmployee = new JComboBox<>();
        cbEmployee.addItem("Chọn nhân viên");
        for (String employeeName : employeeMap.values()) {
            cbEmployee.addItem(employeeName);
        }
        cbEmployee.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbEmployee, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 3;
        JLabel lblCheckIn = new JLabel("CHECK-IN (yyyy-MM-dd):");
        lblCheckIn.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCheckIn, gbcPanel2);
        gbcPanel2.gridx = 1;
        txtCheckIn = new JTextField();
        txtCheckIn.setPreferredSize(new Dimension(200, 25));
        txtCheckIn.setEditable(false); // Không cho phép nhập tay
        txtCheckIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDatePickerDialog(txtCheckIn, "Chọn ngày Check-in");
            }
        });
        jPanel2.add(txtCheckIn, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 4;
        JLabel lblCheckOut = new JLabel("CHECK-OUT (yyyy-MM-dd):");
        lblCheckOut.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblCheckOut, gbcPanel2);
        gbcPanel2.gridx = 1;
        txtCheckOut = new JTextField();
        txtCheckOut.setPreferredSize(new Dimension(200, 25));
        txtCheckOut.setEditable(false); // Không cho phép nhập tay
        txtCheckOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDatePickerDialog(txtCheckOut, "Chọn ngày Check-out");
            }
        });
        jPanel2.add(txtCheckOut, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 5;
        JLabel lblStatus = new JLabel("TRẠNG THÁI:");
        lblStatus.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblStatus, gbcPanel2);
        gbcPanel2.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"BOOKED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"});
        cbStatus.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(cbStatus, gbcPanel2);

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 6;
        JLabel lblNote = new JLabel("GHI CHÚ:");
        lblNote.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanel2.add(lblNote, gbcPanel2);
        gbcPanel2.gridx = 1;
        txtNote = new JTextField(20);
        txtNote.setPreferredSize(new Dimension(200, 25));
        jPanel2.add(txtNote, gbcPanel2);

        gbcPanel2.gridx = 0;
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
        btnAdd.addActionListener(e -> addBooking());
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
        cbFilterAttribute = new JComboBox<>(new String[]{"Trạng thái"});
        cbFilterAttribute.setPreferredSize(new Dimension(120, 25));

        cbFilterValue = new JComboBox<>();
        cbFilterValue.setPreferredSize(new Dimension(120, 25));
        cbFilterValue.addItem("Tất cả");
        cbFilterValue.addItem("BOOKED");
        cbFilterValue.addItem("CHECKED_IN");
        cbFilterValue.addItem("CHECKED_OUT");
        cbFilterValue.addItem("CANCELLED");
        cbFilterValue.setSelectedIndex(0);

        cbFilterAttribute.addActionListener(e -> filterTable());
        cbFilterValue.addActionListener(e -> filterTable());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblFilterAttribute);
        searchPanel.add(cbFilterAttribute);
        searchPanel.add(cbFilterValue);

        // Bảng hiển thị
        String[] columnNames = {"ID", "Khách Hàng", "Phòng", "Nhân Viên", "Check-in", "Check-out", "Trạng Thái", "Ghi Chú", "Ngày Tạo", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Cột "Action"
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            if (i != 6) { // Cột "Trạng Thái"
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ActionCellEditor());
        table.getColumnModel().getColumn(9).setPreferredWidth(80);

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

    // Phương thức hiển thị dialog chọn ngày
    private void showDatePickerDialog(JTextField targetField, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dialog.add(dateChooser, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSelect = new JButton("Chọn");
        JButton btnCancel = new JButton("Hủy");

        btnSelect.addActionListener(e -> {
            if (dateChooser.getDate() != null) {
                String selectedDate = dateFormat.format(dateChooser.getDate());
                targetField.setText(selectedDate);
            }
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSelect);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addBooking() {
        try {
            String customerName = (String) cbCustomer.getSelectedItem();
            String roomNum = (String) cbRoom.getSelectedItem();
            String employeeName = (String) cbEmployee.getSelectedItem();
            String checkIn = txtCheckIn.getText().trim();
            String checkOut = txtCheckOut.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String note = txtNote.getText().trim();

            if (customerName.equals("Chọn khách hàng") || roomNum.equals("Chọn phòng") || employeeName.equals("Chọn nhân viên") || checkIn.isEmpty() || checkOut.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Các trường khách hàng, phòng, nhân viên, check-in và check-out không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int customerId = customerIdMap.get(customerName);
            int roomId = roomIdMap.get(roomNum);
            int employeeId = employeeIdMap.get(employeeName);

            String sql = "INSERT INTO booking (customer_id, room_id, employee_id, check_in, check_out, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = dbConn.executeUpdate(sql,
                    customerId,
                    roomId,
                    employeeId,
                    java.sql.Date.valueOf(checkIn),
                    java.sql.Date.valueOf(checkOut),
                    status,
                    note.isEmpty() ? null : note
            );

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm đặt phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm đặt phòng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm đặt phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xem chi tiết!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedId = (int) tableModel.getValueAt(selectedRow, 0);
        ModelBooking selectedBooking = null;
        for (ModelBooking booking : bookingList) {
            if (booking.getId() == selectedId) {
                selectedBooking = booking;
                break;
            }
        }

        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đặt phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT ĐẶT PHÒNG", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"ID:", "Khách Hàng:", "Phòng:", "Nhân Viên:", "Check-in:", "Check-out:", "Trạng Thái:", "Ghi Chú:", "Ngày Tạo:"};
        String[] values = {
                String.valueOf(selectedBooking.getId()),
                selectedBooking.getCustomerName(),
                selectedBooking.getRoomName(),
                selectedBooking.getEmployeeName(),
                selectedBooking.getCheckIn().toString(),
                selectedBooking.getCheckOut().toString(),
                selectedBooking.getStatus(),
                selectedBooking.getNote() != null ? selectedBooking.getNote() : "",
                selectedBooking.getCreatedAt().toString()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            dialog.add(new JLabel(values[i]), gbc);
        }

        JButton btnClose = new JButton("ĐÓNG");
        btnClose.addActionListener(e -> dialog.dispose());
        gbc.gridx = 1;
        gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(btnClose, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void exportToExcel() {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách đặt phòng");

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
            fileDialog.setFile("Danh_sach_dat_phong.xls");
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
                } else {
                    JOptionPane.showMessageDialog(this, "Chỉ hỗ trợ định dạng .xls!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                rowIterator.next(); // Bỏ qua hàng tiêu đề

                String sql = "INSERT INTO booking (customer_id, room_id, employee_id, check_in, check_out, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
                int rowsAffected = 0;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        String customerName = row.getCell(0).getStringCellValue();
                        String roomNum = row.getCell(1).getStringCellValue();
                        String employeeName = row.getCell(2).getStringCellValue();
                        String checkIn = row.getCell(3).getStringCellValue();
                        String checkOut = row.getCell(4).getStringCellValue();
                        String status = row.getCell(5).getStringCellValue();
                        String note = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;

                        Integer customerId = customerIdMap.get(customerName);
                        Integer roomId = roomIdMap.get(roomNum);
                        Integer employeeId = employeeIdMap.get(employeeName);

                        if (customerId == null || roomId == null || employeeId == null) {
                            continue;
                        }

                        int rows = dbConn.executeUpdate(sql,
                                customerId, roomId, employeeId,
                                java.sql.Date.valueOf(checkIn),
                                java.sql.Date.valueOf(checkOut),
                                status,
                                note
                        );

                        if (rows > 0) {
                            rowsAffected++;
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dòng: " + e.getMessage());
                    }
                }

                workbook.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Nhập " + rowsAffected + " đặt phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    updateCards();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu nào được nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi nhập Excel: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        cbCustomer.setSelectedIndex(0);
        cbRoom.setSelectedIndex(0);
        cbEmployee.setSelectedIndex(0);
        txtCheckIn.setText("");
        txtCheckOut.setText("");
        cbStatus.setSelectedIndex(0);
        txtNote.setText("");
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
        int totalCount = 0, activeCount = 0, cancelledCount = 0, completedCount = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT status, COUNT(*) as count FROM booking GROUP BY status")) {
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                totalCount += count;
                switch (status) {
                    case "BOOKED":
                    case "CHECKED_IN":
                        activeCount += count;
                        break;
                    case "CANCELLED":
                        cancelledCount = count;
                        break;
                    case "CHECKED_OUT":
                        completedCount = count;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        card1.setData(new ModelCard("TỔNG ĐẶT PHÒNG", totalCount, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BOOK, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("ĐANG HOẠT ĐỘNG", activeCount, activeCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PLAY_ARROW, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card3.setData(new ModelCard("ĐÃ HỦY", cancelledCount, cancelledCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CANCEL, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card4.setData(new ModelCard("ĐÃ HOÀN TẤT", completedCount, completedCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editBooking(ModelBooking booking) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa đặt phòng", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JComboBox<String> editCustomer = new JComboBox<>();
        editCustomer.addItem("Chọn khách hàng");
        for (String customerName : customerMap.values()) {
            editCustomer.addItem(customerName);
        }
        editCustomer.setSelectedItem(booking.getCustomerName());

        JComboBox<String> editRoom = new JComboBox<>();
        editRoom.addItem("Chọn phòng");
        for (String roomNum : roomMap.values()) {
            editRoom.addItem(roomNum);
        }
        editRoom.setSelectedItem(booking.getRoomName());

        JComboBox<String> editEmployee = new JComboBox<>();
        editEmployee.addItem("Chọn nhân viên");
        for (String employeeName : employeeMap.values()) {
            editEmployee.addItem(employeeName);
        }
        editEmployee.setSelectedItem(booking.getEmployeeName());

        JTextField editCheckIn = new JTextField(booking.getCheckIn().toString());
        editCheckIn.setEditable(false);
        editCheckIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDatePickerDialog(editCheckIn, "Chọn ngày Check-in");
            }
        });

        JTextField editCheckOut = new JTextField(booking.getCheckOut().toString());
        editCheckOut.setEditable(false);
        editCheckOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDatePickerDialog(editCheckOut, "Chọn ngày Check-out");
            }
        });

        JComboBox<String> editStatus = new JComboBox<>(new String[]{"BOOKED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"});
        editStatus.setSelectedItem(booking.getStatus());
        JTextField editNote = new JTextField(booking.getNote() != null ? booking.getNote() : "", 20);

        String[] labels = {"Khách Hàng:", "Phòng:", "Nhân Viên:", "Check-in:", "Check-out:", "Trạng Thái:", "Ghi Chú:"};
        Component[] fields = {editCustomer, editRoom, editEmployee, editCheckIn, editCheckOut, editStatus, editNote};
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
                String customerName = (String) editCustomer.getSelectedItem();
                String roomNum = (String) editRoom.getSelectedItem();
                String employeeName = (String) editEmployee.getSelectedItem();
                String checkIn = editCheckIn.getText().trim();
                String checkOut = editCheckOut.getText().trim();
                String status = (String) editStatus.getSelectedItem();
                String note = editNote.getText().trim();

                if (customerName.equals("Chọn khách hàng") || roomNum.equals("Chọn phòng") || employeeName.equals("Chọn nhân viên") || checkIn.isEmpty() || checkOut.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Các trường khách hàng, phòng, nhân viên, check-in và check-out không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int customerId = customerIdMap.get(customerName);
                int roomId = roomIdMap.get(roomNum);
                int employeeId = employeeIdMap.get(employeeName);

                String sql = "UPDATE booking SET customer_id = ?, room_id = ?, employee_id = ?, check_in = ?, check_out = ?, status = ?, note = ? WHERE id = ?";
                int rowsAffected = dbConn.executeUpdate(sql,
                        customerId,
                        roomId,
                        employeeId,
                        java.sql.Date.valueOf(checkIn),
                        java.sql.Date.valueOf(checkOut),
                        status,
                        note.isEmpty() ? null : note,
                        booking.getId()
                );

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật đặt phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                    updateCards();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật đặt phòng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật đặt phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void deleteBooking(ModelBooking booking) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đặt phòng ID " + booking.getId() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM booking WHERE id = ?";
            int rowsAffected = dbConn.executeUpdate(sql, booking.getId());

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa đặt phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa đặt phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTableData() {
        String sql = "SELECT b.id, b.customer_id, c.full_name as customer_name, b.room_id, r.room_num as room_name, " +
                "b.employee_id, e.name as employee_name, b.check_in, b.check_out, b.status, b.note, b.created_at " +
                "FROM booking b " +
                "LEFT JOIN customer c ON b.customer_id = c.id " +
                "LEFT JOIN room r ON b.room_id = r.id " +
                "LEFT JOIN employee e ON b.employee_id = e.id";
        tableModel.setRowCount(0);
        bookingList.clear();

        EventAction<ModelBooking> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelBooking booking) throws SQLException {
                deleteBooking(booking);
            }

            @Override
            public void update(ModelBooking booking) {
                editBooking(booking);
            }

            @Override
            public void view(ModelBooking booking) {
                viewDetails();
            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelBooking booking = new ModelBooking(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getString("customer_name"),
                            rs.getInt("room_id"),
                            rs.getString("room_name"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getDate("check_in"),
                            rs.getDate("check_out"),
                            rs.getString("status"),
                            rs.getString("note"),
                            rs.getDate("created_at")
                    );
                    bookingList.add(booking);

                    Object[] row = {
                            booking.getId(),
                            booking.getCustomerName(),
                            booking.getRoomName(),
                            booking.getEmployeeName(),
                            booking.getCheckIn(),
                            booking.getCheckOut(),
                            booking.getStatus(),
                            booking.getNote(),
                            booking.getCreatedAt(),
                            new ModelAction<>(booking, eventAction)
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3));
        }

        if (filterValue != null && !filterValue.equals("Tất cả")) {
            int columnIndex = 6; // Cột "Trạng Thái"
            filters.add(RowFilter.regexFilter("(?i)^" + filterValue + "$", columnIndex));
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
                case "BOOKED":
                case "CHECKED_IN":
                    JLabel activeLabel = new JLabel();
                    activeLabel.setOpaque(true);
                    activeLabel.setBackground(Color.GREEN);
                    activeLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(activeLabel);
                    break;
                case "CANCELLED":
                    JLabel cancelledLabel = new JLabel();
                    cancelledLabel.setOpaque(true);
                    cancelledLabel.setBackground(Color.RED);
                    cancelledLabel.setPreferredSize(new Dimension(20, 20));
                    panel.add(cancelledLabel);
                    break;
                case "CHECKED_OUT":
                    JLabel completedLabel = new JLabel();
                    completedLabel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 20, Color.BLACK));
                    panel.add(completedLabel);
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
        private ModelAction<ModelBooking> action;

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
                this.action = (ModelAction<ModelBooking>) value;
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
        JFrame frame = new JFrame("QUẢN LÝ ĐẶT PHÒNG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 600));
        ScrQLBooking scrQLBooking = new ScrQLBooking();
        frame.add(scrQLBooking);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrQLBooking.dbConn.close();
            }
        });
    }
}