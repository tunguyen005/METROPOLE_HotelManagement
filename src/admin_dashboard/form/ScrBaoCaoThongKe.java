package admin_dashboard.form;

import admin_dashboard.swing.icon.GoogleMaterialDesignIcons;
import admin_dashboard.swing.icon.IconFontSwing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import screen_utils.MySQLConn;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ScrBaoCaoThongKe extends JPanel {
    private MySQLConn dbConn;
    private JLabel lblTotalRooms, lblAvailableRooms, lblOccupiedRooms, lblMaintenanceRooms;
    private JLabel lblBooked, lblCheckedIn, lblCheckedOut, lblCancelled;
    private JLabel lblTotalRevenue;
    private ChartPanel barChartPanel; // Biến instance cho biểu đồ cột
    private ChartPanel pieChartPanel; // Biến instance cho biểu đồ tròn

    public ScrBaoCaoThongKe() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        loadStatistics();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel lblTitle = new JLabel("Dashboard / BÁO CÁO THỐNG KÊ");
        lblTitle.setFont(new Font("Calistoga", Font.BOLD, 12));
        lblTitle.setForeground(new Color(4, 72, 210));

        // Panel cho các card thống kê
        JPanel cardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        cardPanel.setBackground(new Color(255, 255, 255));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Card: Tổng số phòng
        lblTotalRooms = createCard("Tổng Số Phòng", Color.decode("#4CAF50"));
        cardPanel.add(lblTotalRooms);

        // Card: Phòng trống
        lblAvailableRooms = createCard("Phòng Trống", Color.decode("#2196F3"));
        cardPanel.add(lblAvailableRooms);

        // Card: Phòng đang sử dụng
        lblOccupiedRooms = createCard("Phòng Đang Sử Dụng", Color.decode("#FF9800"));
        cardPanel.add(lblOccupiedRooms);

        // Card: Phòng bảo trì
        lblMaintenanceRooms = createCard("Phòng Bảo Trì", Color.decode("#F44336"));
        cardPanel.add(lblMaintenanceRooms);

        // Card: Đặt phòng (BOOKED)
        lblBooked = createCard("Đặt Phòng (BOOKED)", Color.decode("#9C27B0"));
        cardPanel.add(lblBooked);

        // Card: Đã Check-in
        lblCheckedIn = createCard("Đã Check-in", Color.decode("#3F51B5"));
        cardPanel.add(lblCheckedIn);

        // Card: Đã Check-out
        lblCheckedOut = createCard("Đã Check-out", Color.decode("#009688"));
        cardPanel.add(lblCheckedOut);

        // Card: Đã Hủy
        lblCancelled = createCard("Đã Hủy", Color.decode("#607D8B"));
        cardPanel.add(lblCancelled);

        // Card: Tổng doanh thu
        lblTotalRevenue = createCard("Tổng Doanh Thu", Color.decode("#FF5722"));
        cardPanel.add(lblTotalRevenue);

        // Panel cho biểu đồ
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartPanel.setBackground(new Color(255, 255, 255));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Biểu đồ cột: Số lượng đặt phòng theo trạng thái
        DefaultCategoryDataset bookingDataset = new DefaultCategoryDataset();
        bookingDataset.addValue(0, "Số lượng", "BOOKED");
        bookingDataset.addValue(0, "Số lượng", "CHECKED_IN");
        bookingDataset.addValue(0, "Số lượng", "CHECKED_OUT");
        bookingDataset.addValue(0, "Số lượng", "CANCELLED");
        JFreeChart barChart = ChartFactory.createBarChart(
                "Số Lượng Đặt Phòng Theo Trạng Thái",
                "Trạng Thái",
                "Số Lượng",
                bookingDataset
        );
        barChartPanel = new ChartPanel(barChart);
        barChartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.add(barChartPanel);

        // Biểu đồ tròn: Tỷ lệ phòng theo trạng thái
        DefaultPieDataset roomDataset = new DefaultPieDataset();
        roomDataset.setValue("AVAILABLE", 0);
        roomDataset.setValue("OCCUPIED", 0);
        roomDataset.setValue("MAINTENANCE", 0);
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Tỷ Lệ Phòng Theo Trạng Thái",
                roomDataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("AVAILABLE", Color.decode("#2196F3"));
        plot.setSectionPaint("OCCUPIED", Color.decode("#FF9800"));
        plot.setSectionPaint("MAINTENANCE", Color.decode("#F44336"));
        pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.add(pieChartPanel);

        // Bố cục chính
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblTitle)
                                        .addComponent(cardPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(chartPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblTitle)
                                .addGap(18, 18, 18)
                                .addComponent(cardPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        setPreferredSize(new Dimension(1454, 768));
    }

    private JLabel createCard(String title, Color color) {
        JLabel card = new JLabel();
        card.setOpaque(true);
        card.setBackground(color);
        card.setForeground(Color.WHITE);
        card.setFont(new Font("Calistoga", Font.PLAIN, 14));
        card.setHorizontalAlignment(SwingConstants.CENTER);
        card.setVerticalAlignment(SwingConstants.CENTER);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setText("<html><center>" + title + "<br>0</center></html>");
        card.setPreferredSize(new Dimension(200, 100));
        return card;
    }

    private void loadStatistics() {
        try {
            // Thống kê phòng
            ResultSet rsRooms = dbConn.executeQuery("SELECT status, COUNT(*) as count FROM room GROUP BY status");
            int totalRooms = 0, availableRooms = 0, occupiedRooms = 0, maintenanceRooms = 0;
            while (rsRooms.next()) {
                String status = rsRooms.getString("status");
                int count = rsRooms.getInt("count");
                totalRooms += count;
                switch (status) {
                    case "AVAILABLE":
                        availableRooms = count;
                        break;
                    case "OCCUPIED":
                        occupiedRooms = count;
                        break;
                    case "MAINTENANCE":
                        maintenanceRooms = count;
                        break;
                    default:
                        break;
                }
            }
            rsRooms.close();

            lblTotalRooms.setText("<html><center>Tổng Số Phòng<br>" + totalRooms + "</center></html>");
            lblAvailableRooms.setText("<html><center>Phòng Trống<br>" + availableRooms + "</center></html>");
            lblOccupiedRooms.setText("<html><center>Phòng Đang Sử Dụng<br>" + occupiedRooms + "</center></html>");
            lblMaintenanceRooms.setText("<html><center>Phòng Bảo Trì<br>" + maintenanceRooms + "</center></html>");

            // Thống kê đặt phòng
            ResultSet rsBookings = dbConn.executeQuery("SELECT status, COUNT(*) as count FROM booking GROUP BY status");
            int booked = 0, checkedIn = 0, checkedOut = 0, cancelled = 0;
            while (rsBookings.next()) {
                String status = rsBookings.getString("status");
                int count = rsBookings.getInt("count");
                switch (status) {
                    case "BOOKED":
                        booked = count;
                        break;
                    case "CHECKED_IN":
                        checkedIn = count;
                        break;
                    case "CHECKED_OUT":
                        checkedOut = count;
                        break;
                    case "CANCELLED":
                        cancelled = count;
                        break;
                    default:
                        break;
                }
            }
            rsBookings.close();

            lblBooked.setText("<html><center>Đặt Phòng (BOOKED)<br>" + booked + "</center></html>");
            lblCheckedIn.setText("<html><center>Đã Check-in<br>" + checkedIn + "</center></html>");
            lblCheckedOut.setText("<html><center>Đã Check-out<br>" + checkedOut + "</center></html>");
            lblCancelled.setText("<html><center>Đã Hủy<br>" + cancelled + "</center></html>");

            // Thống kê doanh thu
            ResultSet rsRevenue = dbConn.executeQuery("SELECT SUM(total_amount) as total FROM invoice");
            double totalRevenue = 0;
            if (rsRevenue.next() && rsRevenue.getObject("total") != null) {
                totalRevenue = rsRevenue.getDouble("total");
            }
            rsRevenue.close();

            DecimalFormat df = new DecimalFormat("#,### VNĐ");
            lblTotalRevenue.setText("<html><center>Tổng Doanh Thu<br>" + df.format(totalRevenue) + "</center></html>");

            // Cập nhật biểu đồ cột
            DefaultCategoryDataset bookingDataset = new DefaultCategoryDataset();
            bookingDataset.addValue(booked, "Số lượng", "BOOKED");
            bookingDataset.addValue(checkedIn, "Số lượng", "CHECKED_IN");
            bookingDataset.addValue(checkedOut, "Số lượng", "CHECKED_OUT");
            bookingDataset.addValue(cancelled, "Số lượng", "CANCELLED");
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Số Lượng Đặt Phòng Theo Trạng Thái",
                    "Trạng Thái",
                    "Số Lượng",
                    bookingDataset
            );
            barChartPanel.setChart(barChart);

            // Cập nhật biểu đồ tròn
            DefaultPieDataset roomDataset = new DefaultPieDataset();
            roomDataset.setValue("AVAILABLE", availableRooms > 0 ? availableRooms : 0.1); // Tránh giá trị 0 để biểu đồ hiển thị
            roomDataset.setValue("OCCUPIED", occupiedRooms > 0 ? occupiedRooms : 0.1);
            roomDataset.setValue("MAINTENANCE", maintenanceRooms > 0 ? maintenanceRooms : 0.1);
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Tỷ Lệ Phòng Theo Trạng Thái",
                    roomDataset,
                    true,
                    true,
                    false
            );
            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setSectionPaint("AVAILABLE", Color.decode("#2196F3"));
            plot.setSectionPaint("OCCUPIED", Color.decode("#FF9800"));
            plot.setSectionPaint("MAINTENANCE", Color.decode("#F44336"));
            pieChartPanel.setChart(pieChart);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Đảm bảo đóng kết nối nếu có lỗi
            if (dbConn != null) {
                dbConn.close();
            }
        }
    }

    public MySQLConn getDbConn() {
        return dbConn;
    }

}