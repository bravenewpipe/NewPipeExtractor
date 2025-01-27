package org.schabi.newpipe.extractor.services.peertube.linkHandler;

import org.schabi.newpipe.extractor.search.filter.FilterItem;

import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PeertubeChannelLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final PeertubeChannelLinkHandlerFactory INSTANCE
            = new PeertubeChannelLinkHandlerFactory();
    private static final String ID_PATTERN = "((accounts|a)|(video-channels|c))/([^/?&#]*)";
    public static final String API_ENDPOINT = "/api/v1/";

    private PeertubeChannelLinkHandlerFactory() {
    }

    public static PeertubeChannelLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getId(final String url) throws ParsingException, UnsupportedOperationException {
        return fixId(Parser.matchGroup(ID_PATTERN, url, 0));
    }

    @Override
    public String getUrl(final String id,
                         @Nonnull final List<FilterItem> contentFilters,
                         @Nullable final List<FilterItem> searchFilter)
            throws ParsingException, UnsupportedOperationException {
        return getUrl(id, contentFilters, searchFilter, ServiceList.PeerTube.getBaseUrl());
    }

    @Override
    public String getUrl(final String id,
                         final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter,
                         final String baseUrl)
            throws ParsingException, UnsupportedOperationException {
        if (id.matches(ID_PATTERN)) {
            return baseUrl + "/" + fixId(id);
        } else {
            // This is needed for compatibility with older versions were we didn't support
            // video channels yet
            return baseUrl + "/accounts/" + id;
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        try {
            if (!new BravePeertubeChannelLinkHandlerFactoryHelper().onAcceptUrl(new URL(url))) {
                return false;
            }
            return url.contains("/accounts/") || url.contains("/a/")
                    || url.contains("/video-channels/") || url.contains("/c/");
        } catch (final MalformedURLException e) {
            return false;
        }
    }

    /**
     * Fix id
     *
     * <p>
     * a/:accountName and c/:channelName ids are supported
     * by the PeerTube web client (>= v3.3.0)
     * but not by the API.
     * </p>
     *
     * @param id the id to fix
     * @return the fixed id
     */
    private String fixId(final String id) {
        if (id.startsWith("a/")) {
            return "accounts" + id.substring(1);
        } else if (id.startsWith("c/")) {
            return "video-channels" + id.substring(1);
        }
        return id;
    }
}
