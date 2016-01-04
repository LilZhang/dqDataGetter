package oops.model;

import java.util.List;

/**
 * Gson实体：TCountAndTablePrice
 * @author Lil ZHANG
 *
 */
public class TCountAndTablePrice {
	public List<Tmp> T_Count;
	public List<Price> Table;
	
	
	public static class Tmp {
		public Long Count;
		public Long F_QueryCount;
		public Long F_RefCount;
	}
}
