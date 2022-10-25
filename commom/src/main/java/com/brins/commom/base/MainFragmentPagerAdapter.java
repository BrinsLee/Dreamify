package com.brins.commom.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.brins.commom.R;
import com.brins.commom.utils.KGViewUtil;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.commom.widget.CommonAlphaBgImageView;
import com.brins.commom.widget.CommonLoadingView;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import com.kugou.page.core.KGFrameworkFragment;
import com.kugou.page.sub.IGetItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huguoyin on 16-1-16.
 * 首页听看唱滑动切换 专用 PagerAdapter
 */
public abstract class MainFragmentPagerAdapter extends PagerAdapter implements IGetItem {
    protected static final String TAG = "MainFragmentPagerAdapter";
    protected FragmentManager mFragmentManager;
    protected FragmentTransaction mCurTransaction = null;
    protected Object mCurrentPrimaryItem = null;
    protected int mCurrentPosition = 1;
    protected List<Drawable> mDrawableList = new ArrayList<>();

    protected Context mContext;

    /**
     * 听看唱
     */
    protected ArrayList<Object> list = new ArrayList<Object>(2);

    private int[] mTabFragmentIds;

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
        loadTabFragment(mCurrentPosition);
    }


    protected void initFragmentLayouts() {
        FrameLayout container;
        Fragment fragment;

        for (int i = 0; i < mTabFragmentIds.length; i++) {
            fragment = mFragmentManager.findFragmentByTag(makeFragmentTag(mTabFragmentIds[i]));
            if (fragment != null) {
                list.add(fragment);
            } else {
                if (i == mCurrentPosition) {
                    list.add(getItem(i));
                } else {
                    container = new FrameLayout(mContext);
                    container.setId(mTabFragmentIds[i]);
                    list.add(container);
                }
            }
        }

        printLog("initFragmentLayouts");
    }

    void delayInitMineFragment() {
        if (DrLog.isDebug()) {
            return;
        }
        new StackTraceHandler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPosition == MainFragmentContainer.TAB_TING) {
                    loadTabFragment(MainFragmentContainer.TAB_MINE);
                }
            }
        }, 2000);
    }

    protected void printLog(String info) {
        if (DrLog.DEBUG) {
            DrLog.i(TAG, "printLog after function:" + info + "||size:" + list.size());
            for (int i = 0; i < list.size(); ++i) {
                Object obj = list.get(i);
                DrLog.i(TAG, "printLog obj(" + i + "): " + (obj == null ? "null" : obj.getClass().getSimpleName()));
            }
        }
    }

    public MainFragmentPagerAdapter(FragmentManager fm, Context context,int curPosition) {
        this(fm,context, new int[]{
                R.id.home_navigation_tab_ting,
                R.id.home_navigation_tab_mine,
        },curPosition);
    }

    public MainFragmentPagerAdapter(FragmentManager fm, Context context,int[] tabIds,int curPosition) {
        mContext = context;
        mFragmentManager = fm;
        mTabFragmentIds = tabIds;
        mCurrentPosition = curPosition;
        initFragmentLayouts();
    }



    public abstract KGFrameworkFragment getItem(int position);

    public Fragment getFragmentItem(int position){
        if (list != null) {
            Object item = list.get(position);
            if(item instanceof Fragment){
                return (Fragment) item;
            }
        }
        return null;
    }

    public FrameLayout getFrameLayout(int position){
        if (list != null) {
            Object item = list.get(position);
            if(item instanceof FrameLayout){
                return (FrameLayout) item;
            }
        }
        return null;
    }

    public void setTabAlphaWhenLoading(int position,float alpha){
        if(position > getCount()-1){
            return;
        }
        FrameLayout frameLayout = getFrameLayout(position);
        if(frameLayout != null){
            CommonAlphaBgImageView iv = (CommonAlphaBgImageView)frameLayout.findViewById(R.id.comm_navi_top_view1);
            if(mDrawableList.size() <= 0) {
                KGViewUtil.setCommonTabBg(mContext,iv,mDrawableList);
            }
            iv.setDrawableLists(mDrawableList);
            iv.setBgAlpha(alpha);
        }
    }

    public void startUpdate(ViewGroup container) {

    }

    private Map<Integer, LoadingFaceWrrap> mLoadingFaces = new HashMap<>();

    public Object instantiateItem(ViewGroup container, int position) {

        if (DrLog.DEBUG) DrLog.i(TAG, "instantiateItem:" + position + "||Childcount:" + container.getChildCount());


        Object item = list.get(position);

        FrameLayout view;

        if (item instanceof FrameLayout) {
            view = (FrameLayout) item;
            if (view != null && view.getParent() == null) {
                container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (view.getChildCount() == 0) {
                    addLoadingView(view, position);
                }

                try {
                    getLoadingView(view).setVisibility(View.GONE);
                } catch (Exception e) {
                    if (DrLog.DEBUG) DrLog.d(TAG, "instantiateItem:Exception " + view + "||" + e.getMessage());
                }
            }

            return view;
        } else if (item instanceof Fragment) {
            if (this.mCurTransaction == null) {
                this.mCurTransaction = this.mFragmentManager.beginTransaction();
            }
            Fragment fragment = this.mFragmentManager.findFragmentByTag(makeFragmentTag(mTabFragmentIds[position]));
            if (fragment != null) {
                this.mCurTransaction.attach(fragment);
            } else {
                fragment = (Fragment) item;
                this.mCurTransaction.add(container.getId(), fragment, makeFragmentTag(mTabFragmentIds[position]));
            }

            if (fragment != null && mCurrentPosition != position) {
                fragment.setMenuVisibility(false);
                fragment.setUserVisibleHint(false);
            }

            return fragment;

        }

        return item;
    }

    protected int getItemLayoutID(){
        return R.layout.home_navigation_loading_layout;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {

        if (object instanceof FrameLayout) {
            FrameLayout layout = (FrameLayout) object;
            container.removeView(layout);
            mLoadingFaces.remove(Integer.valueOf(position));
        } else {
            //Fragment 不要回收 首页的特殊处理
        }
        if (DrLog.DEBUG) DrLog.i(TAG, "destroyItem:" + position + "|" + object);
    }

    private void addLoadingView(FrameLayout parent, int position) {
        parent.setPadding(0, 0, 0, mContext.getResources()
                .getDimensionPixelOffset(R.dimen.playing_bar_height_without_shadow));

        CommonAlphaBgImageView topAlphView = new CommonAlphaBgImageView(mContext);
        topAlphView.setId(R.id.comm_navi_top_view1);
        FrameLayout.LayoutParams topLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topLp.gravity = Gravity.TOP;
        topAlphView.setVisibility(View.GONE);
        topAlphView.setLayoutParams(topLp);

        CommonLoadingView loadingView = new CommonLoadingView(mContext);
        loadingView.setId(R.id.progress_info);
        loadingView.setVisibility(View.GONE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        loadingView.setLayoutParams(lp);

        parent.addView(topAlphView);
        parent.addView(loadingView);

        mLoadingFaces.put(Integer.valueOf(position), new LoadingFaceWrrap(topAlphView, loadingView));
    }

    private static class LoadingFaceWrrap implements ISkinViewUpdate {
        private CommonAlphaBgImageView mBarBG;
        private CommonLoadingView mLoading;

        public LoadingFaceWrrap(CommonAlphaBgImageView barBG, CommonLoadingView loadingView) {
            mBarBG = barBG;
            mLoading = loadingView;
        }

        @Override
        public void updateSkin() {
            mBarBG.updateSkin();
            mLoading.updateSkin();
        }
    }

    protected View getLoadingView(FrameLayout layout) {
        if (layout == null)
            return null;

        View view = layout.findViewById(R.id.progress_info);

        return view;
    }

    public void onSkinAllChanged() {
        if (mLoadingFaces != null) {
            Collection<LoadingFaceWrrap> loadingFaces = mLoadingFaces.values();
            if (loadingFaces == null || loadingFaces.size() == 0)
                return;
            for (LoadingFaceWrrap wrrap : loadingFaces) {
                if (wrrap != null) {
                    wrrap.updateSkin();
                }
            }
        }
    }

    private void hideCurrentItem(Object item) {

        if (item == null)
            return;

        if (item instanceof Fragment) {
            Fragment fragment = (Fragment) item;
            if (fragment != null) {
                fragment.setMenuVisibility(false);
                fragment.setUserVisibleHint(false);
            }
        } else if (item instanceof FrameLayout) {
            FrameLayout layout = (FrameLayout) item;
            View loadingView = getLoadingView(layout);
            if (loadingView != null) {
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    protected void showCurrentItem(Object item) {
        if (item == null)
            return;

        if (item instanceof Fragment) {
            Fragment fragment = (Fragment) item;
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
        } else if (item instanceof FrameLayout) {
            FrameLayout layout = (FrameLayout) item;
            View loadingView = getLoadingView(layout);
            if (loadingView != null) {
                loadingView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {

        if (object != mCurrentPrimaryItem) {
            hideCurrentItem(mCurrentPrimaryItem);
            showCurrentItem(object);
            mCurrentPosition = position;
            this.mCurrentPrimaryItem = object;
            if (DrLog.DEBUG) DrLog.i(TAG, "setPrimaryItem:" + position);
        }
    }

    private long start = 0; // 只是debug用

    public void finishUpdate(ViewGroup container) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            if (DrLog.isDebug()) {
                start = System.currentTimeMillis();
                if (DrLog.DEBUG) DrLog.e("burone5", ">>>>start executePendingTransactions()");
            }

            /*
             * 该方法的首次调用会引起Fragment（只关注NavigationFragment）的
             * 初始化生命周期，对其耗时的监测有助于查找启动时长变化的原因
             */
            this.mFragmentManager.executePendingTransactions();

            if (DrLog.isDebug()) {
                DrLog.e("burone5", "<<<<finish executePendingTransactions(), " +
                        "cost = " + (System.currentTimeMillis() - start));
            }
        }
    }

    public boolean isViewFromObject(View view, Object object) {

        if (object instanceof Fragment) {
            return ((Fragment) object).getView() == view;
        }
        return object == view;
    }

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public String makeFragmentTag(int viewId) {
        return "android:switcher:" + viewId;
    }

    @Override
    public int getItemPosition(Object object) {
        //return POSITION_NONE; 比较粗暴的写法: 会引起对象destory，然后再instantiate
        int pos = list.indexOf(object);
        if(pos != -1){
            return pos;
        }
        return POSITION_NONE;
    }

    /**
     * remove掉 loading，替换成Fragment
     *
     * @param position
     */
    public void loadTabFragment(int position) {
        Fragment f = getFragmentItem(position);
        if (f == null) {
            Fragment fragment = getItem(position);
            list.remove(position);
            list.add(position, fragment);
            notifyDataSetChanged();
        }

        printLog("loadTabFragment " + position);
    }
}
