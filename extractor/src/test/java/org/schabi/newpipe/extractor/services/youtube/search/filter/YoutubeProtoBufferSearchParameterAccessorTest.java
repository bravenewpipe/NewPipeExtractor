// Created by evermind-zz 2022, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.youtube.search.filter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.DateFilter;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.Features;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.LengthFilter;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.SearchRequest;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.SortOrder;
import org.schabi.newpipe.extractor.services.youtube.search.filter.protobuf.TypeFilter;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class YoutubeProtoBufferSearchParameterAccessorTest {

    final DateFilter[] dateFilters = {
            DateFilter.hour,
            DateFilter.day,
            DateFilter.week,
            DateFilter.month,
            DateFilter.year
    };
    final SortOrder[] sortOrders = {
            SortOrder.relevance,
            SortOrder.rating,
            SortOrder.date,
            SortOrder.views
    };
    final LengthFilter[] lengthFilters = {
            LengthFilter.duration_short,
            LengthFilter.duration_long,
            LengthFilter.duration_medium
    };
    final TypeFilter[] typeFilters = {
            TypeFilter.video,
            TypeFilter.channel,
            TypeFilter.playlist,
            TypeFilter.movie,
            TypeFilter.show
    };
    final Features[] features = {
            Features.live,
            Features.is_4k,
            Features.is_hd,
            Features.subtitles,
            Features.ccommons,
            Features.is_360,
            Features.is_vr180,
            Features.is_3d,
            Features.is_hdr,
            Features.location,
            Features.purchased
    };
    YoutubeProtoBufferSearchParameterAccessor.Builder spBuilder;

    @BeforeEach
    void setUp() {
        spBuilder = new YoutubeProtoBufferSearchParameterAccessor.Builder();
    }

    @AfterEach
    void tearDown() {
        spBuilder = null;
    }

    @Test
    void dateFilterTest() throws IOException {

        for (final DateFilter dateFilter : dateFilters) {
            spBuilder.setDateFilter(dateFilter);
            final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
            final String encodedSp = what.getSp();
            final SearchRequest decodedSp = what.decodeSp(encodedSp);
            assertEquals(dateFilter.getValue(), decodedSp.filter.date);
        }
    }

    @Test
    void sortFilterTest() throws IOException {
        for (final SortOrder sortOrder : sortOrders) {
            spBuilder.setSortOrder(sortOrder);
            final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
            final String encodedSp = what.getSp();
            final SearchRequest decodedSp = what.decodeSp(encodedSp);
            assertEquals(sortOrder.getValue(), decodedSp.sorted);
        }
    }

    @Test
    void typeFilterTest() throws IOException {
        for (final TypeFilter type : typeFilters) {
            spBuilder.setTypeFilter(type);
            final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
            final String encodedSp = what.getSp();
            final SearchRequest decodedSp = what.decodeSp(encodedSp);
            assertEquals(type.getValue(), decodedSp.filter.type);
        }
    }

    @Test
    void lengthFilterTest() throws IOException {
        for (final LengthFilter length : lengthFilters) {
            spBuilder.setLengthFilter(length);
            final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
            final String encodedSp = what.getSp();
            final SearchRequest decodedSp = what.decodeSp(encodedSp);
            assertEquals(length.getValue(), decodedSp.filter.length);
        }
    }

    // All filters/features disabled.
    @Test
    void noneDateSortTypeLengthFeaturesSetTest() throws IOException {
        final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
        final String encodedSp = what.getSp();
        final SearchRequest decodedSp = what.decodeSp(encodedSp);

        assertNull(decodedSp.filter.date);
        assertNull(decodedSp.filter.type);
        assertNull(decodedSp.sorted);
        assertNull(decodedSp.filter.length);
        for (final Features feature : features) {
            assertEquals(false, getFeatureState(feature, decodedSp));
        }
    }

    @Test
    void featuresOneAtATimeTest() throws IOException {
        for (final Features feature : features) {
            spBuilder.addFeature(feature);
            final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
            final String encodedSp = what.getSp();
            final SearchRequest decodedSp = what.decodeSp(encodedSp);
            assertEquals(true, getFeatureState(feature, decodedSp));
            spBuilder = new YoutubeProtoBufferSearchParameterAccessor.Builder();
        }
    }

    @Test
    void allFeaturesSetTest() throws IOException {
        for (final Features feature : features) {
            spBuilder.addFeature(feature);
        }

        final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
        final String encodedSp = what.getSp();
        final SearchRequest decodedSp = what.decodeSp(encodedSp);

        for (final Features feature : features) {
            assertEquals(true, getFeatureState(feature, decodedSp));
        }
    }

    @Test
    void allOptionsSelectedOnceTesting() throws IOException {
        for (final Features feature : features) {
            spBuilder = new YoutubeProtoBufferSearchParameterAccessor.Builder();
            spBuilder.addFeature(feature);

            for (final SortOrder sortOrder : sortOrders) {
                spBuilder.setSortOrder(sortOrder);

                for (final TypeFilter type : typeFilters) {
                    spBuilder.setTypeFilter(type);

                    for (final LengthFilter length : lengthFilters) {
                        spBuilder.setLengthFilter(length);

                        for (final DateFilter dateFilter : dateFilters) {
                            spBuilder.setDateFilter(dateFilter);
                            final YoutubeProtoBufferSearchParameterAccessor what =
                                    spBuilder.build();
                            final String encodedSp = what.getSp();
                            final SearchRequest decodedSp = what.decodeSp(encodedSp);

                            assertEquals(dateFilter.getValue(), decodedSp.filter.date);
                            assertEquals(type.getValue(), decodedSp.filter.type);
                            assertEquals(sortOrder.getValue(), decodedSp.sorted);
                            assertEquals(length.getValue(), decodedSp.filter.length);
                            assertEquals(true, getFeatureState(feature, decodedSp));
                        }
                    }
                }
            }
        }
    }

    @Test
    @Disabled
        // that is not a real test case but to evaluate new features
    void oneFeaturesSetTest() throws IOException {
        spBuilder.addFeature(Features.is_vr180);

        final YoutubeProtoBufferSearchParameterAccessor what = spBuilder.build();
        //final String encodedSp = "EgPQAQE%3D";
        final String encodedSp = what.getSp();
        final SearchRequest decodedSp = what.decodeSp(encodedSp);
    }

    // helpers
    private Boolean getFeatureState(final Features feature, final SearchRequest decodedSp) {
        Optional<Boolean> state = Optional.empty();
        switch (feature) {
            case live:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.live));
                break;
            case is_4k:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_4k));
                break;
            case is_hd:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_hd));
                break;
            case subtitles:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.subtitles));
                break;
            case ccommons:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.ccommons));
                break;
            case is_360:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_360));
                break;
            case is_vr180:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_vr180));
                break;
            case is_3d:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_3d));
                break;
            case is_hdr:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.is_hdr));
                break;
            case location:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.location));
                break;
            case purchased:
                state = Optional.of(ifNullThanFalse(decodedSp.filter.purchased));
                break;
        }

        if (!state.isPresent()) {
            throw new RuntimeException("this should not happen");
        }
        return state.get();
    }

    private boolean ifNullThanFalse(final Boolean isFeatureEnabled) {
        if (isFeatureEnabled == null) {
            return false;
        }
        return isFeatureEnabled;
    }
}
