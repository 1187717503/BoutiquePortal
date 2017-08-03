package pk.shoplus.service;

import com.alibaba.fastjson.JSON;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RMap;

import pk.shoplus.util.RedisUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class RedisService {
	private static final Logger LOGGER = Logger.getLogger(RedisService.class);
	
    public static final String OSS_CONFIG_FILE = "/redisson.properties";

	private static String IP;

	private static String PORT;

	private static Integer POOL_SIZE;
	
    private static RedisService redisService;
    
    private static Redisson redisson;

    private RedisService(){}
    
    

    static {
        // OSS
        InputStream in = RedisService.class.getResourceAsStream(OSS_CONFIG_FILE);
        Properties props = new Properties();
        try {
            props.load(in);

            IP=props.getProperty("mns.ip");
            PORT=props.getProperty("mns.port");
            POOL_SIZE =Integer.parseInt(props.getProperty("mns.poolSize"));

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.toString());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    

    /**
     * 提供单例模式
     * @return
     */
    public static RedisService getInstance(){
        if(redisService==null)
            synchronized (RedisService.class) {
                if(redisService==null) redisService=new RedisService();
            }
        return redisService;
    }

    
    public Redisson getRedisson() {
    	if(redisson == null){
    		synchronized (RedisService.class) {
    			if(redisson == null){
    				Config config = new Config();
    				config.setConnectionPoolSize(POOL_SIZE);
    				StringBuffer sb = new StringBuffer(IP);
    				sb.append(":");
    				sb.append(PORT);
    				config.addAddress(sb.toString());
    				redisson = Redisson.create(config);
    				System.out.println("reids连接成功...");
    				LOGGER.info("reids连接成功...");
    			}
    		}
    	}

		return redisson;
	}

    
	/**
	 * 获取redis内容
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String get(String key) throws Exception {
		Redisson redisson = null;
		String result = "";
		try {
			redisson = getRedisson();
			Set mySet = redisson.getSet(key);
			result = JSON.toJSONString(mySet);
		} catch (Exception e) {
			throw  e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		}

		return result;
	}

	/**
	 * 设置redis内容
	 * @param key
	 * @param result
	 * @throws Exception
	 */
	public void put(String key, String result) throws Exception  {
		Redisson redisson = null;
		try {
			redisson = getRedisson();
			Set mySet = redisson.getSet(key);
			mySet.clear();
			mySet.add(result);
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		}
	}
	
	
    /** 
     * 获取Map对象 
     * @param mapName 
     * @return 
     */  
    public Map<String,Object> getMap(String mapName){  
		Redisson redisson = null;
		//Map<String,Object> map = new HashMap<String, Object>();
		TreeMap<String,Object> treemap = new TreeMap<String, Object>();
		try {
			redisson = getRedisson();
			RMap<String,Object> rMap = redisson.getMap(mapName);
			
			//将redis对象转换成map对象
			for(Entry<String, Object> info :rMap.entrySet()){
				//map.put(info.getKey(),info.getValue());
				treemap.put(info.getKey(),info.getValue());
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		} 
		return treemap;
    }  
    
    
    /** 
     * 删除Map对象 
     * @param mapName 
     * @return 
     */  
    public Map<String,Object> deleteMap(String mapName){  
		Redisson redisson = null;
		Map<String,Object> map = null;
		try {
			redisson = getRedisson();
			map = redisson.getMap(mapName);
			map.clear();
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		} 
		return map;
    }  
    
    
    /** 
     * 设置Map内容 
     * @param key  
     * @return 
     */  
    public Map<String,Object> putMap(String mapName,String key,String value){  
		Redisson redisson = null;
		Map<String,Object> map = null;
		try {
			redisson = getRedisson();
			map = redisson.getMap(mapName);
			map.put(key, value);
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		} 
		return map;
    }  
    
    
    /** 
     * 根据key 获取Map对象 中消息信息
     * @param mapName,key
     * @return 
     */  
    public Object getMapInfoByKey(String mapName,String key){  
		Redisson redisson = null;
		Object info = null;
		try {
			redisson = getRedisson();
			RMap<String,Object> rMap = redisson.getMap(mapName);
			if(rMap == null)
				return null;
			info = rMap.get(key);
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		} 
		return info;
    } 
    
    
    /** 
     * 根据key删除Map对象中的内容 
     * @param key  
     * @param mapName
     * @return 
     */  
    public Map<String,Object> removeMapInfo(String mapName,String key){  
		Redisson redisson = null;
		Map<String,Object> map = null;
		try {
			redisson = getRedisson();
			map = redisson.getMap(mapName);
			map.remove(key);
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != redisson) {
				//redisson.shutdown();
			}
		} 
		return map;
    }  
}
