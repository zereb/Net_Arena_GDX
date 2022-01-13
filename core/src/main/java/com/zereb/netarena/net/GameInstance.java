package com.zereb.netarena.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.zereb.netarena.*;
import com.zereb.netarena.net.packets.*;

import java.util.ArrayList;


public class GameInstance {

    public final int gameID;
    private final ArrayList<RemoteClient> clients;
    private final Map map;
    private final CollisionFiltering collisionFiltering;
    private final String TAG;

    public static final int MAX_PLAYERS = 6;


    public GameInstance(int id) {
        gameID = id;
        TAG = "Game instance " + id;
        this.clients = new ArrayList<>();
        map = new Map("arena01", true);
        collisionFiltering = new CollisionFiltering(map);
        Gdx.app.log(TAG, "created");
    }

    public void addClient(RemoteClient remoteClient) {
        if (clients.contains(remoteClient)) {
            Gdx.app.error(TAG, "attempt to add existing client!");
            return;
        }

        Vector2 spawn = map.getRandomSpawn();
        remoteClient.player = new Player(remoteClient.name(), spawn.x, spawn.y);
        Gdx.app.log(TAG, "client joined " + remoteClient.name());
        remoteClient.setClientState(RemoteClient.ClientState.INGAME);

        collisionFiltering.addEntity(remoteClient.player);
        remoteClient.player.initHeadlessAnimation();

        ClientConnected clientConnected = new ClientConnected(remoteClient.name(), remoteClient.connection.getID());
        clients.forEach(remoteClient1 -> remoteClient.connection.sendTCP(clientConnected));

        clients.add(remoteClient);
    }

    public void update() {

        clients.forEach(remoteClient -> {
            remoteClient.keys.forEach(key -> {
                if (key == Main.KEY_UP) remoteClient.player.vel.y = Player.SPEED;
                if (key == Main.KEY_DOWN) remoteClient.player.vel.y = -Player.SPEED;
                if (key == Main.KEY_RIGHT) remoteClient.player.vel.x = Player.SPEED;
                if (key == Main.KEY_LEFT) remoteClient.player.vel.x = -Player.SPEED;
                if (key == Main.SKILL_1) {
                    remoteClient.player.attack();
                    remoteClient.keys.remove(key);
                }
            });

            if (remoteClient.touchUp != null) {
                remoteClient.player.vel.x = Player.SPEED * remoteClient.touchUp.x;
                remoteClient.player.vel.y = Player.SPEED * remoteClient.touchUp.y;
            }

            remoteClient.touchUp = null;

            if (remoteClient.player.isDead)
                remoteClient.player.respawnCooldown.fireWithoutCooldown(() -> {
                    remoteClient.player.isDead = false;
                    remoteClient.player.position.set(map.getRandomSpawn());
                    remoteClient.player.life(100);
                });

            remoteClient.player.update(GameServer.delta);
        });

        collisionFiltering.filter();

        GameInstanceSnapshot snapshot = new GameInstanceSnapshot();
        clients.forEach(remoteClient -> {
            PlayerSnapshot playerSnapshot = remoteClient.player.createSnapshot(remoteClient.connection.getID(), remoteClient.connection.getReturnTripTime());
            snapshot.players.put(remoteClient.connection.getID(), playerSnapshot);
        });

        clients.forEach(remoteClient -> {
            if (remoteClient.clientState() == RemoteClient.ClientState.READY)
                remoteClient.connection.sendUDP(snapshot);
        });

    }

    public void removeClient(RemoteClient client) {
        if (!clients.contains(client)) {
            Gdx.app.debug(TAG, "client not this in game");
            return;
        }
        clients.remove(client);
        Gdx.app.debug(TAG, "removed client: " + client.name() + " from game instance: " + gameID);
        client.setClientState(RemoteClient.ClientState.IDLE);

        ClientDisconnected clientDisconnected = new ClientDisconnected(client.connection.getID());
        clients.forEach(remoteClient -> remoteClient.connection.sendTCP(clientDisconnected));
    }


    public int totalPlayers() {
        return clients.size();
    }

    @Override
    public String toString() {
        return "GameInstance{" +
                "gameID=" + gameID +
                ", clients=" + clients +
                ", map=" + map +
                ", collisionFiltering=" + collisionFiltering +
                '}';
    }
}
