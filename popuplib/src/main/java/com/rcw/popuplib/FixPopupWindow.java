package com.rcw.popuplib;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by ruancw on 2018/5/30.
 * 重新定义popupWindow,解决便宜版本过高，popupWindow的位置不在控件下方的问题
 */

public class FixPopupWindow extends PopupWindow {
    public FixPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if(Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

}
