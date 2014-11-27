package com.yuweixu.fxnews.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.yuweixu.fxnews.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuwei on 2014-11-02.
 */
public class NewsArrayAdapter extends ArrayAdapter<Map<String, String>> {
    private int mResource;
    private ArrayList<Map<String,String>> mData;
    private Context mContext;
    private String date;
    private Firebase newsBase;
    public NewsArrayAdapter (Context context,int resource, ArrayList<Map<String,String>> objects,String date){
        super (context, resource, objects);
        mContext=context;
        mResource = resource;
        mData = objects;
        this.date= "November 2, 2014";
        newsBase = new Firebase("https://fx-news.firebaseio.com/news/"+date);
    }

    public View getView (final int position, View convertView, ViewGroup parent){
        Map <String, String> newsInfo = mData.get(position);

        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        return convertView;
    }
    public static class ViewHolder {
        public final TextView impactView;
        public final TextView currencyView;
        public final TextView timeView;
        public final TextView nameView;
        public final TextView forecastView;
        public final TextView actualView;
        public final TextView previousView;

        public ViewHolder(View view) {
            impactView = (TextView) view.findViewById(R.id.list_item_impact_textview);
            currencyView = (TextView) view.findViewById(R.id.list_item_currency_textview);
            timeView = (TextView) view.findViewById(R.id.list_item_time_textview);
            nameView = (TextView) view.findViewById(R.id.list_item_name_textview);
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            actualView = (TextView) view.findViewById(R.id.list_item_actual_textview);
            previousView = (TextView) view.findViewById(R.id.list_item_previous_textview);
        }
    }
}
