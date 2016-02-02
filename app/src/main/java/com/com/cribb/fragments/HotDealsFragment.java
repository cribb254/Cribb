package com.com.cribb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.com.cribb.R;
import com.com.cribb.utility.Functions;
import com.com.cribb.utility.HdealsAdapter;


public class HotDealsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hot_deals, container, false);
        assert view != null;

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new HdealsAdapter(new Functions().randImg()));


        return view;
    }


}
