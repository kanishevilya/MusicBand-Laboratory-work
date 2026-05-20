package restgateway.network;

import common.exception.ProtocolException;
import common.net.udp.SimpleUdpStreamFraming;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.util.BinaryProtocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UdpGatewayImpl implements UdpGateway {
    private final String serverHost;
    private final int serverPort;
    private static final int BUFFER_SIZE = 65535;
    private static final int TIMEOUT_MS = 5000;

    public UdpGatewayImpl(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public AbstractResponse send(AbstractRequest request) throws Exception {
        byte[] requestBytes = BinaryProtocol.serialize(request);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress address = InetAddress.getByName(serverHost);

            for (byte[] chunk : SimpleUdpStreamFraming.split(requestBytes)) {
                DatagramPacket p = new DatagramPacket(chunk, chunk.length, address, serverPort);
                socket.send(p);
            }

            SimpleUdpStreamFraming.StreamReceiver receiver = new SimpleUdpStreamFraming.StreamReceiver();
            byte[] responseBuffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(receivePacket);

                int len = receivePacket.getLength();
                byte[] chunkData = new byte[len];
                System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), chunkData, 0, len);

                byte[] completePayload = receiver.feed(chunkData, chunkData.length);

                if (completePayload != null) {
                    return BinaryProtocol.deserialize(completePayload, AbstractResponse.class);
                }
            }
        } catch (SocketTimeoutException e) {
            throw new java.net.ConnectException("Сервер не ответил на пакеты фрейминга за " + TIMEOUT_MS + " мс");
        } catch (ProtocolException e) {
            throw new RuntimeException("Ошибка транспортного протокола фрейминга при обмене с сервером: " + e.getMessage(), e);
        }
    }
}