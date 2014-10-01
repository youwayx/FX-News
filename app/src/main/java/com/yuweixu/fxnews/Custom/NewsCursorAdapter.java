package com.yuweixu.fxnews.Custom;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
         cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //inflate the view custom xml
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;

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
       /*ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.impactView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_IMPACT)));*/
        /*if (cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_IMPACT)).equals("Low")){
            viewHolder.impactView.setBackgroundColor(Color.YELLOW);
        }*/
        /*viewHolder.currencyView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_CURRENCY)));
        viewHolder.timeView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_TIME)));
        viewHolder.nameView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_NAME)));
        viewHolder.forecastView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_FORECAST)));
        viewHolder.actualView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_ACTUAL)));
        viewHolder.previousView.setText(cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_PREVIOUS)));
        */
    }
}
