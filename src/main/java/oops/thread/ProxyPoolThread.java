package oops.thread;

import oops.utils.ProxyPool;

/**
 * 代理池维护线程
 * @author Lil ZHANG
 *
 */
public class ProxyPoolThread implements Runnable {

	/**
	 * 重写run()方法
	 */
	public void run() {
		
		while(true) {
			ProxyPool.refresh();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(ProxyPool.size());
		}
		
		
	}

}
