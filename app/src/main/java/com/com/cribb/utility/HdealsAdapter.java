package com.com.cribb.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.com.cribb.R;
import com.com.cribb.activities.ListingDetails;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by ekim on 2015/11/04.
 */
public class HdealsAdapter extends RecyclerView.Adapter<HdealsAdapter.ListItemViewHolder> {

    Context context;
    List<String> mDataset;
    DisplayImageOptions options;

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

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_hs_type)
                .showImageOnFail(R.drawable.ic_marker)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

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
        ImageLoader.getInstance().displayImage(
                mDataset.get(position), holder.image, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBar.setVisibility(View.GONE);
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



