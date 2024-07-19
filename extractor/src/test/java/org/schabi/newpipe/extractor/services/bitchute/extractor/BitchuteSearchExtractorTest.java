package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterGroup;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.Bitchute;

/**
 * Test for {@link BitchuteSearchExtractor}
 */
public class BitchuteSearchExtractorTest {


    private static List<FilterItem> defaultContentFilters;
    private static List<FilterItem> defaultSortFilters;

    private static void setDefaultContentAndSortFilters() {
        final FilterContainer fc = Bitchute.getSearchQHFactory().getAvailableContentFilter();
        final int defaultContentFilterItemId =
                fc.getFilterGroups().get(0).getDefaultSelectedFilterId();
        defaultContentFilters = List.of(fc.getFilterItem(defaultContentFilterItemId));

        final List<FilterGroup> sortFilterGroups = Bitchute.getSearchQHFactory()
                .getContentFilterSortFilterVariant(defaultContentFilterItemId).getFilterGroups();
        defaultSortFilters = new ArrayList<>();
        for (final FilterGroup sortFilterGroup : sortFilterGroups) {
            defaultSortFilters.add(fc.getFilterItem(sortFilterGroup.getDefaultSelectedFilterId()));
        }

    }

    @BeforeAll
    public static void setUp() {
        NewPipe.init(DownloaderTestImpl.getInstance());
        setDefaultContentAndSortFilters();
    }

    /**
     * We search for on specific stream and test the result
     * @throws ExtractionException
     * @throws IOException
     */
    @Test
    public void testStreamSearch() throws ExtractionException, IOException {
        final String streamSearchQuery = "battle bus live | the way forward for london - trailer";
        final String expectedUploader = "London Real";

        final SearchExtractor extractor = Bitchute.getSearchExtractor(streamSearchQuery,
                defaultContentFilters, defaultSortFilters);

        final ListExtractor.InfoItemsPage<InfoItem> page = extractor.getInitialPage();
        final StreamInfoItem searchResultItem = (StreamInfoItem) page.getItems().get(0);

        assertEquals(streamSearchQuery, searchResultItem.getName().toLowerCase());
        assertEquals(expectedUploader, searchResultItem.getUploaderName());
        assertTrue(searchResultItem.getThumbnails().get(0).getUrl().endsWith(".jpg"));
        assertTrue(searchResultItem.getThumbnails().get(0).getUrl().contains(".bitchute.com/live/cover_images/"));
        assertEquals(InfoItem.InfoType.STREAM, searchResultItem.getInfoType());
        assertNotNull(searchResultItem.getUploadDate());
    }

    /**
     * Tests searches with multiple pages
     */
    @Test
    public void testMultiplePages() throws ExtractionException, IOException {
        // A query practically guaranteed to have the maximum amount of pages
        final SearchExtractor extractor = Bitchute.getSearchExtractor("battle",
                defaultContentFilters, defaultSortFilters);

        final String expectedUrl = "https://www.bitchute.com/search/?query=battle&kind=video";

        final ListExtractor.InfoItemsPage<InfoItem> infoItemsPage1 = extractor.getInitialPage();
        final Page page2 = infoItemsPage1.getNextPage();
        final ListExtractor.InfoItemsPage<InfoItem> infoItemsPage2 = extractor.getPage(page2);
        final Page page3 = infoItemsPage2.getNextPage();

        assertEquals(expectedUrl, page2.getUrl());
        assertEquals(expectedUrl, page3.getUrl());
        assertEquals("1", page2.getId());
        assertEquals("2", page3.getId());
    }

    public static class DefaultTest extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "noise";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = Bitchute.getSearchExtractor(QUERY,
                    defaultContentFilters, defaultSortFilters);
            extractor.fetchPage();
        }

        @SuppressWarnings("checkstyle:LeftCurly")
        @Override public SearchExtractor extractor() { return extractor; }
        @SuppressWarnings("checkstyle:LeftCurly")
        @Override public StreamingService expectedService() { return Bitchute; }

        @Override public String expectedName() {
            return QUERY;
        }

        @Override public String expectedId() {
            return QUERY;
        }

        @Override public String expectedUrlContains() {
            return BitchuteConstants.SEARCH_URL_PREFIX + QUERY;
        }

        @Override public String expectedOriginalUrlContains() {
            return BitchuteConstants.SEARCH_URL_PREFIX + QUERY;
        }

        @Override public String expectedSearchString() {
            return QUERY;
        }

        @Nullable @Override public String expectedSearchSuggestion() {
            return null;
        }
    }
}
