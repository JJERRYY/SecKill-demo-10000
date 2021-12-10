package com.xq.tmall.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisService {
	
	@Autowired
	JedisPool jedisPool;
	
	/**
	 * 获取当个对象
	 * */
	public <T> T get(KeyPrefix prefix, String key,  Class<T> clazz) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 //生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 String  str = jedis.get(realKey);
			 T t =  stringToBean(str, clazz);
			 return t;
		 }finally {
			  returnToPool(jedis);
		 }
	}

	public  Long expice(KeyPrefix prefix,String key,int exTime){
		Jedis jedis = null;
		Long result = null;
		try {
			jedis =  jedisPool.getResource();
			result = jedis.expire(prefix.getPrefix()+key,exTime);
			return result;
		} finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 设置对象
	 * */
	public <T> boolean set(KeyPrefix prefix, String key,  T value ,int exTime) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 String str = beanToString(value);
			 if(str == null || str.length() <= 0) {
				 return false;
			 }
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 if(exTime == 0) {
			 	 //直接保存
				 jedis.set(realKey, str);
			 }else {
			 	 //设置过期时间
				 jedis.setex(realKey, exTime, str);
			 }
			 return true;
		 }finally {
			  returnToPool(jedis);
		 }
	}

	//用户锁定商品,用户解锁商品
	public boolean setnx(String key, String val) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis == null) {
				return false;
			}
			return jedis.set(key, val, "NX", "PX", 1000 * 60).
					equalsIgnoreCase("ok");
		} catch (Exception ex) {
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}
	public int delnx(String key, String val) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis == null) {
				return 0;
			}

			//if redis.call('get','orderkey')=='1111' then return redis.call('del','orderkey') else return 0 end
			StringBuilder sbScript = new StringBuilder();
			sbScript.append("if redis.call('get','").append(key).append("')").append("=='").append(val).append("'").
					append(" then ").
					append("    return redis.call('del','").append(key).append("')").
					append(" else ").
					append("    return 0").
					append(" end");

			return Integer.valueOf(jedis.eval(sbScript.toString()).toString());
		} catch (Exception ex) {
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}
	public  Long del(KeyPrefix prefix,String key){
		Jedis jedis = null;
		Long result = null;
		try {
			jedis =  jedisPool.getResource();
			result = jedis.del(prefix.getPrefix()+key);
			return result;
		} finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 判断key是否存在
	 * */
	public <T> boolean exists(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.exists(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.incr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.decr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}

	/**
	 * bean 转 String
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}


	/**
	 * string转bean
	 * @param str
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		 if(jedis != null) {
			 jedis.close();
		 }
	}


	/**
	 * 添加一条记录 如果map_key存在 则更新value
	 * hset 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。
	 * 如果字段已经存在于哈希表中，旧值将被覆盖
	 *
	 * @param pool
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	/**
	public static long set(MyRedisPool pool, String key, String field, String value) {
		Jedis jedis = pool.borrowJedis();
		long returnStatus = jedis.hset(key, field, value);
		pool.returnJedis(jedis);
		return returnStatus;
	}
	*/


	/**
	 * 批量添加记录
	 * hmset 同时将多个 field-value (域-值)对设置到哈希表 key 中。
	 * 此命令会覆盖哈希表中已存在的域。
	 * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
	 *
	 */
	public <T> boolean setAllHash(KeyPrefix prefix, String key, Map<String, T> map, int exTime) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			Map<String, String> mapmap =new HashMap<>();
			map.forEach((k,v) -> mapmap.put(k,beanToString(v)));
			if(exTime == 0) {
				//直接保存
				String result = jedis.hmset(key, mapmap);
			}else {
				//设置过期时间
				String result = jedis.hmset(key, mapmap);
			}
			return true;
		}finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 删除hash中 field对应的值
	 * hdel 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略
	 *

	 */
//	public static long deleteAllHash(MyRedisPool pool, String key, String... field) {
//		Jedis jedis = pool.borrowJedis();
//		long returnStatus = jedis.hdel(key, field);
//		pool.returnJedis(jedis);
//		return returnStatus;
//	}

	/**
	 * 获取hash中 指定的field的值
	 * hmget 返回哈希表 key 中，一个或多个给定域的值。
	 * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
	 * 不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 *

	 */
//	public static List<String> get(MyRedisPool redisPool, String key, String... field) {
//		Jedis jedis = redisPool.borrowJedis();
//		List<String> result = jedis.hmget(key, field);
//		redisPool.returnJedis(jedis);
//		return result;
//	}

	/**
	 * 获取hash中 所有的field value
	 *

	 * @return 在返回值里，紧跟每个字段名(field name)之后是字段的值(value)，所以返回值的长度是哈希表大小的两倍。
	 */
	public <T> Map<String, T> getAllHash(String key, Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			Map<String, String> map = jedis.hgetAll(key);
			Map<String,T>result=new HashMap<>();
			map.forEach((k,v) -> result.put(k,stringToBean(v,clazz)));
			return result;
		}finally {
			returnToPool(jedis);
		}

	}

}
