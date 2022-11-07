package org.schabi.newpipe.extractor.services.bitchute;

import java.util.Locale;

public final class BitchuteConstants {

    private BitchuteConstants() { }

    public static final String KIND_CHANNEL = "channel";
    public static final String KIND_VIDEO = "video";
    public static final String BASE_URL = "https://www.bitchute.com";
    public static final String BASE_URL_VIDEO = BASE_URL + "/video";
    public static final String BASE_URL_CHANNEL = BASE_URL + "/channel";
    public static final String SEARCH_URL_PREFIX =
            "https://www.bitchute.com/search/?query=";
    // TODO get automation url if there is one
    public static final String SEARCH_URL_AUTOM = SEARCH_URL_PREFIX;
    public static final Locale BITCHUTE_LOCALE = Locale.ENGLISH;
}
