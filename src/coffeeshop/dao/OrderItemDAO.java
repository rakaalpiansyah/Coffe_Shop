package coffeeshop.dao;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class OrderItemDAO {

    public boolean addOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, item.getOrderId());
                pstmt.setInt(2, item.getMenuItemId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setBigDecimal(4, item.getSubtotal());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan order item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT oi.id, oi.order_id, oi.menu_item_id, oi.quantity, oi.subtotal, mi.name AS menu_item_name, mi.price AS menu_item_price " +
                     "FROM order_items oi JOIN menu_items mi ON oi.menu_item_id = mi.id " +
                     "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    items.add(new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("menu_item_id"),
                        rs.getString("menu_item_name"), 
                        rs.getBigDecimal("menu_item_price"), 
                        rs.getInt("quantity"),
                        rs.getBigDecimal("subtotal")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil order items berdasarkan order ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(rs);
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return items;
    }
}
