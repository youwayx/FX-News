package com.yuweixu.fxnews;

import android.os.Bundle;
import android.app.Fragment;

/**
 * Created by Yuwei on 2014-10-06.
 */
public class SlidePageFragment extends Fragment {
    static String ARG_PAGE = "page";
    public static SlidePageFragment create(int pageNumber) {
        SlidePageFragment fragment = new SlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public SlidePageFragment(){
       //new FxFragment();
    }

}
