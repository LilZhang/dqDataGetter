package oops.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 代理池子
 * @author Lil ZHANG
 *
 */
public class ProxyPool {
	
	private static List<Proxy> proxys = new LinkedList<Proxy>();	//代理集合

	/**
	 * 刷新代理池
	 */
	public static void refresh() {
		//proxys.clear();
		String urlStr = "http://www.xicidaili.com/nn/";
		String encoding = "utf-8";
		String html = RequestUtil.getHTML(urlStr, encoding);
		Document doc = Jsoup.parse(html);
		
		Elements elements = doc.select("tr[class]");
		for (Element e : elements) {
			Proxy proxy;
			try {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(e.child(2).html()), Integer.parseInt(e.child(3).html())));
				proxys.add(proxy);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
		//proxys.subList(8, proxys.size()-1).clear();
		System.out.println("Proxy Pool : " + size());
	}

	/**
	 * 从代理池中随机取出代理
	 */
	public static Proxy getProxy() {
		if (proxys.size() > 0)
			return proxys.get(new Random().nextInt(proxys.size()));
		return null;
	}

	/**
	 * 从代理池中删除对应代理
	 */
	public static void delete(Proxy proxy) {
		proxys.remove(proxy);
	}

	/**
	 * 返回代理池中代理数量
	 * @return
	 */
	public static int size() {
		return proxys.size();
	}
}
