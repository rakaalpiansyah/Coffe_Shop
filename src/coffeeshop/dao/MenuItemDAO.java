package coffeeshop.dao;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.MenuItem; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MenuItemDAO {

    public boolean addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, type, price, image_filename) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, item.getName());
                pstmt.setString(2, item.getType());
                pstmt.setBigDecimal(3, item.getPrice());
                pstmt.setString(4, item.getImageFilename());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            item.setId(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan menu item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public List<MenuItem> getAllMenuItems() {
        String sql = "SELECT id, name, type, price, image_filename FROM menu_items ORDER BY type ASC, name ASC";
        List<MenuItem> menuItems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    MenuItem item = new MenuItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getBigDecimal("price"),
                            rs.getString("image_filename")
                    );
                    menuItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua menu item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(rs);
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return menuItems;
    }

    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT id, name, type, price, image_filename FROM menu_items WHERE id = ?";
        MenuItem item = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    item = new MenuItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getBigDecimal("price"),
                            rs.getString("image_filename")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil menu item berdasarkan ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(rs);
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return item;
    }

    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, type = ?, price = ?, image_filename = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, item.getName());
                pstmt.setString(2, item.getType());
                pstmt.setBigDecimal(3, item.getPrice());
                pstmt.setString(4, item.getImageFilename());
                pstmt.setInt(5, item.getId());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate menu item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    // DELETE 
    public boolean deleteMenuItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menghapus menu item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            UserDAO.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }
}
