package ro.horon.android.auto.hdwidgets.utils;

import android.app.Activity;
import android.content.Context;

public class Helpers {

    public static final String ARG_WidgetIndex = "WidgetIndex";

    public static String GetWidgetIdStorageKey(int index) {
        return "Widget_" + index;
    }

    public static int GetWidgetId(Context context, int index) {
        return context
                .getSharedPreferences(GetWidgetIdStorageKey(index), Context.MODE_PRIVATE)
                .getInt(GetWidgetIdStorageKey(index), -1);
    }
}
