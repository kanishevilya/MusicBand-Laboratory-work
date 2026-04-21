package client.command;

import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Повторная диспетчеризация строки (для execute_script).
 */
@FunctionalInterface
public interface LineDispatchHandler {

    void dispatch(String line) throws IOException, TimeoutException, ProtocolException, DeserializationException;
}
