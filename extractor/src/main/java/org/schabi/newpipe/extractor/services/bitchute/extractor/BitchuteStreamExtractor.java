package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.bitchute.misc.BitchuteHelpers;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamSegment;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.SubtitlesStream;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.stream.Stream.ID_UNKNOWN;

public class BitchuteStreamExtractor extends StreamExtractor {

    private Document doc;
    private BitchuteParserHelper.VideoCount videoCount;
    private Elements relatedStreamAsElements;
    private final Map<String, Integer> agemap = new HashMap() {{
        put("Normal - Content that is suitable for ages 16 and over", 16);
    }};

    public BitchuteStreamExtractor(final StreamingService service, final LinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final Response response = downloader.get(
                getUrl(),
                BitchuteParserHelper.getBasicHeader(), getExtractorLocalization());

        doc = Jsoup.parse(response.responseBody(), getUrl());
        videoCount = BitchuteParserHelper.getVideoCountObjectForStreamID(getId());
        relatedStreamAsElements = doc.select(".video-card");

    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        try {
            return doc.select("#video-title").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing stream name");
        }
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        try {
            return doc.select(".video-publish-date").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing textual upload date");
        }
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        int in = getTextualUploadDate().indexOf("on");
        in += 2;
        final String textualDate = getTextualUploadDate().substring(in)
                .replaceAll("(?<=\\d)(st|nd|rd|th)", "").trim();
        try {
            final Date date;
            try {
                final SimpleDateFormat df =
                        new SimpleDateFormat("MMM d, yyyy.", BitchuteConstants.BITCHUTE_LOCALE);
                date = df.parse(textualDate);
            } catch (final ParseException e) {
                throw new ParsingException("Couldn't parse Date: " + textualDate);
            }
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return new DateWrapper(calendar);
        } catch (final Exception e) {
            throw new ParsingException("Error parsing upload date: " + textualDate);
        }
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            return doc.select("#player").first().attr("poster");
        } catch (final Exception e) {
            throw new ParsingException("Error parsing thumbnail url");
        }
    }

    @Nonnull
    @Override
    public Description getDescription() throws ParsingException {
        try {
            return new Description(doc.select("#video-description .full").first().html(),
                    Description.HTML);
        } catch (final Exception e) {
            throw new ParsingException("Error parsing description");
        }
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        try {
            return agemap.get(
                    doc.select("#video-description + table tbody  td:nth-child(2) > a")
                            .get(1).text());
        } catch (final Exception e) {
            throw new ParsingException("Error parsing age limit");
        }
    }

    @Override
    public long getLength() throws ParsingException {
        try {
            // The duration is not easily extractable without using JavaScript
            // at leas AFAIK. If you know a better way to extract the duration
            // let me know.
            //
            // 1. workaround -- get duration from that was populated by
            // BitchuteSearchExtractor, BitchuteTrendingStreamInfoItemExtractor,
            // BitchuteChannelStreamInfoItemExtractor and store it into a cache.
            return BitchuteHelpers.VideoDurationCache.getDurationForVideoId(getId());
        } catch (final NoSuchElementException e) {
            // 2. workaround -- search for the title on Bitchute and extract
            // duration from search result and store it into the cache
            return workAroundToGetDurationFromSearchResults();
            // 3. TODO workaround -- could be a fix in Player in NewPipe
            // that will extract the duration if possible from the video file
        }
    }

    private long workAroundToGetDurationFromSearchResults() {
        try {
            final SearchInfo searchInfo = SearchInfo.getInfo(getService(),
                    getService().getSearchQHFactory()
                            .fromQuery(getName(), Collections.emptyList(), null));

            final List<InfoItem> items = searchInfo.getRelatedItems();
            if (items.size() > 0 && items.get(0) instanceof StreamInfoItem) {
                for (final InfoItem item : items) {
                    final StreamInfoItem infoItem = (StreamInfoItem) item;

                    final String thatUrl  = infoItem.getUrl();
                    final String extractedId = BitchuteHelpers.VideoDurationCache
                            .extractVideoId(thatUrl);

                    if (extractedId != null && getId().equals(extractedId)) {
                        final long duration = infoItem.getDuration();
                        BitchuteHelpers.VideoDurationCache.addDurationToMap(extractedId, duration);

                        return duration;
                    }
                }

                return -1; // evermind TODO is this valid?
            }
        } catch (final ExtractionException | IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getViewCount() {
        return videoCount.getViewCount();
    }

    @Override
    public long getLikeCount() {
        return videoCount.getLikeCount();
    }

    @Override
    public long getDislikeCount() {
        return videoCount.getDislikeCount();
    }

    @Nullable
    @Override
    public StreamInfoItemsCollector getRelatedItems() throws ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        for (int i = 1; i < relatedStreamAsElements.size(); i++) {
            collector.commit(new BitchuteStreamRelatedInfoItemExtractor(
                    getTimeAgoParser(), relatedStreamAsElements.get(i),
                    getUploaderName(), getUploaderUrl()
            ));
        }
        return collector;
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        try {
            return doc.select("#video-watch  p.name a").first().absUrl("href");
        } catch (final Exception e) {
            throw new ParsingException("Error parsing uploader url");
        }
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        try {
            return doc.select("#video-watch  p.name").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing upload name");
        }
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        try {
            return doc.select("#video-watch div.image-container > a > img")
                    .first().attr("data-src");
        } catch (final Exception e) {
            throw new ParsingException("Error parsing upload avatar url");
        }
    }

    @Nonnull
    @Override
    public String getSubChannelUrl() {
        return ""; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public String getSubChannelName() {
        return ""; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public String getSubChannelAvatarUrl() {
        return ""; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public String getDashMpdUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getHlsUrl() {
        return "";
    }

    @Override
    public List<AudioStream> getAudioStreams() {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoStreams() throws ExtractionException {
        try {
            final String videoUrl = doc.select("#player source").first().attr("src");
            final String extension = videoUrl.substring(videoUrl.lastIndexOf(".") + 1);
            final MediaFormat format = MediaFormat.getFromSuffix(extension);

            final VideoStream.Builder builder = new VideoStream.Builder()
                    .setId(ID_UNKNOWN)
                    .setIsVideoOnly(false)
                    .setResolution("480p")
                    .setContent(videoUrl, true)
                    .setMediaFormat(format);

            return Collections.singletonList(builder.build());
        } catch (final Exception e) {
            throw new ParsingException("Error parsing video stream");
        }
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

    @Override
    public StreamType getStreamType() {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Nonnull
    @Override
    public String getHost() {
        return "";
    }

    @Nonnull
    @Override
    public Privacy getPrivacy() {
        return Privacy.OTHER; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public String getCategory() throws ParsingException {
        try {
            return doc.select("#video-description + table tbody  td:nth-child(2) > a")
                    .first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing category");
        }
    }

    @Nonnull
    @Override
    public String getLicence() {
        return "";
    }

    @Nullable
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
        return "https://www.bitchute.com/help-us-grow/";
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
}
