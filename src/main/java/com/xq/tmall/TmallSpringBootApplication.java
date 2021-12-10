package com.xq.tmall;

import com.xq.tmall.common.Const;
import com.xq.tmall.entity.Product;
import com.xq.tmall.producer.OrderUnpaidProducer;
import com.xq.tmall.redis.GoodsKey;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.service.ProductService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 项目启动类入口
 *
 * 配置文件说明：
 * SpringBoot配置文件地址：    resources/application.properties
 * 数据库(mysql)连接配置文件地址：  resources/jdbc.properties
 * log日志配置文件地址：     resources/log4j2.xml
 */
@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan
@MapperScan("com.xq.tmall.dao")
public class TmallSpringBootApplication extends SpringBootServletInitializer implements CommandLineRunner {
    @Autowired
    RedisService redisService;
    @Autowired
    OrderUnpaidProducer orderUnpaidProducer;
    @Resource(name = "productService")
    private ProductService productService;

    private HashMap<Integer, Boolean> localOverMap = new HashMap<Integer, Boolean>();
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TmallSpringBootApplication.class);
    }

    public static void main(String[] args) {

        SpringApplication.run(TmallSpringBootApplication.class,args);


    }
    /**
     * 系统初始化
     */
    private void afterPropertiesSet() throws Exception {
        List<Product> goodsList = productService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (Product goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getProduct_id(), goods.getProduct_keep_sum(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(Integer.valueOf(goods.getProduct_id()), false);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        afterPropertiesSet();
    }
}
