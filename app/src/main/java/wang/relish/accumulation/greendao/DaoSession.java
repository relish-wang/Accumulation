package wang.relish.accumulation.greendao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig goalDaoConfig;
    private final DaoConfig recordDaoConfig;
    private final DaoConfig userDaoConfig;

    private final GoalDao goalDao;
    private final RecordDao recordDao;
    private final UserDao userDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        goalDaoConfig = daoConfigMap.get(GoalDao.class).clone();
        goalDaoConfig.initIdentityScope(type);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        goalDao = new GoalDao(goalDaoConfig, this);
        recordDao = new RecordDao(recordDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(Goal.class, goalDao);
        registerDao(Record.class, recordDao);
        registerDao(User.class, userDao);
    }

    public void clear() {
        goalDaoConfig.clearIdentityScope();
        recordDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
    }

    public GoalDao getGoalDao() {
        return goalDao;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
