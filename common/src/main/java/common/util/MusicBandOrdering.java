package common.util;

import common.model.MusicBand;

import java.util.Comparator;

public final class MusicBandOrdering {

    public static final Comparator<MusicBand> BY_NAME_THEN_ID =
            Comparator.comparing(MusicBand::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingLong(MusicBand::getId);

    private MusicBandOrdering() {
    }
}
