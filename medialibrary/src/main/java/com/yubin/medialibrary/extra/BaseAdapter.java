package com.yubin.medialibrary.extra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author laiwei
 */
public abstract class BaseAdapter<T extends IDiffData, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {

    private List<T> mList;
    private LayoutInflater mLayoutInflater;

    public BaseAdapter() {
        mList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T itemData = mList.get(position);
        if (itemData == null) {
            return;
        }
        holder.bindDataFully(itemData, position, getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position);
            return;
        }
        Bundle payload = (Bundle) payloads.get(0);
        if (payload.isEmpty()) {
            return;
        }
        T itemData = mList.get(position);
        holder.bindDiffData(itemData, payload, position, getItemCount());
    }

    /**
     * 数据刷新
     *
     * @param listData 最新列表数据 {@link List}
     */
    public void submitList(List<T> listData) {
        if (listData == null) {
            return;
        }
        mList = listData;
        this.notifyDataSetChanged();
    }

    /**
     * 获取LayoutInflater
     *
     * @param parent holder的itemView的父视图容器
     * @return layoutInflater {@link LayoutInflater}
     */
    protected LayoutInflater getLayoutInflater(@NonNull ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        return mLayoutInflater;
    }
}
