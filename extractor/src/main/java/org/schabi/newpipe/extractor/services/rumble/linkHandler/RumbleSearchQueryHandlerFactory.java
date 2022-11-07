package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.rumble.search.filter.RumbleFilters;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public final class RumbleSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String UTF_8 = "UTF-8";
    private static RumbleSearchQueryHandlerFactory instance = null;

    private RumbleSearchQueryHandlerFactory() {
        super(new RumbleFilters());
    }

    public static synchronized RumbleSearchQueryHandlerFactory getInstance() {
        if (instance == null) {
            instance = new RumbleSearchQueryHandlerFactory();
        }
        return instance;
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter)
            throws ParsingException {

        searchFilters.setSelectedSortFilter(selectedSortFilter);
        searchFilters.setSelectedContentFilter(selectedContentFilter);

        final String sortQuery = searchFilters.evaluateSelectedSortFilters();
        final String urlEndpoint = searchFilters.evaluateSelectedContentFilters();


        try {
            return urlEndpoint
                    + URLEncoder.encode(searchString, UTF_8)
                    + sortQuery;
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
