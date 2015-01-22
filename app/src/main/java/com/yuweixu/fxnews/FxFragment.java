package com.yuweixu.fxnews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.yuweixu.fxnews.Custom.NewsCursorAdapter;

import org.apache.http.HttpConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Yuwei on 2014-08-07.
 */
public class FxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter mNewsAdapter;
    NewsCursorAdapter mNewsAdapter2;
    public int LOADER_ID = 0;
    static String [] dates;
    public String [] columns ={
        "NEWS_TABLE."+NewsEntry._ID,
        NewsEntry.COLUMN_DATE,
        NewsEntry.COLUMN_TIME,
        NewsEntry.COLUMN_CURRENCY,
        NewsEntry.COLUMN_IMPACT,
        NewsEntry.COLUMN_NAME,
        NewsEntry.COLUMN_ACTUAL,
        NewsEntry.COLUMN_FORECAST,
        NewsEntry.COLUMN_PREVIOUS};
    public boolean loadPage;
    public static String todayDate;
    public static ViewPagerActivity mActivity;
    static boolean showPanel = true;
    static boolean loadTextViews = false;
    static boolean isToday;
    String queryDate;
    public void setLoadPage(boolean b){
        loadPage=b;
    }
    public void setLoadingPanel (boolean b){
        showPanel = b;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        queryDate = getArguments().getString("date");
//        FetchNewsTask newsTask = new FetchNewsTask(this,getActivity());
//        newsTask.execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fxfragment, menu);
    }
//    public void update (){
//        if (loadPage) {
//            String dateUsed = getArguments().getString("date");
//            FetchNewsTask newsTask;
//            newsTask = new FetchNewsTask(this, getActivity(), dateUsed, true,isToday);
//            newsTask.execute();
//            loadPage=false;
//        }
//    }
//    public void refresh (){
//        String dateUsed = getArguments().getString("date");
//        FetchNewsTask newsTask;
//        newsTask = new FetchNewsTask(this, getActivity(), dateUsed, true,isToday);
//        newsTask.execute();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //refresh();


            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getLoaHandler derManager().restartLoader(LOADER_ID,null,this);
        mActivity = (ViewPagerActivity)getActivity();
        String[] dailyNews = {"4:30am GBP Low Italian Bank Holiday", "5:30am GBP Low French Bank Holiday", "8:30 USD High FOMC Meeting", "9:30 USD High Employment Announcement"};
        List<String> newsList = new ArrayList<String>(Arrays.asList(dailyNews));

        mNewsAdapter2 = new NewsCursorAdapter(getActivity(), null,0);


        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);





        Log.v("Querydate is ", queryDate);
        TextView titleTextView =(TextView) rootView.findViewById(R.id.title_textview);
        titleTextView.setText(queryDate);
        Log.v("loadTextViews ", loadTextViews+"");

            final ListView listView = (ListView) rootView.findViewById(R.id.listview_news);
//        FloatingActionButton fab = mActivity.getButton();
//        fab.attachToListView(listView);
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mNewsAdapter2);
        animationAdapter.setAbsListView(listView);
        listView.setAdapter(animationAdapter);





//        if (!showPanel) {
//            View loadingPanel = rootView.findViewById(R.id.loadingPanel);
//            if (loadingPanel!= null){
//                loadingPanel.setVisibility(View.GONE);}
//        }
        Log.v("testrun","adapter has been set"+todayDate);

        //Log.v("Id",mNewsAdapter2.getView(listView)+"");


        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();



        //getLoaderManager().restartLoader(LOADER_ID,null,this);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.v("loader created","yes");
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        mNewsAdapter2.notifyDataSetChanged();
        super.onActivityResult(requestCode,resultCode,data);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = NewsEntry.COLUMN_TIME + " ASC";
        String selection =NewsEntry.COLUMN_DATE+" = ?";
        String [] selectionArgs=null;
        if (queryDate!= null){
            selectionArgs=new String [1];
            selectionArgs[0]=queryDate;
        }
        Uri queryUri = Uri.parse("content://com.yuweixu.fxnews");
        //String [] a={"September 23, 2014"};
        return new CursorLoader(
                getActivity(),
                queryUri,
                columns,
                selection,
                selectionArgs,
                "time ASC"
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter2.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNewsAdapter2.swapCursor(null);
    }
}
