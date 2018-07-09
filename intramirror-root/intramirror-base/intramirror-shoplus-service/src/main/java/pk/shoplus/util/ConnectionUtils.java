package pk.shoplus.util;

import org.sql2o.Connection;

/**
 * Created by dingyifan on 2017/6/9.
 */
public class ConnectionUtils {

    public static void closeConn(Connection conn){
        if(conn != null) {
            conn.close();
        }
    }

    public static void closeAndRollback(Connection conn){
        if(conn != null) {
            conn.rollback();
            conn.close();
        }
    }

}
