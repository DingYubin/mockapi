package com.getech.android.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.getech.android.utils.widget.calendarview.CalendarViewDialog;
import com.getech.android.utils.widget.calendarview.SelectDateParamException;

import java.util.Calendar;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DialogUtils {
    public static Dialog showDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        return showDialog(activity, activity.getString(R.string.waite));
    }

    public static Dialog showDialog(Activity activity, String msg) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        Dialog dialog = new Dialog(activity, R.style.common_loading_dialog);
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.dialog_layout, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.progressImageView);
        AnimationDrawable loadingAnimation;
        loadingAnimation = (AnimationDrawable) imageView.getDrawable();

        loadingAnimation.start();
        TextView tv = (TextView) v.findViewById(R.id.dialog_text);
        if (!TextUtils.isEmpty(msg)) {
            tv.setText(msg);
        }
        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = SizeUtils.dp2px(90);
        lp.height = SizeUtils.dp2px(90);
        dialog.setCancelable(true);
        dialogWindow.setAttributes(lp);
        return dialog;
    }

    /**
     * 选择日期
     *
     * @param activity              activity
     * @param maxDate               最大日期
     * @param minDate               最小日期
     * @param selectDate            已经选中日期
     * @param calendarClickListener 点击取消，确定按钮回调
     */
    public static void selectDate(Activity activity, long maxDate, long minDate, long selectDate, CalendarViewDialog.CalendarClickListener calendarClickListener) {
        if (maxDate != 0L && maxDate < minDate) {
            throw new SelectDateParamException("max date is smaller than min date");
        }
        CalendarViewDialog.CalendarViewDialogBuilder builder = new CalendarViewDialog.CalendarViewDialogBuilder(activity);
        builder.setCalendarClickListener(calendarClickListener);
        if (maxDate > 0) {
            Calendar maxCalendar = Calendar.getInstance();
            maxCalendar.setTimeInMillis(maxDate);
            builder.setMaxCalendar(maxCalendar);
        }
        if (minDate > 0) {
            Calendar minCalendar = Calendar.getInstance();
            minCalendar.setTimeInMillis(minDate);
            builder.setMinCalendar(minCalendar);
        }
        if (selectDate != 0) {
            Calendar currentSelectCalendar = Calendar.getInstance();
            currentSelectCalendar.setTimeInMillis(selectDate);
            builder.setSelectedCalendar(currentSelectCalendar);
        }
        builder.show();
    }

}
