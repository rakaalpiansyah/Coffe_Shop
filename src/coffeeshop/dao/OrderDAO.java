package coffeeshop.dao;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int addOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int orderId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, order.getUserId());
                pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                pstmt.setBigDecimal(3, order.getTotalAmount());
                pstmt.setString(4, order.getStatus());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            orderId = generatedKeys.getInt(1);
                            order.setId(orderId); // Set ID yang di-generate ke objek Order
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan order: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return orderId;
    }

    public List<Order> getOrdersByUserId(int userId) {
        String sql = "SELECT id, user_id, order_date, total_amount, status FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil order berdasarkan user ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(rs);
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT id, user_id, order_date, total_amount, status FROM orders ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua order: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(rs);
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return orders;
    }
}
