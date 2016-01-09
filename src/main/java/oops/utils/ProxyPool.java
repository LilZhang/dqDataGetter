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
	private static List<Proxy> cache = new LinkedList<Proxy>();	//这里面的代理都是用过而且好用的(并不代表将来也好用)

	/**
	 * 刷新代理池
	 */
	public static void refresh() {

		proxys.clear();
		proxys.addAll(cache);
		cache.clear();

//		String urlStr = "http://www.xicidaili.com/nn/";
//		String encoding = "utf-8";
//		String html = RequestUtil.getHTML(urlStr, encoding);
//		Document doc = Jsoup.parse(html);
		
//		Elements elements = doc.select("tr[class]");
//		for (Element e : elements) {
//			Proxy proxy;
//			try {
//				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(e.child(2).html()), Integer.parseInt(e.child(3).html())));
//				proxys.add(proxy);
//			} catch (NumberFormatException e1) {
//				e1.printStackTrace();
//			} catch (UnknownHostException e1) {
//				e1.printStackTrace();
//			}
//		}
		Proxy proxy;
		String proxyStr = RequestUtil.getWithoutProxy("http://ip.zdaye.com/?api=201601091306198335&dengji=%C4%E4%C3%FB&checktime=1%B7%D6%D6%D3%C4%DA&gb=2&ct=150&daochu=1");

		if (!proxyStr.startsWith("<bad>")) {
			String[] proxyLists = proxyStr.split(",");
			for (String p : proxyLists) {
				String[] ipWithPort = p.split(":");
				if (ipWithPort.length == 2) {
					String ip = ipWithPort[0];
					String port = ipWithPort[1];
					InetAddress inetAddress = null;
					try {
						inetAddress = InetAddress.getByName(ip);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(inetAddress, Integer.parseInt(port)));
					proxys.add(proxy);
				}
			}
			//proxys.subList(0, proxys.size()-1).clear();
			System.out.println("Proxy Pool : " + size());
		} else {
			System.out.println("[ PROXYPOOL : TRY AGAIN ]");
			refresh();
		}

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

	public static void cache(Proxy proxy) {
		cache.add(proxy);
	}
}
