package com.xq.tmall.dao;

import com.xq.tmall.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SecKillGoodsMapper {
//    int deleteByPrimaryKey(Long id);
//
//    int insert(SeckillGoods record);
//
//    int insertSelective(SeckillGoods record);


//    int updateByPrimaryKeySelective(SeckillGoods record);
//
//    int updateByPrimaryKey(SeckillGoods record);

    List<Product>  selectSecKillGoods();
}