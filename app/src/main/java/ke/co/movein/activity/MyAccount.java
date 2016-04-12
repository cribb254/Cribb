package ke.co.movein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import ke.co.movein.R;
import ke.co.movein.utility.Consts;
import ke.co.movein.utility.Functions;

/**
 * Created by ekim on 2016/01/21.
 */
public class MyAccount extends AppCompatActivity implements View.OnClickListener{

    public static JSONObject userInfo;
    static final String TAG = MyAccount.class.getSimpleName();

    Functions fx;

    TextView tvName,tvPhone,tvLct;

    //InterstitialAd mInterstitialAd;
    //AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fx = new Functions(this);

        Button btnMyPosts = (Button) findViewById(R.id.btn_my_posts);
        TextView tvEdit = (TextView) findViewById(R.id.tv_edit);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvLct= (TextView) findViewById(R.id.tv_location);

        showAccountDetails();
        //requestNewAd();
        btnMyPosts.setOnClickListener(this);
        tvEdit.setOnClickListener(this);

        try{
            switch (getIntent().getIntExtra(Consts.ACTION, 0)){
                case Consts.PROCEED_TO_MYPOSTS:
                    startActivity(new Intent(MyAccount.this, MyStuffList.class));
                    break;
            }
        }catch (Exception ex){
            //ex.getMessage();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                //showAd();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
              /*
                pseudoIDs
                myposts = 1;
                myorders = 2;
                rec_orders = 3
             */
            case R.id.btn_my_posts:
                startActivity(new Intent(MyAccount.this, MyStuffList.class));
                break;
            case R.id.tv_edit:
                startActivity(new Intent(this, SignUp.class).putExtra("isEdit",true));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAccountDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //showAd();
    }

    void showAccountDetails(){
        try {
            tvName.setText(userInfo.getString("fname"));
                    //String.format("%s %s", userInfo.getString("fname"), userInfo.getString("lname")));
            tvPhone.setText(userInfo.getString("msisdn"));
            tvLct.setText(userInfo.getString("location"));
        }catch (Exception ex){
            ex.getMessage();
        }
    }
/*
    private void showAd() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            requestNewAd();
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
        }
    }

    private void requestNewAd() {
        //reload the ad to prepare for next build.
        mInterstitialAd = new InterstitialAd(this);
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .setRequestAgent("android_studio:ad_template").build();

        mInterstitialAd.setAdUnitId(getString(R.string.ad_myaccount_interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // do something
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // do something
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                requestNewAd();
                finish();
            }
        });
        mInterstitialAd.loadAd(adRequest);
    }
    */
}
