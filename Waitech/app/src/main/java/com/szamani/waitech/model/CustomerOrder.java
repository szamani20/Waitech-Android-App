package com.szamani.waitech.model;

/**
 * Created by Szamani on 9/23/2016.
 */
public class CustomerOrder {
    SimplifiedFood[] foods;
    String restaurant_name;

    public CustomerOrder(SimplifiedFood[] foods, String restaurant_name) {
        this.foods = foods;
        this.restaurant_name = restaurant_name;
    }
}
