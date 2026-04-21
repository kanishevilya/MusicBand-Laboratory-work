package common.protocol.response;

import common.model.MusicBand;
import common.protocol.AbstractResponse;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ShowResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    private final TreeMap<Long, MusicBand> bands;

    public ShowResponse(long requestId, boolean success, String message, TreeMap<Long, MusicBand> bands) {
        super(requestId, success, message);
        this.bands = bands == null ? new TreeMap<>() : bands;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n\n"
                + bands.entrySet().stream().map(entry -> "Key: " + entry.getKey() + "  " + entry.getValue().toString())
                        .collect(Collectors.joining("\n\n"));
    }

    public Map<Long, MusicBand> getBands() {
        return bands;
    }
}
