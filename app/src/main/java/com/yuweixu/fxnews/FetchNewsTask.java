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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Yuwei on 2014-08-30.
 */
public class FetchNewsTask extends AsyncTask<Void, Void, ArrayList<String[]>> {
    Context mContext;
    static String date;
    static boolean isToday,parseNews;
    public String queryDate;
    static FxFragment fragment;
    FetchNewsTask (FxFragment fragment, Activity context,String queryDate,boolean parseNews){
        this.fragment = fragment;
        mContext = context;
        this.queryDate = queryDate;
        this.parseNews=parseNews;
    }

    private void addNews(String [] info){
        if (info[4]!=null) {
            Log.v("Insert:", info[4]);
            String a= NewsEntry.COLUMN_NAME + " = '" +info [4]+ "' and "+NewsEntry.COLUMN_DATE + " = '" + info[0]+ "'";
            Log.v("addNews",a);

            Cursor nameCursor = mContext.getContentResolver().query(
                    NewsProvider.CONTENT_URI,
                    new String[]{NewsEntry._ID},
                    NewsEntry.COLUMN_NAME + " = '" +info [4]+ "' and "+NewsEntry.COLUMN_DATE + " = '" + info[0]+ "' and "+NewsEntry.COLUMN_ACTUAL +" = '"+info[5]+"' and "
                            +NewsEntry.COLUMN_CURRENCY +" = '"+ info[2] + "' and "+ NewsEntry.COLUMN_FORECAST+ " = '" + info[6]+ "' and "+NewsEntry.COLUMN_PREVIOUS+" = '"+info[7]+"'",

                    null,
                    null);

            if (nameCursor.moveToFirst()) {
            }
            else {
                ContentValues newsValues = new ContentValues();
                newsValues.put(NewsEntry.COLUMN_DATE, info[0]);
                newsValues.put(NewsEntry.COLUMN_TIME, info[1]);
                newsValues.put(NewsEntry.COLUMN_CURRENCY, info[2]);
                newsValues.put(NewsEntry.COLUMN_IMPACT, info[3]);
                newsValues.put(NewsEntry.COLUMN_NAME, info[4]);
                newsValues.put(NewsEntry.COLUMN_ACTUAL, info[5]);
                newsValues.put(NewsEntry.COLUMN_FORECAST, info[6]);
                newsValues.put(NewsEntry.COLUMN_PREVIOUS, info[7]);
                if (newsValues != null) {

                    mContext.getContentResolver().insert(Uri.parse("content://" + "com.yuweixu.fxnews"), newsValues);
                }
                Log.v("INSERTED: ", info[4]);



            }
        }
    }
    @Override
    protected ArrayList<String[]> doInBackground(Void... params) {
        Log.v("HI", "doInBackground started");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String newsStuff = "";
        if (!isToday) {

            try {
                //connects to the url
                String formattedQueryDate = Utility.formatDateForWebQuery(queryDate);
                String g = "http://forexfactory.com/index.php?day=" + formattedQueryDate;
                URL url = new URL("http://forexfactory.com/index.php?day=" + formattedQueryDate);
                Log.v("formatted date", formattedQueryDate);
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
                    newsStuff += line;

                }
                if (parseNews) {
                    ArrayList<String[]> data = parseNews(newsStuff);
                    Log.v("DATA SIZE IS", "" + data.size());
                    for (String[] n : data) {
                        Log.v("name", n[4]);
                        addNews(n);
                    }
                    return data;
                }
                return null;
                //                Log.v("FIRST LINES",newsStuff.substring(100));
                //                for (int i=0; i<data.size(); i++){
                //
                //  Log.v("DATA IS", Arrays.toString(data.get(i)));
                //                }
            } catch (IOException e) {
                Log.v("ERROR", "HTTP CONNECTION NOT MADE");
                return null;
            } finally {
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
            }
        }
        return null;
    }

    private ArrayList<String[]> parseNews(String source) {

        //finding date -- bginput flexDatePicker
        ArrayList<String[]> data = new ArrayList<String[]>();
        String key = "td class=\"time\"";
        String endKey = "More";
        int index = 19500;
        int index2 = 2500;
        String[] newsInfo = new String[8];
        boolean flag = false;
        boolean done = false;
        boolean done2= false;
        date="";
        String prevTime = "";
        while (true){
            if (source.substring(index2, index2+38).equals("class=\"bginput flexDatePicker\" value=\"")){
                date = "";
                for (int i=0; i<25; i++){
                    if (source.charAt(index2+38+i)!='\"'){
                        date+=source.charAt(index2+38+i);

                    }
                    else{
                        done2= true;
                        break;
                    }
                }

            }
            index2++;
            if (done2){
                Log.v("Today date:", date);
                break;
            }

        }


         while (true) {
                if (done) {
                Log.v("DONE", "DONEEEEEE");
                break;
            }
            if (!flag) {
                done = false;
                if (source.substring(index, index + 4).equals("more") && data.size() > 0) {
                    done = true;
                    break;
                }
                //if the key is found

                try {
                    if (source.substring(index, index + 15).equals(key)) {
                        flag = true;
                        newsInfo = new String [8];
                        newsInfo[0]=date;

                        String time = "";


                        //finds the time
                        for (int i = index + 16; i < index + 26; i++) {
                            char c = source.charAt(i);
                            if (c != '<') {
                                time += "" + c;
                            } else {
                                break;
                            }
                        }
                        if (time.equals("")) {
                            if (source.substring(index + 16, index + 32).equals("<a name=\"upnext\"")) {
                                time = "*";
                                int timeindex = 0;
                                boolean timestart = false;

                                for (int i = 0; i < 90; i++) {
                                    if (source.substring(index + 32 + i, index + 47 + i).equals("class=\"upnext\">")) {
                                        timestart = true;
                                        timeindex = index + 47 + i;
                                    }
                                }
                                if (timestart == true) {
                                    for (int i = timeindex; i < timeindex + 10; i++) {
                                        char c = source.charAt(i);
                                        if (c != '<') {
                                            time += "" + c;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                index += 20;
                            }
                            else{
                                time = prevTime;
                            }
                        }

                        newsInfo[1] = time;
                        prevTime = time;
                        index += 26;
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    //Log.v("index, length", index+" "+ source.length());
                }
            }
            if (flag) {

                if (source.substring(index, index + 16).equals("class=\"currency\"")) {
                    String currency = "";
                    for (int i = index + 17; i < index + 20; i++) {
                        currency += "" + source.charAt(i);
                    }
                    newsInfo[2] = currency;
                    index += 20;
                }
                if (source.substring(index, index + 27).equals("class=\"impact\"> <span title")) {
                    String impact = "";
                    for (int i = index + 29; i < index + 100; i++) {
                        if (source.charAt(i) != '\"') {
                            impact += source.charAt(i) + "";

                        } else {
                            break;
                        }
                    }
                    if (impact.equals("Low Impact Expected")){
                        impact = "Low";
                    }
                    if (impact.equals("Medium Impact Expected")){
                        impact = "Med";
                    }
                    if (impact.equals("High Impact Expected")){
                        impact = "High";
                    }
                    newsInfo[3] = impact;
                    index += 29;
                }
                if (source.substring(index, index + 20).equals("class=\"event\"><span>")) {
                    String event = "";
                    for (int i = index + 20; i < index + 60; i++) {
                        if (source.charAt(i) == '<') {
                            break;
                        } else {
                            event += "" + source.charAt(i);
                        }
                    }
                    newsInfo[4] = event;
                    index += 20;
                }
                if (source.substring(index, index + 15).equals("class=\"actual\">")) {
                    String actual = "";
                    if (source.charAt(index + 15) == ' ') {
                        int newindex = index + 17;
                        if (source.substring(newindex, newindex + 19).equals("span class=\"worse\">")) {
                            for (int i = newindex + 19; i < newindex + 40; i++) {
                                if (source.charAt(i) == '<') {
                                    break;
                                } else {
                                    actual += "" + source.charAt(i);
                                }
                            }
                        } else if (source.substring(newindex, newindex + 20).equals("span class=\"better\">")) {
                            for (int i = newindex + 20; i < newindex + 40; i++) {
                                if (source.charAt(i) == '<') {
                                    break;
                                } else {
                                    actual += "" + source.charAt(i);
                                }
                            }
                        }
                    } else {
                        for (int i = index + 15; i < index + 20; i++) {
                            if (source.charAt(i) == '<') {
                                break;
                            } else {
                                actual += "" + source.charAt(i);
                            }
                        }
                    }

                    newsInfo[5] = actual;
                }
                if (source.substring(index, index + 17).equals("class=\"forecast\">")) {
                    String forecast = "";
                    for (int i = index + 17; i < index + 40; i++) {
                        if (source.charAt(i) == '<') {
                            break;
                        } else {
                            forecast += "" + source.charAt(i);
                        }
                    }
                    newsInfo[6] = forecast;



                }
                if (source.substring(index, index+21).equals("<td class=\"previous\">")){
                    String previous = "";
                    if (source.charAt(index+21)!='<') {
                        for (int i = 0; i < 15; i++) {
                            if (source.charAt(index+21+i) == '<') {
                                break;
                            } else {
                                previous += "" + source.charAt(index+21+i);
                            }
                        }
                    }
                    else{
                        for (int i=0; i<50; i++){
                            if (source.charAt(index+21+i)=='>'){
                                index=index+21+i;
                            }
                        }
                        for (int j=0; j<10; j++){
                            if (source.charAt(index+1+j)=='<'){
                                break;
                            }
                            else{
                                previous += source.charAt(index+j+1);
                            }
                        }
                    }
                    newsInfo[7]=previous;
                    Log.v("Previous",previous);
                    flag = false;
                    Log.v("DATA:", Arrays.toString(newsInfo));
                    data.add(newsInfo);
                }

            }

            index++;
        }
        return data;
    }
    @Override
    protected void onPostExecute (ArrayList<String[]> a){
        //fragment.setLoadingPanel(false);
        //ListView view = mContext.findViewById(R.id.listview_news);
        mContext.getContentResolver().notifyChange(Uri.parse("content://" + "com.yuweixu.fxnews"),null);

    }
}
