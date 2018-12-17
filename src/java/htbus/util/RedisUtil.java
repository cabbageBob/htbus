package htbus.util;

import htbus.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisUtil {
	static Logger logger = Logger.getLogger(UserService.class);
	
	private static JedisPool jedisPool = null;
    // Redis服务器IP
    private static String ADDR = ConfigUtil.config("redis_server_url");
    // Redis的端口号
    private static int PORT = 6379;
    // 访问密码
    private static String AUTH = ConfigUtil.config("redis_server_password");
    
    private static int db = 0 ;
    
    public RedisUtil(int db){
    	this.db = db ;
    }

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
            config.setBlockWhenExhausted(true);
            // 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
            config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
            // 是否启用pool的jmx管理功能, 默认true
            config.setJmxEnabled(true);
            // 最大空闲连接数, 默认8个 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(8);
            // 最大连接数, 默认8个
            config.setMaxTotal(200);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000 * 100);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            if(AUTH.equals("")||AUTH.equals(null)) {
            	jedisPool = new JedisPool(config, ADDR, PORT, 3000);
            }else {
            	jedisPool = new JedisPool(config, ADDR, PORT, 3000,AUTH);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * 
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * 
     * @param jedis
     */
    public static void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
	
	public static void set(String key,String value){
		Jedis jedis = null;
		try{
			value = value == null?"":value;
			jedis = getJedis();
			if(jedis!=null)
			jedis.set(key, value);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static void set(String key, String value,int minute){
		Jedis jedis = null;
		try{
			value = value == null?"":value;
			jedis = getJedis();
			jedis.setex(key, 60 * minute, value);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	/**
	 * @description 毫秒设置过期时间
	 * @param
	 * @return 
	 */
	public static void psetExAt(String key, String value, long unixtime){
		Jedis jedis = null;
		try{
			value = value == null?"":value;
			jedis = getJedis();
			jedis.set(key, value);
			jedis.pexpireAt(key, unixtime);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	/*static boolean reConnect(){
		try{
			logger.info("redis 连接失效");
			logger.info("redis 重新连接");
			initJedis();
			return true;
		}catch(Exception e){
			return false;
		}
	}*/
	
	public static String get(String key){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getJedis();
			if(jedis != null && jedis.exists(key)){  
				result = jedis.get(key);
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
		return result;
	}
	
	public static void del(String key){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			jedis.del(key);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	/**2017年12月11日下午11:16:32
	 * @author Jokki
	 * @description key的剩余生存时间
	 * @param key
	 * @return -1:没有过期时间，-2：不存在key
	 */
	public static Long ttl(String key){
		Jedis jedis = null;
		Long result = null;
		try{
			jedis = getJedis();
			result = jedis.ttl(key);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
		return result;
	}
	
	/**2017年12月13日上午10:22:25
	 * @author Jokki
	 * @description 如果key存在则设置失败
	 * @param 
	 * @return 1：成功，0：失败
	 */
	public static Long setnx(String key, String value, int seconds){
		Jedis jedis = null;
		Long result = null;
		try{
			jedis = getJedis();
			result = jedis.setnx(key, value);
			jedis.expire(key, seconds);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
		return result;
	}
	
	public static void hmset(String key, Map<String, String> hash){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			jedis.hmset(key, hash);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static void hmsetBatch(String setKey, Map<String, Map<String, String>> map){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			for(Map.Entry<String, Map<String, String>> entry : map.entrySet()){
				jedis.hmset(entry.getKey(), entry.getValue());
				jedis.sadd(setKey, entry.getKey());
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static List<Map<String, Object>> hgetList(String setKey, String pattern){
		Jedis jedis = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try{
			jedis = getJedis();
			Set<String> keys = sscan(setKey, pattern);
			for(String key : keys){
				Map<String, Object> map = new HashMap<>();
				map.putAll(jedis.hgetAll(key));
				if(map.size() > 0) {
					list.add(map);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
		return list;
	}
	
	public static void deleteMulti(String setKey){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Set<String> keys = sscan(setKey);
			for(String key : keys){
				jedis.del(key);
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static void hset(String key, String field, String value){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			jedis.hset(key, field, value);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static Map<String, String> hgetall(String key){
		Jedis jedis = null;
		Map<String, String> hash = null;
		try{
			jedis = getJedis();
			hash = jedis.hgetAll(key);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
		return hash;
	}
	
	public static void sadd(String key, List<String> members){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			for(String member: members){
				jedis.sadd(key, member);
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static void sadd(String key, String member){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			jedis.sadd(key, member);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	public static Set<String> sscan(String key, String pattern){
		Jedis jedis = null;
		Set<String> result = new LinkedHashSet<>();
		try{
			jedis = getJedis();
			
			ScanResult<String> scanResuslt = null;
			String cursor = "0";
			ScanParams params = new ScanParams();
			params.count(5000);
			do{
				if(pattern != null){
					params.match(pattern);
					scanResuslt = jedis.sscan(key, cursor, params);
				}else{
					scanResuslt = jedis.sscan(key, cursor);
				}
				cursor = scanResuslt.getStringCursor();
				result.addAll(scanResuslt.getResult());
			}while(!cursor.equals("0"));
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			close(jedis);
		}
		return result;
	}
	
	public static Set<String> sscan(String key){
		return sscan(key, null);
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> result = hgetall("sb");
		System.out.println("");
		//del("/api/4/searchSLGC");
	}
}
