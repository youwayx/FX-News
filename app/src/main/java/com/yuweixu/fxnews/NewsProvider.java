package com.yuweixu.fxnews;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Yuwei on 2014-08-27.
 */
public class NewsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final String CONTENT_AUTHORITY = "com.yuweixu.fxnews";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "news";
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH;

    public static int NEWS = 100;
    public static int NEWS_WITH_ID = 101;
    private NewsOpenHelper mOpenHelper;
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH, NEWS);
        matcher.addURI(authority, PATH +"/*", NEWS_WITH_ID);

        return matcher;
    }

    public static Uri buildNewsUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsOpenHelper(getContext());
        return false;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor=mOpenHelper.getReadableDatabase().query(NewsOpenHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public String getType(Uri uri) {

        return CONTENT_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.v("HI","insert has been called");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Log.v("HI", "db has been found");
        long id =-1;
        if (db!= null && contentValues != null) {
            id = db.insert(NewsOpenHelper.TABLE_NAME, null, contentValues);
        }
        Log.v("HI", "inserted");
        Uri returnUri;

        if ( id > 0 )
            returnUri = buildNewsUri(id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = db.delete(NewsOpenHelper.TABLE_NAME, selection, selectionArgs);
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated= db.update(NewsOpenHelper.TABLE_NAME,contentValues,selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }
}

