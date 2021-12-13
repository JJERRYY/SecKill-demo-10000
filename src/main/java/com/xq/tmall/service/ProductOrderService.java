package com.xq.tmall.service;

import com.xq.tmall.entity.OrderGroup;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.User;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProductOrderService {
    ProductOrder getSeckillOrderByUserIdGoodsId(Integer productOrder_user_id, Integer productOrder_product_id) throws Exception;

    boolean add(ProductOrder productOrder);
    boolean update(ProductOrder productOrder);
    boolean deleteList(Integer[] productOrder_id_list);

    List<ProductOrder> getList(ProductOrder productOrder, Byte[] productOrder_status_array, OrderUtil orderUtil, PageUtil pageUtil);

    List<OrderGroup> getTotalByDate(Date beginDate, Date endDate);

    ProductOrder get(Integer productOrder_id) throws Exception;
    ProductOrder getByCode(String productOrder_code) throws Exception;
    Integer getTotal(ProductOrder productOrder,Byte[] productOrder_status_array);
    ProductOrder getById(Integer productOrder_id) throws Exception;
//
//    ProductOrder insert(User user , Product product);

    //String createMiaoshaPath(User user, Integer product_id);

//    OrderInfo insert(User user , GoodsBo goodsBo);
//    long getSeckillResult(Integer user_id, Integer product_id);
//
//    boolean checkPath(User user, Integer product_id, String path);

    List<ProductOrder> getSeckillOrdersList();
}
