package com.brins.commom.playing;

import android.content.Context;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.SystemUtils;

/**
 * Created by tingcao on 2019/12/29.
 */
public class PlayingItemViewUtil {

    private static Boolean isLowMachine;

    private static int mFeeDownloadRightMargin;

    private static int mDownloadedRightMargin;

    private static int mVipDownloadRightMargin;

    private static int mNormalDownloadRightMargin;

    private static final float FEE_DOWNLOAD_RIGHT_MARGIN_DP = 22.5f;

    private static final int NORMAL_DOWNLOAD_RIGHT_MARGIN_DP = 25;

    private static final float VIP_DOWNLOAD_RIGHT_MARGIN_DP = 14.5f;

    private static final int DOWNLOADED_RIGHT_MARGIN_DP = 22;

    private static int mPlayingIconPadding;

/*    public static String getPlayingSingerUrl(KGMusic kgMusic){
        String coverUrl = "";
        int authorId = AuthorIdDBHelper.getAuthorId(kgMusic.getHashValue(), 0, kgMusic.getDisplayName(), kgMusic.getMixId());
        if (authorId > 0) {
            coverUrl  = AvatarFinder.getAvatarImgByAuthorId(authorId);
        } else {
            coverUrl = AvatarFinder.getAvatarImgBySingerName(kgMusic.getArtistName());
        }

        if (!FileUtil.isExist(coverUrl)) {
            coverUrl = "";
        }

        return coverUrl;
    }*/

    public static boolean isLowMachine(){
        if(isLowMachine == null){
            String machineMemoInf = SystemUtils.getTotalMemory(DRCommonApplication.getContext());
            isLowMachine = Integer.parseInt(machineMemoInf) <= 1024;
        }

        return isLowMachine;
    }

    public static int getNormalDownloadRightMargin(Context context){
        if(mNormalDownloadRightMargin == 0){
            mNormalDownloadRightMargin = SystemUtils.dip2px(context,NORMAL_DOWNLOAD_RIGHT_MARGIN_DP);
        }
        return mNormalDownloadRightMargin - getPlayingIconPadding(context);
    }

    public static int getFeeDownloadRightMargin(Context context){
        if(mFeeDownloadRightMargin == 0){
            mFeeDownloadRightMargin = SystemUtils.dip2px(context,FEE_DOWNLOAD_RIGHT_MARGIN_DP);
        }
        return mFeeDownloadRightMargin - getPlayingIconPadding(context);
    }

    public static int getDownloadedRightMargin(Context context){
        if(mDownloadedRightMargin == 0){
            mDownloadedRightMargin = SystemUtils.dip2px(context,DOWNLOADED_RIGHT_MARGIN_DP);
        }
        return mDownloadedRightMargin - getPlayingIconPadding(context);
    }

    public static int getVipDownloadRightMargin(Context context){
        if(mVipDownloadRightMargin == 0){
            mVipDownloadRightMargin = SystemUtils.dip2px(context,VIP_DOWNLOAD_RIGHT_MARGIN_DP);
        }
        return mVipDownloadRightMargin - getPlayingIconPadding(context);
    }

    private static int getPlayingIconPadding(Context context){
        if(mPlayingIconPadding == 0){
            mPlayingIconPadding = 2*context.getResources().getDimensionPixelOffset(R.dimen.playing_view_padding);
        }
        return mPlayingIconPadding;
    }

}
