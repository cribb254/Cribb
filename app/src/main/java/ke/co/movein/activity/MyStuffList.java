package ke.co.movein.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import ke.co.movein.R;
import ke.co.movein.adapter.MyPostsAdapter;
import ke.co.movein.utility.Functions;
import ke.co.movein.utility.RequestHandler;

public class MyStuffList extends AppCompatActivity {

    RecyclerView rv;
    SwipeRefreshLayout refresh;
    Functions fx;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stuff_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fx = new Functions(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(fx.isConnectionAvailable())
            new Fetch().execute();
        else
            Toast.makeText(this, getString(R.string.no_net), Toast.LENGTH_SHORT).show();
    }

    class Fetch extends AsyncTask<Void, Void, Boolean>{

        JSONArray dataset = new JSONArray();
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... prm) {
            try {
                //Log.w("JSON", params.toString());
                String response = rh.postJSON(fx.endPoint("get_my_posts"),new JSONObject()
                                                    .put("user_id", sp.getInt("userId", 0)));
                //Log.w("MyStuffList", response);
                dataset = new JSONObject(response).getJSONArray("data");
            }catch (Exception e){
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(dataset.length() != 0)
                rv.setAdapter(new MyPostsAdapter(dataset));
            else
                Toast.makeText(MyStuffList.this, "You have no posts!", Toast.LENGTH_LONG).show();

            refresh.setRefreshing(false);
        }
    }

}
