package wang.relish.accumulation.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

import wang.relish.accumulation.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "USER".
 */
public class UserDao extends AbstractDao<User, String> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Name = new Property(0, String.class, "name", false, "NAME");
        public final static Property Password = new Property(1, String.class, "password", false, "PASSWORD");
        public final static Property Mobile = new Property(2, String.class, "mobile", true, "MOBILE");
        public final static Property Photo = new Property(3, String.class, "photo", false, "PHOTO");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }

    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"NAME\" TEXT," + // 0: name
                "\"PASSWORD\" TEXT," + // 1: password
                "\"MOBILE\" TEXT PRIMARY KEY NOT NULL ," + // 2: mobile
                "\"PHOTO\" TEXT);"); // 3: photo
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, User entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(2, password);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(3, mobile);
        }
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(4, photo);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(2, password);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(3, mobile);
        }
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(4, photo);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2);
    }    

    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // name
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // password
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mobile
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // photo
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPassword(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMobile(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPhoto(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final String updateKeyAfterInsert(User entity, long rowId) {
        return entity.getMobile();
    }
    
    @Override
    public String getKey(User entity) {
        if (entity != null) {
            return entity.getMobile();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(User entity) {
        return entity.getMobile() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
