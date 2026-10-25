
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class BankServer {

    public static void main(String[] args) {

        HashMap<String, Account> accounts = new HashMap<>();

        // Sample accounts
        accounts.put("1001",
            new Account("1001", "1234", 10000));

        accounts.put("1002",
            new Account("1002", "5678", 15000));

        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            System.out.println("================================");
            System.out.println("       BANK SERVER STARTED");
            System.out.println("================================");
            System.out.println("Listening on port 5000...");

            // Accept multiple client connections
            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("New ATM Client Connected!");

                // Handle each client in a separate thread
                Thread clientThread = new Thread(() ->
                    handleClient(clientSocket, accounts)
                );

                clientThread.start();
            }

        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    // Handle client requests
    private static void handleClient(
        Socket socket, HashMap<String, Account> accounts) {

        try (
            Socket clientSocket = socket;

            DataInputStream input =
                new DataInputStream(clientSocket.getInputStream());

            DataOutputStream output =
                new DataOutputStream(clientSocket.getOutputStream())
        ) {

            // Receive command
            String command = input.readUTF();

            // Receive card number
            String cardNumber = input.readUTF();

            Account account = accounts.get(cardNumber);

            String response;

            // Process login
            if (command.equals("LOGIN")) {

                String pin = input.readUTF();

                if (account != null &&
                    account.validateLogin(cardNumber, pin)) {

                    response = "LOGIN_SUCCESS";

                    System.out.println("Login successful: " + cardNumber);

                } else {

                    response = "LOGIN_FAILED";

                    System.out.println("Login failed: " + cardNumber);
                }

            }

            // Check whether account exists
            else if (account == null) {

                response = "ERROR|Account not found.";
            }

            // Balance enquiry
            else if (command.equals("BALANCE")) {

                response = account.checkBalance();
            }

            // Cash withdrawal
            else if (command.equals("WITHDRAW")) {

                double amount = input.readDouble();

                response = account.withdraw(amount);
            }

            // Cash deposit
            else if (command.equals("DEPOSIT")) {

                double amount = input.readDouble();

                response = account.deposit(amount);
            }

            // Mini statement
            else if (command.equals("STATEMENT")) {

                response = account.miniStatement();
            }

            else {

                response = "ERROR|Invalid request.";
            }

            // Send response
            output.writeUTF(response);
            output.flush();

            System.out.println("Request: " + command);
            System.out.println("Response sent to ATM.");

        } catch (Exception e) {

            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
