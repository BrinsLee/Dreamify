
package com.brins.commom.preference;

import android.text.TextUtils;
import androidx.annotation.IntRange;
import com.brins.commom.utils.log.DrLog;

public class CommonSettingPrefs extends AbstractSharedPreference {

    public static final int NO_SET_INT = -1;
    public static final int TRUE_INT = 1;
    public static final int FALSE_INT = 0;

    private static final String PREFERENCES_NAME = "setting";

    private volatile static CommonSettingPrefs sInstance;

    private CommonSettingPrefs(String name) {
        super(name);
    }

    public static synchronized CommonSettingPrefs getInstance() {
        if (sInstance == null) {
            synchronized (CommonSettingPrefs.class) {
                if (sInstance == null) {
                    sInstance = new CommonSettingPrefs(PREFERENCES_NAME);
                }
            }
        }
        return sInstance;
    }

    public static boolean hasInstance() {
        return sInstance != null;
    }

    /**
     * 是否新安装用户
     */
    public boolean isNewInstall() {
        return getBoolean(Keys.IS_NEW_INSTALL, true);
    }

    /**
     * 设置是否新安装用户
     */
    public boolean setNewInstall(boolean isNewInstall) {
        return putBoolean(Keys.IS_NEW_INSTALL, isNewInstall);
    }

    public String getCustomColorName() {
        return getString(Keys.CUSTOM_SKIN_COLOR_NAME, "");
    }

    public void setCustomColorName(String name) {
        putString(Keys.CUSTOM_SKIN_COLOR_NAME, name);
    }

    /**
     * 记录跑步使用的步长
     */
    public void setUsedRunningStepLength(float length) {
        putFloat(Keys.KEY_KG_RUN_STEP_LENGTH, length);
    }

    /**
     * 得到记录的历史步长，默认90厘米
     */
    public float getUsedRunningStepLength() {
        return getFloat(Keys.KEY_KG_RUN_STEP_LENGTH, 90);
    }

    public int getRunningUsedBpm() {
        return getInt(Keys.KEY_KG_RUN_USED_BPM, 160);
    }

    public void setRunningUsedBpm(int bpm) {
        putInt(Keys.KEY_KG_RUN_USED_BPM, bpm);
    }

    public String getAudioAdUa() {
        return getString(Keys.KEY_AUDIO_AD_UA, "");
    }

    public void setAudioAdUa(String ua) {
        putString(Keys.KEY_AUDIO_AD_UA, ua);
    }

    public int getBackProcessPid() {
        return getInt(Keys.KUGOU_PID_BACKPROCESS, -1);
    }

    public boolean setBackProcessPid(int value) {
        return putInt(Keys.KUGOU_PID_BACKPROCESS, value);
    }

    public int getForeProcessPid() {
        return getInt(Keys.KUGOU_PID, -1);
    }

    public boolean setForeProcessPid(int value) {
        return putInt(Keys.KUGOU_PID, value);
    }

    private byte[] exitAppLock = new byte[0];

    public boolean isExitApp() {
        synchronized (exitAppLock) {
            return getBoolean(Keys.IS_EXIT_APP_KEY, false);
        }
    }

    public boolean setExitApp(boolean value) {
        if (DrLog.DEBUG) DrLog.i("exit::CommonSettingPrefs", "setExitApp value = " + value);
        synchronized (exitAppLock) {
            return putBoolean(Keys.IS_EXIT_APP_KEY, value);
        }
    }

    public void setShowShortVideoTipDialog(boolean show) {
        putBoolean(Keys.KG_SHOW_SHORT_VIDEO_TIP_DIALOG, show);
    }

    public boolean isShowShortVideoTipDialog() {
        return getBoolean(Keys.KG_SHOW_SHORT_VIDEO_TIP_DIALOG, false);
    }

    public String getVipSkinName() {
        return getString(Keys.USED_VIP_SKIN_NAME, "");
    }

    public boolean setVipSkinName(String skinName) {
        return putString(Keys.USED_VIP_SKIN_NAME, skinName);
    }

    public String getUsedSkinFileMd5WithId() {
        return getString(Keys.USED_SKIN_FILE_MD5_ID, "");
    }

    public boolean setUsedSkinFileMd5WithId(String md5WithId) {
        return putString(Keys.USED_SKIN_FILE_MD5_ID, md5WithId);
    }

    public String getMusicalNoteBalance(long userid) {
        return getString(Keys.MUSICAL_NOTE_ACCOUNT + userid, "");
    }

    public boolean setMusicalNoteBalance(String str, long userid) {
        return putString(Keys.MUSICAL_NOTE_ACCOUNT + userid, str);
    }

    //月日
    public void setUserBirthdayMMDD(String birthday) {
        if (DrLog.DEBUG) {
            DrLog.d("birthday", "birthday=" + birthday);
        }
        putString(Keys.USER_BIRTHDAY_MMDD, birthday);
    }

    public String getUserBirthdayMMDD() {
        String birthday = getString(Keys.USER_BIRTHDAY_MMDD, "");
        return birthday == null ? "" : birthday;
    }

    public void setUserBirthdayYYYYmmDD(String birthday) {
        putString(Keys.USER_BIRTHDAY_YYYYMMDD, birthday);
    }

    public String getUserBirthdayYYYYmmDD() {
        String birthday = getString(Keys.USER_BIRTHDAY_YYYYMMDD, "");
        return birthday == null ? "" : birthday;
    }

    public int getUserSex() {
        return getInt(Keys.USER_SEX, 2);
    }

    public void setUserSex(int sex) {
        putInt(Keys.USER_SEX, sex);
    }

    public boolean hasShowImportPlaylistDialog() {
        return getBoolean(Keys.HAS_SHOW_IMPORT_PLAYLIST_DIALOG, false);
    }

    public void setHasShowedImportPlaylistDialog(boolean hasShowed) {
        putBoolean(Keys.HAS_SHOW_IMPORT_PLAYLIST_DIALOG, hasShowed);
    }

    public int getNewStartUpDays() {
        return getInt(Keys.NEW_START_UP_DAYS, 0);
    }

    public void addNewStartUpDays() {
        int days = getNewStartUpDays();
        days++;
        putInt(Keys.NEW_START_UP_DAYS, days);
    }

    public void setLocalNeedRefreshPlaylistCover(long userId, String playlistIdArray) {
        putString(Keys.LOCAL_NEED_REFRESH_PLAYLIST_COVER + userId, playlistIdArray);
    }

    public String getLocalNeedRefreshPlaylistCover(long userId) {
        return getString(Keys.LOCAL_NEED_REFRESH_PLAYLIST_COVER + userId, null);
    }

    public void setGameAssistantDelMsgId(String msgId) {
        putString(Keys.MINI_GAME_ASSISTANT_DELETE_MSG_ID, msgId);
    }

    public String getGameAssistantDelMsgId() {
        return getString(Keys.MINI_GAME_ASSISTANT_DELETE_MSG_ID, null);
    }

    public void setHasClickMyTabMiniAppEntrance(boolean hasClick) {
        putBoolean(Keys.MINI_APP_MY_TAB_ENTRANCE_CLICK, hasClick);
    }

    public boolean getHasClickMyTabMiniAppEntrance() {
        return getBoolean(Keys.MINI_APP_MY_TAB_ENTRANCE_CLICK, false);
    }

    public void setHasClickMyTabGuideMiniAppEntrance(boolean hasClick) {
        putBoolean(Keys.MINI_APP_MY_TAB_GUIDE_ENTRANCE_CLICK, hasClick);
    }

    public boolean getHasClickMyTabGuideMiniAppEntrance() {
        return getBoolean(Keys.MINI_APP_MY_TAB_GUIDE_ENTRANCE_CLICK, false);
    }

    public void setHasShowMiniAppIntro(boolean hasShow) {
        putBoolean(Keys.HAS_SHOW_MINIAPP_INTRO, hasShow);
    }

    public boolean getHasShowMiniAppIntro() {
        return getBoolean(Keys.HAS_SHOW_MINIAPP_INTRO, false);
    }

    public void setHasShowModeSwitchDialog(boolean hasShow, String type) {
        putBoolean(Keys.HAS_SHOW_MODE_SWITCH + type, hasShow);
    }

    public boolean getHasShowModeSwitchDialog(String type) {
        return getBoolean(Keys.HAS_SHOW_MODE_SWITCH + type, false);
    }

    public void setHasShowModeSwitchTip(boolean hasShow) {
        putBoolean(Keys.HAS_SHOW_MODE_SWITCH_TIP, hasShow);
    }

    public boolean getHasShowModeSwitchTip() {
        return getBoolean(Keys.HAS_SHOW_MODE_SWITCH_TIP, false);
    }

    // 读取密码（离线or自动登陆的sign?
    public String getUserSign() {
        return getString(Keys.USER_SIGN, "");
    }

    public boolean isUserVip() {
        return getBoolean(Keys.USER_IS_VIP, false);
    }

    public String getVipType() {
        return String.valueOf(getInt(Keys.LOCAL_VIP_TYPE, 0));
    }

    public int getVipTypeInt() {
        return getInt(Keys.LOCAL_VIP_TYPE, 0);
    }

    public void setVipType(int vipType) {
        putInt(Keys.LOCAL_VIP_TYPE, vipType);
    }

    public void setMusicType(int m_type) {
        putInt(Keys.MUSIC_TYPE, m_type);
    }

    public int getMusicType() {
        return getInt(Keys.MUSIC_TYPE, 0);
    }

    public void setMusicIsOld(int m_is_old) {
        putInt(Keys.M_IS_OLD, m_is_old);
    }

    public int getMusicIsOld() {
        return getInt(Keys.M_IS_OLD, 0);
    }

    public void setMusicBeginTime(String m_begin) {
        putString(Keys.MUSIC_BEGIN_TIME, m_begin);
    }

    public String getMusicBeginTime() {
        return getString(Keys.MUSIC_BEGIN_TIME, "");
    }

    public String getVIPBeginTime() {
        return getString(Keys.VIP_BEGIN_TIME, "");
    }

    public void setMusicEndTime(String m_end) {
        putString(Keys.MUSIC_END_TIME, m_end);
    }

    public String getMusicEndTime() {
        String timeStr = getString(Keys.MUSIC_END_TIME, "");
        if ("null".equals(timeStr)) {
            timeStr = "";
        }
        return timeStr;
    }

    public String getVipEndTime() {
        String timeStr = getString(Keys.VIP_END_TIME, "");
        if ("null".equals(timeStr)) {
            timeStr = "";
        }
        return timeStr;
    }

    public String getBindMail() {
        return getString(Keys.BIND_MAIL, "");
    }

    public String getBindPhone() {
        return getString(Keys.BIND_PHONE, "");
    }

    public boolean isAutoLogin() {
        return getBoolean(Keys.USER_AUTO_LOGIN, false);
    }

    public boolean setAutoLogin(boolean value) {
        return putBoolean(Keys.USER_AUTO_LOGIN, value);
    }

    public boolean isRememberPwd() {
        return getBoolean(Keys.USER_REMEMBER_PWD, false);
    }

    public boolean setRememberPwd(boolean value) {
        return putBoolean(Keys.USER_REMEMBER_PWD, value);
    }

    /**
     * 游戏红点点击后，设置游戏红点推送信息
     */
    public void setNewTips(String newTips) {
        putString(Keys.KUGOU_GAME_NEW_TIPS, newTips);
    }

    /**
     * 返回游戏红点推送信息以判断是否显示红点
     */
    public String getNewTips() {
        return getString(Keys.KUGOU_GAME_NEW_TIPS, "");
    }

    /**
     * 读取密码?013版之前的密码，用户版本过度时的检测的，新版登陆后会把这项清除? *
     */
    public String getLoginPassword() {
        return getString(Keys.USER_PASSWORD, "");
    }

    /**
     * 清除旧版中的密码?013版之前的密码，用户版本过度时的检测的，新版登陆后会把这项清除? *
     */
    public boolean clearLoginPassword() {
        return remove(Keys.USER_PASSWORD);
    }

    public int[] getUnreadMsgCount() {
        String str = getString(Keys.UNREAD_MSG_COUNT, "");
        if (TextUtils.isEmpty(str)) {
            return new int[] { 0, 0 };
        }
        try {
            String[] intStr = str.split("\\|");
            return new int[] { Integer.valueOf(intStr[0]), Integer.valueOf(intStr[1]) };
        } catch (Throwable t) {
            t.printStackTrace();
            return new int[] { 0, 0 };
        }
    }

    public void setUnreadMsgCountWithoutCommentCount(int count) {
        putInt(Keys.UNREAD_MSG_COUNT_WITHOUT_COMMENT_COUNT, count);
    }

    public int getUnreadMsgCountWithoutCommentCount() {
        return getInt(Keys.UNREAD_MSG_COUNT_WITHOUT_COMMENT_COUNT, 0);
    }

    public void setUnreadMsgCount(int canShowCount, int cannotShowCount) {
        String str = canShowCount + "|" + cannotShowCount;
        putString(Keys.UNREAD_MSG_COUNT, str);
    }

    public long[] getCacheGradeInfo() {
        String str = getString(Keys.CACHE_GRADE_INFO, "");
        if (TextUtils.isEmpty(str)) {
            return new long[] { 0, 0 };
        }
        try {
            String[] longStr = str.split("\\|");
            return new long[] { Long.valueOf(longStr[0]), Long.valueOf(longStr[1]) };
        } catch (Throwable t) {
            t.printStackTrace();
            return new long[] { 0, 0 };
        }
    }

    public void setCacheGradeInfo(int grade, long localMillis) {
        String str = grade + "|" + localMillis;
        putString(Keys.CACHE_GRADE_INFO, str);
    }

    public int getCacheFinishPercent() {
        return getInt(Keys.CACHE_FINISH_PERENCT_INFO, 100);
    }

    public void setCacheFinishPercent(int finishPercent) {
        putInt(Keys.CACHE_FINISH_PERENCT_INFO, finishPercent);
    }

    public String getUserImageUrl() {
        return getString(Keys.USER_IMAGE_URL, "");
    }

    public void setDefaultImgVersion(int version) {
        putInt(Keys.DEFAULT_USER_IMG_VERSION, version);
    }

    public int getDefaultImgVersion() {
        return getInt(Keys.DEFAULT_USER_IMG_VERSION, 0);
    }

