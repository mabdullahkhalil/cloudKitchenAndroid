package com.something.mabdullahk.cloudkitchen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mabdullahk on 09/09/2018.
 *
 */

public class SectionStatePageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mfragmentlist=  new ArrayList<>();
    private final List<String> mfragmentTitleList=  new ArrayList<>();

    public SectionStatePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        mfragmentlist.add(fragment);
        mfragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mfragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentlist.size();
    }
}
