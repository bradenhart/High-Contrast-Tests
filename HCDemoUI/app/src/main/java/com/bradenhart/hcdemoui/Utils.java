package com.bradenhart.hcdemoui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;

/**
 * Created by bradenhart on 3/12/15.
 */
public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabs_height);
    }

    public static void startActivity(Context context, Class<?> theClass) {
        Intent intent = new Intent(context, theClass);
        context.startActivity(intent);
    }
}
