package com.randmcnally.bb.poc.interactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randmcnally.bb.poc.dao.DaoMaster;
import com.randmcnally.bb.poc.dao.DaoSession;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.HistoryEntityDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInteractor {
    private final DaoSession daoSession;
    private static final String DB_NAME = "database";
    private static DatabaseInteractor instance;

    private DatabaseInteractor(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DatabaseInteractor getInstance(Context context){
        if (instance == null){
            instance = new DatabaseInteractor(context);
        }
        return instance;
    }

    private List<HistoryEntity> getAllRows() {
        QueryBuilder<HistoryEntity> query = daoSession.getHistoryEntityDao().queryBuilder();
        if(query.count() == 0)
            return new ArrayList<>();
        return query.list();
    }

    public void readByName(String name, DatabaseListener<HistoryEntity> listener){
        List<HistoryEntity> histories = getAllRows();
        int index = findItem(name, histories);
        if (index > histories.size() || index == -1){
            listener.onResult(new HistoryEntity());
        }else {
            listener.onResult(histories.get(index));
        }
    }

    /**
     *
     * @param name
     * @param histories
     * @return If the name it does not exist the result is -1.
     */
    private int findItem(String name, List<HistoryEntity> histories) {
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).getId().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void update(String name, DatabaseListener<HistoryEntityDao> listener, String json) {
        daoSession.getHistoryEntityDao().update(new HistoryEntity(name, json));
        listener.onResult(daoSession.getHistoryEntityDao());
    }

    public void read(DatabaseListener<List<HistoryEntity>> listener) {
        List<HistoryEntity> history = getAllRows();
        listener.onResult(history);
    }

    public void create(HistoryEntity history, DatabaseListener<HistoryEntity> listener) {
        long key = daoSession.getHistoryEntityDao().insert(history);
        listener.onResult(daoSession.getHistoryEntityDao().load(history.getId()));
    }

//    public interface GetHistoriesDBListener {
//        void onResult(List<HistoryEntity> histories);
//    }
//
//    public interface GetHistoryDBListener {
//        void onResult(List<VoiceMessageEntity> voiceMessages);
//    }
//
//    public interface CreateHistoryDBListener {
//        void onResult(HistoryEntity history);
//    }
//
//    public interface AddVoiceMessageDBListener {
//        void onResult(HistoryEntityDao historyEntityDao);
//    }

    public interface DatabaseListener<T>{
        void onResult(T result);
    }
}
