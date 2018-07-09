package pk.shoplus.data;

import org.sql2o.Connection;
import org.sql2o.data.Row;
import pk.shoplus.DBConnector;

import java.util.List;
import java.util.Map;

/**
 * Created by chone on 2017/4/12.
 */
public class DataBase {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            connection = DBConnector.sql2o.open();
        }
        return connection;
    }


    public static List<Row> query(String sql) {
        Connection con = getConnection();
        return con.createQuery(sql).executeAndFetchTable().rows();
    }

    public static void update(String sql) {
        Connection con = getConnection();
        con.createQuery(sql).executeUpdate();
    }

}
