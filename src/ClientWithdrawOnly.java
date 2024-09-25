import java.io.*;
import java.net.*;

public class ClientWithdrawOnly extends Thread {
    private Socket s;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader consoleReader;

    public ClientWithdrawOnly() {
        try {
            s = new Socket("127.0.0.1", 3001);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
    }

    public void run() {
        try {
            String command;
            while (true) {
                System.out.println("Введите команду для снятия (withdraw <сумма>):");
                command = consoleReader.readLine();
                if (command == null || command.isEmpty() || !command.startsWith("withdraw")) {
                    System.out.println("Команда должна начинаться с 'withdraw'.");
                    continue;
                }

                out.println(command);

                String response = in.readLine();
                if (response != null) {
                    System.out.println("Ответ сервера: " + response);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при обмене данными с сервером: " + e);
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                System.out.println("Ошибка при закрытии сокета: " + e);
            }
        }
    }
}
