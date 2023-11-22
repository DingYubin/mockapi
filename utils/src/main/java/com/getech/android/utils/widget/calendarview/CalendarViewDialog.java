package com.getech.android.utils.widget.calendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.getech.android.utils.BuildConfig;
import com.getech.android.utils.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 *     @author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2019-2-26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CalendarViewDialog extends BaseDialog {
    MatchParentCalendarView materialCalendarView;
    TextView cancelTextView;
    TextView okTextView;
    Calendar selectedCalendar;
    CalendarClickListener calendarClickListener;
    Calendar minCalendar;
    Calendar maxCalendar;
    OneDayDecorator dayDecorator;

    public void setCalendarClickListener(CalendarClickListener calendarClickListener) {
        this.calendarClickListener = calendarClickListener;
    }

    public CalendarViewDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public CalendarViewDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public CalendarViewDialog(Context context) {
        super(context);
        initView();
    }

    public CalendarViewDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public void setSelectedCalendar(Calendar selectedCalendar) {
        this.selectedCalendar = selectedCalendar;
        materialCalendarView.setSelectedDate(selectedCalendar);
        materialCalendarView.setCurrentDate(selectedCalendar);
    }

    public void setMinCalendar(Calendar minCalendar) {
        this.minCalendar = minCalendar;
        materialCalendarView.state().edit()
                .setMinimumDate(minCalendar).commit();
    }

    public void setMaxCalendar(Calendar maxCalendar) {
        this.maxCalendar = maxCalendar;
        materialCalendarView.state().edit()
                .setMaximumDate(maxCalendar).commit();
    }

    private void initView() {
        dayDecorator = new OneDayDecorator();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_dialog, null);
        materialCalendarView = (MatchParentCalendarView) dialogView.findViewById(R.id.calendarView);
        try {
            // 获取私有成员变量:name
            Field nameField = materialCalendarView.getClass().getSuperclass().getDeclaredField("topbar");
            // 设置操作权限为true
            nameField.setAccessible(true);
            LinearLayout topBar = (LinearLayout) nameField.get(materialCalendarView);
            topBar.setBackgroundColor(getContext().getResources().getColor(R.color.bg_blue_button));
        } catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
//        materialCalendarView.addDecorator(dayDecorator);
        materialCalendarView.setHeaderTextAppearance(R.style.HeadTitleTextAppearance);
        materialCalendarView.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Small);
        materialCalendarView.setDateTextAppearance(R.style.CustomDayTextAppearance);
        materialCalendarView.setSelectionColor(getContext().getResources().getColor(R.color.bg_blue_button));
        materialCalendarView.setTitleAnimationOrientation(MaterialCalendarView.HORIZONTAL);
        cancelTextView = (TextView) dialogView.findViewById(R.id.cancelTextView);
        okTextView = (TextView) dialogView.findViewById(R.id.okTextView);
        cancelTextView.setClickable(true);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarClickListener != null) {
                    calendarClickListener.onCalendarCancelled();
                }
                dismiss();
            }
        });
        okTextView.setClickable(true);
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarClickListener != null) {
                    if (calendarClickListener.onCalendarOk(selectedCalendar)) {
                        dismiss();
                    }
                }
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedCalendar = date.getCalendar();
            }
        });
        setContentView(dialogView);
    }

    public static class CalendarViewDialogBuilder {
        private CalendarClickListener calendarClickListener;
        private Calendar minCalendar;
        private Calendar maxCalendar;
        private Calendar selectedCalendar;
        private CalendarViewDialog dialog;

        public CalendarViewDialogBuilder(Context context) {
            dialog = new CalendarViewDialog(context, R.style.CalendarViewDialog);
        }

        public CalendarViewDialogBuilder setCalendarClickListener(CalendarClickListener calendarClickListener) {
            this.calendarClickListener = calendarClickListener;
            return this;
        }

        public CalendarViewDialogBuilder setMinCalendar(Calendar minCalendar) {
            this.minCalendar = minCalendar;
            return this;
        }

        public CalendarViewDialogBuilder setMaxCalendar(Calendar maxCalendar) {
            this.maxCalendar = maxCalendar;
            return this;
        }

        public CalendarViewDialogBuilder setSelectedCalendar(Calendar selectedCalendar) {
            this.selectedCalendar = selectedCalendar;
            return this;
        }

        public CalendarViewDialog build() {
            dialog.setCalendarClickListener(this.calendarClickListener);
            if (maxCalendar != null) {
                dialog.setMaxCalendar(maxCalendar);
            }
            if (minCalendar != null) {
                dialog.setMinCalendar(minCalendar);
            }
            if (selectedCalendar == null) {
                selectedCalendar = Calendar.getInstance();
            }
            dialog.setSelectedCalendar(selectedCalendar);
            return dialog;
        }

        public void show() {
            build().show();
        }
    }

    public interface CalendarClickListener {
        /**
         * 点击取消键
         */
        void onCalendarCancelled();

        /**
         * 点击确定键
         *
         * @param calendar 已选择的日期
         * @return 如果需要隐藏日期选择dialog，则return true，否则return false
         */
        boolean onCalendarOk(Calendar calendar);
    }

    public static class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.NORMAL));
            view.addSpan(new RelativeSizeSpan(1.4f));
        }

        /**
         * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
         */
        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

}
