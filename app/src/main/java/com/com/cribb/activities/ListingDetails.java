package com.com.cribb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.com.cribb.R;
import com.com.cribb.utility.Functions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

public class ListingDetails extends AppCompatActivity {

    String firstImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstImageURL = getIntent().getStringExtra("imgURL");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(String.format("tel:%s", "0739222003")));
                startActivity(intent);
            }
        });

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter());
        //pager.setCurrentItem(INDEX);

        CirclePageIndicator cpi = (CirclePageIndicator) findViewById(R.id.cpi);
        cpi.setViewPager(pager);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private class ImageAdapter extends PagerAdapter {

        List<String> IMAGE_URLS = new Functions().slideShowImg(firstImageURL);

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = getLayoutInflater().inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            Picasso.with(getApplicationContext())
                    .load(IMAGE_URLS.get(position))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
