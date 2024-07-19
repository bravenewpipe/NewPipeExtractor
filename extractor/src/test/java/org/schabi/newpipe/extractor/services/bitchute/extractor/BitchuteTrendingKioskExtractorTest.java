package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.BaseListExtractorTest;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteKioskLinkHandlerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.schabi.newpipe.extractor.ServiceList.Bitchute;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoMoreItems;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestRelatedItems;

/**
 * Tests for {@link BitchuteTrendingKioskExtractor}
 * and tests for {@link BitchuteRecommendedChannelKioskExtractor}
 *
 * We have 4 different Kiosk categories:
 * - Trending Day
 * - Trending Week
 * - Trending Month
 * - And we have the Recommended Channels
 *
 *  Here we have a base class Trending that implements testing for all of those categories.
 *  For every category we have one derived test class.
 */
public class BitchuteTrendingKioskExtractorTest {

    public static class TrendingWeek extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.name, BitchuteKioskLinkHandlerFactory.TRENDING_WEEK);
                put(KeysForTestDataMap.id, BitchuteKioskLinkHandlerFactory.TRENDING_WEEK);
                put(KeysForTestDataMap.url, BitchuteConstants.BASE_URL + "/#trending-week");
                put(KeysForTestDataMap.originalUrl, BitchuteConstants.BASE_URL + "/#trending-week");
            }};
            Trending.setUp();
        }
    }

    public static class TrendingDay extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.name, BitchuteKioskLinkHandlerFactory.TRENDING_DAY);
                put(KeysForTestDataMap.id, BitchuteKioskLinkHandlerFactory.TRENDING_DAY);
                put(KeysForTestDataMap.url, BitchuteConstants.BASE_URL + "/#trending-day");
                put(KeysForTestDataMap.originalUrl, BitchuteConstants.BASE_URL + "/#trending-day");
            }};
            Trending.setUp();
        }
    }

    public static class TrendingMonth extends Trending {
        @SuppressWarnings("checkstyle:LineLength")
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.name, BitchuteKioskLinkHandlerFactory.TRENDING_MONTH);
                put(KeysForTestDataMap.id, BitchuteKioskLinkHandlerFactory.TRENDING_MONTH);
                put(KeysForTestDataMap.url, BitchuteConstants.BASE_URL + "/#trending-month");
                put(KeysForTestDataMap.originalUrl, BitchuteConstants.BASE_URL + "/#trending-month");
            }};
            Trending.setUp();
        }
    }

    public abstract static class Trending implements BaseListExtractorTest {

        public enum KeysForTestDataMap {
            name, id, url, originalUrl
        }
        private static ListExtractor extractor;
        protected static Map<KeysForTestDataMap, String> testDataMap;

        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());

            extractor = Bitchute.getKioskList()
                    .getExtractorById(testDataMap.get(KeysForTestDataMap.id), null);
            extractor.fetchPage();
        }

        @AfterAll
        public static void tearDown() throws IOException, ExtractionException {
            final List<Throwable> errors = extractor.getInitialPage().getErrors();
            System.out.println(errors.toString());

        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(Bitchute.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals(testDataMap.get(KeysForTestDataMap.name), extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals(testDataMap.get(KeysForTestDataMap.id), extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            // TODO evermind figure out for what this URL really are
            //assertEquals(testDataMap.get(keysForTestDataMap.url), extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            // TODO evermind figure out for what this URL really are
            // assertEquals(testDataMap.get(keysForTestDataMap.originalUrl),
            //         extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(extractor);
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            assertNoMoreItems(extractor);
        }
    }
}
