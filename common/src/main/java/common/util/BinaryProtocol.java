package common.util;

import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.*;

/**
 * Сериализация объектов протокола (Java Serialization).
 */
public final class BinaryProtocol {

    private BinaryProtocol() {
    }

    public static byte[] serialize(Serializable object) throws ProtocolException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("Ошибка сериализации объекта", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data, Class<T> type) throws DeserializationException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            if (!type.isInstance(obj)) {
                throw new DeserializationException(
                        "Ожидался тип " + type.getName() + ", получен " + obj.getClass().getName(),
                        null);
            }
            return (T) obj;
        } catch (ClassNotFoundException e) {
            throw new DeserializationException("Неизвестный класс в потоке", e);
        } catch (IOException e) {
            throw new DeserializationException("Ошибка чтения объекта", e);
        }
    }
}
