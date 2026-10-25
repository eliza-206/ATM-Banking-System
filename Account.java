
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private String cardNumber;
    private String pin;
    private double balance;

    private List<String> transactions = new ArrayList<>();

    // Constructor
    public Account(String cardNumber, String pin, double balance) {

        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;

        transactions.add("Account created | Balance: ₹" + balance);
    }

    // Validate login
    public synchronized boolean validateLogin(
        String enteredCard, String enteredPin) {

        return cardNumber.equals(enteredCard)
                && pin.equals(enteredPin);
    }

    // Balance enquiry
    public synchronized String checkBalance() {

        return "BALANCE_SUCCESS|Current Balance: ₹"
                + String.format("%.2f", balance);
    }

    // Withdraw money
    public synchronized String withdraw(double amount) {

        if (amount <= 0) {
            return "WITHDRAW_FAILED|Enter a valid amount.";
        }

        if (amount > balance) {
            return "WITHDRAW_FAILED|Insufficient balance.";
        }

        balance -= amount;

        transactions.add(
            "Withdrawal: ₹" + amount + " | " + LocalDateTime.now()
        );

        return "WITHDRAW_SUCCESS|Withdrawal successful! "
                + "Remaining balance: ₹" + balance;
    }

    // Deposit money
    public synchronized String deposit(double amount) {

        if (amount <= 0) {
            return "DEPOSIT_FAILED|Enter a valid amount.";
        }

        balance += amount;

        transactions.add(
            "Deposit: ₹" + amount + " | " + LocalDateTime.now()
        );

        return "DEPOSIT_SUCCESS|Deposit successful! "
                + "Updated balance: ₹" + balance;
    }

    // Mini statement
    public synchronized String miniStatement() {

        StringBuilder statement = new StringBuilder();

        statement.append("MINI STATEMENT\n\n");

        for (String transaction : transactions) {
            statement.append(transaction).append("\n");
        }

        return "STATEMENT_SUCCESS|" + statement;
    }
}
