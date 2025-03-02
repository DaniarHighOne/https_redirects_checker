package kz.fcbk.echo.core.seederstest.models;

import java.util.List;
import java.util.Map;

public record UrlResponseResult(
        String url,
        int statusCode,
        Map<String, List<String>> headers,
        List<RedirectedUrlStep> redirectChain,
        String error
) {

    public static UrlResponseResult createRequest(String url, int statusCode, Map<String, List<String>> headers,
                                                  List<RedirectedUrlStep> redirects) {
        return new UrlResponseResult(
                url, statusCode, headers,redirects, null);
    }

    public static UrlResponseResult error(String url, String errorMessage, List<RedirectedUrlStep> redirects) {
        return new UrlResponseResult(url,-1,null, redirects, errorMessage);
    }

    public boolean hasRedirects() {
        return !redirectChain.isEmpty();
    }

     public int getFinalStatusCode() {
        if (redirectChain.isEmpty()) return -1; {
            return redirectChain.getLast().statusCode();
        }
     }

}
