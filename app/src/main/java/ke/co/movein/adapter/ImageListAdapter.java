package ke.co.movein.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import ke.co.movein.R;
import ke.co.movein.activity.NewPost;

/**
 * Created by ekim on 2016/02/11.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    ArrayList<String> dataSet;
    Context context;
    boolean isEdit;

    public ImageListAdapter(ArrayList<String> items, boolean isEdit) {
        dataSet = items;
        this.isEdit = isEdit;
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

        if(isEdit){
            holder.imDelete.setVisibility(View.VISIBLE);
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
            holder.imDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSet.remove(position);
                    NewPost.oldImagesArr = dataSet;
                    notifyDataSetChanged();
                }
            });
        }else{
            Picasso.with(context)
                    .load(new File(dataSet.get(position)))
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
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imDelete;
        ProgressBar progressBar;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            imDelete = (ImageView) view.findViewById(R.id.im_delete);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }

    }
}
