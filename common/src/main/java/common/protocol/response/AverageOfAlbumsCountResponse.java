package common.protocol.response;

import common.protocol.AbstractResponse;

public final class AverageOfAlbumsCountResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    private final double average;

    public AverageOfAlbumsCountResponse(long requestId, boolean success, String message, double average) {
        super(requestId, success, message);
        this.average = average;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + System.lineSeparator() + "Среднее albumsCount: " + average;
    }
}
