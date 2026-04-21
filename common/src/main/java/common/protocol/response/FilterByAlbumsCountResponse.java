package common.protocol.response;

import common.model.MusicBand;
import common.protocol.AbstractResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterByAlbumsCountResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    private final List<MusicBand> bands;

    public FilterByAlbumsCountResponse(long requestId, boolean success, String message, List<MusicBand> bands) {
        super(requestId, success, message);
        this.bands = bands == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(bands));
    }

    public List<MusicBand> getBands() {
        return bands;
    }

    @Override
    public String getMessage() {
        String header = super.getMessage();
        if (bands.isEmpty()) {
            return header + "\n(нет элементов)";
        }
        return header + "\n\n"
                + bands.stream().map(MusicBand::toString)
                        .collect(Collectors.joining("\n\n"));
    }
}
