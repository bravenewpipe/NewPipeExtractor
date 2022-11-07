package org.schabi.newpipe.extractor.services.rumble.extractors;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;

import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.rumble.search.filter.RumbleFilters;

import javax.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertEmptyErrors;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoDuplicatedItems;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:InvalidJavadocPosition", "checkstyle:LeftCurly"})
public class RumbleSearchExtractorTest {

    private static final String MOCK_PATH =
            RESOURCE_PATH + "/services/rumble/extractor/search/";

    public static class MultiplePagesResults extends AbstractSearchBaseTest {

        @BeforeAll
        public static void setUp() throws Exception {
            query = "paul";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH
                    + "/MultiplePagesResults"));
            final FilterItem videoFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
            extractor = (RumbleSearchExtractor)
                    Rumble.getSearchExtractor(query, singletonList(videoFilterItem), null);
            extractor.fetchPage();
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() {
            return InfoItem.InfoType.STREAM;
        }

        @Override
        public void testMoreRelatedItems() throws Exception {
            super.testMoreRelatedItems();
        }

        /**
         *
         *  Test for {@link RumbleSearchVideoStreamInfoItemExtractor}
         */
        @Test
        public void rumbleSearchVideoStreamInfoItemExtractorTest()
                throws ExtractionException, IOException {

            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            final String[] someExpectedResults = {
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Dinesh D'Souza', textualUploadDate='2021-04-21T19:28:06-04:00', viewCount=33779, duration=125, uploaderUrl='https://rumble.com/c/DineshDsouza', infoType=STREAM, serviceId=6, url='https://rumble.com/vfx78t-rand-paul-eviscerates-fauci-with-facts.html', name='Rand Paul EVISCERATES Fauci with Facts', thumbnailUrl='https://sp.rmbl.ws/s8/6/T/h/b/M/ThbMb.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='BonginoReport', textualUploadDate='2021-02-25T11:50:36-04:00', viewCount=660701, duration=334, uploaderUrl='https://rumble.com/user/BonginoReport', infoType=STREAM, serviceId=6, url='https://rumble.com/ve5yg1-rand-paul-grills-bidens-transgender-hhs-nominee-gets-arrogant-answer.html', name='Rand Paul GRILLS Biden's Transgender HHS Nominee, Gets Arrogant Answer', thumbnailUrl='https://sp.rmbl.ws/s8/6/H/U/W/A/HUWAb.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Dailycaller', textualUploadDate='2021-04-13T16:44:01-04:00', viewCount=97480, duration=432, uploaderUrl='https://rumble.com/user/Dailycaller', infoType=STREAM, serviceId=6, url='https://rumble.com/vfn27x-sen.-rand-paul-rips-anthony-fauci-over-conflicting-pandemic-advice.html', name='Sen. Rand Paul Rips Anthony Fauci Over Conflicting Pandemic Advice', thumbnailUrl='https://i.rmbl.ws/s8/6/9/N/n/K/9NnKb.oq1b.1.jpg', uploaderVerified='false'}"
            };

            RumbleSharedTests.infoItemsResultsTest(extractor.getService(),
                    extractor.getInitialPage().getItems(),
                    extractor.getInitialPage().getErrors(),
                    someExpectedResults
            );
        }
    }

    public static class OnlyOnePageResults extends AbstractSearchBaseTest {

        @BeforeAll
        public static void setUp() throws Exception {
            query = "desantis bomb";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/OnlyOnePageResults"));
            final FilterItem videoFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
            final FilterItem channelsFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_CHANNELS);
            extractor = (RumbleSearchExtractor)
                Rumble.getSearchExtractor(query, singletonList(videoFilterItem), null);
            extractor.fetchPage();
        }

        // override tests
        @Test
        public void testMoreRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage<InfoItem> initialPage = extractor().getInitialPage();
            assertFalse(initialPage.hasNextPage(), "More items available when it shouldn't");
        }
    }

    public static class NoResultsAtAll extends AbstractSearchBaseTest {

        @BeforeAll
        public static void setUp() throws Exception {
            query = "p3423n dkje ";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/NoResultsAtAll"));
            final FilterItem videoFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
            extractor = (RumbleSearchExtractor)
                    Rumble.getSearchExtractor(query, singletonList(videoFilterItem), null);
            extractor.fetchPage();
        }

        // override tests
        @Test
        public void testRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage initialPage = extractor.getInitialPage();
            assertFalse(initialPage.hasNextPage(), "Does have more items, when it should not");
            final List items = initialPage.getItems();
            assertTrue(items.isEmpty(), "Page items list is empty");
            assertEmptyErrors("Page have errors", initialPage.getErrors());
        }
        @Test
        public void testMoreRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage<InfoItem> initialPage = extractor().getInitialPage();
            assertFalse(initialPage.hasNextPage(), "More items available when it shouldn't");
        }
    }

    public static class Channel extends AbstractSearchBaseTest {


        @BeforeAll
        public static void setUp() throws Exception {
            query = "mark";
            expectedSearchBaseUrl = "rumble.com/search/channel?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/channel?q=";

            System.setProperty("downloader", "MOCK");
            // System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/channel"));
            final FilterItem channelFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_CHANNELS);
            extractor =
                    Rumble.getSearchExtractor(query, singletonList(channelFilterItem), null);
            extractor.fetchPage();
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() {
            return InfoItem.InfoType.CHANNEL;
        }

        @Disabled("Test not relevant here")
        @Override
        public void testMoreRelatedItems() throws Exception { }
    }

    public static class PagingTest {
        @Test
        public void duplicatedItemsCheck() throws Exception {
            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/paging"));
            final FilterItem videoFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_VIDEOS);
            final SearchExtractor extractor =
                    Rumble.getSearchExtractor("facts", singletonList(videoFilterItem), null);
            extractor.fetchPage();

            final ListExtractor.InfoItemsPage<InfoItem> page1 = extractor.getInitialPage();
            final ListExtractor.InfoItemsPage<InfoItem> page2 = extractor.getPage(page1.getNextPage());

            assertNoDuplicatedItems(Rumble, page1, page2);
        }
    }

    public static class ChannelVerified extends AbstractSearchBaseTest {

        @BeforeAll
        public static void setUp() throws Exception {
            query = "Mark Dice";
            expectedSearchBaseUrl = "rumble.com/search/channel?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/channel?q=";

            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "verified"));

            final FilterItem channelFilterItem = DefaultSearchExtractorTest
                    .getFilterItem(Rumble, RumbleFilters.ID_CF_MAIN_CHANNELS);
            extractor = Rumble.getSearchExtractor(query, singletonList(channelFilterItem), null);
            extractor.fetchPage();
        }

        @Test
        public void testAtLeastOneVerified() throws IOException, ExtractionException {
            final List<InfoItem> items = extractor.getInitialPage().getItems();
            boolean verified = false;
            for (final InfoItem item : items) {
                if (((ChannelInfoItem) item).isVerified()) {
                    verified = true;
                    break;
                }
            }

            assertTrue(verified);
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() {
            return InfoItem.InfoType.CHANNEL;
        }

        @Disabled("Test not relevant here")
        @Override
        public void testMoreRelatedItems() throws Exception { }
    }

    private abstract static class AbstractSearchBaseTest extends DefaultSearchExtractorTest {

        protected static SearchExtractor extractor;
        protected static String query;
        protected static String expectedSearchBaseUrl;
        protected static String expectedSearchOriginalBaseUrl;

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return Rumble; }
        @Override public String expectedName() { return query; }
        @Override public String expectedId() { return query; }
        @Override public String expectedUrlContains() throws UnsupportedEncodingException {
            return  expectedSearchBaseUrl + URLEncoder.encode(query, UTF_8);
        }
        @Override public String expectedOriginalUrlContains() throws UnsupportedEncodingException {
            return expectedSearchOriginalBaseUrl  + URLEncoder.encode(query, UTF_8);
        }
        @Override public String expectedSearchString() { return query; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
    }
}
