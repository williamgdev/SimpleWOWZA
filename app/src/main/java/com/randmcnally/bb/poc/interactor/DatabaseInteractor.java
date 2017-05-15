package com.randmcnally.bb.poc.interactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randmcnally.bb.poc.dao.ChannelEntity;
import com.randmcnally.bb.poc.dao.DaoMaster;
import com.randmcnally.bb.poc.dao.DaoSession;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.util.ChannelUtil;

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

    // History methods //

    private List<HistoryEntity> getAllHistoryRows() {
        QueryBuilder<HistoryEntity> query = daoSession.getHistoryEntityDao().queryBuilder();
        if(query.count() == 0)
            return new ArrayList<>();
        return query.list();
    }

    public void readOrCreateHistoryByName(String name, final DatabaseListener<HistoryEntity> listener){
        List<HistoryEntity> histories = getAllHistoryRows();
        int index = findHistoryItem(name, histories);
        if (index > histories.size() || index == -1){
            HistoryEntity history = new HistoryEntity();
            history.setId(name);
            createHistory(history, new DatabaseListener<HistoryEntity>() {
                @Override
                public void onResult(HistoryEntity result) {
                    listener.onResult(result);
                }
            });
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
    private int findHistoryItem(String name, List<HistoryEntity> histories) {
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).getId().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void updateHistory(String name, DatabaseListener<HistoryEntity> listener, String json) {
        HistoryEntity historyEntity = new HistoryEntity(name, json);
        daoSession.getHistoryEntityDao().update(historyEntity);
        listener.onResult(daoSession.getHistoryEntityDao().load(historyEntity.getId()));
    }

    public void readHistory(DatabaseListener<List<HistoryEntity>> listener) {
        List<HistoryEntity> history = getAllHistoryRows();
        listener.onResult(history);
    }

    public void createHistory(HistoryEntity history, DatabaseListener<HistoryEntity> listener) {
        long index = daoSession.getHistoryEntityDao().insert(history);
        listener.onResult(daoSession.getHistoryEntityDao().load(history.getId()));
    }

    // Channel methods //

    private List<ChannelEntity> getAllChannelRows() {
        QueryBuilder<ChannelEntity> query = daoSession.getChannelEntityDao().queryBuilder();
        if(query.count() == 0)
            return new ArrayList<>();
        return query.list();
    }
    public void readChannels(DatabaseListener<List<ChannelEntity>> listener) {
        listener.onResult(getAllChannelRows());
    }

    public void saveChannels(List<Channel> channels, DatabaseListener<List<ChannelEntity>> listener) {
        List<ChannelEntity> channelEntities = getAllChannelRows();
        for (Channel channel :
                channels) {
            for (ChannelEntity channelEntity :
                    channelEntities) {
                if (channel.getName().equals(channelEntity.getName())) {
                    String json = ChannelUtil.getJsonFromVoiceMesssage(channel.getHistory().getVoiceMessages());
                    HistoryEntity historyEntity = new HistoryEntity(channel.getName(), json);
                    channelEntity.setHistoryEntity(historyEntity);
                    daoSession.update(channelEntity);
                }
            }
        }
        listener.onResult(channelEntities);
    }

    public void createChannel(Channel channel, DatabaseListener<ChannelEntity> listener) {
        ChannelEntity channelEntity = new ChannelEntity(channel.getName(), channel.isFavorite(), channel.getFullName());
        HistoryEntity historyEntity = new HistoryEntity(channel.getName(), ChannelUtil.getJsonFromVoiceMesssage(channel.getHistory().getVoiceMessages()));
        channelEntity.setHistoryEntity(historyEntity);

        long index = daoSession.getChannelEntityDao().insert(channelEntity);
        listener.onResult(daoSession.getChannelEntityDao().load(historyEntity.getId()));
    }

    public void updateChannel(Channel channel, DatabaseListener<ChannelEntity> listener) {
        for (ChannelEntity channelEntity :
                getAllChannelRows()) {
            if (channel.getName().equals(channelEntity.getName())){

                channelEntity.setFavorite(channel.isFavorite());

                HistoryEntity historyEntity = new HistoryEntity(channel.getName(), ChannelUtil.getJsonFromVoiceMesssage(channel.getHistory().getVoiceMessages()));
                channelEntity.setHistoryEntity(historyEntity);

                daoSession.getChannelEntityDao().update(channelEntity);
                listener.onResult(daoSession.getChannelEntityDao().load(channelEntity.getName()));
            }
        }

    }

    public void removeChannel(String channelName) {
        daoSession.getChannelEntityDao().deleteByKey(channelName);
    }

    public interface DatabaseListener<T>{
        void onResult(T result);
    }
}
