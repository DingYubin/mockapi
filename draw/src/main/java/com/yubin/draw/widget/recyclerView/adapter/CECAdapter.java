package com.yubin.draw.widget.recyclerView.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yubin.draw.widget.recyclerView.adapter.protocol.ICECDiffData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * description: RecyclerView适配器(以后需要继承该适配器，就已经具备异步diff比较刷新数据和局部刷新数据)
 * <p>
 * date: created on 2019/2/22
 * <p>
 * author:   侯军(A01082)
 */
public abstract class CECAdapter<T extends ICECDiffData, VH extends CECViewHolder<T>> extends RecyclerView.Adapter<VH> {

    private List<T> mList;
    private LayoutInflater mLayoutInflater;

    public CECAdapter() {
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
     * 获取适配器的数据
     *
     * @return list数据 {@link List}
     */
    public List<T> submitList() {
        if (mList == null) {
            return Collections.emptyList();
        }
        return mList;
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

    public int indexOf(T t) {
        return mList.indexOf(t);
    }

    public List<T> getData() {
        return mList;
    }
}
