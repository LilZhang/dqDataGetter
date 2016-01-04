package oops.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 地址工具类
 * @author Lil ZHANG
 *
 */
public class UrlUtil {

    /**
     * factory地址
     * @param t		时间
     * @return
     */
    public static String getFactoryUrl(String t) {
        return "http://www.dq123.com/price/cache/factory.js?t=" + t;
    }

    /**
     * price地址
     * @param t		时间
     * @return
     */
    public static String getPriceUrl(String t) {
        return "http://www.dq123.com/price/getpricelistjson.php?t=" + t;
    }

    /**
     * price表单
     * @param factoryid
     * @param categoryid
     * @param classid
     * @param keywordstr
     * @param modelstr
     * @param pageindex
     * @param initpagesize
     * @return
     */
    public static String getPriceContent(String factoryid, String categoryid, String classid, String keywordstr, String modelstr, String pageindex, String initpagesize) {
        return "factoryid="+factoryid+"&categoryid="+categoryid
                +"&classid="+classid+"&keywordstr="+keywordstr
                +"&modelstr="+modelstr+"&pageindex="+pageindex
                +"&initpagesize="+initpagesize;
    }

    /**
     * series地址
     * @param fid	factoryId
     * @param t		时间
     * @return
     */
    public static String getSeriesUrl(String fid, String t) {
        return "http://www.dq123.com/price/cache/"+fid+".js?t="+t;
    }

    /**
     * hash地址
     * @param cid	classId
     * @param t		时间
     * @return
     */
    public static String getHashUrl(String cid, String t) {
        return "http://www.dq123.com/price/getseriesfile.php?t="+t+"&classid="+cid;
    }

    /**
     * category地址
     * @param hash		hash码
     * @return
     */
    public static String getCategoryUrl(String hash) {
        return "http://www.dq123.com/price/cache/classjson.js?t="+hash;
    }

    /**
     * elementInfo地址
     * @param t		时间
     * @return
     */
    public static String getElementInfoUrl(String t) {
        return "http://www.dq123.com/price/getelementinfojson.php?t="+t+"&islog=1";
    }

    /**
     * elementInfo表单
     * @param classid
     * @param innermodel
     * @return
     */
    public static String getElementInfoContent(String classid, String innermodel) {
        try {
            innermodel = URLEncoder.encode(innermodel, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "classid="+classid+"&innermodel="+innermodel;
    }

    /**
     * supplier地址
     * @param cid
     * @param t
     * @return
     */
    public static String getSupplierUrl(String cid, String t) {
        return "http://www.dq123.com/price/getsupplierlist.php?t="+t+"&classid="+cid;
    }

    /**
     * sampleFile地址
     * @param cid
     * @param fid
     * @param im
     * @param pcid
     * @param t
     * @return
     */
    public static String getSampleFileUrl(String cid, String fid, String im, String pcid, String t) {
        return "http://www.dq123.com/price/getsamplefile.php?t="
                +t+"&factoryid="+fid+"&preclassid="+pcid+"&classid="+cid+"&innermode="+im;
    }

}
