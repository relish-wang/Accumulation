package wang.relish.accumulation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.util.WeakHashMap;

import wang.relish.accumulation.entity.User;
import wang.relish.accumulation.greendao.DaoMaster;
import wang.relish.accumulation.greendao.DaoSession;
import wang.relish.accumulation.util.AppLog;
import wang.relish.accumulation.util.Temp;

/**
 * App应用管理类
 * Created by Relish on 2016/11/4.
 */
public class App extends Application {

    public static User USER;

    public static final String GOOGLE_PLAY = "com.android.vending";
    private static final String TAG = "App";
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    private static WeakHashMap<String, Activity> mActivities;

    public static App CONTEXT;

    private static DaoSession sDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

//        DBHelper.getInstance(this).getReadableDatabase();
        Temp.initDemoData();

        //日志打印工具
        Logger.init(TAG).methodCount(10) // 方法栈打印的个数，默认是 2
                .hideThreadInfo() // // 隐藏线程信息，默认显示
                .methodOffset(0); // 设置调用堆栈的函数偏移值，默认是 0
        //.logAdapter(new AndroidLogAdapter()); // 自定义一个打印适配器;

        CONTEXT = this;
        mActivities = new WeakHashMap<>();

        //获取屏幕尺寸
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "accumulation.db");
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        sDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaosession() {
        return sDaoSession;
    }


    /**
     * Activity入栈
     *
     * @param activity activity
     */
    public static synchronized void addActivity(Activity activity) {
        mActivities.put(activity.getClass().getName(), activity);
    }

    /**
     * Activity出栈
     *
     * @param activityNames activity名
     */
    public static synchronized void removeActivities(String... activityNames) {
        for (String activityClassName : activityNames) {
            Activity activity = mActivities.get(activityClassName);
            if (activity != null) {
                activity.finish();
            }
            mActivities.remove(activityClassName);
        }
    }

    /**
     * 退出Activity
     */
    public static void exitApp() {
        Object[] allActivityNames = mActivities.keySet().toArray();
        String[] names = new String[allActivityNames.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = allActivityNames[i].toString();
        }
        removeActivities(names);
    }


    /**
     * 获取版本名
     *
     * @param context 上下文
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            AppLog.e(TAG, "getVersionName", "packageInfo equals null!!!");
            return "";
        }
        return packageInfo.versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
