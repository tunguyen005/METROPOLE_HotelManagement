package admin_dashboard.form;

import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import admin_dashboard.swing.scrollbar.ScrollBarCustom;
import admin_dashboard.swing.table.Table;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import screen_utils.MySQLConn;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrCheckIn extends JPanel {
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<ModelBooking> bookingList = new ArrayList<>();
    private JButton btnCheckIn, btnBack, btnExportReport;

    // ModelBooking class
    public static class ModelBooking {
        private int id;
        private String customerName, roomName, employeeName;
        private java.sql.Date checkIn, checkOut;
        private String status;

        public ModelBooking(int id, String customerName, String roomName, String employeeName,
                            java.sql.Date checkIn, java.sql.Date checkOut, String status) {
            this.id = id;
            this.customerName = customerName;
            this.roomName = roomName;
            this.employeeName = employeeName;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.status = status;
        }

        // Getters
        public int getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getRoomName() { return roomName; }
        public String getEmployeeName() { return employeeName; }
        public java.sql.Date getCheckIn() { return checkIn; }
        public java.sql.Date getCheckOut() { return checkOut; }
        public String getStatus() { return status; }
    }

    public ScrCheckIn() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        table.fixTable(jScrollPane1);
        loadTableData();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / CHECK-IN");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Bảng hiển thị
        String[] columnNames = {"ID", "Khách Hàng", "Phòng", "Nhân Viên", "Check-in", "Check-out", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new Table();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(i).setPreferredWidth(120);
        }

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setVerticalScrollBar(new ScrollBarCustom());
        jScrollPane1.setHorizontalScrollBar(new ScrollBarCustom());
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new Dimension(800, 400));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnCheckIn = new JButton("CHECK-IN");
        btnCheckIn.setBackground(new Color(0, 204, 102));
        btnCheckIn.setForeground(Color.BLACK);
        btnCheckIn.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnCheckIn.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CHECK_CIRCLE, 35, Color.BLACK));
        btnCheckIn.addActionListener(e -> performCheckIn());
        buttonPanel.add(btnCheckIn);

        btnBack = new JButton("QUAY LẠI");
        btnBack.setBackground(new Color(255, 165, 0));
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnBack.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ARROW_BACK, 35, Color.BLACK));
        btnBack.addActionListener(e -> goBack());
        buttonPanel.add(btnBack);

        btnExportReport = new JButton("XUẤT BÁO CÁO");
        btnExportReport.setBackground(new Color(66, 133, 244));
        btnExportReport.setForeground(Color.BLACK);
        btnExportReport.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExportReport.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.REPORT, 35, Color.BLACK));
        btnExportReport.addActionListener(e -> exportReport());
        buttonPanel.add(btnExportReport);

        // Bố cục chính
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(jLabel1)
                                        .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(buttonPanel))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(buttonPanel)
                                .addContainerGap())
        );

        setPreferredSize(new Dimension(1454, 768));
    }

    private void loadTableData() {
        String sql = "SELECT b.id, c.full_name as customer_name, r.room_num as room_name, e.name as employee_name, " +
                "b.check_in, b.check_out, b.status " +
                "FROM booking b " +
                "LEFT JOIN customer c ON b.customer_id = c.id " +
                "LEFT JOIN room r ON b.room_id = r.id " +
                "LEFT JOIN employee e ON b.employee_id = e.id " +
                "WHERE b.status = 'BOOKED'";
        tableModel.setRowCount(0);
        bookingList.clear();

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            while (rs.next()) {
                ModelBooking booking = new ModelBooking(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("room_name"),
                        rs.getString("employee_name"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("status")
                );
                bookingList.add(booking);

                Object[] row = {
                        booking.getId(),
                        booking.getCustomerName(),
                        booking.getRoomName(),
                        booking.getEmployeeName(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performCheckIn() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt phòng để check-in!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            String sql = "UPDATE booking SET status = 'CHECKED_IN' WHERE id = ? AND status = 'BOOKED'";
            int rowsAffected = dbConn.executeUpdate(sql, selectedId);

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Check-in thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể check-in. Đặt phòng có thể đã được cập nhật trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thực hiện check-in: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dbConn.close();
        ScrDashboard dashboard = (ScrDashboard) SwingUtilities.getWindowAncestor(this);
        if (dashboard != null) {
            dashboard.showForm(new ScrQLBooking());
        }
    }

    private void exportReport() {
        if (bookingList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất báo cáo!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Tải file JRXML từ resources
            InputStream reportStream = getClass().getResourceAsStream("/reports/CheckInReport.jrxml");
            if (reportStream == null) {
                throw new IOException("Không tìm thấy file CheckInReport.jrxml trong resources");
            }

            // Biên dịch JRXML thành JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Chuẩn bị dữ liệu
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bookingList);

            // Tham số (nếu cần)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_LOCALE", java.util.Locale.getDefault());

            // Tạo báo cáo
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Hiển thị dialog để chọn định dạng xuất
            String[] options = {"PDF", "Excel"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Chọn định dạng báo cáo:",
                    "Xuất Báo Cáo",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // Xuất báo cáo
            if (choice == 0) { // PDF
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("CheckIn_Report.pdf"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile());
                    try {
                        JasperExportManager.exportReportToPdfStream(jasperPrint, fos);
                        fos.flush();
                        JOptionPane.showMessageDialog(this, "Xuất báo cáo PDF thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        fos.close();
                    }
                }
            } else if (choice == 1) { // Excel
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("CheckIn_Report.xlsx"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile());
                    try {
                        JRXlsxExporter exporter = new JRXlsxExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
                        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                        configuration.setOnePagePerSheet(false);
                        configuration.setRemoveEmptySpaceBetweenRows(true);
                        exporter.setConfiguration(configuration);
                        exporter.exportReport();
                        fos.flush();
                        JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        fos.close();
                    }
                }
            }
        } catch (JRException | IOException e) {
            System.err.println("Lỗi khi xuất báo cáo: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public MySQLConn getDbConn() {
        return dbConn;
    }
}