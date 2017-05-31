package com.randmcnally.bb.poc.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randmcnally.bb.poc.custom.BBGroupChat;
import com.randmcnally.bb.poc.dao.ChannelEntity;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.dto.eventbus.HistoryMessage;
import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.interactor.Red5ProApiInteractor;
import com.randmcnally.bb.poc.model.Channel;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.LiveStream;
import com.randmcnally.bb.poc.model.Playlist;
import com.randmcnally.bb.poc.model.VoiceMessage;

import org.jivesoftware.smack.packet.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class ChannelUtil {

    private static final String TAG = "ChannelUtil ->";
    /**
     * Todo get limit of message globally. Actual is 5 and start from 0.
     */
    private static final int HISTORY_LIMIT = 5;

    @NonNull
    public static List<VoiceMessageEntity> voiceMessageEntityToList(HistoryEntity result) {
        Type type = new TypeToken<List<VoiceMessageEntity>>() {}.getType();
        List<VoiceMessageEntity> voiceMessages = new Gson().fromJson(result.getMessages(), type);
        if (voiceMessages == null){
            voiceMessages = new ArrayList<>();
        }
        return voiceMessages;
    }

    public static String getJsonFromVoiceMessageEntity(List<VoiceMessageEntity> voiceMessages) {
        Type listType = new TypeToken<List<VoiceMessageEntity>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(voiceMessages, listType);
    }

    public static String getJsonFromVoiceMesssage(List<VoiceMessage> voiceMessages) {
        List<VoiceMessageEntity> voiceMessageEntities = new ArrayList<>();
        for (VoiceMessage voiceMessage :
                voiceMessages) {
            voiceMessageEntities.add(new VoiceMessageEntity(voiceMessage.getName()));
        }
        Type listType = new TypeToken<List<VoiceMessageEntity>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(voiceMessageEntities, listType);
    }

    public static List<VoiceMessage> getMissedMessage(List<VoiceMessage> history, List<VoiceMessage> voiceMessages) {
        List<VoiceMessage> missedMessages = new ArrayList<>();
        for (VoiceMessage voiceMessage :
                history) {
            missedMessages.add(voiceMessage);
        }
        missedMessages.removeAll(voiceMessages);

        return missedMessages;
    }

    public static String getPublishName(String streamName, String streamId) {
        return streamName + "_" + streamId;
    }

    public static String getPublishName(String streamName, int streamId) {
        return streamName + "_" + streamId;
    }

    public static List<Channel> hasChanges(List<Channel> oldestChannel, List<Channel> recentChannels) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : recentChannels) {
            if (oldestChannel.contains(channel)){
                channel.setFavorite(oldestChannel.get(oldestChannel.indexOf(channel)).isFavorite());
            }
            result.add(channel);

        }
        return result;
    }


    public static void removeListenedMessages(HistoryEntity history, VoiceMessageEntity voiceMessage, DatabaseInteractor databaseInteractor) {
        List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(history);
        voiceMessages.remove(voiceMessage);

        String json = ChannelUtil.getJsonFromVoiceMessageEntity(voiceMessages);

        databaseInteractor.updateHistory(history.getId(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                Log.d(TAG, "updateHistory: Saved Voice Messages " + result.getId() + " - " + result.getMessages().toString());
            }
        }, json);

    }

    public static void addListenedMessages(HistoryEntity history, VoiceMessageEntity voiceMessage, DatabaseInteractor databaseInteractor) {
        List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(history);
        voiceMessages.add(voiceMessage);

        if (voiceMessages.size() - 1 > HISTORY_LIMIT) {
            voiceMessages.remove(0);
            // if limit reached, remove the first one
            // because the first one will be the oldest.
        }

        String json = ChannelUtil.getJsonFromVoiceMessageEntity(voiceMessages);

        databaseInteractor.updateHistory(history.getId(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                Log.d(TAG, "updateHistory: Saved Voice Messages " + result.getId() + " - " + result.getMessages().toString());
            }
        }, json);

    }

    public static void notifyMessageListened(final LiveStream streamReceived, final DatabaseInteractor databaseInteractor) {
        databaseInteractor.readOrCreateHistoryByName(streamReceived.getStreamName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity history) {
                if (history.getId() != null) {
                    VoiceMessageEntity voiceMessageEntity = new VoiceMessageEntity(ChannelUtil.getPublishName(streamReceived.getStreamName(), streamReceived.getId()));
                    ChannelUtil.addListenedMessages(history, voiceMessageEntity, databaseInteractor); //TODO create a Add/Remove VoiceMessage
                }
            }
        });
    }

    public static void notifyMessageMissed(final LiveStream streamReceived, final DatabaseInteractor databaseInteractor) {
        databaseInteractor.readOrCreateHistoryByName(streamReceived.getStreamName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity history) {
                if (history.getId() != null) {
                    VoiceMessageEntity voiceMessageEntity = new VoiceMessageEntity(ChannelUtil.getPublishName(streamReceived.getStreamName(), streamReceived.getId()));
                    ChannelUtil.removeListenedMessages(history, voiceMessageEntity, databaseInteractor); //TODO create a Add/Remove VoiceMessage
                }
            }
        });
    }

    public static Channel findChannel(String streamName, List<Channel> channels) {
        for (Channel channel :
                channels) {
            if (channel.getName().equals(streamName)){
                return channel;
            }
        }
        return null;
    }


    public static List<HistoryMessage> convertToHistoryMessage(List<VoiceMessage> voiceMessages) {
        List<HistoryMessage> result = new ArrayList<>();
        for (int i = 0; i < voiceMessages.size(); i++) {
            result.add(new HistoryMessage(voiceMessages.get(i), i));
        }
        return result;
    }

    public static void updateChannelMissedMessages(final Channel channel, DatabaseInteractor databaseInteractor ) {
        databaseInteractor.readOrCreateHistoryByName(channel.getName(), new DatabaseInteractor.DatabaseListener<HistoryEntity>() {
            @Override
            public void onResult(HistoryEntity result) {
                List<VoiceMessageEntity> voiceMessages = ChannelUtil.voiceMessageEntityToList(result);
                List<VoiceMessage> missedMessages = ChannelUtil.getMissedMessage(channel.getHistory().getVoiceMessages(), VoiceMessage.createFromVoiceMessagelEntity(voiceMessages));
                channel.getHistory().setMissedMessages(Playlist.create(missedMessages));
            }
        });
    }

    public static void addMessageToHistory(History history, VoiceMessage voiceMessage) {
        history.getVoiceMessages().add(voiceMessage);
        if (history.getVoiceMessages().size() - 1 > HISTORY_LIMIT) {
            history.getVoiceMessages().remove(0);
            // if limit reached, remove the first one
            // because the first one will be the oldest.
        }
    }

    public static void addMessageToBBGroupChat(List<Message> messageList, Message message) {
        messageList.add(message);
        if (messageList.size() > 5){
            messageList.remove(0);
        }
    }
}
