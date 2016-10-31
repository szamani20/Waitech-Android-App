package com.szamani.waitech.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.szamani.waitech.adapters.ShopRecyclerViewAdapter;
import com.szamani.waitech.connections.FetchShop;
import com.szamani.waitech.interfaces.OnItemFetchTaskCompleted;
import com.szamani.waitech.interfaces.OnLoginTaskCompleted;
import com.szamani.waitech.model.ShopItem;
import com.szamani.waitech.model.ShopProfile;
import com.szamani.waitech.utility.Utility;

import java.util.List;

import szamani.com.waitech.R;


public class ShopActivity extends AppCompatActivity implements ShopRecyclerViewAdapter.OnItemClickListener,
        OnItemFetchTaskCompleted, OnLoginTaskCompleted {

    private static final String SHOP_PROFILE_TAG = "SHOP_PROFILE_TAG";

    public static String shopItemsUrl = "http://szamani.pythonanywhere.com/fetch_shop_items/";

    public static List<ShopItem> sShopItems;
    public static ShopProfile sShopProfile;

    private DrawerLayout drawerLayout;
    private CoordinatorLayout mainCoordinatorLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private FloatingActionButton checkoutFab;

    public static void navigate(AppCompatActivity activity,
                                ShopProfile shopProfile) {
        Intent intent = new Intent(activity, ShopActivity.class);
        intent.putExtra(SHOP_PROFILE_TAG, shopProfile);

        ActivityCompat.startActivity(activity, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        initShopProfileAndURLs();
        initShopItems();
        initRecyclerView();
        initFab();
        initToolbar();
        setupDrawerLayout();


        mainCoordinatorLayout = (CoordinatorLayout)
                findViewById(R.id.mainCoordinatorLayout);


//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            setRecyclerAdapter(recyclerView);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // the user expect to be logged in
        // it's not necessary to do login
        // until it is really needed (Show user Inf.)

        setHeaderText();
    }

    private void initShopProfileAndURLs() {
        sShopProfile = ((ShopProfile) getIntent().getParcelableExtra(SHOP_PROFILE_TAG));
        String id = sShopProfile.getShop_id();
        shopItemsUrl += id;
    }

    private void setHeaderText() {
        View header = navigationView.getHeaderView(0);
        if (header != null) {
            Log.d("Hooray! ", "header is not null!");
            TextView emailText = (TextView) header.findViewById(R.id.email);
            emailText.setText(Utility.getEmail(ShopActivity.this));

        } else Log.d("Sorry ", "header is null");
    }

    private boolean shouldBeLoggedIn() {
        return Utility.isLoggedIn(ShopActivity.this);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setRecyclerAdapter(RecyclerView recyclerView) {
        ShopRecyclerViewAdapter adapter = new ShopRecyclerViewAdapter(sShopItems);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initFab() {
        checkoutFab = (FloatingActionButton) findViewById(R.id.checkout_float);

        checkoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutActivity.navigate(ShopActivity.this, sShopProfile.getName());
            }
        });
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        setHeaderText();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_home:
                        Snackbar.make(mainCoordinatorLayout, "drawer_home pressed", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_login:
                        AccountActivity.navigate(ShopActivity.this,
                                Utility.isLoggedIn(ShopActivity.this));
                        break;
                    case R.id.drawer_downloaded:
                        Snackbar.make(mainCoordinatorLayout, "drawer_downloaded pressed", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_more:
                        Snackbar.make(mainCoordinatorLayout, "drawer_more pressed", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_settings:
                        Snackbar.make(mainCoordinatorLayout, "drawer_settings pressed", Snackbar.LENGTH_LONG).show();
                        break;
                }

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initShopItems() {
        new FetchShopItemsTask(this).execute();
    }

    @Override
    public void onItemFetchCompleted() {
        setRecyclerAdapter(recyclerView);
    }

    @Override
    public void onLoginCompleted(String response) {
        if (Utility.readFromCookie(getApplicationContext()) == null)
            Utility.writeToCookie(getApplicationContext(), response);
    }

    private class FetchShopItemsTask extends AsyncTask<Void, Void, List<ShopItem>> {
        private OnItemFetchTaskCompleted listener;

        public FetchShopItemsTask(OnItemFetchTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected List<ShopItem> doInBackground(Void... params) {
            return new FetchShop().getShopItems(shopItemsUrl);
        }

        @Override
        protected void onPostExecute(List<ShopItem> ShopItems) {
            sShopItems = ShopItems;
            listener.onItemFetchCompleted();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, ShopItem shopItem) {
        DetailActivity.navigate(this, view.findViewById(R.id.item_image), shopItem, sShopProfile);
    }
}

