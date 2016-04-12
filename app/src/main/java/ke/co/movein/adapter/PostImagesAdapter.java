package ke.co.movein.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ke.co.movein.R;
import ke.co.movein.activity.Gallery;

/**
 * Created by ekim on 2016/04/07.
 */
public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ViewHolder> {

    List<String> dataSet;
    Context context;

    public PostImagesAdapter(List<String> items) {
        dataSet = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_horizontal_images, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

            Picasso.with(context)
                    .load(dataSet.get(position))
                    .resize(100, 100)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.imageView.setImageResource(R.drawable.ic_null);
                        }
                    });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Gallery.class)
                        .putExtra("position", position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }

    }
}
