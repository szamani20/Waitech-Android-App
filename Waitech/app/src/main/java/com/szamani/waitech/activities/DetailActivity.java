
package com.szamani.waitech.activities;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.szamani.waitech.cart.model.Saleable;
import com.szamani.waitech.cart.util.CartHelper;
import com.szamani.waitech.model.ShopItem;
import com.szamani.waitech.model.ShopProfile;
import com.szamani.waitech.utility.BlurImage;

import szamani.com.waitech.R;

public class DetailActivity extends AppCompatActivity {

    private static final String IMAGE_TAG = "com.antonioleiva.materializeyourapp.extraImage";
    private static final String SHOP_ITEM = "com.antonioleiva.materializeyourapp.shopItem";
    private static final String SHOP_PROFILE = "com.antonioleiva.materializeyourapp.shopProfile";
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;

    public static void navigate(AppCompatActivity activity, View transitionImage,
                                ShopItem shopItem, ShopProfile shopProfile) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(SHOP_PROFILE, shopProfile);
        intent.putExtra(SHOP_ITEM, shopItem);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, IMAGE_TAG);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_detail);

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), IMAGE_TAG);
        supportPostponeEnterTransition();

        setSupportActionBar((Toolbar) findViewById(R.id.detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String itemTitle = ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM))
                .getName();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(itemTitle);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detailCoordinatorLayout);
        final ImageView backgroundImage = new ImageView(this);
        Picasso.with(this).load("http://szamani.pythonanywhere.com/media/" +
                ((ShopProfile) getIntent().getParcelableExtra(SHOP_PROFILE)).getBrand_photo())
                .into(backgroundImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SHOPPROFILE", "  " + ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).getFood_photo());
                        Bitmap bitmap = BlurImage.blur(DetailActivity.this, ((BitmapDrawable) backgroundImage.getDrawable()).getBitmap(), 10f);
                        backgroundImage.setImageBitmap(bitmap);
                        coordinatorLayout.setBackground(backgroundImage.getDrawable());
                    }

                    @Override
                    public void onError() {
                        Log.d("SHOPERROR", "  " + ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).getFood_photo());
                    }
                });

        final ImageView image = (ImageView) findViewById(R.id.detail_item_image);
        Picasso.with(this).load("http://szamani.pythonanywhere.com/media/" +
                ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).getFood_photo())
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SUCCESS", "  " + ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).getFood_photo());
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Log.d("ERROR", "  " + ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).getFood_photo());
                    }
                });

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(itemTitle);

        String description = ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM))
                .getDescription();
        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(description);

        String price = ((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM))
                .getPrice() + " T";
        TextView priceView = (TextView) findViewById(R.id.price);
        priceView.setText(price);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            transition.excludeTarget(findViewById(R.id.detail_item_image), true);
            transition.excludeTarget(findViewById(R.id.add_item_fab), true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground((FloatingActionButton) findViewById(R.id.add_item_fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item added to cart",
                        Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                for (Saleable item : CartHelper.getCart().getItemWithQuantity().keySet())
                                    if (((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).equals(item)) {
                                        CartHelper.getCart().remove(item, 1);
                                    }
                            }
                        });

                boolean found = false;
                for (Saleable item : CartHelper.getCart().getItemWithQuantity().keySet())
                    if (((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)).equals(item)) {
                        CartHelper.getCart().add(item, 1);
                        found = true;
                    }

                if (!found)
                    CartHelper.getCart().add(((ShopItem) getIntent().getParcelableExtra(SHOP_ITEM)), 1);

                snackbar.setActionTextColor(Color.RED);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView
                        .findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();
            }
        });
    }
}
