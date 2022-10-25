package com.brins.commom.base;

import android.view.View;
import android.view.ViewGroup;
import org.greenrobot.eventbus.EventBus;

/**
 * @author lipeilin
 * @date 2022/10/17
 * @desc
 */
public class BaseMvpViewLifecycleDelegate {

    public static void registerEventBus(Class clazz, Object subscriber) {
        if (clazz != null && subscriber != null) {
            try {
                EventBus.getDefault().register(subscriber);
            } catch (Exception e) {
                // do nothing 如果没有EventBus的接收方法，不需要输出异常信息
            }
        }
    }

    public static void unregisterEventBus(Object subscriber) {
        if (subscriber != null) {
            try {
                EventBus.getDefault().unregister(subscriber);
            } catch (Exception e) {
                // do nothing 如果没有EventBus的接收方法，不需要输出异常信息
            }
        }
    }

    public static void onStart(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onStart();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onStart(parent.getChildAt(i));
            }
        }
    }

    public static void onResume(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onResume();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onResume(parent.getChildAt(i));
            }
        }
    }

    public static void onPause(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onPause();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onPause(parent.getChildAt(i));
            }
        }
    }

    public static void onStop(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onStop();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onStop(parent.getChildAt(i));
            }
        }
    }

    public static void onDestroy(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onDestroy();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onDestroy(parent.getChildAt(i));
            }
        }
    }

    public static void onFragmentResume(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onFragmentResume();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onFragmentResume(parent.getChildAt(i));
            }
        }
    }

    public static void onFragmentPause(View view) {
        if (view instanceof IBaseMVPViewLifeCycle) {
            ((IBaseMVPViewLifeCycle) view).onFragmentPause();
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = ((ViewGroup) view);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                onFragmentPause(parent.getChildAt(i));
            }
        }
    }
}
