package ke.co.movein.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.co.movein.R;
import ke.co.movein.activity.NewPost;
import ke.co.movein.utility.DbAdapter;
import ke.co.movein.utility.Functions;

/**
 * Created by ekim on 2016/02/24.
 */
public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    Functions fx;
    JSONArray dataSet;
    Context context;
    DbAdapter db;

    public static JSONObject postDetails;

    public MyPostsAdapter(JSONArray jObt) {
        dataSet = jObt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        fx = new Functions(context);
        db = new DbAdapter(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_my_posts, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {

            JSONObject jsonObject = dataSet.getJSONObject(position);
            db.open();
            String ptyType = db.getPtyType(jsonObject.getInt("tbl_pty_type_id"));
            db.close();
            holder.tvTitle.setText(ptyType);
            holder.tvPrice.setText(jsonObject.getString("last_update"));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        postDetails = dataSet.getJSONObject(position);
                        context.startActivity(new Intent(context, NewPost.class)
                        .putExtra("isEdit", true));
                    }catch (JSONException ex){
                        ex.getMessage();
                    }
                }
            });

        }catch (Exception ex){
            ex.getMessage();
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvPrice;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}

