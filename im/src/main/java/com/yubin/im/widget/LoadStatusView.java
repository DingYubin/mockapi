package com.yubin.im.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.yubin.baselibrary.util.ViewUtil;
import com.yubin.im.R;


/**
 * Created by zhaojieb9 on 2017/8/14.
 */

public class LoadStatusView extends FrameLayout {
    /**
     * 正在加载
     */
    public static final int STATUS_LOADING = 0;
    /**
     * 加载失败
     */
    public static final int STATUS_LOAD_FAIL = 1;
    /**
     * 加载成功，但是数据为空
     */
    public static final int STATUS_LOAD_EMPTY = 2;

    public LoadStatusView(Context context) {
        this(context, null, 0);
    }

    public LoadStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private int loadStatus = STATUS_LOADING;

    private LinearLayout  llLoadEmpty;
    private ConstraintLayout llLoading;
    private RelativeLayout llLoadFail;
    private ImageView ivLoading, ivLoadFail, ivLoadEmpty;
    private TextView tvLoading, tvLoadFail, tvLoadEmpty;
    private Button btnRefresh;
    private AnimationDrawable loadingDrawable;

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_load_status_view, this, true);
        llLoading =  root.findViewById(R.id.ll_loading);
        ivLoading = (ImageView) root.findViewById(R.id.iv_loading);
        loadingDrawable = (AnimationDrawable) ivLoading.getDrawable();
        tvLoading = (TextView) root.findViewById(R.id.tv_loading);
        llLoadFail = (RelativeLayout) root.findViewById(R.id.ll_load_fail);
        ivLoadFail = (ImageView) root.findViewById(R.id.iv_load_fail);
        tvLoadFail = (TextView) root.findViewById(R.id.tv_load_fail);
        llLoadEmpty = (LinearLayout) root.findViewById(R.id.ll_load_empty);
        ivLoadEmpty = (ImageView) root.findViewById(R.id.iv_load_empty);
        tvLoadEmpty = (TextView) root.findViewById(R.id.tv_load_empty);
        btnRefresh = (Button) root.findViewById(R.id.btn_refresh);
        if (attrs != null) {
            TypedArray typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.LoadStatusView, defStyleAttr, 0);
            loadStatus = typedArray.getInt(R.styleable.LoadStatusView_status, STATUS_LOADING);
            Drawable emptDrawable = typedArray.getDrawable(R.styleable.LoadStatusView_id_empty_image);
            if (emptDrawable != null) {
                ivLoadEmpty.setImageDrawable(emptDrawable);
            }
            String emptyStr = typedArray.getString(R.styleable.LoadStatusView_id_empty_text);
            if (!TextUtils.isEmpty(emptyStr)) {
                tvLoadEmpty.setText(emptyStr);
            }
            typedArray.recycle();
        }
        checkStatus(loadStatus);
    }

    public static void bindingStatus(LoadStatusView loadStatusView,int status){
        loadStatusView.checkStatus(status);
    }

    public void checkStatus(int loadStatus) {

        switch (loadStatus) {
            case STATUS_LOADING:
                ViewUtil.gone(llLoadFail);
                ViewUtil.gone(llLoadEmpty);
                ViewUtil.visiable(llLoading);
                if (loadingDrawable != null && !loadingDrawable.isRunning()) {
                    loadingDrawable.start();
                }
                this.setClickable(false);
                break;

            case STATUS_LOAD_FAIL:
                ViewUtil.gone(llLoading);
                ViewUtil.visiable(llLoadFail);
                ViewUtil.gone(llLoadEmpty);
                if (loadingDrawable != null && loadingDrawable.isRunning()) {
                    loadingDrawable.stop();
                }
                this.setClickable(true);
                break;

            case STATUS_LOAD_EMPTY:
                ViewUtil.gone(llLoading);
                ViewUtil.gone(llLoadFail);
                ViewUtil.visiable(llLoadEmpty);
                if (loadingDrawable != null && loadingDrawable.isRunning()) {
                    loadingDrawable.stop();
                }
                this.setClickable(true);
                break;
            default:
                this.setClickable(false);
                break;
        }
    }

    public void setEmptyImageResource(int resId) {
        if (resId > 0) ivLoadEmpty.setImageResource(resId);
    }

    public void setEmptyString(String emptyString) {
        if (!TextUtils.isEmpty(emptyString)) tvLoadEmpty.setText(emptyString);
    }

    public void startLoading() {
        this.loadStatus = STATUS_LOADING;
        checkStatus(loadStatus);
    }

    public void loadComplete(boolean emptyData) {
        if (emptyData) {
            this.loadStatus = STATUS_LOAD_EMPTY;
        } else {
            this.loadStatus = STATUS_LOAD_FAIL;
        }
        checkStatus(loadStatus);
    }

    public void hideBtnRefresh() {
        ViewUtil.gone(btnRefresh);
    }

    public void noNetwork() {
        this.loadStatus = STATUS_LOAD_FAIL;
        ivLoadFail.setImageResource(R.drawable.iv_no_network);
        tvLoadFail.setText(R.string.tv_bad_network_try_later_again);
        checkStatus(loadStatus);
    }

    public void emptyData() {
        this.loadStatus = STATUS_LOAD_EMPTY;
        ivLoadEmpty.setImageResource(R.drawable.ic_svg_search_empty_data);
        tvLoadEmpty.setText(R.string.tv_empty_data);
        checkStatus(loadStatus);
    }

    public void emptyGroup() {
        this.loadStatus = STATUS_LOAD_EMPTY;
        ivLoadEmpty.setImageResource(R.drawable.iv_empty_group);
        tvLoadEmpty.setText(R.string.tv_empty_group);
        checkStatus(loadStatus);
    }

    public void emptyContacts() {
        this.loadStatus = STATUS_LOAD_EMPTY;
        ivLoadEmpty.setImageResource(R.drawable.iv_empty_contacts);
        tvLoadEmpty.setText(R.string.tv_empty_start_search_contacts);
        checkStatus(loadStatus);
    }


}
