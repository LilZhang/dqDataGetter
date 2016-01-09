package oops;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import oops.model.Category;
import oops.model.ElementInfo;
import oops.model.Factory;
import oops.model.Hash;
import oops.model.Price;
import oops.model.SampleFile;
import oops.model.Series;
import oops.model.Supplier;
import oops.thread.DataThread;
import oops.thread.ProxyPoolThread;
import oops.utils.DatabaseUtil;
import oops.utils.RequestUtil;
import oops.utils.TableUtil;
import oops.utils.UrlUtil;

/**
 * 主线程类
 * @author Lil ZHANG
 *
 */
public class Main {

    /**
     * 主线程
     * @param args
     */
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

        List<Factory> subFactories = new ArrayList<Factory>();
        DataThread dt = null;
        int param = 20;

        //根据对应结果生成相应数量的分支线程
        for (int i = 0; i < factories.size(); i+=param) {
            if (i+param < factories.size())
                subFactories = factories.subList(i, i+param);
            else
                subFactories = factories.subList(i, factories.size());

            dt = new DataThread(subFactories);
            new Thread(dt).start();
        }
    }

    /**
     * 初始化
     * 创建对应表
     */
    private static void init() {
        try {
            DatabaseUtil.init();

            DatabaseUtil.execute(TableUtil.getDropIfExists(Factory.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Category.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(ElementInfo.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Hash.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Price.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(SampleFile.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Series.class));
            DatabaseUtil.execute(TableUtil.getDropIfExists(Supplier.class));

            DatabaseUtil.execute(TableUtil.getCreateTable(Factory.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Category.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(ElementInfo.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Hash.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Price.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(SampleFile.class));
            DatabaseUtil.execute(TableUtil.getCreateTable(Series.class));
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

}
