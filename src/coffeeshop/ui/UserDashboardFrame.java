package coffeeshop.ui;

import coffeeshop.db.DatabaseConnection;
import coffeeshop.model.MenuItem;
import coffeeshop.model.User;
import coffeeshop.model.Order; 
import coffeeshop.model.OrderItem; 
import coffeeshop.service.MenuService;
import coffeeshop.service.OrderService; 

import javax.swing.*;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter; 
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 
import java.util.concurrent.ExecutionException;
import java.util.Vector;

public class UserDashboardFrame extends JFrame {
    private MenuService menuService;
    private OrderService orderService; 
    private User currentUser;
    private JTabbedPane userTabbedPane; 
    
    private JPanel menuItemsPanel;
    private String currentFilter = "all";

    private Map<MenuItem, Integer> cart; 
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel cartTotalLabel;
    private JButton checkoutButton;

    private JTable orderHistoryTable;
    private DefaultTableModel orderHistoryTableModel;
    private JTable orderDetailTable; 
    private DefaultTableModel orderDetailTableModel;
    private JLabel orderHistoryTitleLabel;


    public UserDashboardFrame(User user) {
        this.currentUser = user;
        menuService = new MenuService();
        orderService = new OrderService(); 
        cart = new HashMap<>(); 

        setTitle("Selamat Datang di Coffee Shop - " + currentUser.getName());
        setSize(1200, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadMenuItems(); 
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Halo, " + currentUser.getName() + "!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRightPanel.add(logoutButton);

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(headerRightPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        userTabbedPane = new JTabbedPane();

        // 1. Menu Selection Panel
        JPanel menuSelectionPanel = createMenuSelectionPanel();
        userTabbedPane.addTab("Pilih Menu", menuSelectionPanel);

        // 2. Cart Panel
        JPanel cartPanel = createCartPanel();
        userTabbedPane.addTab("Keranjang Saya", cartPanel);

        // 3. Order History Panel
        JPanel orderHistoryPanel = createOrderHistoryPanel();
        userTabbedPane.addTab("Riwayat Pesanan", orderHistoryPanel);
        
        userTabbedPane.addChangeListener(e -> {
            if (userTabbedPane.getSelectedComponent() == orderHistoryPanel) {
                loadOrderHistory();
            }
        });


        mainPanel.add(userTabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createMenuSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Menu"));

        JButton allFilterButton = new JButton("Semua");
        allFilterButton.addActionListener(e -> setFilter("all"));
        filterPanel.add(allFilterButton);

        JButton drinkFilterButton = new JButton("Minuman");
        drinkFilterButton.addActionListener(e -> setFilter("drink"));
        filterPanel.add(drinkFilterButton);

        JButton foodFilterButton = new JButton("Makanan");
        foodFilterButton.addActionListener(e -> setFilter("food"));
        filterPanel.add(foodFilterButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Menu Items Display Panel
        menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new GridLayout(0, 3, 15, 15));
        menuItemsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(menuItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Keranjang Belanja Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Cart Table
        cartTableModel = new DefaultTableModel(new Object[]{"Item", "Harga", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cell tidak bisa diedit
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        cartTotalLabel = new JLabel("Total: Rp 0.00", SwingConstants.RIGHT);
        cartTotalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(cartTotalLabel, BorderLayout.EAST);

        JPanel cartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton clearCartButton = new JButton("Bersihkan Keranjang");
        clearCartButton.setBackground(Color.ORANGE);
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.addActionListener(e -> clearCart());
        cartButtonsPanel.add(clearCartButton);

        checkoutButton = new JButton("Checkout & Bayar");
        checkoutButton.setBackground(Color.GREEN);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.addActionListener(e -> performCheckout());
        cartButtonsPanel.add(checkoutButton);
        bottomPanel.add(cartButtonsPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        orderHistoryTitleLabel = new JLabel("Riwayat Pesanan Anda", SwingConstants.CENTER);
        orderHistoryTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(orderHistoryTitleLabel, BorderLayout.NORTH);

        // Top table for Orders
        JPanel orderListPanel = new JPanel(new BorderLayout());
        orderListPanel.setBorder(BorderFactory.createTitledBorder("Daftar Pesanan"));
        orderHistoryTableModel = new DefaultTableModel(new Object[]{"ID Pesanan", "Tanggal", "Total Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderHistoryTable = new JTable(orderHistoryTableModel);
        orderHistoryTable.setFillsViewportHeight(true);
        orderHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderHistoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderHistoryTable.getSelectedRow() != -1) {
                displayOrderDetails();
            }
        });
        orderListPanel.add(new JScrollPane(orderHistoryTable), BorderLayout.CENTER);
        panel.add(orderListPanel, BorderLayout.CENTER);

        // Bottom table for Order Details
        JPanel orderDetailPanel = new JPanel(new BorderLayout());
        orderDetailPanel.setBorder(BorderFactory.createTitledBorder("Detail Item Pesanan"));
        orderDetailTableModel = new DefaultTableModel(new Object[]{"Nama Item", "Harga Satuan", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderDetailTable = new JTable(orderDetailTableModel);
        orderDetailTable.setFillsViewportHeight(true);
        orderDetailPanel.add(new JScrollPane(orderDetailTable), BorderLayout.CENTER);
        panel.add(orderDetailPanel, BorderLayout.SOUTH);

        return panel;
    }


    private void setFilter(String filter) {
        currentFilter = filter;
        loadMenuItems();
    }

    private void loadMenuItems() {
        menuItemsPanel.removeAll();
        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();

        List<MenuItem> allItems = menuService.getAllMenuItems();

        List<MenuItem> filteredItems = allItems.stream()
                .filter(item -> currentFilter.equals("all") || item.getType().equalsIgnoreCase(currentFilter))
                .collect(java.util.stream.Collectors.toList());

        if (filteredItems.isEmpty()) {
            menuItemsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            menuItemsPanel.add(new JLabel("Tidak ada item menu yang tersedia untuk filter ini."));
        } else {
            menuItemsPanel.setLayout(new GridLayout(0, 3, 15, 15));
            for (MenuItem item : filteredItems) {
                menuItemsPanel.add(createMenuItemCard(item));
            }
        }
        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setPreferredSize(new Dimension(250, 300));
        card.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel("Memuat Gambar...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(250, 180));
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        imageLabel.setForeground(Color.GRAY);
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("<html><b>" + item.getName() + "</b></html>");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(nameLabel);

        JLabel typeLabel = new JLabel("Tipe: " + (item.getType().equalsIgnoreCase("drink") ? "Minuman" : "Makanan"));
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsPanel.add(typeLabel);

        JLabel priceLabel = new JLabel("Harga: Rp " + item.getPrice().toPlainString());
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(255, 165, 0));
        detailsPanel.add(priceLabel);

        // Add to Cart Button
        JButton addToCartButton = new JButton("Tambah ke Keranjang");
        addToCartButton.setBackground(new Color(0, 150, 136)); // Teal
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.addActionListener(e -> addToCart(item));
        detailsPanel.add(addToCartButton);


        card.add(detailsPanel, BorderLayout.CENTER);

        new ImageLoaderWorker(imageLabel, item.getImageFilename()).execute();

        return card;
    }

    // Metode untuk menambah item ke keranjang
    private void addToCart(MenuItem item) {
        cart.put(item, cart.getOrDefault(item, 0) + 1);
        updateCartDisplay();
        JOptionPane.showMessageDialog(this, item.getName() + " telah ditambahkan ke keranjang.", "Keranjang Diperbarui", JOptionPane.INFORMATION_MESSAGE);
        userTabbedPane.setSelectedComponent(userTabbedPane.getComponentAt(1));
    }

    // Metode untuk mengupdate tampilan keranjang
    private void updateCartDisplay() {
        cartTableModel.setRowCount(0); 
        BigDecimal currentTotal = BigDecimal.ZERO;

        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            MenuItem item = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(quantity));
            currentTotal = currentTotal.add(subtotal);

            cartTableModel.addRow(new Object[]{item.getName(), item.getPrice(), quantity, subtotal});
        }
        cartTotalLabel.setText("Total: Rp " + currentTotal.toPlainString());
        checkoutButton.setEnabled(!cart.isEmpty()); 
    }

    // Metode untuk membersihkan keranjang
    private void clearCart() {
        cart.clear();
        updateCartDisplay();
        JOptionPane.showMessageDialog(this, "Keranjang belanja telah dibersihkan.", "Keranjang Kosong", JOptionPane.INFORMATION_MESSAGE);
    }

    // Metode untuk melakukan checkout
    private void performCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja Anda kosong.", "Checkout Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menyelesaikan pesanan ini dan melakukan pembayaran?", "Konfirmasi Pembayaran", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Simulasi pembayaran
            if (orderService.createOrder(currentUser, cart)) {
                JOptionPane.showMessageDialog(this, "Pembayaran Berhasil! Pesanan Anda telah dibuat.", "Pembayaran Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearCart(); 
                // userTabbedPane.setSelectedComponent(userTabbedPane.getComponentAt(2)); // Pindah ke tab riwayat pesanan opsi
            } else {
                JOptionPane.showMessageDialog(this, "Pembayaran Gagal. Terjadi kesalahan saat membuat pesanan.", "Pembayaran Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Metode untuk memuat riwayat pesanan user
    private void loadOrderHistory() {
        orderHistoryTableModel.setRowCount(0); 
        orderDetailTableModel.setRowCount(0);

        List<Order> userOrders = orderService.getUserOrders(currentUser.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (userOrders.isEmpty()) {
            orderHistoryTitleLabel.setText("Riwayat Pesanan Anda (Kosong)");
        } else {
            orderHistoryTitleLabel.setText("Riwayat Pesanan Anda");
            for (Order order : userOrders) {
                Vector<Object> row = new Vector<>();
                row.add(order.getId());
                row.add(order.getOrderDate().format(formatter));
                row.add(order.getTotalAmount().toPlainString());
                row.add(order.getStatus());
                orderHistoryTableModel.addRow(row);
            }
        }
    }

    // Metode untuk menampilkan detail pesanan yang dipilih
    private void displayOrderDetails() {
        orderDetailTableModel.setRowCount(0); 

        int selectedRow = orderHistoryTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) orderHistoryTableModel.getValueAt(selectedRow, 0);

            List<Order> userOrders = orderService.getUserOrders(currentUser.getId());
            Order selectedOrder = userOrders.stream()
                                            .filter(order -> order.getId() == orderId)
                                            .findFirst()
                                            .orElse(null);

            if (selectedOrder != null && selectedOrder.getItems() != null) {
                for (OrderItem item : selectedOrder.getItems()) {
                    orderDetailTableModel.addRow(new Object[]{
                        item.getMenuItemName(),
                        item.getMenuItemPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                    });
                }
            }
        }
    }


    // Kelas SwingWorker untuk memuat gambar di background 
    private class ImageLoaderWorker extends SwingWorker<ImageIcon, Void> {
        private final JLabel targetLabel;
        private final String imageFilename;
        private final Dimension imageSize = new Dimension(250, 180);

        public ImageLoaderWorker(JLabel targetLabel, String imageFilename) {
            this.targetLabel = targetLabel;
            this.imageFilename = imageFilename;
        }

        @Override
        protected ImageIcon doInBackground() throws Exception {
            BufferedImage bImage = null;
            if (imageFilename != null && !imageFilename.trim().isEmpty()) {
                File imageFile = new File(DatabaseConnection.getImageDirectory(), imageFilename);
                if (imageFile.exists() && imageFile.isFile()) {
                    try {
                        bImage = ImageIO.read(imageFile);
                    } catch (IOException e) {
                        System.err.println("Error saat memuat gambar dari file: " + imageFile.getAbsolutePath() + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("File gambar tidak ditemukan atau bukan file: " + imageFile.getAbsolutePath());
                }
            }

            if (bImage == null) {
                try {
                    URL fallbackImageUrl = getClass().getResource("/images/no_image.png");
                    if (fallbackImageUrl != null) {
                        bImage = ImageIO.read(fallbackImageUrl);
                    } else {
                        bImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = bImage.createGraphics();
                        g2d.setColor(new Color(200, 200, 200));
                        g2d.fillRect(0, 0, bImage.getWidth(), bImage.getHeight());
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                        String text = "No Image";
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(text);
                        int textHeight = fm.getHeight();
                        int x = (bImage.getWidth() - textWidth) / 2;
                        int y = (bImage.getHeight() - textHeight) / 2 + fm.getAscent();
                        g2d.drawString(text, x, y);
                        g2d.dispose();
                    }
                } catch (IOException e) {
                    System.err.println("Error saat memuat gambar fallback: " + e.getMessage());
                }
            }

            if (bImage != null) {
                Image scaledImage = bImage.getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                ImageIcon icon = get();
                if (icon != null) {
                    targetLabel.setIcon(icon);
                    targetLabel.setText("");
                } else {
                    targetLabel.setText("Tidak Ada Gambar");
                    targetLabel.setIcon(null);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error saat menyelesaikan pemuatan gambar: " + e.getMessage());
                targetLabel.setText("Error Memuat");
                targetLabel.setIcon(null);
            }
            targetLabel.revalidate();
            targetLabel.repaint();
        }
    }
}
