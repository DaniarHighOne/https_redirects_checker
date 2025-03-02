package kz.fcbk.echo.core.seederstest.config;


import okhttp3.OkHttpClient;

public class OkHttpClientProvider {
    public static OkHttpClient createClient( boolean followRedirects){
            return new OkHttpClient.Builder()
                    .followRedirects(followRedirects)
                    .build();
        }

    }

