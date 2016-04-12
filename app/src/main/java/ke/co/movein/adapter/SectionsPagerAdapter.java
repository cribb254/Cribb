package ke.co.movein.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ke.co.movein.fragment.AroundMe;
import ke.co.movein.fragment.PostList;

/**
 * Created by ekim on 2016/04/06.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment =  new PostList();
                notifyDataSetChanged();
                break;
            case 1:
                fragment = new AroundMe();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Hot Deals";
            case 1:
                return "Around Me";
        }
        return null;
    }

}