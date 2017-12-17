package com.brentvanvosselen.oogappl.RestClient;

import android.content.Context;
import android.util.Log;

import com.brentvanvosselen.oogappl.util.Utils;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private static final String IPADRESS = "https://oogappl-server.herokuapp.com";

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheDir = context.getApplicationContext().getCacheDir();
        File httpCacheDirectory = new File(cacheDir, "cache_file");

        if(!httpCacheDirectory.exists()) {
            Log.i("CACHE", "CREATED");
            httpCacheDirectory.mkdir();
        }

        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new OfflineResponseCacheInterceptor(context))
                .addInterceptor(interceptor)
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                //.baseUrl("http://" + RetrofitClient.IPADRESS + ":5000/")
                .baseUrl(IPADRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    private static class OfflineResponseCacheInterceptor implements Interceptor {

        private Context context;

        public OfflineResponseCacheInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if(!Utils.isNetworkAvailable(context)) {
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 2419200)
                        .build();
            }

            return chain.proceed(request);
        }
    }
}
