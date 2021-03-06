package ro.horon.android.auto.hdwidgets.car;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

public class CarUtils {
    public static Bitmap getCarBitmap(Context context, @DrawableRes int id, @ColorRes int tint, int size) {
        Drawable drawable = context.getResources().getDrawable(id, context.getTheme());
        drawable.setTint(ContextCompat.getColor(context, tint));
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Bitmap bitmap = Bitmap.createBitmap(metrics, size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
