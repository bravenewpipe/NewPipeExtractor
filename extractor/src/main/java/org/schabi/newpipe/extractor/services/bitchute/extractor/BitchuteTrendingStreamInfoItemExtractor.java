package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.jsoup.nodes.Element;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.localization.TimeAgoParser;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nullable;

public class BitchuteTrendingStreamInfoItemExtractor implements StreamInfoItemExtractor {

    private final Element element;
    private final TimeAgoParser parser;
    private String cssQueryVideoName = ".video-result-title";
    private String cssQueryUploaderName = ".video-result-channel";
    private String cssQueryThumbnailUrl = ".video-result-image img";
    private String cssQueryUploaderUrl = ".video-result-channel a";
    private String cssQueryUploadDate = ".video-result-details";
    private String cssQueryVideoViews = ".video-views";
    private String cssQueryVideoDuration = ".video-duration";
    private String cssQueryVideoUrl = ".video-result-title a";

    public BitchuteTrendingStreamInfoItemExtractor(TimeAgoParser parser, Element element) {
        this.element = element;
        this.parser = parser;
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        try {
            return YoutubeParsingHelper
                    .parseDurationString(element.select(cssQueryVideoDuration)
                            .first().text());
        } catch (Exception e) {
            throw new ParsingException("Error parsing duration");
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        try {
            return Utils.mixedNumberWordToLong(element
                    .select(cssQueryVideoViews).first().text());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingException("Error parsing view count");
        }
    }

    @Override
    public String getUploaderName() throws ParsingException {
        try {
            return element.select(cssQueryUploaderName).first().text();
        } catch (Exception e) {
            throw new ParsingException("Error parsing uploader name");
        }
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        try {
            return element.select(cssQueryUploaderUrl).first()
                    .absUrl("href");
        } catch (Exception e) {
            throw new ParsingException("Error parsing uploader url");
        }
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        try {
            return element.select(cssQueryUploadDate ).first().text();
        } catch (Exception e) {
            throw new ParsingException("Error parsing Textual Upload Date");
        }
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return parser.parse(getTextualUploadDate());
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return element.select(cssQueryVideoName).first().text();
        } catch (Exception e) {
            throw new ParsingException("Error parsing Stream title");
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            return element.select(cssQueryVideoUrl)
                    .first().absUrl("href");
        } catch (Exception e) {
            throw new ParsingException("Error parsing Stream url");
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            return element.select(cssQueryThumbnailUrl)
                    .first().attr("data-src");
        } catch (Exception e) {
            throw new ParsingException("Error parsing thumbnail url");
        }
    }

}