
package com.brins.commom.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class VerticalImageSpan extends ImageSpan {

    private ColorFilter colorFilter;

    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
    }

    public VerticalImageSpan(Context context, Bitmap bmp) {
        super(context, bmp);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = fontHeight / 2 - drHeight / 2;
            int bottom = drHeight / 2 + fontHeight / 2;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
            int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        if(colorFilter != null){
            drawable.setColorFilter(colorFilter);
        }
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top + 1;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
        if(colorFilter != null){
            drawable.setColorFilter(null);
        }
    }

    public void setColorFilter(ColorFilter colorFilter){
        this.colorFilter = colorFilter;
    }
}
