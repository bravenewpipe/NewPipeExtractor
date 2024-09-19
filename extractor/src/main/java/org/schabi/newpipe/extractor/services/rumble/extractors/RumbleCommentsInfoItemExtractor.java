package org.schabi.newpipe.extractor.services.rumble.extractor;

import org.jsoup.nodes.Element;

import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.comments.CommentsInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.rumble.extractors.RumbleCommentsExtractor;
import org.schabi.newpipe.extractor.stream.Description;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.services.rumble.extractors.RumbleCommentsExtractor.intArrayToString;

public class RumbleCommentsInfoItemExtractor implements CommentsInfoItemExtractor {
    private final RumbleCommentsExtractor extractor;
    private final int[] id;
    private byte[] responseBody;
    private Element element;

    public RumbleCommentsInfoItemExtractor(final RumbleCommentsExtractor extractor,
            final int[] id, final byte[] responseBody) {
        this.extractor = extractor;
        this.id = id;
        this.responseBody = responseBody;
        this.element = extractor.getComments(id).first();
    }

    @Override
    public int getLikeCount() throws ParsingException {
        return Integer.parseInt(element.selectFirst("div.rumbles-vote span.rumbles-count").text());
    }

    @Override
    public String getTextualLikeCount() throws ParsingException {
        return element.selectFirst("div.rumbles-vote span.rumbles-count").text() + " " +
                element.selectFirst("div.rumbles-vote span.rumbles-unit").text();
    }

    @Override
    public Description getCommentText() {
        return new Description(element.selectFirst("p.comment-text").wholeText(),
                Description.PLAIN_TEXT);
    }

    @Override
    public String getTextualUploadDate() {
        return element.selectFirst("a.comments-meta-post-time").attr("title");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final var formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm a x", Locale.ENGLISH);
        final var datetime = ZonedDateTime.parse(getTextualUploadDate(), formatter);
        return new DateWrapper(datetime.toOffsetDateTime(), false);
    }

    @Override
    public String getCommentId() {
        return element.attr("data-comment-id");
    }

    @Override
    public String getUploaderUrl() {
        return "https://rumble.com" + element.selectFirst("a.comments-meta-author").attr("href");
    }

    @Override
    public String getUploaderName() {
        return element.selectFirst("a.comments-meta-author").text();
    }

    @Nonnull
    @Override
    public List<Image> getUploaderAvatars() throws ParsingException {
        String image = extractor.getImage(element);
        if (image == null) {
            return List.of();
        }
        return List.of(new Image(image,
                Image.HEIGHT_UNKNOWN, Image.WIDTH_UNKNOWN, Image.ResolutionLevel.UNKNOWN));
    }

    public boolean isPinned() throws ParsingException {
        return element.selectFirst("> div.comments-meta > span.pinned-text") != null;
    }

    public boolean isUploaderVerified() throws ParsingException {
        return element.selectFirst("> div.comments-meta > div.comments-meta-user-badges > img[alt='Verified']") != null;
    }

    private int[] getReplyId() {
        int[] replyId = new int[id.length + 1];
        System.arraycopy(id, 0, replyId, 0, id.length);
        return replyId;
    }

    public int getReplyCount() throws ParsingException {
        int[] replyId = getReplyId();
        replyId[id.length] = 0;
        return extractor.getComments(replyId).size();
    }

    @Nullable
    public Page getReplies() throws ParsingException {
        int[] replyId = getReplyId();
        replyId[id.length] = 0;
        if (extractor.getComments(replyId).size() == 0) {
            return null;
        }
        replyId[id.length] = 1;
        return new Page(intArrayToString(replyId), responseBody);
    }

    public boolean isChannelOwner() throws ParsingException {
        return element.selectFirst("> div.comments-meta > a.comments-meta-author-video-owner") != null;
    }

    @Override
    public String getName() throws ParsingException {
        return getUploaderName();
    }

    @Override
    public String getUrl() {
        try {
            return extractor.getUrl();
        }
        catch (ParsingException e) {
            return null;
        }
    }

    @Nonnull
    @Override
    public List<Image> getThumbnails() throws ParsingException {
        return getUploaderAvatars();
    }

}
