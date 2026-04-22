package client;

import client.command.ClientLineDispatcher;
import client.network.UdpClient;
import client.util.InputHandler;
import common.exception.CancelInputException;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.exception.ScriptEndException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;


public class ClientMain {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? parseInt(args[1], "порт") : 5555;
        int timeoutMs = args.length > 2 ? parseInt(args[2], "таймаут") : 5000;

        AtomicLong requestIds = new AtomicLong(0);
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new InputHandler(scanner, true);

        try (UdpClient client = new UdpClient(host, port, timeoutMs)) {
            ClientLineDispatcher dispatcher = new ClientLineDispatcher(client, inputHandler, requestIds);
            System.out.println("Клиент MusicBand. Сервер " + host + ":" + port + ", таймаут " + timeoutMs + " мс.");
            System.out.println("Введите help. Команда exit завершает клиент.");

            while (true) {
                System.out.print("\n> ");
                try {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.equalsIgnoreCase("exit")) {
                        break;
                    }
                    try {
                        dispatcher.handleLine(line);
                    } catch (TimeoutException e) {
                        System.out.println(e.getMessage());
                    }  catch (DeserializationException e) {
                        System.out.println("Ошибка разбора ответа: " + e.getMessage());
                    }  catch (ProtocolException e) {
                        System.out.println("Ошибка протокола: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Ошибка обмена: " + e.getMessage());
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("\nEOF. Завершение клиента.");
                    break;
                } catch (CancelInputException e) {
                    System.out.println(e.getMessage());
                } catch (ScriptEndException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка сети: " + e.getMessage());
            System.exit(1);
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
