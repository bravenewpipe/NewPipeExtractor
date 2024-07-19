package org.schabi.newpipe.extractor.services.bitchute.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.search.filter.BitchuteFilters;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BitchuteSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    private static final BitchuteSearchQueryHandlerFactory INSTANCE =
            new BitchuteSearchQueryHandlerFactory();
    private static final String SEARCH_URL = BitchuteConstants.SEARCH_URL_PREFIX;

    private BitchuteSearchQueryHandlerFactory() {
        super(new BitchuteFilters());
    }

    public static BitchuteSearchQueryHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(
            final String query,
            @Nonnull final List<FilterItem> selectedContentFilter,
            @Nullable final List<FilterItem> selectedSortFilter)
            throws ParsingException {

        searchFilters.setSelectedContentFilter(selectedContentFilter);
        searchFilters.setSelectedSortFilter(selectedSortFilter);

        final String sortQuery = searchFilters.evaluateSelectedFilters(null);

        try {
            return SEARCH_URL
                    + Utils.encodeUrlUtf8(query)
                    + Objects.requireNonNullElse(sortQuery, "");
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }
}
