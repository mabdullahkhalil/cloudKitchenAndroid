package com.something.mabdullahk.cloudkitchen;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class Startactivity extends AppCompatActivity {

    private SectionStatePageAdapter sectionStatePageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);


        sectionStatePageAdapter = new SectionStatePageAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.containter);

        setupPageViewer(viewPager);

    }

    private void setupPageViewer(ViewPager viewPager){
        SectionStatePageAdapter adapter= new SectionStatePageAdapter(getSupportFragmentManager());

        adapter.addFragment(new Login(),"Login");
        adapter.addFragment(new Signup(),"SignnUp");

        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int position){
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Activity", "ON RESULT CALLED");
            }
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
    }


    @Override
    public void onBackPressed() {

    }



}
