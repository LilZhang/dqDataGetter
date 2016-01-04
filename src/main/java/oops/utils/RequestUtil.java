package oops.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;*/

/**
 * 请求操作工具类
 * @author Lil ZHANG
 *
 */
public class RequestUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    private static long RETRY_MILLIS = 3000;		//超时重试时间(毫秒)

    /**
     * 发送post请求并获取返回结果
     * @param urlStr	请求地址
     * @param content	请求表单
     * @param encoding	编码格式
     * @return
     */
    public static String post(String urlStr, String content, String encoding) {
        System.out.println(sdf.format(new Date()) + " => POST : " + urlStr + " || Data : " + content);
        HttpURLConnection connection = null;
        try {
            connection = initRequest(urlStr, true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());// 打开输出流往对端服务器写数据
            out.writeBytes(content);// 提交表单
            out.flush();// 刷新
            out.close();// 关闭输出流

            return getResponseStr(connection, encoding);

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("[ POST : TRY AGAIN ] : " + urlStr + " || Data : " + content);
            try {
                Thread.sleep(RETRY_MILLIS);
                return post(urlStr, content, encoding);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } finally {
            finalizeRequest(connection);
        }
        return null;
    }

    /**
     * 发送post请求并获取返回结果(UTF-8编码)
     * @param urlStr	请求地址
     * @param content	请求表单
     * @return
     */
    public static String post(String urlStr, String content) {

        return post(urlStr, content, "utf-8");
    }

    /**
     * 发送get请求并获取返回结果
     * @param urlStr	请求地址
     * @param encoding	编码格式
     * @return
     */
    public static String get(String urlStr, String encoding) {
        System.out.println(sdf.format(new Date()) + " => GET : " + urlStr);
        HttpURLConnection connection = null;
        try {
            connection = initRequest(urlStr, false);

            return getResponseStr(connection, encoding);

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("[ GET : TRY AGAIN ] : " + urlStr);
            try {
                Thread.sleep(RETRY_MILLIS);
                return get(urlStr, encoding);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } finally {
            finalizeRequest(connection);
        }
        return null;
    }

    /**
     * 发送get请求并获取返回结果(UTF-8编码)
     * @param urlStr	请求地址
     * @return
     */
    public static String get(String urlStr) {
        return get(urlStr, "utf-8");
    }

    /**
     * 获取请求所返回的结果
     * @param connection	连接
     * @param encoding		编码格式
     * @return
     * @throws IOException
     */
    private static String getResponseStr(HttpURLConnection connection, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));// 往对端写完数据对端服务器返回数据
        // ,以BufferedReader流来读取
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * 初始化请求连接
     * @param urlStr	请求地址
     * @param isPost	是否为post方式
     * @return
     * @throws IOException
     */
    private static HttpURLConnection initRequest(String urlStr, boolean isPost) throws IOException {
        return initRequest(urlStr, null, isPost);
    }

    /**
     * 初始化请求连接
     * @param urlStr	请求地址
     * @param proxy		代理
     * @param isPost	是否为post方式
     * @return
     * @throws IOException
     */
    private static HttpURLConnection initRequest(String urlStr, Proxy proxy,boolean isPost) throws IOException {
        URL url = null;
        HttpURLConnection connection = null;

        url = new URL(urlStr);
        if (proxy != null)
            connection = (HttpURLConnection) url.openConnection(proxy);// 新建连接实例
        else
            connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(1500000);// 设置连接超时时间，单位毫秒
        connection.setReadTimeout(1500000);// 设置读取数据超时时间，单位毫秒
        connection.setDoOutput(true);// 是否打开输出流 true|false
        connection.setDoInput(true);// 是否打开输入流true|false
        if (isPost)
            connection.setRequestMethod("POST");// 提交方法POST|GET
        else
            connection.setRequestMethod("GET");// 提交方法POST|GET
        connection.setUseCaches(false);// 是否缓存true|false
        connection.connect();// 打开连接端口
        return connection;
    }

    /**
     * 关闭连接
     * @param connection
     */
    private static void finalizeRequest(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();// 关闭连接
        }
    }

    /**
     * 获取页面地址所在html源码
     * @param pageURL	页面地址
     * @param encoding	编码格式
     * @return
     */
    public static String getHTML(String pageURL, String encoding) {
        StringBuilder pageHTML = new StringBuilder();
        try {
            URL url = new URL(pageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "MSIE 7.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
            String line = null;
            while ((line = br.readLine()) != null) {
                pageHTML.append(line);
                //pageHTML.append("\r\n");
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageHTML.toString();
    }
}
