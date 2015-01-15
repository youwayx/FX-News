package com.yuweixu.fxnews;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.yuweixu.fxnews.Custom.FixedSpeedScroller;
import com.yuweixu.fxnews.Custom.TypeFaceSpan;
import com.yuweixu.fxnews.Material.NavigationDrawerCallbacks;
import com.yuweixu.fxnews.Material.NavigationDrawerFragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuwei on 2014-10-06.
 */
public class ViewPagerActivity extends ActionBarActivity implements NavigationDrawerCallbacks {
    private static final int NUM_PAGES = 6;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    static boolean [] loadPages;
    private PagerAdapter mPagerAdapter;
    static FxFragment [] fragments;
    static boolean dontLoadList;
    SharedPreferences prefs;
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    String [] dates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        dates = new String[6];
        new FetchNewsTask(this).execute();

        setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color.navy));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SimpleDateFormat df = new SimpleDateFormat("MM dd, yyyy");
        String rawDate = df.format(new Date());
        prefs.edit().putString("date",rawDate);
        loadPages = new boolean [5];
        for (int i=0; i<5; i++){
            loadPages[i]=true;
        }
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        fragments = new FxFragment[6];
        mPager.setOffscreenPageLimit(4);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int positionCurrent;
            //boolean dontLoadList;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                positionCurrent = position;
                if( positionOffset == 0 && positionOffsetPixels == 0 ) // the offset is zero when the swiping ends{
                    dontLoadList = false;

                else
                    dontLoadList = true;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
            }
        });
    }
    void setAdapter(){
        mPager.setAdapter(mPagerAdapter);
    }
    void stopLoading(){
        RelativeLayout bar = (RelativeLayout)findViewById(R.id.progress_container);
        bar.setVisibility(View.GONE);


    }
    void setFragmentsVisible(){
        FxFragment f = fragments[0];
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    /*Adapter for loading/displaying the ViewPager fragments */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            Log.v("loadpages", Arrays.toString(loadPages));
            if (fragments[position]==null){
                FxFragment f = new FxFragment();
                String fDate = dates[position];
                Bundle args = new Bundle();
                args.putString("date", fDate);
                f.setArguments(args);       //gives the fragment the date it should be querying for
                fragments[position] = f;
                return fragments[position];

            }
            else{

                return fragments[position];
            }
        }
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed(){

    }
    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Log.v("actionbar, statusbar",actionBarHeight + " "+statusBarHeight);
            //action bar height
            statusBar.getLayoutParams().height = statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))    {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}


