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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import screen_utils.ScrLogin;

public class ScrQLDichVu extends JPanel {
    private JTextField txtServiceName, txtServiceDescription, txtServicePrice, txtServiceSearch;
    private JTextField txtUsageBookingId, txtUsageSearch;
    private JSpinner spnUsageQuantity;
    private JList<String> lstServices; // JList for multiple service selection
    private DefaultListModel<String> serviceListModel;
    private JButton btnAddService, btnClearService, btnAddUsage, btnClearUsage, btnExportServiceReport, btnExportUsageReport, btnExit;
    private MySQLConn dbConn;
    private Table tableService, tableUsage;
    private DefaultTableModel tableModelService, tableModelUsage;
    private JScrollPane jScrollPaneService = new JScrollPane();
    private JScrollPane jScrollPaneUsage = new JScrollPane();
    private List<ModelService> serviceList = new ArrayList<>();
    private List<ModelServiceUsage> usageList = new ArrayList<>();
    private Card card1, card2;
    private Map<String, Integer> serviceNameToIdMap = new HashMap<>(); // Map service names to IDs

    private static class ModelService {
        private int id;
        private String name, description;
        private float price;

        public ModelService(int id, String name, String description, float price) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public float getPrice() { return price; }
    }

    private static class ModelServiceUsage {
        private int id, bookingId, serviceId, quantity;
        private Timestamp usageDate;

        public ModelServiceUsage(int id, int bookingId, int serviceId, int quantity, Timestamp usageDate) {
            this.id = id;
            this.bookingId = bookingId;
            this.serviceId = serviceId;
            this.quantity = quantity;
            this.usageDate = usageDate;
        }

        public int getId() { return id; }
        public int getBookingId() { return bookingId; }
        public int getServiceId() { return serviceId; }
        public int getQuantity() { return quantity; }
        public Timestamp getUsageDate() { return usageDate; }
    }

    public ScrQLDichVu() {
        dbConn = new MySQLConn();
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initComponents();
        setOpaque(false);
        tableService.fixTable(jScrollPaneService);
        tableUsage.fixTable(jScrollPaneUsage);
        loadServiceTableData();
        loadUsageTableData();
        loadServiceList(); // Load services into JList
        updateCards();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel jLabel1 = new JLabel("Dashboard / QUẢN LÝ DỊCH VỤ");
        jLabel1.setFont(new Font("Calistoga", Font.BOLD, 12));
        jLabel1.setForeground(new Color(4, 72, 210));

        // Cards
        card1 = new Card();
        card1.setBackground(new Color(238, 130, 238));
        card1.setColorGradient(new Color(211, 28, 215));
        card1.setData(new ModelCard("TỔNG DỊCH VỤ", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SPA, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        card2 = new Card();
        card2.setBackground(new Color(10, 30, 214));
        card2.setColorGradient(new Color(72, 111, 252));
        card2.setData(new ModelCard("TỔNG LƯỢT SỬ DỤNG", 0, 0, IconFontSwing.buildIcon(GoogleMaterialDesignIcons.HISTORY, 40, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));

        // Panel nhập thông tin dịch vụ
        JPanel jPanelService = new JPanel();
        jPanelService.setBackground(new Color(255, 255, 255));
        jPanelService.setLayout(new GridBagLayout());
        jPanelService.setBorder(BorderFactory.createTitledBorder("THÔNG TIN DỊCH VỤ"));
        GridBagConstraints gbcService = new GridBagConstraints();
        gbcService.insets = new Insets(5, 10, 5, 10);
        gbcService.fill = GridBagConstraints.HORIZONTAL;
        jPanelService.setPreferredSize(new Dimension(600, 200));

        gbcService.gridx = 0;
        gbcService.gridy = 0;
        JLabel lblServiceName = new JLabel("TÊN DỊCH VỤ:");
        lblServiceName.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelService.add(lblServiceName, gbcService);
        gbcService.gridx = 1;
        txtServiceName = new JTextField(20);
        txtServiceName.setPreferredSize(new Dimension(250, 25));
        jPanelService.add(txtServiceName, gbcService);

        gbcService.gridx = 0;
        gbcService.gridy = 1;
        JLabel lblServiceDescription = new JLabel("MÔ TẢ:");
        lblServiceDescription.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelService.add(lblServiceDescription, gbcService);
        gbcService.gridx = 1;
        txtServiceDescription = new JTextField(25);
        txtServiceDescription.setPreferredSize(new Dimension(250, 25));
        jPanelService.add(txtServiceDescription, gbcService);

        gbcService.gridx = 0;
        gbcService.gridy = 2;
        JLabel lblServicePrice = new JLabel("GIÁ (VNĐ):");
        lblServicePrice.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelService.add(lblServicePrice, gbcService);
        gbcService.gridx = 1;
        txtServicePrice = new JTextField(15);
        txtServicePrice.setPreferredSize(new Dimension(250, 25));
        jPanelService.add(txtServicePrice, gbcService);

        gbcService.gridx = 2;
        gbcService.gridy = 0;
        gbcService.gridwidth = 2;
        gbcService.fill = GridBagConstraints.NONE;
        gbcService.anchor = GridBagConstraints.CENTER;
        btnAddService = new JButton("THÊM");
        btnAddService.setBackground(new Color(127, 255, 212));
        btnAddService.setForeground(Color.BLACK);
        btnAddService.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnAddService.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ADD, 20, Color.BLACK));
        btnAddService.addActionListener(e -> addService());
        jPanelService.add(btnAddService, gbcService);

        gbcService.gridx = 2;
        gbcService.gridy = 1;
        btnClearService = new JButton("CLEAR");
        btnClearService.setBackground(new Color(255, 165, 0));
        btnClearService.setForeground(Color.BLACK);
        btnClearService.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnClearService.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CLEAR, 20, Color.BLACK));
        btnClearService.addActionListener(e -> clearServiceFields());
        jPanelService.add(btnClearService, gbcService);

