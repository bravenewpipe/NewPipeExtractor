package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultStreamExtractorTest;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestListOfItems;

public class RumbleStreamExtractorTest {

    public static class NormalStreamExtractorTest extends DefaultStreamExtractorTest {

        protected static final String MOCK_PATH =
                RESOURCE_PATH + "/services/rumble/extractor/stream/";

        protected static RumbleStreamExtractor extractor;
        protected static StreamingService expectedService = Rumble;

        protected static String url = "https://rumble.com/vftupt-governor-ron-desantis-signs-hallmark-anti-riot-legislation-in-florida-41920.html";
        protected static String expectedUrl = "https://rumble.com/vftupt";
        protected static String expectedName = "Governor Ron DeSantis Signs Hallmark Anti-Riot Legislation in Florida 4/19/2021";
        protected static String expectedId = "vftupt";
        protected static String expectedDesc = "Governor Ron DeSantis Signs Hallmark Anti-Riot Legislation in Florida 4/19/2021";
        protected static String expectedCategory = "";
        protected static int expectedAgeLimit = 0;
        protected static long expectedViewCountAtLeast = 5700;
        protected static String expectedUploaderName = "Ron DeSantis";
        protected static String expectedUploadDate = "2021-04-19 19:11:31.000";
        protected static String expectedTextualUploadDate = "2021-04-19T19:11:31+00:00";
        protected static StreamExtractor.Privacy expectedPrivacy = StreamExtractor.Privacy.PUBLIC;
        protected static String expectedUploaderUrl = "https://rumble.com/c/GovRonDeSantis";
        protected static String expectedSupportInfo = "";
        protected static boolean expectedHasAudioStreams = false;
        protected static boolean expectedHasVideoStreams = true;
        protected static String expectedArtistProfilePictureInfix = ".rumble.com/live/channel_images/"; // TODO
        protected static long expectedLength = 1937;

        @BeforeClass
        public static void setUp () throws ExtractionException, IOException {
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/streamExtractor"));

            extractor = (RumbleStreamExtractor) Rumble
                    .getStreamExtractor(url);
            extractor.fetchPage();
        }

        @Test
        public void testAvatarThumbnailPicture () throws Exception {
            final String url = extractor().getUploaderAvatarUrl();
            assertTrue(!url.isEmpty() || url == null);
        }

        @Override
        public StreamExtractor extractor () {
            return extractor;
        }

        @Override
        public StreamingService expectedService () {
            return expectedService;
        }

        @Override
        public String expectedName () {
            return expectedName;
        }

        @Override
        public String expectedId () {
            return expectedId;
        }

        @Override
        public String expectedUrlContains () {
            return expectedUrl;
        }

        @Override
        public String expectedOriginalUrlContains () {
            return expectedUrl;
        }

        @Override
        public StreamType expectedStreamType () {
            return StreamType.VIDEO_STREAM;
        }

        @Override
        public String expectedUploaderName () {
            return expectedUploaderName;
        }

        @Override
        public String expectedUploaderUrl () {
            return expectedUploaderUrl;
        }

        @Override
        public List<String> expectedDescriptionContains () {
            return Collections.singletonList(expectedDesc);
        }

        @Override
        public long expectedLength () {
            return expectedLength;
        }

        @Override
        public long expectedViewCountAtLeast () {
            return expectedViewCountAtLeast;
        }

        @Override
        public String expectedUploadDate () {
            return expectedUploadDate;
        }

        @Override
        public String expectedTextualUploadDate () {
            return expectedTextualUploadDate;
        }

        @Override
        public long expectedLikeCountAtLeast () {
            return Long.MIN_VALUE;
        }

        @Override
        public long expectedDislikeCountAtLeast () {
            return Long.MIN_VALUE;
        }

        @Override
        public boolean expectedHasVideoStreams () {
            return expectedHasVideoStreams;
        }

        @Override
        public boolean expectedHasRelatedItems () {
            return true;
        }

        @Override
        public boolean expectedHasSubtitles () {
            return false;
        }

        @Override
        public boolean expectedHasFrames () {
            return false;
        }

        @Override
        public String expectedCategory () {
            return expectedCategory;
        }

        @Override
        public int expectedAgeLimit () {
            return expectedAgeLimit;
        }

        @Override
        public StreamExtractor.Privacy expectedPrivacy () {
            return expectedPrivacy;
        }

        @Override
        public String expectedSupportInfo () {
            return expectedSupportInfo;
        }

