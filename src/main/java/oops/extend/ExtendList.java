package oops.extend;

import java.util.Collection;
import java.util.List;

/**
 * java.util.List扩展接口，添加对JDBC的直接支持
 * @author Lil ZHANG
 * @param <E>
 */
public interface ExtendList<E> extends List<E> {
	
	/**
	 * 将容器内所有元素写入数据库
	 */
	void insertAllIntoDB();
	
	/**
	 * 将元素添加至容器（并使容器在适当时机写入数据库）
	 * @param e
	 */
	void addAndInsertIntoDB(E e);
	
	/**
	 * 将元素集添加至容器（并使容器在适当时机写入数据库）
	 * @param c
	 */
	void addAndInsertAllIntoDB(Collection<? extends E> c);
}
