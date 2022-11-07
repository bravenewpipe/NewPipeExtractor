// Created by evermind-zz 2022, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bitchute.search.filter;

import org.schabi.newpipe.extractor.search.filter.BaseSearchFilters;
import org.schabi.newpipe.extractor.search.filter.FilterContainer;
import org.schabi.newpipe.extractor.search.filter.FilterGroup;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.search.filter.LibraryStringIds;

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

    @Override
    protected void init() {
        /* sort filters */
        /* 'Sort by' filter items */
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_SORT_BY_RELEVANCE, LibraryStringIds.SEARCH_FILTERS_RELEVANCE,
                ""));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_SORT_BY_NEWEST, LibraryStringIds.SEARCH_FILTERS_NEWEST_FIRST,
                "sort=new"));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_SORT_BY_OLDEST, LibraryStringIds.SEARCH_FILTERS_OLDEST_FIRST,
                "sort=old"));


        /* 'Duration' filter items */
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_DURATION_ALL, LibraryStringIds.SEARCH_FILTERS_ALL,
                ""));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_DURATION_SHORT, LibraryStringIds.SEARCH_FILTERS_SHORT_0_5M,
                "duration=short"));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_DURATION_MEDIUM, LibraryStringIds.SEARCH_FILTERS_MEDIUM_5_20M,
                "duration=medium"));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_DURATION_LONG, LibraryStringIds.SEARCH_FILTERS_LONG_20M_PLUS,
                "duration=long"));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_SF_DURATION_FEATURE, LibraryStringIds.SEARCH_FILTERS_FEATURE_45M_PLUS,
                "duration=feature"));


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
                        }, null)
        });

        /* content filters */
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_CF_MAIN_VIDEOS, LibraryStringIds.SEARCH_FILTERS_VIDEOS, "kind=video"));
        groupsFactory.addFilterItem(new BitchuteFilterItem(
                ID_CF_MAIN_CHANNELS, LibraryStringIds.SEARCH_FILTERS_CHANNELS, "kind=channel"));

        /* content filter groups */
        addContentFilterGroup(groupsFactory.createFilterGroup(ID_CF_MAIN_GRP, null, true,
                ID_CF_MAIN_VIDEOS, new FilterItem[]{
                        groupsFactory.getFilterForId(ID_CF_MAIN_VIDEOS),
                        groupsFactory.getFilterForId(ID_CF_MAIN_CHANNELS),
                }, allSortFilters));
        addContentFilterSortVariant(ID_CF_MAIN_VIDEOS, allSortFilters);
    }

    @Override
    public String evaluateSelectedFilters(final String searchString) {
        final String queryResult = evaluateSelectedContentFilters()
                + evaluateSelectedSortFilters();

        // set queryData to Filter as we later need to retrieve it from there
        final BitchuteFilterItem contentItem = getFirstContentFilter();
        if (contentItem != null) {
            contentItem.setDataParams(queryResult);
        } else {
            throw new RuntimeException("we have no content filter set. that is a problem");
        }

        return queryResult;
    }

    @Override
    public String evaluateSelectedSortFilters() {
        final StringBuilder sortQuery = new StringBuilder();
        if (selectedSortFilter != null) {
            for (final FilterItem item : selectedSortFilter) {
                final BitchuteFilterItem sortItem = (BitchuteFilterItem) item;
                if (sortItem != null && !sortItem.query.isEmpty()) {
                    sortQuery.append("&").append(sortItem.query);
                }
            }
        }
        return sortQuery.toString();
    }

    private BitchuteFilterItem getFirstContentFilter() {
        if (selectedContentFilter != null && !selectedContentFilter.isEmpty()) {
            return (BitchuteFilterItem) selectedContentFilter.get(0);
        }
        return null;
    }

    @Override
    public String evaluateSelectedContentFilters() {
        final BitchuteFilterItem contentItem = getFirstContentFilter();
        if (contentItem != null) {
            return "&" + contentItem.query;
        }
        return super.evaluateSelectedContentFilters();
    }

    public static class BitchuteFilterItem extends FilterItem {
        public final String query;
        private String dataParams = "";

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
    }
}
