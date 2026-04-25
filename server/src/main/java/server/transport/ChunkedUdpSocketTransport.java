package server.transport;

import common.exception.ProtocolException;
import common.net.udp.SimpleUdpStreamFraming;
import common.util.BinaryProtocol;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Objects;


public final class ChunkedUdpSocketTransport {

    private final DatagramSocket socket;
    private final byte[] buffer;
    private final SimpleUdpStreamFraming.StreamReceiver receiver = new SimpleUdpStreamFraming.StreamReceiver();
    private SocketAddress expectedSender;

    public ChunkedUdpSocketTransport(DatagramSocket socket, int maxPacketSize) {
        this.socket = socket;
        int buf = Math.max(maxPacketSize, SimpleUdpStreamFraming.PACKET_SIZE);
        this.buffer = new byte[buf];
    }

    public record Received(byte[] payload, SocketAddress replyAddress) {
    }

    public Received receiveComplete() throws IOException, ProtocolException {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            int len = packet.getLength();
            SocketAddress from = packet.getSocketAddress();
            byte[] data = new byte[len];
            System.arraycopy(packet.getData(), packet.getOffset(), data, 0, len);

            if (expectedSender == null) {
                expectedSender = from;
                receiver.reset();
            } else if (!Objects.equals(expectedSender, from)) {
                receiver.reset();
                expectedSender = null;
                throw new ProtocolException("Фрагмент от другого адреса во время сборки сообщения: " + from);
            }

            try {
                byte[] complete = receiver.feed(data, data.length);
                if (complete != null) {
                    expectedSender = null;
                    return new Received(complete, from);
                }
            } catch (ProtocolException e) {
                receiver.reset();
                expectedSender = null;
                throw e;
            }
        }
    }

    public void sendComplete(SocketAddress to, Serializable object) throws IOException, ProtocolException {
        byte[] bytes = BinaryProtocol.serialize(object);
        for (byte[] chunk : SimpleUdpStreamFraming.split(bytes)) {
            DatagramPacket p = new DatagramPacket(chunk, chunk.length, to);
            socket.send(p);
        }
    }
}
