package coffeeshop.ui;

import coffeeshop.model.User; 
import coffeeshop.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        setTitle("Coffee Shop - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Selamat Datang di Coffee Shop", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(255, 165, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Daftar Akun Baru");
        registerButton.setBackground(new Color(50, 205, 50));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        add(panel);
    }

    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email dan password tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User loggedInUser = authService.login(email, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat datang, " + loggedInUser.getName(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            if (loggedInUser.isAdmin()) {
                AdminDashboardFrame adminFrame = new AdminDashboardFrame();
                adminFrame.setVisible(true);
            } else {
                UserDashboardFrame userFrame = new UserDashboardFrame(loggedInUser);
                userFrame.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email atau password salah.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}
