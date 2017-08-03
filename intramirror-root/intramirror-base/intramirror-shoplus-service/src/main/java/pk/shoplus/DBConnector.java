
package pk.shoplus;

import java.io.InputStream;
import java.util.Properties;

import org.sql2o.Sql2o;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import pk.shoplus.parameter.DBConfiguration;

/**
 * @author author :
 * @date create_at : 2016年11月2日 下午5:02:45
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class DBConnector {

	/**
	 * 
	 */
	public static Sql2o sql2o;

	/**
	 * 
	 */
	public static MemCachedClient memcached;

	static {
		Properties props = new Properties();
		// sql2o
		InputStream in = null;
		try {

			in = DBConnector.class.getResourceAsStream(DBConfiguration.DB_CONFIG_FILE);
			props = new Properties();
			props.load(in);
			in.close();
			HikariConfig config = new HikariConfig(props);
			sql2o = new Sql2o(new HikariDataSource(config));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// memcached
		in = DBConnector.class.getResourceAsStream(DBConfiguration.MEMCACHED_CONFIG_FILE);
		props = new Properties();
		try {
			props.load(in);
			in.close();
			memcached = new MemCachedClient();
			String[] servers = { props.getProperty("host") + ':' + props.getProperty("port") };
			Integer[] weights = { Integer.parseInt(props.getProperty("weights")) };
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(servers);
			pool.setWeights(weights);
			pool.setInitConn(Integer.parseInt(props.getProperty("initConn")));
			pool.setMinConn(Integer.parseInt(props.getProperty("minConn")));
			pool.setMaxConn(Integer.parseInt(props.getProperty("maxConn")));
			pool.setMaxIdle(Integer.parseInt(props.getProperty("maxIdle")));
			pool.setMaintSleep(Integer.parseInt(props.getProperty("maintSleep")));
			pool.setNagle(Boolean.parseBoolean(props.getProperty("nagle")));
			pool.setSocketTO(Integer.parseInt(props.getProperty("socketTO")));
			pool.setSocketConnectTO(Integer.parseInt(props.getProperty("socketConnectTO")));
			pool.initialize();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
