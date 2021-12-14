package com.xq.tmall;

import com.xq.tmall.common.Const;
import com.xq.tmall.entity.Category;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.producer.OrderUnpaidProducer;
import com.xq.tmall.redis.GoodsKey;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.service.CategoryService;
import com.xq.tmall.service.ProductOrderService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource(name="categoryService")
    private CategoryService categoryService;
    @Resource(name = "productOrderService")
    private ProductOrderService productOrderService;
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
    private Map<String, String> map_product_name() throws Exception{
        Map<String, String> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("1" + goods.getProduct_id(),goods.getProduct_name());
        }
        return map;

    }
    private Map<String, String > map_product_title() throws Exception{
        Map<String,String> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("2" + goods.getProduct_id(),goods.getProduct_title());
        }
        return map;

    }
    private Map<String, Double> map_product_sale_price() throws Exception{
        Map<String, Double> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("4" + goods.getProduct_id(),goods.getProduct_sale_price());
        }
        return map;

    }
    private Map<String, Date> map_product_start_date() throws Exception{
        Map<String, Date> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("5" + goods.getProduct_id(),goods.get_start_Date());
        }
        return map;

    }
    private Map<String, Date> map_product_end_date() throws Exception{
        Map<String, Date> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("6" + goods.getProduct_id(),goods.get_end_Date());
        }
        return map;

    }
    private Map<String, Integer> map_product_sale_count() throws Exception{
        Map<String, Integer> map =new HashMap<>();
        List<Product> goodsList = productService.getSeckillGoodsList();
        for (Product goods:goodsList)
        {
            map.put("7" + goods.getProduct_id(),goods.getProduct_sale_count());
        }
        return map;

    }
    private Map<String, String> map_category_name() throws Exception{
        Map<String, String> map =new HashMap<>();
        List<Category> categoryList = categoryService.getSeckillCategoryList();
        for (Category category:categoryList)
        {
            map.put("8" + category.getCategory_id(),category.getCategory_name());
        }
        return map;

    }
    private Map<String, String> map_category_images() throws Exception{
        Map<String, String> map =new HashMap<>();
        List<Category> categoryList = categoryService.getSeckillCategoryList();
        for (Category category:categoryList)
        {
            map.put("9" + category.getCategory_id(),category.getCategory_image_src());
        }
        return map;

    }

    private Map<String,Integer> map_productOrder_id() throws Exception{
        Map<String, Integer> map =new HashMap<>();
        List<ProductOrder> orderList =productOrderService.getSeckillOrdersList();
        for (ProductOrder order:orderList)
        {
            map.put("10" + order.getProductOrder_id(),order.getProductOrder_id());
        }
        return map;

    }

    private void afterPropertiesSet() throws Exception{
//        if()
//        {
            List<Product> goodsList = productService.getSeckillGoodsList();
            redisService.setAllHash("1", map_product_name(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("2", map_product_title(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("4", map_product_sale_price(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("5", map_product_start_date(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("6", map_product_end_date(), Const.RedisCacheExtime.GOODS_LIST);

            redisService.setAllHash("7", map_product_sale_count(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("8", map_category_name(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("9", map_category_images(), Const.RedisCacheExtime.GOODS_LIST);
            redisService.setAllHash("10", map_productOrder_id(), Const.RedisCacheExtime.GOODS_LIST);
            for (Product goods : goodsList) {
                redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getProduct_id(), goods.getProduct_keep_sum(), Const.RedisCacheExtime.GOODS_LIST);

                localOverMap.put(Integer.valueOf(goods.getProduct_id()), false);
            }
//        }





    }

    @Override
    public void run(String... args) throws Exception {
        afterPropertiesSet();
    }
}
