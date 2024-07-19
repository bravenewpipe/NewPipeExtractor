package com.github.bravenewpipe.json2java4nanojson.bitchute.api.results.stream.videos;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Request: {"selection":"suggested","offset":1,"limit":20,"advertisable":true}.
 *
 * (This file was generated by json2java4nanoJson)
 **/
public class ResultsStreamVideos implements Serializable {

    public static final String ENDPOINT = "https://api.bitchute.com/api/beta9/videos";

    private final List<Videos> videos;

    public ResultsStreamVideos(JsonObject jsonObject) {
        List<Videos> listobjArrayvideos = new ArrayList<>();
        JsonArray objArrayvideos = jsonObject.getArray("videos");
        for (Object obj : objArrayvideos) {
            listobjArrayvideos.add(new Videos(((JsonObject) obj)));
        }
        this.videos = listobjArrayvideos;
    }

    public List<Videos> getVideos() {
        return videos;
    }
}
