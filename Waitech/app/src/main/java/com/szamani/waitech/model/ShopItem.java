package com.szamani.waitech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.szamani.waitech.cart.model.Saleable;

import java.math.BigDecimal;

/**
 * Created by Szamani on 8/1/2016.
 */

public class ShopItem implements Parcelable, Saleable {
    private String title;
    private String description;
    private String food_photo;
    private int price;


    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof ShopItem))
            return false;

        final ShopItem newItem = (ShopItem) o;

        return newItem.title.equals(this.title) &&
                newItem.price == this.price;
    }

    public ShopItem(String title, String description,
                    String food_photo, int price) {
        this.title = title;
        this.description = description;
        this.food_photo = food_photo;
        this.price = price;
    }

    protected ShopItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        food_photo = in.readString();
        price = in.readInt();
    }

    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(food_photo);
        dest.writeInt(price);
    }

    @Override
    public BigDecimal getPrice() {
        return BigDecimal.valueOf(price);
    }

    @Override
    public String getName() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFood_photo() {
        return food_photo;
    }
}
