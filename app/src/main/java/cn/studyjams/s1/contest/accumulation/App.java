package cn.studyjams.s1.contest.accumulation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.firebase.client.Firebase;
import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.util.WeakHashMap;

import cn.studyjams.s1.contest.accumulation.util.AppLog;

/**
 * App应用管理类
 * Created by Relish on 2016/11/4.
 */
public class App extends LitePalApplication {

    public static final String GOOGLE_PLAY = "com.android.vending";
    private static final String TAG = "App";

    private static WeakHashMap<String, Activity> mActivities;

    public static App CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        LitePal.initialize(this);
        Firebase.setAndroidContext(this);

        CONTEXT = this;
        mActivities = new WeakHashMap<>();
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
        String[] allActivityNames = (String[]) mActivities.keySet().toArray();
        removeActivities(allActivityNames);
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
