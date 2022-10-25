package com.brins.arouter_api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import com.brins.arouter_annotation.bean.RouterBean;

/**
 * @author lipeilin
 * @date 2022/10/24
 * @desc
 */
public class RouterManager {

    private String group; // 路由组名 app, order
    private String path; // 路由路径 /order/Order_MainActivity

    // 单例模式
    private static RouterManager instance;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    // 提供性能  LRU缓存
    private LruCache<String, ARouterGroup> groupLruCache;
    private LruCache<String, ARouterPath> pathLruCache;
    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";

    private RouterManager() {
        groupLruCache = new LruCache<>(100);
        pathLruCache = new LruCache<>(100);
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        if (path.lastIndexOf("/") == 0) { // 只写了一个 /
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 证明没有问题，没有抛出异常
        this.path = path;  // 最终的效果：如 /order/Order_MainActivity
        this.group = finalGroup; // 例如：order，personal

        return new BundleManager();
    }

    public Object navigation(Context context, BundleManager bundleManager) {
        String groupClassName = context.getPackageName() + "." + FILE_GROUP_NAME + group;
        try {
            ARouterGroup loadGroup = groupLruCache.get(group);
            if (null == loadGroup) { // 缓存里面没有东东
                // 加载APT路由组Group类文件 例如：ARouter$$Group$$order
                Class<?> aClass = Class.forName(groupClassName);
                // 初始化类文件
                loadGroup = (ARouterGroup) aClass.newInstance();

                // 保存到缓存
                groupLruCache.put(group, loadGroup);
            }
            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group报废了..."); // Group这个类 加载失败
            }
            ARouterPath loadPath = pathLruCache.get(path);
            if (null == loadPath) { // 缓存里面没有东东 Path
                // 1.invoke loadGroup
                // 2.Map<String, Class<? extends ARouterLoadPath>>
                Class<? extends ARouterPath> clazz = loadGroup.getGroupMap().get(group);

                // 3.从map里面获取 ARouter$$Path$$personal.class
                loadPath = clazz.newInstance();

                // 保存到缓存
                pathLruCache.put(path, loadPath);
            }

            if (loadPath != null) {
                if (loadPath.getPathMap().isEmpty()) { // pathMap.get("key") == null
                    throw new RuntimeException("路由表Path报废了...");
                }
                RouterBean routerBean = loadPath.getPathMap().get(path);

                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:{
                            break;
                        }

                        case CALL: {
                            Class<?> clazz = routerBean.getMyclass();
                            Call call = (Call) clazz.newInstance();
                            bundleManager.setCall(call);
                            Log.e("zcw_arouter", "CALL 跳转类型" + bundleManager.getCall().getClass().getName());
                            return bundleManager.getCall();
                        }
                    }
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
