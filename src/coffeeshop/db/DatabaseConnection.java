package coffeeshop.db;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/coffee_shop_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // PASSWORD MYSQL

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Kesalahan Driver JDBC: " + e.getMessage() + "\nPastikan MySQL JDBC driver (mysql-connector-j-x.x.xx.jar) ada di Build Path proyek Anda.",
                    "Kesalahan Koneksi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Kesalahan SQL: " + e.getMessage() + "\nGagal terhubung ke database. Periksa URL, username, password, dan pastikan MySQL Server berjalan.",
                    "Kesalahan Koneksi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static String getAppBaseDirectory() {
        String path = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File jarFile = new File(decodedPath);
            return jarFile.getParentFile().getPath();
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error decoding path: " + e.getMessage());
            return new File("").getAbsolutePath(); 
        }
    }

    public static File getImageDirectory() {
        File imageDir = new File(System.getProperty("user.dir"), "data" + File.separator + "images");
        if (!imageDir.exists()) {
            if (imageDir.mkdirs()) {
                System.out.println("Direktori gambar dibuat: " + imageDir.getAbsolutePath());
            } else {
                System.err.println("Gagal membuat direktori gambar: " + imageDir.getAbsolutePath());
            }
        }
        return imageDir;
    }
}

