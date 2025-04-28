package admin_dashboard.form;

import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import admin_dashboard.swing.scrollbar.ScrollBarCustom;
import admin_dashboard.swing.table.Table;
import screen_utils.MySQLConn;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ScrQLThanhToan extends JPanel {
    private MySQLConn dbConn;
    private Table table;
    private DefaultTableModel tableModel;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private List<InvoicePayment> invoiceList = new ArrayList<>();
    private JButton btnPay, btnBack;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private JLabel lblNoData;

    // Lớp lưu trữ thông tin hóa đơn và thanh toán
    public static class InvoicePayment {
        private int invoiceId, bookingId;
        private double totalAmount, amountPaid;
        private String paymentStatus, paymentMethod;
        private java.sql.Timestamp createdAt;
        private String customerName;
        private String roomNum;
        private java.sql.Date checkIn, checkOut;

        public InvoicePayment(int invoiceId, int bookingId, double totalAmount, double amountPaid,
                              String paymentStatus, String paymentMethod, java.sql.Timestamp createdAt,
                              String customerName, String roomNum, java.sql.Date checkIn, java.sql.Date checkOut) {
            this.invoiceId = invoiceId;
            this.bookingId = bookingId;
            this.totalAmount = totalAmount;
            this.amountPaid = amountPaid;
            this.paymentStatus = paymentStatus;
            this.paymentMethod = paymentMethod;
            this.createdAt = createdAt;
            this.customerName = customerName;
            this.roomNum = roomNum;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public int getInvoiceId() { return invoiceId; }
        public int getBookingId() { return bookingId; }
        public double getTotalAmount() { return totalAmount; }
        public double getAmountPaid() { return amountPaid; }
        public String getPaymentStatus() { return paymentStatus; }
        public String getPaymentMethod() { return paymentMethod; }
        public java.sql.Timestamp getCreatedAt() { return createdAt; }
        public String getCustomerName() { return customerName; }
        public String getRoomNum() { return roomNum; }
        public java.sql.Date getCheckIn() { return checkIn; }
        public java.sql.Date getCheckOut() { return checkOut; }
    }

    public ScrQLThanhToan() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        table.fixTable(jScrollPane1);
        loadTableData();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ THANH TOÁN");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Label thông báo không có dữ liệu
        lblNoData = new JLabel("Không có dữ liệu hóa đơn để hiển thị.");
        lblNoData.setFont(new Font("Calistoga", Font.PLAIN, 14));
        lblNoData.setForeground(Color.GRAY);
        lblNoData.setHorizontalAlignment(SwingConstants.CENTER);
        lblNoData.setVisible(false);

        // Bảng hiển thị
        String[] columnNames = {
                "ID Hóa Đơn", "Tên Khách Hàng", "Số Phòng", "Ngày Check-in", "Ngày Check-out",
                "Tổng Tiền", "Đã Thanh Toán", "Trạng Thái", "Phương Thức", "Ngày Tạo"
        };
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
            table.getColumnModel().getColumn(i).setPreferredWidth(i < 2 ? 150 : 120);
        }

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setVerticalScrollBar(new ScrollBarCustom());
        jScrollPane1.setHorizontalScrollBar(new ScrollBarCustom());
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new Dimension(1000, 400));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.add(jScrollPane1, BorderLayout.CENTER);
        tablePanel.add(lblNoData, BorderLayout.SOUTH);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnPay = new JButton("THANH TOÁN");
        btnPay.setBackground(new Color(0, 204, 102));
        btnPay.setForeground(Color.BLACK);
        btnPay.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnPay.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PAYMENT, 35, Color.BLACK));
        btnPay.addActionListener(e -> performPayment());
        buttonPanel.add(btnPay);

        btnBack = new JButton("QUAY LẠI");
        btnBack.setBackground(new Color(255, 165, 0));
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnBack.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ARROW_BACK, 35, Color.BLACK));
        btnBack.addActionListener(e -> goBack());
        buttonPanel.add(btnBack);

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
        String sql = "SELECT i.id as invoice_id, i.booking_id, i.total_amount, " +
                "COALESCE(SUM(p.amount_paid), 0) as amount_paid, " +
                "COALESCE(MAX(p.status), 'PENDING') as payment_status, " +
                "MAX(p.payment_method) as payment_method, " +
                "i.created_at, " +
                "c.full_name as customer_name, " +
                "r.room_num, " +
                "b.check_in, " +
                "b.check_out " +
                "FROM invoice i " +
                "LEFT JOIN payment p ON i.id = p.invoice_id " +
                "LEFT JOIN booking b ON i.booking_id = b.id " +
                "LEFT JOIN customer c ON b.customer_id = c.id " +
                "LEFT JOIN room r ON b.room_id = r.id " +
                "GROUP BY i.id, i.booking_id, i.total_amount, i.created_at, " +
                "c.full_name, r.room_num, b.check_in, b.check_out";
        tableModel.setRowCount(0);
        invoiceList.clear();

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                InvoicePayment invoice = new InvoicePayment(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("created_at"),
                        rs.getString("customer_name"),
                        rs.getString("room_num"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out")
                );
                invoiceList.add(invoice);

                Object[] row = {
                        invoice.getInvoiceId(),
                        invoice.getCustomerName() != null ? invoice.getCustomerName() : "N/A",
                        invoice.getRoomNum() != null ? invoice.getRoomNum() : "N/A",
                        invoice.getCheckIn(),
                        invoice.getCheckOut(),
                        df.format(invoice.getTotalAmount()),
                        df.format(invoice.getAmountPaid()),
                        invoice.getPaymentStatus(),
                        invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : "Chưa thanh toán",
                        invoice.getCreatedAt()
                };
                tableModel.addRow(row);
            }

            if (rowCount == 0) {
                lblNoData.setVisible(true);
            } else {
                lblNoData.setVisible(false);
            }

            tableModel.fireTableDataChanged();
            table.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performPayment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để thanh toán!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InvoicePayment selectedInvoice = invoiceList.get(selectedRow);
        if (selectedInvoice.getPaymentStatus().equals("PAID")) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán hoàn toàn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double remainingAmount = selectedInvoice.getTotalAmount() - selectedInvoice.getAmountPaid();
        JTextField amountField = new JTextField(String.format("%.0f", remainingAmount));
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"CASH", "CARD", "BANK_TRANSFER", "MOMO", "ZALO_PAY", "OTHER"});
        Object[] message = {
                "Số tiền còn lại: " + df.format(remainingAmount),
                "Số tiền thanh toán:", amountField,
                "Phương thức thanh toán:", methodCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thanh Toán Hóa Đơn", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountField.getText());
            if (amountPaid <= 0 || amountPaid > remainingAmount) {
                JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentMethod = (String) methodCombo.getSelectedItem();
        String status = (amountPaid == remainingAmount) ? "PAID" : "PARTIAL";

        try {
            String sql = "INSERT INTO payment (invoice_id, amount_paid, payment_method, status) VALUES (?, ?, ?, ?)";
            int rowsAffected = dbConn.executeUpdate(sql, selectedInvoice.getInvoiceId(), amountPaid, paymentMethod, status);

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thực hiện thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dbConn.close();
        ScrDashboard dashboard = (ScrDashboard) SwingUtilities.getWindowAncestor(this);
        if (dashboard != null) {
            dashboard.showForm(new ScrQLBooking());
        }
    }

    public MySQLConn getDbConn() {
        return dbConn;
    }

    
}