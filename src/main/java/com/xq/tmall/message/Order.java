package com.xq.tmall.message;

import org.apache.rocketmq.common.message.Message;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Order implements Serializable {

    public Order() {
    }

    public static final String TOPIC = "Unpaid";
    private String orderId;//订单编号
    public Order( String orderId) {
        this.orderId =orderId;
    }

    public static String getTOPIC() {
        return TOPIC;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OrderId=" + orderId +
                '}';
    }
}
