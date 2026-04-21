package common.protocol.response;

import common.model.MusicBand;
import common.protocol.AbstractResponse;

public final class GetByKeyResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    private final MusicBand band;

    public GetByKeyResponse(long requestId, boolean success, String message, MusicBand band) {
        super(requestId, success, message);
        this.band = band;
    }

    public MusicBand getBand() {
        return band;
    }
}
