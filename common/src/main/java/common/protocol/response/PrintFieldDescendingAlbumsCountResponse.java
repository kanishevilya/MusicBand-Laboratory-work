package common.protocol.response;

import common.protocol.AbstractResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PrintFieldDescendingAlbumsCountResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    private final List<Integer> albumsCountsDescending;

    public PrintFieldDescendingAlbumsCountResponse(long requestId, boolean success, String message,
            List<Integer> albumsCountsDescending) {
        super(requestId, success, message);
        this.albumsCountsDescending = albumsCountsDescending == null ? List.of()
                : Collections.unmodifiableList(new ArrayList<>(albumsCountsDescending));
    }

    public List<Integer> getAlbumsCountsDescending() {
        return albumsCountsDescending;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n"
                + albumsCountsDescending.stream().map(String::valueOf).collect(Collectors.joining("\n"));
    }
}
