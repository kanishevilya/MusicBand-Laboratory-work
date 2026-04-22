package server.script;

import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Стек открытых скриптов по адресу клиента (только проверка рекурсии, без чтения файлов).
 */
public final class ScriptRecursionGuard {

    private static final ConcurrentHashMap<String, Deque<String>> STACKS = new ConcurrentHashMap<>();

    private ScriptRecursionGuard() {
    }

    public static String key(SocketAddress client) {
        return client == null ? "" : client.toString();
    }

    public static boolean contains(SocketAddress client, String pathToken) {
        Deque<String> st = STACKS.get(key(client));
        if (st == null) {
            return false;
        }
        return st.contains(pathToken);
    }

    public static void push(SocketAddress client, String pathToken) {
        String k = key(client);
        STACKS.computeIfAbsent(k, x -> new ArrayDeque<>()).push(pathToken);
    }

    /**
     * Снимает вершину стека, если она совпадает с ожидаемым токеном.
     *
     * @return сообщение об ошибке или {@code null}, если всё корректно
     */
    public static String tryPop(SocketAddress client, String pathToken) {
        String k = key(client);
        Deque<String> st = STACKS.get(k);
        if (st == null || st.isEmpty()) {
            return "Нет открытого скрипта для закрытия (ожидался script begin).";
        }
        if (!pathToken.equals(st.peek())) {
            return "Нарушение порядка вложенности скриптов (сначала закройте внутренний файл). Ожидался конец: "
                    + st.peek();
        }
        st.pop();
        if (st.isEmpty()) {
            STACKS.remove(k);
        }
        return null;
    }
}