    public void setLoginThirdPlatform(int platform) {
        putInt(Keys.USER_THIRD_PLATFORM, platform);
    }

    public int getLoginThirdPlatform() {
        return getInt(Keys.USER_THIRD_PLATFORM, 0);
    }

    public long getMusicCloudUploadMaxDuration() {
        return getLong(Keys.MUSIC_CLOUD_UPLOAD_MAX_DURATION, 3600L);
    }

    public void setMusicCloudUploadMaxDuration(long maxDuration) {
        putLong(Keys.MUSIC_CLOUD_UPLOAD_MAX_DURATION, maxDuration);
    }

    public long getMusicCloudUploadMinDuration() {
        return getLong(Keys.MUSIC_CLOUD_UPLOAD_MIN_DURATION, 5L);
    }

    public void setMusicCloudUploadMinDuration(long minDuration) {
        putLong(Keys.MUSIC_CLOUD_UPLOAD_MIN_DURATION, minDuration);
    }

    public boolean getChatFaceRedPoint() {
        return getBoolean(Keys.CHAT_FACE_RED_POINT, true);
    }

    public void setChatFaceRedPoint(boolean canShow) {
        putBoolean(Keys.CHAT_FACE_RED_POINT, canShow);
    }

    public boolean getChatSearchStatus() {
        return getBoolean(Keys.CHAT_FACE_SEARCH_GIF, true);
    }

    public void setChatSearchStatus(boolean canShow) {
        putBoolean(Keys.CHAT_FACE_SEARCH_GIF, canShow);
    }

    public void setAutoCleanCachePeriod(int period) {
        putInt(Keys.AUTO_CLEAN_CACHE_PERIOD, period);
    }

    public int getAutoCleanCachePeriod() {
        return getInt(Keys.AUTO_CLEAN_CACHE_PERIOD, Integer.MIN_VALUE);
    }

    public void setCleanCacheTimeFlag(long time) {
        putLong(Keys.CLEAN_CACHE_TIME, time);
    }

    public long getCleanCacheTimeFlag() {
        return getLong(Keys.CLEAN_CACHE_TIME, -1);
    }

    public boolean hasClearRecentPlaylistRecord() {
        return getBoolean(Keys.HAS_CLEAR_RECENT_PLAYLIST_RECORD, false);
    }

    public void setClearRecentPlaylistRecord(boolean hasClear) {
        putBoolean(Keys.HAS_CLEAR_RECENT_PLAYLIST_RECORD, hasClear);
    }

    public String getInstallUUID() {
        return getString(Keys.INSTALL_UUID, "");
    }

    /**
     * 设置彩铃购买时间
     */
    public void setRingtoneBuyTime(long buyTime) {
        putLong(Keys.RINGTONE_USER_VAILTIME, buyTime);
    }

