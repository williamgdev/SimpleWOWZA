package com.randmcnally.bb.poc.util;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randmcnally.bb.poc.dao.HistoryEntity;
import com.randmcnally.bb.poc.dao.VoiceMessageEntity;
import com.randmcnally.bb.poc.model.History;
import com.randmcnally.bb.poc.model.VoiceMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChannelUtil {

    @NonNull
    public static List<VoiceMessageEntity> voiceMessageEntityToList(HistoryEntity result) {
        Type type = new TypeToken<List<VoiceMessageEntity>>() {}.getType();
        List<VoiceMessageEntity> voiceMessages = new Gson().fromJson(result.getMessages(), type);
        if (voiceMessages == null){
            voiceMessages = new ArrayList<>();
        }
        return voiceMessages;
    }



    public static String getJsonFromHistory(HistoryEntity history, List<VoiceMessageEntity> voiceMessages) {
        Type listType = new TypeToken<List<VoiceMessageEntity>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(voiceMessages, listType);
    }



    public static List<VoiceMessage> getMissedMessage(List<VoiceMessage> history, List<VoiceMessage> voiceMessages) {
        List<VoiceMessage> missedMessages = history.subList(0, history.size());
        missedMessages.removeAll(voiceMessages);

        return missedMessages;
    }

    public static String getPublishName(String streamName, String streamId) {
        return streamName + "_" + streamId;
    }

    public static String getPublishName(String streamName, int streamId) {
        return streamName + "_" + streamId;
    }
}
