package com.yubin.draw.widget.recyclerView.adapter.protocol;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * description: 数据源比较的接口定义(需要实现该接口),并且为了比较内容(显示内容)是否一致。实现房比较重写equals方法
 * <p>
 * date: created on 2019/2/22
 * <p>
 */
public interface ICECDiffData extends Serializable {

    /**
     * 同一个对象的唯一标识
     *
     * @return 对象唯一标识
     */
    public String getItemSameId();

    /**
     * 获取diff数据
     *
     * @param other 其他对象
     * @return diff数据
     */
    @Nullable
    public abstract Bundle getChangePayload(ICECDiffData other);
}
