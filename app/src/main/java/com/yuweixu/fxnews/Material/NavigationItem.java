package com.yuweixu.fxnews.Material;

import android.graphics.drawable.Drawable;

/**
 * Created by Yuwei on 2015-01-09.
 */
public class NavigationItem {
    private String mText;
    private Drawable mDrawable;

    public NavigationItem(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
}
