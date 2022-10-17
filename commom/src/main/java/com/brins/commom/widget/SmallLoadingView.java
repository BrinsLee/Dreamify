package com.brins.commom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.annotation.Nullable;
import com.brins.commom.utils.SystemUtils;

public class SmallLoadingView extends CommLoadingView {

    // LoadingView 28.32dp

    private static final int DEF_SMALL_BACKGROUND_SIZE = SystemUtils.dip2px(18.91f);
    private static final int DEF_SMALL_KSIGN_SIZE = SystemUtils.dip2px(8.73f);
    private static final int SMALL_RADIUS = SystemUtils.dip2px(2.91f);

    protected LoadingKSignView viewKSign;

    private boolean withDarkAdjust = true;

    public SmallLoadingView(Context context) {
        super(context);
        init();
    }

    public SmallLoadingView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmallLoadingView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {}

    @Override
    protected void initKG11LoadingKSignView(LoadingKSignView viewKSign) {
        LoadingArcView.LayoutParams params = new LoadingArcView.LayoutParams(DEF_SMALL_BACKGROUND_SIZE, DEF_SMALL_BACKGROUND_SIZE);
        params.gravity = Gravity.CENTER;
        viewKSign.setLayoutParams(params);
        viewKSign.setPivotX(DEF_SMALL_BACKGROUND_SIZE / 2);
        viewKSign.setPivotY(DEF_SMALL_BACKGROUND_SIZE / 2);
        viewKSign.setKSignViewSize(DEF_SMALL_KSIGN_SIZE, DEF_SMALL_KSIGN_SIZE);

        this.viewKSign = viewKSign;

        setRadius(SMALL_RADIUS);
    }

    @Override
    public void resetColor() {
        int[] colors = null;
        if (getFixedColorGetter() != null) {
            colors = getFixedColorGetter().getColors(isDelayState());
        }
        if (colors == null) {
            colors = isDelayState() ? LoadingColorHelper.getDelayColor(withDarkAdjust) : LoadingColorHelper.getRegularColor(withDarkAdjust);
        }
        setColorArcAndCircle(colors[2]);
        if (viewKSign != null) {
            viewKSign.setColor(colors[1], colors[0]);
        }
    }

    public void setWithThemeAdjust(boolean withDarkAdjust) {
        this.withDarkAdjust = withDarkAdjust;
        resetColor();
    }
}
