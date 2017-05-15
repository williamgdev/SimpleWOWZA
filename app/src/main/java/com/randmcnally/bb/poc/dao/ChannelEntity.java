package com.randmcnally.bb.poc.dao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

@Entity
public class ChannelEntity {
    @Id private String name;
    private boolean favorite;
    private String fullName;

    @ToOne(joinProperty = "name")
    private HistoryEntity historyEntity;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1096940961)
    private transient ChannelEntityDao myDao;

    @Generated(hash = 1925563443)
    public ChannelEntity(String name, boolean favorite, String fullName) {
        this.name = name;
        this.favorite = favorite;
        this.fullName = fullName;
    }

    @Generated(hash = 781881457)
    public ChannelEntity() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Generated(hash = 1590642214)
    private transient String historyEntity__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2037352735)
    public HistoryEntity getHistoryEntity() {
        String __key = this.name;
        if (historyEntity__resolvedKey == null
                || historyEntity__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HistoryEntityDao targetDao = daoSession.getHistoryEntityDao();
            HistoryEntity historyEntityNew = targetDao.load(__key);
            synchronized (this) {
                historyEntity = historyEntityNew;
                historyEntity__resolvedKey = __key;
            }
        }
        return historyEntity;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 889576281)
    public void setHistoryEntity(HistoryEntity historyEntity) {
        synchronized (this) {
            this.historyEntity = historyEntity;
            name = historyEntity == null ? null : historyEntity.getId();
            historyEntity__resolvedKey = name;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1862996460)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChannelEntityDao() : null;
    }


}