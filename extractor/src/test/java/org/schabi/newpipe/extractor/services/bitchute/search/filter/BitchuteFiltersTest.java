package org.schabi.newpipe.extractor.services.bitchute.search.filter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.search.filter.BaseSearchFilters;
import org.schabi.newpipe.extractor.services.DefaultFiltersTest;

import java.util.List;

import static java.util.Collections.singletonList;

class BitchuteFiltersTest extends DefaultFiltersTest {

    @Override
    protected BaseSearchFilters setupPriorTesting() {
        doNotCallAssertButShowResult = false;
        return new BitchuteFilters();
    }

    @Test
    @Disabled
    @Override
    public void emptyContentFilterTest() {
        // no implementation for this test as bitchute rely on content filters to be present
    }

    @Override
    protected String emptyContentFilterTestSetup(final BaseSearchFilters base) {
        return null;
    }

    @Override
    protected void validContentFilterSetup(
            final List<InputAndExpectedResultData> validContentFiltersAndExpectedResults) {
        validContentFiltersAndExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                null,
                "&kind=video",
                null,
                null,
                null
        ));
        validContentFiltersAndExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_CHANNELS),
                null,
                "&kind=channel",
                null,
                null,
                null
        ));

    }

    @Override
    protected void validContentFilterAllSortFiltersTestSetup(
            final List<InputAndExpectedResultData>
                    validContentFilterAllSortFiltersExpectedResults) {
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_SORT_BY_RELEVANCE),
                "&kind=video",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_SORT_BY_NEWEST),
                "&kind=video&sort=new",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_SORT_BY_OLDEST),
                "&kind=video&sort=old",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_DURATION_ALL),
                "&kind=video",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_DURATION_SHORT),
                "&kind=video&duration=short",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_DURATION_MEDIUM),
                "&kind=video&duration=medium",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_DURATION_LONG),
                "&kind=video&duration=long",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                singletonList(BitchuteFilters.ID_SF_DURATION_FEATURE),
                "&kind=video&duration=feature",
                null,
                null,
                null
        ));
    }

    @Override
    protected void validAllSortFilterSetup(
            final List<InputAndExpectedResultData> validAllSortFilters) {
        // no implementation for this test as bitchute rely on content filters to be present
    }

    @Override
    protected void validContentFilterWithAllSortFiltersTestSetup(
            final List<InputAndExpectedResultData> validContentFiltersWithExpectedResult) {
        validContentFiltersWithExpectedResult.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_VIDEOS),
                getAllSortFiltersIdsList(),
                "&kind=video&sort=new&sort=old&duration=short&duration=medium&duration=long&duration=feature",
                null,
                null,
                null
        ));
        validContentFiltersWithExpectedResult.add(new InputAndExpectedResultData(
                null,
                singletonList(BitchuteFilters.ID_CF_MAIN_CHANNELS),
                getAllSortFiltersIdsList(),
                "&kind=channel&sort=new&sort=old&duration=short&duration=medium&duration=long&duration=feature",
                null,
                null,
                null
        ));
    }

    @Override
    protected void contentFiltersThatHaveCorrespondingSortFiltersTestSetup(
            final List<Integer> contentFiltersThatHaveCorrespondingSortFilters) {
        contentFiltersThatHaveCorrespondingSortFilters.add(BitchuteFilters.ID_CF_MAIN_VIDEOS);
    }
}
