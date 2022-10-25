package com.brins.commom.widget.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import com.brins.commom.R;
import com.brins.commom.utils.FileUtil;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author jamywang
 * @since 2018/6/12 10:40
 */

public class AccessibilityHelper {
    public static final String ACCESSIBILITY_TYPE_TALKBACK = "talkback";
    public static final String ACCESSIBILITY_TYPE_BYYT = "保益悦听";
    public static final String ACCESSIBILITY_TYPE_DM = "点明";
    public static final String ACCESSIBILITY_TYPE_JS = "解说";

    public static final String ACCESSIBILITY_PACKAGE_NAME_BYYT = "com.bjbyhd.voiceback";
    public static final String ACCESSIBILITY_PACKAGE_NAME_DM = "com.dianming.phoneapp";
    public static final String ACCESSIBILITY_PACKAGE_NAME_JS = "com.nirenr.talkman";

    HashMap<String, AccessibilityImgEntity> mEntityHashMap = new HashMap<>();

    private boolean mDataLoad = false;

    private String mAccessibilityType;

    private List<AccessibilityViewEntity> mWaitingViewList;

    private UIHandler mUIHandler;

    private static AccessibilityHelper mInstance;

    public static AccessibilityHelper getInstance() {
        if (mInstance == null) {
            synchronized (AccessibilityHelper.class) {
                mInstance = new AccessibilityHelper();
            }
        }
        return mInstance;
    }

    private AccessibilityHelper() {
        mUIHandler = new UIHandler(this, Looper.getMainLooper());
        mWaitingViewList = new ArrayList<>();
    }

//    final int[] a = new int[10] {
//        R.drawable.svg_kg_common_btn_download,
//    }


    private void onDescDataGet() {
        mUIHandler.obtainMessage(UIHandler.SET_WAITING_VIEW_LIST, mWaitingViewList)
                .sendToTarget();
    }

    public String getContentDescBySrcName(int srcId, String remark, ImageView view) {
        if (mDataLoad) {
            if (remark == null) remark = "";
            String key = srcId + remark;
            if (mEntityHashMap.containsKey(key)) {
                return mEntityHashMap.get(key).getContentDesc();
            } else {
                return "";
            }
        } else {
            AccessibilityViewEntity viewEntity = new AccessibilityViewEntity();
            viewEntity.setSrcId(srcId);
            viewEntity.setRemark(remark);
            viewEntity.setView(view);
            mWaitingViewList.add(viewEntity);
        }
        return "";
    }

    public String getContentDescBySrcName(int srcId, String remark) {
        if (remark == null) remark = "";
        String key = srcId + remark;
        if (mEntityHashMap.containsKey(key)) {
            return mEntityHashMap.get(key).getContentDesc();
        } else {
            return "";
        }
    }

    public String buildKey(AccessibilityImgEntity entity) {
        if (entity != null) {
            return entity.getImgId() + entity.getRemark();
        }
        return "";
    }

/*    private int getResId(String imgName) {
        if (!TextUtils.isEmpty(imgName)) {
            try {
//                Class c = Class.forName("com.kugou.common.accessibility.AccessibilityResId");

                Field[] f = AccessibilityResId.class.getFields();
                for (Field field : f) {
                    if (field.getName().equals(imgName)) {
                        return field.getInt(AccessibilityResId.class);
                    }
                }
            } catch (Exception e) {
                com.kugou.common.utils.KGLog.uploadException(e);
            }
        }
        return 0;
    }*/

    public boolean isAccessibilityEnable(Context context) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        if (runningServices == null){
            return false;
        }
        for (AccessibilityServiceInfo service : runningServices) {
            String serverName = service.getId();
            if (serverName != null && !TextUtils.isEmpty(serverName)) {
                String[] serverNamePair = serverName.split("/");
                if (serverNamePair != null && serverNamePair.length > 0){
                    String packageName = serverNamePair[0];
                    if (!TextUtils.isEmpty(packageName)) {
                        if (packageName.endsWith(".talkback")) {
                            mAccessibilityType = ACCESSIBILITY_TYPE_TALKBACK;
                            return true;
                        }
                        if (packageName.equals(ACCESSIBILITY_PACKAGE_NAME_BYYT)) {
                            mAccessibilityType = ACCESSIBILITY_TYPE_BYYT;
                            return true;
                        }
                        if (packageName.equals(ACCESSIBILITY_PACKAGE_NAME_DM)) {
                            mAccessibilityType = ACCESSIBILITY_TYPE_DM;
                            return true;
                        }
                        if (packageName.equals(ACCESSIBILITY_PACKAGE_NAME_JS)) {
                            mAccessibilityType = ACCESSIBILITY_TYPE_JS;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private static class UIHandler extends StackTraceHandler {
        private WeakReference<AccessibilityHelper> helperRef;


        public UIHandler(AccessibilityHelper helper, Looper looper) {
            super(looper);
            helperRef = new WeakReference<>(helper);
        }

        private final static int SET_WAITING_VIEW_LIST = 0;

        @Override
        public void handleMessage(Message msg) {
            AccessibilityHelper helper = helperRef.get();
            if (helper == null) return;
            switch (msg.what) {
                case SET_WAITING_VIEW_LIST:
                    Iterator<AccessibilityViewEntity> it = helper.mWaitingViewList.iterator();
                    while (it.hasNext()) {
                        AccessibilityViewEntity viewEntity = it.next();
                        if (viewEntity == null) continue;
                        ImageView imageView = viewEntity.getView();
                        if (imageView != null && ViewCompat.isAttachedToWindow(imageView)) {
                            imageView.setContentDescription(helper.getContentDescBySrcName(viewEntity.getSrcId(), viewEntity.getRemark()));
                        }
                        it.remove();
                    }
                    break;
            }
        }
    }
}
