package oops.thread;

import com.google.gson.Gson;
import oops.extend.ExtendList;
import oops.extend.impl.ExtendArrayList;
import oops.model.*;
import oops.utils.DataObjectUtil;

import java.util.List;

/**
 * Created by 0ku on 2016/1/10.
 */
public class DataThreadQuickSnap implements Runnable {

    private Long fromPageIndex;
    private Long toPageIndex;


    public void run() {
        Gson gson = new Gson();

        ExtendList<Supplier> supplierList = new ExtendArrayList<Supplier>();
        ExtendList<Price> priceList = new ExtendArrayList<Price>();
        ExtendList<ElementInfo> elementInfoList = new ExtendArrayList<ElementInfo>();

        for (Long i = fromPageIndex; i < toPageIndex; i++) {
            TCountAndTablePrice tCountAndTablePrice = DataObjectUtil.getPriceData(gson, "", "", "", i);
            if (tCountAndTablePrice != null) {
                List<Price> prices = tCountAndTablePrice.Table;

                if (prices != null && prices.size() > 0) {
                    priceList.addAndInsertAllIntoDB(prices);

                    for (Price price : prices) {
                        ElementInfo elementInfo = DataObjectUtil.getElementInfo(gson, price.F_Class_ID, price.F_InnerModel);
                        elementInfoList.addAndInsertIntoDB(elementInfo);

                        TCountAndTableSupplier tCountAndTableSupplier = DataObjectUtil.getSupplierData(gson, price.F_Class_ID);
                        List<Supplier> suppliers = tCountAndTableSupplier.Table;

                        if (suppliers != null && suppliers.size() > 0) {
                            for (Supplier supplier : suppliers) {
                                supplier.param_cid = price.F_Class_ID;
                            }
                            supplierList.addAndInsertAllIntoDB(suppliers);
                        }
                    }
                }
            }
        }
        supplierList.insertAllIntoDB();
        priceList.insertAllIntoDB();
        elementInfoList.insertAllIntoDB();
    }

    public DataThreadQuickSnap(Long fromPageIndex, Long toPageIndex) {
        this.fromPageIndex = fromPageIndex;
        this.toPageIndex = toPageIndex;
    }





    public static void main(String[] args) {
        DataThreadQuickSnap dataThread2 = new DataThreadQuickSnap(3L, 5L);
        dataThread2.run();



    }
}
