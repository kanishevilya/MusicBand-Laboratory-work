package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Приём UDP-датаграмм.
 */
public class DatagramConnectionModule {

    private final DatagramSocket socket;
    private final byte[] buffer;

    public DatagramConnectionModule(DatagramSocket socket, int maxPacketSize) {
        this.socket = socket;
        this.buffer = new byte[maxPacketSize];
    }

    public ReceivedDatagram receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return new ReceivedDatagram(data, packet.getSocketAddress());
    }

    public record ReceivedDatagram(byte[] payload, SocketAddress replyAddress) {
    }
}
