// Created by evermind-zz 2022, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bitchute.search.filter;

import com.github.bravenewpipe.json2java4nanojson.bitchute.api.results.search.channels.ResultsSearchChannels;
import com.github.bravenewpipe.json2java4nanojson.bitchute.api.results.search.videos.ResultsSearchVideos;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.search.filter.BaseSearchFilters;
import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterGroup;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.search.filter.LibraryStringIds;

import java.util.ArrayList;
import java.util.List;

public final class BitchuteFilters extends BaseSearchFilters {

    public static final int ID_CF_MAIN_GRP = 0;
    public static final int ID_CF_MAIN_VIDEOS = 1;
    public static final int ID_CF_MAIN_CHANNELS = 2;
    public static final int ID_SF_SORT_BY_GRP = 3;
    public static final int ID_SF_SORT_BY_RELEVANCE = 4;
    public static final int ID_SF_SORT_BY_NEWEST = 5;
    public static final int ID_SF_SORT_BY_OLDEST = 6;
    public static final int ID_SF_DURATION_GRP = 7;
    public static final int ID_SF_DURATION_ALL = 8;
    public static final int ID_SF_DURATION_SHORT = 9;
    public static final int ID_SF_DURATION_MEDIUM = 10;
    public static final int ID_SF_DURATION_LONG = 11;
    public static final int ID_SF_DURATION_FEATURE = 12;

    private static final int ID_SF_SENSITIVITY_GRP = 13;
    private static final int ID_SF_SENSITIVITY_SAFE = 14;
    private static final int ID_SF_SENSITIVITY_NORMAL = 15;
    private static final int ID_SF_SENSITIVITY_NSFW = 16;
    private static final int ID_SF_SENSITIVITY_NSFL = 17;
    private JsonBuilder<JsonObject> currentJsonSearchObject = null;

