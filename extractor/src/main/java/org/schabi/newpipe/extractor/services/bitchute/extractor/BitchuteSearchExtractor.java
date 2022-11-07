package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.schabi.newpipe.extractor.services.bitchute.search.filter.BitchuteFilters;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.InfoItemExtractor;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.MultiInfoItemsCollector;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.bitchute.misc.BitchuteHelpers;
import org.schabi.newpipe.extractor.services.bitchute.misc.BitchuteTimeAgoParser;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BitchuteSearchExtractor extends SearchExtractor {

    BitchuteTimeAgoParser timeAgoParser;

    public BitchuteSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
        timeAgoParser = new BitchuteTimeAgoParser();
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {

    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() throws ParsingException {
        return false; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList(); // TODO evermind verify what really should be done
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl(), "0")); // id is used as the page number
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {

        /*
        //TODO evermind: do we need document for something?
        Document document = Jsoup.parse(getDownloader().get(page.getUrl(),
                BitchuteParserHelper.getBasicHeader()).responseBody());
         */

        final List<FilterItem> newQuery = getLinkHandler().getContentFilters();
        if (newQuery == null || newQuery.isEmpty()) {
            throw new RuntimeException("Somehow there is not content filter");
        }
        final FilterItem filter = newQuery.get(0); // assume only one content filter
        if (!(filter instanceof BitchuteFilters.BitchuteFilterItem)) {
            throw new RuntimeException("Somehow this is no valid Bitchute content filter");
        }

        final BitchuteFilters.BitchuteFilterItem contentFilter =
                (BitchuteFilters.BitchuteFilterItem) filter;

        final String sortQuery = contentFilter.getDataParams();
        final String searchString = getLinkHandler().getId();
        int currentPageNumber = Integer.parseInt(page.getId());

        // retrieve the results via json result
        final JsonObject jsonResponse = BitchuteParserHelper
                .getSearchResultForQuery(searchString, sortQuery, currentPageNumber);

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());
        InfoItemExtractor infoItemExtractor;

        final String jsonResultArrayKey = "results";
        /* below keys for the array with jsonResultArrayKey as key */
        final String jsonTitleKey = "name";
        final String jsonVideoPathKey = "path";
        final String jsonDescKey = "description";
        final String jsonPublishedKey = "published";
        final String jsonViewsKey = "views";
        final String jsonKindKey = "kind";
        final String jsonDurationKey = "duration";
        final String jsonUploaderKey = "channel_name";
        final String jsonUploaderUrlKey = "channel_path";

        final JsonArray results = jsonResponse.getArray(jsonResultArrayKey);
        if (results.size() == 0) {
            return new InfoItemsPage<>(collector, null);
        }

        for (final Object elem : results) {
            if (elem instanceof JsonObject) {
                final JsonObject result = (JsonObject) elem;
                final String name = result.getString(jsonTitleKey);
                final String url = BitchuteConstants.BASE_URL + result.getString(jsonVideoPathKey);

                final String thumbUrl = result.getObject("images").getString("thumbnail");
                final String desc = result.getString(jsonDescKey);

                final String kind = result.getString(jsonKindKey);


                switch (kind) {
                    case BitchuteConstants.KIND_CHANNEL:
                        infoItemExtractor = new BitchuteQuickChannelInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                desc
                        );
                        break;
                    case BitchuteConstants.KIND_VIDEO:
                    default:
                        final String textualDate = result.getString(jsonPublishedKey);
                        final String views =
                                BitchuteHelpers.getIntAlwaysAsString(result, jsonViewsKey);
                        final String duration = result.getString(jsonDurationKey);
                        final String videoId = result.getString("id");
                        final String uploader = result.getString(jsonUploaderKey);
                        final String uploaderUrl =
                                BitchuteConstants.BASE_URL + result.getString(jsonUploaderUrlKey);

                        DateWrapper uploadDate = null;
                        // textualDate is sometimes null. Observation 20220812
                        if (textualDate != null) {
                            try {
                                uploadDate = timeAgoParser.parse(textualDate);
                            } catch (final Exception e) {
                                throw new ParsingException("Error Parsing Upload Date: "
                                        + e.getMessage());
                            }
                        }
                        infoItemExtractor = new BitchuteQuickStreamInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                views,
                                textualDate,
                                duration,
                                uploader,
                                uploaderUrl,
                                uploadDate
                        );
                        BitchuteHelpers.VideoDurationCache.addDurationToMap(videoId,
                                ((BitchuteQuickStreamInfoItemExtractor) infoItemExtractor)
                                        .getDuration());
                }
                collector.commit(infoItemExtractor);
            }
        }

        try {

            final String jsonResultsCount = "count";
            final String jsonResultsTotal = "total";

            final long count = jsonResponse.getLong(jsonResultsCount);
            final long total = jsonResponse.getLong(jsonResultsTotal);

            final int maxPages = (count != 0) ? (int) (total / count) : 0;
            if (maxPages > currentPageNumber) {
                currentPageNumber += 1;
                return new InfoItemsPage<>(collector,
                        new Page(getUrl(), String.valueOf(currentPageNumber)));
            } else {
                return new InfoItemsPage<>(collector, null);
            }
        } catch (final Exception e) {
            return new InfoItemsPage<>(collector, null);
        }
    }

    private static class BitchuteQuickStreamInfoItemExtractor implements StreamInfoItemExtractor {
        String viewCount;
        String textualDate;
        String name;
        String url;
        String thumbUrl;
        String duration;
        String uploader;
        String uploaderUrl;
        DateWrapper uploadDate;

        @SuppressWarnings("checkstyle:ParameterNumber")
        BitchuteQuickStreamInfoItemExtractor(final String name, final String url,
                                             final String thumbUrl, final String viewCount,
                                             final String textualDate, final String duration,
                                             final String uploader, final String uploaderUrl,
                                             final DateWrapper uploadDate) {
            this.viewCount = viewCount;
            this.textualDate = textualDate;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
            this.duration = duration;
            this.uploader = uploader;
            this.uploaderUrl = uploaderUrl;
            this.uploadDate = uploadDate;
        }

        @Override
        public StreamType getStreamType() {
            return StreamType.VIDEO_STREAM;
        }

        @Override
        public boolean isAd() {
            return false;
        }

        @Override
        public long getDuration() throws ParsingException {
            return YoutubeParsingHelper.parseDurationString(duration);
        }

        @Override
        public long getViewCount() throws ParsingException {
            return Utils.mixedNumberWordToLong(viewCount);
        }

        @Override
        public String getUploaderName() {
            return this.uploader;
        }

        @Override
        public String getUploaderUrl() {
            return this.uploaderUrl;
        }

        @Nullable
        @Override
        public String getUploaderAvatarUrl() throws ParsingException {
            return null;
        }

        @Override
        public boolean isUploaderVerified() throws ParsingException {
            return false; // TODO evermind: this is just to get it compiled not verified
        }

        @Nullable
        @Override
        public String getTextualUploadDate() {
            return textualDate;
        }

        @Nullable
        @Override
        public DateWrapper getUploadDate() throws ParsingException {
            return uploadDate;
        }

        @Override
        public String getName() throws ParsingException {
            return name;
        }

        @Override
        public String getUrl() throws ParsingException {
            return url;
        }

        @Override
        public String getThumbnailUrl() throws ParsingException {
            return thumbUrl;
        }
    }

    private static class BitchuteQuickChannelInfoItemExtractor implements ChannelInfoItemExtractor {

        String description;
        String name;
        String url;
        String thumbUrl;

        BitchuteQuickChannelInfoItemExtractor(final String name, final String url,
                                              final String thumbUrl, final String description) {
            this.description = description;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
        }

        @Override
        public String getDescription() throws ParsingException {
            return description;
        }

        @Override
        public long getSubscriberCount() throws ParsingException {
            return -1;
        }

        @Override
        public long getStreamCount() throws ParsingException {
            return -1;
        }

        @Override
        public boolean isVerified() throws ParsingException {
            return false; // TODO evermind: this is just to get it compiled not verified
        }

        @Override
        public String getName() throws ParsingException {
            return name;
        }

        @Override
        public String getUrl() throws ParsingException {
            return url;
        }

        @Override
        public String getThumbnailUrl() throws ParsingException {
            return thumbUrl;
        }
    }
}
