package wang.relish.accumulation.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import wang.relish.accumulation.entity.Goal;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GOAL".
*/
public class GoalDao extends AbstractDao<Goal, Long> {

    public static final String TABLENAME = "GOAL";

    /**
     * Properties of entity Goal.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mobile = new Property(1, String.class, "mobile", false, "MOBILE");
        public final static Property Name = new Property(2, String.class, "name", false, "name");
        public final static Property Time = new Property(3, String.class, "time", false, "TIME");
        public final static Property UpdateTime = new Property(4, String.class, "updateTime", false, "UPDATE_TIME");
    }


    public GoalDao(DaoConfig config) {
        super(config);
    }
    
    public GoalDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GOAL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MOBILE\" TEXT NOT NULL ," + // 1: mobile
                "\"name\" TEXT," + // 2: name
                "\"TIME\" TEXT NOT NULL ," + // 3: time
                "\"UPDATE_TIME\" TEXT NOT NULL );"); // 4: updateTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GOAL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Goal entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMobile());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindString(4, entity.getTime());
        stmt.bindString(5, entity.getUpdateTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Goal entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMobile());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindString(4, entity.getTime());
        stmt.bindString(5, entity.getUpdateTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Goal readEntity(Cursor cursor, int offset) {
        Goal entity = new Goal( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // mobile
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.getString(offset + 3), // time
            cursor.getString(offset + 4) // updateTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Goal entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMobile(cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.getString(offset + 3));
        entity.setUpdateTime(cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Goal entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Goal entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Goal entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
