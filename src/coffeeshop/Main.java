package coffeeshop;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Test koneksi database saat aplikasi dimulai
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("Database terhubung dengan sukses saat startup.");
            DatabaseConnection.closeConnection(DatabaseConnection.getConnection()); 
        } else {
            System.err.println("Gagal terhubung ke database saat startup. Periksa konfigurasi.");
            // Mungkin tampilkan pesan error ke user atau exit aplikasi
            return;
        }

        // Jalankan aplikasi GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}