package com.randmcnally.bb.poc.interactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randmcnally.bb.poc.database.DaoMaster;
import com.randmcnally.bb.poc.database.DaoSession;
import com.randmcnally.bb.poc.database.HistoryDB;
import com.randmcnally.bb.poc.database.HistoryDBDao;
import com.randmcnally.bb.poc.database.VoiceMessageDB;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DatabaseInteractor {
    private final DaoSession daoSession;
    private final HistoryDBDao historyDBDao;
    HistoryDB historyDB;
    private static final String DB_NAME = "database";

    List<VoiceMessageDB> currentHistory = new ArrayList<>();
    String currentHistoryName;
    private boolean currentHistoryFlag;

    public DatabaseInteractor(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        historyDBDao = daoSession.getHistoryDBDao();
        currentHistoryName = "";
    }

    private List<HistoryDB> getAllRows() {
        return historyDBDao.queryBuilder().list();
    }

    public void getVoiceMessages(String name, GetHistoryDBListener listener) {
        List<HistoryDB> histories = getAllRows();
        List<VoiceMessageDB> voiceMessages = getListVoiceMessagessFromString(name, histories);
        listener.onResult(voiceMessages);
        setCurrentHistory(name, voiceMessages);
    }

    private List<VoiceMessageDB> getListVoiceMessagessFromString(String name, List<HistoryDB> histories) {
        Type type = new TypeToken<List<VoiceMessageDB>>() {}.getType();
        //search wich history has the same name and getMessages to convert to List
        int index = findItem(name, histories);
        if (index > histories.size() || index == -1){
            return new ArrayList<>();
        }
        // return the List of VoiceMessages
        List<VoiceMessageDB> voiceMessages = new Gson().fromJson(histories.get(index).getMessages(), type);
        if (voiceMessages == null){
            return new ArrayList<>();
        }
        return voiceMessages;
    }

    private int findItem(String name, List<HistoryDB> histories) {
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).getId().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void setCurrentHistory(String name, List<VoiceMessageDB> voiceMessageDBs) {
        this.currentHistoryName = name;
        this.currentHistory = voiceMessageDBs;
    }

    public boolean currentHistoryFlagOff() {
        return !currentHistoryFlag;
    }

    public void updateHistory(String name, VoiceMessageDB voiceMessage, AddVoiceMessageDBListener listener) {
        if (!currentHistoryFlagOff()) {
            throw new ConcurrentModificationException(); //Improve the temp History concurrence
        }

        currentHistoryFlag = true;
        if (currentHistory == null || !currentHistoryName.equals(name)) {
            List<HistoryDB> histories = getAllRows();
            List<VoiceMessageDB> voiceMessages = getListVoiceMessagessFromString(name, histories);
            setCurrentHistory(name, voiceMessages);
        }

        currentHistory.add(voiceMessage);
        /**
         * Todo get limit of message globally. Actual is 5.
         */
        if (currentHistory.size() > 5) {
            currentHistory.remove(0);
            // if limit reached, remove the first one
            // because the first one will be the oldest.
        }

        Type listType = new TypeToken<List<VoiceMessageDB>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(currentHistory, listType);


        update(new HistoryDB(name, json), listener);

        currentHistoryFlag = false;

    }

    private void update(HistoryDB historyDB, AddVoiceMessageDBListener listener) {
        historyDBDao.update(historyDB);
        listener.onResult(currentHistory.size());
    }

    public void read(GetHistoriesDBListener listener) {
        List<HistoryDB> history = getAllRows();
        listener.onResult(history);
    }

    public void create(HistoryDB history, CreateHistoryDBListener listener) {
        long key = historyDBDao.insert(history);
        listener.onResult(historyDBDao.load(history.getId()));
    }

    public interface GetHistoriesDBListener {
        void onResult(List<HistoryDB> histories);
    }

    public interface GetHistoryDBListener {
        void onResult(List<VoiceMessageDB> voiceMessages);
    }

    public interface CreateHistoryDBListener {
        void onResult(HistoryDB history);
    }

    public interface AddVoiceMessageDBListener {
        void onResult(int size);
    }
}
