package org.schabi.newpipe.extractor.services.rumble.linkHandler;


import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;
import org.schabi.newpipe.extractor.services.rumble.search.filter.RumbleFilters;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public class RumbleSearchQueryHandlerFactoryTest {

    @Test
    public void testRegularValues() throws Exception {

        assertEquals("https://rumble.com/search/video?q=Mark+Dice",
                Rumble.getSearchQHFactory().fromQuery("Mark Dice").getUrl());
        assertEquals("https://rumble.com/search/video?q=paul",
                Rumble.getSearchQHFactory().fromQuery("paul").getUrl());
    }

    @Test
    public void testGetContentFilter() throws Exception {
        final FilterItem videoFilterItem = DefaultSearchExtractorTest
                .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
        final FilterItem channelsFilterItem = DefaultSearchExtractorTest
                .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_CHANNELS);
        assertEquals(videoFilterItem.getIdentifier(), Rumble.getSearchQHFactory()
                .fromQuery("", singletonList(videoFilterItem), null)
                .getContentFilters().get(0).getIdentifier());
        assertEquals(channelsFilterItem.getIdentifier(), Rumble.getSearchQHFactory()
                .fromQuery("asdf", singletonList(channelsFilterItem), null)
                .getContentFilters().get(0).getIdentifier());
    }

    @Test
    public void testWithContentfilter() throws Exception {
        final FilterItem videoFilterItem = DefaultSearchExtractorTest
                .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
        final FilterItem channelsFilterItem = DefaultSearchExtractorTest
                .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_CHANNELS);
        assertEquals("https://rumble.com/search/channel?q=%C3%B6%C3%A4%C3%BC",
                Rumble.getSearchQHFactory()
                        .fromQuery("öäü", singletonList(channelsFilterItem), null).getUrl());
        assertEquals("https://rumble.com/search/video?q=%C3%B6%C3%A4%C3%BC",
                Rumble.getSearchQHFactory()
                        .fromQuery("öäü", singletonList(videoFilterItem), null).getUrl());
    }

    @Test
    public void testGetAvailableContentFilter() {
        final FilterContainer contentFilter =
                Rumble.getSearchQHFactory().getAvailableContentFilter();

        final int noOfContentFilters = DefaultSearchExtractorTest.getNoOfFilterItems(contentFilter);
        final FilterItem[] filterItems = contentFilter.getFilterGroups()[0].getFilterItems();
        assertEquals(2, noOfContentFilters);
        assertEquals(RumbleFilters.ID_CF_MAIN_VIDEOS, filterItems[0].getIdentifier());
        assertEquals(RumbleFilters.ID_CF_MAIN_CHANNELS, filterItems[1].getIdentifier());
    }

    @Test
    public void testGetAvailableSortFilter() {
        final FilterContainer contentFilterContainer =
                Rumble.getSearchQHFactory().getAvailableContentFilter();
        final int noOfSortFilters =
                DefaultSearchExtractorTest.getNoOfSortFilterItems(contentFilterContainer);
        assertEquals(12, noOfSortFilters);
    }
}
