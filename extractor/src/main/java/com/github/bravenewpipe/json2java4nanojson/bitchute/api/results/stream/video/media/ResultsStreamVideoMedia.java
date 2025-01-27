package com.github.bravenewpipe.json2java4nanojson.bitchute.api.results.stream.video.media;

import com.grack.nanojson.JsonObject;
import java.io.Serializable;

/**
 * Request: {"video_id":"29XrAAn5VQzl"}.
 *
 * (This file was generated by json2java4nanoJson)
 **/
public class ResultsStreamVideoMedia implements Serializable {

    public static final String ENDPOINT = "https://api.bitchute.com/api/beta/video/media";

    private final String mediaType;
    private final String mediaUrl;
    private final String videoId;

    public ResultsStreamVideoMedia(JsonObject jsonObject) {
        this.mediaType = jsonObject.getString("media_type");
        this.mediaUrl = jsonObject.getString("media_url");
        this.videoId = jsonObject.getString("video_id");
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getVideoId() {
        return videoId;
    }
}
