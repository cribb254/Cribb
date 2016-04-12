package ke.co.movein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import ke.co.movein.R;

public class Search extends AppCompatActivity {

    DecimalFormat df;
    EditText etMin;
    EditText etMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etMin = (EditText) findViewById(R.id.et_min);
        etMax = (EditText) findViewById(R.id.et_max);

        df = new DecimalFormat("###,###,###");
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(symbols);

        // Seek bar for which we will set text color in code
        RangeSeekBar rsb = (RangeSeekBar) findViewById(R.id.rangeSeekBar);
        rsb.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                etMin.setText(df.format(Integer.parseInt(minValue.toString())));
                etMax.setText(df.format(Integer.parseInt(maxValue.toString())));
            }
        });

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchResults.class));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
