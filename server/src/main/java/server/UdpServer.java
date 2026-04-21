package server;

import common.exception.ProtocolException;
import common.protocol.AbstractResponse;
import server.transport.IncomingDatagramProcessor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Транспорт: приём датаграмм → обработка через {@link IncomingDatagramProcessor} → ответ.
 */
public class UdpServer {

    public static final int DEFAULT_MAX_PACKET = 65507;

    private final DatagramSocket socket;
    private final DatagramConnectionModule connectionModule;
    private final DatagramResponseModule responseModule;
    private final IncomingDatagramProcessor incomingProcessor;
    private final Queue<DatagramConnectionModule.ReceivedDatagram> pending = new ArrayDeque<>();

    public UdpServer(int port, IncomingDatagramProcessor incomingProcessor) throws SocketException {
        this(new DatagramSocket(port), incomingProcessor);
    }

    public UdpServer(DatagramSocket socket, IncomingDatagramProcessor incomingProcessor) {
        this.socket = socket;
        this.connectionModule = new DatagramConnectionModule(socket, DEFAULT_MAX_PACKET);
        this.responseModule = new DatagramResponseModule(socket);
        this.incomingProcessor = incomingProcessor;
    }

    public void run() throws IOException {
        while (true) {
            pending.add(connectionModule.receive());
            while (!pending.isEmpty()) {
                DatagramConnectionModule.ReceivedDatagram incoming = pending.remove();
                System.out.println("Получено от: " + incoming.replyAddress());
                AbstractResponse response = incomingProcessor.process(incoming.payload());
                try {
                    responseModule.send(response, incoming.replyAddress());
                } catch (ProtocolException e) {
                    System.err.println("Не удалось отправить ответ: " + e.getMessage());
                }
            }
        }
    }

    public void close() {
        socket.close();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }
}
