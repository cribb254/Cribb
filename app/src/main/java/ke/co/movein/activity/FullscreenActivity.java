package ke.co.movein.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.viewpagerindicator.CirclePageIndicator;

import ke.co.movein.fragment.Blue;
import ke.co.movein.fragment.Green;
import ke.co.movein.fragment.Yellow;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ke.co.movein.R.layout.activity_fullscreen);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt("previewed", 1).apply();

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(ke.co.movein.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(ke.co.movein.R.id.indicator);
        circlePageIndicator.setViewPager(mViewPager);

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment =  new Green();
                    break;
                case 1:
                    fragment = new Yellow();
                    break;
                case 2:
                    fragment = new Blue();
                    break;
                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}
