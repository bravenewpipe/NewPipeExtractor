package org.schabi.newpipe.extractor.services.bitchute.extractor;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;

/**
 * Test for {@link BitchuteChannelStreamInfoItemExtractor}
 *
 * Using mock files to have this class proper tested. Files are from 20210414.
 * -> this is no live test!! In the future the data from the server may change.
 *    Than also at least the html @{value testInputHtmlMockFile} needs updates.
 *
 * -> Included mock files:
 * input:
 *      json: {@value testInputJsonMockFile}
 *      html: {@value testInputHtmlMockFile}
 * output/ expected results
 *      {@value testExpectedResultsJsonMockFile}
 */
public class BitchuteChannelStreamInfoItemExtractorTest {

    private static final String CHANNEL_MOCK_PATH =
            RESOURCE_PATH + "/services/bitchute/extractor/channel/";

    private static String testInputJsonMockFile = CHANNEL_MOCK_PATH
            + "BitchuteChannelStreamInfoItemExtractor_mock_0.input.json";
    private static String testInputHtmlMockFile = CHANNEL_MOCK_PATH
            + "BitchuteChannelStreamInfoItemExtractor_mock_0.input.html";
    private static String testExpectedResultsJsonMockFile = CHANNEL_MOCK_PATH
            + "BitchuteChannelStreamInfoItemExtractor_mock_0.expected_result.json";

    private static List<BitchuteChannelStreamInfoItemExtractor> channelStreamInfoExtractors;
    private static JsonObject testInputHelperJsonMock;
    private static JsonArray testExpectedResultsJsonMock;

    @BeforeAll
    public static void setUp() throws JsonParserException, IOException {

        channelStreamInfoExtractors = new LinkedList<>();

        testInputHelperJsonMock = (JsonObject) JsonParser.any()
                .from(new FileInputStream(new File(testInputJsonMockFile)));
        final Document testInputHtmlMock = Jsoup.parse(new File(testInputHtmlMockFile), null);
        testInputHtmlMock.setBaseUri(BitchuteConstants.BASE_URL);

        testExpectedResultsJsonMock = (JsonArray) JsonParser.any()
                .from(new FileInputStream(new File(testExpectedResultsJsonMockFile)));

        final Elements videos = testInputHtmlMock.select(".channel-videos-container");
        for (final Element e : videos) {
            // TODO
            channelStreamInfoExtractors.add(new BitchuteChannelStreamInfoItemExtractor(null) {
                @Override
                public String getUploaderName() throws ParsingException {
                    return testInputHelperJsonMock.getString("name");
                }

                @Override
                public String getUploaderUrl() throws ParsingException {
                    return testInputHelperJsonMock.getString("url");
                }

                @Override
                public boolean isUploaderVerified() throws ParsingException {
                    return false;
                }
            });
        }
    }

    @Test
    public void quickTestAllValues() throws JsonParserException {
        int i = 0;
        for (final BitchuteChannelStreamInfoItemExtractor elem : channelStreamInfoExtractors) {
            final String compactResultValues = elem.toString();
            final JsonObject jsonResult = (JsonObject) JsonParser.any().from(compactResultValues);
            final JsonObject jsonExpectedResult = (JsonObject) testExpectedResultsJsonMock.get(i++);

            assertTrue(jsonExpectedResult.equals(jsonResult));
        }
    }
}
