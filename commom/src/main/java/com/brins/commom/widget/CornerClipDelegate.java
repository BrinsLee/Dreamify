package com.brins.commom.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.brins.commom.utils.SystemUtils;

public class CornerClipDelegate {
    private int clipWH = SystemUtils.dip2px(2);
    private ImageView imageView;
    Path path = new Path();
    Paint mPaint = new Paint();

    private void init() {
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAntiAlias(true);
    }

    public CornerClipDelegate(ImageView view) {
        this.imageView = view;
        init();
    }

    public void setShouldClip(boolean shouldClip) {
        setShouldClip(shouldClip, false);
    }

    public void setShouldClip(boolean shouldClip, boolean isRect) {
        if (imageView == null) return;
        if (shouldClip && imageView.getDrawable() != null) {
            Drawable drawable = imageView.getDrawable();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            path.reset();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            if (isRect) {
                path.addRect(bitmap.getWidth() - (bitmap.getWidth() / 3), 0, bitmap.getWidth(), (bitmap.getHeight() / 4), Path.Direction.CCW);
            } else {
                path.addCircle(imageView.getWidth() - clipWH, clipWH, imageView.getHeight() / 4, Path.Direction.CCW);
            }
            canvas.drawPath(path, mPaint);
            mPaint.setXfermode(null);
            imageView.setImageBitmap(bitmap);
        }
    }
}
