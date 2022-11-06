// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bandcamp.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.bandcamp.search.filter.BandcampFilters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.schabi.newpipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;

public final class BandcampSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public BandcampSearchQueryHandlerFactory() {
        super(new BandcampFilters());
    }

    @Override
    public String getUrl(final String query,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter) throws ParsingException {


        searchFilters.setSelectedSortFilter(selectedSortFilter);
        searchFilters.setSelectedContentFilter(selectedContentFilter);

        final String filterQuery = searchFilters.evaluateSelectedContentFilters();

        try {
            return BASE_URL + "/search?q=" + URLEncoder.encode(query, "UTF-8")
                    + filterQuery + "&page=1";
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("query \"" + query + "\" could not be encoded", e);
        }
    }
}
