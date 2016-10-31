package com.szamani.waitech.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.szamani.waitech.adapters.CheckoutRecyclerViewAdapter;
import com.szamani.waitech.cart.model.Saleable;
import com.szamani.waitech.cart.util.CartHelper;
import com.szamani.waitech.interfaces.OnOrderCustomerTaskCompleted;
import com.szamani.waitech.interfaces.OnOrderShopTaskCompleted;
import com.szamani.waitech.model.CustomerOrder;
import com.szamani.waitech.model.ShopOrder;
import com.szamani.waitech.model.SimplifiedFood;
import com.szamani.waitech.utility.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import szamani.com.waitech.R;

/**
 * Created by Szamani on 8/11/2016.
 */
public class CheckoutActivity extends AppCompatActivity
        implements OnOrderShopTaskCompleted, OnOrderCustomerTaskCompleted {
    private static List<Saleable> items;
    private RecyclerView recyclerView;
    private Button checkout_button;
    private CoordinatorLayout mCoordinatorLayout;
    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");
    private static final String SHOP_NAME_TAG = "SHOP_NAME_TAG";

    private static final String orderCustomerBaseUrl =
            "https://restaurant-user.herokuapp.com/order/";

    public static void navigate(AppCompatActivity activity, String shopName) {
        Intent intent = new Intent(activity, CheckoutActivity.class);
        intent.putExtra(SHOP_NAME_TAG, shopName);
        ActivityCompat.startActivity(activity, intent, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_checkout);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.checkout_coordinator_layout);

        initToolbar();
        initRecyclerView();
        initCheckout();
    }

    private void initCheckout() {
        checkout_button = (Button) findViewById(R.id.checkout_button);

        checkout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderRequest();

                final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Your order sent to shop",
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private String createShopJsonFromOrder() {
        Map<Saleable, Integer> ordersAndQuantity =
                CartHelper.getCart().getItemWithQuantity();
        List<SimplifiedFood> foods = new ArrayList<>();
        for (Saleable s : ordersAndQuantity.keySet())
            foods.add(new SimplifiedFood(s.getName(),
                    s.getPrice().intValue(), ordersAndQuantity.get(s)));
        ShopOrder shopOrder = new ShopOrder(foods.toArray(new SimplifiedFood[foods.size()]),
                "custormer 1",
                Integer.valueOf(ShopActivity.sShopProfile.getShop_id()));

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ShopOrder> adapter = moshi.adapter(ShopOrder.class);

        return adapter.toJson(shopOrder);
    }

    private String createUserJsonFromOrder() {
        Map<Saleable, Integer> ordersAndQuantity =
                CartHelper.getCart().getItemWithQuantity();
        List<SimplifiedFood> foods = new ArrayList<>();
        for (Saleable s : ordersAndQuantity.keySet())
            foods.add(new SimplifiedFood(s.getName(),
                    s.getPrice().intValue(), ordersAndQuantity.get(s)));
        CustomerOrder customerOrder = new CustomerOrder(foods.toArray(new SimplifiedFood[foods.size()]),
                getIntent().getStringExtra(SHOP_NAME_TAG));

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CustomerOrder> adapter = moshi.adapter(CustomerOrder.class);

        return adapter.toJson(customerOrder);
    }

    private void sendOrderRequest() {
        String jsonOrder = createShopJsonFromOrder();
        Log.d("JSON SHOP ORDER ", jsonOrder);
        new OrderShopTask(this, jsonOrder).execute();
    }

    @Override
    public void onOrderShopCompleted(String response) {
        String jsonOrder = createUserJsonFromOrder();
        Log.d("JSON CUSTOMER ORDER ", jsonOrder);
        new OrderCustomerTask(jsonOrder, CheckoutActivity.this, CheckoutActivity.this).execute();
    }

    @Override
    public void onOrderCustomerCompleted(String response) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, response,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private class OrderCustomerTask extends AsyncTask<Void, Void, Void> {
        String orderRequest;
        OnOrderCustomerTaskCompleted listener;
        Context mContext;

        public OrderCustomerTask(String orderRequest, OnOrderCustomerTaskCompleted listener,
                                 Context context) {
            this.orderRequest = orderRequest;
            this.listener = listener;
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, orderRequest);

            Request request;

            String savedCookie = Utility.readFromCookie(mContext);

            if (savedCookie != null) {
                request = new Request.Builder()
                        .url(orderCustomerBaseUrl)
                        .post(body)
                        .addHeader("Cookie", savedCookie)
                        .build();
                Log.d("Cookie Sent: ", savedCookie);
            } else request = new Request.Builder()
                    .url(orderCustomerBaseUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("Response: ", " " + response.toString());

                Headers headers = response.headers();
                String cookieReceived = "";

                for (String item : headers.values("Set-Cookie"))
                    if (item.contains("sessionid")) {
                        cookieReceived = item;
                        Log.d("item: ", item);
                        break;
                    }

                Log.d("Cookie received: ", cookieReceived);

                listener.onOrderCustomerCompleted(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class OrderShopTask extends AsyncTask<Void, Void, Void> {
        String orderRequest;
        OnOrderShopTaskCompleted listener;

        public OrderShopTask(OnOrderShopTaskCompleted listener, String orderRequest) {
            this.orderRequest = orderRequest;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, orderRequest);
            Request request = new Request.Builder()
                    .url("http://szamani.pythonanywhere.com/order_food")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                listener.onOrderShopCompleted(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void initRecyclerView() {
        items = new ArrayList<>(CartHelper.getCart().getProducts());

        recyclerView = (RecyclerView) findViewById(R.id.checkout_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CheckoutRecyclerViewAdapter adapter = new CheckoutRecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.checkout_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

