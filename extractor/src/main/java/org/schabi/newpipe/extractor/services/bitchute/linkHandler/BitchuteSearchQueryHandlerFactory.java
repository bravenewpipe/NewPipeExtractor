package org.schabi.newpipe.extractor.services.bitchute.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.search.filter.BitchuteFilters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public final class BitchuteSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    private static final BitchuteSearchQueryHandlerFactory INSTANCE =
            new BitchuteSearchQueryHandlerFactory();
    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String SEARCH_URL = BitchuteConstants.SEARCH_URL_PREFIX;

    private BitchuteSearchQueryHandlerFactory() {
        super(new BitchuteFilters());
    }

    public static BitchuteSearchQueryHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(final String query, final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter) throws ParsingException {

        searchFilters.setSelectedContentFilter(selectedContentFilter);
        searchFilters.setSelectedSortFilter(selectedSortFilter);
        final String sortQuery = searchFilters.evaluateSelectedFilters(null);

        try {
            return SEARCH_URL + URLEncoder.encode(query, CHARSET_UTF_8)
                    + sortQuery;
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }
}
