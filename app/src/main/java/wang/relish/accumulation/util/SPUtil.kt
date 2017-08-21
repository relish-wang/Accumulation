package wang.relish.accumulation.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import wang.relish.accumulation.App
import wang.relish.accumulation.entity.User

/**
 * SharedPreferences 工具类
 * Created by Relish on 2016/12/3.
 */

object SPUtil {
    private val TAG = "SPUtil"

    private var settings: SharedPreferences? = null

    init {
        settings = App.CONTEXT!!.getSharedPreferences("user", Context.MODE_PRIVATE)
    }

    val user: User
        get() {
            val user = User()
            user.name = getString("name", "")
            user.password = getString("password", "")
            user.photo = getString("photo", "")
            user.mobile = getString("mobile", "")
            return user
        }


    fun saveUser(user: User?) {
        if (user == null) {
            AppLog.e(TAG, "saveUser", "coming param is null!!!")
            return
        }
        save("name", user.name)
        save("photo", user.photo)
        save("password", user.password)
        save("mobile", user.mobile)
    }


    private fun save(key: String, value: Any?) {
        if (value is String) {

            putString(key, checkNull(key, value as String?))
        } else if (value is Int) {

            putInt(key, checkNull(key, value as Int?)!!)
        } else if (value is Long) {

            putLong(key, checkNull(key, value as Long?)!!)
        } else if (value is Float) {

            putFloat(key, checkNull(key, value as Float?)!!)
        } else if (value is Boolean) {

            putBoolean(key, checkNull(key, value as Boolean?)!!)
        } else {
            if (value != null) {
                AppLog.e(TAG, "save", "Unknown type: " + value.javaClass.toString())
            }
        }
    }

    private fun <T : Any> checkNull(key: String, value: T?): T? {
        if (value is String) {

            return if (TextUtils.isEmpty(value as String?) || value.equals("null", ignoreCase = true))
                getString(key, "") as T
            else
                value
        } else if (value is Int || value is Boolean ||
                value is Long || value is Float) {

            return value
        } else {
            if (value != null)
                AppLog.e(TAG, "checkNull", "Unknown type: " + value.javaClass)
            return null
        }
    }

    fun putString(key: String?, value: String?): Boolean {
        if (key != null && value != null) {
            val editor = settings!!.edit()
            editor.putString(key, value)
            return editor.commit()
        } else {
            return false
        }
    }

    @JvmOverloads fun getString(key: String, defaultValue: String = (null as String?)!!): String {
        return settings!!.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int): Boolean {
        val editor = settings!!.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    @JvmOverloads fun getInt(key: String, defaultValue: Int = -1): Int {
        return settings!!.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long): Boolean {
        val editor = settings!!.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    @JvmOverloads fun getLong(key: String, defaultValue: Long = -1L): Long {
        return settings!!.getLong(key, defaultValue)
    }

    fun putFloat(key: String, value: Float): Boolean {
        val editor = settings!!.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    @JvmOverloads fun getFloat(key: String, defaultValue: Float = -1.0f): Float {
        return settings!!.getFloat(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        val editor = settings!!.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    @JvmOverloads fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return settings!!.getBoolean(key, defaultValue)
    }

    fun clearAll() {
        val editor = settings!!.edit()
        editor.clear().apply()
    }


}
