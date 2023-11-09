package com.yubin.baselibrary.event;

import java.util.Map;

/**
 * description: 事件
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
