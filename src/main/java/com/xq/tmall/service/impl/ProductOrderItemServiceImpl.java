package com.xq.tmall.service.impl;

import com.xq.tmall.dao.ProductOrderItemMapper;
import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.ProductOrderItem;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.service.ProductOrderItemService;
import com.xq.tmall.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xq.tmall.common.Const;
import com.xq.tmall.redis.RedisService;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xq.tmall.util.SerializeUtil;
@Service("productOrderItemService")
public class ProductOrderItemServiceImpl implements ProductOrderItemService{
    @Autowired
    RedisService redisService;


    @Resource(name = "productOrderItemService")
    private ProductOrderItemService productOrderItemService;

    private ProductOrderItemMapper productOrderItemMapper;
    @Resource(name = "productOrderItemMapper")
    public void setProductOrderItemMapper(ProductOrderItemMapper productOrderItemMapper) {
        this.productOrderItemMapper = productOrderItemMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean add(ProductOrderItem productOrderItem) {
        return productOrderItemMapper.insertOne(productOrderItem)>0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean update(ProductOrderItem productOrderItem) {
        return productOrderItemMapper.updateOne(productOrderItem)>0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean deleteList(Integer[] productOrderItem_id_list) {
        return productOrderItemMapper.deleteList(productOrderItem_id_list)>0;
    }

    @Override
    public List<ProductOrderItem> getList(PageUtil pageUtil) {
        return productOrderItemMapper.select(pageUtil);
    }

    @Override
    public List<ProductOrderItem> getListByOrderId(Integer order_id, PageUtil pageUtil) {
        return productOrderItemMapper.selectByOrderId(order_id,pageUtil);
    }

    @Override
    public List<ProductOrderItem> getListByUserId(Integer user_id, PageUtil pageUtil) {
        return productOrderItemMapper.selectByUserId(user_id,pageUtil);
    }

    @Override
    public List<ProductOrderItem> getListByProductId(Integer product_id, PageUtil pageUtil) {
        return productOrderItemMapper.selectByProductId(product_id,pageUtil);
    }

    private Map<String, Object> map_productOrderItem_id() throws Exception{
        Map<String, Object> map =new HashMap<>();
        List<ProductOrderItem> productOrderItems = productOrderItemService.getSeckillOrderItemList();
        for (ProductOrderItem productOrderItem:productOrderItems)
        {
            map.put("" + productOrderItem.getProductOrderItem_id(),productOrderItem);
        }
        return map;

    }

    @Override
    public ProductOrderItem get(Integer productOrderItem_id) throws Exception{
        byte[] productOrderItem=redisService.getAllHash("" + productOrderItem_id,byte[].class).get("" + productOrderItem_id);
        ProductOrderItem resultProductOrderItem = (ProductOrderItem)SerializeUtil.unserialize(productOrderItem);
        if (resultProductOrderItem!=null)
        {
            return resultProductOrderItem;
        }
//        map_productOrder_id();
        resultProductOrderItem=productOrderItemMapper.selectOne(productOrderItem_id);
        if (resultProductOrderItem!=null)
        {
            redisService.setAllHash("" +productOrderItem_id,map_productOrderItem_id(),Const.RedisCacheExtime.GOODS_LIST);
        }
        System.out.println("订单item信息id");
        System.out.println(resultProductOrderItem.getProductOrderItem_id());
        return resultProductOrderItem;
//        return productOrderItemMapper.selectOne(productOrderItem_id);
    }

    @Override
    public Integer getTotal() {
        return productOrderItemMapper.selectTotal();
    }

    @Override
    public Integer getTotalByOrderId(Integer order_id) {
        return productOrderItemMapper.selectTotalByOrderId(order_id);
    }

    @Override
    public Integer getTotalByUserId(Integer user_id) {
        return productOrderItemMapper.selectTotalByUserId(user_id);
    }

    @Override
    public List<OrderGroup> getTotalByProductId(Integer product_id, Date beginDate, Date endDate) {
        return productOrderItemMapper.getTotalByProductId(product_id,beginDate,endDate);
    }

    @Override
    public Integer getSaleCountByProductId(Integer product_id) {
        return productOrderItemMapper.selectSaleCount(product_id);
    }

    @Override
    public List<ProductOrderItem> getSeckillOrderItemList() {
        return productOrderItemMapper.selectAllOrderItem();
    }
}
