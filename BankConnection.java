
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class BankConnection {

    public static String sendRequest(
        String command,
        String cardNumber,
        String pin,
        double amount
    ) throws Exception {

        try (
            Socket socket = new Socket("localhost", 5000);

            DataOutputStream output =
                new DataOutputStream(socket.getOutputStream());

            DataInputStream input =
                new DataInputStream(socket.getInputStream())
        ) {

            // Send command
            output.writeUTF(command);

            // Send card number
            output.writeUTF(cardNumber);

            // Send additional data when required
            if (command.equals("LOGIN")) {

                output.writeUTF(pin);

            } else if (
                command.equals("WITHDRAW") ||
                command.equals("DEPOSIT")
            ) {

                output.writeDouble(amount);
            }

            output.flush();

            // Receive server response
            return input.readUTF();
        }
    }
}
