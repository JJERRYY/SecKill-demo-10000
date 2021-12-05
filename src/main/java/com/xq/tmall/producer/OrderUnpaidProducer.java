package com.xq.tmall.producer;

import com.xq.tmall.message.Order;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/*
MQ消息生产者,产生订单超时未付款的消息
 */
@Component
public class OrderUnpaidProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void asyncSend(Integer id, int delayLevel,  SendCallback callback) {


        Message message = MessageBuilder.withPayload(new Order(id)).build();

        rocketMQTemplate.asyncSend(Order.TOPIC,message,callback,3000,delayLevel);
    }

}
