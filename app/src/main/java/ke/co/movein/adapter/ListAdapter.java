package ke.co.movein.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ke.co.movein.R;
import ke.co.movein.activity.PostDetails;
import ke.co.movein.utility.DbAdapter;
import ke.co.movein.utility.Functions;

/**
 * Created by ekim on 2015/11/04.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    Context context;
    Cursor dataSet;
    DbAdapter db;
    Functions fx;

    public static int postID;

    public ListAdapter(Context context) {
        this.context = context;
        db = new DbAdapter(context);
        fx = new Functions(context);
        db.open();
        dataSet = db.getPostList(0);
        db.close();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post, parent, false);

        context = parent.getContext();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        dataSet.moveToPosition(position);
        db.open();
        String image0Path = db.getImagePath(Integer.parseInt(fx.getColumn(dataSet, "id")));
        String ptyType = db.getPtyType(Integer.parseInt(fx.getColumn(dataSet, "tbl_pty_type_id")));
        //Log.w("ImagePath0", image0Path);
        db.close();

        if(image0Path != null) {
            Picasso.with(context)
                    .load(image0Path)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.image.setImageResource(R.drawable.ic_null);
                        }
                    });
        }
        else{
            holder.image.setImageResource(R.drawable.ic_null);
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.tvPtyType.setText(ptyType);
        holder.tvPrice.setText(String.format("Ksh %s", fx.getColumn(dataSet, "price")));
        holder.tvLocation.setText(fx.getColumn(dataSet, "location"));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.moveToPosition(position);
                postID = Integer.parseInt(fx.getColumn(dataSet, "id"));
                context.startActivity(new Intent(context, PostDetails.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPtyType;
        TextView tvPrice;
        TextView tvLocation;
        CardView cardView;
        ImageView image;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            tvPtyType = (TextView) view.findViewById(ke.co.movein.R.id.tv_pty_type);
            tvPrice = (TextView) view.findViewById(ke.co.movein.R.id.tv_price);
            tvLocation = (TextView) view.findViewById(R.id.tv_loc);
            cardView = (CardView) view.findViewById(ke.co.movein.R.id.card_view);
            image = (ImageView) view.findViewById(ke.co.movein.R.id.image);
            progressBar = (ProgressBar) view.findViewById(ke.co.movein.R.id.progressBar);

        }
    }

}



