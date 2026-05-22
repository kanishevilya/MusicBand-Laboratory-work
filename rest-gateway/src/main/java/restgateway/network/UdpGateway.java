package restgateway.network;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;

public interface UdpGateway {
    AbstractResponse sendAndReceive(AbstractRequest request) throws Exception;
}