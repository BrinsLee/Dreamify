package com.brins.dreamify.framework.uiframe;

import android.app.Activity;
import android.os.Bundle;
import com.kugou.common.base.uiframe.AnimationAccelerate;
import com.kugou.common.base.uiframe.FragmentViewBase;
import com.kugou.common.base.uiframe.FragmentViewNormal;
import com.kugou.page.framework.CreateViewInterface;

public class KGFragmentViewFactory implements CreateViewInterface {

    private static class Holder {
        private final static KGFragmentViewFactory INSTANCE = new KGFragmentViewFactory();
    }

    public static KGFragmentViewFactory getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public FragmentViewBase create(Activity context,
                                   AnimationAccelerate annotation,
                                   Bundle bundle) {
        FragmentViewBase fragmentView = null;
        if (annotation == null) return null;
        /*
        switch (annotation.viewType()) {
            case AnimationAccelerate.PLAYLIST_LOCAL: {
                if (bundle.containsKey("numOfSongs")) {
                    fragmentView = new FragmentViewPlaylistLocal(context, annotation, bundle);
                } else {
                    fragmentView = new FragmentViewNormal(context);
                }
                break;
            }
            case AnimationAccelerate.PLAYLIST_NET: {
                fragmentView = new FragmentViewPlaylistNet(context, annotation, bundle);
                break;
            }
            case AnimationAccelerate.FAV_AND_ASSET: {
                fragmentView = new FragmentViewFavAndAsset(context, annotation, bundle);
                break;
            }
            case AnimationAccelerate.VIP_FEE_SONG_PAGE: {
                fragmentView = new FragmentViewVIPFeeSong(context, annotation, bundle);
                break;
            }
        }
        */
        return fragmentView;
    }
}
