package com.brins.commom.skin;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.skinlib.KGSkinEnv;
import com.kugou.skinlib.attrs.KGBackgroundAttr;
import com.kugou.skinlib.attrs.base.ISkinAttrFactory;
import com.kugou.skinlib.listener.IKGSkinChangedListener;
import com.kugou.skinlib.manager.ISkinChangedListener;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.brins.commom.app.DRCommonApplication.getContext;

public class SkinManager implements ISkinChangedListener {


    private Context mContext;
    //private SkinLoader mSkinLoader;
    private String mCurSkinName = SkinConfig.DEFAULT_SKIN_PATH;

    // 覆盖安装时没有预下载皮肤，进行自动升级。
    private int updateThemeId = Integer.MIN_VALUE;
    private boolean failAutoResetDefault;
    private int defaultSimpleRetryCount;

    private static String sChangedSkinPath = null;

    private SkinManager() {}

    public void setCoverRetry(boolean isCover) {
        checkSkinLoader();
        //mSkinLoader.setCoverRetry(isCover);
    }

    public void setFailAutoResetDefault(boolean failAutoResetDefault) {
        this.failAutoResetDefault = failAutoResetDefault;
    }

    public boolean isFailAutoResetDefault() {
        return failAutoResetDefault;
    }

    public void resetDefaultByPermissionDenied() {
        this.mContext = getContext();
        int viewTopStart = getViewTopStart();
        int[] screenSize = SystemUtils.getPhysicalSS(mContext);
        SkinResourcesUtils.getInstance().setAppResource(mContext.getResources());
        SkinResourcesUtils.getInstance().storeBgRect(0, 0, screenSize[0], screenSize[1] - viewTopStart,false,true);
        /*SkinVerChecker.getInstance().setInnerSkinVersion(SkinProfileUtil.getInnerVersion());
        load(SkinConfig.SKIN_DEFAULT_SKIN_FILE_PATH + SkinConfig.DEFAULT_SKIN_SIMPLE_PATH);*/
    }

    /**
     * 不要随便调用，该方法适配系统深色模式用的
     */
    public void adaptSystemDarkMode() {
        String curSkinName = getCurSkinName();
        boolean isDefault = curSkinName.endsWith(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH) || curSkinName.endsWith(SkinConfig.DEFAULT_SKIN_DARK_NIGHT_PATH);
        if (DrLog.DEBUG) {
            DrLog.i("yyb-skin", "resetDefaultDark: curSkinName="+curSkinName+" isDefault="+isDefault);
        }
        this.mContext = getContext();
        int viewTopStart = getViewTopStart();
        int[] screenSize = SystemUtils.getPhysicalSS(mContext);
        SkinResourcesUtils.getInstance().setAppResource(mContext.getResources());
        SkinResourcesUtils.getInstance().storeBgRect(0, 0, screenSize[0], screenSize[1] - viewTopStart,false,true);
        /*SkinVerChecker.getInstance().setInnerSkinVersion(SkinProfileUtil.getInnerVersion());
        load(SkinConfig.SKIN_DEFAULT_SKIN_FILE_PATH + SkinConfig.DEFAULT_SKIN_DARK_NIGHT_PATH);
        CommonSettingPrefs.getInstance().setDefaultSimpleAdaptSystemDarkInUse(true);*/
    }

    private static class InstanceHolder {
        private final static SkinManager sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return InstanceHolder.sInstance;
    }

    public void init() {
        loadSkin();
    }

