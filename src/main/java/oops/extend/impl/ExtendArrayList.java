package oops.extend.impl;

import java.util.ArrayList;
import java.util.Collection;

import oops.extend.ExtendList;
import oops.utils.DatabaseUtil;
import oops.utils.TableUtil;


/**
 * java.util.List扩展接口实现
 * @author Lil ZHANG
 * @param <E>
 */
public class ExtendArrayList<E> extends ArrayList<E> implements ExtendList<E> {
	
	private static final long serialVersionUID = -2164082747432131609L;
	
	private static int WRITE_IN_SIZE = 50;		//元素写入阈值

	/**
	 * 将容器内所有元素写入数据库
	 */
	public void insertAllIntoDB() {
		DatabaseUtil.writeIn(this);
		this.clear();
	}

	/**
	 * 将元素添加至容器（并使容器在适当时机写入数据库）
	 * @param e
	 */
	public void addAndInsertIntoDB(E e) {
		this.add(e);
		if (this.size() >= WRITE_IN_SIZE) {
			insertAllIntoDB();
		}
	}

	/**
	 * 将元素集添加至容器（并使容器在适当时机写入数据库）
	 * @param c
	 */
	public void addAndInsertAllIntoDB(Collection<? extends E> c) {
		this.addAll(c);
		if (this.size() >= WRITE_IN_SIZE) {
			insertAllIntoDB();
		}
		
	}
}
