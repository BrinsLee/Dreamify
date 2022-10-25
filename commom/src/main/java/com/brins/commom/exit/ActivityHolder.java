
package com.brins.commom.exit;

import android.app.Activity;
import com.brins.commom.page.framework.StateFragmentActivity;
import com.brins.commom.utils.log.DrLog;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Burone on 2015/11/24.
 */
public class ActivityHolder {
    public final String TAG = "ActivityHolder";

    private static ActivityHolder mInstance;

    private Set<ActSofRef> actSofRefs;

    private Set<Integer> notifyReceiver;

    private boolean isFinishCall = false;

    private OnAllActivityFinishedListener listener;


    public interface OnAllActivityFinishedListener {
        void onAllActivityFinished();
    }

    public void setOnAllActivityFinishedListener(OnAllActivityFinishedListener l) {
        listener = l;
    }

    public ActivityHolder() {
        actSofRefs = new CopyOnWriteArraySet<>();

        notifyReceiver = new HashSet<>();
        activityHasStart = new LinkedList<>();
    }

    public static ActivityHolder getInstance() {
        if (mInstance == null) {
            synchronized (ActivityHolder.class) {
                mInstance = new ActivityHolder();
            }
        }
        return mInstance;
    }

    public void add(Activity activity) {
        if (DrLog.DEBUG) DrLog.i(TAG, "add0 an Activity: " + activity);
        if (actSofRefs != null) {
            actSofRefs.add(new ActSofRef(activity));
        }
        if (activityHasStart != null) {
            activityHasStart.add(new WeakReference<Activity>(activity));
        }
    }

    public void remove(Activity activity) {
        if (activity == null)
            return;
        if (actSofRefs != null) {
            boolean b = actSofRefs.remove(new ActSofRef(activity));
            if (DrLog.DEBUG) DrLog.i(TAG, "remove an Activity: " + activity + (b ? " success" : " fail"));
        }
        if (isFinishCall) {
            notifyFinished(activity);
        }
    }

    /**
     * finish持有的所有Activity，会依次调用{@link StateFragmentActivity}的
     * notifyAppExiting()和finish()方法。
     * <p>
     * 故只应在软件退出时调用
     */
    public void finishAllOnExit() {
        if (actSofRefs != null && actSofRefs.size() > 0) {
            isFinishCall = true;
            for (ActSofRef srf : actSofRefs) {
                if (srf != null) {
                    Activity act = srf.get();
                    if (act != null && !act.isFinishing()) {
                        notifyReceiver.add(act.hashCode());
                        if (act instanceof StateFragmentActivity) {
                            ((StateFragmentActivity) act).notifyAppExiting();
                        }
                        act.finish();
                    }
                }
            }
        }
    }

    /**
     * 获取当前可见的Activity实例
     * @return
     */
    public Activity getCurrentActivity() {
        if (actSofRefs != null && actSofRefs.size() > 0) {
            ArrayList<ActSofRef> arrayList = new ArrayList<>(actSofRefs);
            ActSofRef actSofRef = arrayList.get(arrayList.size() - 1);
            if (actSofRef != null && actSofRef.get() != null) {
                return actSofRef.get();
            }
        }

        return null;
    }

    /**
     * 告知参数所指Activity已销毁（生命周期走到onDestroy）。
     * <p>
     * 如果不是ActivityHolder调用的finish，则不作处理
     * 
     * @param activity
     */
    private void notifyFinished(Activity activity) {
        if (activity == null)
            return;
        if (hasBeNotifyed(activity)) {
            notifyReceiver.remove(activity.hashCode());
            if (notifyReceiver.size() <= 0) {
                isFinishCall = false;
                actSofRefs.clear();
                notifyAllActivityFinished();
            }
        }
    }

    /**
     * 判断该Activity是否由ActivityHolder调用销毁
     * 
     * @param activity
     * @return
     */
    private boolean hasBeNotifyed(Activity activity) {
        return notifyReceiver.contains(activity.hashCode());
    }

    private void notifyAllActivityFinished() {
        if (DrLog.DEBUG) DrLog.i(TAG, "all activity finished");
        if (listener != null) {
            listener.onAllActivityFinished();
        }
    }

    class ActSofRef extends SoftReference<Activity> {
        private final int hashCode;

        public ActSofRef(Activity activity) {
            super(activity);
            hashCode = activity.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return hashCode == o.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
    private List<WeakReference<Activity>> activityHasStart;

    public List<WeakReference<Activity>> getActivitiesHasStart() {
        return activityHasStart;
    }
}
