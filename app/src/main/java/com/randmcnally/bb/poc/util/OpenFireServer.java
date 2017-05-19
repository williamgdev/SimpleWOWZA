package com.randmcnally.bb.poc.util;

import android.util.Log;

import com.randmcnally.bb.poc.custom.BBGroupChat;
import com.randmcnally.bb.poc.interactor.OpenFireApiInteractor;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import needle.Needle;

public class OpenFireServer implements ConnectionListener {
    private static final String TAG = "OpenFireServer ->";
    private static OpenFireServer instance;
    private DomainBareJid groupChatService;
    private OpenFireServerListener listener;
    private AbstractXMPPConnection connection;
    private MultiUserChatManager multiUserChatManager;
    private HashMap<String,BBGroupChat> groupChatHistoryHashMap;

    private OpenFireServer(String UID, String ipAddress) {
        XMPPTCPConnectionConfiguration.Builder configBuilder;
        configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(UID, UID);
        try {
            configBuilder.setXmppDomain(OpenFireApiInteractor.XMPP_DOMAIN);

            configBuilder.setHost(ipAddress);
            connection = new XMPPTCPConnection(configBuilder.build());
            connection.addConnectionListener(this);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        groupChatHistoryHashMap = new HashMap<>();
    }

    public static OpenFireServer getInstance(String UID, String ipAddress) {
        if (instance == null){
            instance = new OpenFireServer(UID, ipAddress);
        }
        /**
         * TODO check if UID is the same as a current connection
         */
        return instance;
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public boolean isAuthenticated() {
        return connection.isAuthenticated();
    }

    public void setListener(OpenFireServerListener listener) {
        this.listener = listener;
    }

    private void addGroupChat(BBGroupChat bbGroupChat) {
        groupChatHistoryHashMap.put(bbGroupChat.getUniqueIdentifier(), bbGroupChat);

    }

    public HashMap<String, BBGroupChat> getGroupChatHistoryHashMap() {
        return groupChatHistoryHashMap;
    }

    public void connectOpenFireServer() {
        Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * TODO Merge all Exception as a unique process and one listener Connect, Login, Authenticate
                     */
                    connection.connect();
                } catch (SmackException e) {
                    if (e.getMessage().equals("Client is already connected")){
                        Log.d(TAG, "Try to login again: ");
                        /**
                         * TODO send the notification to the listener and the listener do the login
                         */
                        login();
                    }
                    else {
                        Log.d(TAG, "connectOpenFireServer: " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void login() {
        boolean success = false;
        try {
            connection.login();
            success = true;
        } catch (XMPPException e) {
            if (e instanceof SASLErrorException){
                SASLErrorException saslErrorException = (SASLErrorException) e;
                switch (saslErrorException.getSASLFailure().getSASLError()) {
                    case not_authorized:
                        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.NOT_AUTHORIZED, e.getMessage());
                        break;

                    default:
                        e.printStackTrace();
                        break;
                }
            } else {
                Log.d(TAG, "login: " + e.getMessage());
            }
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!success)
            listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.ERROR, "Error login");
    }

    public void join(final OpenFireListener<HashMap<String, BBGroupChat>> listener) {
        getHostedRooms(new OpenFireListener<List<HostedRoom>>() {
            @Override
            public void onSuccess(List<HostedRoom> result) {
                for (HostedRoom room :
                        result) {
                    final MultiUserChat chat = multiUserChatManager.getMultiUserChat(room.getJid());
                    joinToGroupChat(chat, new OpenFireListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> result) {
                            Log.d(TAG, "onSuccess: Joint to the GroupChat - " + chat.getRoom().getLocalpart());
                        }

                        @Override
                        public void onError(String message) {
                            Log.d(TAG, "onError: " + message);
                        }
                    });
                }
                listener.onSuccess(groupChatHistoryHashMap);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });

    }

    public void joinToGroupChat(final MultiUserChat multiUserChat, final OpenFireListener<List<Message>> listener) {

        if (!multiUserChat.isJoined()) {
            try {
                multiUserChat.join(connection.getUser().getResourcepart());
            } catch (InterruptedException e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                listener.onError(e.getMessage());
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                listener.onError(e.getMessage());
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                listener.onError(e.getMessage());
                e.printStackTrace();
            } catch (MultiUserChatException.NotAMucServiceException e) {
                listener.onError(e.getMessage());
                e.printStackTrace();
            }
        }

        getPendingMessages(multiUserChat, new OpenFireListener<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                multiUserChat.addMessageListener(messageListener);
                BBGroupChat bbGroupChat = new BBGroupChat(multiUserChat, result);
                addGroupChat(bbGroupChat);
                listener.onSuccess(result);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }




    public void sendNotification(final MultiUserChat multiUserChat, String streamName, int id) {
        if (!isAuthenticated()) {
            listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.ERROR, "Error authenticating");
            return;
        }
        final Message message = new Message();
        message.setSubject(streamName);
        message.setBody(String.valueOf(id));

        if (multiUserChat != null) {
            Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        multiUserChat.sendMessage(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void getGroupChatRoom(final String groupChatRoomId, final OpenFireListener<MultiUserChat> listener){
        getHostedRooms(new OpenFireListener<List<HostedRoom>>() {
            @Override
            public void onSuccess(List<HostedRoom> rooms) {
                for (HostedRoom room : rooms) {
                    if (room.getJid().getLocalpart().equals(groupChatRoomId)){
                        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(room.getJid());
                        listener.onSuccess(multiUserChat);
                        return;
                    }
                }
                listener.onError("GroupChat not found.");
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });

    }

    public void getHostedRooms(final OpenFireListener<List<HostedRoom>> listener) {
        Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * TODO get the value for service domains depend of the name instead of 0.
                     */
                    groupChatService = multiUserChatManager.getXMPPServiceDomains().get(0);

                    List<HostedRoom> rooms = multiUserChatManager.getHostedRooms(groupChatService.asDomainBareJid());
                    if (rooms != null)
                        listener.onSuccess(rooms);
                    else
                        listener.onError("Check if the server has the right implementation for GroupChatServices: " + groupChatService.getDomain());

                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MultiUserChatException.NotAMucServiceException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getPendingMessages(MultiUserChat multiUserChat, OpenFireListener<List<Message>> listener) {
        List<Message> oldMessages = new ArrayList<>();
        try {
            Message message = multiUserChat.nextMessage();

            while (message != null) {
                oldMessages.add(message);
                message = multiUserChat.nextMessage();
            }
        } catch (MultiUserChatException.MucNotJoinedException e) {
            e.printStackTrace();
            listener.onError(e.getMessage());
        } catch (InterruptedException e) {
            listener.onError(e.getMessage());
            e.printStackTrace();
        }
        listener.onSuccess(oldMessages);
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTED, "");
        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        login();
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.AUTHENTICATED, "");
    }

    @Override
    public void connectionClosed() {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTION_CLOSED, "Connection Closed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTION_CLOSED, "Connection Closed: " + e.getMessage());

    }

    @Override
    public void reconnectionSuccessful() {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_SUCCESS, "Reconnection Successfully");

    }

    @Override
    public void reconnectingIn(int i) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_SUCCESS, "Reconnection in: " + i);

    }

    @Override
    public void reconnectionFailed(Exception e) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_FAILED, ("Reconnection Failed"));

    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Message message) {
            updateGroupChat(message);
            listener.notifyMessage(message.getSubject(), message.getBody());
        }
    };

    private void updateGroupChat(Message message) {
        getGroupChatHistoryHashMap().get(message.getSubject()).addMessage(message);
        groupChatHistoryHashMap.put(message.getSubject(), getGroupChatHistoryHashMap().get(message.getSubject()));
    }

    public interface OpenFireListener<T>{
        void onSuccess(T result);
        void onError(String message);
    }

    public interface OpenFireServerListener {
        enum STATE {ERROR, CONNECTION_CLOSED, RECONNECTION_SUCCESS, RECONNECTION_FAILED, AUTHENTICATED, NOT_AUTHORIZED, CONNECTED}

        void notifyStatusOpenFireServer(STATE state, String message);
        void notifyMessage(String streamName, String streamId);

    }

}
