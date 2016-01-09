package oops.thread;

import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import oops.extend.ExtendList;
import oops.extend.impl.ExtendArrayList;
import oops.model.Category;
import oops.model.ElementInfo;
import oops.model.Factory;
import oops.model.Hash;
import oops.model.Price;
import oops.model.SampleFile;
import oops.model.Series;
import oops.model.Supplier;
import oops.model.TCountAndTablePrice;
import oops.model.TCountAndTableSupplier;
import oops.utils.RequestUtil;
import oops.utils.UrlUtil;

/**
 * 分支线程
 * @author Lil ZHANG
 *
 */
public class DataThread implements Runnable {

    private List<Factory> factories;

    /**
     * 重写run()方法
     */
    public void run() {
        Gson gson = new Gson();
        String results = "";

        ExtendList<Series> seriesList = new ExtendArrayList<Series>();
        ExtendList<Hash> hashList = new ExtendArrayList<Hash>();
        ExtendList<Supplier> supplierList = new ExtendArrayList<Supplier>();
        ExtendList<Category> categoryList = new ExtendArrayList<Category>();
        ExtendList<Price> priceList = new ExtendArrayList<Price>();
        ExtendList<ElementInfo> elementInfoList = new ExtendArrayList<ElementInfo>();
        ExtendList<SampleFile> sampleFileList = new ExtendArrayList<SampleFile>();

        for (Factory f : factories) {
            if (f.fid.length() > 2) {

                //通过factory获取series
//                results = RequestUtil.get(UrlUtil.getSeriesUrl(f.fid, t()));
//                List<Series> seriess = gson.fromJson(results, new TypeToken<List<Series>>() {}.getType());
                List<Series> seriess = getSeriess(gson, f.fid);
                seriesList.addAndInsertAllIntoDB(seriess);

                for (Series se : seriess) {

                    if (se.cid.length() > 2) {

                        //通过series获取hash
//                        results = RequestUtil.get(UrlUtil.getHashUrl(se.cid, t()));
//                        Hash hash = gson.fromJson(results, new TypeToken<Hash>() {}.getType());
                        Hash hash = getHash(gson, se.cid);
                        if (hash != null) {
                            hash.classid = se.cid; //// Hash
                            hashList.addAndInsertIntoDB(hash);
                        }

                        //通过series获取Supplier
//                        results =  RequestUtil.get(UrlUtil.getSupplierUrl(se.cid, t()));
//                        TCountAndTableSupplier supplierData = gson.fromJson(results, new TypeToken<TCountAndTableSupplier>() {}.getType());
                        TCountAndTableSupplier supplierData = getSupplierData(gson, se.cid);
                        if (supplierData != null) {
                            List<Supplier> sps = supplierData.Table;
                            for (Supplier sp : sps) {
                                sp.param_cid = se.cid;
                            }
                            supplierList.addAndInsertAllIntoDB(sps); //// supplier
                        }

                        //通过hash获取category
                        if (hash != null) {
//                            results = RequestUtil.get(UrlUtil.getCategoryUrl(hash.hash));
                            Map<String, String> categoryMap = getCategoryMap(gson, hash.hash);

                            for (Entry<String, String> entry : categoryMap.entrySet()) {
                                Category category = new Category(entry.getKey(), entry.getValue(), se.cid); //// category
                                categoryList.addAndInsertIntoDB(category);

                                //立flag
                                boolean flag = true;
                                Long page = 1L;
                                List<Price> prices = new ArrayList<Price>();
                                while(flag) {

                                    //通过factory和category获取price
                                    page++;
//                                    results = RequestUtil.post(UrlUtil.getPriceUrl(t()), UrlUtil.getPriceContent(f.fid, category.categoryid, se.cid, "", "", page.toString(), "50"));

//                                    TCountAndTablePrice priceData = gson.fromJson(results, new TypeToken<TCountAndTablePrice>() {}.getType());
                                    TCountAndTablePrice priceData = getPriceData(gson, f.fid, category.categoryid,se.cid, page);


                                    //若结果为空则跳出循环
                                    if (priceData.Table == null || priceData.Table.size() == 0) {
                                        flag = false;
                                    } else {
                                        prices.addAll(priceData.Table); //// price
                                        priceList.addAndInsertAllIntoDB(priceData.Table);
                                    }
                                }
                                for (Price pr : prices) {

                                    //通过series和price获取elementInfo
//                                    results = RequestUtil.post(UrlUtil.getElementInfoUrl(t()), UrlUtil.getElementInfoContent(se.cid, pr.F_InnerModel));
//                                    ElementInfo elementInfo = gson.fromJson(results, new TypeToken<ElementInfo>() {}.getType()); //// ei
                                    ElementInfo elementInfo = getElementInfo(gson, se.cid, pr.F_InnerModel); //// ei
                                    elementInfoList.addAndInsertIntoDB(elementInfo);

                                    //通过series, factory和 price获取sampleFile
                                    if(se.pcid != null && se.pcid != "null") {
//                                        results = RequestUtil.get(UrlUtil.getSampleFileUrl(se.cid, f.fid, pr.F_InnerModel, se.pcid, t()));
//                                        List<SampleFile> sampleFiles = gson.fromJson(results, new TypeToken<List<SampleFile>>() {}.getType()); ////sampleFile
                                        List<SampleFile> sampleFiles = getSampleFiles(gson, se.cid, f.fid, pr.F_InnerModel, se.pcid);
                                        if (sampleFiles != null && sampleFiles.size() > 0) {
                                            for (SampleFile sf : sampleFiles) {
                                                sf.param_cid = se.cid;
                                                sf.param_fid = f.fid;
                                                sf.param_innermode = pr.F_InnerModel;
                                                sf.param_pcid = se.pcid;
                                            }
                                            sampleFileList.addAndInsertAllIntoDB(sampleFiles);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        seriesList.insertAllIntoDB();
        hashList.insertAllIntoDB();
        supplierList.insertAllIntoDB();
        categoryList.insertAllIntoDB();
        priceList.insertAllIntoDB();
        elementInfoList.insertAllIntoDB();
        sampleFileList.insertAllIntoDB();

    }

    /**
     * 构造器
     * @param factories
     */
    public DataThread(List<Factory> factories) {
        super();
        this.factories = factories;
    }

    /**
     * get time
     * @return
     */
    private String t() {
        return new Long(new Date().getTime()).toString();
    }

    /**
     * 获取CategoryMap
     * @param gson
     * @param hash
     * @return
     */
    private Map<String, String> getCategoryMap(Gson gson, String hash) {
        Map<String, String> categoryMap = null;
        String result = RequestUtil.get(UrlUtil.getCategoryUrl(hash));

        try {
            categoryMap = gson.fromJson(result, new TypeToken<Map<String, String>>() {}.getType());
        } catch(JsonSyntaxException e) {
            return getCategoryMap(gson, hash);
        }
        return categoryMap;
    }

    /**
     * 获取Series列表
     * @param gson
     * @param fid
     * @return
     */
    private List<Series> getSeriess(Gson gson, String fid) {
        List<Series> seriess = null;
        String results = RequestUtil.get(UrlUtil.getSeriesUrl(fid, t()));

        try {
            seriess = gson.fromJson(results, new TypeToken<List<Series>>() {}.getType());
        } catch(JsonSyntaxException e) {
            return getSeriess(gson, fid);
        }
        return seriess;
    }

    /**
     * 获取hash码
     * @param gson
     * @param cid
     * @return
     */
    private Hash getHash(Gson gson, String cid) {
        Hash hash = null;
        String results = RequestUtil.get(UrlUtil.getHashUrl(cid, t()));

        try {
            hash = gson.fromJson(results, new TypeToken<Hash>() {}.getType());
        } catch(JsonSyntaxException e) {
            return getHash(gson, cid);
        }
        return hash;
    }

    private TCountAndTablePrice getPriceData(Gson gson, String fid, String categoryid, String cid, Long page) {
        TCountAndTablePrice priceData = null;
        String results = RequestUtil.post(UrlUtil.getPriceUrl(t()), UrlUtil.getPriceContent(fid, categoryid, cid, "", "", page.toString(), "50"));
        try {
            priceData = gson.fromJson(results, new TypeToken<TCountAndTablePrice>() {}.getType());
        }catch(JsonSyntaxException e) {
            return getPriceData(gson, fid, categoryid, cid, page);
        }
        return priceData;
    }

    private TCountAndTableSupplier getSupplierData(Gson gson, String cid) {
        TCountAndTableSupplier supplierData = null;
        String results =  RequestUtil.get(UrlUtil.getSupplierUrl(cid, t()));
        try {
            supplierData = gson.fromJson(results, new TypeToken<TCountAndTableSupplier>() {}.getType());
        } catch(JsonSyntaxException e) {
            return getSupplierData(gson, cid);
        }
        return supplierData;
    }

    private ElementInfo getElementInfo(Gson gson, String cid, String F_InnerModel) {
        ElementInfo elementInfo = null;
        String results = RequestUtil.post(UrlUtil.getElementInfoUrl(t()), UrlUtil.getElementInfoContent(cid, F_InnerModel));
        try {
            elementInfo = gson.fromJson(results, new TypeToken<ElementInfo>() {}.getType()); //// ei
        }catch(JsonSyntaxException e) {
            return getElementInfo(gson, cid, F_InnerModel);
        }
        return elementInfo;
    }

    private List<SampleFile> getSampleFiles(Gson gson, String cid, String fid, String F_InnerModel, String pcid) {
        List<SampleFile> sampleFiles = null;
        String results = RequestUtil.get(UrlUtil.getSampleFileUrl(cid, fid, F_InnerModel, pcid, t()));
        try {
            sampleFiles = gson.fromJson(results, new TypeToken<List<SampleFile>>() {}.getType()); ////sampleFile
        }catch(JsonSyntaxException e) {
            return getSampleFiles(gson, cid, fid, F_InnerModel, pcid);
        }
        return sampleFiles;
    }
}
