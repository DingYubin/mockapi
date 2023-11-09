package com.casstime.base.event;

import java.util.Map;

/**
 * description: 开思事件实体
 * <p>
 * email: jun.hou@casstime.com
 * <p>
 * date: created on 2019/3/6
 * <p>
 * author:   侯军(A01082)
 */
public class CECEvent {

    /**
     * 事件id，标识事件类型
     */
    private String eventId;

    /**
     * 事件携带参数
     */
    private Map param;

    public CECEvent(String eventId, Map param) {
        this.eventId = eventId;
        this.param = param;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map getParam() {
        return param;
    }

    public void setParam(Map param) {
        this.param = param;
    }
}
