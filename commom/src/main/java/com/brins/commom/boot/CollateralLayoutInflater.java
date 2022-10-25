package com.brins.commom.boot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.kugou.skinlib.engine.KGSkinFactory;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by burone on 2017/5/24.
 */

public class CollateralLayoutInflater extends LayoutInflater {

    private static final Map<Context, CollateralLayoutInflater> sInflaters = new WeakHashMap<>();

    public static CollateralLayoutInflater from(Context context) {
        CollateralLayoutInflater inflater;
        synchronized (sInflaters) {
            inflater = sInflaters.get(context);
            if (inflater == null) {
                inflater = new CollateralLayoutInflater(context);
                sInflaters.put(context, inflater);
            }
        }
        return inflater;
    }


    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "com.tencent.smtt.sdk.",
            "android.app."
    };

    private CollateralLayoutInflater(Context context) {
        super(context);
        if (getFactory() == null) {
            KGSkinFactory factory = new KGSkinFactory() {
                @Override
                protected LayoutInflater getInflater(Context context) {
                    return CollateralLayoutInflater.this;
                }
            };
            setFactory(factory);
        }
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new CollateralLayoutInflater(newContext);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    public KGSkinFactory getSkinFactory() {
        return (KGSkinFactory) getFactory();
    }

    public View inflate(int res) {
        return inflate(res, null);
    }

}
