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



    public static List<VoiceMessage> getMissedMessage(List<VoiceMessage> history, List<VoiceMessage> voiceMessages) {
        List<VoiceMessage> missedMessages = history.subList(0, history.size());
        missedMessages.removeAll(voiceMessages);

        return missedMessages;
    }
}