    public boolean isInRingtoneValidTime() {
        long buyTime = getLong(Keys.RINGTONE_USER_VAILTIME, 0);
        long nowTime = System.currentTimeMillis();
        if ((nowTime > buyTime) && (((nowTime - buyTime) / 86400000) <= 30)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean setInstallUUID(String value) {
        return putString(Keys.INSTALL_UUID, value);
    }

    public boolean isDownloadX86Code() {
        return getBoolean(Keys.IS_DOWNLOAD_X86_CODE, false);
    }

    public boolean setDownloadX86Code(boolean flag) {
        return putBoolean(Keys.IS_DOWNLOAD_X86_CODE, flag);
    }

    public boolean setX86VersionCode(int X86version) {
        return putInt(Keys.X86_VERSION_CODE, X86version);
    }

    public int getX86VersionCode() {
        return getInt(Keys.X86_VERSION_CODE, 0);
    }

    public boolean setDownloadingX86So(boolean isDownloading) {
        return putBoolean(Keys.X86_DOWNLOADING, isDownloading);
    }

    public Boolean isDownloadingX86So() {
        return getBoolean(Keys.X86_DOWNLOADING, false);
    }

    public boolean setX86InstallVersion(int versionCode) {
        return putInt(Keys.X86_INTALL_VERSION, versionCode);
    }

    public int getX86InstallVersion() {
        return getInt(Keys.X86_INTALL_VERSION, -1);
    }

    /**
     * 最近切换的sdcard的根目录--缓存目录所在的sdcard
     *
     * @param rootPath 根目录，不会已 ‘/’结尾
     */
    public boolean setLastSwitchSDcard(String rootPath) {
        return putString(Keys.LAST_SWITCH_SDCARD_ROOT, rootPath);
    }

    /**
     * 最近切换的sdcard的根目录--缓存目录所在的sdcard
     */
    public String getLastSwitchSDcard() {
        return getString(Keys.LAST_SWITCH_SDCARD_ROOT, "");
    }

    /**
     * 设置是否因为 SD 卡损坏而需要使用短文件名
     */
    public boolean setEnabledDownloadShortname(boolean enableShortname) {
        return putBoolean(Keys.ENABLED_DOWNLOAD_SHORTNAME, enableShortname);
    }

    public boolean enabledDownloadShortname() {
        return getBoolean(Keys.ENABLED_DOWNLOAD_SHORTNAME, false);
    }

    public boolean setCachedEncryptKey(String cachedKey) {
        return putString(Keys.TEMP_CACHE_ID, cachedKey);
    }

    public String getTempCacheID() {
        return getString(Keys.TEMP_CACHE_ID, "");
    }

    public boolean setTransferPeerID(long peerID) {
        return putLong(Keys.TRANSFER_PEER_ID, peerID);
    }

    public long getTransferPeerID() {
        return getLong(Keys.TRANSFER_PEER_ID, 0);
    }

    public boolean setLastDiffusionTime(long unixTime) {
        return putLong(Keys.P2P_DIFFUSION_TIME, unixTime);
    }

    public long getLastDiffusionTime() {
        return getLong(Keys.P2P_DIFFUSION_TIME, 0);
    }

    public void setLastVipRemain(int remain) {
        remain = remain <= 0 ? 0 : remain;
        putInt(Keys.MEDIA_STORE_LAST_VIP_REMAIN_NUM, remain);
    }

    public int getLastVipRemain() {
        return getInt(Keys.MEDIA_STORE_LAST_VIP_REMAIN_NUM, 0);
    }

    public void setLastWalletBalance(String remain) {
        putString(Keys.WALLET_LAST_USER_BALANCE_NUM, remain);
    }

    public String getLastWalletBalance() {
        return getString(Keys.WALLET_LAST_USER_BALANCE_NUM, "0");
    }

    /**
     * 是否为流量保护
     */
    public boolean isTrafficProtected() {
        return getBoolean(Keys.IS_TRAFFIC_PROCTED, true);
    }

    /**
     * 设置流量保护开关
     */
    public void setTrafficProtected(boolean value) {
        putBoolean(Keys.IS_TRAFFIC_PROCTED, value);
        if (DrLog.DEBUG) DrLog.d(PREFERENCES_NAME, "setTrafficProtected:" + value);
    }

    /**
     * 是否可弹通知弹窗
     */
    public boolean isNotificationDialogShowed() {
        return getBoolean(Keys.IS_NOTIFICATION_DIALOG_SHOWED, false);
    }

    /**
     * 设置流量保护开关
     */
    public void setNotificationDialogShowed(boolean value) {
        putBoolean(Keys.IS_NOTIFICATION_DIALOG_SHOWED, value);
        if (DrLog.DEBUG) DrLog.d(PREFERENCES_NAME, "setNotificationDialogShowed:" + value);
    }

    /**
     * 是否桌面控件关闭流量提醒弹窗开关
     */
    public boolean isCloseTrafficProctedByWidget() {
        return getBoolean(Keys.IS_CLOSE_TRAFFIC_PROCTED_BY_WIDGET, false);
    }

    /**
     * 设置是否桌面控件关闭流量弹窗开关
     */
    public void setCloseTrafficProtedByWidget(boolean value) {
        putBoolean(Keys.IS_CLOSE_TRAFFIC_PROCTED_BY_WIDGET, value);
        if (DrLog.DEBUG) DrLog.d(PREFERENCES_NAME, "setCloseTrafficProtedByWidget:" + value);
    }

    /**
     * 设置定时结束是否播完整首歌再停止
     */
    public void setStopAfterPlayed(boolean flag) {
        putBoolean(Keys.IS_STOP_AFTER_PLAYED, flag);
    }

    public boolean isStopAfterPlayed() {
        return getBoolean(Keys.IS_STOP_AFTER_PLAYED, false);
    }

    /**
     * 记住用户定时结束后的选择
     */
    public int getToDoAfterTiming() {
        return getInt(Keys.TO_DO_AFTER_TIMING, 0);
    }

    public void setToDoAfterTiming(int value) {
        putInt(Keys.TO_DO_AFTER_TIMING, value);
    }

    public String getAudioAdCountDailyDate() {
        return getString(Keys.KEY_AUDIO_AD_COUNT_DATE, "");
    }

    public void setAudioAdCountDailyDate(String date) {
        setAudioAdTodayAlreadyPlayCount(0);
        putString(Keys.KEY_AUDIO_AD_COUNT_DATE, date);
    }

    public int getAudioAdTodayAlreadyPlayCount() {
        return getInt(Keys.KEY_AUDIO_AD_TODAY_ALREADY_COUNT, 0);
    }

    public void setAudioAdTodayAlreadyPlayCount(int value) {
        putInt(Keys.KEY_AUDIO_AD_TODAY_ALREADY_COUNT, value);
    }

    public long getAlarmSelectedCustomTime() {   //单位：分
        return getLong(Keys.ALARM_SELECT_CUSTOM_TIME, -1);
    }

    public void setAlarmSelectedCustomTime(long customTime) {
        putLong(Keys.ALARM_SELECT_CUSTOM_TIME, customTime);
    }

    /**
     * 设置是否检测过歌曲是否在云端
     */
    public void setGameCenterShortcutOnceCreate(boolean flag) {
        putBoolean(Keys.GAME_CENTER_SHORTCUT_ONCE_CREATE, flag);
    }

    /**
     * 删除是否检测过歌曲是否在云端
     */
    public boolean getGameCenterShortcutOnceCreate() {
        return getBoolean(Keys.GAME_CENTER_SHORTCUT_ONCE_CREATE, false);
    }

    /**
     * 设置加载看模块的时间点
     */
    public void setLoadingFanxingTimeStamp(long time) {
        putLong(Keys.LOADING_FANXING_TIME_STAMP, time);
    }

    /**
     * 获取加载看模块的时间点
     */
    public long getLoadingFanxingTimeStamp() {
        return getLong(Keys.LOADING_FANXING_TIME_STAMP, 0);
    }

    /**
     * 设置加载唱模块的时间点
     */
    public void setLoadingKtvTimeStamp(long time) {
        putLong(Keys.LOADING_KTV_TIME_STAMP, time);
    }

    /**
     * 获取加载唱模块的时间点
     */
    public long getLoadingKtvTimeStamp() {
        return getLong(Keys.LOADING_KTV_TIME_STAMP, 0);
    }

    /**
     * 设置加载玩模块的时间点
     */
    public void setLoadingGameTimeStamp(long time) {
        putLong(Keys.LOADING_GAME_TIME_STAMP, time);
    }

    /**
     * 获取加载玩模块的时间点
     */
    public long getLoadingGameTimeStamp() {
        return getLong(Keys.LOADING_GAME_TIME_STAMP, 0);
    }

    /**
     * 设置触发酷群活跃用户的时间点
     */
    public void setLoadingKuqunTimeStamp(long time) {
        putLong(Keys.LOADING_KUQUN_TIME_STAMP, time);
    }

    /**
     * 获取触发酷群活跃用户的时间点
     */
    public long getLoadingKuqunTimeStamp() {
        return getLong(Keys.LOADING_KUQUN_TIME_STAMP, 0);
    }

    /**
     * 设置触发酷群活跃用户的时间点
     */
    public void setLoadingPersonalTimeStamp(long time) {
        putLong(Keys.PLAY_PERSONAL_TIME_STAMP, time);
    }

    /**
     * 获取触发酷群活跃用户的时间点
     */
    public long getLoadingPersonalTimeStamp() {
        return getLong(Keys.PLAY_PERSONAL_TIME_STAMP, 0);
    }

    /**
     * 设置用户曾进入过酷群
     */
    public void setHasEnterKuqun(int value) {
        putInt(Keys.USER_HAS_IN_KUQUN_BEFORE, value);
    }

    /**
     * 获取用户是否曾进入过酷群
     */
    public boolean getHasEnterKuqun() {
        return getInt(Keys.USER_HAS_IN_KUQUN_BEFORE, 0) == 1;
    }

    /**
     * 设置用户是否有礼物资源缓存
     * 0 - 未知
     * 1 - 被清理
     * 2 - 有缓存
     */
    public void setKuqunGiftResCacheState(int value) {
        putInt(Keys.KUQUN_GIFT_RES_CACHE_STATE, value);
    }

    /**
     * 获取用户是否有礼物资源缓存
     */
    public int getKuqunGiftResCacheState() {
        return getInt(Keys.KUQUN_GIFT_RES_CACHE_STATE, 0);
    }

    /**
     * 设置进入酷群首页或直播间的时间
     */
    public void setEnterKuqunModuleTime(long time) {
        putLong(Keys.KUQUN_ENTER_KUQUN_MODULE_TIME, time);
    }

    /**
     * 获取进入酷群首页或直播间的时间
     */
    public long getEnterKuqunModuleTime() {
        return getLong(Keys.KUQUN_ENTER_KUQUN_MODULE_TIME, 0L);
    }

    /**
     * 写入鱼声接入繁星的货币配置
     */
    public void setKuqunFxCurrencyConfig(String json) {
        putString(Keys.KUQUN_FX_CURRENCY_CONFIG, json);
    }

    /**
     * 获取鱼声接入繁星的货币配置
     */
    public String getKuqunFxCurrencyConfig() {
        return getString(Keys.KUQUN_FX_CURRENCY_CONFIG, "");
    }

    /**
     * 设置首页瀑布流酷群入口显示时间
     */
    public void setKuqunTingEntryShowTime(long time) {
        putLong(Keys.KUQUN_TING_ENTRY_SHOW_TIME, time);
    }

    /**
     * 获取首页瀑布流酷群入口显示时间
     */
    public long getKuqunTingEntryShowTime() {
        return getLong(Keys.KUQUN_TING_ENTRY_SHOW_TIME, 0L);
    }

    /**
     * 获取用户的类型 听/看/唱/玩
     */
    public int getUserType() {
        final int USERTYPE_TING = 1 << 0;// 00001
        final int USERTYPE_KAN = 1 << 1;// 00010
        final int USERTYPE_CHANG = 1 << 2;// 00100
        final int USERTYPE_WAN = 1 << 3;// 01000
        final int USERTYPE_KUQUN = 1 << 5;// 100000
        //猜你喜欢
        final int USERTYPE_PERSONAL = 1 << 6;//1000000
        final long threeDays = 1000 * 60 * 60 * 24 * 3;//三天
        long currentTime = System.currentTimeMillis();
        int userType = USERTYPE_TING;
        if ((currentTime - getLoadingFanxingTimeStamp()) < threeDays) {
            userType |= USERTYPE_KAN;
        }
        if ((currentTime - getLoadingKtvTimeStamp()) < threeDays) {
            userType |= USERTYPE_CHANG;
        }
        if ((currentTime - getLoadingGameTimeStamp()) < threeDays) {
            userType |= USERTYPE_WAN;
        }

        long thirtyDays = 1000L * 60 * 60 * 24 * 30;

        if ((currentTime - getLoadingKuqunTimeStamp()) < thirtyDays) {//酷群用户使用30天定义
            userType |= USERTYPE_KUQUN;
        }

        if ((currentTime - getLoadingPersonalTimeStamp()) < thirtyDays) {
            userType |= USERTYPE_PERSONAL;
        }
        return userType;
    }

    /**
     * 设置游戏入口红点接口完整json字符串
     */
    public void setNewTipsJson(String newTipsJson) {
        putString(Keys.KUGOU_GAME_NEW_TIPS_JSON, newTipsJson);
    }

    /**
     * 第三方首次登录获取头像供看唱使用
     */
    public void set3rdHeadPhotoUrl(String userId, String url) {
        putString(Keys.KUGOU_3RD_LOGIN_HEAD_PHOTO + userId, url);
    }

    /**
     * 第三方首次登录获取头像供看唱使用 第三方原始Url
     */
    public String get3rdHeadPhotoUrl(String userId) {
        return getString(Keys.KUGOU_3RD_LOGIN_HEAD_PHOTO + userId, "");
    }

    /**
     * 第三方首次登录获取昵称供看唱使用
     */
    public void set3rdNickname(String userId, String nickname) {
        putString(Keys.KUGOU_3RD_LOGIN_NICKNAME + userId, nickname);
    }

    /**
     * 第三方首次登录获取昵称供看唱使用
     */
    public String get3rdNickname(String userId) {
        return getString(Keys.KUGOU_3RD_LOGIN_NICKNAME + userId, "");
    }

    /**
     * 酷狗Live点击后，设置酷狗live推送信息
     */
    public void setKugouLiveNewTips(String newTips) {
        putString(Keys.KUGOU_FX_KUGOU_LIVE_NEW_TIPS, newTips);
    }

    /**
     * 返回酷狗live推送信息以判断是否显示推送信息
     */
    public String getKugouLiveNewTips() {
        return getString(Keys.KUGOU_FX_KUGOU_LIVE_NEW_TIPS, "");
    }

    /**
     * 设置酷狗live红点接口完整json字符串
     */
    public void setKugouLiveNewTipsJson(String newTipsJson) {
        putString(Keys.KUGOU_FX_KUGOU_LIVE_NEW_TIPS_JSON, newTipsJson);
    }

    /**
     * 播放页免费模式-挂件关闭时间
     */
    public void setPlayerFreeModeCloseTime(@IntRange(from = 0) long value) {
        putLong(Keys.PLAYER_FREEMODE_WIDGET_CLOSE_TIME, value);
    }

    /**
     * 播放页免费模式-挂件关闭时间
     */
    public long getPlayerFreeModeCloseTime() {
        return getLong(Keys.PLAYER_FREEMODE_WIDGET_CLOSE_TIME, 0);
    }

    /**
     * 播放页免费模式-挂件是否展示过动画
     */
    public void setPlayerFreeModeAnimResult(boolean hasShow) {
        putBoolean(Keys.PLAYER_FREEMODE_WIDGET_ANIM_SHOW, hasShow);
    }

    /**
     * 播放页免费模式-挂件是否展示过动画
     */
    public boolean getPlayerFreeModeAnimResult() {
        return getBoolean(Keys.PLAYER_FREEMODE_WIDGET_ANIM_SHOW, false);
    }

    /**
     * 获取酷狗live红点接口完整json字符串
     */
    public String getKugouLiveNewTipsJson() {
        return getString(Keys.KUGOU_FX_KUGOU_LIVE_NEW_TIPS_JSON, "");
    }

    /**
     * 明星在线点击后，设置明星在线推送信息
     */
    public void setStarOnlineNewTips(String newTips) {
        putString(Keys.KUGOU_FX_STAR_ONLINE_NEW_TIPS, newTips);
    }

    /**
     * 返回明星在线推送信息以判断是否显示推送信息
     */
    public String getStarOnlineNewTips() {
        return getString(Keys.KUGOU_FX_STAR_ONLINE_NEW_TIPS, "");
    }

    /**
     * 设置明星在线红点接口完整json字符串
     */
    public void setStarOnlineNewTipsJson(String newTipsJson) {
        putString(Keys.KUGOU_FX_STAR_ONLINE_NEW_TIPS_JSON, newTipsJson);
    }

    /**
     * 获取明星在线红点接口完整json字符串
     */
    public String getStarOnlineNewTipsJson() {
        return getString(Keys.KUGOU_FX_STAR_ONLINE_NEW_TIPS_JSON, "");
    }

    /**
     * 线下演出点击后，设置线下演出推送信息
     */
    public void setOfflineNewTips(String newTips) {
        putString(Keys.KUGOU_FX_OFFLINE_SHOW_NEW_TIPS, newTips);
    }

    /**
     * 返回线下演出推送信息以判断是否显示推送信息
     */
    public String getOfflineNewTips() {
        return getString(Keys.KUGOU_FX_OFFLINE_SHOW_NEW_TIPS, "");
    }

    /**
     * 设置线下演出红点接口完整json字符串
     */
    public void setOfflineNewTipsJson(String newTipsJson) {
        putString(Keys.KUGOU_FX_OFFLINE_SHOW_NEW_TIPS_JSON, newTipsJson);
    }

    /**
     * 获取线下演出红点接口完整json字符串
     */
    public String getOfflineNewTipsJson() {
        return getString(Keys.KUGOU_FX_OFFLINE_SHOW_NEW_TIPS_JSON, "");
    }

    /**
     * 获取游戏入口红点接口完整json字符串
     */
    public String getNewTipsJson() {
        return getString(Keys.KUGOU_GAME_NEW_TIPS_JSON, "");
    }

    public long getFriendListVer() {
        return getLong(Keys.FRIEND_LIST_VER, -1);
    }

    public void setFriendListVer(long ver) {
        putLong(Keys.FRIEND_LIST_VER, ver);
    }

    public long getFriendListRefreshTime() {
        return getLong(Keys.FRIEND_LIST_REFRESH_TIME, 0);
    }

    public void setFriendListRefreshTime(long time) {
        putLong(Keys.FRIEND_LIST_REFRESH_TIME, time);
    }

    public long getContactsListRefreshTime() {
        return getLong(Keys.CONTACTS_LIST_REFRESH_TIME, 0);
    }

    public void setContactsListRefreshTime(long time) {
        putLong(Keys.CONTACTS_LIST_REFRESH_TIME, time);
    }

    public void setFocusSingerListRefreshTime(long time) {
        putLong(Keys.FOCUS_SINGER_LIST_REFRESH_TIME, time);
    }

    public long getFocusSingerListRefreshTime() {
        return getLong(Keys.FOCUS_SINGER_LIST_REFRESH_TIME, 0);
    }

    public void setAppifFirstLogin(boolean flag) {
        putBoolean(Keys.If_APP_FIRST_LOGIN, flag);
    }

    public boolean getAppifFirstLogin() {
        return getBoolean(Keys.If_APP_FIRST_LOGIN, true);
    }

    public void setPullMessageFeedback(String json) {
        putString(Keys.PULL_MESSAGE_FEEDBACK, json);
    }

    public String getPullMessageFeedback() {
        return getString(Keys.PULL_MESSAGE_FEEDBACK, "");
    }

    public void setPullMessageFeedbackCanShowDot(boolean dot) {
        putBoolean(Keys.PULL_MESSAGE_FEEDBACK_CAN_SHOW_DOT, dot);
    }

    public boolean getPullMessageFeedbackCanShowDot() {
        return getBoolean(Keys.PULL_MESSAGE_FEEDBACK_CAN_SHOW_DOT, false);
    }

    public void setPullMessageFeedbackUnreadCount(boolean hasCount) {
        putBoolean(Keys.PULL_MESSAGE_FEEDBACK_UNREAD_COUNT, hasCount);
    }

    public boolean getPullMessageFeedbackUnreadCount() {
        return getBoolean(Keys.PULL_MESSAGE_FEEDBACK_UNREAD_COUNT, false);
    }

    public int getCurrentLoginType() {
        return getInt(Keys.CURRENT_LOGIN_TYPE, 1);
    }

    public void setKanDefTab(String tab) {
        putString(Keys.KAN_HOME_DEF_TAB, tab);
    }

    public String getKanDefTab() {
        return getString(Keys.KAN_HOME_DEF_TAB, "看首页/直播");
    }

    /**
     * 1 账号登录
     * 2 手机号登录
     * 3 第三方账号登录
     */
    public void setCurrentLoginType(int type) {
        putInt(Keys.CURRENT_LOGIN_TYPE, type);
    }

    /**
     * -1:默认值;0:未开通;1:开通;
     */
    public void setUserIsOpenRingtone(String phoneNumber, int open) {
        putInt(Keys.USER_IS_OPEN_RINGTONE + "-" + phoneNumber, open);
    }

    public int getUserIsOpenRingtone(String phoneNumber) {
        return getInt(Keys.USER_IS_OPEN_RINGTONE + "-" + phoneNumber, -1);
    }

    public void setUserOpenUnicomRingtone(String token, int open) {
        putInt(Keys.USER_IS_OPEN_UNICOM_RINGTONE + "-" + token, open);
    }

    public int getUserIsOpenUnicomRingtone(String token) {
        return getInt(Keys.USER_IS_OPEN_UNICOM_RINGTONE + "-" + token, 0);
    }

    public void setUserOpenTelecomRingtone(String phone, int open) {
        putInt(Keys.USER_IS_OPEN_TELECOM_RINGTONE + "-" + phone, open);
    }

    public int getUserIsOpenTelecomRingtone(String phone) {
        return getInt(Keys.USER_IS_OPEN_TELECOM_RINGTONE + "-" + phone, 0);
    }

    public String getCommRingtonePhoneImsi() {
        return getString(Keys.COMMON_CURRENT_RINGTONE_PHONE_IMSI, "");
    }

    public void setCommRingtonePhoneImsi(String currentImsi) {
        putString(Keys.COMMON_CURRENT_RINGTONE_PHONE_IMSI, currentImsi);
    }

    public void setUserStatus(int status) {
        putInt(Keys.USER_STATUS, status);
    }

    public int getUserStatus() {
        return getInt(Keys.USER_STATUS, 0);
    }

    public void setRequestContactNextTime(long timeInMills) {
        putLong(Keys.REQUEST_LOCAL_CONTACT_TIME, timeInMills);
    }

    public long getRequestContactNextTime() {
        return getLong(Keys.REQUEST_LOCAL_CONTACT_TIME, -1);
    }

    public void setShowContactPermissionDialogNextTime(long timeInMills) {
        putLong(Keys.SHOW_CONTACT_PERMISSION_DIALOG_TIME, timeInMills);
    }

    public long getShowContactPermissionDialogNextTime() {
        return getLong(Keys.SHOW_CONTACT_PERMISSION_DIALOG_TIME, -1);
    }

    public boolean getIsClickIdentfyTips() {
        return getBoolean(Keys.IS_USER_CLICK_IDENTIFY_TIPS, false);
    }

    public void setClickIdentfyTips(boolean isClickIdentfyTips) {
        putBoolean(Keys.IS_USER_CLICK_IDENTIFY_TIPS, isClickIdentfyTips);
    }

    public boolean getIsClickLongTimeModeTips() {
        return getBoolean(Keys.IS_CLICK_LONG_TIME_MODE_TIPS, false);
    }

    public void setClickLongTimeModeTips(boolean isClickLongTimeMode) {
        putBoolean(Keys.IS_CLICK_LONG_TIME_MODE_TIPS, isClickLongTimeMode);
    }

    public boolean getIsFirstDownloadMv() {
        return getBoolean(Keys.IS_FIRST_DOWNLOAD_MV, true);
    }

    public void setIsFirstDownladMv(boolean isFirst) {
        putBoolean(Keys.IS_FIRST_DOWNLOAD_MV, isFirst);
    }

    public boolean isFirstEnterDynamicShare() {
        return getBoolean(Keys.FIRST_ENTER_DYNAMIC_SHARE, true);
    }

    public void setNotFirstEnterDynamicShare() {
        putBoolean(Keys.FIRST_ENTER_DYNAMIC_SHARE, false);
    }

    public boolean haveUseDynamicShare() {
        return getBoolean(Keys.HAVE_USE_DYNAMIC_SHARE, false);
    }

    public void setHaveUseDynamicShare() {
        putBoolean(Keys.HAVE_USE_DYNAMIC_SHARE, true);
    }

    public String getParentalPatternPwd() {
        return getString(Keys.PARENTAL_PATTERN_PWD, "");
    }

    public void setParentalPatternPwd(String pwd) {
        putString(Keys.PARENTAL_PATTERN_PWD, pwd);
    }

    public String getParentalPatternSourcePwd() {
        return getString(Keys.PARENTAL_PATTERN_PWD_SOURCE, "");
    }

    public void setParentalPatternSourcePwd(String pwd) {
        putString(Keys.PARENTAL_PATTERN_PWD_SOURCE, pwd);
    }

    public boolean setParentalPatternSetDate(long date) {
        return putLong(Keys.PARENTAL_PATTERN_SET_PWD_TIME, date);
    }

    public long getParentalPatternSetDate() {
        return getLong(Keys.PARENTAL_PATTERN_SET_PWD_TIME, -1);
    }

    public boolean setParentalPatternPwdPeriod(int period) {
        return putInt(Keys.PARENTAL_PATTERN_PWD_PERIOD, period);
    }

    public int getParentalPatternPwdPeriod() {
        return getInt(Keys.PARENTAL_PATTERN_PWD_PERIOD, -2);
    }

    public boolean isShowSearchSongTypeGuide() {
        return getBoolean(Keys.IS_SHOW_SEARCH_SONG_TYPE_GUIDE, false);
    }

    public void setIsShowSearchSongTypeGuide(boolean isShowed) {
        putBoolean(Keys.IS_SHOW_SEARCH_SONG_TYPE_GUIDE, isShowed);
    }

    public int getYearVipOrMusicType() {
        return getInt(Keys.YEAR_VOM_TYPE, 0);
    }

    public int getSVIPLevelType() {
        return getInt(Keys.SVIP_LEVEL_TYPE, 0);
    }

    public int getSVIPScoreType() {
        return getInt(Keys.SVIP_SCORE_TYPE, 0);
    }

    public boolean hasSaveSingerNameToDB() {
        return getBoolean(Keys.HAS_SAVE_SINGER_NAME_TO_DB, false);
    }

    public void setAlreadySaveSingerNameToDB(boolean result) {
        putBoolean(Keys.HAS_SAVE_SINGER_NAME_TO_DB, result);
    }

    public void saveLastUpdateSingerNameTime(
        @IntRange(from = 0, to = Long.MAX_VALUE) long timeMillis) {
        putLong(Keys.LAST_REQ_SINGER_NAME_TIME, timeMillis);
    }

    public long getLastUpdateSingerNameTime() {
        return getLong(Keys.LAST_REQ_SINGER_NAME_TIME, 0);
    }

    public int getSaveSingerNameVersion() {
        return getInt(Keys.SAVE_SINGER_NAME_VERSION, 0);
    }

    public void setSaveSingerNameVersion(@IntRange(from = 0, to = Integer.MAX_VALUE) int version) {
        putInt(Keys.SAVE_SINGER_NAME_VERSION, version);
    }

    public void setPreVersion(int version) {
        putInt(Keys.IS_RESUME_WIFI_DOWNLOAD_VERSION, version);
    }

    public int getPreVersion() {
        return getInt(Keys.IS_RESUME_WIFI_DOWNLOAD_VERSION, -1);
    }

    public void setStoreageStatus(boolean isRight) {
        putBoolean(Keys.IS_RESUME_WIFI_DOWNLOAD_STOREAGE_IS_RIGHT, isRight);
    }

    public boolean getStoreageStatus() {
        return getBoolean(Keys.IS_RESUME_WIFI_DOWNLOAD_STOREAGE_IS_RIGHT, true);
    }

    public String getKuqunActiveInfoStr() {
        return getString(Keys.KUQUN_ACTIVE_INFO, "");
    }

    public void setKuqunActiveInfo(String activeInfo) {
        putString(Keys.KUQUN_ACTIVE_INFO, activeInfo);
    }

    public void setLocalMusicQualityFilter(String qualityFilter) {
        putString(Keys.LOCAL_MUSIC_QUALITY_FILTER, qualityFilter);
    }

    public String getLocalMusicQualityFilter() {
        return getString(Keys.LOCAL_MUSIC_QUALITY_FILTER, "");
    }

    public void setSaveLocalMusicFilter(boolean isSave) {
        putBoolean(Keys.SAVE_LOCAL_MUSIC_FILTER, isSave);
    }

    public boolean isSaveLocalMusicFilter() {
        return getBoolean(Keys.SAVE_LOCAL_MUSIC_FILTER, false);
    }

    public void setSaveLocalMusicFilterTip(boolean isSave) {
        putBoolean(Keys.SAVE_LOCAL_MUSIC_FILTER_TIP_DIALOG, isSave);
    }

    public boolean isSaveLocalMusicFilterTipShowed() {
        return getBoolean(Keys.SAVE_LOCAL_MUSIC_FILTER_TIP_DIALOG, false);
    }

    public void setLocalMusicBpmFilter(String bpmFilter) {
        putString(Keys.LOCAL_MUSIC_BPM_FILTER, bpmFilter);
    }

    public String getLocalMusicBpmFilter() {
        return getString(Keys.LOCAL_MUSIC_BPM_FILTER, "");
    }

    public void setLocalMusicGenreFilter(String genreFilter) {
        putString(Keys.LOCAL_MUSIC_GENRE_FILTER, genreFilter);
    }

    public String getLocalMusicGenreFilter() {
        return getString(Keys.LOCAL_MUSIC_GENRE_FILTER, "");
    }

    public void setLocalMusicRecentListenFilter(String genreFilter) {
        putString(Keys.LOCAL_MUSIC_RECENT_LISTEN_FILTER, genreFilter);
    }

    public String getLocalMusicRecentListenFilter() {
        return getString(Keys.LOCAL_MUSIC_RECENT_LISTEN_FILTER, "");
    }

    public void setLocalMusicMoreFilter(String genreFilter) {
        putString(Keys.LOCAL_MUSIC_MORE_FILTER, genreFilter);
    }

    public String getLocalMusicMoreFilter() {
        return getString(Keys.LOCAL_MUSIC_MORE_FILTER, "");
    }

    public long getMediastorePaidRecordLastUpdateTime(long userId) {
        return getLong(Keys.MEDIASTORE_PAID_RECORD_LAST_UPDATE_TIME + userId, 0);
    }

    public void setMediastorePaidRecordLastUpdateTime(long userId, long time) {
        putLong(Keys.MEDIASTORE_PAID_RECORD_LAST_UPDATE_TIME + userId, time);
    }

    public long getLastReportLocalMatchStatusTime() {
        return getLong(Keys.KEY_LOCAL_MUSIC_MATCH_STATUS_LAST_REPORT_TIME, 0);
    }

    public void setLastReportLocalMatchStatusTime(long time) {
        putLong(Keys.KEY_LOCAL_MUSIC_MATCH_STATUS_LAST_REPORT_TIME, time);
    }

    public long getLastShowLimitedFreeBarTime() {
        return getLong(Keys.LAST_SHOW_LIMITED_FREE_BAR_TIME, 0);
    }

    public void setLastShowLimitedFreeBarTime(long time) {
        putLong(Keys.LAST_SHOW_LIMITED_FREE_BAR_TIME, time);
    }

    public long getFeeStrengthenDialogLastShowTime() {
        return getLong(Keys.FEE_STRENGTHEN_DIALOG_LAST_SHOW_TIME, 0);
    }

    public void setFeeStrengthenDialogLastShowTime(long time) {
        putLong(Keys.FEE_STRENGTHEN_DIALOG_LAST_SHOW_TIME, time);
    }

    public long getFeeConfigLastUpdateTime() {
        return getLong(Keys.FEE_CONFIG_LAST_UPDATE_TIME, 0);
    }

    public void setFeeConfigLastUpdateTime(long time) {
        putLong(Keys.FEE_CONFIG_LAST_UPDATE_TIME, time);
    }

    public long getLastUpdateFeeCacheExpirationTime() {
        return getLong(Keys.LAST_UPDATE_FEE_CACHE_EXPIRATION_TIME, 0);
    }

    public void setLastUpdateFeeCacheExpirationTime(long time) {
        putLong(Keys.LAST_UPDATE_FEE_CACHE_EXPIRATION_TIME, time);
    }

    public void setLastTimeStamp(long currentTimeMillis) {
        putLong(Keys.LAST_RECOMMEND_VIDEO_DIALOG_SHOW_TIME, currentTimeMillis);
    }

    public void setAllowedCheckVipGradeFreeListenAuth(boolean allowed) {
        putBoolean(Keys.ALLOWED_CHECK_VIP_GRADE_FREE_LISTEN_AUTH, allowed);
    }

    public boolean allowedCheckVipGradeFreeListenAuth() {
        return getBoolean(Keys.ALLOWED_CHECK_VIP_GRADE_FREE_LISTEN_AUTH, false);
    }

    public long getLastTimeStamp() {
        return getLong(Keys.LAST_RECOMMEND_VIDEO_DIALOG_SHOW_TIME, 0);
    }

    public int getVideoDialogCount() {
        return getInt(Keys.RINGTONE_RECOMMEND_VIDEO_DIALOG_COUNT, 0);
    }

    public int getVideoDialogType() {
        return getInt(Keys.RINGTONE_RECOMMEND_VIDEO_DIALOG_TYPE, 0);
    }

    public void setVideoDialogType(int type) {
        putInt(Keys.RINGTONE_RECOMMEND_VIDEO_DIALOG_TYPE, type);
    }

    public void setPlayerColorTye(int type) {
        putInt(Keys.PLAYER_COLOR_TYPE, type);
    }

    public int getPlayercolorType() {
        return getInt(Keys.PLAYER_COLOR_TYPE, 5);
    }

    public void setPlayerColorNearType(boolean type) {
        putBoolean(Keys.PLAYER_COLOR_NEAR_TYPE, type);
    }

    public boolean getPlayerColorNearType() {
        return getBoolean(Keys.PLAYER_COLOR_NEAR_TYPE, false);
    }

    public int getAdapterSystemDarkModeState() {
        return getInt(Keys.KEY_ADAPTER_SYSTEM_DARK_MODE_STATE, -1);
    }

    public void setAdaptSystemDarkModeState(int state) {
        putInt(Keys.KEY_ADAPTER_SYSTEM_DARK_MODE_STATE, state);
    }

    public void setDefaultSimpleAdaptSystemDarkInUse(boolean inUse) {
        putBoolean(Keys.KEY_DEFAULT_SIMPLE_ADAPT_SYSTEM_DARK, inUse);
    }

    public boolean isDefaultSimpleAdaptSystemDarkInUse() {
        return getBoolean(Keys.KEY_DEFAULT_SIMPLE_ADAPT_SYSTEM_DARK, false);
    }

    public interface Keys {

        String KEY_ADAPTER_SYSTEM_DARK_MODE_STATE = "key_adapter_system_dark_mode_state";

        String KEY_DEFAULT_SIMPLE_ADAPT_SYSTEM_DARK = "key_default_simple_adapt_system_dark";

        String LAST_RECOMMEND_VIDEO_DIALOG_SHOW_TIME = "last_recommend_video_dialog_show_time";

        String LAST_UPDATE_FEE_CACHE_EXPIRATION_TIME = "last_update_fee_cache_expiration_time";

        String FEE_CONFIG_LAST_UPDATE_TIME = "fee_config_last_update_time";

        String FEE_STRENGTHEN_DIALOG_LAST_SHOW_TIME = "fee_strengthen_dialog_last_show_time";

        String MEDIASTORE_PAID_RECORD_LAST_UPDATE_TIME = "mediastore_paid_record_last_update_time";

        String MEDIASTORE_PAID_RECORD = "mediastore_paid_record";

        String LAST_SHOW_LIMITED_FREE_BAR_TIME = "last_show_limited_free_bar_time";

        String FIRST_ENTER_DYNAMIC_SHARE = "first_enter_dynamic_share";

        String HAVE_USE_DYNAMIC_SHARE = "have_use_dynamic_share";

        String DEFAULT_USER_IMG_VERSION = "default_user_img_version";

        String KEY_KG_RUN_STEP_LENGTH = "key_kg_run_step_length";
        String KEY_KG_RUN_USED_BPM = "key_kg_run_used_bpm";
        String IS_NEW_INSTALL = "isNewInstall";

        String VERSION_CODE = "version_code";

        String KG_MUSICAL_NOTE_GOLD_COIN_TASK_TOTAL_DONE_COUNT =
            "kg_musical_note_gold_coin_task_total_done_count";

        String KG_SHOW_SHORT_VIDEO_TIP_DIALOG = "kg_show_shortv_ideo_tip_dialog";

        String KEY_AUDIO_AD_UA = "audio_ad_ua";

        String KUGOU_PID = "kugou_pid";

        String KUGOU_PID_BACKPROCESS = "kugou_pid_backprocess";

        String IS_EXIT_APP_KEY = "is_exit_app_key";

        String ALLOWED_CHECK_VIP_GRADE_FREE_LISTEN_AUTH =
            "allowed_check_vip_grade_free_listen_auth";

        String VIDEO_PLAY_TYPE = "video_play_type";

        String USER_SEX = "user_sex";

        String USER_DATA_JSON = "user_data_json";

        String VIP_INFO_JSON = "vip_info_json";

        String MUSICAL_NOTE_ACCOUNT = "musical_note_account";

        String USED_SKIN_NAME = "used_skin_name";

        String USED_VIP_SKIN_NAME = "used_vip_skin_name";

        String USED_SKIN_FILE_MD5_ID = "used_skin_file_md5_id";

        String IS_SKIN_PATH_UPDATE_DONE = "is_skin_path_update_done";

        String IS_UPDATE_SKIN_PRIVILEGE = "is_update_skin_privilege";

        String SKIN_STORE_PATH_BACKUP = "skin_store_path_backup";

        String LAST_CLICK_AI_REC = "last_click_ai_rec";

        String STUDENT_INDENTIFY_URL = "student_indentify_url";

        String SHOW_SKIN_UPDATE_DIALOG = "show_skin_update_dialog";

        String USER_FAN_BADGE_SINGER_ID = "user_fan_badge_singer_id";

        String USER_FAN_BADGE_URL = "user_fan_badge_url";

        String HAS_SHOW_IMPORT_PLAYLIST_DIALOG = "has_show_import_playlist_dialog";

        String NEW_START_UP_DAYS = "new_start_up_days";

        String HAS_UPDATE_FAV_PLAYLIST_COVER = "has_update_fav_playlist_cover";

        String FAV_PLAYLIST_COVER_MIXID = "fav_playlist_cover_mixid";

        String LOCAL_NEED_REFRESH_PLAYLIST_COVER = "local_need_refresh_playlist_cover";

        String MINI_GAME_ASSISTANT_DELETE_MSG_ID = "mini_game_assistant_delete_msg_id";

        String MINI_APP_MY_TAB_ENTRANCE_CLICK = "mini_app_my_tab_entrance_click";

        String MINI_APP_MY_TAB_GUIDE_ENTRANCE_CLICK = "mini_app_my_tab_guide_entrance_click";

        String HAS_SHOW_MINIAPP_INTRO = "has_show_miniapp_intro";

        String HAS_SHOW_MODE_SWITCH = "has_show_mode_switch_";

        String HAS_SHOW_MODE_SWITCH_TIP = "has_show_tip_mode_switch";

        String MSG_CHATSET_NO_CHAT_SELECTED = "msg_chatset_no_chat_selected";

        String MSG_CHAT_SETTING_TIME = "msg_chat_setting_time";

        public final static String USER_ID = "userid";

        public final static String USER_SIGNATURE = "signature";

        public final static String USER_NAME = "loveusername";

        public final static String TRUE_NAME = "user_true_name";

        public final static String USER_NICK_NAME = "love_login_nick_name";

        public final static String USER_SIGN = "lovesign";

        public final static String USER_IS_VIP = "loveisvip";

        public final static String BIND_MAIL = "user_bind_mail";

        public final static String BIND_PHONE = "user_bind_phone";

        public final static String SCORE = "user_score";

        public final static String VIP_BEGIN_TIME = "user_vip_begin_time";

        public final static String VIP_END_TIME = "user_vip_end_time";

        public final static String REG_TIME = "user_reg_time";

        public final static String PROVINCE = "user_province";

        public final static String CITY = "user_city";

        public final static String MEMO = "user_memo";

        public final static String SIGNATURE = "user_signature";

        public final static String TAGS = "user_tags";

        public final static String LAST_LOGIN_TIME = "user_last_login_time";

        public final static String BIRTHDAY = "user_birthday";

        public final static String USER_BIRTHDAY_MMDD = "user_birthday_mmdd";

        public final static String USER_BIRTHDAY_YYYYMMDD = "user_birthday_yyyyMMdd";

        public final static String VIP_TYPE = "user_vip_type";

        public final static String VIP_CLEARDAY = "user_vip_clearday";

        public final static String SECURITY_EMAIL = "user_security_email";

        // public final static String LOGIN_EMAIL = "user_login_email";
        //
        // public final static String LOGIN_MOBILE = "user_login_mobile";

        public final static String MUSIC_TYPE = "user_music_type";

        public final static String MUSIC_BEGIN_TIME = "user_music_begin_time";

        public final static String MUSIC_END_TIME = "user_music_end_time";

        public final static String ROAM_TYPE = "user_roam_type";

        public final static String ROAM_BEGIN_TIME = "user_roam_begin_time";

        public final static String ROAM_END_TIME = "user_roam_end_time";

        public final static String MUSIC_RESET_TIME = "user_music_reset_time";
        public final static String M_IS_OLD = "user_m_is_old";

        public final static String QUESTION_ID = "user_question_id";

        public final static String SERVERTIME = "user_servertime";

        public final static String LOCAL_VIP_TYPE = "localviptype"; // 本地VIP类型
        // 用于离线登录显示

        public final static String USER_HANDSEL_VIP = "userhandselvip_request";

        public final static String USER_AUTO_LOGIN = "isLoveAutoLogin";

        public final static String USER_REMEMBER_PWD = "isLoveRememberPwd";

        // 游戏红点从SettingPref移到SettingPrefs
        public static final String KUGOU_GAME_NEW_TIPS = "kugou_game_new_tips";

        public static final String KUGOU_FX_KUGOU_LIVE_NEW_TIPS = "kugou_fx_kugou_live_new_tips";

        public static final String KUGOU_FX_KUGOU_LIVE_NEW_TIPS_JSON =
            "kugou_fx_kugou_live_new_tips_json";

        public static final String PLAYER_FREEMODE_WIDGET_CLOSE_TIME =
            "player_freemode_widget_close_time";

        public static final String PLAYER_FREEMODE_WIDGET_ANIM_SHOW =
            "player_freemode_widget_anim_show";

        public static final String KUGOU_FX_STAR_ONLINE_NEW_TIPS = "kugou_fx_star_online_new_tips";

        public static final String KUGOU_FX_STAR_ONLINE_NEW_TIPS_JSON =
            "kugou_fx_star_online_new_tips_json";

        public static final String KUGOU_FX_OFFLINE_SHOW_NEW_TIPS =
            "kugou_fx_offline_show_new_tips";

        public static final String KUGOU_FX_OFFLINE_SHOW_NEW_TIPS_JSON =
            "kugou_fx_offline_show_new_tips_json";

        public final static String USER_PASSWORD = "lovepassword";

        public final static String USER_IMAGE_URL = "user_image_url";

        public final static String USER_IMAGE_SAVE_PATH = "user_image_save_path";

        public final static String CLOUD_PLAYLIST_VER = "loveplaylistver";

        public final static String CLOUD_PLAYLIST_MAX_COUNT = "cloud_playlist_max_count";   //歌单上限

        public final static String CLOUD_PLAYLIST_MAX_MUSIC_COUNT =
            "cloud_playlist_max_music_count";   //单个歌单歌曲数量上限,除了我喜欢歌单

        public final static String CLOUD_FAV_PLAYLIST_MAX_MUSIC_COUNT =
            "cloud_fav_playlist_max_music_count";   //“我喜欢”歌单歌曲数量上限

        public final static String CLOUD_PROTOCOL_PER_LIST_PAGE_SIZE =
            "cloud_protocol_per_list_page_size"; //  拉取歌单列表的单页个数

        public final static String HAS_CLOUD_SYNC_SUCCEEDED = "has_cloud_sync_succeeded";

        public final static String CLOUD_FAV_FAIL_REASION = "cloud_fav_fail_reasion";

        public final static String MUSIC_CLOUD_VER = "music_cloud_ver";
        public final static String MUSIC_CLOUD_MAX_SIZE = "music_cloud_max_size";
        public final static String MUSIC_CLOUD_LIST_COUNT = "music_cloud_list_count";
        public final static String MUSIC_CLOUD_AVAILBLE_SIZE = "music_cloud_availble_size";
        public final static String MUSIC_CLOUD_UPLOAD_MAX_SIZE = "music_cloud_upload_max_size";
        public final static String MUSIC_CLOUD_UPLOAD_MIN_DURATION =
            "music_cloud_upload_min_duration";
        public final static String MUSIC_CLOUD_UPLOAD_MAX_DURATION =
            "music_cloud_upload_max_duration";

        //定时清理
        public final static String AUTO_CLEAN_CACHE_PERIOD = "auto_clean_cache_period"; //周期
        public final static String CLEAN_CACHE_TIME = "clean_cache_time";   //执行清除缓存的时间戳

        public final static String MUSIC_CLOUD_USER_TYPE = "music_cloud_user_type";
            //用户类型: 0-普通用户,　1-老用户(付费上线前云盘歌曲>0), 2-豪华vip

        public final static String MUSIC_CLOUD_NORMAL_USER_MAX_SIZE =
            "music_cloud_normal_user_max_size";   //普通用户最大可用容量

        public final static String MUSIC_CLOUD_OLD_USER_MAX_SIZE = "music_cloud_old_user_max_size";
            //老用户最大可用容量

        public final static String MUSIC_CLOUD_SVIP_USER_MAX_SIZE =
            "music_cloud_svip_user_max_size";   //豪华VIP用户最大可用容量

        public final static String MUSIC_CLOUD_USED_SIZE = "music_cloud_used_size"; //用户已用云盘容量

        public final static String USER_THIRD_PLATFORM = "user_third_platform";

        public final static String INSTALL_UUID = "install_uuid";

        public final static String RINGTONE_USER_VAILTIME = "ringtone_user_vailtime";

        public final static String IS_DOWNLOAD_ALIPAY_SUCCESS = "is_download_alipay_success";

        public final static String IS_DOWNLOAD_UNIONPAY_SUCCESS = "is_download_unionpay_success";

        public final static String IS_DOWNLOAD_X86_CODE = "is_download_x86code";

        public final static String X86_VERSION_CODE = "x86_version";

        public final static String X86_DOWNLOADING = "x86_downloading";

        public final static String X86_INTALL_VERSION = "x86_install_version";

        public final static String APP_UUID = "app_uuid";

        // 炫铃token是否失效
        public final static String RINGTON_UNC_TOKEN_IS_FAILURE = "RINGTON_UNC_TOKEN_IS_FAILURE";

        // 下载失败切换的sdcard--缓存目录所在的sdcard
        public final static String LAST_SWITCH_SDCARD_ROOT = "last_switch_sdcard_root_path";

        public final static String ENABLED_DOWNLOAD_SHORTNAME = "enabled_download_shortname";

        // 缓存加密的默认 KEY（防止没有网络的时候无法播放加密缓存）
        public final static String TEMP_CACHE_ID = "temp_cache_id";

        public final static String TRANSFER_PEER_ID = "transfer_peer_id";

        public final static String P2P_DIFFUSION_TIME = "diffusion_time";

        public final static String MEDIA_STORE_LAST_VIP_REMAIN_NUM =
            "media_store_last_vip_remain_num";

        public final static String MEDIA_STORE_DAY_LIMIT_QUOTA = "media_store_day_limit_quota";
        public final static String MEDIA_STORE_TOTAL_LIMIT_QUOTA = "media_store_total_limit_quota";

        public final static String WALLET_LAST_USER_BALANCE_NUM = "wallet_user_last_balance_num";

        public final static String HAS_CLEAR_RECENT_PLAYLIST_RECORD =
            "has_clear_recent_playlist_record";

        /**
         * 当前是否为流量保护状态
         */
        public final static String IS_TRAFFIC_PROCTED = "is_traffic_proected";

        /**
         * 歌手关注后通知弹窗状态
         */
        public final static String IS_NOTIFICATION_DIALOG_SHOWED = "is_notification_dialog_showed";

        /**
         * 是否桌面控件启动了流量弹窗开关
         */
        public final static String IS_CLOSE_TRAFFIC_PROCTED_BY_WIDGET =
            "is_close_traffice_procted_by_widget";

        public final static String GAME_CENTER_SHORTCUT_ONCE_CREATE =
            "game_center_shortcut_once_create";

        /**
         * 用户加载看模块的时间点
         */
        public final static String LOADING_FANXING_TIME_STAMP = "loading_fanxing_time_stamp";

        /**
         * 定时结束是否播完整首歌再停止
         */
        public final static String IS_STOP_AFTER_PLAYED = "is_stop_after_played";

        /**
         * 用户加载唱模块的时间点
         */
        public final static String LOADING_KTV_TIME_STAMP = "loading_ktv_time_stamp";

        /**
         * 用户加载玩模块的时间点
         */
        public final static String LOADING_GAME_TIME_STAMP = "loading_game_time_stamp";

        /**
         * 用户触发酷群活跃用户的时间点
         */
        public final static String LOADING_KUQUN_TIME_STAMP = "loading_kuqun_time_stamp";

        /**
         * 定时结束后动作
         */
        public final static String TO_DO_AFTER_TIMING = "to_do_after_timing";

        public final static String ALARM_SELECT_CUSTOM_TIME = "alarm_select_custom_time";

        // 游戏入口红点接口完整json字符串
        public final static String KUGOU_GAME_NEW_TIPS_JSON = "kugou_game_new_tips_json";

        // 第三方账号首次登录获取到的头像
        public final static String KUGOU_3RD_LOGIN_HEAD_PHOTO = "kugou_3rd_login_head_photo";

        // 第三方账号首次登录获取到的昵称
        public final static String KUGOU_3RD_LOGIN_NICKNAME = "kugou_3rd_login_nickname";

        public final static String USER_PERSONAL_INFO = "user_personal_info";

        public final static String FOLLOW_LIST_VER = "follow_list_ver_v2";
        public final static String FOLLOW_LIST_SINGER_SRC_VER = "follow_list_singer_src_ver";
        public final static String FRIEND_LIST_VER = "friend_list_ver";
        public final static String FOLLOW_LIST_TOTAL_COUNT = "follow_list_total_count";
        public final static String FAN_LIST_TOTAL_COUNT = "fan_list_total_count";

        public final static String FOLLOW_LIST_REFRESH_TIME = "follow_list_refresh_time";
        public final static String FRIEND_LIST_REFRESH_TIME = "friend_list_refresh_time";

        public final static String FOLLOW_LIST_SHOW_REC_FRIEND = "follow_list_show_rec_friend";
        public final static String FOLLOW_LIST_SHOW_SINGER = "follow_list_show_singer";
        public final static String FOLLOW_LIST_SHOW_STAR = "follow_list_show_star";
        public final static String FOLLOW_LIST_SHOW_USER = "follow_list_show_user";

        public final static String FOCUS_SINGER_LIST_VER = "focus_singer_list_ver";
        public final static String FOCUS_SINGER_LIST_REFRESH_TIME =
            "focus_singer_list_refresh_list";
        public final static String If_APP_FIRST_LOGIN = "if_app_first_login";

        public final static String NEW_SONG_SHOW_TIME = "new_song_show_time_";
        public final static String NEW_SONG_DO_NOT_REMIND = "new_song__do_not_remind";

        public final static String MUSICIAN_SONG_REMAIN_SHOW_TIME =
            "musician_song_remain_show_time_";

        public final static String PULL_MESSAGE_FEEDBACK = "pull_message_feedback";
        public final static String PULL_MESSAGE_FEEDBACK_CAN_SHOW_DOT =
            "pull_message_feedback_can_show_dot";
        public final static String PULL_MESSAGE_FEEDBACK_UNREAD_COUNT =
            "pull_message_feedback_unread_count";

        public final static String UNREAD_MSG_COUNT = "unread_msg_count";
        public final static String CACHE_GRADE_INFO = "cache_grade_info";
        public final static String CACHE_FINISH_PERENCT_INFO = "cache_finish_perenct_info";
        public final static String UNREAD_MSG_COUNT_WITHOUT_COMMENT_COUNT =
            "unread_msg_count_without_comment_count";

        //通讯录刷新时间
        public final static String CONTACTS_LIST_REFRESH_TIME = "contacts_list_refresh_time";

        public final static String CURRENT_LOGIN_TYPE = "current_login_type";

        public final static String KAN_HOME_DEF_TAB = "kan_home_def_tab";

        /**
         * 用户是否开通过彩铃;
         */
        public final static String USER_IS_OPEN_RINGTONE = "user_is_open_ringtone";

        public final static String USER_IS_OPEN_UNICOM_RINGTONE = "user_is_open_unicom_ringtone";
        public final static String USER_IS_OPEN_TELECOM_RINGTONE = "user_is_open_telecom_ringtone";
        public final static String COMMON_CURRENT_RINGTONE_PHONE_IMSI =
            "common_current_ringtone_phone_imsi";

        /**
         * 用戶状态
         */
        public final static String USER_STATUS = "user_status";

        String HAS_SHOW_CONTACT_NAME = "has_show_contact_name";

        String CONTACT_NEXT_FULL_PULL_TIME = "contact_next_full_pull_time";

        String CONTACT_MAIN_PAGE_NEXT_SHOW_TIME = "contact_main_page_next_show_time";

        String REQUEST_LOCAL_CONTACT_TIME = "contact_load_local_time";

        String SHOW_CONTACT_PERMISSION_DIALOG_TIME = "show_contact_permission_dialog_time";

        String SKIN_CUSTOM_VERSION_KEY_ACACHE = "skin_custom_version_key_acache";

        /**
         * 专区收藏数量版本号
         */
        String KEY_VERSION_FAV_COUNT_SPECIAL_AREA = "key_version_fav_count_special_area";
        /**
         * 专区收藏数量
         */
        String KEY_FAV_COUNT_SPECIAL_AREA = "key_fav_count_special_area";

        /**
         * IP标签收藏数量版本号
         */
        String KEY_VERSION_FAV_COUNT_SONG_IP_TAG = "key_version_fav_count_song_ip_tag";
        /**
         * IP标签收藏数量
         */
        String KEY_FAV_COUNT_SONG_IP_TAG = "key_fav_count_song_ip_tag";

        /**
         * 关注MV版本号
         */
        public final static String FOCUS_MV_LIST_VER = "focus_mv_list_ver";

        public final static String IS_FIRST_DOWNLOAD_MV = "is_first_download_mv";

        public final static String IS_SYNC_FOCUS_MV_LIST_DONE = "is_sync_focus_mv_list_done";
            //本地该账号是否有成功同步过关注MV

        public final static String IS_SYNC_FOCUS_SINGER_LIST_DONE =
            "is_sync_focus_singer_list_done";   //本地该账号是否有成功同步过关注歌手
        public final static String IS_SYNC_FOCUS_PROGRAM_LIST_DONE =
            "is_sync_focus_program_list_done";   //本地该账号是否有成功同步过关注有声电台
        /**
         * 青少年模式
         */
        public final static String PARENTAL_PATTERN_PWD = "parental_pattern_pwd";   //青少年模式密码，原家长模式

        public final static String PARENTAL_PATTERN_PWD_SOURCE = "feedback_extra_info";
            //青少年模式原始密码，用户反馈用的，防止人家忘记密码。变形

        public final static String PARENTAL_PATTERN_SET_PWD_TIME = "parental_pattern_set_pwd_time";
            //设置密码的日期

        public final static String PARENTAL_PATTERN_PWD_PERIOD = "parental_pattern_pwd_period";
            //设置密码的有效期

        /**
         * 是否显示搜索歌曲类型的引导
         */
        String IS_SHOW_SEARCH_SONG_TYPE_GUIDE = "is_show_search_song_type_guide";

        public static final String HAS_SAVE_SINGER_NAME_TO_DB = "has_save_singer_name_to_db";
        String LAST_REQ_SINGER_NAME_TIME = "last_req_singer_name_time"; // 最近一次请求歌手库接口更新歌手库的时间

        String SAVE_SINGER_NAME_VERSION = "save_singer_name_version";

        String UPDATE_SINGER_NAME_VERSION = "update_singer_name_version";

        String LAST_LOCAL_SINGER_AVATR_GET_TIME = "last_local_singer_avatar_get_time";

        String SVIP_SCORE_TYPE = "svip_score_type";

        String SVIP_LEVEL_TYPE = "svip_level_type";

        String SUPER_VIP_EXPAND_INFO = "super_vip_expand_info";

        public final static String YEAR_VOM_TYPE = "year_vom_type";

        public final static String IS_USER_CLICK_IDENTIFY_TIPS = "is_user_click_indentify_tips";

        public final static String IS_CLICK_LONG_TIME_MODE_TIPS = "is_click_long_time_tips";

        String USER_STAR_VIP_STATUS = "user_star_vip_status";

        String KG_USER_IDEN = "kg_user_iden";

        String USER_STAR_VIP_AUTH_INFO = "user_star_vip_auth_info";

        String USER_STAR_ID = "user_star_id";

        String CONTACT_PERMISSION_GRANTED_FLAG = "contact_permission_granted_flag";

        String USER_BIZ_STATUS = "user_biz_status";

        String USER_TALENT_STATUS = "user_talent_status";

        String CACHE_DEVICE_ID_KEY = "cache_device_id_key";
        String CACHE_DEVICE_ID_VERSION_KEY = "cache_device_id_version_key";

        String CACHE_MACHINE_ID_KEY = "cache_machine_id_key";
        // 共享库抽样数据
        String SHARE_LIBRARY_SAMPLING_DATE = "share_library_sampling_date";
        String SHARE_LIBRARY_IS_PICKED = "share_library_is_picked";
        String SHARE_LIBRARY_HASH_REPORT_IS_PICKED = "share_library_hash_report_is_picked";
        String SHARE_LIBRARY_NATPROXY_IS_PICKED = "share_library_natproxy_is_picked";
        String SHARE_LIBRARY_P2P_UPLOAD_IS_PICKED = "share_library_p2p_upload_is_picked";

        // 灰度测试用，已废弃
        String P2P_PUSH_MODE_VIRTUAL_HASH_JOB = "p2p_push_mode_virtual_hash_job";

        String IS_RESUME_WIFI_DOWNLOAD_VERSION = "is_resume_wifi_download";

        String IS_RESUME_WIFI_DOWNLOAD_STOREAGE_IS_RIGHT =
            "is_resume_wifi_download_storeage_is_right";

        String HAS_ALBUMINFO_IN_MUSICCLOUDDAO = "has_albuminfo_in_musicclouddao";

        String HAS_NEWMIXID_IN_MUSICCLOUDDAO = "has_new_mix_id_in_musicclouddao";

        String LOCAL_SOLID_SKIN_COLOR_NAME = "local_solid_skin_color_name";

        /**
         * 自定义皮肤毛玻璃开关，对播放bar无效
         */
        String IS_CUSTOM_SKIN_BLUR = "is_custom_skin_blur";

        String CUSTOM_SKIN_COLOR_NAME = "custom_skin_color_name";

        String USER_TME_STAR_VIP_STATUS = "user_tme_star_vip_status";
        String USER_TME_STAR_ACTOR_STATUS = "user_tme_star_actor_status";

        String USER_SINGER_STATUS = "user_singer_status";
        String USER_INFO_IDEN = "user_info_iden";

        String LAST_THEME_PROTOCOL_TIME = "last_theme_protocol_time";   //上一次皮肤接口的成功请求时间
        String HAS_SHOW_SINGER_DETAIL_SKIN_ENTRY_GUIDE = "has_show_singer_detail_skin_entry_guide";
            //是否显示了引导图3
        String HAS_SHOW_SINGER_DETAIL_QA_ENTRY_GUIDE = "has_show_singer_detail_qa_entry_guide";
            //是否显示了引导图2
        String HAS_SHOW_SINGER_DETAIL_ENERGY_ENTRY_GUIDE =
            "has_show_singer_detail_energy_entry_guide"; //是否显示了引导图1
        String HAS_SHOW_DISCOVERY_SINGER_ENTRY_GUIDE = "has_show_discovery_singer_entry_guide";
            //乐库歌手
        String HAS_SHOW_SINGER_DETAIL_ENERGY_ENTRY_TIP_TASK =
            "has_show_singer_detail_energy_entry_tip"; //能量榜气泡

        String LAST_VISIT_TIME = "last_visit_time"; //访客页上次查看的时间
        String LAST_VISIT_COUNT_REQUEST_TIME = "last_visit_count_request_time"; //上次请求访客人数的时间

        String HAS_CLOSE_MUSICIAN_TAB_RECOMMEND = "has_close_musician_tab_recommend";
        String HAS_CLOSE_MUSICIAN_TAB_SONG_SALE_RECOMMEND =
            "has_close_musician_tab_song_sale_recommend";

        String HAS_SHOW_SLIDE_KG_CONCERT = "has_show_slide_kg_concert"; //侧边栏演出
        String HAS_SHOW_SLIDE_ALBUM_STORE = "has_show_slide_album_store"; //侧边栏专辑

        String HAS_SINGER_SONG_TIP = "has_singer_song_tip_h5";
        String HAS_SHOW_SINGER_SONG_STICK_NEW = "has_show_singer_song_stick_new";
        /**
         * 记录酷群每天打开次数，定向灰度使用
         */
        String KUQUN_ACTIVE_INFO = "kuqun_active_info";

        String IS_OPEN_LYRIC_MAKER_VIBRATE = "is_open_lyric_maker_vibrate";

        String LOCAL_MUSIC_NUM = "local_music_num";
        String LOCAL_COMPETING_PRODUCT_MUSIC_NUM = "local_competing_product_music_num";
        String LOCAL_OTHER_MUSIC_FOLDER_INFO = "local_other_music_folder_info";

        String LYRIC_NEW_FLAG = "lyric_new";

        String VIDEO_RECORD_LAST_PREVIEW_CAMERA_ID = "video_record_last_preview_camera_id";
        String VIDEO_RECORD_FILTER_INDEX = "video_record_filter_index";
        String VIDEO_RECORD_FILTER_NAME = "video_record_filter_name";
        String VIDEO_RECORD_BEAUTIFUL_INDEX_PROGRESS = "video_record_beautiful_index_progress";

        String LYRIC_MAKER_NEW_FLAG = "lyric_maker_new";
        String LYRIC_MAKER_NEW_GUIDE_FLAG = "lyric_maker_new_guide";
        String LYRIC_MAKER_FIRST_START_PLAY = "lyric_maker_first_start_play";
        String LYRIC_MAKER_TRANSLATE_NEW_GUIDE_FLAG = "lyric_maker_translate_new_guide";
        String LYRIC_MAKE_UPLOAD_SELECTED_ITEM = "lyric_make_upload_selected_item";

        String VIVO_DESK_LYRIC_IS_CHECK = "vivo_desk_lyric_is_check";

        String SEARCH_MUSIC_IDENTIFY_TIPS_HAS_SHOW = "search_music_identify_tips_has_show";

        String SEARCH_RINGTONE_TOAST_HAS_SHOW = "search_ringtone_toast_has_show";

        String SEARCH_IS_ALWAYS_SHOW_RINGTONE_ENTRANCE = "search_is_always_show_ringtone_entrance";

        String SEARCH_SWITCH_CACHE_RINGTONE_VALUE = "search_switch_cache_ringtone_value";

        String SEARCH_SHOW_RINGTONE_VALUE = "search_show_ringtone_value";

        String EXIST_HISTORY_CACHE_NUM = "is_exist_history_cache_num";

        String SEARCH_TAB_TOAST_HAS_SHOW = "search_tab_toast_has_show";

        String AUDIO_LIAN_TAB_TIPS_SHOW = "audio_lian_tab_tips_show";

        String IS_SHOW_SEARCH_LYRIC_TIPS = "is_show_search_lyric_tips";

        String SHOW_SEARCH_RESULT_AD_TIME = "show_search_result_ad_time";

        String SEARCH_SCROLL_TIPS_SHOW = "search_scroll_tips_show";

        String MORE_MENU_TIPS_SHOW = "more_menu_tips_show";

        String DRIVE_MODE_SCREEN_ROTATE = "drive_mode_screen_rotate";

        String IS_FIRST_CREATE_DRIVE_MODE = "is_first_create_drive_mode";

        String SHOW_REDAR_DIALOG = "show_redar_dailog";

        String SHOW_LEVITATE_IDENTIFY_DIALOG = "show_levitate_identify_dialog";

        String SEARCH_KSONG_TIPS_SHOW = "search_kong_tips_show";

        String LONG_TIME_RESULT_TIPS_SHOW = "long_time_result_tips_show";

        String IS_CAN_SHOW_LONG_TIME_RESULT_TIPS = "is_can_show_long_time_result_tips";

        String EDIT_LYRIC_TIPS_SHOW = "edit_lyric_tips_show";

        String NAVIGATION_IMPORT_PLAYLIST_ENTRY_GUIDE = "navigation_import_playlist_entry_guide";

        String EDIT_LYRIC_CONTENT_FIRST_SHOW = "edit_lyric_content_first_show";

        String IS_SEARCH_REC_HOT_TIPS_SHOW = "is_search_rec_hot_tips_show";

        /**
         * 播放页关联推荐显示
         */
        String SHOW_PLAYER_RELATE_REC_PRE = "SHOW_PLAYER_RELATE_REC_PRE";

        /**
         * 上次获取号码时间
         */
        String LAST_GET_HIDE_INFO_TIME = "last_get_hide_info_time";

        public static final String PLAY_PERSONAL_TIME_STAMP = "play_personal_time_stamp";
        /**
         * 之前进入过酷群
         */
        public static final String USER_HAS_IN_KUQUN_BEFORE = "user_has_in_kuqun_before";

        /**
         * 酷群礼物资源缓存状态
         */
        String KUQUN_GIFT_RES_CACHE_STATE = "kuqun_gift_res_cache_state";

        /**
         * 进入酷群模块的时间
         */
        String KUQUN_ENTER_KUQUN_MODULE_TIME = "kuqun_enter_kuqun_module_time";

        /**
         * 鱼声接入繁星货币配置
         */
        String KUQUN_FX_CURRENCY_CONFIG = "kuqun_fx_currency_config";

        /**
         * 瀑布流曝光时间
         */
        String KUQUN_TING_ENTRY_SHOW_TIME = "kuqun_ting_entry_show_time";

        /**
         * 本地音乐语言过滤
         */
        public static final String LOCAL_MUSIC_LANGUAGE_FILTER = "local_music_language_filter";
        /**
         * 本地音乐音质过滤
         */
        public static final String LOCAL_MUSIC_QUALITY_FILTER = "local_music_quality_filter";
        /**
         * 本地音乐年代过滤
         */
        public static final String LOCAL_MUSIC_YEAR_FILTER = "local_music_quality_year";
        /**
         * 本地音乐bpm过滤
         */
        public static final String LOCAL_MUSIC_BPM_FILTER = "local_music_bpm_filter";
        /**
         * 本地音乐流派过滤
         */
        public static final String LOCAL_MUSIC_GENRE_FILTER = "local_music_genre_filter";
        /**
         * 本地音乐最近常听过滤
         */
        public static final String LOCAL_MUSIC_RECENT_LISTEN_FILTER =
            "local_music_recent_listen_filter";
        /**
         * 本地音乐重名过滤
         */
        public static final String LOCAL_MUSIC_MORE_FILTER = "local_music_more_filter";
        //本地音乐重名标签曝光次数
        public static final String LOCAL_MUSIC_MORE_FILTER_COUNT = "local_music_more_filter_count";

        public static final String USER_DYNAMIC_ENTRY_INFO = "user_dynamic_entry_info";

        /**
         * 是否记住过滤选项
         */
        public static final String SAVE_LOCAL_MUSIC_FILTER = "save_local_music_filter";

        public static final String SAVE_LOCAL_MUSIC_FILTER_TIP_DIALOG =
            "save_local_music_filter_tip_dialog";

        /**
         * 首页红包入口是否展示
         */
        public static final String HOME_REDPACKET_ENTRANCE = "home_redpacket_entrance";

        /**
         * 首页红包入口链接
         */
        public static final String HOME_REDPACKET_URL = "home_redpacket_url";

        /**
         * 是否有删除过歌手的敏感消息
         */
        public static final String ALREADY_DELETE_SINGER_MSG = "already_delete_singer_msg";

        String LAST_SET_MUTE_VOLUME = "last_set_mute_volume";

        /**
         * 最近添加的歌单id
         */
        public static final String LAST_CLOUD_PLAYLIST_ID = "last_cloud_playlist_id";

        /**
         * 新歌推荐模块播放次数
         */
        public static final String NEW_SONG_RECOMMEND_PLAY = "new_song_recommend_play";

        //音乐圈动态count
        public static final String MUSIC_ZONE_NEW_MSG_FEEDS_COUNT =
            "music_zone_new_msg_feeds_count";

        public static final String IS_FX_SINGER_ENTRY_VISIABLE = "is_fx_singer_entry_visiable";

        /**
         * 定时关闭
         */
        public static final String ALARM_CONFIG_INFO = "alarm_config_info";

        /**
         * 酷狗互联设备历史记录
         */
        String KGPC_DEVICE_HISTORY = "kgpc_device_history";
        String KGPC_KGTOOL_ENTRANCE_RED_DOT = "kgpc_kgtool_entrance_red_dot";
        String KGPC_KGTOOL_ENTRANCE_SECOND_RED_DOT = "kgpc_kgtool_entrance_second_red_dot";
        String KGPC_PLAYER_ENTRANCE_RED_DOT = "kgpc_player_entrance_red_dot";
        String KGPC_PLAYER_ENTRANCE_SECOND_RED_DOT = "kgpc_player_entrance_second_red_dot";

        public static final String KEY_LOCAL_MUSIC_MATCH_STATUS_LAST_REPORT_TIME =
            "key_local_music_match_status_report_time";

        public static final String KEY_SHOWED_PERSONFM_SMALL_MODE_TIPS =
            "key_showed_personfm_small_mode_tips";
        public static final String KEY_PLAYER_FRAGMENT_LYRIC_MENU_RED_DOT =
            "key_player_fragment_lyric_menu";
        public static final String KEY_PLAYER_FRAGMENT_FONT_MENU_RED_DOT =
            "key_player_fragment_font_menu";

        String KEY_CAR_BLUETOOTH_LYRIC_DIALOG_SHOW = "key_car_bluetooth_lyric_dialog_show";
        String KEY_CAR_BLUETOOTH_LYRIC_NEX_REQUEST_TIME =
            "key_car_bluetooth_lyric_nex_request_time";
        String KEY_CAR_BLUETOOTH_LIST_REQUEST_TIME = "key_car_bluetooth_list_request_time";

        public static final String KEY_IDENTIFY_TIPS_CONTENT = "key_identify_tips_content";

        public static final String KEY_PERSONFM_FROM_SOURCE = "key_personfm_from_source";
        public static final String KEY_SHOW_TIPS_TO_PERSONFM_LAST_RANDOM =
            "key_show_tips_to_personfm_last_random";

        String KEY_IS_PLAYER_SHOW_GAME_CENTER_TIMES = "KEY_IS_PLAYER_SHOW_GAME_CENTER_TIMES";
        String KEY_IS_PLAYER_CLICK_GAME_CENTER = "KEY_IS_PLAYER_CLICK_GAME_CENTER";

        String KEY_CAN_SHOW_USER_CENTER_BG = "key_can_load_user_center_bg";

        String KEY_IS_PLAYER_SCROLLABLE_PROMOTION_SHOWED = "IS_PLAYER_SCROLLABLE_PROMOTION_SHOWED";

        String KEY_IS_NEED_SHOW_RELATED_VIDEO = "KEY_is_need_show_related_video";
        String KEY_FROM_RELATED_VIDEO_STATUS = "key_from_related_video_status";

        String KEY_IS_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW =
            "KEY_IS_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW";
        String KEY_ALREADY_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW =
            "KEY_ALREADY_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW";
        String KEY_RESET_IS_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW =
            "KEY_RESET_IS_PLAYER_SHORT_VIDEO_TIPS_SHOWED_NEW";

        String KEY_IS_PLAYER_SHORT_VIDEO_ACTION_TIPS_SHOWED =
            "KEY_IS_PLAYER_SHORT_VIDEO_ACTION_TIPS_SHOWED";

        String HAS_SHOW_HOME_ALBUM_RED = "has_show_home_album_red"; //首页唱片好的

        String HAS_SINGER_QA_RED = "has_singer_qa_red";

        String KEY_FIVE_SING_RED_TIME = "key_five_sing_red_time";

        /**
         * 歌词视频素材信息
         */
        String KEY_LYRIC_VIDEO_MATERIAL_INFO = "KEY_LYRIC_VIDEO_MATERIAL_INFO";

        /**
         * 关注的艺人的本地缓存屏蔽列表
         */
        String KEY_STAR_FOLLOW_BLOCK_LIST_INFO = "KEY_STAR_FOLLOW_BLOCK_LIST_INFO";

        /**
         * 首次关闭串串视频
         */
        String KEY_HAS_PLAYER_SHORT_VIDEO_FIRST_CLOSED = "KEY_HAS_PLAYER_SHORT_VIDEO_FIRST_CLOSED";
        String KEY_HAS_PLAYER_SHORT_VIDEO_FIRST_CLICK = "KEY_HAS_PLAYER_SHORT_VIDEO_FIRST_CLICK";
        String KEY_LAST_SHOW_PLAYER_SHORT_VIDEO_WAVE = "key_last_show_player_short_video_wave";

        String KEY_IS_SHORT_VIDEO_NEW_OPEN = "KEY_IS_SHORT_VIDEO_NEW_OPEN";
        String KEY_CC_LIKE_GUIDE_LAST_TIME = "KEY_CC_LIKE_GUIDE_LAST_TIME";
        String KEY_CC_MENU_GUIDE_TYPE = "KEY_CC_MENU_GUIDE_TYPE";
        String KEY_SHOWMVEDITMOVEITEMTIPSCOUNT = "KEY_SHOWMVEDITMOVEITEMTIPSCOUNT";

        String KEY_INTEREST_STUDENT_AUTH = "key_interest_student_auth";

        String KEY_REQUEST_LOCATE_PERMISSION_LAST_TIME = "key_request_locate_permission_last_time";

        String KEY_STUDENT_AUTH_STATE = "key_student_auth_state";

        String KEY_CAR_DRIVE_MODE_DIALOG_SHOW = "key_car_drive_mode_show";

        String KEY_DEF_KEYBOARD_HEIGHT = "KEY_DEF_KEYBOARD_HEIGHT";
        String KEY_PLAYER_CD_AD_CLOSE_DATE = "key_player_cd_ad_close_date";
        String KEY_SHARE_AD_CLOSE_DATE = "key_share_ad_close_date";
        String KEY_MUSIC_DOWN_AD_CLOSE_DATE = "key_music_down_ad_close_date";
        String KEY_YUEKU_BOTTOM_AD_CLOSE_DATE = "key_yueku_bottom_ad_close_date";
        String KEY_SPECIAL_BOTTOM_AD_CLOSE_DATE = "key_special_bottom_ad_close_date";
        String KEY_COMMENT_FLOW_AD_CLOSE_DATE = "key_comment_flow_ad_close_date";
        String KEY_PLAYER_BANNER_AD_CLOSE_DATE = "key_player_banner_ad_close_date";
        String KEY_PLAYER_BANNER_GDT_AD_SHOW_DATE = "key_player_banner_gdt_ad_show_date";
        String KEY_PLAYER_BANNER_GDT_AD_SHOW_COUNT = "key_player_banner_gdt_ad_show_count";
        String KEY_DEVICE_FINGER_ID_DATE = "key_device_finger_id_date";
        String KEY_HAS_SHOW_SLIDE_DESK_BALL_TIPS = "key_hash_show_slide_desk_ball_tips";

        String KEY_MUSIC_ZONE_DYNAMIC_GUIDING_SHOWED = "KEY_MUSIC_ZONE_DYNAMIC_GUIDING_SHOWED";

        String KEY_LAST_MUSIC_ZONE_CLEANING_TIME_STAMP = "KEY_LAST_MUSIC_ZONE_CLEANING_TIME_STAMP";
        String KEY_MINE_PULL_AD = "key_mine_pull_ad_last_showdate";

        String KEY_VERSION_ANNOUNCEMENT_IN_COMMENT_AREA = "KEY_ANNOUNCEMENT_IN_COMMENT_AREA";
        String KEY_URL_ANNOUNCEMENT_IN_COMMENT_AREA = "KEY_URL_ANNOUNCEMENT_IN_COMMENT_AREA";

        String KEY_HAS_SHOW_DESK_LYRIC_LOCK_TIPS = "key_has_show_desk_lyric_lock_tips";
        String KEY_HAS_SHOW_DESK_LYRIC_HIDE_TIPS = "key_has_show_desk_lyric_hide_tips";

        String KEY_HAS_SHOW_WECHAT_TIPS = "key_has_show_wechat_tips";
        String KEY_VERSION_SHOW_WECHAT_TIPS = "key_version_show_wechat_tips";
        String KEY_USER_SUBSCRIBE_WECHAT_STATUS = "key_user_subscribe_wechat_status";

        String KEY_YUKE_MINIAPP_TAB_POSITION = "key_yuke_miniapp_tab_position";

        String KEY_HAS_SHOW_PLAYER_ENCOUNTER_SUPLIKE_GUIDE =
            "key_has_show_player_encounter_suplike_guide";
        String KEY_HAS_SHOW_PLAYER_ENCOUNTER_SLIDE_LEFT_TIPS =
            "key_has_show_player_encounter_slide_left_tips";
        String KEY_HAS_SHOW_PLAYER_ENCOUNTER_SLIDE_RIGHT_TIPS =
            "key_has_show_player_encounter_slide_right_tips";
        String KEY_HEARTBEAT_PENDANT_CLOSE_DATE = "key_heartbeat_pendant_close_date";
        String KEY_HEARTBEAT_OPEN_CLICK = "key_heartbeat_open_click";
        String KEY_HEARTBEAT_GENDER_PREFERENCE = "key_heartbeat_gender_preference";
        String KEY_HEARTBEAT_PROFILE_CARE_TOTAL = "key_heartbeat_profile_care_reddot_nums";
        String KEY_HEARTBEAT_TOP_ENTRANCE_LAST_TIME = "key_heartbeat_entrance_last_time";

        String KEY_HAS_CLICK_GREET_LIST_BTN = "key_has_click_greet_list_btn";

        String KEY_AUDIO_AD_TODAY_ALREADY_COUNT = "key_audio_ad_today_already_count";
        String KEY_AUDIO_AD_COUNT_DATE = "key_audio_ad_count_date";

        String KEY_SCENE_REC_ALL_TOP = "key_scene_rec_all_top";

        //Icon入口是否可见
        String LAUNCHER_ICON_VISIBLE = "launcher_icon_visible";

        //Icon入口相关信息
        String LAUNCHER_ICON_INFO = "launcher_icon_info";
        //Icon入口上次返回的位置
        String LAUNCHER_ICON_SORT = "launcher_icon_sort";
        //Icon入口上次点击时间
        String LAUNCHER_ICON_TIME = "launcher_icon_time";
        //是否拖动icon
        String LAUNCHER_ICON_HAS_DRAG = "launcher_icon_has_drag";

        //是否有真实修改过当前的排序
        String LAUNCHER_ICON_CHANGE_SORT = "launcher_icon_change_sort";

        //当前拖动后，保存当前的config
        String LAUNCHER_ICON_CONFIG_INFO = "launcher_icon_config_info";

        //是否已经处理过旧版本升级到新版本的映射问题了
        String LAUNCHER_ICON_RESORT_BY_APP_UPDATE = "launcher_icon_config_info";

        /**
         * 消息中心轮询时服务器时间差，精确到毫秒
         */
        String KEY_MSG_CENTER_PULL_TIME_OFFSET = "key_msg_center_pull_time_offset";
        String KEY_MSG_CENTER_SHOW_NO_FOLLOW_USER_MSG_GUIDE =
            "key_msg_center_show_no_follow_user_msg_guide";
        String KEY_MSG_CENTER_SHOW_DISTURB_OFFICIAL_GUIDE =
            "key_msg_center_show_disturb_official_guide";
        String KEY_MSG_CENTER_PUSH_GUIDE_LAST_TIME = "key_msg_center_push_guide_last_time";
        String KEY_MSG_CENTER_PUSH_GUIDE_COUNT = "key_msg_center_push_guide_count";
        String KEY_MSG_CENTER_PUSH_GUIDE_TYPE = "key_msg_center_push_guide_type";
        String KEY_MSG_CENTER_CLOSE_NOTIFY_PERMISSION_TIPS =
            "key_msg_center_close_notify_permission_tips";
        String KEY_MSG_CENTER_NOTIFY_PERMISSION_SHOW_COUNT =
            "key_msg_center_notify_permission_show_count";
        String KEY_MSG_CENTER_WECHAT_SHOW_COUNT = "key_msg_center_wechat_show_count";
        String KEY_HAS_SHOW_DESK_LYRIC_NEW_SETTING_TIPS = "key_has_show_desk_lyric_new_tips";
        String KEY_PLAYER_SHARE_BUTTON_ANIM_LAST_RED_HEART_PLAYING_TIME =
            "KEY_PLAYER_SHARE_BUTTON_ANIM_LAST_RED_HEART_PLAYING_TIME";

        String KEY_MSG_CENTER_SYNC_POLL_S = "key_msg_center_sync_poll_s";
        String KEY_MSG_CENTER_SYNC_PULL_S = "key_msg_center_sync_pull_s";
        String KEY_MSG_CENTER_SYNC_PULL_SYT = "key_msg_center_sync_pull_syt";
        String KEY_MSG_CENTER_SYNC_RECALL_S = "key_msg_center_sync_recall_s";

        String KEY_COMMENT_LIST_TOP_TAB_TOAST_LAST_TIME_PRE =
            "KEY_COMMENT_LIST_TOP_TAB_TOAST_LAST_TIME_PRE_";

        String KEY_CHECK_CURRENT_FONT_VALID_TIME = "key_check_current_font_valid_time";

        String CHAT_FACE_RED_POINT = "chat_face_red_point"; //私聊表情红点状态
        String CHAT_FACE_SEARCH_GIF = "chat_face_search_gif"; //私聊表情-搜索new状态

        /**
         * 用于跨平台互联的设备id
         */
        String KEY_KGPLAY_DEVICE_ID = "key_kgplay_device_id";
        String KEY_MUSIC_ZONE_DEFAULT_TAB_TITLE = "KEY_MUSIC_ZONE_DEFAULT_TAB_TITLE_";

        String KG_SHORT_VIDEO_LAST_TIME_SHOW_UP_PROMPT = "kg_short_video_last_time_show_up_prompt";

        String KG_SHORT_VIDEO_NON_UP_SLIDE_PLAY_COUNT = "kg_short_video_non_up_slide_play_count";

        String KEY_MUSIC_ZONE_DEFAULT_TAB_INDEX_NAME = "KEY_MUSIC_ZONE_DEFAULT_TAB_INDEX_NAME_";

        String KEY_MUSIC_ZONE_IS_SQUARE_FIRST_SHOWN = "KYE_MUSIC_ZONE_IS_SQUARE_FIRST_SHOWN";

        String KG_SHORT_VIDEO_ALREADY_UP_SLIDE_PLAY = "KG_SHORT_VIDEO_ALREADY_UP_SLIDE_PLAY";

        String KEY_SO_RELINKER_VERSION = "KEY_SO_RELINKER_VERSION";

        String KEY_SHOW_POST_H5_GUIDE = "key_show_post_h5_guide";

        String KEY_X_MAIN_BOTTOM_BAR_DOUBLE_CLICK_TIPS = "key_x_main_bottom_bar_double_click_tips";

        /**
         * 浮浮雷达已安装按钮是否显示
         */
        String KEY_FU_FU_RADIO_INSTALLED_BUTTON_SHOW = "key_fu_fu_radio_installed_button_show";

        String KEY_SHOW_POST_ENTRY_GUIDING = "show_post_entry_guiding";
        String KEY_SHOW_NEWDYNAMIC_ALL_POST_ENTRY_GUIDING =
            "show_newdynamic_all_post_entry_guiding";

        String KEY_MZ_USER_PER_PROTOCOL_URL = "mz_user_per_protocol_url";
        String KEY_MZ_USER_PER_IS_SHARE_CHECKED = "mz_user_per_is_share_checked";
        String KEY_MZ_USER_PER_IS_CMT_CHECKED_1 = "mz_uer_per_is_cmt_checked_1";
        String KEY_MZ_SYNC_TIPS_SHOWED = "key_mz_sync_tips_showed";
        /**
         * 第一次点击歌单投稿
         */
        String KEY_FIRST_SPECIAL_POST = "key_first_special_post";

        /**
         * 显示歌单投稿失败弹层
         */
        String KEY_SHOW_PLAY_LIST_POST_FAIL_DIALOG = "key_show_play_list_post_fail_dialog";

        /**
         * 私聊底部游戏按钮红点
         */
        String KEY_CHAT_GAME_RED_TIP = "key_chat_game_red_tip";

        //
        String KEY_MAIN_DYNAMIC_LAST_INDEX = "KEY_MAIN_DYNAMIC_LAST_INDEX";

        String KEY_SHOW_LISTEN_SLIDE_PULL_MORE = "key_show_listen_slide_pull_more";

        String KEY_HAS_CLICK_STATUS_BAR_LYRIC = "key_has_show_status_bar_lyric";

        String RINGTONE_RECOMMEND_VIDEO_DIALOG_COUNT = "ringtone_recommend_video_dialog_count";

        String RINGTONE_RECOMMEND_VIDEO_DIALOG_TYPE = "ringtone_recommend_video_dialog_type";

        String PLAYER_COLOR_TYPE = "player_color_type";

        String PLAYER_COLOR_NEAR_TYPE = "player_color_near_type";

        String KEY_HAS_SHOW_FLOAT_VIEW_CLOSE_DIALOG = "key_has_show_float_view_close_dialog";

        String KEY_HAS_SHOW_MUSIC_IDENTIFY_BALL_TIPS = "key_has_show_music_identify_ball_tips";
        String KEY_HAS_SHOW_MUSIC_IDENTIFY_BALL_TIPS_TITLE_BAR =
            "key_has_show_music_identify_ball_tips_title_bar";
        String KEY_HAS_SHOW_LINK_ENTRY_TIPS = "key_has_show_link_entry_tips";

        String KEY_LAST_SHOW_MUSIC_IDENTIFY_NOTIFICATION_TIME =
            "key_last_show_music_identify_notification_time";

        String KEY_HAS_SHOW_SINGLE_LINE_LYRIC_TIPS = "key_has_show_single_line_tips";

        String KEY_HAS_SHOW_LOCK_VIDEO_GUIDE = "key_has_show_lock_video_guide";

        String KEY_HAS_SHOW_LYRIC_OPEN_VIDEO_GUIDE = "key_has_show_lyric_open_video_guide";

        String KEY_SYNC_IMAGEUPLOADING_TO_MZ = "key_sync_imageuploading_to_mz_";

        String KEY_IS_MUSIC_ZONE_UPLOAD_CONTACT_REQUEST_BANNER_SHOWED =
            "key_is_music_zone_upload_contact_request_banner_showed";

        String KEY_LAST_TIME_MUSIC_ZONE_REQUEST_CONTACT =
            "key_last_time_music_zone_request_contact";

        String KEY_PERSONFM_ENABLE = "key_personfm_enable";
        String KEY_HAS_SHOW_SINGER_LIVE_TOAST = "key_has_show_singer_live_toast";

        String KEY_LAST_TIME_MUSIC_ZONE_ITEM_HEADER_SWITCH =
            "KEY_LAST_TIME_MUSIC_ZONE_ITEM_HEADER_SWITCH";
        String KEY_LAST_TIME_MUSIC_ZONE_ITEM_CAS = "KEY_LAST_TIME_MUSIC_ZONE_ITEM_CAS_";

        String KEY_HAS_LISTEN_SLIDE_SHOW_LYRIC_TIPS = "KEY_HAS_LISTEN_SLIDE_SHOW_LYRIC_TIPS";

        String KEY_CHAT_QUICK_MSGS = "KEY_CHAT_QUICK_MSGS";

        String KEY_HAS_SHOW_SEARCH_AI_READER_TIPS = "KEY_HAS_SHOW_SEARCH_AI_READER_TIPS";

        String KEY_HAS_SHOW_SEARCH_PROGRAM_TIPS = "KEY_HAS_SHOW_SEARCH_PROGRAM_TIPS";

        String KEY_WEB_READER_PERSON_INDEX = "KEY_WEB_READER_PERSON_INDEX";

        String KEY_WEB_READER_SPEED_INDEX = "KEY_WEB_READER_SPEED_INDEX";
        String KEY_DYNAMIC_TAB_FEEDS_NUM = "KEY_DYNAMIC_TOP_TAB_FEEDS_NUM";
        String KEY_DYNAMIC_ALL_TAB_FEEDS_NUM = "KEY_DYNAMIC_ALL_TAB_FEEDS_NUM";
        String KEY_DYNAMIC_LAST_FEEDS_NUM = "KEY_DYNAMIC_ALL_TAB_LAST_FEEDS_NUM";
        String KEY_HAS_SHOW_SLIDE_IDENTIFY_TIPS = "KEY_HAS_SHOW_SLIDE_IDENTIFY_TIPS";
        String KEY_ARTIST_TAB_FEEDS_NUM = "KEY_ARTIST_TAB_FEEDS_NUM";
        String KEY_SUBSCRIBE_TAB_FEEDS_NUM = "KEY_SUBSCRIBE_TAB_FEEDS_NUM";
        String KEY_NEWEST_ARTIST_TAB_FEEDS_NUM = "KEY_NEWEST_ARTIST_TAB_FEEDS_NUM";
        String KEY_NEWEST_SUBSCRIBE_TAB_FEEDS_NUM = "KEY_NEWEST_SUBSCRIBE_TAB_FEEDS_NUM";
        /**
         * 首页铃声小红点
         */
        String KEY_HAS_SHOW_HOME_RING_RED = "KEY_HAS_SHOW_HOME_RING_RED";

        /**
         * 播放页分享动画显示次数
         */
        String KEY_PLAYER_FRAGMENT_SHARE_ANIMATION_COUNT =
            "KEY_PLAYER_FRAGMENT_SHARE_ANIMATION_COUNT";
        String KEY_PLAYER_FRAGMENT_SHARE_ANIMATION_DATE =
            "KEY_PLAYER_FRAGMENT_SHARE_ANIMATION_DATE";

        /**
         * 播放页分享任务展示时间与次数
         */
        String KEY_SONG_SHARE_MISSION_EXPOSE_DATE = "KEY_SONG_SHARE_MISSION_EXPOSE_DATE";
        String KEY_SONG_SHARE_MISSION_EXPOSE_COUNT = "KEY_SONG_SHARE_MISSION_EXPOSE_COUNT";
        String KEY_SONG_SHARE_MISSION_CAN_EXPOSE = "KEY_SONG_SHARE_MISSION_CAN_EXPOSE";
        String KEY_SONG_SHARE_MISSION_SIGNED = "KEY_SONG_SHARE_MISSION_SIGNED";

        String KEY_PLAYER_SHOW_DATE = "KEY_PLAYER_SHOW_DATE";
        String KEY_PLAYER_SHARE_CLICK = "KEY_PLAYER_SHARE_CLICK_DATE";
        String KEY_PLAYER_SHARE_MISSION_CLOSE_CLICK = "KEY_PLAYER_SHARE_MISSION_CLOSE_DATE";

        /**
         * 付费歌曲分享播放完整版相关本地记录
         */
        String KSY_SONG_SHARE_LISTEN_MIXIDS = "KSY_SONG_SHARE_LISTEN_MIXIDS";
        String KEY_SONG_SHARE_LISTEN_EXPOSE_DATE = "KEY_SONG_SHARE_LISTEN_EXPOSE_DATE";
        String KEY_SONG_SHARE_LISTEN_EXPOSE_COUNT = "KEY_SONG_SHARE_LISTEN_EXPOSE_COUNT";

        String KEY_DEFAULT_DYNAMIC_SYNC_CHECKED = "KEY_DEFAULT_DYNAMIC_SYNC_CHECKED";

        String KEY_DYNAMIC_NUM_OF_MSG_TOP = "KEY_DYNAMIC_NUM_OF_MSG_TOP";
        String KEY_DYNAMIC_NUM_OF_MSG_SUB = "KEY_DYNAMIC_NUM_OF_MSG_SUB";
        String KEY_DYNAMIC_LAST_MSG_COUNT = "KEY_DYNAMIC_LAST_MSG_COUNT";

        //渠道广告唯一id
        String KEY_GLOBAL_CHANNEL_AD_ID = "KEY_GLOBAL_CHANNEL_AD_ID";

        String KEY_TASK_SYS_OPENID = "key_task_sys_openid";

        //设置是否使用定制化闪屏
        String KEY_USE_COMMISSION_SPLASH_TYPE = "KEY_USE_COMMISSION_SPLASH_TYPE";

        //定制化闪屏是否静音
        String KEY_USE_COMMISSION_SPLASH_MUTE = "KEY_USE_COMMISSION_SPLASH_MUTE";

        //定制化闪屏声波引导是否出现过
        String KEY_COMMISSION_SOUND_WAVE_HAVE_GUIDE = "KEY_COMMISSION_SOUND_WAVE_HAVE_GUIDE";

        //定制化入场引导是否出现过
        String KEY_COMMISSION_ENTRY_GUIDE = "KEY_COMMISSION_ENTRY_GUIDE";

        String KEY_HAS_LIKE_POPUP_SHOWN = "KEY_HAS_LIKE_POPUP_SHOWN";

        String KEY_HAS_SHOW_FLOAT_BALL_TIPS = "KEY_HAS_SHOW_FLOAT_BALL_TIPS";

        String KEY_DYNAMIC_COMBINATION_TOAST_SHOWN = "KEY_DYNAMIC_COMBINATION_TOAST_SHOWN";

        String KEY_COMMISSION_EVER_ENCODE_FAIL = "KEY_COMMISSION_EVER_ENCODE_FAIL";

        String KEY_HAS_SHOW_VIDEO_IDENTIFY_NEW = "KEY_HAS_SHOW_VIDEO_IDENTIFY_NEW";

        String KEY_HAS_SHOW_PUBLISH_DYNAMIC_HINT = "KEY_HAS_SHOW_PUBLISH_DYNAMIC_HINT";
    }
}
