package com.yuweixu.fxnews;

import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Yuwei on 2014-08-27.
 */
public class NewsOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "fxnews.db";
    public static final String TABLE_NAME = "NEWS_TABLE";

    public static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    NewsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    NewsEntry.COLUMN_DATE+ " TEXT, "+
                    NewsEntry.COLUMN_TIME+ " TEXT, " +
                    NewsEntry.COLUMN_CURRENCY + " TEXT, " +
                    NewsEntry.COLUMN_IMPACT + " TEXT, " +
                    NewsEntry.COLUMN_NAME  + " TEXT, " +
                    NewsEntry.COLUMN_ACTUAL + " TEXT, " +
                    NewsEntry.COLUMN_FORECAST  + " TEXT, " +
                    NewsEntry.COLUMN_PREVIOUS + " TEXT" +
                    ");";

    NewsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
