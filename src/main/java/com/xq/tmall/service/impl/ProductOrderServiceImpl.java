package com.xq.tmall.service.impl;

import com.xq.tmall.dao.ProductOrderMapper;
import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.ProductOrderService;
import com.xq.tmall.service.UserService;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import com.xq.tmall.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xq.tmall.util.MD5Util;
import javax.annotation.Resource;
import java.util.*;

import com.xq.tmall.common.Const;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.util.SerializeUtil;
import com.xq.tmall.redis.SeckillKey;

@Service("productOrderService")
public class ProductOrderServiceImpl implements ProductOrderService{
    @Autowired
    RedisService redisService;

    @Resource(name = "productOrderService")
    private ProductOrderService productOrderService;

    private ProductOrderMapper productOrderMapper;
    @Resource(name = "productOrderMapper")

    public void setProductOrderMapper(ProductOrderMapper productOrderMapper) {
        this.productOrderMapper = productOrderMapper;
    }

    @Override
    public List<ProductOrder> getSeckillOrdersList() {
        return productOrderMapper.selectAllOrders();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)

    @Override
    public boolean add(ProductOrder productOrder) {



        return productOrderMapper.insertOne(productOrder)>0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean update(ProductOrder productOrder) {
        return productOrderMapper.updateOne(productOrder)>0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean deleteList(Integer[] productOrder_id_list) {
        return productOrderMapper.deleteList(productOrder_id_list)>0;
    }

    @Override
    public List<ProductOrder> getList(ProductOrder productOrder, Byte[] productOrder_status_array, OrderUtil orderUtil, PageUtil pageUtil) {
        return productOrderMapper.select(productOrder,productOrder_status_array,orderUtil,pageUtil);
    }



    @Override
    public List<OrderGroup> getTotalByDate(Date beginDate, Date endDate) {
        return productOrderMapper.getTotalByDate(beginDate,endDate);
    }

    private Map<String, byte[]> map_productOrder_id() throws Exception{
        Map<String, byte[]> map =new HashMap<>();
        List<ProductOrder> orderList = productOrderService.getSeckillOrdersList();
        for (ProductOrder order:orderList)
        {
            map.put("" + order.getProductOrder_id(), SerializeUtil.serialize(order));
        }
        return map;

    }

    @Override
    public ProductOrder get(Integer productOrder_id) throws Exception{

        byte[] productOrder=redisService.getAllHash("" + productOrder_id,byte[].class).get("" + productOrder_id);
        ProductOrder resultProductOrder = (ProductOrder)SerializeUtil.unserialize(productOrder);
        if (resultProductOrder!=null)
        {
            return resultProductOrder;
        }
        map_productOrder_id();
        resultProductOrder=productOrderMapper.selectById(productOrder_id);
        if (resultProductOrder!=null)
        {
            redisService.setAllHash("" +productOrder_id,map_productOrder_id(),Const.RedisCacheExtime.GOODS_LIST);
        }
        System.out.println("订单信息id");
        System.out.println(resultProductOrder.getProductOrder_id());
        return resultProductOrder;
//        return productOrderMapper.selectOne(productOrder_id);
    }
//
    private Map<String, Object> map_productOrder_code() throws Exception{
        Map<String, Object> map =new HashMap<>();
        List<ProductOrder> orderList = productOrderService.getSeckillOrdersList();
        for (ProductOrder order:orderList)
        {
            map.put("" + order.getProductOrder_code(),order);
        }
        return map;

    }

    @Override
    public ProductOrder getByCode(String productOrder_code) throws Exception{

        byte[] productOrder=redisService.getAllHash("" + productOrder_code,byte[].class).get(productOrder_code);
        ProductOrder resultProductOrder1 = (ProductOrder)SerializeUtil.unserialize(productOrder);
        if (resultProductOrder1!=null)
        {
            return resultProductOrder1;
        }
        map_productOrder_code();
        resultProductOrder1=productOrderMapper.selectByCode(productOrder_code);
        if (resultProductOrder1!=null)
        {
            redisService.setAllHash("" +productOrder_code,map_productOrder_code(),Const.RedisCacheExtime.GOODS_LIST);
        }
        System.out.println("订单信息code");
        System.out.println("1"+resultProductOrder1.getProductOrder_code());
        return resultProductOrder1;
//        return productOrderMapper.selectByCode(productOrder_code);
    }

    @Override
    public Integer getTotal(ProductOrder productOrder, Byte[] productOrder_status_array) {
        return productOrderMapper.selectTotal(productOrder,productOrder_status_array);
    }

    @Override
    public ProductOrder getById(Integer productOrder_id) throws Exception {
        return productOrderMapper.selectById(productOrder_id);
    }

    private Map<String, Object> map_productOrder_good_user() throws Exception{
        Map<String, Object> map =new HashMap<>();
        List<ProductOrder> orderList = productOrderService.getSeckillOrdersList();
        for (ProductOrder order:orderList)
        {
            map.put("" + order.getProductOrder_user()+""+order.getProductOrder_product(),order);
        }
        return map;

    }

    @Override
    public ProductOrder getSeckillOrderByUserIdGoodsId(Integer productOrder_user_id, Integer productOrder_product_id) throws Exception{
        byte[] productOrder=redisService.getAllHash("" + productOrder_user_id+""+productOrder_product_id,byte[].class).get("" + productOrder_user_id+""+productOrder_product_id);
        ProductOrder resultProductOrder1 = (ProductOrder)SerializeUtil.unserialize(productOrder);
        if (resultProductOrder1!=null)
        {
            return resultProductOrder1;
        }
        map_productOrder_good_user();
        resultProductOrder1=productOrderMapper.selectByUserIdAndGoodsId(productOrder_user_id,productOrder_product_id);
        if (resultProductOrder1!=null)
        {
            redisService.setAllHash("" + productOrder_user_id+""+productOrder_product_id,map_productOrder_good_user(),Const.RedisCacheExtime.GOODS_LIST);
        }
        System.out.println("订单用户及其商品信息");
        System.out.println("1"+resultProductOrder1.getProductOrder_user());

//        return productOrderMapper.selectByUserIdAndGoodsId(productOrder_user_id,productOrder_product_id);
        return resultProductOrder1;
    }
//    public String createMiaoshaPath(User user, Integer product_id) {
//        if(user == null || product_id <=0) {
//            return null;
//        }
//        String str = MD5Util.md5(UUID.randomUUID()+"123456");
//        redisService.set(SeckillKey.getSeckillPath, ""+user.getUser_id() + "_"+ product_id, str , Const.RedisCacheExtime.GOODS_ID);
//        return str;
//    }

//    public long getSeckillResult(Integer user_id,Integer product_id) {
//        ProductOrder order = getSeckillOrderByUserIdGoodsId(user_id, product_id);
//        if(order != null) {//秒杀成功
//            return order.getProductOrder_id();
//        }else {
//            boolean isOver = getGoodsOver(product_id);
//            if(isOver) {
//                return -1;
//            }else {
//                return 0;
//            }
//        }
//    }

//    private boolean getGoodsOver(Integer product_id) {
//
//        return redisService.exists(SeckillKey.isGoodsOver, ""+product_id);
//    }

//    public boolean checkPath(User user, Integer product_id, String path) {
//        if(user == null || path == null) {
//            return false;
//        }
//        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getUser_id() + "_"+ product_id, String.class);
//        return path.equals(pathOld);
//    }





}
