package wang.relish.accumulation.dao

import android.content.ContentValues
import wang.relish.accumulation.App
import java.io.Serializable
import java.lang.reflect.ParameterizedType

abstract class BaseData : Serializable {

    var id: Long = 0

    open fun save(): Long {
        try {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cv = ContentValues()
            val clazz = javaClass
            val fields = clazz.declaredFields

            for (field in fields) {
                field.isAccessible = true
                val key = field.name
                var value = ""
                val oValue = field.get(this)
                if (oValue != null) {
                    if (oValue is List<*>) {//List
                        if (oValue.size > 0) {
                            if (oValue[0] is BaseData) {
                                for (data in oValue as List<BaseData>) {
                                    data.save()
                                }
                            }
                        }
                    } else {
                        value = field.get(this).toString() + ""
                        cv.put(key, value)
                    }
                }
            }
            if (isExist) {
                return db.update(javaClass.simpleName.toLowerCase(), cv, "id = ?", arrayOf(id.toString() + "")).toLong()
            } else {
                return db.insert(javaClass.simpleName.toLowerCase(), null, cv)
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            return -1
        }

    }

    private val isExist: Boolean
        get() {
            val helper = DBHelper.getInstance(App.CONTEXT!!)
            val db = helper.readableDatabase
            val cursor = db.rawQuery("select * from " + javaClass.simpleName.toLowerCase() +
                    " where id = ?", arrayOf(id.toString() + ""))
            val isExist = cursor != null && cursor.count > 0
            cursor?.close()
            return isExist
        }


    /**
     * 获得泛型类型
     *
     *
     * 注：其中DBSupport<T>是泛型类
     * 在父类中声明getGenericType
     * 子类继承具体的DBSupport<Person>
     * 那么在子类中就可以通过getGenericType()获取到Person的class.

     * @return T.class
    </Person></T> */
    private val genericType: Class<*>
        get() {
            val genType = javaClass.genericSuperclass as? ParameterizedType ?: return Any::class.java
            val params = genType.actualTypeArguments
            if (params[0] !is Class<*>) {
                return Any::class.java
            }
            return params[0] as Class<*>
        }
}
