package com.xq.tmall.consumer;

import com.xq.tmall.message.Order;
import com.xq.tmall.service.ProductOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        //        consumerGroup = "demo01-A-consumer-group-" + Demo01Message.TOPIC
        consumerGroup = "my-group1", topic = Order.TOPIC
)
public class UnpaidMessageConsumer implements RocketMQListener<Order> {
    private Logger logger = LogManager.getLogger(UnpaidMessageConsumer.class);
    ProductOrderService productOrderService;

    @Override
    public void onMessage(Order order) {
        //触发监听之后去查根据Order.orderId的订单编号去数据库/Redis缓存里查是否付款,没付款取消订单
        logger.info("延迟消息[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), order);
        productOrderService.deleteList(new Integer[]{Integer.valueOf(order.getOrderId())});
    }
}
