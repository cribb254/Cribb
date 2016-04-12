package ke.co.movein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ke.co.movein.BuildConfig;
import ke.co.movein.R;
import ke.co.movein.adapter.SectionsPagerAdapter;
import ke.co.movein.utility.Consts;
import ke.co.movein.utility.CustomDialog;

public class MainActivity extends AppCompatActivity {

    public static ViewPager mViewPager;
    public static boolean isLoggedIn = false;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(ke.co.movein.R.id.toolbar);
            setSupportActionBar(toolbar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);

        // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(ke.co.movein.R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(ke.co.movein.R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
/*

        db.open();
        String image0Path = db.getImagePath(6);
        Log.w("ImagePath0", image0Path);
        db.close();


        //startService(new Intent(this, Sync.class));

        if(PreferenceManager.getDefaultSharedPreferences(this).getInt("previewed", 0)==0) {
            startActivity(new Intent(MainActivity.this, FullscreenActivity.class));
        }

*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ke.co.movein.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account:
                if(isLoggedIn){
                    startActivity(new Intent(MainActivity.this,  MyAccount.class));
                }else{
                    startActivity(new Intent(MainActivity.this, Login.class)
                            .putExtra(Consts.ACTION, Consts.PROCEED_TO_MYACCOUNT));
                }
                break;
            case R.id.action_search:
                startActivity(new Intent(this, Search.class));
                break;
            case R.id.action_about:
                Spanned body = Html.fromHtml(
                        "MoveIn v " + BuildConfig.VERSION_NAME + "<br>\n" +
                                "Copyright © 2016<br>\n" +
                                "All rights reserved. <a href=\"http://www.movein.co.ke\">MoveIn, Inc.</a>"

                );
                new CustomDialog().newInstance(getString(R.string.app_name),
                        body.toString(), "ok"
                ).show(getFragmentManager(), getClass().getSimpleName());

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onBackPressed() {
        // Close app
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            isLoggedIn = false;
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}


