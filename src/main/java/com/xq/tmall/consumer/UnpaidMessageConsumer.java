package com.xq.tmall.consumer;

import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.message.Order;
import com.xq.tmall.redis.GoodsKey;
import com.xq.tmall.result.CodeMsg;
import com.xq.tmall.result.Result;
import com.xq.tmall.service.ProductOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.result.CodeMsg;
import java.util.HashMap;
import com.xq.tmall.result.Result;
@Component
@RocketMQMessageListener(
        //        consumerGroup = "demo01-A-consumer-group-" + Demo01Message.TOPIC
        consumerGroup = "my-group1", topic = Order.TOPIC
)
public class UnpaidMessageConsumer implements RocketMQListener<Order> {
    private Logger logger = LogManager.getLogger(UnpaidMessageConsumer.class);
    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    RedisService redisService;
    private HashMap<Integer, Boolean> localOverMap = new HashMap<Integer, Boolean>();
    @Override
    public void onMessage(Order order) {
        //触发监听之后去查根据Order.orderId的订单编号去数据库/Redis缓存里查是否付款,没付款取消订单
        logger.info("延迟消息[onMessage][线程编号:{} 内容：{}]", Thread.currentThread().getId(), order);
        try {
            if (productOrderService.get(order.getOrderId()).getProductOrder_status()==0){
                logger.info("订单ID{}超时未付款，关闭订单",order.getOrderId());
                ProductOrder productOrder = new ProductOrder()
                        .setProductOrder_id(order.getOrderId())
                        .setProductOrder_status((byte) 4);
                boolean yn = productOrderService.update(productOrder);

    //            productOrderService.deleteList(new Integer[]{order.getOrderId()});
                long stock = redisService.incr(GoodsKey.getSeckillGoodsStock, "" + order.getOrderId());

            }else{
                logger.info("订单id{}已付款",order.getOrderId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
