package com.yubin.draw.widget.recyclerView.adapter;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData;


/**
 * description: viewHolder抽象(主要差异数据的绑定)
 * <p>
 * date: created on 2019/2/22
 * <p>
 */
public abstract class CECViewHolder<T extends ICECDiffData> extends RecyclerView.ViewHolder {

    public CECViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 全量绑定数据
     *
     * @param data     数据
     * @param position position
     */
    public abstract void bindDataFully(@NonNull T data, int position, int count);

    /**
     * 差量绑定数据
     *
     * @param payload  差异数据
     * @param position position
     */
    public abstract void bindDiffData(@NonNull T data, @NonNull Bundle payload, int position, int count);
}