    @Override
    protected void init() {
        /* sort filters */
        /* 'Sort by' filter items */
        groupsFactory.addFilterItem(new BitchuteSortFilterItem(
                ID_SF_SORT_BY_RELEVANCE, LibraryStringIds.SEARCH_FILTERS_RELEVANCE,
                ""));
        groupsFactory.addFilterItem(new BitchuteSortFilterItem(
                ID_SF_SORT_BY_NEWEST, LibraryStringIds.SEARCH_FILTERS_NEWEST_FIRST,
                "new"));
        groupsFactory.addFilterItem(new BitchuteSortFilterItem(
                ID_SF_SORT_BY_OLDEST, LibraryStringIds.SEARCH_FILTERS_OLDEST_FIRST,
                "old"));


        /* 'Duration' filter items */
        groupsFactory.addFilterItem(new BitchuteDurationFilterItem(
                ID_SF_DURATION_ALL, LibraryStringIds.SEARCH_FILTERS_ALL,
                ""));
        groupsFactory.addFilterItem(new BitchuteDurationFilterItem(
                ID_SF_DURATION_SHORT, LibraryStringIds.SEARCH_FILTERS_SHORT_0_5M,
                "short"));
        groupsFactory.addFilterItem(new BitchuteDurationFilterItem(
                ID_SF_DURATION_MEDIUM, LibraryStringIds.SEARCH_FILTERS_MEDIUM_5_20M,
                "medium"));
        groupsFactory.addFilterItem(new BitchuteDurationFilterItem(
                ID_SF_DURATION_LONG, LibraryStringIds.SEARCH_FILTERS_LONG_20M_PLUS,
                "long"));
        groupsFactory.addFilterItem(new BitchuteDurationFilterItem(
                ID_SF_DURATION_FEATURE, LibraryStringIds.SEARCH_FILTERS_FEATURE_45M_PLUS,
                "feature"));


        /* 'Sensitivity' filter items */
        groupsFactory.addFilterItem(new BitchuteSensitivityFilterItem(
                ID_SF_SENSITIVITY_SAFE, LibraryStringIds.SEARCH_FILTERS_SENSITIVITY_SAFE,
                "safe"));
        groupsFactory.addFilterItem(new BitchuteSensitivityFilterItem(
                ID_SF_SENSITIVITY_NORMAL, LibraryStringIds.SEARCH_FILTERS_SENSITIVITY_NORMAL,
                "normal"));
        groupsFactory.addFilterItem(new BitchuteSensitivityFilterItem(
                ID_SF_SENSITIVITY_NSFW, LibraryStringIds.SEARCH_FILTERS_SENSITIVITY_NSFW,
                "nsfw"));
        groupsFactory.addFilterItem(new BitchuteSensitivityFilterItem(
                ID_SF_SENSITIVITY_NSFL, LibraryStringIds.SEARCH_FILTERS_SENSITIVITY_NSFL,
                "nsfl"));

        final FilterContainer allSortFilters = new FilterContainer(new FilterGroup[]{
                groupsFactory.createFilterGroup(ID_SF_SORT_BY_GRP,
                        LibraryStringIds.SEARCH_FILTERS_SORT_BY, true,
                        ID_SF_SORT_BY_RELEVANCE, new FilterItem[]{
                                groupsFactory.getFilterForId(ID_SF_SORT_BY_RELEVANCE),
                                groupsFactory.getFilterForId(ID_SF_SORT_BY_NEWEST),
                                groupsFactory.getFilterForId(ID_SF_SORT_BY_OLDEST),
                        }, null),
                groupsFactory.createFilterGroup(ID_SF_DURATION_GRP,
                        LibraryStringIds.SEARCH_FILTERS_DURATION, true,
                        ID_SF_DURATION_ALL, new FilterItem[]{
                                groupsFactory.getFilterForId(ID_SF_DURATION_ALL),
                                groupsFactory.getFilterForId(ID_SF_DURATION_SHORT),
                                groupsFactory.getFilterForId(ID_SF_DURATION_MEDIUM),
                                groupsFactory.getFilterForId(ID_SF_DURATION_LONG),
                                groupsFactory.getFilterForId(ID_SF_DURATION_FEATURE),
                        }, null),
                groupsFactory.createFilterGroup(ID_SF_SENSITIVITY_GRP,
                        LibraryStringIds.SEARCH_FILTERS_SENSITIVITY, true,
                        ID_SF_SENSITIVITY_NORMAL, new FilterItem[]{
                                groupsFactory.getFilterForId(ID_SF_SENSITIVITY_SAFE),
                                groupsFactory.getFilterForId(ID_SF_SENSITIVITY_NORMAL),
                                groupsFactory.getFilterForId(ID_SF_SENSITIVITY_NSFW),
                                groupsFactory.getFilterForId(ID_SF_SENSITIVITY_NSFL),
                        }, null)
        });

        /* content filters */
        groupsFactory.addFilterItem(new BitchuteKindContentFilterItem(
                ID_CF_MAIN_VIDEOS, LibraryStringIds.SEARCH_FILTERS_VIDEOS, "video",
                ResultsSearchVideos.ENDPOINT));
        groupsFactory.addFilterItem(new BitchuteKindContentFilterItem(
                ID_CF_MAIN_CHANNELS, LibraryStringIds.SEARCH_FILTERS_CHANNELS, "channel",
                ResultsSearchChannels.ENDPOINT));

        /* content filter groups */
        addContentFilterGroup(groupsFactory.createFilterGroup(ID_CF_MAIN_GRP, null, true,
                ID_CF_MAIN_VIDEOS, new FilterItem[]{
                        groupsFactory.getFilterForId(ID_CF_MAIN_VIDEOS),
                        groupsFactory.getFilterForId(ID_CF_MAIN_CHANNELS),
                }, allSortFilters));
        addContentFilterSortVariant(ID_CF_MAIN_VIDEOS, allSortFilters);
    }

    // in case we have no filters selected we default to Bitchute standard options
    private void setDefaultContentAndSortFilters() {
        final FilterContainer fc = getContentFilters();
        final int defaultContentFilterItemId =
                fc.getFilterGroups().get(0).getDefaultSelectedFilterId();
        final List<FilterItem> defaultContentFilters =
                List.of(fc.getFilterItem(defaultContentFilterItemId));

        final List<FilterGroup> sortFilterGroups =
                getContentFilterSortFilterVariant(defaultContentFilterItemId).getFilterGroups();
        final List<FilterItem> defaultSortFilters = new ArrayList<>();
        for (final FilterGroup sortFilterGroup : sortFilterGroups) {
            defaultSortFilters.add(getFilterItem(sortFilterGroup.getDefaultSelectedFilterId()));
        }

        setSelectedContentFilter(defaultContentFilters);
        setSelectedSortFilter(defaultSortFilters);
    }