        // Panel nhập thông tin sử dụng dịch vụ
        JPanel jPanelUsage = new JPanel();
        jPanelUsage.setBackground(new Color(255, 255, 255));
        jPanelUsage.setLayout(new GridBagLayout());
        jPanelUsage.setBorder(BorderFactory.createTitledBorder("SỬ DỤNG DỊCH VỤ"));
        GridBagConstraints gbcUsage = new GridBagConstraints();
        gbcUsage.insets = new Insets(5, 10, 5, 10);
        gbcUsage.fill = GridBagConstraints.HORIZONTAL;

        gbcUsage.gridx = 0;
        gbcUsage.gridy = 0;
        JLabel lblUsageBookingId = new JLabel("MÃ ĐẶT PHÒNG:");
        lblUsageBookingId.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelUsage.add(lblUsageBookingId, gbcUsage);
        gbcUsage.gridx = 1;
        txtUsageBookingId = new JTextField(15);
        txtUsageBookingId.setPreferredSize(new Dimension(250, 25));
        jPanelUsage.add(txtUsageBookingId, gbcUsage);

        gbcUsage.gridx = 0;
        gbcUsage.gridy = 1;
        JLabel lblUsageServiceId = new JLabel("CHỌN DỊCH VỤ:");
        lblUsageServiceId.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelUsage.add(lblUsageServiceId, gbcUsage);
        gbcUsage.gridx = 1;
        serviceListModel = new DefaultListModel<>();
        lstServices = new JList<>(serviceListModel);
        lstServices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane serviceScrollPane = new JScrollPane(lstServices);
        serviceScrollPane.setPreferredSize(new Dimension(250, 60));
        jPanelUsage.add(serviceScrollPane, gbcUsage);

        gbcUsage.gridx = 0;
        gbcUsage.gridy = 2;
        JLabel lblUsageQuantity = new JLabel("SỐ LƯỢNG:");
        lblUsageQuantity.setFont(new Font("Calistoga", Font.BOLD, 14));
        jPanelUsage.add(lblUsageQuantity, gbcUsage);
        gbcUsage.gridx = 1;
        spnUsageQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spnUsageQuantity.setPreferredSize(new Dimension(250, 25));
        jPanelUsage.add(spnUsageQuantity, gbcUsage);

