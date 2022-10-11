package org.schabi.newpipe.extractor.services.youtube.linkHandler;

import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.youtube.search.filter.YoutubeFilters;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;

import javax.annotation.Nonnull;

import java.util.List;

public final class YoutubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String ALL = "all";
    public static final String VIDEOS = "videos";
    public static final String CHANNELS = "channels";
    public static final String PLAYLISTS = "playlists";

    public static final String MUSIC_SONGS = "music_songs";
    public static final String MUSIC_VIDEOS = "music_videos";
    public static final String MUSIC_ALBUMS = "music_albums";
    public static final String MUSIC_PLAYLISTS = "music_playlists";
    public static final String MUSIC_ARTISTS = "music_artists";

    private final YoutubeFilters searchFilters = new YoutubeFilters();

    @Nonnull
    public static YoutubeSearchQueryHandlerFactory getInstance() {
        return new YoutubeSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(final String searchString,
                         @Nonnull final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter) throws ParsingException {
        searchFilters.setSelectedContentFilter(selectedContentFilter);
        searchFilters.setSelectedSortFilter(selectedSortFilter);
        return searchFilters.evaluateSelectedFilters(searchString);
    }

    @Override
    public FilterContainer getAvailableContentFilter() {
        return searchFilters.getContentFilters();
    }

    @Override
    public FilterContainer getContentFilterSortFilterVariant(final int contentFilterId) {
        return searchFilters.getContentFilterSortFilterVariant(contentFilterId);
    }

    @Override
    public FilterItem getFilterItem(final int filterId) {
        return searchFilters.getFilterItem(filterId);
    }
}
