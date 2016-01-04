package oops.utils;

import java.lang.reflect.Field;

/**
 * SQL语句生成工具类
 * @author Lil ZHANG
 *
 */
public class TableUtil {

    /**
     * 生成 DROP IF EXISTS 语句
     * DROP TABLE IF EXISTS `example_table`
     * @param c	CLASS对象
     * @return
     */
    public static <T> String getDropIfExists(Class<T> c) {
        return "DROP TABLE IF EXISTS `" + c.getSimpleName() + "`";
    }

    /**
     * 生成 CREATE TABLE 语句
     * CREATE TABLE `example_table`(prop_a varchar(300), prob_b bigint(11))
     * @param c	CLASS对象
     * @return
     */
    public static <T> String getCreateTable(Class<T> c) {
        StringBuilder sb = new StringBuilder();
        String tableName = c.getSimpleName();
        sb.append("CREATE TABLE `"+tableName+"` ");
        sb.append("(");

        Field[] fields = c.getDeclaredFields();
        for (int i = 0; i <fields.length; i++) {
            if (fields[i].getName().equals("F_MoreInfo"))
                sb.append("`"+fields[i].getName()+"` "+"varchar(4500)");
            else if(fields[i].getType().equals(String.class))
                sb.append("`"+fields[i].getName()+"` "+"varchar(300)");
            else if (fields[i].getType().equals(Long.class))
                sb.append("`"+fields[i].getName()+"` "+"bigint(11)");
            else if (fields[i].getType().equals(Double.class))
                sb.append("`"+fields[i].getName()+"` "+"double");
            else if (fields[i].getType().equals(Boolean.class))
                sb.append("`"+fields[i].getName()+"` "+"varchar(10)");
            else if (fields[i].getType().equals(Integer.class))
                sb.append("`"+fields[i].getName()+"` "+"int");
            else
                sb.append("`"+fields[i].getName()+"` "+"varchar(10)");

            if(i != fields.length - 1)
                sb.append(", ");
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * 生成  INSERT INTO 语句
     * INSERT INTO `example_table`(prop_a varchar(300), prob_b bigint(11))
     * VALUES('hekko world', '11')
     * @param t
     * @return
     */
    public static <T> String getInsert(T t) {
        StringBuilder sb = new StringBuilder();
        Class<? extends Object> clz = t.getClass();
        Field[] fields = clz.getDeclaredFields();
        sb.append("INSERT INTO `");
        sb.append(clz.getSimpleName());
        sb.append("`(");
        for (int i = 0; i < fields.length; i++) {
            sb.append(fields[i].getName());
            if (i != fields.length - 1)
                sb.append(", ");
        }
        sb.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            try {
                sb.append("'" + fields[i].get(t) + "'");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (i != fields.length - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }

}
