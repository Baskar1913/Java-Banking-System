package BankSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.swing.table.DefaultTableModel;

public class BankEmployeeSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection connection;
    
    // Login components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Dashboard components
    private JLabel welcomeLabel;
    private JLabel customerCountLabel;
    private JLabel recentTransactionsLabel;
    private JTextArea recentTransactionsArea;
    private JButton customerServiceBtn;
    private JButton transactionsBtn;
    private JButton logoutBtn;
    
    // Customer Service components
    private JButton createCustomerBtn;
    private JButton deleteCustomerBtn;
    private JButton editCustomerBtn;
    private JButton depositWithdrawBtn;
    private JButton balanceEnquiryBtn;
    private JButton backToDashboardBtn;
    
    // Current employee info
    private int currentEmployeeId;
    private String currentEmployeeName;
    
    public BankEmployeeSystem() {
        super("Bank Employee System");
        initializeDB();
        initializeUI();
    }
    
    private void initializeDB() {
        connection = DBConnection.getConnection();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to database. Application will exit.",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initializeUI() {
        // Main window setup
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create card layout for navigation
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different panels
        createLoginPanel();
        createDashboardPanel();
        createCustomerServicePanel();
        
        add(mainPanel);
        
        // Show login panel first
        cardLayout.show(mainPanel, "login");
    }
    
    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 240, 240));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Bank Employee Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);
        
        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
        
        // Login button action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT employee_id, full_name FROM employees WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password); // In real app, use hashed passwords
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentEmployeeId = rs.getInt("employee_id");
                    currentEmployeeName = rs.getString("full_name");
                    
                    // Update last login
                    PreparedStatement updateStmt = connection.prepareStatement(
                        "UPDATE employees SET last_login = NOW() WHERE employee_id = ?");
                    updateStmt.setInt(1, currentEmployeeId);
                    updateStmt.executeUpdate();
                    
                    // Show dashboard
                    updateDashboard();
                    cardLayout.show(mainPanel, "dashboard");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add enter key listener
        passwordField.addActionListener(e -> loginButton.doClick());
        
        mainPanel.add(loginPanel, "login");
    }
    
    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(240, 240, 240));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutBtn.setBackground(new Color(220, 80, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "login");
            usernameField.setText("");
            passwordField.setText("");
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Left panel - Stats
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        // Customer count
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(Color.WHITE);
        
        JLabel customerTitle = new JLabel("Total Customers");
        customerTitle.setFont(new Font("Arial", Font.BOLD, 16));
        customerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        customerPanel.add(customerTitle, BorderLayout.NORTH);
        
        customerCountLabel = new JLabel("0");
        customerCountLabel.setFont(new Font("Arial", Font.BOLD, 36));
        customerCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        customerCountLabel.setForeground(new Color(70, 130, 180));
        customerPanel.add(customerCountLabel, BorderLayout.CENTER);
        
        statsPanel.add(customerPanel);
        
        // Quick actions
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        quickActionsPanel.setBackground(Color.WHITE);
        
        customerServiceBtn = new JButton("Customer Service");
        customerServiceBtn.setFont(new Font("Arial", Font.BOLD, 14));
        customerServiceBtn.setBackground(new Color(70, 130, 180));
        customerServiceBtn.setForeground(Color.WHITE);
        customerServiceBtn.setFocusPainted(false);
        customerServiceBtn.addActionListener(e -> cardLayout.show(mainPanel, "customerService"));
        quickActionsPanel.add(customerServiceBtn);
        
        transactionsBtn = new JButton("View All Transactions");
        transactionsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        transactionsBtn.setBackground(new Color(50, 150, 100));
        transactionsBtn.setForeground(Color.WHITE);
        transactionsBtn.setFocusPainted(false);
        transactionsBtn.addActionListener(e -> showAllTransactions());
        quickActionsPanel.add(transactionsBtn);
        
        statsPanel.add(quickActionsPanel);
        
        contentPanel.add(statsPanel);
        
        // Right panel - Recent transactions
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        recentTransactionsLabel = new JLabel("Recent Transactions");
        recentTransactionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        recentTransactionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        recentPanel.add(recentTransactionsLabel, BorderLayout.NORTH);
        
        recentTransactionsArea = new JTextArea();
        recentTransactionsArea.setEditable(false);
        recentTransactionsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        recentTransactionsArea.setLineWrap(true);
        recentTransactionsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsArea);
        recentPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(recentPanel);
        
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(dashboardPanel, "dashboard");
    }
    
    private void createCustomerServicePanel() {
        JPanel customerServicePanel = new JPanel(new BorderLayout());
        customerServicePanel.setBackground(new Color(240, 240, 240));
        customerServicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Customer Service");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        backToDashboardBtn = new JButton("Back to Dashboard");
        backToDashboardBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backToDashboardBtn.setBackground(new Color(50, 150, 100));
        backToDashboardBtn.setForeground(Color.WHITE);
        backToDashboardBtn.setFocusPainted(false);
        backToDashboardBtn.addActionListener(e -> {
            updateDashboard();
            cardLayout.show(mainPanel, "dashboard");
        });
        headerPanel.add(backToDashboardBtn, BorderLayout.EAST);
        
        customerServicePanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create buttons with icons and styling
        createCustomerBtn = createServiceButton("Create Customer", "icons/add-user.png", new Color(70, 130, 180));
        createCustomerBtn.addActionListener(e -> showCreateCustomerForm());
        
        deleteCustomerBtn = createServiceButton("Delete Customer", "icons/remove-user.png", new Color(220, 80, 60));
        deleteCustomerBtn.addActionListener(e -> showDeleteCustomerForm());
        
        editCustomerBtn = createServiceButton("Edit Customer", "icons/edit-user.png", new Color(255, 165, 0));
        editCustomerBtn.addActionListener(e -> showEditCustomerForm());
        
        depositWithdrawBtn = createServiceButton("Deposit/Withdraw", "icons/money-transfer.png", new Color(50, 150, 100));
        depositWithdrawBtn.addActionListener(e -> showDepositWithdrawForm());
        
        balanceEnquiryBtn = createServiceButton("Balance Enquiry", "icons/balance.png", new Color(147, 112, 219));
        balanceEnquiryBtn.addActionListener(e -> showBalanceEnquiryForm());
        
        JButton transactionHistoryBtn = createServiceButton("Transaction History", "icons/history.png", new Color(75, 0, 130));
        transactionHistoryBtn.addActionListener(e -> showTransactionHistoryForm());
        
        contentPanel.add(createCustomerBtn);
        contentPanel.add(deleteCustomerBtn);
        contentPanel.add(editCustomerBtn);
        contentPanel.add(depositWithdrawBtn);
        contentPanel.add(balanceEnquiryBtn);
        contentPanel.add(transactionHistoryBtn);
        
        customerServicePanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(customerServicePanel, "customerService");
    }
    
    private JButton createServiceButton(String text, String iconPath, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(150, 120));
        
        // In a real app, you would load actual icons
        // For now, we'll just use text buttons
        return button;
    }
    
    private void updateDashboard() {
        welcomeLabel.setText("Welcome, " + currentEmployeeName);
        
        // Update customer count
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers WHERE account_status = 'Active'");
            if (rs.next()) {
                customerCountLabel.setText(String.valueOf(rs.getInt(1)));
            }
            
            // Update recent transactions
            rs = stmt.executeQuery(
                "SELECT t.transaction_id, c.first_name, c.last_name, t.transaction_type, t.amount, t.transaction_date " +
                "FROM transactions t JOIN customers c ON t.account_number = c.account_number " +
                "ORDER BY t.transaction_date DESC LIMIT 5");
            
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            
            while (rs.next()) {
                sb.append(String.format("%s %s\n", rs.getString("first_name"), rs.getString("last_name")));
                sb.append(String.format("Transaction ID: %s\n", rs.getString("transaction_id")));
                sb.append(String.format("Type: %s, Amount: ₹%.2f\n", 
                    rs.getString("transaction_type"), rs.getDouble("amount")));
                sb.append(String.format("Date: %s\n\n", 
                    sdf.format(rs.getTimestamp("transaction_date"))));
            }
            
            recentTransactionsArea.setText(sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating dashboard: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCreateCustomerForm() {
        JDialog dialog = new JDialog(this, "Create New Customer Account", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("New Customer Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Personal Details Section
        JLabel personalLabel = new JLabel("Personal Details");
        personalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy++;
        panel.add(personalLabel, gbc);
        
        // First Name
        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(firstNameLabel, gbc);
        
        JTextField firstNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        
        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lastNameLabel, gbc);
        
        JTextField lastNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        
        // DOB
        JLabel dobLabel = new JLabel("Date of Birth:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(dobLabel, gbc);
        
        JTextField dobField = new JTextField(20);
        dobField.setToolTipText("YYYY-MM-DD");
        gbc.gridx = 1;
        panel.add(dobField, gbc);
        
        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(genderLabel, gbc);
        
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);
        
        // Contact Details Section
        JLabel contactLabel = new JLabel("Contact Details");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(contactLabel, gbc);
        
        // Address
        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(addressLabel, gbc);
        
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        gbc.gridx = 1;
        panel.add(addressScroll, gbc);
        
        // City
        JLabel cityLabel = new JLabel("City:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(cityLabel, gbc);
        
        JTextField cityField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(cityField, gbc);
        
        // State
        JLabel stateLabel = new JLabel("State:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(stateLabel, gbc);
        
        JTextField stateField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(stateField, gbc);
        
        // Pin Code
        JLabel pinLabel = new JLabel("Pin Code:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(pinLabel, gbc);
        
        JTextField pinField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(pinField, gbc);
        
        // Mobile
        JLabel mobileLabel = new JLabel("Mobile:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(mobileLabel, gbc);
        
        JTextField mobileField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(mobileField, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("Email (Optional):");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Identification Section
        JLabel idLabel = new JLabel("Identification Details");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(idLabel, gbc);
        
        // Aadhar
        JLabel aadharLabel = new JLabel("Aadhar Number:");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(aadharLabel, gbc);
        
        JTextField aadharField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(aadharField, gbc);
        
        // PAN
        JLabel panLabel = new JLabel("PAN Number:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(panLabel, gbc);
        
        JTextField panField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(panField, gbc);
        
        // Account Details Section
        JLabel accountLabel = new JLabel("Account Details");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(accountLabel, gbc);
        
        // Account Type
        JLabel typeLabel = new JLabel("Account Type:");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(typeLabel, gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        
        // Initial Deposit
        JLabel depositLabel = new JLabel("Initial Deposit:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(depositLabel, gbc);
        
        JTextField depositField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(depositField, gbc);
        
        // Buttons
        JButton createBtn = new JButton("Create Account");
        createBtn.setBackground(new Color(70, 130, 180));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(220, 80, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Button actions
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        createBtn.addActionListener(e -> {
            // Validate fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || 
                dobField.getText().isEmpty() || addressArea.getText().isEmpty() || 
                cityField.getText().isEmpty() || stateField.getText().isEmpty() || 
                pinField.getText().isEmpty() || mobileField.getText().isEmpty() || 
                aadharField.getText().isEmpty() || panField.getText().isEmpty()) {
                
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double initialDeposit = Double.parseDouble(depositField.getText());
                if (initialDeposit < 0) {
                    JOptionPane.showMessageDialog(dialog, "Initial deposit cannot be negative", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Insert customer
                PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO customers (first_name, last_name, dob, gender, address, city, " +
                    "state, pin_code, mobile, email, aadhar_number, pan_number, account_type, balance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                
                stmt.setString(1, firstNameField.getText());
                stmt.setString(2, lastNameField.getText());
                stmt.setString(3, dobField.getText());
                stmt.setString(4, (String) genderCombo.getSelectedItem());
                stmt.setString(5, addressArea.getText());
                stmt.setString(6, cityField.getText());
                stmt.setString(7, stateField.getText());
                stmt.setString(8, pinField.getText());
                stmt.setString(9, mobileField.getText());
                stmt.setString(10, emailField.getText().isEmpty() ? null : emailField.getText());
                stmt.setString(11, aadharField.getText());
                stmt.setString(12, panField.getText());
                stmt.setString(13, (String) typeCombo.getSelectedItem());
                stmt.setDouble(14, initialDeposit);
                
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    // Get the auto-generated account number
                    Statement accStmt = connection.createStatement();
                    ResultSet rs = accStmt.executeQuery("SELECT LAST_INSERT_ID()");
                    
                    if (rs.next()) {
                        long accountNumber = rs.getLong(1);
                        
                        // Create initial deposit transaction if amount > 0
                        if (initialDeposit > 0) {
                            createTransaction(accountNumber, "Deposit", initialDeposit, "Initial deposit");
                        }
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Account created successfully!\nAccount Number: " + accountNumber, 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateDashboard();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for initial deposit", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("aadhar_number")) {
                    JOptionPane.showMessageDialog(dialog, "Aadhar number already exists", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else if (ex.getMessage().contains("pan_number")) {
                    JOptionPane.showMessageDialog(dialog, "PAN number already exists", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error creating account: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showDeleteCustomerForm() {
        JDialog dialog = new JDialog(this, "Close Customer Account", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Account Closure");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Account Number
        JLabel accNumLabel = new JLabel("Account Number:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(accNumLabel, gbc);
        
        JTextField accNumField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(accNumField, gbc);
        
        // Verify Button
        JButton verifyBtn = new JButton("Verify Account");
        verifyBtn.setBackground(new Color(70, 130, 180));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(verifyBtn, gbc);
        
        // Customer Details (to be filled after verification)
        JTextArea customerDetailsArea = new JTextArea(8, 30);
        customerDetailsArea.setEditable(false);
        customerDetailsArea.setLineWrap(true);
        customerDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(customerDetailsArea);
        gbc.gridy = 3;
        panel.add(detailsScroll, gbc);
        
        // Reason for closure
        JLabel reasonLabel = new JLabel("Reason for Closure:");
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(reasonLabel, gbc);
        
        JTextArea reasonArea = new JTextArea(3, 30);
        reasonArea.setLineWrap(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        gbc.gridx = 1;
        panel.add(reasonScroll, gbc);
        
        // Buttons
        JButton closeBtn = new JButton("Close Account");
        closeBtn.setBackground(new Color(220, 80, 60));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setEnabled(false);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(closeBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Button actions
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        verifyBtn.addActionListener(e -> {
            String accNumText = accNumField.getText().trim();
            if (accNumText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumText);
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM customers WHERE account_number = ? AND account_status = 'Active'");
                stmt.setLong(1, accountNumber);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Display customer details
                    StringBuilder sb = new StringBuilder();
                    sb.append("Account Holder: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    sb.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                    sb.append("Balance: ₹").append(String.format("%.2f", rs.getDouble("balance"))).append("\n");
                    sb.append("Mobile: ").append(rs.getString("mobile")).append("\n");
                    sb.append("Account Created: ").append(new SimpleDateFormat("dd-MMM-yyyy").format(rs.getDate("account_created_date"))).append("\n");
                    customerDetailsArea.setText(sb.toString());
                    closeBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Account not found or already closed", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    customerDetailsArea.setText("");
                    closeBtn.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        closeBtn.addActionListener(e -> {
            String reason = reasonArea.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter reason for account closure", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumField.getText().trim());
                
                // Get current balance
                PreparedStatement balanceStmt = connection.prepareStatement(
                    "SELECT balance FROM customers WHERE account_number = ?");
                balanceStmt.setLong(1, accountNumber);
                ResultSet rs = balanceStmt.executeQuery();
                
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    
                    // Start transaction
                    connection.setAutoCommit(false);
                    
                    try {
                        // Update account status
                        PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE customers SET account_status = 'Closed' WHERE account_number = ?");
                        updateStmt.setLong(1, accountNumber);
                        updateStmt.executeUpdate();
                        
                        // Record closure
                        PreparedStatement closureStmt = connection.prepareStatement(
                            "INSERT INTO account_closures (account_number, reason, final_balance, handled_by) " +
                            "VALUES (?, ?, ?, ?)");
                        closureStmt.setLong(1, accountNumber);
                        closureStmt.setString(2, reason);
                        closureStmt.setDouble(3, balance);
                        closureStmt.setInt(4, currentEmployeeId);
                        closureStmt.executeUpdate();
                        
                        // Commit transaction
                        connection.commit();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Account closed successfully!\nFinal balance: ₹" + String.format("%.2f", balance) + 
                            "\nPlease hand over this amount to the customer.", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateDashboard();
                    } catch (SQLException ex) {
                        connection.rollback();
                        throw ex;
                    } finally {
                        connection.setAutoCommit(true);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error closing account: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditCustomerForm() {
        JDialog dialog = new JDialog(this, "Edit Customer Details", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Edit Customer Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Account Number
        JLabel accNumLabel = new JLabel("Account Number:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(accNumLabel, gbc);
        
        JTextField accNumField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(accNumField, gbc);
        
        // Verify Button
        JButton verifyBtn = new JButton("Verify Account");
        verifyBtn.setBackground(new Color(70, 130, 180));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(verifyBtn, gbc);
        
        // Customer Details (to be filled after verification)
        JTextArea customerDetailsArea = new JTextArea(10, 30);
        customerDetailsArea.setEditable(false);
        customerDetailsArea.setLineWrap(true);
        customerDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(customerDetailsArea);
        gbc.gridy = 3;
        panel.add(detailsScroll, gbc);
        
        // Fields to edit
        JLabel editLabel = new JLabel("Select Field to Edit:");
        gbc.gridy = 4;
        panel.add(editLabel, gbc);
        
        JComboBox<String> fieldCombo = new JComboBox<>(new String[]{
            "Mobile", "Email", "Address", "City", "State", "Pin Code"
        });
        fieldCombo.setEnabled(false);
        gbc.gridx = 1;
        panel.add(fieldCombo, gbc);
        
        JLabel newValueLabel = new JLabel("New Value:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(newValueLabel, gbc);
        
        JTextField newValueField = new JTextField(20);
        newValueField.setEnabled(false);
        gbc.gridx = 1;
        panel.add(newValueField, gbc);
        
        // Buttons
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(70, 130, 180));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setEnabled(false);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(220, 80, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(updateBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Button actions
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        verifyBtn.addActionListener(e -> {
            String accNumText = accNumField.getText().trim();
            if (accNumText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumText);
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM customers WHERE account_number = ? AND account_status = 'Active'");
                stmt.setLong(1, accountNumber);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Display customer details
                    StringBuilder sb = new StringBuilder();
                    sb.append("Account Holder: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    sb.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                    sb.append("Mobile: ").append(rs.getString("mobile")).append("\n");
                    sb.append("Email: ").append(rs.getString("email") != null ? rs.getString("email") : "Not provided").append("\n");
                    sb.append("Address: ").append(rs.getString("address")).append("\n");
                    sb.append("City: ").append(rs.getString("city")).append("\n");
                    sb.append("State: ").append(rs.getString("state")).append("\n");
                    sb.append("Pin Code: ").append(rs.getString("pin_code")).append("\n");
                    
                    customerDetailsArea.setText(sb.toString());
                    fieldCombo.setEnabled(true);
                    newValueField.setEnabled(true);
                    updateBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Account not found or closed", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    customerDetailsArea.setText("");
                    fieldCombo.setEnabled(false);
                    newValueField.setEnabled(false);
                    updateBtn.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        updateBtn.addActionListener(e -> {
            String newValue = newValueField.getText().trim();
            if (newValue.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a new value", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumField.getText().trim());
                String field = (String) fieldCombo.getSelectedItem();
                String columnName = "";
                
                switch (field) {
                    case "Mobile": columnName = "mobile"; break;
                    case "Email": columnName = "email"; break;
                    case "Address": columnName = "address"; break;
                    case "City": columnName = "city"; break;
                    case "State": columnName = "state"; break;
                    case "Pin Code": columnName = "pin_code"; break;
                }
                
                PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE customers SET " + columnName + " = ? WHERE account_number = ?");
                stmt.setString(1, newValue);
                stmt.setLong(2, accountNumber);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(dialog, "Customer details updated successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    verifyBtn.doClick(); // Refresh the displayed details
                    newValueField.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating customer: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDepositWithdrawForm() {
        JDialog dialog = new JDialog(this, "Deposit/Withdraw", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Deposit/Withdraw");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Account Number
        JLabel accNumLabel = new JLabel("Account Number:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(accNumLabel, gbc);
        
        JTextField accNumField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(accNumField, gbc);
        
        // Verify Button
        JButton verifyBtn = new JButton("Verify Account");
        verifyBtn.setBackground(new Color(70, 130, 180));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(verifyBtn, gbc);
        
        // Customer Details (to be filled after verification)
        JTextArea customerDetailsArea = new JTextArea(5, 30);
        customerDetailsArea.setEditable(false);
        customerDetailsArea.setLineWrap(true);
        customerDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(customerDetailsArea);
        gbc.gridy = 3;
        panel.add(detailsScroll, gbc);
        
        // Transaction Type
        JLabel typeLabel = new JLabel("Transaction Type:");
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(typeLabel, gbc);
        
        // Changed from "Withdraw" to "Withdrawal" to match database ENUM
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Deposit", "Withdrawal"});
        typeCombo.setEnabled(false);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        
        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(amountLabel, gbc);
        
        JTextField amountField = new JTextField(20);
        amountField.setEnabled(false);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        
        // Buttons
        JButton processBtn = new JButton("Process Transaction");
        processBtn.setBackground(new Color(70, 130, 180));
        processBtn.setForeground(Color.WHITE);
        processBtn.setFocusPainted(false);
        processBtn.setEnabled(false);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(220, 80, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(processBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Button actions
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        verifyBtn.addActionListener(e -> {
            String accNumText = accNumField.getText().trim();
            if (accNumText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumText);
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM customers WHERE account_number = ? AND account_status = 'Active'");
                stmt.setLong(1, accountNumber);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Display customer details
                    StringBuilder sb = new StringBuilder();
                    sb.append("Account Holder: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    sb.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                    sb.append("Current Balance: ₹").append(String.format("%.2f", rs.getDouble("balance"))).append("\n");
                    
                    customerDetailsArea.setText(sb.toString());
                    typeCombo.setEnabled(true);
                    amountField.setEnabled(true);
                    processBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Account not found or closed", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    customerDetailsArea.setText("");
                    typeCombo.setEnabled(false);
                    amountField.setEnabled(false);
                    processBtn.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        processBtn.addActionListener(e -> {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an amount", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumField.getText().trim());
                String transactionType = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountText);
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Start transaction
                connection.setAutoCommit(false);
                
                try {
                    // Get current balance
                    PreparedStatement balanceStmt = connection.prepareStatement(
                        "SELECT balance FROM customers WHERE account_number = ? FOR UPDATE");
                    balanceStmt.setLong(1, accountNumber);
                    ResultSet rs = balanceStmt.executeQuery();
                    
                    if (rs.next()) {
                        double currentBalance = rs.getDouble("balance");
                        
                        if (transactionType.equals("Withdrawal") && amount > currentBalance) {
                            JOptionPane.showMessageDialog(dialog, "Insufficient funds", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Update balance
                        double newBalance = transactionType.equals("Deposit") ? 
                            currentBalance + amount : currentBalance - amount;
                        
                        PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE customers SET balance = ? WHERE account_number = ?");
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setLong(2, accountNumber);
                        updateStmt.executeUpdate();
                        
                        // Record transaction - using the correct transaction type
                        createTransaction(accountNumber, transactionType, amount, "Manual transaction by employee");
                        
                        // Commit transaction
                        connection.commit();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Transaction processed successfully!\nNew Balance: ₹" + String.format("%.2f", newBalance), 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateDashboard();
                    }
                } catch (SQLException ex) {
                    connection.rollback();
                    throw ex;
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error processing transaction: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBalanceEnquiryForm() {
        JDialog dialog = new JDialog(this, "Balance Enquiry", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Balance Enquiry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Account Number
        JLabel accNumLabel = new JLabel("Account Number:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(accNumLabel, gbc);
        
        JTextField accNumField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(accNumField, gbc);
        
        // Check Button
        JButton checkBtn = new JButton("Check Balance");
        checkBtn.setBackground(new Color(70, 130, 180));
        checkBtn.setForeground(Color.WHITE);
        checkBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(checkBtn, gbc);
        
        // Result Area
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        gbc.gridy = 3;
        panel.add(resultScroll, gbc);
        
        // Button action
        checkBtn.addActionListener(e -> {
            String accNumText = accNumField.getText().trim();
            if (accNumText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumText);
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT c.first_name, c.last_name, c.balance, c.account_type, " +
                    "(SELECT COUNT(*) FROM transactions t WHERE t.account_number = c.account_number) AS transaction_count " +
                    "FROM customers c WHERE c.account_number = ? AND c.account_status = 'Active'");
                stmt.setLong(1, accountNumber);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Account Holder: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    sb.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                    sb.append("Current Balance: ₹").append(String.format("%.2f", rs.getDouble("balance"))).append("\n");
                    sb.append("Total Transactions: ").append(rs.getInt("transaction_count")).append("\n");
                    
                    resultArea.setText(sb.toString());
                } else {
                    JOptionPane.showMessageDialog(dialog, "Account not found or closed", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    resultArea.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showTransactionHistoryForm() {
        JDialog dialog = new JDialog(this, "Transaction History", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel accNumLabel = new JLabel("Account Number:");
        searchPanel.add(accNumLabel);
        
        JTextField accNumField = new JTextField(15);
        searchPanel.add(accNumField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Transaction Table
        String[] columnNames = {"Date", "Type", "Amount", "Description"};
        Object[][] data = {};
        
        JTable transactionTable = new JTable(data, columnNames);
        transactionTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button action
        searchBtn.addActionListener(e -> {
            String accNumText = accNumField.getText().trim();
            if (accNumText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long accountNumber = Long.parseLong(accNumText);
                
                // Verify account exists
                PreparedStatement verifyStmt = connection.prepareStatement(
                    "SELECT first_name, last_name FROM customers WHERE account_number = ?");
                verifyStmt.setLong(1, accountNumber);
                ResultSet verifyRs = verifyStmt.executeQuery();
                
                if (!verifyRs.next()) {
                    JOptionPane.showMessageDialog(dialog, "Account not found", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String customerName = verifyRs.getString("first_name") + " " + verifyRs.getString("last_name");
                dialog.setTitle("Transaction History - " + customerName);
                
                // Get transactions
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT transaction_date, transaction_type, amount, description " +
                    "FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC");
                stmt.setLong(1, accountNumber);
                
                ResultSet rs = stmt.executeQuery();
                
                // Convert result set to table model
                DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                
                while (rs.next()) {
                    Object[] row = {
                        sdf.format(rs.getTimestamp("transaction_date")),
                        rs.getString("transaction_type"),
                        String.format("₹%.2f", rs.getDouble("amount")),
                        rs.getString("description")
                    };
                    model.addRow(row);
                }
                
                transactionTable.setModel(model);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid account number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAllTransactions() {
        JDialog dialog = new JDialog(this, "All Transactions", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Transaction Table
        String[] columnNames = {"Date", "Account", "Customer", "Type", "Amount", "Description"};
        Object[][] data = {};
        
        JTable transactionTable = new JTable(data, columnNames);
        transactionTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT t.transaction_date, t.account_number, c.first_name, c.last_name, " +
                "t.transaction_type, t.amount, t.description " +
                "FROM transactions t JOIN customers c ON t.account_number = c.account_number " +
                "ORDER BY t.transaction_date DESC");
            
            // Convert result set to table model
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            
            while (rs.next()) {
                Object[] row = {
                    sdf.format(rs.getTimestamp("transaction_date")),
                    rs.getLong("account_number"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("transaction_type"),
                    String.format("₹%.2f", rs.getDouble("amount")),
                    rs.getString("description")
                };
                model.addRow(row);
            }
            
            transactionTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void createTransaction(long accountNumber, String type, double amount, String description) throws SQLException {
        String transactionId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        
        PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, description) " +
            "VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, transactionId);
        stmt.setLong(2, accountNumber);
        stmt.setString(3, type);
        stmt.setDouble(4, amount);
        stmt.setString(5, description);
        stmt.executeUpdate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankEmployeeSystem system = new BankEmployeeSystem();
            system.setVisible(true);
        });
    }   
}