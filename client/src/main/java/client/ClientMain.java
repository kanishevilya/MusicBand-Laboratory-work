package client;

import client.command.ClientLineDispatcher;
import client.network.UdpClient;
import client.util.InputHandler;
import common.exception.CancelInputException;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.exception.ScriptEndException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.auth.LoginRequest;
import common.protocol.auth.RegisterRequest;
import common.util.BinaryProtocol;

import java.io.Console;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class ClientMain {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;
    private static final int TIMEOUT_MS = 5000;

    private static String sessionLogin = null;
    private static String sessionPassword = null;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : HOST;
        int port = args.length > 1 ? parseInt(args[1], "порт") : PORT;
        int timeoutMs = args.length > 2 ? parseInt(args[2], "таймаут") : TIMEOUT_MS;

        AtomicLong requestIds = new AtomicLong(0);
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new InputHandler(scanner, true);

        try (UdpClient client = new UdpClient(host, port, timeoutMs)) {
            System.out.println("Подключение к серверу MusicBand (" + host + ":" + port + ")...");

            while (true) {
                performAuthentication(client, scanner, requestIds);

                ClientLineDispatcher dispatcher = new ClientLineDispatcher(client, inputHandler, requestIds, sessionLogin, sessionPassword);

                System.out.println("\nДобро пожаловать, " + sessionLogin + "!");
                System.out.println("Введите help для просмотра команд, logout для смены пользователя, exit для выхода.");

                boolean sessionActive = true;
                while (sessionActive) {
                    System.out.print("\n> ");
                    try {
                        String line = scanner.nextLine().trim();
                        if (line.isEmpty()) continue;

                        if (line.equalsIgnoreCase("exit")) {
                            System.out.println("Завершение клиента...");
                            System.exit(0);
                        }

                        if (line.equalsIgnoreCase("logout")) {
                            System.out.println("Выход из аккаунта " + sessionLogin + "...");
                            sessionLogin = null;
                            sessionPassword = null;
                            sessionActive = false; // Прерываем внутренний цикл
                            continue;
                        }

                        dispatcher.handleLine(line);

                    } catch (TimeoutException e) {
                        System.out.println("Сервер недоступен: " + e.getMessage());
                    } catch (DeserializationException e) {
                        System.out.println("Ошибка разбора ответа: " + e.getMessage());
                    } catch (ProtocolException e) {
                        System.out.println("Ошибка протокола: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Ошибка обмена: " + e.getMessage());
                    } catch (NoSuchElementException e) {
                        System.out.println("\nEOF. Завершение клиента.");
                        System.exit(0);
                    } catch (CancelInputException | ScriptEndException e) {
                        System.out.println(e.getMessage());
                        if (e instanceof ScriptEndException) break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Критическая ошибка сети: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void performAuthentication(UdpClient client, Scanner scanner, AtomicLong requestIds) {
        while (sessionLogin == null) {
            System.out.println("\n--- Авторизация ---");
            System.out.println("1. Войти");
            System.out.println("2. Зарегистрироваться");
            System.out.print("Выберите действие (1/2): ");

            String choice = scanner.nextLine().trim();
            if (!choice.equals("1") && !choice.equals("2")) {
                System.out.println("Пожалуйста, введите 1 или 2.");
                continue;
            }

            System.out.print("Логин: ");
            String login = scanner.nextLine().trim();

            String password = readPassword(scanner);

            if (login.isEmpty() || password.isEmpty()) {
                System.out.println("Логин и пароль не могут быть пустыми!");
                continue;
            }

            try {
                long reqId = requestIds.incrementAndGet();
                AbstractRequest authReq = choice.equals("1")
                        ? new LoginRequest(reqId)
                        : new RegisterRequest(reqId);

                authReq.setLogin(login);
                authReq.setPassword(password);

                AbstractResponse response = client.sendAndReceive(authReq);

                if (response.isSuccess()) {
                    System.out.println(response.getMessage());
                    sessionLogin = login;
                    sessionPassword = password;
                } else {
                    System.out.println(response.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Ошибка связи с сервером при авторизации: " + e.getMessage());
            }
        }
    }

    private static String readPassword(Scanner scanner) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword("Пароль: ");
            return new String(chars);
        } else {
            System.out.print("Пароль (ввод виден в IDE): ");
            return scanner.nextLine().trim();
        }
    }

    private static int parseInt(String s, String label) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.err.println("Некорректный " + label + ": " + s);
            System.exit(1);
            return 0;
        }
    }
}