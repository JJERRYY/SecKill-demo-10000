package com.xq.tmall.controller;

import com.xq.tmall.annotations.*;
import com.xq.tmall.common.Const;
import com.xq.tmall.entity.Product;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.User;
import com.xq.tmall.redis.GoodsKey;
import com.xq.tmall.redis.UserKey;
import com.xq.tmall.service.ProductService;
import com.xq.tmall.util.CookieUtil;
import com.xq.tmall.redis.RedisService;
import com.xq.tmall.service.ProductOrderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.xq.tmall.result.CodeMsg;
import com.xq.tmall.result.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("seckill")
public class SecKillController implements InitializingBean {


    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductOrderService productOrderService;

//    @Autowired
////    MQSender mqSender;

    private HashMap<Integer, Boolean> localOverMap = new HashMap<Integer, Boolean>();

    /**
     * 系统初始化
     */
    public void afterPropertiesSet() throws Exception {
        List<Product> goodsList = productService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (Product goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getProduct_id(), goods.getProduct_real_sum(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(Integer.valueOf(goods.getProduct_id()), false);
        }
    }

    @RequestMapping("/seckill2")
    public String list2(Model model,
                        @RequestParam("Product_id") Integer product_id, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
//        ProductOrder user=redisService.get(UserKey.getByName,loginToken,ProductOrder.class);
        model.addAttribute("user", user);
        if (user == null) {
            return "redirect:/login";
        }
        //判断库存
//        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        Product goods=productService.getseckillGoodsBoByGoodsId(product_id);
        int stock = goods.getProduct_real_sum();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "redirect:/miaosha_fail";
        }
        //判断是否已经秒杀到了

//          SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);

        ProductOrder order = productOrderService.getSeckillOrderByUserIdGoodsId(user.getUser_id(),goods.getProduct_id());
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "redirect:/miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单
//        ProductOrder productOrder = productOrderService.insert(user,goods)


//        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        model.addAttribute("orderInfo", order);
        model.addAttribute("goods", goods);
        return "fore/productBuyPage";
    }

    @RequestMapping(value = "/{path}/seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model,
                                @RequestParam("Product_id") Integer product_id,
                                @PathVariable("path") String path,
                                HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
//        ProductOrder user=redisService.get(UserKey.getByName,loginToken,ProductOrder.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        //验证path
        boolean check = productOrderService.checkPath(user, product_id, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(product_id);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }/**/
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + product_id);//10
        if (stock < 0) {
            localOverMap.put(product_id, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
//        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        ProductOrder order=productOrderService.getSeckillOrderByUserIdGoodsId(user.getUser_id(),product_id);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        else {return null;}
//        入队
//        SeckillMessage mm = new SeckillMessage();
//        mm.setUser(user);
//        mm.setGoodsId(goodsId);
//        mqSender.sendSeckillMessage(mm);
//        return Result.success(0);//排队中
        /*//判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if(goods == null) {
            return Result.error(CodeMsg.NO_GOODS);
        }
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        return Result.success(orderInfo);*/
    }


    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(@RequestParam("Product_id") Integer product_id, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user=redisService.get(UserKey.getByName,loginToken,User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result=productOrderService.getSeckillResult((Integer) user.getUser_id(),product_id);
//        long result = productOrderService.getSeckillResult((long) user.getId(), goodsId);
        return Result.success(result);
    }
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, User user,
                                         @RequestParam("product_id") Integer product_id) {
        String loginToken = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = productOrderService.createMiaoshaPath(user, product_id);
        return Result.success(path);
    }
}
