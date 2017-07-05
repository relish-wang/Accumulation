package com.qyt.accumulation.util

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.TextUtils

import java.util.ArrayList

import com.qyt.accumulation.App


/***
 * 跳转应用市场评分
 * @author Lucasey
 */
object ScoreUtils {
    /**
     * 获取已安装应用商店的包名列表

     * @param context
     * *
     * @return
     */
    fun InstalledAPPs(context: Context?): ArrayList<String> {
        val pkgs = ArrayList<String>()
        if (context == null)
            return pkgs
        val intent = Intent()
        intent.action = "android.intent.action.MAIN"
        intent.addCategory("android.intent.category.APP_MARKET")
        val pm = context.packageManager
        val infos = pm.queryIntentActivities(intent, 0)
        if (infos == null || infos.size == 0)
            return pkgs
        val size = infos.size
        for (i in 0..size - 1) {
            var pkgName = ""
            try {
                val activityInfo = infos[i].activityInfo
                pkgName = activityInfo.packageName
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!TextUtils.isEmpty(pkgName))
                pkgs.add(pkgName)

        }
        return pkgs
    }

    /**
     * 过滤出已经安装的包名集合
     * @param context
     * *
     * @param pkgs
     * *            待过滤包名集合
     * *
     * @return 已安装的包名集合
     */
    fun SelectedInstalledAPPs(context: Context?, pkgs: ArrayList<String>?): ArrayList<String> {
        val empty = ArrayList<String>()
        if (context == null || pkgs == null || pkgs.size == 0)
            return empty
        val pm = context.packageManager
        val installedPkgs = pm.getInstalledPackages(0)
        val li = installedPkgs.size
        val lj = pkgs.size
        for (j in 0..lj - 1) {
            for (i in 0..li - 1) {
                var installPkg = ""
                val checkPkg = pkgs[j]
                try {
                    installPkg = installedPkgs[i].applicationInfo.packageName
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (TextUtils.isEmpty(installPkg))
                    continue
                if (installPkg == checkPkg) {
                    empty.add(installPkg)
                    break
                }

            }
        }
        return empty
    }

    /**
     * 跳转到app详情界面
     * @param appPkg App的包名
     * *
     * @param marketPkg
     * *  应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    fun launchAppDetail(appPkg: String, marketPkg: String) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return
            val uri = Uri.parse("market://details?id=" + appPkg)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (!TextUtils.isEmpty(marketPkg))
                intent.`package` = marketPkg
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            App.CONTEXT!!.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}