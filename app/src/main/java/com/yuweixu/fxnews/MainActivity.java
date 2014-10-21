package com.yuweixu.fxnews;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;


import com.yuweixu.fxnews.Custom.TypeFaceSpan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class MainActivity extends ActionBarActivity {
    public static Typeface font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String date = getStartDate();
        //initializePaging();
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FxFragment())
                    .commit();
        }*/
        SpannableString s = new SpannableString("FX News");
        s.setSpan(new TypeFaceSpan(this, "Roboto-Thin.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Update the action bar title with the TypefaceSpan instance
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
        startActivity(new Intent(MainActivity.this, ViewPagerActivity.class));
        this.deleteDatabase("fxnews.db");


       //font = Typeface.createFromAsset(getAssets(), "Raleway-Regular.otf");


    }

    /*public String getStartDate (){
        SimpleDateFormat df = new SimpleDateFormat("MM dd, yyyy");
        String rawDate = df.format(new Date());
        return rawDate;
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
