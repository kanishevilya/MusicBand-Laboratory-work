package server.network;

import java.net.SocketAddress;

/**
 * Контейнер для хранения "сырых" считанных из сети байт и адреса отправителя.
 * Необходим для передачи данных между пулом чтения и пулом обработки.
 */
public class RawPack {
    private final byte[] data;
    private final SocketAddress clientAddress;

    public RawPack(byte[] data, SocketAddress clientAddress) {
        this.data = data;
        this.clientAddress = clientAddress;
    }

    public byte[] getData() { return data; }
    public SocketAddress getClientAddress() { return clientAddress; }
}