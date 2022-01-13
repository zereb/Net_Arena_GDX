package com.zereb.netarena.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.zereb.netarena.Player;
import com.zereb.netarena.net.packets.TouchPad;

import java.util.HashSet;

public class RemoteClient {

    public final Connection connection;
    private ClientState clientState;
    private int currentGameID;
    private String name;
    public Player player;
    public HashSet<Integer> keys = new HashSet<>();
    public TouchPad touchUp;

    private final String TAG;
    public enum ClientState {
        NAMELESS,
        IDLE,
        QUEUED,
        INGAME,
        READY
    }

    public RemoteClient(Connection c) {
        connection = c;
        clientState = ClientState.NAMELESS;
        currentGameID = -1;
        TAG = "Remote client " + connection.getID();
        Gdx.app.debug(TAG, "created");
    }


    public Connection getConnection() {
        return connection;
    }

    public String name() {
        return name;
    }

    public ClientState clientState() {
        return clientState;
    }

    public int getGameID() {
        return currentGameID;
    }

    public void setGameID(int id) {
        currentGameID = id;
    }

    public void setName(String nameToSet) {
        if (clientState == ClientState.NAMELESS) {
            name = nameToSet;
            clientState = ClientState.IDLE;
        } else {
            Gdx.app.error(TAG, "Tried to set a name for a client that already has one!");
        }
    }

    public void setClientState(ClientState newState) {
        if (clientState == ClientState.NAMELESS) {
            Gdx.app.error(TAG, "Nameless client");
            return;
        }

        if (newState == ClientState.QUEUED && clientState == ClientState.IDLE) {
            clientState = ClientState.QUEUED;
            Gdx.app.log(TAG, " has queued for matchmaking!");
            return;
        }

        clientState = newState;
    }


    @Override
    public String toString() {
        return "RemoteClient{" +
                "connection=" + connection +
                ", clientState=" + clientState +
                ", currentGameID=" + currentGameID +
                ", name='" + name + '\'' +
                ", player=" + player +
                ", keys=" + keys +
                '}';
    }
}
