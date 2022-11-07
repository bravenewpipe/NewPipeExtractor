package org.schabi.newpipe.extractor.services.bitchute;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.schabi.newpipe.extractor.NewPipe.getDownloader;
import static org.schabi.newpipe.extractor.services.bitchute.BitchuteService.BITCHUTE_LINK;

public final class BitchuteParserHelper {

    private BitchuteParserHelper() { }

    private static String cookies;
    private static String csrfToken;

    public static boolean isInitDone() {
        if (cookies == null || csrfToken == null || cookies.isEmpty() || csrfToken.isEmpty()) {
            return false;
        }
        return true;
    }

    public static void init() throws ReCaptchaException, IOException {
        final Response response = getDownloader().get(BITCHUTE_LINK);
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry entry : response.responseHeaders().entrySet()) {
            if (entry.getKey().equals("set-cookie")) {
                final List<String> values = (List<String>) entry.getValue();
                for (final String v : values) {
                    final String val = v.split(";", 2)[0];
                    sb.append(val).append(";");
                    if (val.contains("csrf")) {
                        csrfToken = val.split("=", 2)[1];
                    }
                }
                break;
            }
        }
        cookies = sb.toString();
    }

    public static Map<String, List<String>> getPostHeader(final int contentLength)
            throws IOException, ReCaptchaException {
        final Map<String, List<String>> headers = getBasicHeader();
        headers.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));
        headers.put("Content-Length", Collections.singletonList(String.valueOf(contentLength)));
        System.out.println("Headers: ");
        for (final Map.Entry m : headers.entrySet()) {
            System.out.println(m.getKey() + ": " + m.getValue());
        }
        return headers;
    }

    public static Map<String, List<String>> getBasicHeader()
            throws IOException, ReCaptchaException {
        if (!isInitDone()) {
            init();
        }
        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("Cookie", Collections.singletonList(cookies));
        headers.put("Referer", Collections.singletonList(BITCHUTE_LINK));
        return headers;
    }

    public static String getSubscriberCountForChannelID(final String channelID)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data =
                String.format("csrfmiddlewaretoken=%s", csrfToken).getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format(BitchuteConstants.BASE_URL_CHANNEL + "/%s/counts/", channelID),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return String.valueOf(jsonObject.getLong("subscriber_count"));
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute sub count json");
        }
    }

    public static VideoCount getVideoCountObjectForStreamID(final String streamID)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data =
                String.format("csrfmiddlewaretoken=%s", csrfToken).getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format(BitchuteConstants.BASE_URL_VIDEO + "/%s/counts/", streamID),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return (new VideoCount(jsonObject.getInt("like_count"),
                    jsonObject.getInt("dislike_count"), jsonObject.getInt("view_count")));
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute sub count json");
        }

    }

    public static Document getExtendDocumentForUrl(final String pageUrl, final String offset)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data = String.format("csrfmiddlewaretoken=%s&offset=%s", csrfToken, offset)
                .getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format("%s/extend/", pageUrl),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return Jsoup.parse(jsonObject.getString("html")
                    .replace("\n", ""), pageUrl);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute sub count json");
        }
    }

    public static JsonObject getSearchResultForQuery(final String query, final String sortQuery,
                                                     final int pageNumber)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final String dataWithPlaceholders =
                "csrfmiddlewaretoken=%s&query=%s%s&page=%d";

        final byte[] data =
                String.format(dataWithPlaceholders, csrfToken, query, sortQuery, pageNumber)
                        .getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format("%s/api/search/list/", BitchuteConstants.BASE_URL),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            final String keySuccess = "success";
            final String keyResults = "results";
            if (jsonObject.has(keySuccess) && jsonObject.getBoolean(keySuccess)
                    && jsonObject.has(keyResults)) {
                return jsonObject;
            }
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute search results JsonObject");
        }
        throw new ExtractionException(
                "Server response for bitchute search results was not successful");
    }

    public static class VideoCount {
        private final long likeCount;
        private final long dislikeCount;
        private final long viewCount;

        public VideoCount(final long likeCount, final long dislikeCount, final long viewCount) {
            this.likeCount = likeCount;
            this.dislikeCount = dislikeCount;
            this.viewCount = viewCount;
        }

        public long getLikeCount() {
            return likeCount;
        }

        public long getDislikeCount() {
            return dislikeCount;
        }

        public long getViewCount() {
            return viewCount;
        }
    }
}
