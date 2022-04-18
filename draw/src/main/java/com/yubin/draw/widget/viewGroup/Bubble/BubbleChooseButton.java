package com.yubin.draw.widget.viewGroup.Bubble;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yubin.draw.R;


/**
 * Created by yubin.ding on 2017/5/26.
 * 多方法回调
 * 单方法回调
 *
 */

public class BubbleChooseButton extends LinearLayout {

    private BubblePopupWindow centerWindow;
    private OnTextViewListener listener;
    private OnOnlyListener onlyListener;

    public BubbleChooseButton(Context context) {
        this(context, null);
    }


    public BubbleChooseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        centerWindow = new BubblePopupWindow(context);
    }

    /**
     * 在lambda表达式，只支持单抽象方法模型，也就是说设计的接口里面只有一个抽象的方法，才符合lambda表达式的规则，多个回调方法不支持。
     */
    public void setOnlyListener(OnOnlyListener onlyListener) {
        this.onlyListener = onlyListener;
        onlyCallback();
    }

    private void onlyCallback() {
        onlyListener.onClick("lambda表达式，只支持单抽象方法模型");
    }

    public void setViewAndCallback(View view, OnTextViewListener listener) {
        this.listener = listener;
        initView(view);
    }

    private void initView(View longView) {
        final View bubbleView = View.inflate(getContext(), R.layout.top_layout_popup_view, null);
        final TextView deleteTv = bubbleView.findViewById(R.id.delete);

        deleteTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.delete();
                    closePopupWindow();
                }
            }
        });

        bubbleView.findViewById(R.id.top).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.top();
                    closePopupWindow();
                }
            }
        });

        bubbleView.findViewById(R.id.more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.more();
                    closePopupWindow();
                }
            }
        });

        centerWindow.setBubbleView(bubbleView);
        centerWindow.show(longView, Gravity.TOP, 30);
    }

    private void closePopupWindow() {
        if (centerWindow.isShowing())
            centerWindow.dismiss();
    }

    public interface OnOnlyListener {
        void onClick(String message);
    }

    public interface OnTextViewListener {
        void delete();

        void top();

        void more();
    }
}
