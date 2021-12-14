package com.xq.tmall.redis;

public class OrderKey extends BasePrefix{
    private OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getOrder = new OrderKey("order");
}
