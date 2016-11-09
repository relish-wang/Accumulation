package cn.studyjams.s1.contest.accumulation;

import android.app.Activity;
import android.app.Application;

import com.firebase.client.Firebase;
import com.squareup.leakcanary.LeakCanary;

import java.util.WeakHashMap;

/**
 * App应用管理类
 * Created by Relish on 2016/11/4.
 */
public class App extends Application {


    private static WeakHashMap<String, Activity> mActivities;

    public static App CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
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

}
