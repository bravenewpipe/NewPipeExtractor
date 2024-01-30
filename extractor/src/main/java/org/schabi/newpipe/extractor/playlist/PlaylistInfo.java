package org.schabi.newpipe.extractor.playlist;

import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage;
import org.schabi.newpipe.extractor.ListInfo;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.utils.ExtractorHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PlaylistInfo extends ListInfo<StreamInfoItem> {

    /**
     * Mixes are handled as particular playlists in NewPipeExtractor. {@link PlaylistType#NORMAL} is
     * for non-mixes, while other values are for the different types of mixes. The type of a mix
     * depends on how its contents are autogenerated.
     */
    public enum PlaylistType {
        /**
         * A normal playlist (not a mix)
         */
        NORMAL,

        /**
         * A mix made only of streams related to a particular stream, for example YouTube mixes
         */
        MIX_STREAM,

        /**
         * A mix made only of music streams related to a particular stream, for example YouTube
         * music mixes
         */
        MIX_MUSIC,

        /**
         * A mix made only of streams from (or related to) the same channel, for example YouTube
         * channel mixes
         */
        MIX_CHANNEL,

        /**
         * A mix made only of streams related to a particular (musical) genre, for example YouTube
         * genre mixes
         */
        MIX_GENRE,
    }

    @SuppressWarnings("RedundantThrows")
    private PlaylistInfo(final int serviceId, final ListLinkHandler linkHandler, final String name)
            throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static PlaylistInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static PlaylistInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final PlaylistExtractor extractor = service.getPlaylistExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static InfoItemsPage<StreamInfoItem> getMoreItems(final StreamingService service,
                                                             final String url,
                                                             final Page page)
            throws IOException, ExtractionException {
        return service.getPlaylistExtractor(url).getPage(page);
    }

    /**
     * Get PlaylistInfo from PlaylistExtractor
     *
     * @param extractor an extractor where fetchPage() was already got called on.
     */
    public static PlaylistInfo getInfo(final PlaylistExtractor extractor)
            throws ExtractionException {

        final PlaylistInfo info = new PlaylistInfo(
                extractor.getServiceId(),
                extractor.getLinkHandler(),
                extractor.getName());
        // collect uploader extraction failures until we are sure this is not
        // just a playlist without an uploader
        final List<Throwable> uploaderParsingErrors = new ArrayList<>();

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setStreamCount(extractor.getStreamCount());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setDescription(extractor.getDescription());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setThumbnails(extractor.getThumbnails());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderUrl(extractor.getUploaderUrl());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setUploaderName(extractor.getUploaderName());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setUploaderAvatars(extractor.getUploaderAvatars());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setSubChannelUrl(extractor.getSubChannelUrl());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setSubChannelName(extractor.getSubChannelName());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setSubChannelAvatars(extractor.getSubChannelAvatars());
        } catch (final Exception e) {
            uploaderParsingErrors.add(e);
        }
        try {
            info.setBanners(extractor.getBanners());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setPlaylistType(extractor.getPlaylistType());
        } catch (final Exception e) {
            info.addError(e);
        }

        // do not fail if everything but the uploader infos could be collected (TODO better comment)
        if (!uploaderParsingErrors.isEmpty()
                && (!info.getErrors().isEmpty() || uploaderParsingErrors.size() < 3)) {
            info.addAllErrors(uploaderParsingErrors);
        }

        final InfoItemsPage<StreamInfoItem> itemsPage
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPage(itemsPage.getNextPage());

        return info;
    }

    private String uploaderUrl = "";
    private String uploaderName = "";
    private String subChannelUrl;
    private String subChannelName;
    private Description description;
    @Nonnull
    private List<Image> banners = List.of();
    @Nonnull
    private List<Image> subChannelAvatars = List.of();
    @Nonnull
    private List<Image> thumbnails = List.of();
    @Nonnull
    private List<Image> uploaderAvatars = List.of();
    private long streamCount;
    private PlaylistType playlistType;

    @Nonnull
    public List<Image> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(@Nonnull final List<Image> thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Nonnull
    public List<Image> getBanners() {
        return banners;
    }

    public void setBanners(@Nonnull final List<Image> banners) {
        this.banners = banners;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(final String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(final String uploaderName) {
        this.uploaderName = uploaderName;
    }

    @Nonnull
    public List<Image> getUploaderAvatars() {
        return uploaderAvatars;
    }

    public void setUploaderAvatars(@Nonnull final List<Image> uploaderAvatars) {
        this.uploaderAvatars = uploaderAvatars;
    }

    public String getSubChannelUrl() {
        return subChannelUrl;
    }

    public void setSubChannelUrl(final String subChannelUrl) {
        this.subChannelUrl = subChannelUrl;
    }

    public String getSubChannelName() {
        return subChannelName;
    }

    public void setSubChannelName(final String subChannelName) {
        this.subChannelName = subChannelName;
    }

    @Nonnull
    public List<Image> getSubChannelAvatars() {
        return subChannelAvatars;
    }

    public void setSubChannelAvatars(@Nonnull final List<Image> subChannelAvatars) {
        this.subChannelAvatars = subChannelAvatars;
    }

    public long getStreamCount() {
        return streamCount;
    }

    public void setStreamCount(final long streamCount) {
        this.streamCount = streamCount;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(final Description description) {
        this.description = description;
    }

    public PlaylistType getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(final PlaylistType playlistType) {
        this.playlistType = playlistType;
    }
}
