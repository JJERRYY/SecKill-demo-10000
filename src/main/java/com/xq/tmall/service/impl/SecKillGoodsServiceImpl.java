package com.xq.tmall.service.impl;


import com.xq.tmall.dao.SecKillGoodsMapper;
import com.xq.tmall.entity.Product;
import com.xq.tmall.service.SecKillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by: HuangFuBin
 * Date: 2018/7/12
 * Time: 19:47
 * Such description:
 */

@Service("seckillGoodsService")
public class SecKillGoodsServiceImpl implements SecKillGoodsService {
    @Autowired
    private SecKillGoodsMapper secKillGoodsMapper;
    @Override
    public List<Product> getSecKillGoodsList() {
        return secKillGoodsMapper.selectSecKillGoods();
    }


}
