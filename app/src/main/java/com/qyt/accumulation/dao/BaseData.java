package com.qyt.accumulation.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qyt.accumulation.App;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseData {

    @SuppressWarnings({"unchecked", "Convert2streamapi"})
    public long save() {
        try {
            DBHelper helper = DBHelper.getInstance(App.CONTEXT);
            SQLiteDatabase db = helper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            Class clazz = getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.getName();
                String value = "";
                Object oValue = field.get(this);
                if (oValue != null) {
                    if (oValue instanceof List) {//List
                        if (((List) oValue).size() > 0) {
                            if (((List) oValue).get(0) instanceof BaseData) {
                                for (BaseData data : ((List<BaseData>) oValue)) {
                                    data.save();
                                }
                            }
                        }
                    } else {
                        value = field.get(this) + "";
                        cv.put(key, value);
                    }
                }
            }
            return db.insert(getClass().getSimpleName().toLowerCase(), null, cv);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获得泛型类型
     * <p></p>
     * 注：其中DBSupport<T>是泛型类
     * 在父类中声明getGenericType
     * 子类继承具体的DBSupport<Person>
     * 那么在子类中就可以通过getGenericType()获取到Person的class.
     *
     * @return T.class
     */
    private Class getGenericType() {
        Type genType = getClass().getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!(params[0] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[0];
    }
}
