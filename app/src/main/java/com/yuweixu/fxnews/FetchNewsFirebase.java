package com.yuweixu.fxnews;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.Firebase;

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
 * Created by Yuwei on 2014-11-02.
 */
public class FetchNewsFirebase extends AsyncTask<Void, Void, Void> {
    Context mContext;
    String queryDate;
    boolean parseNews;
    Firebase newsBase;
    public FetchNewsFirebase(Context context,String queryDate, boolean parseNews){
        this.queryDate= queryDate;
        mContext = context;
        this.parseNews= parseNews;
        Firebase.setAndroidContext(mContext);
        newsBase = new Firebase("https://fx-news.firebaseio.com/news");

    }
    public void addNews (String [] info){
        Map newsValues= new HashMap<String,String>();
        newsValues.put(NewsEntry.COLUMN_DATE, info[1]);
        newsValues.put(NewsEntry.COLUMN_TIME, info[2]);
        newsValues.put(NewsEntry.COLUMN_CURRENCY, info[3]);
        newsValues.put(NewsEntry.COLUMN_IMPACT, info[4]);
        newsValues.put(NewsEntry.COLUMN_NAME, info[5]);
        newsValues.put(NewsEntry.COLUMN_ACTUAL, info[6]);
        newsValues.put(NewsEntry.COLUMN_FORECAST, info[7]);
        newsValues.put(NewsEntry.COLUMN_PREVIOUS, info[8]);
        newsBase.child(queryDate).child(info[0]).setValue(newsValues);
    }
    @Override
    protected Void doInBackground(Void... params) {
        Log.v("HI", "doInBackground started");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String newsStuff = "";
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

                for (int i=0; i<data.size(); i++) {
                    String [] newsInfo = new String [9];
                    newsInfo [0] = i+"";
                    String [] news = data.get(i);
                    for (int j=0; j<8; j++){
                        newsInfo[j+1]= news[j];
                    }
                    addNews(newsInfo);
                }

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
    private ArrayList<String[]> parseNews(String source) {

        //finding date -- bginput flexDatePicker
        ArrayList<String[]> data = new ArrayList<String[]>();
        String key = "td class=\"time\"";
        String endKey = "More";
        int index = 20000;
        int index2 = 2500;
        String[] newsInfo = new String[8];
        boolean flag = false;
        boolean done = false;
        boolean done2= false;
        String date="";
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
                        for (int i=0; i<70; i++){
                            if (source.charAt(index+21+i)=='>'){
                                index=index+21+i;
                                break;
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
                    Log.v("indices",index+" "+index2);
                }

            }

            index++;
        }
        return data;
    }
}
