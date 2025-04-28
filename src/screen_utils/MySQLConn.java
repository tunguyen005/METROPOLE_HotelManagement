package screen_utils;

import java.sql.*;

public class MySQLConn {
    private static final String URL = "jdbc:mysql://localhost:3306/Metropole?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "baotran0911";

    private Connection con;
    private Statement stmt;

    public MySQLConn() {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = con.createStatement();
            System.out.println("✅ Kết nối thành công!");
        } catch (SQLException ex) {
            System.err.println("❌ Kết nối thất bại: " + ex.getMessage());
        }
    }

    // Phương thức executeQuery để chạy truy vấn SELECT không có tham số
    public ResultSet executeQuery(String query) throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối MySQL chưa được mở!");
        }
        if (stmt == null || stmt.isClosed()) {
            stmt = con.createStatement();
        }
        System.out.println("📌 Đang thực thi truy vấn: " + query);
        return stmt.executeQuery(query);
    }

    // Phương thức executeQuery để chạy truy vấn SELECT với tham số
    public PreparedStatement executeQueryWithStatement(String query, Object... params) throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối MySQL chưa được mở!");
        }
        PreparedStatement pstmt = con.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        System.out.println("📌 Đang thực thi truy vấn: " + query);
        return pstmt; // Return the PreparedStatement so the caller can manage its lifecycle
    }

    // Phương thức executeUpdate để chạy INSERT, UPDATE, DELETE
    public int executeUpdate(String query, Object... params) throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối MySQL chưa được mở!");
        }
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            System.out.println("📌 Đang thực thi cập nhật: " + query);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected;
        } catch (SQLException ex) {
            System.err.println("❌ Lỗi SQL: " + ex.getMessage());
            throw ex;
        }
    }

    public void close() {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
                System.out.println("🔌 Đã đóng Statement.");
            }
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("🔌 Đã đóng kết nối.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}