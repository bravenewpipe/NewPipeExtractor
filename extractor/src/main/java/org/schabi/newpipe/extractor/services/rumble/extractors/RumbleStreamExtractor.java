package org.schabi.newpipe.extractor.services.rumble.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.PrivateContentException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.DeliveryMethod;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamSegment;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.SubtitlesStream;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.schabi.newpipe.extractor.stream.Stream.ID_UNKNOWN;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

public class RumbleStreamExtractor extends StreamExtractor {

    private final String videoUploaderJsonKey = "author";
    private final String videoTitleJsonKey = "title";
    private final String videoCoverImageJsonKey = "i";
    private final String videoDateJsonKey = "pubDate";
    private final String videoDurationJsonKey = "duration";
    private final String bitrateJsonKey = "bitrate";
    private final String resHeightJsonKey = "h";

    private final String videoViewerCountHtmlKey =
            "div.media-engage div.video-counters--item.video-item--views";
    private final String relatedStreamHtmlKey = "ul.mediaList-list";

    private Document doc;
    JsonObject embedJsonStreamInfoObj;

    private int ageLimit = -1;
    private List<VideoStream> videoStreams;
    private String hlsUrl = "";

    public RumbleStreamExtractor(final StreamingService service, final LinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        assertPageFetched();
        final String title =
                Parser.unescapeEntities(embedJsonStreamInfoObj.getString(videoTitleJsonKey), true);

        return title;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {

        final String textualDate = embedJsonStreamInfoObj.getString(videoDateJsonKey);
        return textualDate;
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = getTextualUploadDate();


        if (isNullOrEmpty(textualUploadDate)) {
            return null;
        }
        // the format is: 2021-02-08T19:37:25+00:00  youtube-dl pares it with iso8601b
        return new DateWrapper(YoutubeParsingHelper.parseDateFrom(textualUploadDate), false);
    }

    @Nonnull
    @Override
    public List<Image> getThumbnails() throws ParsingException {
        assertPageFetched();
        final String thumbUrl = embedJsonStreamInfoObj.getString(videoCoverImageJsonKey);
        return List.of(new Image(thumbUrl,
                Image.HEIGHT_UNKNOWN, Image.WIDTH_UNKNOWN, Image.ResolutionLevel.UNKNOWN));
    }

    @Nonnull
    @Override
    public Description getDescription() throws ParsingException {
        assertPageFetched();
        String description = "";

        final Elements descriptionData = doc.select("p.media-description");
        if (!descriptionData.isEmpty()) {
            final List<Node> nodes = descriptionData.first().childNodes();

            // the node that contains the the description may vary.
            // Some videos do not have a description at all
            for (final Node node : nodes) {
                if (node instanceof TextNode) {
                    if (!((TextNode) node).isBlank()) {
                        description += node.toString();
                    }
                }
            }
        }

        return new Description(Parser.unescapeEntities(description, false), Description.PLAIN_TEXT);
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        if (ageLimit == -1) {
            ageLimit = NO_AGE_LIMIT;
        }

        return ageLimit;
    }

    @Override
    public long getLength() throws ParsingException {
        assertPageFetched();
        final Number duration = embedJsonStreamInfoObj.getNumber(videoDurationJsonKey);

        return duration.longValue();
    }

    /**
     * @return 0 means no timestamp is found.
     */
    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getViewCount() throws ParsingException {
        assertPageFetched();
        if (getStreamType() == StreamType.LIVE_STREAM) {
            return getLiveViewCount();
        } else {
            final String errorMsg = "Could not extract the view count";
            try {
                final String viewCount =
                        RumbleParsingHelper.extractSafely(true, errorMsg,
                                () -> doc.select("div.media-description-info-views")
                                        .first().text());
                return Utils.mixedNumberWordToLong(viewCount.replace(",", ""));
            } catch (final NumberFormatException e) {
                throw new ParsingException(errorMsg, e);
            }
        }
    }

    @Override
    public long getLikeCount() throws ParsingException {
        return -1;
    }

    @Override
    public long getDislikeCount() throws ParsingException {
        return -1;
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        assertPageFetched();
        return embedJsonStreamInfoObj.getObject(videoUploaderJsonKey).getString("url");
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        assertPageFetched();
        final String uploaderName =
                embedJsonStreamInfoObj.getObject(videoUploaderJsonKey).getString("name");
        return uploaderName;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        // TODO can be done
        return false;
    }

    @Nonnull
    @Override
    public List<Image> getUploaderAvatars() throws ParsingException {
        assertPageFetched();
        final Elements elems = doc.getElementsByClass("media-by--a");
        final String theUserPathToHisAvatar =
                elems.get(0).getElementsByTag("i").first().attributes().get("class");
        try {
            final String thumbnailUrl = RumbleParsingHelper
                    .totalMessMethodToGetUploaderThumbnailUrl(theUserPathToHisAvatar, doc);
            return List.of(new Image(thumbnailUrl,
                    Image.HEIGHT_UNKNOWN, Image.WIDTH_UNKNOWN, Image.ResolutionLevel.UNKNOWN));
        } catch (final Exception e) {
            throw new ParsingException(
                    "Could not extract the avatar url: " + theUserPathToHisAvatar);
        }
    }

    @Nonnull
    @Override
    public String getSubChannelUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getSubChannelName() {
        return "";
    }

    @Nonnull
    @Override
    public String getDashMpdUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getHlsUrl() {
        try {
            this.hlsUrl = embedJsonStreamInfoObj.getObject("ua").getObject("hls")
                    .getObject("auto").getString("url", "");

        } catch (final Exception e) {

        }
        return this.hlsUrl;
    }

    @Override
    public List<AudioStream> getAudioStreams() {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoStreams() throws ExtractionException {
        if (videoStreams == null) {
            videoStreams = extractVideoStreams();
        }
        return videoStreams;
    }

    private List<VideoStream> extractVideoStreams() throws ExtractionException {
        assertPageFetched();

        final List<VideoStream> videoStreamsList = new ArrayList<>();
        final String videoAlternativesKey = "ua";
        final String videoMetaKey = "meta";
        final String videoUrlKey = "url";

        final Set<String> formatKeys =
                embedJsonStreamInfoObj.getObject(videoAlternativesKey).keySet();
        for (final String formatKey : formatKeys) { // mp4 or webm or whatever format

            // For some videos there is also a "timeline stream" that is identified
            // by the key 'timeline'. It has only one frame per second and is
            // not useful here --> so we skip it
            if (formatKey.equals("timeline")) {
                continue;
            }

            // todo validate if we want to support this formats or not
            final JsonObject formatObj =
                    embedJsonStreamInfoObj.getObject(videoAlternativesKey).getObject(formatKey);
            final Set<String> resolutionKeys = formatObj.keySet();
            for (final String res : resolutionKeys) { // 240, 360 , 480 ...
                // todo validate if we support this resolution

                final JsonObject metadata =
                        formatObj.getObject(res).getObject(videoMetaKey); // size w h bitrate
                final String videoUrl =
                        formatObj.getObject(res).getString(videoUrlKey); // where the mp4 sits

                if (formatKey.equals("hls") && getStreamType() == StreamType.LIVE_STREAM) {
                    return fakeVideoStreamForLiveStream(videoStreamsList, videoUrl);
                }

                // rumble has some videos resolution data incorrect in 'res'
                // --> now we use the apparently correct resolution from the available metadata.
                // --> as the resolution is not sufficient to distinguish the streams, we also
                //     add the bitrate to the resolution specification, e.g: "1080p@2000k"
                final String actualRes;
                final String bitrate;
                if (metadata.has(resHeightJsonKey) && metadata.has(bitrateJsonKey)) {
                    actualRes = String.valueOf(metadata.getInt(resHeightJsonKey));
                    bitrate = "@" + metadata.getInt(bitrateJsonKey) + "k";
                } else {
                    actualRes = res;
                    bitrate = "";
                }

                final MediaFormat format = MediaFormat.getFromSuffix(formatKey);
                final VideoStream.Builder builder = new VideoStream.Builder()
                        .setId(ID_UNKNOWN)
                        .setIsVideoOnly(false)
                        .setResolution(actualRes + "p" + bitrate)
                        .setContent(videoUrl, true)
                        .setMediaFormat(format);

                videoStreamsList.add(builder.build());
            }
        }

        return videoStreamsList;
    }

    private List<VideoStream> fakeVideoStreamForLiveStream(
            final List<VideoStream> videoStreamsList,
            final String videoUrl) {
        final VideoStream.Builder builder = new VideoStream.Builder()
                .setId(ID_UNKNOWN)
                .setIsVideoOnly(false)
                .setResolution("")
                .setContent(videoUrl, false) // is HLS manifest
                .setDeliveryMethod(DeliveryMethod.HLS)
                // 'mp4' is just chosen as it seems the most common
                .setMediaFormat(MediaFormat.MPEG_4);

        videoStreamsList.add(builder.build());
        return videoStreamsList;
    }

    @Override
    public StreamType getStreamType() {
        assertPageFetched();
        final String videoLiveStreamKey = "live";
        final Number isLive = embedJsonStreamInfoObj.getNumber(videoLiveStreamKey);
        // '1' is also assumed live stream. TODO check if that is true
        final boolean isLiveStream = (isLive.intValue() == 1 || isLive.intValue() == 2);
        return isLiveStream ? StreamType.LIVE_STREAM : StreamType.VIDEO_STREAM;
    }

    @Nullable
    @Override
    public StreamInfoItemsCollector getRelatedItems() throws ExtractionException {
        final List<Node> nodes = doc.select(relatedStreamHtmlKey).first().childNodes();
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        for (final Node node : nodes) {
            collector.commit(new RumbleStreamRelatedInfoItemExtractor(
                    getTimeAgoParser(), node, doc
            ));
        }

        return collector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessage() {
        return null;
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {

        final Response response = downloader.get(getUrl());
        final String rb = response.responseBody();
        doc = Jsoup.parse(rb, getUrl());

        checkIfVideoIsAccessible(response);

        final String queryUrl = "https://rumble.com/embedJS/u3/?request=video&ver=2&v=v"
                + RumbleParsingHelper.getEmbedVideoId(rb);

        final Response response2 = downloader.get(
                queryUrl);

        // TODO keep some cookies to be more browser like
        //curl 'https://rumble.com/embedJS/u3/?request=video&ver=2&v=vb294t&ext=%7B%22ad_count%22%3Anull%7D&ad_wt=0'
        // -H 'Referer: https://rumble.com/vdofb7-1-year-old-pulls-pony-behind-electric-car.html'
        // -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)
        // Chrome/80.0.3987.149 Safari/537.36' -H 'Sec-Fetch-Dest: empty' --compressed
        //Document doc = Jsoup.parse(response.responseBody(), getUrl());
        try {
            embedJsonStreamInfoObj = JsonParser.object().from(response2.responseBody());
        } catch (final JsonParserException e) {
            e.printStackTrace();
            throw new ParsingException("Could not read json from: " + queryUrl);
        }
    }

    private void checkIfVideoIsAccessible(final Response response) throws PrivateContentException {
        if (response.responseCode() == 403) {
            String errMsg = "This video is private.";
            if (doc != null && doc.title() != null && !doc.title().isEmpty()) {
                errMsg = doc.title();

            }

            throw new PrivateContentException(errMsg);
        }
    }

    @Nonnull
    @Override
    public String getHost() {
        return "";
    }

    @Nonnull
    @Override
    public Privacy getPrivacy() {
        return Privacy.PUBLIC;
    }

    @Nonnull
    @Override
    public String getCategory() {
        return "";
    }

    @Nonnull
    @Override
    public String getLicence() {
        return "";
    }

    @Override
    public Locale getLanguageInfo() {
        return null;
    }

    @Nonnull
    @Override
    public List<String> getTags() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public String getSupportInfo() {
        return "";
    }

    @Nonnull
    @Override
    public List<StreamSegment> getStreamSegments() throws ParsingException {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<SubtitlesStream> getSubtitlesDefault() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<SubtitlesStream> getSubtitles(final MediaFormat format) {
        return Collections.emptyList();
    }

    private long getLiveViewCount() throws ParsingException {
        final Pattern matchChecksum = Pattern.compile("viewer_id: \"(.*)\"");
        final Matcher matcher = matchChecksum.matcher(doc.toString());
        if (!matcher.find()) {
            throw new ParsingException("Could not extract viewer_id");
        }
        final String viewerId = matcher.group(1);

        return retrieveLiveStreamViewerCount(getDownloader(),
                retrieveNumericVideoId(),
                viewerId);
    }

    private long retrieveLiveStreamViewerCount(final Downloader downloader,
                                               final String videoNumericId,
                                               final String theViewerId) {
        try {

            // This is the post version of below get version. It does not work but kept here
            // for maybe later:)
            // final Response response = downloader
            //         .post("https://wn0.rumble.com/service.php?api=7&name=video.watching-now",
            //                 null,
            //                 ("video_id=" + videoId + "&viewer_id=" + theViewerId).getBytes());
            final Response response = downloader
                    .get("https://wn0.rumble.com/service.php?video_id="
                            + videoNumericId
                            + "&viewer_id="
                            + theViewerId
                            + "&name=video.watching-now");
            final JsonObject jsonObject =
                    JsonParser.object().from(response.responseBody());
            return jsonObject.getObject("data").getLong("viewer_count", -1);
        } catch (final IOException | ReCaptchaException | JsonParserException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // as of somewhere in 2023 it is no longer working. Kept for reference
    private String createRandomViewerId() {
        // the magic 8 comes from: $$.generateRandomID(8));
        // from the html page that belongs to the video
        return YoutubeParsingHelper.generateTParameter().substring(0, 8);
    }

    private String retrieveNumericVideoId() {
        return embedJsonStreamInfoObj.get("vid").toString();
    }
}
