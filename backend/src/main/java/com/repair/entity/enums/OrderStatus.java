package com.repair.entity.enums;

public enum OrderStatus {
    PENDING_ACCEPT(0, "待接单"),
    ACCEPTED(1, "已接单"),
    VISITED(2, "已上门"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    TIMEOUT_RELEASE(5, "超时释放");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
