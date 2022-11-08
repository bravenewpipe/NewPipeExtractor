package org.schabi.newpipe.extractor.services.rumble.search.filter;

import org.schabi.newpipe.extractor.search.filter.BaseSearchFilters;
import org.schabi.newpipe.extractor.services.DefaultFiltersTest;

import java.util.List;

import static java.util.Collections.singletonList;

class RumbleFiltersTest extends DefaultFiltersTest {

    @Override
    protected BaseSearchFilters setupPriorTesting() {
        doNotCallAssertButShowResult = false;
        return new RumbleFilters();
    }

    @Override
    protected String genericTesterEvaluator(final InputAndExpectedResultData testData) {
        return "(CF)"
                + searchFilterBase.evaluateSelectedContentFilters()
                + "|(SF)"
                + searchFilterBase.evaluateSelectedSortFilters();
    }

    /**
     * There is no implementation for {@link BaseSearchFilters#evaluateSelectedFilters(String)}.
     * <p>
     * -> therefore expected result is null.
     *
     * @param base the object to set up.
     * @return null
     */
    @Override
    protected String emptyContentFilterTestSetup(final BaseSearchFilters base) {
        return null;
    }

    @Override
    protected void validContentFilterSetup(
            final List<InputAndExpectedResultData> validContentFiltersAndExpectedResults) {
        validContentFiltersAndExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                null,
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validContentFiltersAndExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_CHANNELS),
                null,
                "(CF)https://rumble.com/search/channel?q=|(SF)",
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
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_SORT_BY_RELEVANCE),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_SORT_BY_MOST_RECENT),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=date",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_SORT_BY_RUMBLES),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=rumbles",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_SORT_BY_VIEWS),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=views",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_ALL),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_TODAY),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=today",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_WEEK),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-week",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_MONTH),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-month",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_YEAR),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-year",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_DURATION_ALL),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_DURATION_LONG),
                "(CF)https://rumble.com/search/video?q=|(SF)&duration=long",
                null,
                null,
                null
        ));
        validContentFilterAllSortFiltersExpectedResults.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                singletonList(RumbleFilters.ID_SF_DURATION_SHORT),
                "(CF)https://rumble.com/search/video?q=|(SF)&duration=short",
                null,
                null,
                null
        ));
    }

    @Override
    protected void validAllSortFilterSetup(
            final List<InputAndExpectedResultData> validAllSortFilters) {
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_SORT_BY_RELEVANCE),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_SORT_BY_MOST_RECENT),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=date",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_SORT_BY_RUMBLES),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=rumbles",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_SORT_BY_VIEWS),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=views",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_ALL),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_TODAY),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=today",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_WEEK),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-week",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_MONTH),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-month",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_UPLOAD_DATE_LAST_YEAR),
                "(CF)https://rumble.com/search/video?q=|(SF)&date=this-year",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_DURATION_ALL),
                "(CF)https://rumble.com/search/video?q=|(SF)",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_DURATION_LONG),
                "(CF)https://rumble.com/search/video?q=|(SF)&duration=long",
                null,
                null,
                null
        ));
        validAllSortFilters.add(new InputAndExpectedResultData(
                null,
                null,
                singletonList(RumbleFilters.ID_SF_DURATION_SHORT),
                "(CF)https://rumble.com/search/video?q=|(SF)&duration=short",
                null,
                null,
                null
        ));
    }

    @Override
    protected void validContentFilterWithAllSortFiltersTestSetup(
            final List<InputAndExpectedResultData> validContentFiltersWithExpectedResult) {
        validContentFiltersWithExpectedResult.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_VIDEOS),
                getAllSortFiltersIdsList(),
                "(CF)https://rumble.com/search/video?q=|(SF)&sort=date&sort=rumbles&sort=views&date=today&date=this-week&date=this-month&date=this-year&duration=long&duration=short",
                null,
                null,
                null
        ));
        validContentFiltersWithExpectedResult.add(new InputAndExpectedResultData(
                null,
                singletonList(RumbleFilters.ID_CF_MAIN_CHANNELS),
                getAllSortFiltersIdsList(),
                "(CF)https://rumble.com/search/channel?q=|(SF)&sort=date&sort=rumbles&sort=views&date=today&date=this-week&date=this-month&date=this-year&duration=long&duration=short",
                null,
                null,
                null
        ));
    }

    @Override
    protected void contentFiltersThatHaveCorrespondingSortFiltersTestSetup(
            final List<Integer> contentFiltersThatHaveCorrespondingSortFilters) {
        contentFiltersThatHaveCorrespondingSortFilters.add(RumbleFilters.ID_CF_MAIN_VIDEOS);
    }

}
