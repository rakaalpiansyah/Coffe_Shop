package coffeeshop.dao;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.User; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public User getUserByEmail(String email) {
        String sql = "SELECT id, name, email, password, is_admin FROM users WHERE email = ?";
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getBoolean("is_admin")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil user berdasarkan email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(rs);
            closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return user;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, is_admin) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setBoolean(4, user.isAdmin());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            user.setId(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Email '" + user.getEmail() + "' sudah terdaftar.");
            } else {
                System.err.println("Error saat registrasi user: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public static void closeConnection(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup Statement: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
