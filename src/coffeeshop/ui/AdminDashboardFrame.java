// ui/AdminDashboardFrame.java (Disesuaikan untuk Tampilan Total Penjualan)
package coffeeshop.ui;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.MenuItem;
import coffeeshop.model.Order; // Import Order
import coffeeshop.model.OrderItem; // Import OrderItem
import coffeeshop.service.MenuService;
import coffeeshop.service.OrderService; // Import OrderService

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter; // Untuk format tanggal
import java.util.List;
import java.util.Vector; // Untuk JTable data

public class AdminDashboardFrame extends JFrame {
    private MenuService menuService;
    private OrderService orderService; // Tambahkan OrderService
    private JTabbedPane adminTabbedPane; // Untuk tab baru
    // Menu Management Panel components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;

    private JTextField idField, nameField, priceField, imageFilenameField;
    private JComboBox<String> typeComboBox;
    private JButton addButton, updateButton, deleteButton, clearButton, chooseImageButton;

    // Sales Report Panel components
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private JLabel totalSalesLabel;


    public AdminDashboardFrame() {
        menuService = new MenuService();
        orderService = new OrderService(); // Inisialisasi OrderService
        setTitle("Admin Dashboard - Coffee Shop");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadMenuItems(); // Muat menu awal
        loadSalesData(); // Muat data penjualan awal
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });
        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        adminTabbedPane = new JTabbedPane();

        // 1. Menu Management Panel
        JPanel menuManagementPanel = createMenuManagementPanel();
        adminTabbedPane.addTab("Manajemen Menu", menuManagementPanel);

        // 2. Sales Report Panel
        JPanel salesReportPanel = createSalesReportPanel();
        adminTabbedPane.addTab("Laporan Penjualan", salesReportPanel);

        mainPanel.add(adminTabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createMenuManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manajemen Menu Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titleLabel = new JLabel("Manajemen Menu Coffee Shop", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 4;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Name
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 3;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        row++;

        // Type
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tipe:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(new String[]{"drink", "food"});
        formPanel.add(typeComboBox, gbc);

        // Price
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 3;
        priceField = new JTextField(15);
        formPanel.add(priceField, gbc);
        row++;

        // Image Filename (sekarang dengan tombol pilih)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("File Gambar:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        imageFilenameField = new JTextField(20);
        imageFilenameField.setEditable(false);
        formPanel.add(imageFilenameField, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1;
        chooseImageButton = new JButton("Pilih Gambar");
        chooseImageButton.addActionListener(e -> chooseImageFile());
        formPanel.add(chooseImageButton, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah");
        addButton.setBackground(new Color(50, 205, 50));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addMenuItem());

        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(30, 144, 255));
        updateButton.setForeground(Color.WHITE);
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateMenuItem());

        deleteButton = new JButton("Hapus");
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteMenuItem());

        clearButton = new JButton("Bersihkan Form");
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        row++;

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        menuTableModel = new DefaultTableModel(new Object[]{"ID", "Nama", "Tipe", "Harga", "File Gambar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuTableModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                displaySelectedMenuItem();
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                addButton.setEnabled(false);
            } else {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
                addButton.setEnabled(true);
            }
        });
        JScrollPane scrollPane = new JScrollPane(menuTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Laporan Penjualan Semua Pesanan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        salesTableModel = new DefaultTableModel(new Object[]{"ID Pesanan", "ID User", "Tanggal", "Total Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
        salesTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        totalSalesLabel = new JLabel("Total Penjualan: Rp 0.00", SwingConstants.RIGHT);
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalSalesLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(totalSalesLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMenuItems() {
        menuTableModel.setRowCount(0);
        List<MenuItem> items = menuService.getAllMenuItems();
        for (MenuItem item : items) {
            menuTableModel.addRow(new Object[]{item.getId(), item.getName(), item.getType(), item.getPrice(), item.getImageFilename()});
        }
    }

    private void loadSalesData() {
        salesTableModel.setRowCount(0);
        List<Order> allOrders = orderService.getAllOrders();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BigDecimal grandTotalSales = BigDecimal.ZERO;

        for (Order order : allOrders) {
            Vector<Object> row = new Vector<>();
            row.add(order.getId());
            row.add(order.getUserId());
            row.add(order.getOrderDate().format(formatter));
            row.add(order.getTotalAmount());
            row.add(order.getStatus());
            salesTableModel.addRow(row);
            grandTotalSales = grandTotalSales.add(order.getTotalAmount());
        }
        totalSalesLabel.setText("Total Penjualan: Rp " + grandTotalSales.toPlainString());
    }


    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        typeComboBox.setSelectedItem("drink");
        priceField.setText("");
        imageFilenameField.setText("");
        menuTable.clearSelection();
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void displaySelectedMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(menuTableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(menuTableModel.getValueAt(selectedRow, 1).toString());
            typeComboBox.setSelectedItem(menuTableModel.getValueAt(selectedRow, 2).toString());
            priceField.setText(menuTableModel.getValueAt(selectedRow, 3).toString());
            Object imageFilename = menuTableModel.getValueAt(selectedRow, 4);
            imageFilenameField.setText(imageFilename != null ? imageFilename.toString() : "");
        }
    }

    private void chooseImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Gambar Menu");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Gambar (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File imageDir = DatabaseConnection.getImageDirectory();
            File destinationFile = new File(imageDir, selectedFile.getName());

            try {
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imageFilenameField.setText(selectedFile.getName());
                JOptionPane.showMessageDialog(this, "Gambar berhasil disalin ke: " + destinationFile.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyalin gambar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void addMenuItem() {
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String imageFilename = imageFilenameField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Harga harus diisi.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh negatif.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MenuItem newItem = new MenuItem(name, type, price, imageFilename.isEmpty() ? null : imageFilename);
            if (menuService.addMenuItem(newItem)) {
                JOptionPane.showMessageDialog(this, "Menu item berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuItems();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan menu item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenuItem() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String imageFilename = imageFilenameField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Harga tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh negatif.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MenuItem updatedItem = new MenuItem(id, name, type, price, imageFilename.isEmpty() ? null : imageFilename);
            if (menuService.updateMenuItem(updatedItem)) {
                JOptionPane.showMessageDialog(this, "Menu item berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadMenuItems();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate menu item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu item yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) menuTableModel.getValueAt(selectedRow, 0); // Pastikan ini dari menuTableModel
        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin menghapus menu item ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (menuService.deleteMenuItem(id)) {
                JOptionPane.showMessageDialog(this, "Menu item berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                String filenameToDelete = (String) menuTableModel.getValueAt(selectedRow, 4);
                if (filenameToDelete != null && !filenameToDelete.isEmpty()) {
                    File imageToDelete = new File(DatabaseConnection.getImageDirectory(), filenameToDelete);
                    if (imageToDelete.exists() && imageToDelete.delete()) {
                        System.out.println("File gambar berhasil dihapus: " + filenameToDelete);
                    } else {
                        System.err.println("Gagal menghapus file gambar: " + filenameToDelete);
                    }
                }
                loadMenuItems();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus menu item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