    public void loadSkin() {  //参数用来做记录用的，无其他意义
        setSkinEnv(DrLog.DEBUG);

        this.mContext = DRCommonApplication.getContext();
        int viewTopStart = getViewTopStart();
        int[] screenSize = SystemUtils.getPhysicalSS(mContext);
        SkinResourcesUtils.getInstance().setAppResource(mContext.getResources());
        SkinResourcesUtils.getInstance().storeBgRect(0, 0, screenSize[0], screenSize[1] - viewTopStart,false,true);
        /*SkinVerChecker.getInstance().setInnerSkinVersion(SkinProfileUtil.getInnerVersion());
        CommonSettingPrefs.getInstance().setShowSkinUpdateTips(false);
        boolean isCover = SystemUtils.isCover(DRCommonApplication.getContext());
        File filedir = DRCommonApplication.getContext().getFilesDir();
        String value = ""; //为了方便加日志
        boolean isUpdateDone = CommonSettingPrefs.getInstance().isSkinPathUpdateDone();
        if (isUpdateDone) {
            SkinProfileUtil.setUpdateSkinPathDone(true);
        }else {
            String isDoneValue = SkinInfoPrefs.getInstance().getInfo(SkinInfoPrefs.SKIN_IS_UPDATE_PATH_DONE, false);
            SkinFileUtil.logToFile("启动又出现了异常，备份文件里保存的配置值：" + isDoneValue,"启动换肤",false);
        }
        if (isUpdateDone) {
            value = SkinInfoPrefs.getInstance().getInfo(SkinInfoPrefs.SKIN_ONLINE_SKIN_PATH, true);
        } else {
            value = ACache.get(new com.kugou.common.utils.DelFile(filedir, "SkinNameCache"))
                    .getAsString(SkinConfig.SKIN_PATH_KEY_ACACHE);
        }
        boolean isNormalPathEmpty = TextUtils.isEmpty(value);
        if (isNormalPathEmpty){
            value = CommonSettingPrefs.getInstance().getSkinBackupPath();
        }
        if (isNormalPathEmpty && !TextUtils.isEmpty(value)) {
            //异常
            SkinFileUtil.traceErm(124, CommonEnvManager.getUserID() + "-p-" + value);
        }
        String storePath = TextUtils.isEmpty(value) ? SkinConfig.DEFAULT_SKIN_SIMPLE_PATH : value;

        boolean isCustomVersion = storePath.startsWith(SkinConfig.SKIN_CUSTOM_FILE_PATH);
        String skinPath = isCustomVersion ? storePath : (SkinConfig.SKIN_BACKUP_PATH + storePath);
        int customThemeId = SkinProfileUtil.restoreCustomSkinThemeId();
        checkSkinLoader();
        SkinExceptionEntity.setstorePath(storePath);
        SkinExceptionEntity.setCover(isCover);
        HashMap<String,String> logMap = new HashMap<>();
        logMap.put("lastSaveName :", CommonSettingPrefs.getInstance().getSkinName());
        logMap.put("local store Path : ",value);
        logMap.put("storePath",storePath);
        logMap.put("skinPath",skinPath);
        logMap.put("isCover",isCover + "");
        logMap.put("isUpdateDone",isUpdateDone + "");
        logMap.put("fromSource",fromSource + "");
        logMap.put("isNormalPathEmpty", isNormalPathEmpty + "");
        if (isCover && !SharedPreferencesForeProcess.hasShowConfigSkin()) {
            //覆盖升级后，并且未显示过选择皮肤页需要把蓝色皮肤换成白色。如果是覆盖升级，且显示过皮肤选择页了，则说明是旧酷狗X升到新酷狗X，不处理
            logMap.put("cover and replace simple skin", skinPath);
            if (skinPath.endsWith(SkinConfig.DEFAULT_SKIN_PATH) || skinPath.endsWith("solid_default")) {
                skinPath = SkinConfig.SKIN_BACKUP_PATH + SkinConfig.DEFAULT_SKIN_SIMPLE_PATH;
            }
        }

        if (Party100YearUtil.afterAutoChangeLastDay()
                && Party100YearSharedPreference.isParty100YearAutoRestore()
                && Party100YearUtil.PARTY_100_YEAR_SKIN_PATH.equals(skinPath)) {
            skinPath = Party100YearSharedPreference.getParty100YearLastSkinPath();
            Log.d("Party100Year", "auto restore to skin path: " + skinPath);
            Party100YearSharedPreference.setParty100YearAutoRestore(false);
        } else {
            Log.d("Party100Year", String.format(Locale.getDefault(), "after auto change last day: %b, auto restore: %b, use party skin: %b",
                    Party100YearUtil.afterAutoChangeLastDay(),
                    Party100YearSharedPreference.isParty100YearAutoRestore(),
                    Party100YearUtil.PARTY_100_YEAR_SKIN_PATH.equals(skinPath)
                    )
            );
        }

        SkinFileUtil.logToFile(logMap,"酷狗启动，执行换肤初始化.换肤开始标志",true);
        if (DrLog.DEBUG) {
            DrLog.d("yyt_skin","skinPath:"+skinPath + " customThemeId:"+customThemeId);
        }
        load(skinPath, false, isCover,customThemeId);*/
    }

    private boolean isInited = false;
    public void setSkinEnv(boolean isDebug) {
        if (!isInited) {
            isInited = true;
            HashMap<String, ISkinAttrFactory> skinFactoryMap = new HashMap<String, ISkinAttrFactory>();
            skinFactoryMap.put(KGBackgroundAttr.ATTR_NAME, BackgroundAttr::new);
            if (isDebug) {
                Log.d("SkinManager", "setSkinEnv: ");
            }
            KGSkinEnv.getInstance().init().setKGSkinLog(isDebug)
                    .setAppContext(getContext())
                    .setSkinResource(new KGSkinResourceImpl())
                    .setSkinChangedListener(new KGSkinChangedImpl())
                    .setSkinAttrFactory(skinFactoryMap)
                    .build();
        }
    }


