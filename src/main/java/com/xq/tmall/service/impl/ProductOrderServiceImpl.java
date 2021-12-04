package com.xq.tmall.service.impl;

import com.xq.tmall.dao.ProductOrderMapper;
import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.ProductOrderService;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xq.tmall.util.MD5Util;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.xq.tmall.common.Const;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.redis.SeckillKey;

@Service("productOrderService")
public class ProductOrderServiceImpl implements ProductOrderService{
    @Autowired
    RedisService redisService;
    private ProductOrderMapper productOrderMapper;
    @Resource(name = "productOrderMapper")
    public void setProductOrderMapper(ProductOrderMapper productOrderMapper) {
        this.productOrderMapper = productOrderMapper;
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

    @Override
    public ProductOrder get(Integer productOrder_id) {
        return productOrderMapper.selectOne(productOrder_id);
    }

    @Override
    public ProductOrder getByCode(String productOrder_code) {
        return productOrderMapper.selectByCode(productOrder_code);
    }

    @Override
    public Integer getTotal(ProductOrder productOrder, Byte[] productOrder_status_array) {
        return productOrderMapper.selectTotal(productOrder,productOrder_status_array);
    }

    @Override
    public ProductOrder getSeckillOrderByUserIdGoodsId(Integer productOrder_user_id, Integer productOrder_product_id) {
        return productOrderMapper.selectByUserIdAndGoodsId(productOrder_user_id,productOrder_product_id);
    }
    public String createMiaoshaPath(User user, Integer product_id) {
        if(user == null || product_id <=0) {
            return null;
        }
        String str = MD5Util.md5(UUID.randomUUID()+"123456");
        redisService.set(SeckillKey.getSeckillPath, ""+user.getUser_id() + "_"+ product_id, str , Const.RedisCacheExtime.GOODS_ID);
        return str;
    }

    public long getSeckillResult(Integer user_id,Integer product_id) {
        ProductOrder order = getSeckillOrderByUserIdGoodsId(user_id, product_id);
        if(order != null) {//秒杀成功
            return order.getProductOrder_id();
        }else {
            boolean isOver = getGoodsOver(product_id);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(Integer product_id) {

        return redisService.exists(SeckillKey.isGoodsOver, ""+product_id);
    }

    public boolean checkPath(User user, Integer product_id, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getUser_id() + "_"+ product_id, String.class);
        return path.equals(pathOld);
    }





}
