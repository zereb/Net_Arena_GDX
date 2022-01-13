package com.zereb.netarena.net.packets;

import java.util.HashMap;

public class GameInstanceSnapshot {

    public HashMap<Integer, PlayerSnapshot> players = new HashMap<>();

    public GameInstanceSnapshot(){}

    public GameInstanceSnapshot(GameInstanceSnapshot snapshot){
        snapshot.players.values().forEach(playerSnapshot ->
                players.put(playerSnapshot.id, new PlayerSnapshot(playerSnapshot))
        );
    }

    @Override
    public String toString() {
        return "GameInstanceSnapshot{" +
                "players=" + players +
                '}';
    }
}
