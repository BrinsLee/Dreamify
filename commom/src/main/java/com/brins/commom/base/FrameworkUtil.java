package com.brins.commom.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.app.TaskType;
import com.brins.commom.utils.log.DrLog;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import rx.functions.Action1;

public class FrameworkUtil {

    private final static String TG = "FrameworkUtil::";

    static WeakReference<ViewPagerFrameworkDelegate> ref;

    static HashMap<Integer,WeakReference> delegateHashMap = new HashMap<>();

    static int currentStack = TaskType.TYPE_MAIN;

    public static void setCurrentStack(int currentStack) {
        FrameworkUtil.currentStack = currentStack;
    }

    static void setDelegate(ViewPagerFrameworkDelegate d) {
        delegateHashMap.put(currentStack, new WeakReference<ViewPagerFrameworkDelegate>(d));
    }

    static WeakReference<ViewPagerFrameworkDelegate> getWeakReference(int type_stack){
        return delegateHashMap.get(type_stack);
    }

    public static ViewPagerFrameworkDelegate getViewPagerFrameworkDelegate(int type_stack){
        ref = getWeakReference(type_stack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            return d;
        }
        return null;
    }

    public static ViewPagerFrameworkDelegate getCurrentViewPagerFrameworkDelegate(){
        ref = getWeakReference(currentStack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            return d;
        }
        return null;
    }

    public static void release(int typeStack){
        delegateHashMap.remove(typeStack);
        setCurrentStack(TaskType.TYPE_MAIN);
    }

    public static boolean isMainCurrentStack(){
        return currentStack == TaskType.TYPE_MAIN;
    }

    public static int getCurrentStack(){
        return currentStack;
    }



    private final static  String IN_ACTION = "fragment_act";

    private final static String IN_FRAGMENT_CLASS = "fragment_class";

    private final static String IN_BUNDLE = "fragment_bundle";

    private final static String IN_ENABLE_ANIM = "enable_anim";

    private final static String IN_IS_CLEAR_TOP = "is_clear_top";

    public static boolean onMediaActIntent(Intent in, ViewPagerFrameworkDelegate delegate){
        if(in == null || delegate == null){
            if (DrLog.DEBUG) DrLog.e(TG, "onMediaActIntent " + in + ", " + delegate);
            return false;
        }
        String act = in.getAction();
        if(IN_ACTION.equals(act)){
            String cls = in.getStringExtra(IN_FRAGMENT_CLASS);
            if(TextUtils.isEmpty(cls)){
                if (DrLog.DEBUG) DrLog.e(TG, "onMediaActIntent cls is empty");
                return false;
            }
            try {
                Class<?extends AbsFrameworkFragment> fg = (Class<? extends AbsFrameworkFragment>) Class.forName(cls);
                if(fg != null) {
                    Bundle args = in.getBundleExtra(IN_BUNDLE);
                    boolean anim = in.getBooleanExtra(IN_ENABLE_ANIM, true);
                    boolean clearTop = in.getBooleanExtra(IN_IS_CLEAR_TOP, true);
                    delegate.startFragmentOnMainThread(null, fg, args, anim, false, clearTop);
                }
            } catch (ClassNotFoundException e) {
            }
            return true;
        }
        return false;
    }

