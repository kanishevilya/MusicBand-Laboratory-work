package common.protocol.request;

import common.protocol.AbstractRequest;

public final class FilterByAlbumsCountRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final int albumsCount;

    public FilterByAlbumsCountRequest(long requestId, int albumsCount) {
        super(requestId);
        this.albumsCount = albumsCount;
    }

    public int getAlbumsCount() {
        return albumsCount;
    }
}
