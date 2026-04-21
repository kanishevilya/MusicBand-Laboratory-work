package server;

import common.exception.ProtocolException;
import common.protocol.AbstractResponse;
import common.util.BinaryProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Отправка ответа по UDP.
 */
public class DatagramResponseModule {

    public static final int MAX_UDP_PAYLOAD = 65507;

    private final DatagramSocket socket;

    public DatagramResponseModule(DatagramSocket socket) {
        this.socket = socket;
    }

    public void send(AbstractResponse response, SocketAddress to) throws IOException, ProtocolException {
        byte[] bytes = BinaryProtocol.serialize(response);
        if (bytes.length > MAX_UDP_PAYLOAD) {
            throw new ProtocolException("Ответ слишком велик для одной UDP-датаграммы: " + bytes.length + " байт");
        }
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, to);
        socket.send(packet);
    }
}
