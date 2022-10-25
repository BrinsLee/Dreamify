package com.brins.commom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.Locale;

/**
 * Created by Administrator on 2017/8/11.
 */

public class KgListView extends ListView {

    public KgListView(Context context) {
        super(context);

    }

    public KgListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public KgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public KgListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

/*    public void initScrollListener() {
        super.setOnScrollListener(new OnKgScrollListener());
    }

    OnKgScrollListener kgScrollListener;*/

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        /*if (l == null) {
            kgScrollListener = null;
        }

        kgScrollListener = new OnKgScrollListener(l);
        super.setOnScrollListener(kgScrollListener);*/
    }

    public void onDestroy() {
        /*if (kgScrollListener != null) {
            kgScrollListener = null;
        }*/
    }


    @Override
    public void addFooterView(View v) {
        if(v != null){
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if(params != null && !(params instanceof LayoutParams)){
                ViewGroup.LayoutParams layoutParams = generateLayoutParams(params);
                v.setLayoutParams(layoutParams);
            }
        }
        super.addFooterView(v);
    }
}
