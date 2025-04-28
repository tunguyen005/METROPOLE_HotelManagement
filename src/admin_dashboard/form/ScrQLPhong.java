package admin_dashboard.form;

import admin_dashboard.component.Card;
import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import admin_dashboard.model.ModelCard;
import admin_dashboard.model.ModelRoom;
import admin_dashboard.swing.noticeboard.ModelNoticeBoard;
import admin_dashboard.swing.noticeboard.NoticeBoard;
import admin_dashboard.swing.table.EventAction;
import admin_dashboard.swing.table.ModelAction;
import admin_dashboard.swing.table.Table;
import screen_utils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ScrQLPhong extends JPanel {
    private NoticeBoard noticeBoard;
    private JTextField txtFloor, txtRoomNum, txtRoomType, txtSearch;
    private JTextArea txtDescription;
    private JComboBox<String> cbStatus, cbBedSize, cbFilterStatus;
    private JButton btnAdd, btnViewDetails, btnExportExcel, btnImportExcel, btnClear, btnExit;
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelRoom> roomList = new ArrayList<>();
    private Card card1, card2, card3, card4;

    public ScrQLPhong() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        initNoticeBoard();
        table.fixTable(jScrollPane1);
        loadTableData();
        updateCards();
    }

    private void initComponents() {
        JLabel jLabel1 = new JLabel("Dashboard / THÊM PHÒNG");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("PHÒNG TRỐNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ROOM, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("PHÒNG ĐÃ ĐẶT", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BOOK, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card3 = new Card();
        card3.setBackground(new Color(194, 85, 1));
        card3.setColorGradient(new Color(255, 212, 99));
        card3.setData(new ModelCard("TỔNG PHÒNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.HOTEL, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card4 = new Card();
        card4.setBackground(new Color(60, 195, 0));
        card4.setColorGradient(new Color(208, 255, 90));
        card4.setData(new ModelCard("BẢO TRÌ", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BUILD, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin phòng
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder("THÔNG TIN PHÒNG"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel jLabel5 = new JLabel("QUẢN LÝ PHÒNG");
        jLabel5.setFont(new Font("Calistoga", Font.BOLD, 15));
        jLabel5.setForeground(new Color(76, 76, 76));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        jPanel2.add(jLabel5, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTang = new JLabel("TẦNG:");
        lblTang.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblTang, gbc);
        txtFloor = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jPanel2.add(txtFloor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel lblSoPhong = new JLabel("SỐ PHÒNG:");
        lblSoPhong.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblSoPhong, gbc);
        txtRoomNum = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jPanel2.add(txtRoomNum, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel lblLoaiPhong = new JLabel("LOẠI PHÒNG:");
        lblLoaiPhong.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblLoaiPhong, gbc);
        txtRoomType = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jPanel2.add(txtRoomType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel lblTrangThai = new JLabel("TRẠNG THÁI:");
        lblTrangThai.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblTrangThai, gbc);
        cbStatus = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jPanel2.add(cbStatus, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        JLabel lblKichThuoc = new JLabel("KÍCH THƯỚC GIƯỜNG:");
        lblKichThuoc.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblKichThuoc, gbc);
        cbBedSize = new JComboBox<>(new String[]{"SINGLE", "DOUBLE", "QUEEN", "KING", "TWIN"});
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jPanel2.add(cbBedSize, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        JLabel lblMoTa = new JLabel("MÔ TẢ:");
        lblMoTa.setFont(new Font("Calistoga", Font.BOLD, 15));
        jPanel2.add(lblMoTa, gbc);
        txtDescription = new JTextArea(3, 10);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        jPanel2.add(txtDescription, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        btnClear = new JButton("CLEAR");
        btnClear.setBackground(new Color(255, 165, 0));
        btnClear.setForeground(Color.BLACK);
        btnClear.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnClear.addActionListener(e -> clearFields());
        jPanel2.add(btnClear, gbc);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("CỤM PHÍM CHỨC NĂNG"));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(300, 365));

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 10, 5, 10);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;

        btnAdd = new JButton("THÊM");
        btnAdd.setBackground(new Color(127, 255, 212));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnAdd.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ADD, 35, Color.BLACK));
        btnAdd.addActionListener(e -> addRoom());
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonPanel.add(btnAdd, btnGbc);

        btnViewDetails = new JButton("CHI TIẾT");
        btnViewDetails.setBackground(new Color(0, 102, 204));
        btnViewDetails.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PERM_DEVICE_INFORMATION, 35, Color.BLACK));
        btnViewDetails.addActionListener(e -> viewDetails());
        btnGbc.gridy = 1;
        buttonPanel.add(btnViewDetails, btnGbc);

        btnExportExcel = new JButton("XUẤT EXCEL");
        btnExportExcel.setBackground(new Color(16, 124, 65));
        btnExportExcel.setForeground(Color.BLACK);
        btnExportExcel.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExportExcel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.FILE_DOWNLOAD, 35, Color.BLACK));
        btnExportExcel.addActionListener(e -> exportToExcel());
        btnGbc.gridy = 2;
        buttonPanel.add(btnExportExcel, btnGbc);

        btnImportExcel = new JButton("NHẬP EXCEL");
        btnImportExcel.setBackground(new Color(16, 124, 65));
        btnImportExcel.setForeground(Color.BLACK);
        btnImportExcel.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnImportExcel.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.FILE_UPLOAD, 35, Color.BLACK));
        btnImportExcel.addActionListener(e -> importFromExcel());
        btnGbc.gridy = 3;
        buttonPanel.add(btnImportExcel, btnGbc);

        btnExit = new JButton("ĐĂNG XUẤT");
        btnExit.setBackground(new Color(255, 69, 0));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.OFFLINE_PIN, 35, Color.BLACK));
        btnExit.addActionListener(e -> logout());
        btnGbc.gridy = 4;
        btnGbc.weighty = 1.0;
        buttonPanel.add(btnExit, btnGbc);

        // Panel tìm kiếm và lọc
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(255, 255, 255));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel lblSearch = new JLabel("TÌM KIẾM:");
        lblSearch.setFont(new Font("Calistoga", Font.PLAIN, 14));
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        JLabel lblFilter = new JLabel("LỌC THEO TRẠNG THÁI:");
        lblFilter.setFont(new Font("Calistoga", Font.PLAIN, 14));
        cbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        cbFilterStatus.addActionListener(e -> filterTable());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblFilter);
        searchPanel.add(cbFilterStatus);

        // Thêm bảng hiển thị
        String[] columnNames = {"ID", "Tầng", "Số phòng", "Loại phòng", "Trạng thái", "Kích thước giường", "Mô tả", "Ngày tạo", "Ngày cập nhật", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(9).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ActionCellEditor());

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);

        // NoticeBoard panel
        JPanel jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(255, 255, 255));
        jPanel1.setPreferredSize(new Dimension(300, 150));
        noticeBoard = new NoticeBoard();

        JLabel jLabel2 = new JLabel("THÔNG BÁO HỆ THỐNG");
        jLabel2.setFont(new Font("Calistoga", Font.BOLD, 15));
        jLabel2.setForeground(new Color(76, 76, 76));
        jLabel2.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));

        JLabel jLabel3 = new JLabel("THÔNG BÁO MỚI");
        jLabel3.setFont(new Font("Calistoga", Font.PLAIN, 12));
        jLabel3.setForeground(new Color(105, 105, 105));
        jLabel3.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));

        JLabel jLabel4 = new JLabel();
        jLabel4.setOpaque(true);
        jLabel4.setBackground(new Color(200, 200, 200));

        GroupLayout noticePanelLayout = new GroupLayout(jPanel1);
        jPanel1.setLayout(noticePanelLayout);
        noticePanelLayout.setHorizontalGroup(
                noticePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, noticePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(noticePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(noticeBoard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, noticePanelLayout.createSequentialGroup()
                                                .addGroup(noticePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        noticePanelLayout.setVerticalGroup(
                noticePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, noticePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel3)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(noticeBoard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        // Bố cục chính với Responsive
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(card1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(card4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
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
                                                .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );

        setPreferredSize(new Dimension(1454, 768));
    }

    private void initNoticeBoard() {
        noticeBoard.addDate("04/10/2021");
        noticeBoard.addNoticeBoard(new ModelNoticeBoard(new Color(94, 49, 238), "Hidemode", "Now", "Sets the hide mode."));
        noticeBoard.addNoticeBoard(new ModelNoticeBoard(new Color(218, 49, 238), "Tag", "2h ago", "Tags metadata."));
        noticeBoard.scrollToTop();
    }

    private void addRoom() {
        String sql = "INSERT INTO room (floor, room_num, room_type, status, description, bed_size) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            int floor = Integer.parseInt(txtFloor.getText().trim());
            String roomNum = txtRoomNum.getText().trim();
            String roomType = txtRoomType.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String description = txtDescription.getText().trim();
            String bedSize = (String) cbBedSize.getSelectedItem();

            if (roomNum.isEmpty() || roomType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số phòng và loại phòng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rowsAffected = dbConn.executeUpdate(sql, floor, roomNum, roomType, status, description.isEmpty() ? null : description, bedSize);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadTableData();
                updateCards();
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Tầng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xem chi tiết!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT PHÒNG", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"ID:", "Tầng:", "Số phòng:", "Loại phòng:", "Trạng thái:", "Kích thước giường:", "Mô tả:", "Ngày tạo:", "Ngày cập nhật:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            dialog.add(new JLabel(tableModel.getValueAt(selectedRow, i).toString()), gbc);
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
            Sheet sheet = workbook.createSheet("Danh sách phòng");

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
            fileDialog.setFile("Danh_sach_phong.xls");
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

                String sql = "INSERT INTO room (floor, room_num, room_type, status, description, bed_size) VALUES (?, ?, ?, ?, ?, ?)";
                int rowsAffected = 0;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        int floor = (int) row.getCell(0).getNumericCellValue();
                        String roomNum = row.getCell(1).getStringCellValue();
                        String roomType = row.getCell(2).getStringCellValue();
                        String status = row.getCell(3).getStringCellValue();
                        String description = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null;
                        String bedSize = row.getCell(5).getStringCellValue();

                        if (roomNum.isEmpty() || roomType.isEmpty()) {
                            continue;
                        }

                        rowsAffected += dbConn.executeUpdate(sql, floor, roomNum, roomType, status, description, bedSize);
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dòng: " + e.getMessage());
                    }
                }

                workbook.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Nhập " + rowsAffected + " phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
        txtFloor.setText("");
        txtRoomNum.setText("");
        txtRoomType.setText("");
        cbStatus.setSelectedIndex(0);
        txtDescription.setText("");
        cbBedSize.setSelectedIndex(0);
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
        int availableCount = 0, occupiedCount = 0, maintenanceCount = 0, totalCount = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT status, COUNT(*) as count FROM room GROUP BY status")) {
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                totalCount += count;
                switch (status) {
                    case "AVAILABLE":
                        availableCount = count;
                        break;
                    case "OCCUPIED":
                        occupiedCount = count;
                        break;
                    case "MAINTENANCE":
                        maintenanceCount = count;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        card1.setData(new ModelCard("PHÒNG TRỐNG", availableCount, availableCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ROOM, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("PHÒNG ĐÃ ĐẶT", occupiedCount, occupiedCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BOOK, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card3.setData(new ModelCard("TỔNG PHÒNG", totalCount, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.HOTEL, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card4.setData(new ModelCard("BẢO TRÌ", maintenanceCount, maintenanceCount * 100 / (totalCount == 0 ? 1 : totalCount),
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BUILD, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editRoom(ModelRoom room) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa phòng", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField editFloor = new JTextField(String.valueOf(room.getFloor()), 15);
        JTextField editRoomNum = new JTextField(room.getRoomNum(), 15);
        JTextField editRoomType = new JTextField(room.getRoomType(), 15);
        JComboBox<String> editStatus = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        editStatus.setSelectedItem(room.getStatus());
        JComboBox<String> editBedSize = new JComboBox<>(new String[]{"SINGLE", "DOUBLE", "QUEEN", "KING", "TWIN"});
        editBedSize.setSelectedItem(room.getBedSize());
        JTextField editDescription = new JTextField(room.getDescription(), 15);

        String[] labels = {"Tầng:", "Số phòng:", "Loại phòng:", "Trạng thái:", "Kích thước giường:", "Mô tả:"};
        Component[] fields = {editFloor, editRoomNum, editRoomType, editStatus, editBedSize, editDescription};
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
                int floor = Integer.parseInt(editFloor.getText().trim());
                String roomNum = editRoomNum.getText().trim();
                String roomType = editRoomType.getText().trim();
                String status = (String) editStatus.getSelectedItem();
                String bedSize = (String) editBedSize.getSelectedItem();
                String description = editDescription.getText().trim();

                if (roomNum.isEmpty() || roomType.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Số phòng và loại phòng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sqlUpdate = "UPDATE room SET floor = ?, room_num = ?, room_type = ?, status = ?, bed_size = ?, description = ? WHERE id = ?";
                int rowsAffected = dbConn.executeUpdate(sqlUpdate, floor, roomNum, roomType, status, bedSize, description.isEmpty() ? null : description, room.getId());
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                    updateCards();
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Tầng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void deleteRoom(ModelRoom room) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phòng " + room.getRoomNum() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM room WHERE id = ?";
            int rowsAffected = dbConn.executeUpdate(sqlDelete, room.getId());
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTableData() {
        String sql = "SELECT id, floor, room_num, room_type, status, bed_size, description, created_at, updated_at FROM room";
        tableModel.setRowCount(0);
        roomList.clear();

        EventAction<ModelRoom> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelRoom room) throws SQLException {
                deleteRoom(room);
            }

            @Override
            public void update(ModelRoom room) {
                editRoom(room);
            }

            @Override
            public void view(ModelRoom room) {
                viewDetails();
            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelRoom room = new ModelRoom(
                            rs.getInt("id"),
                            rs.getInt("floor"),
                            rs.getString("room_num"),
                            rs.getString("room_type"),
                            rs.getString("status"),
                            rs.getString("bed_size"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                    roomList.add(room);
                    Object[] row = {
                            room.getId(),
                            room.getFloor(),
                            room.getRoomNum(),
                            room.getRoomType(),
                            room.getStatus(),
                            room.getBedSize(),
                            room.getDescription(),
                            room.getCreatedAt(),
                            room.getUpdatedAt(),
                            new ModelAction<>(room, eventAction)
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewRoomDetails(ModelRoom room) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT PHÒNG", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"ID:", "Tầng:", "Số phòng:", "Loại phòng:", "Trạng thái:", "Kích thước giường:", "Mô tả:", "Ngày tạo:", "Ngày cập nhật:"};
        String[] values = {
                String.valueOf(room.getId()),
                String.valueOf(room.getFloor()),
                room.getRoomNum(),
                room.getRoomType(),
                room.getStatus(),
                room.getBedSize(),
                room.getDescription() != null ? room.getDescription() : "",
                room.getCreatedAt() != null ? room.getCreatedAt().toString() : "",
                room.getUpdatedAt() != null ? room.getUpdatedAt().toString() : ""
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

    private void filterTable() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        String filterStatus = (String) cbFilterStatus.getSelectedItem();

        tableModel.setRowCount(0);
        EventAction<ModelRoom> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelRoom room) throws SQLException {
                deleteRoom(room);
            }

            @Override
            public void update(ModelRoom room) {
                editRoom(room);
            }

            @Override
            public void view(ModelRoom room) {
                viewRoomDetails(room);
            }
        };

        for (ModelRoom room : roomList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    room.getRoomNum().toLowerCase().contains(searchText) ||
                    room.getRoomType().toLowerCase().contains(searchText);
            boolean matchesStatus = filterStatus.equals("Tất cả") || room.getStatus().equals(filterStatus);

            if (matchesSearch && matchesStatus) {
                Object[] row = {
                        room.getId(),
                        room.getFloor(),
                        room.getRoomNum(),
                        room.getRoomType(),
                        room.getStatus(),
                        room.getBedSize(),
                        room.getDescription(),
                        room.getCreatedAt(),
                        room.getUpdatedAt(),
                        new ModelAction<>(room, eventAction)
                };
                tableModel.addRow(row);
            }
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
        private ModelAction<ModelRoom> action;

        private final JPanel panel;

        private final JButton btnEdit;
        private final JButton btnDelete;

        public ActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            btnEdit = new JButton();
            btnEdit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 13));
            btnEdit.setBackground(new Color(0, 102, 204));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setPreferredSize(new Dimension(20, 25));

            btnDelete = new JButton();
            btnDelete.setBackground(new Color(204, 0, 0));
            btnDelete.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 13));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setPreferredSize(new Dimension(20, 25));


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
                this.action = (ModelAction<ModelRoom>) value;
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
        JFrame frame = new JFrame("Thêm Phòng");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        ScrQLPhong scrThemPhong = new ScrQLPhong();
        frame.add(scrThemPhong);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrThemPhong.dbConn.close();
            }
        });
    }
}