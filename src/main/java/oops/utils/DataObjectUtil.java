package oops.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import oops.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 0ku on 2016/1/10.
 */
public class DataObjectUtil {

    private static String t() {
        return new Long(new Date().getTime()).toString();
    }

    /**
     * 获取CategoryMap
     * @param gson
     * @param hash
     * @return
     */
    public static Map<String, String> getCategoryMap(Gson gson, String hash) {
        Map<String, String> categoryMap = null;
        String result = RequestUtil.get(UrlUtil.getCategoryUrl(hash));

        try {
            categoryMap = gson.fromJson(result, new TypeToken<Map<String, String>>() {}.getType());
        } catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
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
    public static List<Series> getSeriess(Gson gson, String fid) {
        List<Series> seriess = null;
        String results = RequestUtil.get(UrlUtil.getSeriesUrl(fid, t()));

        try {
            seriess = gson.fromJson(results, new TypeToken<List<Series>>() {}.getType());
        } catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
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
    public static Hash getHash(Gson gson, String cid) {
        Hash hash = null;
        String results = RequestUtil.get(UrlUtil.getHashUrl(cid, t()));

        try {
            hash = gson.fromJson(results, new TypeToken<Hash>() {}.getType());
        } catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
            return getHash(gson, cid);
        }
        return hash;
    }

    public static TCountAndTablePrice getPriceData(Gson gson, String fid, String categoryid, String cid, Long page) {
        TCountAndTablePrice priceData = null;
        String results = RequestUtil.post(UrlUtil.getPriceUrl(t()), UrlUtil.getPriceContent(fid, categoryid, cid, "", "", page.toString(), "50"));
        try {
            priceData = gson.fromJson(results, new TypeToken<TCountAndTablePrice>() {}.getType());
        }catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
            return getPriceData(gson, fid, categoryid, cid, page);
        }
        return priceData;
    }

    public static TCountAndTableSupplier getSupplierData(Gson gson, String cid) {
        TCountAndTableSupplier supplierData = null;
        String results =  RequestUtil.get(UrlUtil.getSupplierUrl(cid, t()));
        try {
            supplierData = gson.fromJson(results, new TypeToken<TCountAndTableSupplier>() {}.getType());
        } catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
            return getSupplierData(gson, cid);
        }
        return supplierData;
    }

    public static ElementInfo getElementInfo(Gson gson, String cid, String F_InnerModel) {
        ElementInfo elementInfo = null;
        String results = RequestUtil.post(UrlUtil.getElementInfoUrl(t()), UrlUtil.getElementInfoContent(cid, F_InnerModel));
        try {
            elementInfo = gson.fromJson(results, new TypeToken<ElementInfo>() {}.getType()); //// ei
        }catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
            return getElementInfo(gson, cid, F_InnerModel);
        }
        return elementInfo;
    }

    public static List<SampleFile> getSampleFiles(Gson gson, String cid, String fid, String F_InnerModel, String pcid) {
        List<SampleFile> sampleFiles = null;
        String results = RequestUtil.get(UrlUtil.getSampleFileUrl(cid, fid, F_InnerModel, pcid, t()));
        try {
            sampleFiles = gson.fromJson(results, new TypeToken<List<SampleFile>>() {}.getType()); ////sampleFile
        }catch(JsonSyntaxException e) {
            System.out.println("[ BAD PROXY : TRY AGAIN ]");
            return getSampleFiles(gson, cid, fid, F_InnerModel, pcid);
        }
        return sampleFiles;
    }
}
