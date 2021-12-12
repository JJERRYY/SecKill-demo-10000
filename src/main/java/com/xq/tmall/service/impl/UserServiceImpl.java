package com.xq.tmall.service.impl;

import com.xq.tmall.common.Const;
import com.xq.tmall.dao.UserMapper;
import com.xq.tmall.entity.ProductOrder;
import com.xq.tmall.entity.User;
import com.xq.tmall.service.ProductOrderService;
import com.xq.tmall.service.UserService;
import com.xq.tmall.util.OrderUtil;
import com.xq.tmall.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.xq.tmall.redis.RedisService;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xq.tmall.util.SerializeUtil;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean add(User user) {
        return userMapper.insertOne(user)>0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean update(User user) {
        return userMapper.updateOne(user)>0;
    }

    @Override
    public List<User> getList(User user, OrderUtil orderUtil, PageUtil pageUtil) {
        return userMapper.select(user,orderUtil,pageUtil);
    }



//    private Map<String, byte[]> map_user_id() throws Exception{
//        Map<String, byte[]> map =new HashMap<>();
//        List<User> userList = userService.getSeckillUsersList();
//        for (User user:userList)
//        {
//            map.put(""+user.getUser_id(),SerializeUtil.serialize(user));
//        }
//        return map;
//
//    }

    @Override
    public User get(Integer user_id) throws Exception{
//        byte[] user= redisService.getAllHash(""+ user_id,byte[].class).get(""+user_id);
//        User resultUser=(User)SerializeUtil.unserialize(user);
//
//        if (resultUser!=null)
//        {
//            return resultUser;
//
//        }
//        map_user_id();
//        if (resultUser!=null)
//        {
//            redisService.setAllHash("" + user_id,map_user_id(), Const.RedisCacheExtime.GOODS_LIST);
//        }
//        System.out.println("用户信息");
//        System.out.println(resultUser.getUser_id());
//        return resultUser;
        return userMapper.selectOne(user_id);
    }

    @Override
    public User login(String user_name, String user_password) {
        return userMapper.selectByLogin(user_name,user_password);
    }

    @Override
    public Integer getTotal(User user) {
        return userMapper.selectTotal(user);
    }

    @Override
    public List<User> getSeckillUsersList() {
        return userMapper.selectAllUsers();
    }


}
