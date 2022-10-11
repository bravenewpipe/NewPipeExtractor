package org.schabi.newpipe.extractor.services.soundcloud.search.filter;

import org.schabi.newpipe.extractor.search.filter.Filter;
import org.schabi.newpipe.extractor.search.filter.FilterGroup;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.search.filter.SearchFiltersBase;

public final class SoundcloudFilters extends SearchFiltersBase {

    public static final String TRACKS = "tracks";
    public static final String USERS = "users";
    public static final String PLAYLISTS = "playlists";
    public static final String ALL = "all";

    public SoundcloudFilters() {
        init();
        build();
    }

    @Override
    public String evaluateSelectedContentFilters() {
        if (selectedContentFilter != null && !selectedContentFilter.isEmpty()) {
            final SoundcloudContentFilterItem contentItem =
                    // we assume there is just one content filter
                    (SoundcloudContentFilterItem) selectedContentFilter.get(0);
            return contentItem.urlEndpoint;
        }
        return "";
    }

    @Override
    protected void init() {
        /* content filters */
        final int contentFilterAll = builder.addFilterItem(
                new SoundcloudContentFilterItem(ALL, ""));
        final int contentFilterTracks = builder.addFilterItem(
                new SoundcloudContentFilterItem(TRACKS, "/tracks"));
        final int contentFilterUsers = builder.addFilterItem(
                new SoundcloudContentFilterItem(USERS, "/users"));
        final int contentFilterPlaylists = builder.addFilterItem(
                new SoundcloudContentFilterItem(PLAYLISTS, "/playlists"));

        /* set default content filter */

        // 'ALL' this is the default search content filter.
        this.defaultContentFilterId = contentFilterAll;


        /* content filters with sort filters */
        addContentFilter(builder.createSortGroup(null, true, new FilterItem[]{
                builder.getFilterForId(contentFilterAll),
                builder.getFilterForId(contentFilterTracks),
                builder.getFilterForId(contentFilterUsers),
                builder.getFilterForId(contentFilterPlaylists),
        }));

        /* 'Date' filter items */
        final int filterIdDateAll = builder.addSortItem(
                new SoundcloudSortFilterItem("all", ""));
        final int filterIdDateLastHour = builder.addSortItem(
                new SoundcloudSortFilterItem("Past hour", "filter.created_at=last_hour"));
        final int filterIdDateLastDay = builder.addSortItem(
                new SoundcloudSortFilterItem("Past day", "filter.created_at=last_day"));
        final int filterIdDateLastWeek = builder.addSortItem(
                new SoundcloudSortFilterItem("Past week", "filter.created_at=last_week"));
        final int filterIdDateLastMonth = builder.addSortItem(
                new SoundcloudSortFilterItem("Past month", "filter.created_at=last_month"));
        final int filterIdDateLastYear = builder.addSortItem(
                new SoundcloudSortFilterItem("Past year", "filter.created_at=last_year"));

        /* duration' filter items */
        final int filterIdDurationAll = builder.addSortItem(
                new SoundcloudSortFilterItem("all", ""));
        final int filterIdDurationShort = builder.addSortItem(
                new SoundcloudSortFilterItem("< 2 min", "filter.duration=short"));
        final int filterIdDurationMedium = builder.addSortItem(
                new SoundcloudSortFilterItem("2-10 min", "filter.duration=medium"));
        final int filterIdDurationLong = builder.addSortItem(
                new SoundcloudSortFilterItem("10-30 min", "filter.duration=long"));
        final int filterIdDurationEpic = builder.addSortItem(
                new SoundcloudSortFilterItem("> 30 min", "filter.duration=epic"));

        /* license */
        final int filterIdLicenseAll = builder.addSortItem(
                new SoundcloudSortFilterItem("all", ""));
        final int filterIdLicenseCommerce = builder.addSortItem(
                new SoundcloudSortFilterItem("To modify commercially",
                        "filter.license=to_modify_commercially"));

        addContentFilterSortVariant(contentFilterTracks,
                new Filter.Builder(new FilterGroup[]{
                        builder.createSortGroup("Sort by", true, new FilterItem[]{
                                builder.getFilterForId(filterIdDateAll),
                                builder.getFilterForId(filterIdDateLastHour),
                                builder.getFilterForId(filterIdDateLastDay),
                                builder.getFilterForId(filterIdDateLastWeek),
                                builder.getFilterForId(filterIdDateLastMonth),
                                builder.getFilterForId(filterIdDateLastYear),
                        }),
                        builder.createSortGroup("Length", true, new FilterItem[]{
                                builder.getFilterForId(filterIdDurationAll),
                                builder.getFilterForId(filterIdDurationShort),
                                builder.getFilterForId(filterIdDurationMedium),
                                builder.getFilterForId(filterIdDurationLong),
                                builder.getFilterForId(filterIdDurationEpic),
                        }),
                        builder.createSortGroup("License", true, new FilterItem[]{
                                builder.getFilterForId(filterIdLicenseAll),
                                builder.getFilterForId(filterIdLicenseCommerce),
                        }),
                }).build());
    }

    @Override
    public String evaluateSelectedSortFilters() {
        final StringBuilder sortQuery = new StringBuilder();
        for (final FilterItem item : selectedSortFilter) {
            final SoundcloudSortFilterItem sortItem =
                    (SoundcloudSortFilterItem) item;
            if (sortItem != null) {
                if (!sortItem.query.isEmpty()) {
                    sortQuery.append("&").append(sortItem.query);
                }
            }
        }

        return sortQuery.toString();
    }

    private static class SoundcloudSortFilterItem extends FilterItem {
        private final String query;

        SoundcloudSortFilterItem(final String name, final String query) {
            super(Filter.ITEM_IDENTIFIER_UNKNOWN, name);
            this.query = query;
        }
    }

    private static final class SoundcloudContentFilterItem extends FilterItem {
        private final String urlEndpoint;

        private SoundcloudContentFilterItem(final String name, final String urlEndpoint) {
            super(Filter.ITEM_IDENTIFIER_UNKNOWN, name);
            this.urlEndpoint = urlEndpoint;
        }
    }
}
