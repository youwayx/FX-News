package com.yuweixu.fxnews.Custom;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuweixu.fxnews.NewsEntry;
import com.yuweixu.fxnews.R;

/**
 * Created by Yuwei on 2014-09-29.
 */
public class NewsCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    public NewsCursorAdapter (Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
         //cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //inflate the view custom xml
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.news_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        /*TextView impactView =(TextView) view.findViewById(R.id.list_item_impact_textview);
        if (cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_IMPACT)).toString().equals("Low"))
            impactView.setBackgroundColor(Color.YELLOW);*/

        return view;

    }
    @Override
    public int getViewTypeCount() {
        return 1;
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
    //get elements of the view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor==null){
            Log.v("Its fine","the cursor's null");
        }
        if (view!= null) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();

            //FontTextView currencyView = (FontTextView) view.findViewById(R.id.list_item_currency_textview);
            viewHolder.currencyView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_CURRENCY)).toString());
            //FontTextView nameView= (FontTextView) view.findViewById(R.id.list_item_name_textview);
            viewHolder.nameView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_NAME)).toString());
            //FontTextView forecastView = (FontTextView) view.findViewById(R.id.list_item_forecast_textview);
            viewHolder.forecastView.setText("Forecast: "+cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_FORECAST)).toString());
            //FontTextView impactView = (FontTextView) view.findViewById(R.id.list_item_impact_textview);
            if (viewHolder.impactView== null){
                Log.v("this is causing", "the null");
            }
            String impact = cursor.getString(4);
            if (impact.equals("Medium")){
                impact = "Med";
            }
            viewHolder.impactView.setText(impact);
            /*if (impact!=null && impact.equals("Low"))
            {   q
                viewHolder.impactView.setBackgroundColor(Color.YELLOW);
            }
            if (impact!= null && impact.equals("Med")){
                viewHolder.impactView.setBackgroundColor(Color.RED);
            }*/




            // FontTextView timeView = (FontTextView) view.findViewById(R.id.list_item_time_textview);
            viewHolder.timeView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_TIME)).toString());
            //FontTextView previousView = (FontTextView) view.findViewById(R.id.list_item_previous_textview);
            viewHolder.previousView.setText("Previous: "+cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_PREVIOUS)).toString());
            //FontTextView actualView = (FontTextView) view.findViewById(R.id.list_item_actual_textview);
            viewHolder.actualView.setText("Actual: "+ cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_ACTUAL)));
        }
    }
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        LayoutInflater inflater;

        if (convertView != null){
            viewHolder=(ViewHolder) convertView.getTag();
            if (viewHolder.impactView.getText().equals ("Low")){
                viewHolder.impactView.setBackgroundColor(Color.YELLOW);
            }
            else{
                viewHolder.impactView.setBackgroundColor(Color.RED);
            }
        }
        return convertView;
    }*/
}