    private int getViewTopStart() {
        int viewTopStart = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                viewTopStart = SystemUtils.getStatusBarHeight(mContext);
            } catch (Exception e) {

            }
        }
        return viewTopStart;
    }

/*    public void removeSkinLoadListener(SkinLoader.SkinLoadListener listener) {
        checkSkinLoader();
        mSkinLoader.removeSkinLoadListener(listener);
    }

    public void addSkinLoadListener(SkinLoader.SkinLoadListener listener) {
        checkSkinLoader();
        mSkinLoader.addSkinLoadListener(listener);
    }*/

    public void load(String skinPath) {
        load(skinPath, false);
    }

    public void load(String skinPath, boolean isUpdateTask) {
        load(skinPath, isUpdateTask, false);
    }

    public void load(String skinPath, boolean isUpdateTask, boolean isCover) {
        load(skinPath,isUpdateTask,isCover,-1);
    }

    public void load(String skinPath, boolean isUpdateTask, boolean isCover, int customThemeId) {

    }

    private void checkSkinLoader() {

    }

/*    public void resetDefaultSkin(boolean showTraceBugTree) {
        SkinResourcesUtils.getInstance().setCancelByPermission(false);
        SkinFileUtil.logToFile("当前皮肤名：" + CommonSettingPrefs.getInstance().getSkinName() + "--皮肤路径：" + SkinProfileUtil.resotreSkinName(), "重置默认皮肤", false);
        SkinProfileUtil.sotreCustomInfoPath("");
        CommonSettingPrefs.getInstance().setSkinName(SkinConfig.DEFAULT_SIMPLE_SKIN_NAME);
        load(SkinConfig.SKIN_DEFAULT_SKIN_FILE_PATH + SkinConfig.DEFAULT_SKIN_SIMPLE_PATH);
        if (showTraceBugTree) {
            SkinFileUtil.traceErm(120,CommonEnvManager.getUserID() + "--" + CommonSettingPrefs.getInstance().getSkinName());
//            KGUncaughtHandler.reflectCrashHandlerSaveAnException(new Throwable(), "重置成默认皮肤。uid:" + CommonEnvManager.getUserID(), true);
        }*/
//        CommonSettingPrefs.getInstance().setLocalSolidColorName(SkinColorLib.solidColorSkinNames[0]);
//        CommonSettingPrefs.getInstance().setUsedSkinFileMd5WithId("");
//        setCurSkinName(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH);
//        SkinProfileUtil.sotreSkinName(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH);
//        SkinResourcesUtils.getInstance().setAppResource(mContext.getResources());
//        SkinResourcesUtils.getInstance().setSkinPackageResources(null);
//        handleDataForSupport(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH);
//        sendUpdateSkinAction();
//    }
/*    private void resetDefaultBlueSkin() {
        SkinResourcesUtils.getInstance().setCancelByPermission(false);
        SkinFileUtil.logToFile("当前皮肤名：" + CommonSettingPrefs.getInstance().getSkinName() + "--皮肤路径：" + SkinProfileUtil.resotreSkinName(), "重置默认皮肤", false);
        CommonSettingPrefs.getInstance().setUsedSkinFileMd5WithId("");
        setCurSkinName(SkinConfig.DEFAULT_SKIN_PATH);
        SkinProfileUtil.sotreSkinName(SkinConfig.DEFAULT_SKIN_PATH);
        if (mContext == null){
            mContext = getContext();
        }
        SkinResourcesUtils.getInstance().setAppResource(mContext.getResources());
        SkinResourcesUtils.getInstance().setSkinPackageResources(null);
        SkinResourcesUtils.getInstance().setCustomSkinConfigEntiry(null);
        CommonSettingPrefs.getInstance().setSkinName(SkinConfig.DEFAULT_SKIN_NAME);
        handleDataForSupport(SkinConfig.DEFAULT_SKIN_PATH);
        sendUpdateSkinAction();
    }*/

