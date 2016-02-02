package com.com.cribb.utility;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.com.cribb.R;
import com.com.cribb.activities.ListingDetails;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ekim on 2015/11/04.
 */
public class HdealsAdapter extends RecyclerView.Adapter<HdealsAdapter.ListItemViewHolder> {

    Context context;
    List<String> mDataset;

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvHsType;
        TextView tvPrice;
        CardView cardView;
        ImageView image;
        ProgressBar progressBar;

        public ListItemViewHolder(View itemView) {
            super(itemView);

            tvHsType = (TextView)itemView.findViewById(R.id.tv_hs_type);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            image = (ImageView)itemView.findViewById(R.id.image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HdealsAdapter(List<String> myDataset) {
        mDataset = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hd_row_layout, parent, false);

        context = parent.getContext();

        return new ListItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, final int position) {
/*
        Random rand = new Random();
        holder.tvHsType.setText(mDataset.get(position));
        holder.tvPrice.setText(5000 + rand.nextInt(50000) + " /=");
*/
        Picasso.with(context)
                .load(mDataset.get(position))
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListingDetails.class);
                intent.putExtra("imgURL", mDataset.get(position));
                context.startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}



