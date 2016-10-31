package com.szamani.waitech.connections;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.szamani.waitech.model.ShopItem;
import com.szamani.waitech.model.ShopProfile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Szamani on 8/1/2016.
 */
public class FetchShop {
    private String getUrlString(String urlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Log.d("getUrlString", " " + urlSpec);

        Request request = new Request.Builder()
                .url(urlSpec)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public List<ShopItem> getShopItems(String url) {
        try {
            String all_foods = getUrlString(url);
            Moshi moshi = new Moshi.Builder().build();
            Type listMyData = Types.newParameterizedType(List.class, ShopItem.class);
            JsonAdapter<List<ShopItem>> adapter = moshi.adapter(listMyData);

            List<ShopItem> models = adapter.fromJson(all_foods);

            return models;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ShopProfile getShopProfile(String url) {
        try {
            String shop_profile = getUrlString(url);
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<ShopProfile> adapter = moshi.adapter(ShopProfile.class);

            ShopProfile profile = adapter.fromJson(shop_profile);

            return profile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
