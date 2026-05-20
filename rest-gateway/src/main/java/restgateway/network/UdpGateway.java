package restgateway.network;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;

public interface UdpGateway {
    AbstractResponse send(AbstractRequest request) throws Exception;
}