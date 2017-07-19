package wang.relish.accumulation.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import wang.relish.accumulation.App;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseData implements Serializable{

    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
                String value;
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
            if (isExist()) {
                return db.update(getClass().getSimpleName().toLowerCase(), cv, "id = ?", new String[]{id + ""});
            } else {
                return db.insert(getClass().getSimpleName().toLowerCase(), null, cv);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean isExist() {
        DBHelper helper = DBHelper.getInstance(App.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + getClass().getSimpleName().toLowerCase() +
                " where id = ?", new String[]{id + ""});
        boolean isExist = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return isExist;
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
