
package com.brins.commom.delegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import com.brins.commom.activity.AbsBaseFragment;
import com.brins.commom.base.BaseMvpViewLifecycleDelegate;
import com.brins.commom.rxlifecycle.LifecycleProvider;
import com.brins.commom.rxlifecycle.LifecycleTransformer;
import com.brins.commom.rxlifecycle.android.FragmentEvent;
import com.brins.commom.utils.KGAssert;
import com.kugou.common.base.FragmentPersistable;
import com.kugou.page.framework.delegate.SaveInstanceDelegate;
import rx.Observable;

public abstract class DelegateFragment extends AbsBaseFragment implements
    LifecycleProvider<FragmentEvent> {


    private TitleDelegate titleDelegate;
    private RxFragmentLifeDelegate rxLifeDelegate;

    /**
     * 打开标题功能
     *
     * 标题功能事件处理，如果不需要重新实现某些点击事件，可传null
     */
    public void enableTitleDelegate() {
        titleDelegate = new TitleDelegate(this);
    }

    public void enableRxLifeDelegate() {
        if (null == rxLifeDelegate)
            rxLifeDelegate = new RxFragmentLifeDelegate(this);
    }

    public void setExtraTitleDelegate(TitleDelegate titleDelegate) {
        this.titleDelegate = titleDelegate;
    }



    /**
     * 在initDelegates()之前要先调用所有聚合类的设置选项
     */
    public void initDelegates() {
        if (titleDelegate != null) {
            titleDelegate.init();
        }
        if (rxLifeDelegate != null) {
            rxLifeDelegate.init();
        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onFragmentFirstStart() {
        super.onFragmentFirstStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (titleDelegate != null) {
            titleDelegate.onResume();
        }

        if (null != rxLifeDelegate)
            rxLifeDelegate.onResume();

    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (titleDelegate != null && this instanceof FragmentPersistable) {
            titleDelegate.onResume();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onStart();
        BaseMvpViewLifecycleDelegate.onStart(getView());
    }

    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        if (titleDelegate != null) {
            titleDelegate.onSkinColorChanged();
        }
    }

    public void onUpdateSkin() {
        onSkinAllChanged();
    }

    @Override
    protected void onSkinColorChanged() {
        super.onSkinColorChanged();
        if (titleDelegate != null)
            titleDelegate.onSkinColorChanged();

    }

    @Override
    public void startActivity(Intent intent) {

        super.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onScreenStateChanged(int state) {
        super.onScreenStateChanged(state);

        hideSoftInput();
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        hideSoftInput();
        BaseMvpViewLifecycleDelegate.onFragmentPause(getView());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (titleDelegate != null) {
            titleDelegate.onDestroy();
        }
        if (null != rxLifeDelegate) {
            rxLifeDelegate.onDestroyView();
        }

        BaseMvpViewLifecycleDelegate.onDestroy(getView());
    }


    @Override
    protected void startFragmentOnUIThread(Class<? extends Fragment> cls, Bundle args,
                                           boolean anim, boolean replaceMode, boolean clearTop) {
        Bundle bundle;
        if (args == null) {
            bundle = new Bundle();
        } else {
            bundle = new Bundle(args);
        }

        Bundle arguments = getArguments();
        if (arguments == null) {
            arguments = new Bundle();
        }
        String previousKeyIdentifier = arguments.getString(KEY_IDENTIFIER);
        if (previousKeyIdentifier == null) {
            previousKeyIdentifier = "";
        }
        String customKeyIdentifier = arguments.getString(KEY_CUSTOM_IDENTIFIER);
        if (customKeyIdentifier == null) {
            customKeyIdentifier = "";
        }
        String title = getIdentifier();
        boolean appendTitle = arguments.getBoolean(KEY_IDENTIFIER_APPEND_TITLE, true);
        arguments.remove(KEY_IDENTIFIER_APPEND_TITLE);
        super.startFragmentOnUIThread(cls, bundle, anim, replaceMode, clearTop);
    }

    public static final String KEY_IDENTIFIER = "key_identifier";//当前页面identifier = 前一个页面的identifier +

    public static final String KEY_CUSTOM_IDENTIFIER = "key_custom_identifier";//前一个页面的自定义identifier

    public static final String KEY_IDENTIFIER_APPEND_TITLE = "key_identifier_append_title";//前一个页面identifier是否添加title

    public static final String KEY_RESUME_PAGE_CUSTOM_IDENTIFIER = "key_resume_page_custom_identifier";

    public static final String KEY_RESUME_PAGE_IDENTIFIER_APPEND_TITLE = "key_resume_page_identifier_append_title";

    public static final String KEY_USE_AUTO_IDENTIFIER_SOURCE = "key_use_identifier_source";//使用自定义identifier_source

    public static final String KEY_APPEND_SPLIT_IN_CUSTOM_IDENTIFIER = "key_append_split_in_custom_identifier";//自定义时，是否自动加上反斜杠

    public String getIdentifier() {
        if (getArguments() == null || getArguments().getBoolean(KEY_RESUME_PAGE_IDENTIFIER_APPEND_TITLE, true)) {
            if (titleDelegate != null) {
                return titleDelegate.getCommonTitile();
            }
        } else {
            return getArguments().getString(KEY_RESUME_PAGE_CUSTOM_IDENTIFIER);
        }
        return "";
    }

    public void turnToEditMode() {

    }

    public void setExtraParams(Object object){

    }

    public void exitMultiEditMode() {
    }

	// 设置-该fragment被加入到听首页tab中
	private boolean mFromXTingMainFragment = false;
	public void setFromXTingMainFragment() {
		mFromXTingMainFragment = true;
	}
	public boolean isFromXTingMainFragment() {
		return mFromXTingMainFragment;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private RxFragmentLifeDelegate getRxLifeDelegate() {
        if (null != rxLifeDelegate)
            return rxLifeDelegate;
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    @CheckResult
    @NonNull
    public final Observable<FragmentEvent> lifecycle() {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.lifecycle();
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindUntilEvent(FragmentEvent event) {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.bindUntilEvent(event);
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.bindToFragmentLifecycle();
        else KGAssert.fail("");
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (null != rxLifeDelegate)
            rxLifeDelegate.onAttach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != rxLifeDelegate)
            rxLifeDelegate.onCreate();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != rxLifeDelegate)
            rxLifeDelegate.onViewCreated();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != rxLifeDelegate) {
            rxLifeDelegate.onPause();
        }
        BaseMvpViewLifecycleDelegate.onPause(getView());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onStop();
        BaseMvpViewLifecycleDelegate.onStop(getView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onDetach();
    }

    public void requestParentDisableScroll(int value, boolean isBeingDragged) {

    }

/*    public void ensurePlayingBarFooter(KGRecyclerView recyclerView) {
        View playingBarFooter = null;
        if (hasPlayingBar()) {
            if (recyclerView != null) {
                View footer = recyclerView.findViewInFooterArea(R.id.playing_bar_list_footer);
                if (footer == null) {
                    playingBarFooter = createPlayingBarFooter();
                    recyclerView.addFooterView(playingBarFooter);
                }
            }
        }

        ensurePlayListenPartBarFooter(recyclerView);
    }*/

/*    public View createPlayingBarFooter() {
        return getLayoutInflater().inflate(
                R.layout.playing_bar_list_footer, null);
    }*/



/*    protected void showVirtualModelUI(long id, String fo) {
        if (!CommonEnvManager.isLogin()) {
            return;
        }
        enableVirtualModelDelegate();
        //
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getVirtualModelDelegate().showVirtualModel(id, new VirtualModelMovableLayout.DisallowInterceptCallback() {
            @Override
            public void onDisallow(boolean b) {
                if (getParentFragment() instanceof SwipeViewPage.DisallowInterceptCallback) {
                    ((SwipeViewPage.DisallowInterceptCallback) getParentFragment()).requestDisallowInterceptTouchEvent();
                }
            }
        }, params, fo);

    }*/

}
