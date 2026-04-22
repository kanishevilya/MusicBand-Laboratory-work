package common.net.udp;

import common.exception.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Простая нарезка потока по UDP: пакет = до {@value #DATA_SIZE} байт данных + 1 байт-маркер
 * ({@value #CONTINUATION} — ещё есть продолжение, {@value #END} — последний фрагмент).
 */
public final class SimpleUdpStreamFraming {

    public static final int PACKET_SIZE = 1024;
    public static final int DATA_SIZE = PACKET_SIZE - 1;
    public static final byte CONTINUATION = 0;
    public static final byte END = 1;
    public static final int MAX_MESSAGE_BYTES = 50 * 1024 * 1024;

    private SimpleUdpStreamFraming() {
    }

    public static List<byte[]> split(byte[] message) throws ProtocolException {
        if (message.length > MAX_MESSAGE_BYTES) {
            throw new ProtocolException("Сообщение слишком большое: " + message.length + " байт");
        }
        List<byte[]> packets = new ArrayList<>(Math.max(1, (message.length + DATA_SIZE - 1) / DATA_SIZE));
        int offset = 0;
        while (offset < message.length) {
            int len = Math.min(DATA_SIZE, message.length - offset);
            boolean last = offset + len >= message.length;
            byte[] packet = new byte[len + 1];
            System.arraycopy(message, offset, packet, 0, len);
            packet[len] = last ? END : CONTINUATION;
            offset += len;
            packets.add(packet);
        }
        if (packets.isEmpty()) {
            packets.add(new byte[] { END });
        }
        return packets;
    }

    /**
     * Последовательная сборка одного сообщения из датаграмм.
     */
    public static final class StreamReceiver {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public void reset() {
            buffer.reset();
        }

        /**
         * @return полное сообщение при маркере {@link #END}; {@code null}, если нужны ещё пакеты
         */
        public byte[] feed(byte[] datagram, int length) throws ProtocolException {
            if (length < 1) {
                reset();
                throw new ProtocolException("Пустая датаграмма");
            }
            byte marker = datagram[length - 1];
            if (marker != CONTINUATION && marker != END) {
                reset();
                throw new ProtocolException("Некорректный маркер фрагмента: " + marker);
            }
            int dataLen = length - 1;
            if (dataLen > 0) {
                buffer.write(datagram, 0, dataLen);
            }
            if (buffer.size() > MAX_MESSAGE_BYTES) {
                reset();
                throw new ProtocolException("Превышен лимит размера сообщения");
            }
            if (marker == END) {
                byte[] complete = buffer.toByteArray();
                reset();
                return complete;
            }
            return null;
        }
    }
}
