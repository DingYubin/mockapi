package com.yubin.draw.extend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.yubin.baselibrary.util.ResourceUtil;
import com.yubin.draw.R;
import com.yubin.ecwindow.ECPopWindow;
import com.yubin.ecwindow.ECWindowBackgroundDrawable;
import com.yubin.medialibrary.util.UnitHelper;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2024/07/15
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */
public class QuotationWindowHelper {
    public static void showQuotationCouponWindow(Context mContext, AppCompatTextView view, String cutTip) {

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.inquiry_icon_red_coupon_top);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            view.setCompoundDrawables(null, null, drawable, null);
        }



        AppCompatTextView tvReplace = (AppCompatTextView) LayoutInflater.from(mContext).inflate(R.layout.inquiry_window_replace, null);
        tvReplace.setText(cutTip);
        ECPopWindow pw = new ECPopWindow.Builder(mContext)
                .setContentView(tvReplace)
                .setOffsetEdge(((int) UnitHelper.dp2px(mContext, 10f)))
                .setWindowBackground(
                        new ECWindowBackgroundDrawable(
                                UnitHelper.dp2px(mContext, 6f),
                                UnitHelper.dp2px(mContext, 6f),
                                Paint.Style.FILL,
                                1f,
                                Color.parseColor("#14000000"),
                                Color.parseColor("#FFFFFF")
                        )
                )
                .createWindow();

        pw.show(view);
        pw.addOnWindowStateLister(new ECPopWindow.OnWindowStateLister() {

            @Override
            public void onWindowShow(int i, int i1) {
                // do nothing
            }

            @Override
            public void onDismiss() {
                Drawable drawable1 = ContextCompat.getDrawable(mContext, R.drawable.inquiry_icon_red_coupon_bottom_new);
                if (drawable1 != null) {
                    drawable1.setBounds(0, 0, drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
                    view.setCompoundDrawables(null, null, drawable1, null);
                    view.setCompoundDrawablePadding(ResourceUtil.getDimens(R.dimen.dp_4));
                }
            }
        });
    }
}
