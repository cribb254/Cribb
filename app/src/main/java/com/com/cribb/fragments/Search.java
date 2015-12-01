package com.com.cribb.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.com.cribb.R;
import com.com.cribb.activities.SearchResults;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment {

    DecimalFormat df;
    EditText etMin;
    EditText etMax;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etMin = (EditText) view.findViewById(R.id.et_min);
        etMax = (EditText) view.findViewById(R.id.et_max);

        df = new DecimalFormat("###,###,###");
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(symbols);

        // Seek bar for which we will set text color in code
        RangeSeekBar rsb = (RangeSeekBar) view.findViewById(R.id.rangeSeekBar);
        rsb.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                etMin.setText(df.format(Integer.parseInt(minValue.toString())));
                etMax.setText(df.format(Integer.parseInt(maxValue.toString())));
            }
        });

        Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchResults.class));
            }
        });

        return view;
    }


}
