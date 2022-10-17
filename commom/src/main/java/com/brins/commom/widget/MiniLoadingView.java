package com.brins.commom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.annotation.Nullable;
import com.brins.commom.utils.SystemUtils;

public class MiniLoadingView extends SmallLoadingView {

    // LoadingView 19.5dp

    private static final int DEF_MINI_BACKGROUND_SIZE = SystemUtils.dip2px(13f);
    private static final int DEF_MINI_KSIGN_SIZE = SystemUtils.dip2px(6f);
    private static final int MINI_RADIUS = SystemUtils.dip2px(2f);

    public MiniLoadingView(Context context) {
        super(context);
    }

    public MiniLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void initKG11LoadingKSignView(LoadingKSignView viewKSign) {
        LoadingArcView.LayoutParams params = new LoadingArcView.LayoutParams(DEF_MINI_BACKGROUND_SIZE, DEF_MINI_BACKGROUND_SIZE);
        params.gravity = Gravity.CENTER;
        viewKSign.setLayoutParams(params);
        viewKSign.setPivotX(DEF_MINI_BACKGROUND_SIZE / 2);
        viewKSign.setPivotY(DEF_MINI_BACKGROUND_SIZE / 2);
        viewKSign.setKSignViewSize(DEF_MINI_KSIGN_SIZE, DEF_MINI_KSIGN_SIZE);

        this.viewKSign = viewKSign;

        setRadius(MINI_RADIUS);
    }
}
