package com.zhuoxin.treasure.net;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/1/9.
 */

public class NetClient {

    private static NetClient netClient;
    private static Retrofit retrofit;
    public static final String BASE_URL = "http://admin.syfeicuiedu.com";
    private TreasureApi treasureApi;


    private NetClient() {
        //retrofit的初始化

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()// 设置Gson的非严格模式
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        treasureApi = retrofit.create(TreasureApi.class);
    }

    public static NetClient getInstances() {
        if (netClient == null) {
            netClient = new NetClient();
        }
        return netClient;
    }

    public TreasureApi getTreasureApi() {
        if (treasureApi == null) {
            treasureApi = retrofit.create(TreasureApi.class);
        }
        return treasureApi;
    }
}