    public static void finishCurrentFragment() {
        ref = getWeakReference(currentStack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                d.finishCurrentFragmentOnMainThread();
            }
        }
    }

    public static AbsFrameworkFragment getCurrentFragment() {
        ref = getWeakReference(currentStack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                try {
                    return d.getCurrentFragment();
                } catch (Exception e) { // a0c304dde820fafed24b976779d7aa6e 在UI框架还没有初始化的时候调用，就会出现崩溃问题
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static AbsFrameworkFragment getLastFragment() {
        ref = getWeakReference(currentStack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                if (DrLog.DEBUG) DrLog.d("zhpu_fragment", "last : " + d.getCurrentFragment().getClass().getSimpleName());
                return d.getLastFragment();
            }
        }
        return null;
    }

    public static FragmentStackInfo genFragmentStackInfo() {
        ref = getWeakReference(currentStack);
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                return d.genFragmentStackInfo();
            }
        }
        return null;
    }


    public static void startFragment(Class<? extends Fragment> cls, Bundle args,
                                     boolean anim, boolean replaceMode,boolean clearTop){
        startFragment(null, cls, args, anim, replaceMode, clearTop);
    }

    public static void startFragment(AbsFrameworkFragment target, Class<? extends Fragment> cls, Bundle args,
                                     boolean anim, boolean replaceMode,boolean clearTop) {
        ref = getWeakReference(currentStack);
        //1)通过ViewPagerFrameworkDelegate启动fragment
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                d.startFragmentOnMainThread(target, cls, args, anim, replaceMode, clearTop);
                return;
            }
        }
        //2)通过ViewPagerFrameworkDelegate启动失败，通过Intent来启动
        Context c = DRCommonApplication.getContext();
        Intent in = new Intent();
        in.setClassName(c, "com.kugou.android.app.MediaActivity");
        in.putExtra(IN_FRAGMENT_CLASS, cls.getName());
        in.setAction(IN_ACTION);
        in.putExtra(IN_BUNDLE, args);
        in.putExtra(IN_ENABLE_ANIM, true);
        in.putExtra(IN_IS_CLEAR_TOP, false);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(in);
    }

    /**
     * 用于音效评论跳转到公共评论举报页
     */
    public static void startFragment(Context context, Class<? extends Fragment> cls, Bundle args,
                                     boolean anim, boolean replaceMode,boolean clearTop, boolean flagClearTop){
        Intent in = new Intent();
        in.setClassName(context, "com.kugou.android.app.MediaActivity");
        in.putExtra(IN_FRAGMENT_CLASS, cls.getName());
        in.setAction(IN_ACTION);
        in.putExtra(IN_BUNDLE, args);
        in.putExtra(IN_ENABLE_ANIM, true);
        in.putExtra(IN_IS_CLEAR_TOP, false);
        in.setFlags(!flagClearTop ? Intent.FLAG_ACTIVITY_NEW_TASK : Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(in);
    }


	public static void startFragmentToTop(Class<? extends Fragment> cls,
			Bundle args, boolean anim, boolean replaceMode, boolean clearTop) {
		Context c = DRCommonApplication.getContext();
		Intent in = new Intent();
		in.setClassName(c, "com.kugou.android.app.MediaActivity");
		in.putExtra(IN_FRAGMENT_CLASS, cls.getName());
		in.setAction(IN_ACTION);
		in.putExtra(IN_BUNDLE, args);
		in.putExtra(IN_ENABLE_ANIM, anim);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		c.startActivity(in);
	}


    public static void startFragment(Class<? extends Fragment> cls, Bundle args) {
        startFragment(cls, args, true, false, false);
    }

    public static void startFragment(Class<? extends Fragment> cls, Bundle args, boolean anim) {
        startFragment(cls, args, anim, false, false);
    }

    public static void startFragmentFromRecent(Class<? extends Fragment> cls, Bundle args) {
        startFragment(cls, args, true, false, true);
    }

    public static void startFragmentFromRecent(Class<? extends Fragment> cls, Bundle args, boolean anim) {
        startFragment(cls, args, anim, false, true);
    }


    /**
     * @param sourceType
     *      <p>AbsFrameworkFragment.SOURCETYPE_UNKNOW = 0x00;
     *      <p>AbsFrameworkFragment.SOURCETYPE_TING = 0x01;
     *      <p>AbsFrameworkFragment.SOURCETYPE_KAN = 0x02;
     *      <p>AbsFrameworkFragment.SOURCETYPE_CHANG = 0x03;
     *      <p>AbsFrameworkFragment.SOURCETYPE_WAN = 0x04;
     * @param switchCurrentFgTab
     * @param anim
     */
    public static void startMulitPlatformUserInfoFragment(int sourceType, boolean switchCurrentFgTab, boolean anim, boolean replaceMode, boolean clearTop) {
        try {
            Bundle args = new Bundle();
            if (switchCurrentFgTab) {
                args.putInt("current_fragment_type", sourceType);
            }
            Class<? extends Fragment> cls = (Class<? extends Fragment>) Class.forName("com.kugou.android.userCenter.UserInfosMainFragment");
            startFragment(cls, args, anim, replaceMode, clearTop);
        } catch (Exception e) {

        }
    }

    public static void startMulitPlatformUserInfoFragment(int sourceType, boolean switchCurrentFgTab, boolean anim, boolean clearTop) {
        startMulitPlatformUserInfoFragment(sourceType, switchCurrentFgTab, anim, false, clearTop);
    }

    public static void startMulitPlatformUserInfoFragment(int sourceType, boolean switchCurrentFgTab, boolean anim) {
        startMulitPlatformUserInfoFragment(sourceType, switchCurrentFgTab, anim, false, true);
    }


    /**
     * 判断切换的歌手页
     * 跳转新的个人中心 新增统一调用方法
     *
     * @param bundle
     */
    public static void startGuestUserCenterFragment(final Bundle bundle) {
        try {
            startGuestUserCenterFragmentToTop(bundle, true, false, true);
        } catch (Exception e) {
        }
    }

    public static void startGuestUserCenterFragment(Bundle bundle, boolean anim, boolean replaceMode, boolean clearTop) {
        startGuestUserCenterFragment(bundle, anim, replaceMode, clearTop, false);
    }

    public static void startGuestUserCenterFragmentToTop(Bundle bundle, boolean anim, boolean replaceMode, boolean clearTop) {
        startGuestUserCenterFragment(bundle, anim, replaceMode, clearTop, true);
    }
    /**
     * 判断切换的歌手页
     * 跳转新的个人中心 新增统一调用方法
     *
     * @param bundle
     */
    public static void startGuestUserCenterFragment(final Bundle bundle, final boolean anim, final boolean replaceMode, final boolean clearTop, final boolean isToTop) {
        /*try {
            long userId = bundle.getLong("guest_user_id", 0);
            int jumpType = bundle.getInt(UserInfoConstant.USER_JUMP_TYPE, -1);
            if (userId > 0) {
                if (jumpType == 2) {//繁星
                    Bundle intent = new Bundle();
                    intent.putLong(UserInfoConstant.FLAG_TARGET_KUGOU_ID, userId);
                    intent.putInt(UserInfoConstant.KEY_TAB_SELECTED_INDEX, UserInfoConstant.TabIndex.intro_tab);
                    FARouterManager.getInstance().startActivity(DRCommonApplication.getContext(), PageIds.FX_USER_GUEST_INFORMATION_ACTIVITY, intent);
                    return;
                }
                NavigationUtil.checkStartSingerUserFragment(userId, new Action1<Long>() {
                    @Override
                    public void call(Long singerId) {
                        try {
                            if (singerId > 0 && jumpType != 0) {
                                bundle.putLong(NavigationUtil.SINGERID_SEARCH_KEY, singerId);
                                Class<? extends Fragment> cls =
                                        (Class<? extends Fragment>) Class.forName("com.kugou.android.netmusic.bills.SingerDetailFragment");
                                if (isToTop) {
                                    startFragmentToTop(cls, bundle, anim, replaceMode, clearTop);
                                } else {
                                    startFragment(cls, bundle, anim, replaceMode, clearTop);
                                }
                            } else {
                                Class<? extends Fragment> cls =
                                        (Class<? extends Fragment>) Class.forName("com.kugou.android.userCenter.newest.NewestUserCenterMainFragment");
                                bundle.putBoolean(AbsFrameworkFragment.FLAG_NEW_INSTANCE, true);
                                if (isToTop) {
                                    startFragmentToTop(cls, bundle, anim, replaceMode, clearTop);
                                } else {
                                    startFragment(cls, bundle, anim, replaceMode, clearTop);
                                }
                            }
                        } catch (Exception e) {
                            com.kugou.common.utils.DrLog.uploadException(e);
                        }
                    }
                });
            } else {
                Class<? extends Fragment> cls =
                        (Class<? extends Fragment>) Class.forName("com.kugou.android.userCenter.newest.NewestUserCenterMainFragment");
                if (isToTop) {
                    startFragmentToTop(cls, bundle, anim, replaceMode, clearTop);
                } else {
                    startFragment(cls, bundle, anim, replaceMode, clearTop);
                }
            }
        } catch (Exception e) {
            com.kugou.common.utils.DrLog.uploadException(e);
        }*/
    }

	/**
	 * 跳转歌手详情页
	 *
	 * @param singerName 歌手名
	 * @param singerId   歌手id
	 * @param tab        显示tab  // 0 单曲 1专辑 2MV 3详情
	 */
    public static void startSingerDetailFragment(String singerName, int singerId, int tab) {
    	if (TextUtils.isEmpty(singerName) || singerId <= 0) {
    		return;
		}
		if (tab < 0 || tab > 3) {
    		tab = 0;
		}
		Bundle bundle = new Bundle();
		bundle.putString("singer_search", singerName);
		bundle.putInt("singer_id_search", singerId);
		bundle.putInt("jump_to_tab", tab);
		try {
			Class<? extends Fragment> cls =
					(Class<? extends Fragment>) Class.forName("com.kugou.android.netmusic.bills.SingerDetailFragment");
			startFragmentToTop(cls, bundle, true, false, true);
		} catch (Exception e) {

		}
	}

    public static boolean fragmentAlreadyOpen(Class className) {
        ref = getWeakReference(currentStack);
        //1)通过ViewPagerFrameworkDelegate启动fragment
        if (ref != null) {
            ViewPagerFrameworkDelegate d = ref.get();
            if (d != null) {
                AbsFrameworkFragment fragment = d.getCurrentFragment();
                if (fragment != null) {
                    return fragment.getClass().getName().equals(className.getName());
                }
            }
        }
        return false;
    }


	public static int TAB_SONG = 0;
	public static int TAB_ALBUM = 1;
	public static int TAB_MV = 2;
	public static int TAB_DETAIL = 3;

}
