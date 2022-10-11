package org.schabi.newpipe.extractor.services.peertube.linkHandler;

import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.peertube.PeertubeHelpers;
import org.schabi.newpipe.extractor.services.peertube.search.filter.PeertubeFilters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

public final class PeertubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String VIDEOS = "videos";
    // sepia is the global index
    public static final String SEPIA_BASE_URL = "https://sepiasearch.org";
    public static final String SEARCH_ENDPOINT = "/api/v1/search/videos";

    private PeertubeSearchQueryHandlerFactory() {
    }

    public static PeertubeSearchQueryHandlerFactory getInstance() {
        return new PeertubeSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilters) throws ParsingException {

        final String baseUrl;
        final Optional<FilterItem> sepiaFilter =
                PeertubeHelpers.getSepiaFilter(selectedContentFilter);
        if (sepiaFilter.isPresent()) {
            baseUrl = SEPIA_BASE_URL;
        } else {
            baseUrl = ServiceList.PeerTube.getBaseUrl();
        }

        return getUrl(searchString, selectedContentFilter, selectedSortFilters, baseUrl);
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter,
                         final String baseUrl) throws ParsingException {
        try {
            searchFilters.setSelectedSortFilter(selectedSortFilter);
            searchFilters.setSelectedContentFilter(selectedContentFilter);

            final String filterQuery = searchFilters.evaluateSelectedFilters(null);

            return baseUrl + SEARCH_ENDPOINT + "?search=" + URLEncoder.encode(searchString, UTF_8)
                    + filterQuery;
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
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
