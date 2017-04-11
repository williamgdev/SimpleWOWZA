package com.randmcnally.bb.poc.interactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randmcnally.bb.poc.database.DaoMaster;
import com.randmcnally.bb.poc.database.DaoSession;
import com.randmcnally.bb.poc.database.VoiceMailDB;
import com.randmcnally.bb.poc.database.VoiceMailDBDao;

import org.greenrobot.greendao.identityscope.IdentityScope;
import org.greenrobot.greendao.query.Query;

import java.util.List;

public class DatabaseInteractor {
    private static final String DB_NAME = "ptt-db";
    DaoSession daoSession;
    VoiceMailDBDao voiceMailDBDao;

    public DatabaseInteractor(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        voiceMailDBDao = daoSession.getVoiceMailDBDao();
    }

    public void create(VoiceMailDB voiceMailDB, CreateDatabaseListener listener) {
        long key = voiceMailDBDao.insert(voiceMailDB);
        listener.onResult(voiceMailDBDao.load(key));
    }

    public void getRow(int rowIndex, ReadDatabaseListener listener){
        VoiceMailDB voiceMailDB = allVoiceMailDB().get(rowIndex);
        listener.onResult(voiceMailDB);
    }

    private List<VoiceMailDB> allVoiceMailDB() {
        return voiceMailDBDao.queryBuilder().list();
    }

    public void read(GetRowDatabaseListener listener){
        List<VoiceMailDB> voiceMailDBList = allVoiceMailDB();
        listener.onResult(voiceMailDBList);
    }

    public void update(VoiceMailDB voiceMailDB){
        voiceMailDBDao.update(voiceMailDB);
        /**
         * This method is void...
         */
    }

    public void delete(VoiceMailDB voiceMailDB, DeleteDatabaseListener listener){
        voiceMailDBDao.delete(voiceMailDB);
        listener.onResult(allVoiceMailDB().size());
    }


    public interface CreateDatabaseListener{
        void onResult(VoiceMailDB voiceMailDB);
    }

    public interface ReadDatabaseListener{
        void onResult(VoiceMailDB voiceMailDB);
    }

    public interface DeleteDatabaseListener{
        void onResult(int rows);
    }

    public interface GetRowDatabaseListener{
        void onResult(List<VoiceMailDB> voiceMailDBList);
    }

}
