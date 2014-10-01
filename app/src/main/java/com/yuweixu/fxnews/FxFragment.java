package com.yuweixu.fxnews;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


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
    MainActivity mainActivity;
    public int LOADER_ID = 0;
    Typeface ralewayFont;
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
    public static String todayDate;
    public FxFragment() {

    }
    @Override
    public void onStart() {
        super.onStart();
        //getActivity().deleteDatabase("fxnews.db");

        FetchNewsTask newsTask = new FetchNewsTask(getActivity());
        newsTask.execute();


    }

    //takes a date in the form of MM dd, yyyy and replaces the month number with its English word
    public String formatDateString (String date){
        int monthNumber = (int) Integer.parseInt(date.substring(0,2));
        String [] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        return months[monthNumber-1]+date.substring(2);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //enables an options menu
        SimpleDateFormat df = new SimpleDateFormat("MM dd, yyyy");
        String rawDate = df.format(new Date());
        todayDate=formatDateString(rawDate);
        //ralewayFont = MainActivity.font;

        //TextView txt = (TextView) findViewById(R.id.title_textview);

        //txt.setTypeface(font);
        setHasOptionsMenu(true);
    }
    public void setTypeFace (TextView textView, Typeface font){
        if(textView != null) {
            textView.setTypeface(font);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fxfragment, menu);
    }
    public void update (){

        FetchNewsTask newsTask = new FetchNewsTask(getActivity());
        newsTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            update();


            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] dailyNews = {"4:30am GBP Low Italian Bank Holiday", "5:30am GBP Low French Bank Holiday", "8:30 USD High FOMC Meeting", "9:30 USD High Employment Announcement"};
        List<String> newsList = new ArrayList<String>(Arrays.asList(dailyNews));

        mNewsAdapter2 = new NewsCursorAdapter(getActivity(), null,0);
        /*mNewsAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_news,
                null,
                // the column names to use to fill the textviews
                new String[]{NewsEntry.COLUMN_TIME,
                        NewsEntry.COLUMN_CURRENCY,
                        NewsEntry.COLUMN_IMPACT,
                        NewsEntry.COLUMN_NAME,
                        NewsEntry.COLUMN_ACTUAL,
                        NewsEntry.COLUMN_FORECAST,
                        NewsEntry.COLUMN_PREVIOUS

                },
                // the textviews to fill with the data pulled from the columns above
                new int[]{R.id.list_item_time_textview,
                        R.id.list_item_currency_textview,
                        R.id.list_item_impact_textview,
                        R.id.list_item_name_textview,
                        R.id.list_item_actual_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_previous_textview
                },
                0
        );*/

        /*mNewsAdapter.setViewBinder (new SimpleCursorAdapter.ViewBinder(){
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                if (columnIndex == cursor.getColumnIndex(NewsEntry.COLUMN_IMPACT)) {

                    String impactValue = cursor.getString(columnIndex);

                    if (((TextView)view).getText().toString().equals("Low")) {
                        ((TextView) view).setBackgroundColor(Color.YELLOW);

                    }
                    return true;
                }



                return false;
            }
        });*/

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //finds the listView
        ListView listView = (ListView) rootView.findViewById(R.id.listview_news);
        listView.setAdapter(mNewsAdapter2);

        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID,null,this);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = NewsEntry.COLUMN_TIME + " ASC";
        String selection ="(DATE = ?)";
        String [] selectionArgs=null;
        if (todayDate!= null){
            selectionArgs=new String [1];
            selectionArgs[0]=todayDate;
            Log.v("sArgs",Arrays.toString(selectionArgs));
        }

        //String [] a={"September 23, 2014"};
        return new CursorLoader(
                getActivity(),
                Uri.parse("content://com.yuweixu.fxnews"),
                        columns,
                        selection,
                        selectionArgs,
                        null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }
}
