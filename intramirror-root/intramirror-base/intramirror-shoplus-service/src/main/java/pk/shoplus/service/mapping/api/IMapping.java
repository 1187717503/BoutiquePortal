package pk.shoplus.service.mapping.api;

import java.util.Map;

/**
 * 处理MQ消息与底层Service映射
 * @author dingyifan
 *
 */
public interface IMapping {
	
	/**
	 * 处理MQ数据,并且执行相应DB操作
	 * @param mqData
	 * @return 响应结果
	 */
	public Map<String, Object> handleMappingAndExecute(String mqData);
	
}
