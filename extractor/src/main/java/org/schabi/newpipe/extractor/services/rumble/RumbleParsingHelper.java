package org.schabi.newpipe.extractor.services.rumble;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public final class RumbleParsingHelper {

    private RumbleParsingHelper() {
    }

    private static final Map<String, List<String>> HEADERS = new HashMap<>();

    public static int parseDurationStringForRelatedStreams(final String input)
            throws ParsingException {
        // input has the form of h:m:s
        return parseDurationString(input, "(:|:|:)");
    }

    public static int parseDurationString(final String input, final String split)
            throws ParsingException, NumberFormatException {

        final String[] splitInput = input.split(split);
        String days = "0";
        String hours = "0";
        String minutes = "0";
        final String seconds;

        switch (splitInput.length) {
            case 4:
                days = splitInput[0];
                hours = splitInput[1];
                minutes = splitInput[2];
                seconds = splitInput[3];
                break;
            case 3:
                hours = splitInput[0];
                minutes = splitInput[1];
                seconds = splitInput[2];
                break;
            case 2:
                minutes = splitInput[0];
                seconds = splitInput[1];
                break;
            case 1:
                seconds = splitInput[0];
                break;
            default:
                throw new ParsingException("Error duration string with unknown format: " + input);
        }

        return ((Integer.parseInt(Utils.removeNonDigitCharacters(days)) * 24
                + Integer.parseInt(Utils.removeNonDigitCharacters(hours))) * 60
                + Integer.parseInt(Utils.removeNonDigitCharacters(minutes))) * 60
                + Integer.parseInt(Utils.removeNonDigitCharacters(seconds));
    }

    /**
     * @param shouldThrowOnError if true a ParsingException is thrown on error
     * @param msg                in case of Exception the error message that is passed
     * @param function           the function that extract the desired string
     * @return the extracted string or null if shouldThrowOnError is set to false
     * @throws ParsingException
     */
    public static String extractSafely(final boolean shouldThrowOnError, final String msg,
                                       final ExtractFunction function) throws ParsingException {
        String retValue = null;
        try {
            retValue = function.run();
        } catch (final Exception e) {
            if (shouldThrowOnError) {
                throw new ParsingException(msg + ": " + e);
            }
        }
        return retValue;
    }

    /**
     * interface for {@link #extractSafely} extractor function
     */
    public interface ExtractFunction {
        String run();
    }

    public static String totalMessMethodToGetUploaderThumbnailUrl(final String classStr,
                                                                  final Document doc)
            throws ParsingException {
        return extractThumbnail(doc, classStr,
                () -> {
                    // extract checksum to use as identifier
                    final Pattern matchChecksum = Pattern.compile("([a-fA-F0-9]{32})");
                    final Matcher match2 = matchChecksum.matcher(classStr);
                    if (match2.find()) {
                        final String chkSum = match2.group(1);
                        return chkSum;
                    } else {
                        return null;
                    }
                });
    }

    /**
     * TODO implement a faster/easier way to achive same goals
     *
     * @param classStr
     * @return null if there was a letter and not a image, xor url with the uploader thumbnail
     * @throws ParsingException
     */
    public static String extractThumbnail(final Document document,
                                          final String classStr,
                                          final ExtractFunction function) throws ParsingException {

        // special case there is only a letter and no image as user thumbnail
        if (classStr.contains("user-image--letter")) {
            // assume uploader name will do the job
            return null;
        }

        final String thumbIdentifier = function.run();
        if (thumbIdentifier == null) {
            return null;
        }

        // extract thumbnail url
        final String matchThat = document.toString();
        final int pos = matchThat.indexOf(thumbIdentifier);
        final String preciselyMatchHere = matchThat.substring(pos);

        final Pattern channelThumbUrl =
                Pattern.compile("\\W+background-image:\\W+url(?:\\()([^)]*)(?:\\));");
        final Matcher match = channelThumbUrl.matcher(preciselyMatchHere);
        if (match.find()) {
            return match.group(1);
        }
        throw new ParsingException("Could not extract thumbUrl: " + thumbIdentifier);
    }

    public static String moreTotalMessMethodToGenerateUploaderUrl(final String classStr,
                                                                  final Document doc,
                                                                  final String uploaderName)
            throws ParsingException, MalformedURLException {

        final String thumbnailUrl = totalMessMethodToGetUploaderThumbnailUrl(classStr, doc);
        if (thumbnailUrl == null) {
            final String uploaderUrl = Rumble.getBaseUrl() + "/user/" + uploaderName
                    // remove all non alphanumeric characters except dash
                    .replaceAll("[^a-zA-Z0-9\\-]", "");
            return uploaderUrl;
        }

        // Again another special case here
        final URL url = Utils.stringToURL(thumbnailUrl);
        if (!url.getAuthority().contains("rmbl.ws") && !url.getAuthority().contains("rumble")) {
            // there is no img hosted on rumble so we can't rely on it to extract the Channel.
            // So we try to use the name here too.
            final String uploaderUrl = Rumble.getBaseUrl() + "/user/" + uploaderName;
            return uploaderUrl;
        }

        // extract uploader name
        final int skipNoOfLetters = 5;
        final String path = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/")
                + 1 // skip '/'
                // the letters are not relevant but cause problems if there is a '-' -> skip them
                + skipNoOfLetters);
        final String[] splitPath = path.split("-", 0);
        final String theUploader = splitPath[1];

        // the uploaderUrl
        final String uploaderUrl = Rumble.getBaseUrl() + "/user/" + theUploader;
        return uploaderUrl;
    }

    public static long getViewCount(final Element element, final String pattern)
            throws ParsingException {
        final String errorMsg = "Could not extract the view count";
        final String viewCount =
                RumbleParsingHelper.extractSafely(true, errorMsg,
                        () -> element.select(pattern).first().text());
        try {
            return Utils.mixedNumberWordToLong(viewCount);
        } catch (final NumberFormatException e) {
            throw new ParsingException(errorMsg, e);
        }
    }

    /**
     * Rumble needs a cookie to avoid 307 return codes for category browse.
     *
     * Generate random cookies -> seems to work for now. Used atm only in
     * {@link org.schabi.newpipe.extractor.services.rumble.extractors.RumbleTrendingExtractor}
     *
     * @return Cookie with random values
     */
    private static String randomCookieGenerator() {
        final String rand = String.valueOf((int) (Math.random() * 10000));
        final String rand2 = String.valueOf((int) (Math.random() * 10000));
        final String randomCookie = "PNRC=" + rand + " ; RNRC=" + rand2;
        return randomCookie;
    }

    public static synchronized Map<String, List<String>> getMinimalHeaders() {
        final String cookie = "Cookie";
        if (!HEADERS.containsKey(cookie)) {
            HEADERS.put("Cookie", Collections.singletonList(randomCookieGenerator()));
        }
        return HEADERS;
    }

    public static String getEmbedVideoId(final String rb) {
        final String VALID_URL = "https?://(?:www\\.)?rumble\\.com/embed/(?:[0-9a-z]+\\.)?([0-9a-z]+)"; // id is group 1
        final String EMBED_REGEX = "(?:<(?:script|iframe)[^>]+\\bsrc=|[\"']embedUrl[\"']\\s*:\\s*)[\"']" + VALID_URL;
        Pattern pattern = Pattern.compile(EMBED_REGEX);
        Matcher matcher = pattern.matcher(rb);
        if (matcher.find()) {
            // Remove v (first character) from the id
            return matcher.group(1).substring(1);
        } else {
            return null;
        }
    }
}
