package com.zereb.netarena.net.packets;

import com.zereb.netarena.net.RemoteClient;

import java.util.ArrayList;
import java.util.HashMap;

public class GameSetup {
    public int gameID;

    public GameSetup(){}

    public GameSetup(int gameID){
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return "GameSetup{" +
                "gameID=" + gameID +
                '}';
    }
}
