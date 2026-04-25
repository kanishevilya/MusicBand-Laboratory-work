package client.command;

import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface LineDispatchHandler {

    void dispatch(String line) throws IOException, TimeoutException, ProtocolException, DeserializationException;
}
