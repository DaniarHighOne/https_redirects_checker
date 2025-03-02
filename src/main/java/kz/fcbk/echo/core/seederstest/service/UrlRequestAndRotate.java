package kz.fcbk.echo.core.seederstest.service;


import kz.fcbk.echo.core.seederstest.config.OkHttpClientProvider;
import kz.fcbk.echo.core.seederstest.models.RedirectedUrlStep;
import kz.fcbk.echo.core.seederstest.models.UrlResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class UrlRequestAndRotate {

    private final OkHttpClient client;
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final List<String> urls;



    public UrlRequestAndRotate(OkHttpClient client, List<String> urls) {
        this.client = client;
        this.urls = validateUrls(urls);

    }


    public UrlResponseResult makeRequest() {
        String originUrl = getNextUrl();
        List<RedirectedUrlStep> redirectChain = new ArrayList<>();
        String currentUrl = originUrl;


        try {
            while (true) {

                Request request = new Request.Builder().url(currentUrl).build();
                try (Response response = client.newCall(request).execute()) {
                    RedirectedUrlStep urlStep = new RedirectedUrlStep(currentUrl, response.code());
                    redirectChain.add(urlStep);

                    if (response.isRedirect()) {
                        String location = response.header("Location");
                        if (location == null || containsCycle(redirectChain)) {
                            return UrlResponseResult.error(originUrl, "Redirect loop detected or missing location header!",
                                    redirectChain
                            );
                        }
                        currentUrl = location;
                    } else {
                    return UrlResponseResult.createRequest(originUrl, response.code(),
                            response.headers().toMultimap(), redirectChain);
                }
            }
        }
    } catch (IOException e) {
        return UrlResponseResult.error(originUrl,"IO Error: "
                + e.getMessage(), redirectChain);
        }
    }

    private boolean containsCycle(List<RedirectedUrlStep> redirectChain) {
        return redirectChain.stream()
                .map(RedirectedUrlStep ::url)
                .distinct()
                .count() < redirectChain.size();
    }


    private String getNextUrl() {
        int index = currentIndex.getAndIncrement();
        return urls.get(index);
    }

    private List<String> validateUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("urls is null or empty");
        }
         List<String> validUrls = new ArrayList<>(urls);
        for (String url : urls) {
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("Invalid url in list: " + url);
            }
            validUrls.add(url);
        } return validUrls;
    }
}

