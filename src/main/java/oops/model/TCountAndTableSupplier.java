package oops.model;

import java.util.List;

/**
 * Gson实体：TCountAndTableSupplier
 * @author Lil ZHANG
 *
 */
public class TCountAndTableSupplier {
	public List<Tmp> T_Count;
	public List<Supplier> Table;
	
	
	public static class Tmp {
		public Long Count;
		public Long F_QueryCount;
		public Long F_RefCount;
	}
}
