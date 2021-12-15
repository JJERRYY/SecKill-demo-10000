package com.xq.tmall;

import com.xq.tmall.common.Const;
import com.xq.tmall.redis.OrderKey;
import com.xq.tmall.redis.RedisService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.xq.tmall.redis.RedisService.*;

@Component
public class MyDisposableBean implements DisposableBean{
    @Autowired
    RedisService redisService;

    @Override
    public void destroy() throws Exception {
        redisService.set(OrderKey.getOrder,"flag",0, Const.RedisCacheExtime.GOODS_LIST);
        System.out.println("结束");

    }

}