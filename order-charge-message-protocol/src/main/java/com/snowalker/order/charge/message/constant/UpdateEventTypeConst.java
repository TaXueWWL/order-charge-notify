package com.snowalker.order.charge.message.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 14:45
 * @className MessageProtocolConst
 * @desc 消息协议常量
 */
public enum UpdateEventTypeConst {

    /**支付状态更新事件*/
    EVENT_UPDATE_PAY_STATUS("EVENT_UPDATE_PAY_STATUS", "EVENT_UPDATE_PAY_STATUS"),
    /**通知状态及订单状态更新事件*/
    EVENT_UPDATE_NOTIFY_OD_STATUS("EVENT_UPDATE_NOTIFY_OD_STATUS", "EVENT_UPDATE_NOTIFY_OD_STATUS")
    ;

    /**更新事件*/
    private String eventType;
    /**事件描述*/
    private String eventDesc;

    UpdateEventTypeConst(String eventType, String eventDesc) {
        this.eventType = eventType;
        this.eventDesc = eventDesc;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventDesc() {
        return eventDesc;
    }
}
