 package com.brins.commom.swipetap;

 import android.content.Context;
 import android.support.v4.view.PagerAdapter;
 import android.support.v4.view.ViewConfigurationCompat;
 import android.util.AttributeSet;
 import android.view.MotionEvent;
 import android.view.View;
 import android.view.ViewConfiguration;
 import com.brins.commom.utils.log.DrLog;
 import com.kugou.common.base.ViewPager;
 

 /**
  * Created by taicixu on 2016/1/28.
  *
  * @author taicixu
  */
 public class SwipeViewPage extends ViewPager {
 
     private static final String TAG = "SwipeViewPage";
     private float mDownX, mDownY;
 
     private SwipeCallback mSwipeCallback;
     private DisallowInterceptCallback mDisallowInterceptCallback;
 
     private int mTouchSlop;
     private boolean isDisallowScroll, isDisableVertical, isFixedHeight, isRequestLayoutOnAttachWindow;
 
     public void setDisableVertical(boolean disableVertical) {
         isDisableVertical = disableVertical;
     }
 
     public void setFixedHeight(boolean fixedHeight) {
         isFixedHeight = fixedHeight;
     }
 
     public void setDisallowScroll() {
         isDisallowScroll = true;
     }
 
     public void setEnableScroll() {
         isDisallowScroll = false;
     }
 
     public void setRequestLayoutOnAttachWindow(boolean requestLayoutOnAttachWindow) {
         isRequestLayoutOnAttachWindow = requestLayoutOnAttachWindow;
     }
 
     public boolean isDisallowScroll() {
         return isDisallowScroll;
     }
 
     public SwipeViewPage(Context context, AttributeSet attrs) {
         super(context, attrs);
         isDisallowScroll = false;
         mIsDisableLeftOverscroll = true;
         mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
     }
 
     private String getIdName() {
 //        if (!DrLog.isDebug()) {
 //            return "";
 //        }
 //        String disallowIntercept = "unknow";
 //        try {
 //            int mGroupFlags = (int) ViewGroup.class.getDeclaredField("mGroupFlags").get(this);
 //            disallowIntercept = "" + ((mGroupFlags & 0x80000) != 0);
 //            return getContext().getResources().getResourceEntryName(getId()) + " disallowIntercept " + disallowIntercept + " parentName " + getContext().getResources().getResourceEntryName(((View)getParent()).getId());
 //        } catch (IllegalAccessException e) {
 //            e.printStackTrace();
 //        } catch (NoSuchFieldException e) {
 //            e.printStackTrace();
 //        } catch (Throwable e){
 //            e.printStackTrace();
 //        }
         return "";
     }
 
     protected boolean mIsIntercept;
 
     @Override
     public boolean onTouchEvent(MotionEvent ev) {
         DrLog.i(TAG+" id Name " + getIdName() + " MotionEvent onTouchEvent", ev.toString() + "-------x=" + ev.getX() + ",y=" + ev.getY());
         return super.onTouchEvent(ev);
     }
 
     @Override
     public boolean onInterceptTouchEvent(MotionEvent ev) {
         DrLog.i(TAG+" id Name " + getIdName() + " MotionEvent onInterceptTouchEvent", ev.toString() + "-------x=" + ev.getX() + ",y=" + ev.getY());
         float curX = ev.getX();
         float curY = ev.getY();
         if (!isDisallowScroll) {
             switch (ev.getAction()) {
                 case MotionEvent.ACTION_DOWN:
                     mDownX = ev.getX();
                     mDownY = ev.getY();
                     if (DrLog.DEBUG) DrLog.i(TAG+" MotionEvent", "DOWN-------x=" + ev.getX() + ",y=" + ev.getY());
                     getParent().requestDisallowInterceptTouchEvent(true);
                     mIsIntercept= true;
                     break;
                 case MotionEvent.ACTION_MOVE:
                     float offsetX = curX - mDownX;
                     float offsetY = curY - mDownY;
                     boolean disVScroll = !(isDisableVertical && Math.abs(offsetY) > Math.abs(offsetX));
                     String step = "";
                     if (mSwipeCallback != null && disVScroll) {
                         if (offsetX > mTouchSlop
                                 && !mSwipeCallback.canLeftSwipe()) {
                             getParent().requestDisallowInterceptTouchEvent(false);
                             mIsIntercept= false;
                             step = "1";
                             if (mDisallowInterceptCallback != null){
                                 mDisallowInterceptCallback.requestDisallowInterceptTouchEvent();
                             }
                         } else if (offsetX < -mTouchSlop
                                 && !mSwipeCallback.canRightSwipe()) {
                             getParent().requestDisallowInterceptTouchEvent(false);
                             mIsIntercept= false;
                             step = "2";
                             if (mDisallowInterceptCallback != null){
                                 mDisallowInterceptCallback.requestDisallowInterceptTouchEvent();
                             }
                         } else if (offsetX > mTouchSlop || offsetX < -mTouchSlop) {
                             getParent().requestDisallowInterceptTouchEvent(true);
                             mIsIntercept = true;
                             step = "3";
                         }
                     } else {
                         getParent().requestDisallowInterceptTouchEvent(false);
                         mIsIntercept= false;
                         step = "4";
                     }
                     if (DrLog.DEBUG) DrLog.i(TAG+" MotionEvent", "MOVE-------x=" + ev.getX() + ",y=" + ev.getY() + " disVScroll " +disVScroll+",offsetX = "+offsetX+",step = "+step);
                     break;
                 case MotionEvent.ACTION_CANCEL:
                 case MotionEvent.ACTION_UP:
                     getParent().requestDisallowInterceptTouchEvent(false);
                     mIsIntercept= false;
                     if (DrLog.DEBUG) DrLog.i(TAG+" MotionEvent", "UP-------x=" + ev.getX() + ",y=" + ev.getY()+" action="+ev.getAction());
                     break;
             }
         } else {
            return false;
         }
         return super.onInterceptTouchEvent(ev);
     }
 
     private boolean interceptTouchWhenRequest = false;
 
     public void setInterceptTouchWhenRequest(boolean interceptTouch) {
         this.interceptTouchWhenRequest = interceptTouch;
     }
 
     @Override
     public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
         super.requestDisallowInterceptTouchEvent(disallowIntercept);
         DrLog.i(TAG+" id Name " + getIdName() + " requestDisallowInterceptTouchEvent " + disallowIntercept);
         if (!disallowIntercept && interceptTouchWhenRequest) {
             getParent().requestDisallowInterceptTouchEvent(true);
         }
     }
 
     @Override
     public boolean dispatchTouchEvent(MotionEvent ev) {
         DrLog.i(TAG+" id Name " + getIdName() + " MotionEvent dispatchTouchEvent", ev.toString() + "-------x=" + ev.getX() + ",y=" + ev.getY());
         return super.dispatchTouchEvent(ev);
     }
 
     public void registerSwipeCallback(SwipeCallback swipeCallback) {
         mSwipeCallback = swipeCallback;
     }
 
     public void removeSwipeCallback() {
         mSwipeCallback = null;
     }
 
     public interface SwipeCallback {
         public boolean canLeftSwipe();
 
         public boolean canRightSwipe();
     }
 
     public void registerDisallowInterceptCallback(DisallowInterceptCallback callback){
         this.mDisallowInterceptCallback = callback;
     }
 
     public void removeDisallowInterceptCallback(){
         mDisallowInterceptCallback = null;
     }
 
     public interface DisallowInterceptCallback{
         public void requestDisallowInterceptTouchEvent();
     }
     public void setDisableLayout(boolean b) {
         disEnableLayout = b;
     }
     
     private volatile boolean disEnableLayout = false;
     @Override
     protected void onLayout(boolean changed, int l, int t, int r, int b) {
         if (!disEnableLayout) {
             super.onLayout(changed, l, t, r, b);
         }
         DrLog.i("lxj  item nw swipepage:" + getIdName() + " onLayout isFixedHeight " +isFixedHeight);
     }
 
     @Override
     protected void onAttachedToWindow() {
         super.onAttachedToWindow();
 //        DrLog.i("lxj  item nw swipepage:" + getIdName() + " onAttachedToWindow isFixedHeight " + isFixedHeight + " isRequestLayoutOnAttachWindow "+isRequestLayoutOnAttachWindow);
         //修复在列表中重新滑入屏幕后因this.mFirstLayout = true; 导致的indicator点击无平滑过渡的问题
         if (isRequestLayoutOnAttachWindow) {
             requestLayout();
         }
     }
 
     @Override
     public void setAdapter(PagerAdapter adapter) {
         super.setAdapter(adapter);
         DrLog.i("lxj  item nw swipepage:" + getIdName() + " setAdapter isFixedHeight " +isFixedHeight);
     }
 
     @Override
     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 //        DrLog.i("lxj  item nw swipepage onMeasure " + getIdName() + " isFixedHeight " +isFixedHeight);
         if (!isFixedHeight) {
             super.onMeasure(widthMeasureSpec, heightMeasureSpec);
             return;        }
         int height = 0;
         for (int i = 0; i < getChildCount(); i++) {
             View child = getChildAt(i);
             child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
             int h = child.getMeasuredHeight();
             if (h > height) height = h;
         }
 //        DrLog.i("lxj  item nw swipepage onMeasure " + getIdName() + " 1 height" + height + " getChildCount:" + getChildCount());
 
         heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
 
         super.onMeasure(widthMeasureSpec, heightMeasureSpec);
     }
 }