/*    private SkinLoader.SkinLoadListener skinLoadListener = new SkinLoader.SkinLoadListener() {
        @Override
        public void onSuccessed(Resources result, String skinPath,SkinResultEntity skinResultEntity, CustomSkinConfigEntiry customSkinConfigEntiry) {
            SkinResourcesUtils.getInstance().setSkinPackageResources(result);
            SkinResourcesUtils.getInstance().setCustomSkinConfigEntiry(customSkinConfigEntiry);
            preLoadBg();
            sendUpdateSkinAction();
            setCurSkinName(skinPath);
            handleDataForSupport(skinPath);
        }

        @Override
        public void onFailed(String skinPath,int themeId,SkinResultEntity skinResultEntity) {
            if (skinPath.endsWith(SkinConfig.DEFAULT_SKIN_PATH)){
                resetDefaultBlueSkin();
            } else {
                String defaultSimplePath = SkinConfig.SKIN_DEFAULT_SKIN_FILE_PATH + SkinConfig.DEFAULT_SKIN_SIMPLE_PATH;
                if (skinPath.equals(defaultSimplePath)) {
                    // 极简皮肤失败重试次数限制，避免重复上报崩溃
                    if (defaultSimpleRetryCount < 3) {
                        resetDefaultSkin(false);
                        defaultSimpleRetryCount++;
                    }
                } else {
                    resetDefaultSkin(false);
                }
            }
        }
    };*/



    /**
     * 如果是自定义皮肤，提前加载背景图
     */
    private void preLoadBg() {
        if (SkinProfileUtil.isCustomSkin()){
            SkinResourcesUtils.getInstance().getDrawableBg(SkinBgType.MAIN);
            SkinResourcesUtils.getInstance().getDrawableBg(SkinBgType.MENU);
        }
    }

    @Deprecated
    private void handleDataForSupport(String path) {

    }

    private void sendUpdateSkinAction() {
        if (DrLog.DEBUG) {
            DrLog.d("yyt_skin","send sendUpdateSkinAction ACTION_SKIN_CHANGED");
        }
        //对于宿主使用方，发送该广播
        //BroadcastUtil.sendBroadcast(new KGIntent(SkinConfig.ACTION_SKIN_CHANGED));
        //皮肤包加载后，触发换肤引擎 调用skinfactory根据皮肤包resource 以及 对应activity的xml、实现ISkinUpload的实现类进行换肤
        updateSkinChanged();
    }

    public String getCurSkinName() {
        return mCurSkinName;
    }

    public void setCurSkinName(String skinName) {
        this.mCurSkinName = skinName;
    }

    public void releaseSkinLoader() {
        /*if (mSkinLoader != null) {
            mSkinLoader.releaseExecutorService();
        }*/
    }

    public int getUpdateThemeId() {
        return updateThemeId;
    }

    public void setUpdateThemeId(int updateThemeId) {
        this.updateThemeId = updateThemeId;
    }

    /**
     * 由SkinEngine 调用
     * KGSkinEngine对应的Activity生命周期 onCreate时添加注册，onDestroy时remove掉
     * updateSkinChanged调用时机是，当皮肤发生变化，
     * SkinManager#Skinload.onSucced--》sendUpdateSkinAction --》updateSkinChanged
     */
    private final ArrayList<WeakReference<IKGSkinChangedListener>> mSkinChangedListeners = new ArrayList<>();

    @Override
    public void addSkinChangedListener(IKGSkinChangedListener listener) {
        boolean result = mSkinChangedListeners.add(new WeakReference<>(listener));
        if (DrLog.DEBUG) {
            DrLog.i("yabin-SkinManager", "addSkinChangedListener: result="+result+" mSkinChangedListeners.size="+mSkinChangedListeners.size());
        }
    }

    @Override
    public void removeSkinChangedListener(IKGSkinChangedListener listener) {
        for (int i = 0; i < mSkinChangedListeners.size(); i++) {
            WeakReference<IKGSkinChangedListener> reference = mSkinChangedListeners.get(i);
            if (reference != null && reference.get() != null) {
                if (reference.get().equals(listener)) {
                    mSkinChangedListeners.remove(i);
                    if (DrLog.DEBUG) {
                        DrLog.i("yabin-SkinManager", "removeSkinChangedListener: result=" + " mSkinChangedListeners.size=" + mSkinChangedListeners.size());
                    }
                }
            }
        }
    }

    @Override
    public void clearSkinChangedListeners() {
        mSkinChangedListeners.clear();
    }

    /**
     * 收到皮肤变更后,遍历所有的皮肤变更的观察者,通知其更新
     */
    @Override
    public void updateSkinChanged() {
        for (int i = 0; i < mSkinChangedListeners.size(); i++) {
            WeakReference<IKGSkinChangedListener> weakReference = mSkinChangedListeners.get(i);
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().onSkinAllChanged();
            }
        }
    }
}
