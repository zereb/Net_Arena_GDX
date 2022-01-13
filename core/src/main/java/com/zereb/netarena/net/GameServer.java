package com.zereb.netarena.net;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.zereb.netarena.Main;
import com.zereb.netarena.net.packets.*;
import com.zereb.netarena.utils.Cooldown;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class GameServer implements ApplicationListener {

    private final Map<Connection, RemoteClient> remoteClients = new HashMap<>();
    private final Map<Integer, GameInstance> activeGames = new HashMap<>();

    public static final String version = "1.0";
    public static final float TARGET_DELTA = 0.032f;

    private final Server server;
    private int gamesCreated = 0;
    private final Cooldown matchMakingTimer = new Cooldown(3);
    public static float delta;
    private static String TAG = "Game server";
    private ScheduledExecutorService updateLoop;



    public GameServer() throws IOException {
        server = new Server();
        server.start();
        server.bind(54555, 54777);

        Kryo kryo = server.getKryo();
        kryo.setRegistrationRequired(false);

        Listener.TypeListener listener = new Listener.TypeListener();

        listener.addTypeHandler(Ready.class, (connection, ready) -> {
            RemoteClient remoteClient = remoteClients.get(connection);
            remoteClient.setClientState(RemoteClient.ClientState.READY);
        });

        listener.addTypeHandler(Name.class, ((connection, name) -> {
            RemoteClient remoteClient = remoteClients.get(connection);
            remoteClient.setName(name.string);
        }));

        listener.addTypeHandler(KeyPressed.class, ((connection, keyPressed) -> {
            RemoteClient remoteClient = remoteClients.get(connection);
            remoteClient.keys.add(keyPressed.key);
        }));

        listener.addTypeHandler(KeyReleased.class, (connection, keyReleased) -> {
            RemoteClient remoteClient = remoteClients.get(connection);
            remoteClient.keys.remove(keyReleased.key);
        });

        listener.addTypeHandler(TouchPad.class, ((connection, touchUp) -> {
            RemoteClient remoteClient = remoteClients.get(connection);
            remoteClient.touchUp = touchUp;
        }));

        listener.addTypeHandler(QueueMM.class, (connection, queueMM) -> {
            queueClientMatchmaking(connection);
        });

        listener.addTypeHandler(GameDisconnect.class, (connection, gameDisconnect) -> {
            RemoteClient clientToRemove = remoteClients.get(connection);
            if (clientToRemove != null)
                removeClientFromGame(clientToRemove);

        });

        server.addListener(listener);

        server.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                if (!remoteClients.containsKey(connection))
                    return;

                RemoteClient clientToRemove = remoteClients.get(connection);
                remoteClients.remove(connection);

                removeClientFromGame(clientToRemove);
            }

            @Override
            public void connected(Connection connection) {
                addClient(connection);
                connection.sendTCP(new VersionCheck(GameServer.version));
            }
        });


        long sleep = (long) (1000 * TARGET_DELTA);
        AtomicLong currentTime = new AtomicLong(System.nanoTime());

        updateLoop = Executors.newSingleThreadScheduledExecutor();
        updateLoop.scheduleAtFixedRate(() -> {
            long newTime = TimeUtils.nanoTime();
            long frameTime = TimeUtils.timeSinceNanos(currentTime.get());
            currentTime.set(newTime);
            GameServer.delta = frameTime / 1000000000f;

            matchMakingTimer.fire(this::attemptMatchmake);

            activeGames.values().forEach(GameInstance::update);

        }, 0L, sleep, TimeUnit.MILLISECONDS);


    }

    public void addClient(Connection connection) {
        remoteClients.putIfAbsent(connection, new RemoteClient(connection));
    }

    public void removeClientFromGame(RemoteClient remoteClient) {
       GameInstance gameInstance = activeGames.get(remoteClient.getGameID());
        if (gameInstance == null) return;

        gameInstance.removeClient(remoteClient);
        if (gameInstance.totalPlayers() == 0) {
            activeGames.remove(gameInstance.gameID);
            Gdx.app.log(TAG, "Closed game: " + gameInstance.gameID);
        }
    }

    public void queueClientMatchmaking(Connection connection) {
        if (!remoteClients.containsKey(connection)) {
            Gdx.app.error(TAG, "remoteClient provided for mm queue does not exist id: " + connection.getID());
            return;
        }

        RemoteClient client = remoteClients.get(connection);
        client.setClientState(RemoteClient.ClientState.QUEUED);
    }


    public void attemptMatchmake() {
        if (remoteClients.isEmpty())
            return;

        for (RemoteClient remoteClient : this.remoteClients.values()) {
            if (remoteClient.clientState() != RemoteClient.ClientState.QUEUED)
                continue;

            //todo bug
            GameInstance gameInstance = activeGames.values()
                    .stream()
                    .filter(instance -> instance.totalPlayers() < GameInstance.MAX_PLAYERS)
                    .findAny()
                    .orElseGet(() -> {
                        gamesCreated++;
                        GameInstance newGame = new GameInstance(gamesCreated);
                        activeGames.putIfAbsent(gamesCreated, newGame);
                        return newGame;
                    });
            gameInstance.addClient(remoteClient);
            GameSetup gameSetup = new GameSetup(gamesCreated);

            remoteClient.getConnection().sendTCP(gameSetup);
            remoteClient.setClientState(RemoteClient.ClientState.INGAME);
            remoteClient.setGameID(gamesCreated);
        }

    }


    public void dispose(Main main) {

    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        updateLoop.shutdown();
//        try {
//            server.close();
//        } catch (ClosedSelectorException e) {
//            e.printStackTrace();
//        }

    }
}