        @Override
        public boolean expectedHasAudioStreams () {
            return expectedHasAudioStreams;
        }

        /**
         *  Test for {@link RumbleStreamRelatedInfoItemExtractor}
         */
        @Test
        public void RumbleStreamRelatedInfoItemsExtractorTest () throws ExtractionException {
            StreamInfoItemsCollector page = extractor.getRelatedItems();
            List<StreamInfoItem> actualResultsList = page.getItems();

            List<Throwable> errors = page.getErrors();
            defaultTestListOfItems(extractor.getService(), actualResultsList, errors);

            // if the mock is changed you need to update these here too.
            // Get it from actualResultsList.get(x).toString() and update someExpectedResults
            String[] someExpectedResults = {
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='null', viewCount=-1, duration=262, uploaderUrl='https://rumble.com/user/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/vfvdep-florida-gov.-desantis-rips-bidens-reckless-ice-policies.html', name='Florida Gov. DeSantis rips Biden's 'reckless' ICE policies', thumbnailUrl='https://i.rmbl.ws/s8/6/H/s/S/L/HsSLb.0kob.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='null', viewCount=-1, duration=48, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/vbxxz3-florida-governor-ron-desantis-declined-a-reporters-request-..html', name='Florida Governor Ron DeSantis declined a reporter’s request .', thumbnailUrl='https://i.rmbl.ws/s8/6/_/u/H/m/_uHmb.0kob.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='WFTX', textualUploadDate='null', viewCount=-1, duration=47, uploaderUrl='https://rumble.com/user/WFTX', infoType=STREAM, serviceId=6, url='https://rumble.com/vf6b53-governor-desantis-signs-covid-liability-bill-today.html', name='Governor DeSantis signs COVID liability bill today', thumbnailUrl='https://i.rmbl.ws/s8/1/N/Z/o/H/NZoHb.0kob-small-Governor-DeSantis-signs-COV.jpg', uploaderVerified='false'}"
            };

            // test if someExpectedResults are matching actualResultsList entries
            List<Integer> foundEntries = new LinkedList<>();
            for (StreamInfoItem actualResult : actualResultsList) {
                for (int i = 0; i < someExpectedResults.length; i++) {
                    String expectedResult = someExpectedResults[i];
                    if (expectedResult.equals(actualResult.toString())) {
                        foundEntries.add(i);
                    }
                }
            }

            // verify if all someExpectedResults were found
            for (int i = 0; i < someExpectedResults.length; i++) {
                assertTrue("Not found: " + someExpectedResults[i], foundEntries.contains(i));
            }
        }
    }

    public static class LiveStreamExtractorTest extends NormalStreamExtractorTest {


        @BeforeClass
        public static void setUp () throws ExtractionException, IOException {
            url = "https://rumble.com/vg7h6j";
            expectedUrl = "https://rumble.com/vg7h6j";
            expectedName = "EP 2422-9AM UKRAINIANS USED BY FBI TO SPY ON AMERICANS?  FISA Warrants Issued MONTHS Before Jan 6th";
            expectedId = "vg7h6j";
            expectedDesc = "EP 2422-9AM UKRAINIAN ASSETS USED BY FBI TO SPY ON AMERICANS - FISA Warrants Issued MONTHS Before Jan 6th";
            expectedCategory = "";
            expectedAgeLimit = 0;
            expectedViewCountAtLeast = 92;
            expectedUploaderName = "The Pete Santilli Show - LIVE Channel";
            expectedUploadDate = "2021-04-28 12:07:24.000";
            expectedTextualUploadDate = "2021-04-28T12:07:24+00:00";
            expectedPrivacy = StreamExtractor.Privacy.PUBLIC;
            expectedUploaderUrl = "https://rumble.com/c/PeteLive";
            expectedSupportInfo = "";
            expectedHasAudioStreams = false;
            expectedHasVideoStreams = true;
            expectedArtistProfilePictureInfix = ".rumble.com/live/channel_images/"; // TODO
            expectedLength = 59;
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/streamExtractorLiveStream"));

            extractor = (RumbleStreamExtractor) Rumble
                    .getStreamExtractor(url);
            extractor.fetchPage();
        }

        /**
         *  Test for {@link RumbleStreamRelatedInfoItemExtractor}
         */
        @Test
        public void RumbleStreamRelatedInfoItemsExtractorTest () throws ExtractionException {

        }
    }
}