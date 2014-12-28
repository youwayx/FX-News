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

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuwei on 2014-10-06.
 */
public class ViewPagerActivity extends FragmentActivity{
    private static final int NUM_PAGES = 5;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.v("firebase", "hi");


        setContentView(R.layout.view_pager);
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
        mPager.setAdapter(mPagerAdapter);
        fragments = new FxFragment[5];
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


                if(state == 0){ // the viewpager is idle as swipping ended
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if(!dontLoadList) {
                                if (fragments[positionCurrent]!=null){
                                    if (fragments[positionCurrent].loadPage==true) {
                                        Log.v("loadTextViews","loadPage = true");

                                        fragments[positionCurrent].update();

                                        //[positionCurrent].loadTextViews=false;

                                    }

                                }
                            }
                        }
                    },100);
                }

            }

            @Override
            public void onPageSelected(int position) {

                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                //invalidateOptionsMenu();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String [] drawerTitles = {"About"};
        // Set the adapter for the list view
        final Context context = this;
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item,drawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                SpannableString s = new SpannableString("FX News");
                s.setSpan(new TypeFaceSpan(context, "Roboto-Thin.ttf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update the action bar title with the TypefaceSpan instance
                ActionBar actionBar = getActionBar();
                actionBar.setTitle(s);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                SpannableString s = new SpannableString("FX News");
                s.setSpan(new TypeFaceSpan(context, "Roboto-Thin.ttf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Update the action bar title with the TypefaceSpan instance
                ActionBar actionBar = getActionBar();
                actionBar.setTitle(s);

            }
        };

        /*this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.title_textview, null);
        */
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        SpannableString s = new SpannableString("FX News");
        s.setSpan(new TypeFaceSpan(this, "Roboto-Thin.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
        // Set the list's click listener
       //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        //stopLoading();

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
        SimpleDateFormat df = new SimpleDateFormat("MM dd, yyyy");
        String rawDate = df.format(new Date());
        String date = prefs.getString("date","none");
        if (date.equals("none") || !rawDate.equals(date)){
            fragments= new FxFragment[5];
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
       /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            Log.v("loadpages", Arrays.toString(loadPages));
            //return new FxFragment(position);


            if (fragments[position]==null){

                fragments[position]=new FxFragment(position);
                //setFragmentsVisible();
                Log.v("this is being called for",position+"");
                return fragments[position];
            }
            else{

                return fragments[position];
            }

        }

        /*@Override
        public Fragment getItem(int position) {
            return SlidePageFragment.create(position);
        }*/

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
            Log.v("actionbar, statusbar", actionBarHeight + " " + statusBarHeight);
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
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

    class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Context context = parent.getContext();
            CharSequence text = "Created By Yuwei Xu";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            mDrawerLayout.closeDrawer(mDrawerList);
            //selectItem(position);
        }

    }
}


