package oops.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * 数据库操作工具类
 * @author Lil ZHANG
 *
 */
public class DatabaseUtil {
    private static Connection conn;		//JDBC Connection
    private static Statement stmt;		//JDBC Statement
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    /**
     * JDBC 初始化
     * @throws Exception
     */
    public static void init() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/testG", "root", "root");
        stmt = conn.createStatement();
    }

    /**
     * 执行 SQL语句
     * @param sql 要执行的SQL语句
     * @return 是否执行成功
     * @throws Exception
     */
    public static boolean execute(String sql) throws Exception {
        System.out.println(sdf.format(new Date()) + " => " + sql);
        return stmt.execute(sql);
    }

    /**
     * 关闭JDBC连接
     */
    public static void terminate(){
        try {
            if(stmt != null)
                stmt.close();
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将容器内的元素批量写入数据库
     * @param c
     */
    public synchronized static <T> void writeIn(Collection<T> c) {
        try {
            init();
            for (T t : c) {
                execute(TableUtil.getInsert(t));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.terminate();
        }
    }
}
