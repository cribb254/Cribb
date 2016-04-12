package ke.co.movein.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.movein.R;
import ke.co.movein.activity.Login;
import ke.co.movein.activity.MainActivity;
import ke.co.movein.activity.NewPost;
import ke.co.movein.adapter.ListAdapter;
import ke.co.movein.adapter.SectionsPagerAdapter;
import ke.co.movein.utility.Consts;
import ke.co.movein.utility.DbAdapter;
import ke.co.movein.utility.Functions;

public class PostList extends Fragment {

    Functions fx;
    DbAdapter db;
    SwipeRefreshLayout refresh;
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        assert view != null;

        fx = new Functions(getActivity());
        db = new DbAdapter(getActivity());

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        rv = (RecyclerView) view.findViewById(ke.co.movein.R.id.recycler_view);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isLoggedIn)
                    startActivity(new Intent(getActivity(), NewPost.class));
                else
                    startActivity(
                            new Intent(getActivity(), Login.class)
                                    .putExtra(Consts.ACTION, Consts.PROCEED_TO_ADDPOST));
            }
        });

        db.open();
        boolean bln = db.isDbEmpty();
        db.close();
        //Log.w("isDbEmpty", String.valueOf(bln));
        //1st sync
        if(bln && fx.isConnectionAvailable()){
            new LoadStuff(false).execute();
        }
        rv.setAdapter(new ListAdapter(getActivity()));

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(fx.isConnectionAvailable())
                    new LoadStuff(true).execute();
            }
        });


        return view;
    }

    class LoadStuff extends AsyncTask<Void, Void, Boolean> {

        boolean isAlreadyRefreshing;

        LoadStuff(boolean isAlreadyRefreshing){
            this.isAlreadyRefreshing = isAlreadyRefreshing;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isAlreadyRefreshing)
                refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            fx.autoSync();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            refresh.setRefreshing(false);
            if(isAlreadyRefreshing)
                rv.setAdapter(new ListAdapter(getActivity()));
            else
                MainActivity.mViewPager.setAdapter(new SectionsPagerAdapter(getActivity().getSupportFragmentManager()));
        }
    }


}
