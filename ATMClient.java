
import javax.swing.*;
import java.awt.*;

public class ATMClient extends JFrame {

    private JTextField cardField;
    private JPasswordField pinField;
    private JButton loginButton;

    private final Color NAVY = new Color(15, 35, 65);
    private final Color BLUE = new Color(35, 105, 190);
    private final Color LIGHT = new Color(242, 246, 252);

    public ATMClient() {

        setTitle("ATM Banking System");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT);

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(NAVY);
        header.setPreferredSize(new Dimension(500, 130));

        JLabel title = new JLabel(
            "ATM BANKING SYSTEM", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
            "WELCOME TO YOUR BANK", JLabel.CENTER);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        header.add(title);
        header.add(subtitle);

        // Login form
        JPanel form = new JPanel(new GridLayout(6, 1, 10, 12));
        form.setBackground(LIGHT);
        form.setBorder(
            BorderFactory.createEmptyBorder(35, 55, 30, 55));

        JLabel cardLabel = new JLabel("ATM Card Number");
        cardField = new JTextField();

        JLabel pinLabel = new JLabel("Enter PIN");
        pinField = new JPasswordField();

        loginButton = new JButton("LOGIN");
        loginButton.setBackground(BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));

        JButton clearButton = new JButton("CLEAR");

        form.add(cardLabel);
        form.add(cardField);
        form.add(pinLabel);
        form.add(pinField);
        form.add(loginButton);
        form.add(clearButton);

        loginButton.addActionListener(e -> login());

        clearButton.addActionListener(e -> {
            cardField.setText("");
            pinField.setText("");
        });

        JLabel footer = new JLabel(
            "Secure Banking System",
            JLabel.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Login
    private void login() {

        String card = cardField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (card.isEmpty() || pin.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please enter card number and PIN.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("CONNECTING...");

        SwingWorker<String, Void> worker =
            new SwingWorker<String, Void>() {

            protected String doInBackground() throws Exception {

                return BankConnection.sendRequest(
                    "LOGIN", card, pin, 0);
            }

            protected void done() {

                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");

                try {

                    String response = get();

                    if (response.equals("LOGIN_SUCCESS")) {

                        JOptionPane.showMessageDialog(
                            ATMClient.this, "Login Successful!");

                        dispose();
                        new Dashboard(card);

                    } else {

                        JOptionPane.showMessageDialog(
                            ATMClient.this,
                            "Invalid Card Number or PIN!");
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                        ATMClient.this,
                        "Server connection failed: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new ATMClient());
    }
}


// DASHBOARD CLASS

class Dashboard extends JFrame {

    private final Color NAVY = new Color(15, 35, 65);
    private final Color LIGHT = new Color(242, 246, 252);
    private final Color BLUE = new Color(35, 105, 190);

    private String cardNumber;

    public Dashboard(String cardNumber) {

        this.cardNumber = cardNumber;

        setTitle("ATM Dashboard");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(LIGHT);

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(NAVY);
        header.setPreferredSize(new Dimension(650, 110));

        JLabel title = new JLabel(
            "WELCOME TO YOUR ACCOUNT", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel account = new JLabel(
            "Account: " + cardNumber, JLabel.CENTER);
        account.setForeground(Color.WHITE);

        header.add(title);
        header.add(account);

        // Operation buttons
        JPanel operations = new JPanel(
            new GridLayout(3, 2, 15, 15));

        operations.setBackground(LIGHT);
        operations.setBorder(
            BorderFactory.createEmptyBorder(35, 45, 35, 45));

        String[] options = {
            "BALANCE ENQUIRY",
            "CASH WITHDRAWAL",
            "CASH DEPOSIT",
            /*"FUND TRANSFER",*/
            "MINI STATEMENT",
            "LOGOUT"
        };

        for (String option : options) {

            JButton button = new JButton(option);

            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(BLUE);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            if (option.equals("LOGOUT")) {
                button.setBackground(NAVY);
            }

            button.addActionListener(e -> handleOperation(option));

            operations.add(button);
        }

        main.add(header, BorderLayout.NORTH);
        main.add(operations, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // Handle dashboard operations
    private void handleOperation(String option) {

        try {

            String response;

            switch (option) {

                case "BALANCE ENQUIRY":

                    response = BankConnection.sendRequest(
                        "BALANCE", cardNumber, "", 0);

                    showResponse(response);
                    break;

                case "CASH WITHDRAWAL":

                    processAmount("WITHDRAW");
                    break;

                case "CASH DEPOSIT":

                    processAmount("DEPOSIT");
                    break;

                case "MINI STATEMENT":

                    response = BankConnection.sendRequest(
                        "STATEMENT", cardNumber, "", 0);

                    showStatement(response);
                    break;

                case "FUND TRANSFER":

                    JOptionPane.showMessageDialog(
                        this,
                        "Fund transfer will be implemented next!");

                    break;

                case "LOGOUT":

                    dispose();
                    new ATMClient();
                    break;
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Transaction Error: " + ex.getMessage());
        }
    }

    // Deposit and withdrawal
    private void processAmount(String command) {

        String message = command.equals("WITHDRAW")
            ? "Enter withdrawal amount:"
            : "Enter deposit amount:";

        String value = JOptionPane.showInputDialog(this, message);

        if (value == null) {
            return;
        }

        try {

            double amount = Double.parseDouble(value);

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new NumberFormatException();
            }

            String response = BankConnection.sendRequest(
                command, cardNumber, "", amount);

            showResponse(response);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this, "Please enter a valid positive amount.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this, "Transaction Error: " + ex.getMessage());
        }
    }

    // Display transaction response
    private void showResponse(String response) {

        String[] parts = response.split("\\|", 2);

        String message = parts.length > 1
            ? parts[1]
            : response;

        JOptionPane.showMessageDialog(this, message);
    }

    // Display mini statement
    private void showStatement(String response) {

        String[] parts = response.split("\\|", 2);

        String message = parts.length > 1
            ? parts[1]
            : response;

        JTextArea area = new JTextArea(message, 12, 35);

        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(area);

        JOptionPane.showMessageDialog(
            this,
            scroll,
            "ATM MINI STATEMENT",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
