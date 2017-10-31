package com.brentvanvosselen.oogappl.RestClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

<<<<<<< HEAD
    private static final String IPADRESS = "172.18.142.235";
=======
    private static final String IPADRESS = "172.18.153.169";
>>>>>>> b9a1781d0bc60470184cbb43a16de3ed1c16ead7

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + RetrofitClient.IPADRESS + ":5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