    @Override
    public String evaluateSelectedFilters(final String searchString) {

        if (selectedContentFilter.isEmpty()) {
            // load default search filter configuration as having a URL alone
            // will not work for the new API
            setDefaultContentAndSortFilters();
        }

        currentJsonSearchObject = JsonObject.builder();
        final String queryResult = evaluateSelectedContentFilters()
                + evaluateSelectedSortFilters();

        // set queryData to Filter as we later need to retrieve it from there
        final BitchuteFilterItem contentItem = getFirstContentFilter();
        if (contentItem != null) {
            contentItem.setDataParams(queryResult);
            contentItem.setDataParamsJson(currentJsonSearchObject);
        }

        return queryResult;
    }

    @Override
    public String evaluateSelectedSortFilters() {
        final StringBuilder sortQuery = new StringBuilder();
        if (selectedSortFilter != null) {
            for (final FilterItem item : selectedSortFilter) {
                final BitchuteKeyValueFilterItem sortItem = (BitchuteKeyValueFilterItem) item;
                if (sortItem != null && !sortItem.query.isEmpty()) {
                    sortQuery.append("&").append(sortItem.key).append("=").append(sortItem.query);
                    currentJsonSearchObject.value(sortItem.key, sortItem.query);
                }
            }
        }
        return sortQuery.toString();
    }

    private BitchuteKindContentFilterItem getFirstContentFilter() {
        if (selectedContentFilter != null && !selectedContentFilter.isEmpty()) {
            return (BitchuteKindContentFilterItem) selectedContentFilter.get(0);
        }
        return null;
    }

    @Override
    public String evaluateSelectedContentFilters() {
        final BitchuteKindContentFilterItem contentItem = getFirstContentFilter();
        if (contentItem != null) {
            currentJsonSearchObject.value(contentItem.key, contentItem.query);
            return "&" + contentItem.key + "=" + contentItem.query;
        }
        return "";
    }

    public static class BitchuteSensitivityFilterItem extends BitchuteKeyValueFilterItem {
        BitchuteSensitivityFilterItem(
                final int identifier,
                final LibraryStringIds nameId,
                final String value) {
            super(identifier, nameId, "sensitivity_id", value);
        }
    }

    public static class BitchuteSortFilterItem extends BitchuteKeyValueFilterItem {
        BitchuteSortFilterItem(
                final int identifier,
                final LibraryStringIds nameId,
                final String value) {
            super(identifier, nameId, "sort", value);
        }
    }

    public static class BitchuteKindContentFilterItem extends BitchuteKeyValueFilterItem {

        public final String endpoint;

        BitchuteKindContentFilterItem(final int identifier,
                                      final LibraryStringIds nameId,
                                      final String value,
                                      final String endpoint) {
            super(identifier, nameId, "kind", value);
            this.endpoint = endpoint;
        }
    }

    public static class BitchuteDurationFilterItem extends BitchuteKeyValueFilterItem {

        BitchuteDurationFilterItem(final int identifier,
                                   final LibraryStringIds nameId,
                                   final String value) {
            super(identifier, nameId, "duration", value);
        }
    }

    public static class BitchuteKeyValueFilterItem extends BitchuteFilterItem {

        public final String key;

        BitchuteKeyValueFilterItem(final int identifier,
                                   final LibraryStringIds nameId,
                                   final String key,
                                   final String value) {
            super(identifier, nameId, value);
            this.key = key;
        }
    }

    public static class BitchuteFilterItem extends FilterItem {
        public final String query;
        private String dataParams = "";
        private JsonBuilder<JsonObject> dataParamsJson;

        BitchuteFilterItem(final int identifier,
                           final LibraryStringIds nameId,
                           final String query) {
            super(identifier, nameId);
            this.query = query;
        }

        public String getDataParams() {
            return dataParams;
        }

        public void setDataParams(final String dataParams) {
            this.dataParams = dataParams;
        }

        public void setDataParamsJson(final JsonBuilder<JsonObject> dataParamsJson) {
            this.dataParamsJson = dataParamsJson;
        }

        public JsonBuilder<JsonObject> getDataParamsNew() {
            return dataParamsJson;
        }
    }
}
