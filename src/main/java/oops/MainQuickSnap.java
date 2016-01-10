package oops;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import oops.model.*;
import oops.thread.DataThread;
import oops.thread.DataThreadQuickSnap;
import oops.thread.ProxyPoolThread;
import oops.utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 0ku on 2016/1/10.
 */
public class MainQuickSnap {

    private static int THREAD_COUNT = 15;

    public static void main(String[] args) {
        init();

        ProxyPoolThread ppt = new ProxyPoolThread();
        Thread daemonThread = new Thread(ppt);
        daemonThread.setDaemon(true);
        daemonThread.start();

        Gson gson = new Gson();
        String results = "";

        results = RequestUtil.get(UrlUtil.getFactoryUrl(new Long(new Date().getTime()).toString()));
        List<Factory> factories = gson.fromJson(results, new TypeToken<List<Factory>>() {}.getType());
        saveFactories(factories);

        //long res = findTotalCount(gson);    //163456
        long res = 163456;
        long perNum = 163456 / (THREAD_COUNT - 1);

        for (long ii = 1; ii <= res; ii += perNum) {
            long toPageIndex = ii + perNum > res ? res : ii + perNum;
            DataThreadQuickSnap dtqs = new DataThreadQuickSnap(ii, toPageIndex);
            new Thread(dtqs).start();
            try {
                Thread.sleep(7000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
//        DataThreadQuickSnap dtqs = new DataThreadQuickSnap(1L, 163456L);
//        new Thread(dtqs).start();

    }

    /**
     * 初始化
     * 创建对应表
     */
    private static void init() {
        try {
            DatabaseUtil.init();

            DatabaseUtil.execute(TableUtil.getDropIfExists(Factory.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(ElementInfo.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Price.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Supplier.class));

            DatabaseUtil.execute(TableUtil.getCreateTable(Factory.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(ElementInfo.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Price.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Supplier.class));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.terminate();
        }
    }

    /**
     * 保存factory
     * @param factories
     */
    private static void saveFactories(List<Factory> factories) {
        try {
            DatabaseUtil.init();
            for (Factory fa : factories) {
                DatabaseUtil.execute(TableUtil.getInsert(fa));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.terminate();
        }
    }

    private static long findTotalCount(Gson gson) {
        boolean flag = true;
        long n = 0;
        long i = 0;
        long j = 0;
        long res = 0;
        while (flag) {
            i = (long) Math.pow(2, n);
            TCountAndTablePrice tCountAndTablePrice = DataObjectUtil.getPriceData(gson, "", "", "",i);

            if (tCountAndTablePrice != null) {
                if (tCountAndTablePrice.Table != null && tCountAndTablePrice.Table.size() > 0 && tCountAndTablePrice.Table.size() <= 50) {
                    j = i;
                    n++;
                }
                else if (tCountAndTablePrice.Table == null || tCountAndTablePrice.Table.size() == 0) {
                    flag = false;
                }
            }
            else {
                flag = false;
            }
        }

        boolean flag2 = true;
        while(flag2) {

            long k = (i - j) / 2 + j;
            TCountAndTablePrice tCountAndTablePrice = DataObjectUtil.getPriceData(gson, "", "", "",(long) k);
            if (tCountAndTablePrice.Table != null && tCountAndTablePrice.Table.size() == 50) {
                j = k;
            }
            else if (tCountAndTablePrice.Table != null && tCountAndTablePrice.Table.size() > 0 && tCountAndTablePrice.Table.size() < 50) {
                res = k + 1;
                flag2 = false;
            }
            else if (tCountAndTablePrice.Table == null || tCountAndTablePrice.Table.size() == 0) {
                i = k;
            } else if (i == j) {
                res = i;
                flag2 = false;
            }
        }
        return res;
    }

}
