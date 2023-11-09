package com.casstime.base.event;

import org.greenrobot.eventbus.EventBus;

/**
 * description: 开思是事件总线辅助类
 * <p>
 * email: jun.hou@casstime.com
 * <p>
 * date: created on 2019/3/6
 * <p>
 * author:   侯军(A01082)
 */
public class CECEventBusHelper {

    /**
     * 注册事件观察者
     *
     * @param subscriber 事件观察者
     */
    public static void register(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        if (EventBus.getDefault().isRegistered(subscriber)) {
            return;
        }
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 注销事件观察者
     *
     * @param subscriber 事件观察者
     */
    public static void unRegister(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            return;
        }
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 发送事件
     */
    public static void post(CECEvent event) {
        if (event == null) {
            return;
        }
        EventBus.getDefault().post(event);
    }

    private CECEventBusHelper() {
    }
}