        gbcUsage.gridx = 2;
        gbcUsage.gridy = 0;
        gbcUsage.gridwidth = 2;
        gbcUsage.fill = GridBagConstraints.NONE;
        gbcUsage.anchor = GridBagConstraints.CENTER;
        btnAddUsage = new JButton("THÊM");
        btnAddUsage.setBackground(new Color(127, 255, 212));
        btnAddUsage.setForeground(Color.BLACK);
        btnAddUsage.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnAddUsage.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ADD, 20, Color.BLACK));
        btnAddUsage.addActionListener(e -> addServiceUsage());
        jPanelUsage.add(btnAddUsage, gbcUsage);

        gbcUsage.gridx = 2;
        gbcUsage.gridy = 1;
        btnClearUsage = new JButton("CLEAR");
        btnClearUsage.setBackground(new Color(255, 165, 0));
        btnClearUsage.setForeground(Color.BLACK);
        btnClearUsage.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnClearUsage.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CLEAR, 20, Color.BLACK));
        btnClearUsage.addActionListener(e -> clearUsageFields());
        jPanelUsage.add(btnClearUsage, gbcUsage);

        // Panel nhập liệu tổng
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(255, 255, 255));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(jPanelService, BorderLayout.NORTH);
        inputPanel.add(jPanelUsage, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 5));
        buttonPanel.setPreferredSize(new Dimension(510, 50));

        btnExportServiceReport = new JButton("XUẤT BÁO CÁO DỊCH VỤ");
        btnExportServiceReport.setBackground(new Color(0, 128, 0));
        btnExportServiceReport.setForeground(Color.WHITE);
        btnExportServiceReport.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExportServiceReport.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.REPORT, 35, Color.WHITE));
        btnExportServiceReport.addActionListener(e -> exportServiceReport());
        buttonPanel.add(btnExportServiceReport);

        btnExportUsageReport = new JButton("XUẤT BÁO CÁO SỬ DỤNG");
        btnExportUsageReport.setBackground(new Color(0, 128, 0));
        btnExportUsageReport.setForeground(Color.WHITE);
        btnExportUsageReport.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExportUsageReport.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.REPORT, 35, Color.WHITE));
        btnExportUsageReport.addActionListener(e -> exportUsageReport());
        buttonPanel.add(btnExportUsageReport);

        // Panel bảng dịch vụ
        JPanel serviceTablePanel = new JPanel(new BorderLayout());
        serviceTablePanel.setBackground(new Color(255, 255, 255));
        serviceTablePanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH DỊCH VỤ"));

        JPanel serviceSearchPanel = new JPanel();
        serviceSearchPanel.setBackground(new Color(255, 255, 255));
        serviceSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblServiceSearch = new JLabel("TÌM KIẾM:");
        lblServiceSearch.setFont(new Font("Calistoga", Font.PLAIN, 14));
        txtServiceSearch = new JTextField(15);
        txtServiceSearch.setPreferredSize(new Dimension(150, 25));
        txtServiceSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterServiceTable();
            }
        });

        serviceSearchPanel.add(lblServiceSearch);
        serviceSearchPanel.add(txtServiceSearch);

        String[] serviceColumnNames = {"ID", "Tên dịch vụ", "Mô tả", "Giá (VNĐ)", "Action"};
        tableModelService = new DefaultTableModel(serviceColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tableService = new Table();
        tableService.setModel(tableModelService);
        tableService.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableService.getColumnCount() - 1; i++) {
            tableService.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            tableService.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        tableService.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());
        tableService.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor(true));
        tableService.getColumnModel().getColumn(4).setPreferredWidth(80);

        jScrollPaneService = new JScrollPane(tableService);
        jScrollPaneService.setVerticalScrollBar(new ScrollBarCustom());
        jScrollPaneService.setHorizontalScrollBar(new ScrollBarCustom());
        jScrollPaneService.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneService.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneService.setPreferredSize(new Dimension(800, 200));

        serviceTablePanel.add(serviceSearchPanel, BorderLayout.NORTH);
        serviceTablePanel.add(jScrollPaneService, BorderLayout.CENTER);

        // Panel bảng sử dụng dịch vụ
        JPanel usageTablePanel = new JPanel(new BorderLayout());
        usageTablePanel.setBackground(new Color(255, 255, 255));
        usageTablePanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH SỬ DỤNG DỊCH VỤ"));

        JPanel usageSearchPanel = new JPanel();
        usageSearchPanel.setBackground(new Color(255, 255, 255));
        usageSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblUsageSearch = new JLabel("TÌM KIẾM:");
        lblUsageSearch.setFont(new Font("Calistoga", Font.PLAIN, 14));
        txtUsageSearch = new JTextField(15);
        txtUsageSearch.setPreferredSize(new Dimension(150, 25));
        txtUsageSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterUsageTable();
            }
        });

        usageSearchPanel.add(lblUsageSearch);
        usageSearchPanel.add(txtUsageSearch);

        String[] usageColumnNames = {"ID", "Mã đặt phòng", "Mã dịch vụ", "Số lượng", "Ngày sử dụng", "Action"};
        tableModelUsage = new DefaultTableModel(usageColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        tableUsage = new Table();
        tableUsage.setModel(tableModelUsage);
        tableUsage.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tableUsage.getColumnCount() - 1; i++) {
            tableUsage.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            tableUsage.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        tableUsage.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        tableUsage.getColumnModel().getColumn(5).setCellEditor(new ActionCellEditor(false));
        tableUsage.getColumnModel().getColumn(5).setPreferredWidth(80);

        jScrollPaneUsage = new JScrollPane(tableUsage);
        jScrollPaneUsage.setVerticalScrollBar(new ScrollBarCustom());
        jScrollPaneUsage.setHorizontalScrollBar(new ScrollBarCustom());
        jScrollPaneUsage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneUsage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneUsage.setPreferredSize(new Dimension(800, 200));

        usageTablePanel.add(usageSearchPanel, BorderLayout.NORTH);
        usageTablePanel.add(jScrollPaneUsage, BorderLayout.CENTER);

        // Panel bảng tổng
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.add(serviceTablePanel, BorderLayout.NORTH);
        tablePanel.add(usageTablePanel, BorderLayout.CENTER);

        // Nút đăng xuất
        btnExit = new JButton("ĐĂNG XUẤT");
        btnExit.setBackground(new Color(255, 69, 0));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFont(new Font("Calistoga", Font.PLAIN, 12));
        btnExit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EXIT_TO_APP, 35, Color.BLACK));
        btnExit.addActionListener(e -> logout());

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
                                                .addComponent(card2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                        .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnExit))
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
                                        .addComponent(card2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnExit))
                                        .addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        setPreferredSize(new Dimension(1454, 768));
    }

    private void loadServiceList() {
        serviceListModel.clear();
        serviceNameToIdMap.clear();
        String sql = "SELECT id, name FROM service";
        try (ResultSet rs = dbConn.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                serviceListModel.addElement(name);
                serviceNameToIdMap.put(name, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isServiceNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM service WHERE name = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, name);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private boolean isBookingIdExists(int bookingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE id = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, bookingId);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private boolean isServiceIdExists(int serviceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM service WHERE id = ?";
        try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sql, serviceId);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private void addService() {
        try {
            String name = txtServiceName.getText().trim();
            String description = txtServiceDescription.getText().trim();
            String priceText = txtServicePrice.getText().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên dịch vụ và giá không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            float price;
            try {
                price = Float.parseFloat(priceText);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Giá phải là số dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Giá phải là một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isServiceNameExists(name)) {
                JOptionPane.showMessageDialog(this, "Tên dịch vụ đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO service (name, description, price) VALUES (?, ?, ?)";
            int rowsAffected = dbConn.executeUpdate(sql,
                    name,
                    description.isEmpty() ? null : description,
                    price
            );

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearServiceFields();
                loadServiceTableData();
                loadServiceList(); // Reload JList
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addServiceUsage() {
        try {
            String bookingIdText = txtUsageBookingId.getText().trim();
            List<String> selectedServices = lstServices.getSelectedValuesList();
            int quantity = (int) spnUsageQuantity.getValue();

            if (bookingIdText.isEmpty() || selectedServices.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã đặt phòng và ít nhất một dịch vụ phải được chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int bookingId;
            try {
                bookingId = Integer.parseInt(bookingIdText);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải là số dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã đặt phòng phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isBookingIdExists(bookingId)) {
                JOptionPane.showMessageDialog(this, "Mã đặt phòng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO service_usage (booking_id, service_id, quantity) VALUES (?, ?, ?)";
            int totalRowsAffected = 0;
            for (String serviceName : selectedServices) {
                int serviceId = serviceNameToIdMap.get(serviceName);
                if (!isServiceIdExists(serviceId)) {
                    JOptionPane.showMessageDialog(this, "Mã dịch vụ " + serviceId + " không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                int rowsAffected = dbConn.executeUpdate(sql, bookingId, serviceId, quantity);
                totalRowsAffected += rowsAffected;
            }

            if (totalRowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm " + totalRowsAffected + " lượt sử dụng dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearUsageFields();
                loadUsageTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm lượt sử dụng dịch vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm lượt sử dụng dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewServiceDetails(ModelService service) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT DỊCH VỤ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"ID:", "Tên dịch vụ:", "Mô tả:", "Giá (VNĐ):"};
        String[] values = {
                String.valueOf(service.getId()),
                service.getName(),
                service.getDescription() != null ? service.getDescription() : "",
                String.valueOf(service.getPrice())
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

    private void viewUsageDetails(ModelServiceUsage usage) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CHI TIẾT SỬ DỤNG DỊCH VỤ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"ID:", "Mã đặt phòng:", "Mã dịch vụ:", "Số lượng:", "Ngày sử dụng:"};
        String[] values = {
                String.valueOf(usage.getId()),
                String.valueOf(usage.getBookingId()),
                String.valueOf(usage.getServiceId()),
                String.valueOf(usage.getQuantity()),
                usage.getUsageDate() != null ? usage.getUsageDate().toString() : ""
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

    private void exportServiceReport() {
        try {
            // Prepare data
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (ModelService service : serviceList) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", service.getId());
                row.put("name", service.getName());
                row.put("description", service.getDescription() != null ? service.getDescription() : "");
                row.put("price", service.getPrice());
                dataList.add(row);
            }

            // Load JasperReport template
            String reportPath = "path/to/your/ServiceReport.jrxml"; // Replace with your .jrxml file path
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // Fill report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "BÁO CÁO DỊCH VỤ");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            FileDialog fileDialog = new FileDialog(parent, "Chọn nơi lưu báo cáo", FileDialog.SAVE);
            fileDialog.setFile("BaoCaoDichVu.pdf");
            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (fileName != null && directory != null) {
                File file = new File(directory + fileName);
                JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Optionally, view the report
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Báo cáo Dịch vụ");
                viewer.setVisible(true);
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportUsageReport() {
        try {
            // Prepare data
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (ModelServiceUsage usage : usageList) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", usage.getId());
                row.put("bookingId", usage.getBookingId());
                row.put("serviceId", usage.getServiceId());
                row.put("quantity", usage.getQuantity());
                row.put("usageDate", usage.getUsageDate() != null ? usage.getUsageDate().toString() : "");
                dataList.add(row);
            }

            // Load JasperReport template
            String reportPath = "path/to/your/UsageReport.jrxml"; // Replace with your .jrxml file path
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // Fill report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "BÁO CÁO SỬ DỤNG DỊCH VỤ");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            FileDialog fileDialog = new FileDialog(parent, "Chọn nơi lưu báo cáo", FileDialog.SAVE);
            fileDialog.setFile("BaoCaoSuDungDichVu.pdf");
            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (fileName != null && directory != null) {
                File file = new File(directory + fileName);
                JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Optionally, view the report
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Báo cáo Sử dụng Dịch vụ");
                viewer.setVisible(true);
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearServiceFields() {
        txtServiceName.setText("");
        txtServiceDescription.setText("");
        txtServicePrice.setText("");
    }

    private void clearUsageFields() {
        txtUsageBookingId.setText("");
        lstServices.clearSelection();
        spnUsageQuantity.setValue(1);
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
        int totalServices = 0, totalUsages = 0;

        try (ResultSet rs = dbConn.executeQuery("SELECT COUNT(*) as count FROM service")) {
            if (rs.next()) {
                totalServices = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet rs = dbConn.executeQuery("SELECT COUNT(*) as count FROM service_usage")) {
            if (rs.next()) {
                totalUsages = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        card1.setData(new ModelCard("TỔNG DỊCH VỤ", totalServices, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SPA, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
        card2.setData(new ModelCard("TỔNG LƯỢT SỬ DỤNG", totalUsages, 100,
                IconFontSwing.buildIcon(GoogleMaterialDesignIcons.HISTORY, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15))));
    }

    private void editService(ModelService service) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa dịch vụ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField editName = new JTextField(service.getName(), 15);
        JTextField editDescription = new JTextField(service.getDescription() != null ? service.getDescription() : "", 20);
        JTextField editPrice = new JTextField(String.valueOf(service.getPrice()), 10);

        String[] labels = {"Tên dịch vụ:", "Mô tả:", "Giá (VNĐ):"};
        Component[] fields = {editName, editDescription, editPrice};
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
                String description = editDescription.getText().trim();
                String priceText = editPrice.getText().trim();

                if (name.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tên dịch vụ và giá không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                float price;
                try {
                    price = Float.parseFloat(priceText);
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Giá phải là số dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá phải là một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sqlCheckName = "SELECT COUNT(*) FROM service WHERE name = ? AND id != ?";
                try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheckName, name, service.getId());
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Tên dịch vụ đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sql = "UPDATE service SET name = ?, description = ?, price = ? WHERE id = ?";
                int rowsAffected = dbConn.executeUpdate(sql,
                        name,
                        description.isEmpty() ? null : description,
                        price,
                        service.getId()
                );

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadServiceTableData();
                    loadServiceList(); // Reload JList
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật dịch vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật dịch vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void editServiceUsage(ModelServiceUsage usage) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa lượt sử dụng dịch vụ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField editBookingId = new JTextField(String.valueOf(usage.getBookingId()), 10);
        JTextField editServiceId = new JTextField(String.valueOf(usage.getServiceId()), 10);
        JSpinner editQuantity = new JSpinner(new SpinnerNumberModel(usage.getQuantity(), 1, 1000, 1));

        String[] labels = {"Mã đặt phòng:", "Mã dịch vụ:", "Số lượng:"};
        Component[] fields = {editBookingId, editServiceId, editQuantity};
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
                String bookingIdText = editBookingId.getText().trim();
                String serviceIdText = editServiceId.getText().trim();
                int quantity = (int) editQuantity.getValue();

                if (bookingIdText.isEmpty() || serviceIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Mã đặt phòng và mã dịch vụ không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int bookingId, serviceId;
                try {
                    bookingId = Integer.parseInt(bookingIdText);
                    serviceId = Integer.parseInt(serviceIdText);
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Số lượng phải là số dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Mã đặt phòng và mã dịch vụ phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isBookingIdExists(bookingId)) {
                    JOptionPane.showMessageDialog(dialog, "Mã đặt phòng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isServiceIdExists(serviceId)) {
                    JOptionPane.showMessageDialog(dialog, "Mã dịch vụ không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "UPDATE service_usage SET booking_id = ?, service_id = ?, quantity = ? WHERE id = ?";
                int rowsAffected = dbConn.executeUpdate(sql,
                        bookingId,
                        serviceId,
                        quantity,
                        usage.getId()
                );

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật lượt sử dụng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsageTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật lượt sử dụng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật lượt sử dụng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void deleteService(ModelService service) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ " + service.getName() + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sqlCheck = "SELECT COUNT(*) FROM service_usage WHERE service_id = ?";
            try (PreparedStatement pstmt = dbConn.executeQueryWithStatement(sqlCheck, service.getId());
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa dịch vụ vì đã có lượt sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            String sql = "DELETE FROM service WHERE id = ?";
            int rowsAffected = dbConn.executeUpdate(sql, service.getId());

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadServiceTableData();
                loadServiceList(); // Reload JList
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteServiceUsage(ModelServiceUsage usage) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lượt sử dụng này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM service_usage WHERE id = ?";
            int rowsAffected = dbConn.executeUpdate(sql, usage.getId());

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa lượt sử dụng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadUsageTableData();
                updateCards();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa lượt sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadServiceTableData() {
        String sql = "SELECT * FROM service";
        tableModelService.setRowCount(0);
        serviceList.clear();

        EventAction<ModelService> eventAction = new EventAction<ModelService>() {
            @Override
            public void delete(ModelService service) throws SQLException {
                deleteService(service);
            }

            @Override
            public void update(ModelService service) {
                editService(service);
            }

            @Override
            public void view(ModelService service) {
                viewServiceDetails(service);
            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelService service = new ModelService(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getFloat("price")
                    );
                    serviceList.add(service);

                    Object[] row = {
                            service.getId(),
                            service.getName(),
                            service.getDescription(),
                            service.getPrice(),
                            new ModelAction<>(service, eventAction)
                    };
                    tableModelService.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadUsageTableData() {
        String sql = "SELECT * FROM service_usage";
        tableModelUsage.setRowCount(0);
        usageList.clear();

        EventAction<ModelServiceUsage> eventAction = new EventAction<>() {
            @Override
            public void delete(ModelServiceUsage usage) throws SQLException {
                deleteServiceUsage(usage);
            }

            @Override
            public void update(ModelServiceUsage usage) {
                editServiceUsage(usage);
            }

            public void view(ModelServiceUsage usage) {
                viewUsageDetails(usage);
            }
        };

        try (ResultSet rs = dbConn.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    ModelServiceUsage usage = new ModelServiceUsage(
                            rs.getInt("id"),
                            rs.getInt("booking_id"),
                            rs.getInt("service_id"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("usage_date")
                    );
                    usageList.add(usage);

                    Object[] row = {
                            usage.getId(),
                            usage.getBookingId(),
                            usage.getServiceId(),
                            usage.getQuantity(),
                            usage.getUsageDate(),
                            new ModelAction<>(usage, eventAction)
                    };
                    tableModelUsage.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu sử dụng dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterServiceTable() {
        String searchText = txtServiceSearch.getText().trim().toLowerCase();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModelService);
        tableService.setRowSorter(sorter);

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
        }
    }

    private void filterUsageTable() {
        String searchText = txtUsageSearch.getText().trim().toLowerCase();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModelUsage);
        tableUsage.setRowSorter(sorter);

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1, 2));
        }
    }

    private class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 6));
            panel.setBackground(table.getBackground());

            if (value instanceof ModelAction) {
                JButton btnView = new JButton();
                btnView.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.VISIBILITY, 13, Color.WHITE));
                btnView.setBackground(new Color(0, 153, 255)); // Blue for View
                btnView.setForeground(Color.WHITE);
                btnView.setPreferredSize(new Dimension(22, 22));
                btnView.setToolTipText("Xem chi tiết");

                JButton btnEdit = new JButton();
                btnEdit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 13, Color.WHITE));
                btnEdit.setBackground(new Color(255, 153, 0)); // Orange for Edit
                btnEdit.setForeground(Color.WHITE);
                btnEdit.setPreferredSize(new Dimension(22, 22));
                btnEdit.setToolTipText("Chỉnh sửa");

                JButton btnDelete = new JButton();
                btnDelete.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 13, Color.WHITE));
                btnDelete.setBackground(new Color(255, 51, 51)); // Red for Delete
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setPreferredSize(new Dimension(22, 22));
                btnDelete.setToolTipText("Xóa");

                panel.add(btnView);
                panel.add(btnEdit);
                panel.add(btnDelete);
            }

            return panel;
        }
    }

    private class ActionCellEditor<T> extends AbstractCellEditor implements TableCellEditor {
        private ModelAction<T> action;
        private final boolean isServiceTable;

        private final JPanel panel;
        private final JButton btnView;
        private final JButton btnEdit;
        private final JButton btnDelete;

        public ActionCellEditor(boolean isServiceTable) {
            this.isServiceTable = isServiceTable;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 6));
            btnView = new JButton();
            btnView.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.VISIBILITY, 13, Color.WHITE));
            btnView.setBackground(new Color(0, 153, 255));
            btnView.setForeground(Color.WHITE);
            btnView.setPreferredSize(new Dimension(22, 22));
            btnView.setToolTipText("Xem chi tiết");

            btnEdit = new JButton();
            btnEdit.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 13, Color.WHITE));
            btnEdit.setBackground(new Color(255, 153, 0));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setPreferredSize(new Dimension(22, 22));
            btnEdit.setToolTipText("Chỉnh sửa");

            btnDelete = new JButton();
            btnDelete.setIcon(IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 13, Color.WHITE));
            btnDelete.setBackground(new Color(255, 51, 51));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setPreferredSize(new Dimension(22, 22));
            btnDelete.setToolTipText("Xóa");

            btnView.addActionListener(e -> {
                if (action != null) {
                    action.getEvent().view(action.getData());
                    fireEditingStopped();
                }
            });

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

            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof ModelAction) {
                this.action = (ModelAction<T>) value;
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
        JFrame frame = new JFrame("QUẢN LÝ DỊCH VỤ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 600));
        ScrQLDichVu scrQLDichVu = new ScrQLDichVu();
        frame.add(scrQLDichVu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scrQLDichVu.dbConn.close();
            }
        });
    }
}