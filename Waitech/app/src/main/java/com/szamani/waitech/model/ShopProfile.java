package com.szamani.waitech.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szamani on 8/8/2016.
 */
public class ShopProfile implements Parcelable {
    private String brand_photo;
    private String phone_numbers;
    private String address;
    private String shop_id;
    private String name;

    public ShopProfile(String name, String brand_photo, String phone_numbers, String address, String shop_id) {
        this.name = name;
        this.brand_photo = brand_photo;
        this.phone_numbers = phone_numbers;
        this.address = address;
        this.shop_id = shop_id;
    }

    protected ShopProfile(Parcel in) {
        brand_photo = in.readString();
        phone_numbers = in.readString();
        address = in.readString();
        shop_id = in.readString();
        name = in.readString();
    }

    public static final Creator<ShopProfile> CREATOR = new Creator<ShopProfile>() {
        @Override
        public ShopProfile createFromParcel(Parcel in) {
            return new ShopProfile(in);
        }

        @Override
        public ShopProfile[] newArray(int size) {
            return new ShopProfile[size];
        }
    };

    public String getBrand_photo() {
        return brand_photo;
    }

    public String getPhone_numbers() {
        return phone_numbers;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getShop_id() {
        return shop_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brand_photo);
        dest.writeString(phone_numbers);
        dest.writeString(address);
        dest.writeString(shop_id);
        dest.writeString(name);
    }
}
