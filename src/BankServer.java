import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankServer extends Thread {
    private static int balance = 1000;  // Начальный баланс счета
    private static Lock lock = new ReentrantLock();  // Для синхронизации

    ServerSocket server;

    public void run() {
        try {
            server = new ServerSocket(3001);  // Создаем серверный сокет на порту 3001
            System.out.println("Сервер запущен, ожидает подключения клиентов...");

            while (true) {
                Socket clientSocket = server.accept();  // Ожидаем подключения клиентов
                System.out.println("Клиент подключен");

                // Обрабатываем клиента в новом потоке
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера: " + e);
        }
    }

    // Вложенный класс для обработки клиента
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request;
                while ((request = in.readLine()) != null) {
                    String[] parts = request.split(" ");
                    if (parts.length < 2) {
                        out.println("Ошибка: неверный формат команды.");
                        continue;
                    }

                    String command = parts[0];
                    int amount = Integer.parseInt(parts[1]);

                    if ("add".equalsIgnoreCase(command)) {
                        addMoney(amount, out);
                    } else if ("withdraw".equalsIgnoreCase(command)) {
                        withdrawMoney(amount, out);
                    } else {
                        out.println("Неверная команда");
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка связи с клиентом: " + e);
            }
        }

        private void addMoney(int amount, PrintWriter out) {
            lock.lock();
            try {
                balance += amount;
                out.println("Добавлено " + amount + ". Текущий баланс: " + balance);
                System.out.println("Добавлено " + amount + ". Текущий баланс: " + balance);
            } finally {
                lock.unlock();
            }
        }

        private void withdrawMoney(int amount, PrintWriter out) {
            lock.lock();
            try {
                if (balance >= amount) {
                    balance -= amount;
                    out.println("Снято " + amount + ". Текущий баланс: " + balance);
                    System.out.println("Снято " + amount + ". Текущий баланс: " + balance);
                } else {
                    out.println("Недостаточно средств. Текущий баланс: " + balance);
                    System.out.println("Недостаточно средств. Текущий баланс: " + balance);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
