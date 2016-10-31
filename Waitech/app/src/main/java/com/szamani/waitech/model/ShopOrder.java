package com.szamani.waitech.model;

/**
 * Created by Szamani on 9/15/2016.
 */
public class ShopOrder {
    SimplifiedFood[] foods;
    String customer;
    Integer shop_id;

    public ShopOrder(SimplifiedFood[] foods, String customer, Integer shop_id) {
        this.foods = foods;
        this.customer = customer;
        this.shop_id = shop_id;
    }
}
