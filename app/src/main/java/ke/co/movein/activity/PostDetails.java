package ke.co.movein.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.solovyev.android.views.llm.DividerItemDecoration;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.List;

import ke.co.movein.R;
import ke.co.movein.adapter.ListAdapter;
import ke.co.movein.adapter.PostImagesAdapter;
import ke.co.movein.utility.Consts;
import ke.co.movein.utility.DbAdapter;
import ke.co.movein.utility.Functions;
import ke.co.movein.utility.RequestHandler;

public class PostDetails extends AppCompatActivity {

    FloatingActionButton fab;
    TextView tvSeller;
    Functions fx;
    int sellerId;
    boolean isMyPost;

    public static List<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Toolbar toolbar = (Toolbar) findViewById(ke.co.movein.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(ke.co.movein.R.id.fab);
        DbAdapter db = new DbAdapter(this);
        fx = new Functions(this);

        db.open();
        Cursor crs = db.getPost(ListAdapter.postID);
        imageUrls = db.getImages(ListAdapter.postID);
        String ptyType = db.getPtyType(Integer.parseInt(fx.getColumn(crs, "tbl_pty_type_id")));

        db.close();

        sellerId = crs.getInt(crs.getColumnIndexOrThrow("tbl_user_id"));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isMyPost = sp.getInt("userId",0) == sellerId;

        //Log.w(getClass().getSimpleName(), String.format("SellerID %d UserID %d", sellerId, sp.getInt("userId",0)));

        if(fx.isConnectionAvailable())
            new GetSellerNumber().execute();

        TextView tvPtyType = (TextView) findViewById(R.id.tv_pty_type);
        TextView tvLoc = (TextView) findViewById(R.id.tv_loc);
        TextView tvPrice = (TextView) findViewById(R.id.tv_price);
        tvSeller = (TextView) findViewById(R.id.tv_seller);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);

        tvPtyType.setText(ptyType);
        tvLoc.setText(fx.getColumn(crs, "location"));
        tvPrice.setText(String.format("Ksh %s", fx.getColumn(crs, "price")));

        String desc = fx.getColumn(crs, "description");
        if(!desc.matches("null") && desc.trim().length() != 0) {
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(desc);
        }

        RecyclerView horizontalList = (RecyclerView) findViewById(R.id.horizontal_rv);
        horizontalList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalList.addItemDecoration(new DividerItemDecoration(this, null));
        TextView tvNumImgs = (TextView) findViewById(R.id.tv_num_imgs);
        CardView cvHzlImgs = (CardView) findViewById(R.id.cv_hzl_imgs);

        if(imageUrls != null) {
            horizontalList.setAdapter(new PostImagesAdapter(imageUrls));
            tvNumImgs.setText(String.valueOf(imageUrls.size()));
            cvHzlImgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Gallery.class)
                            .putExtra("position", 0));
                }
            });
        }else{
            cvHzlImgs.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    class GetSellerNumber extends AsyncTask<Void, Void, Boolean>{

        String msisdn = "";
        String name = "";

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = new RequestHandler().postJSON(fx.endPoint("get_seller_msisdn"),
                        new JSONObject().put("user_id", sellerId));
                //Log.w("SELLER_MSISDN", response);

                JSONObject data = new JSONObject(response);
                if(data.getInt("success") == 1) {
                    msisdn = data.getString("msisdn");
                    name = data.getString("name");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(msisdn.trim().length()!=0){
                tvSeller.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                tvSeller.setText(name);
                if(isMyPost)
                    fab.setImageResource(R.drawable.ic_fab_edit);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isMyPost) {
                            if(MainActivity.isLoggedIn){
                                startActivity(new Intent(PostDetails.this,  MyAccount.class)
                                        .putExtra(Consts.ACTION, Consts.PROCEED_TO_MYPOSTS));
                            }else{
                                startActivity(new Intent(PostDetails.this, Login.class)
                                        .putExtra(Consts.ACTION, Consts.PROCEED_TO_MYACCOUNT));
                            }
                        }else {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(String.format("tel:%s", msisdn)));
                            startActivity(intent);
                        }

                    }
                });
            }
        }
    }

}
