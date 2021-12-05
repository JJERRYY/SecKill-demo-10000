package com.xq.tmall.message;

import java.io.Serializable;

public class Order implements Serializable {

    public Order() {
    }

    public static final String TOPIC = "Unpaid";
    private Integer orderId;//订单编号
    public Order(Integer orderId) {
        this.orderId =orderId;
    }

    public static String getTOPIC() {
        return TOPIC;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                '}';
    }
}
