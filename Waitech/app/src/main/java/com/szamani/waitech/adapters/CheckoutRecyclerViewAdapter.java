package com.szamani.waitech.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.szamani.waitech.cart.model.Saleable;
import com.szamani.waitech.cart.util.CartHelper;
import com.szamani.waitech.model.ShopItem;

import java.util.List;

import szamani.com.waitech.R;

/**
 * Created by Szamani on 8/11/2016.
 */
public class CheckoutRecyclerViewAdapter extends RecyclerView.Adapter<CheckoutRecyclerViewAdapter.ViewHolder> {
    private List<Saleable> items;

    public CheckoutRecyclerViewAdapter(List<Saleable> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Saleable saleable = items.get(position);
        final ShopItem item = (ShopItem) saleable;

        holder.checkout_item_title.setText(item.getName());
        holder.count.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable)));
        holder.checkout_total_price.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable) * item.getPrice().intValue()));
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCart().getQuantity(saleable) <= 0)
                    return;
                CartHelper.getCart().update(saleable, CartHelper.getCart().getQuantity(saleable) - 1);
                holder.count.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable)));
                holder.checkout_total_price.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable) * item.getPrice().intValue()));
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCart().getQuantity(saleable) >= 15)
                    return;
                CartHelper.getCart().update(saleable, CartHelper.getCart().getQuantity(saleable) + 1);
                holder.count.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable)));
                holder.checkout_total_price.setText(String.valueOf(CartHelper.getCart().getQuantity(saleable) * item.getPrice().intValue()));
            }
        });

        // not necessary
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView checkout_item_title;
        public TextView checkout_total_price;
        public ImageView minus;
        public TextView count;
        public ImageView plus;


        public ViewHolder(View itemView) {
            super(itemView);
            checkout_item_title = (TextView) itemView.findViewById(R.id.checkout_item_title);
            checkout_total_price = (TextView) itemView.findViewById(R.id.checkout_total_price);
            minus = (ImageView) itemView.findViewById(R.id.minus);
            count = (TextView) itemView.findViewById(R.id.count);
            plus = (ImageView) itemView.findViewById(R.id.plus);
        }
    }
}
