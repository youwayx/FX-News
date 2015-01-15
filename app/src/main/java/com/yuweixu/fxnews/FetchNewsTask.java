package com.yuweixu.fxnews;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuwei on 2014-08-30.
 */
public class FetchNewsTask extends AsyncTask<Void, Void, String[]> {
    Context mContext;
    static String date;
    static boolean isToday,parseNews;
    public String queryDate;
    static FxFragment fragment;
    ViewPagerActivity mActivity;
    FetchNewsTask( ViewPagerActivity context){
        mActivity = context; mContext = context;
    }
    FetchNewsTask(FxFragment f, Activity context){
        fragment=f;
        mContext=context;
    }
    FetchNewsTask (FxFragment fragment, Activity context,String queryDate,boolean parseNews,boolean isToday){
        this.fragment = fragment;
        mContext = context;
        this.queryDate = queryDate;
        this.parseNews=parseNews;
        this.isToday = isToday;

    }

    private String[] addNews(String data) throws JSONException{
        JSONArray newsJson = new JSONObject(data).getJSONObject("results").getJSONArray("collection1");
        ArrayList<String []> allNews = new ArrayList<String []>();
        String date ="";
        String [] dates = new String[6];
        int counter =0;
        for (int i=0; i<newsJson.length(); i++) {
            JSONObject event = newsJson.getJSONObject(i);
            String[] eventData = new String[8];
            String name = event.getString("name");
            eventData[2] = name.substring(0, 3);
           String eventName= name.substring(4).replace("'","");
            if (eventName.charAt(0)==' '){
                eventName = name.substring(1);

            }
            eventData[4]= eventName;
            String queryDate = event.getString("date");
            Log.v("DATE IS",queryDate);
            if (queryDate.length() > 1){
                char queryDateChar = queryDate.charAt(0);
               // if (queryDateChar == 'S' || queryDateChar == 'M' || queryDateChar == 'T' || queryDateChar == 'W' || queryDateChar == 'F') {
                    date= queryDate.substring(0, 3) + " " + queryDate.substring(4);

                    dates[counter] = date;
                    counter++;
               // }
            }
            eventData[0]=date;
            eventData[1]= event.getString("time");
            eventData[3]= event.getString("impact");
            String act =  event.getString("actual");
            if (act.equals("")){
                act = "- -";
            }
            String fore =  event.getString("forecast");
            if (fore.equals("")){
                fore = "- -";
            }
            String prev =  event.getString("previous");
            if (prev.equals("")){
                prev = "- -";
            }
            eventData[5]=act;
            eventData[6] =fore;
            eventData[7] = prev;
            Log.v("eventData",Arrays.toString(eventData));

            if (eventData[4]!=null) {
                Log.v("Insert:", eventData[4]);
                String a = NewsEntry.COLUMN_NAME + " = '" + eventData[4] + "' and " + NewsEntry.COLUMN_DATE + " = '" + eventData[0] + "'";
                Log.v("addNews", a);
                Cursor testCursor = mContext.getContentResolver().query(
                        NewsProvider.CONTENT_URI,
                        new String[]{NewsEntry._ID},
                        NewsEntry.COLUMN_NAME + " = '" + eventData[4] + "' and " + NewsEntry.COLUMN_DATE + " = '" + eventData[0] + "' and "
                                + NewsEntry.COLUMN_CURRENCY + " = '" + eventData[2] + "'",
                        null,
                        null);
                if (!testCursor.moveToFirst()) {
                    ContentValues newsValues = new ContentValues();
                    newsValues.put(NewsEntry.COLUMN_DATE, eventData[0]);
                    newsValues.put(NewsEntry.COLUMN_TIME, eventData[1]);
                    newsValues.put(NewsEntry.COLUMN_CURRENCY, eventData[2]);
                    newsValues.put(NewsEntry.COLUMN_IMPACT, eventData[3]);
                    newsValues.put(NewsEntry.COLUMN_NAME, eventData[4]);
                    newsValues.put(NewsEntry.COLUMN_ACTUAL, eventData[5]);
                    newsValues.put(NewsEntry.COLUMN_FORECAST, eventData[6]);
                    newsValues.put(NewsEntry.COLUMN_PREVIOUS, eventData[7]);
                    if (newsValues != null) {

                        mContext.getContentResolver().insert(Uri.parse("content://" + "com.yuweixu.fxnews"), newsValues);
                    }
                    Log.v("INSERTED: ", eventData[4]);
                } else {
                    Cursor nameCursor = null;
                    if (testCursor.moveToFirst()) {
                        nameCursor = mContext.getContentResolver().query(
                                NewsProvider.CONTENT_URI,
                                new String[]{NewsEntry._ID},
                                NewsEntry.COLUMN_NAME + " = '" + eventData[4] + "' and " + NewsEntry.COLUMN_DATE + " = '" + eventData[0] + "' and " + NewsEntry.COLUMN_ACTUAL + " = '" + eventData[5] + "' and "
                                        + NewsEntry.COLUMN_CURRENCY + " = '" + eventData[2] + "' and " + NewsEntry.COLUMN_FORECAST + " = '" + eventData[6] + "' and " + NewsEntry.COLUMN_PREVIOUS + " = '" + eventData[7] + "'",
                                null,
                                null);
                    }
                    if (!nameCursor.moveToFirst()) {
                        ContentValues newsValues = new ContentValues();
                        newsValues.put(NewsEntry.COLUMN_DATE, eventData[0]);
                        newsValues.put(NewsEntry.COLUMN_TIME, eventData[1]);
                        newsValues.put(NewsEntry.COLUMN_CURRENCY, eventData[2]);
                        newsValues.put(NewsEntry.COLUMN_IMPACT, eventData[3]);
                        newsValues.put(NewsEntry.COLUMN_NAME, eventData[4]);
                        newsValues.put(NewsEntry.COLUMN_ACTUAL, eventData[5]);
                        newsValues.put(NewsEntry.COLUMN_FORECAST, eventData[6]);
                        newsValues.put(NewsEntry.COLUMN_PREVIOUS, eventData[7]);
                        if (newsValues != null) {

                            mContext.getContentResolver().update(
                                    NewsProvider.CONTENT_URI,
                                    newsValues,
                                    NewsEntry.COLUMN_NAME + " = '" + eventData[4] + "' and " + NewsEntry.COLUMN_DATE + " = '" + eventData[0] + "' and "
                                            + NewsEntry.COLUMN_CURRENCY + " = '" + eventData[2] + "'",
                                    null);
                        }
                    }
                }
            }

        }
        return dates;
    }
    @Override
    protected String [] doInBackground(Void... params) {
        Log.v("HI", "doInBackground started");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String newsStuff = "";
        String [] dates;
        try {
            //connects to the url
            //String formattedQueryDate = Utility.formatDateForWebQuery(queryDate);

            URL url = new URL("https://www.kimonolabs.com/api/d2g7hoj2?apikey=LvMGm0fN7txzr9pjTFy9lz7dHNlQwS4l");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            //reads the page source and stores it in a long string
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");

            }
            try {
                dates = addNews(buffer.toString());
                Log.v("DATES ARE HERE", Arrays.toString(dates));
                return dates;
            }
            catch(JSONException e){}
            }
        catch (IOException e) {
                Log.v("ERROR", "HTTP CONNECTION NOT MADE");
                return null;
            }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ERROR", "Error closing stream", e);
                }
            }
        Log.v("finally","returning null");
        }
        return  null;
    }

    @Override
    protected void onPostExecute (String[] a){
        mActivity.dates = a;
        mActivity.setAdapter();
        mActivity.stopLoading();
        mActivity.setFragmentsVisible();
        Log.v("dates are",Arrays.toString(a));
    }
}
