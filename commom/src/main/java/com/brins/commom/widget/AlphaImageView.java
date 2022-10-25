package com.brins.commom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AlphaImageView extends ImageView {

	private static final int NORMAL_ALPHA = 255;
	private static final int PRESS_ALPHA = 204;
	
	private int mAlpha = NORMAL_ALPHA;
	
	public AlphaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), mAlpha, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
	}

	@Override
	public void refreshDrawableState() {
		if (isEnabled()) {
			mAlpha = isPressed() ? PRESS_ALPHA : NORMAL_ALPHA;
		} else {
			mAlpha = PRESS_ALPHA;
		}
		super.refreshDrawableState();
		invalidate();
	}
}
