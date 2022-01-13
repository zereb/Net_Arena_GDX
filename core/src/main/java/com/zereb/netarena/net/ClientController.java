package com.zereb.netarena.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.zereb.netarena.Main;
import com.zereb.netarena.Player;
import com.zereb.netarena.PlayerRender;
import com.zereb.netarena.net.packets.*;
import com.zereb.netarena.screens.GameScreen;
import com.zereb.netarena.utils.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ClosedSelectorException;
import java.util.HashMap;

public class ClientController {

    public Client client;
    private final Main main;
    private GameScreen gameScreen;
    public final Queue<Packet> packets = new Queue<>();
    private long lastConnectionAttempt;

    {
        client = new Client();
        client.start();
        Kryo kryo = client.getKryo();
        kryo.setRegistrationRequired(false);
    }

    public ClientController(Main main) {
        this.main = main;


        setupController();
    }

    public void connect(){
        lastConnectionAttempt = System.currentTimeMillis();
        InetAddress address = client.discoverHost(54777, 5000);
        if (address != null)
            connect(address.getHostAddress());
    }

    public void connect(String ip){
        if (client.isConnected() && client.getRemoteAddressTCP().getAddress().getHostAddress().equals(ip))
            return;

        lastConnectionAttempt = System.currentTimeMillis();
        try {
            client.connect(5000, ip, 54555, 54777);

            //todo name
            client.sendTCP(new Name("test" + MathUtils.random()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public long lastConnectionAttempt(){
        return lastConnectionAttempt;
    }

    public void setupController() {
        Listener.TypeListener typeListener = new Listener.TypeListener();

        typeListener.addTypeHandler(GameSetup.class, ((connection, gameSetup) -> {
            Gdx.app.log("Game setup", gameSetup.toString());
            Gdx.app.postRunnable(() -> {
                gameScreen = new GameScreen(main, this, gameSetup);
                main.setScreen(gameScreen);
            });
        }));

//        typeListener.addTypeHandler(VersionCheck.class, (connection, versionCheck) -> {
//            if (!versionCheck.equals(versionCheck.version)){
//                Gdx.app.error("Version check", "Old client version");
//                client.close();
//            }
//        });


        typeListener.addTypeHandler(ClientConnected.class, ((connection, clientConnected) -> {
            if (gameScreen != null)
                gameScreen.addPlayer(clientConnected.id, clientConnected.name);
        }));

        typeListener.addTypeHandler(ClientDisconnected.class, ((connection, clientDisconnected) -> {
            if (gameScreen != null)
                Gdx.app.postRunnable(() -> gameScreen.removePlayer(clientDisconnected.id));
        }));

        typeListener.addTypeHandler(GameInstanceSnapshot.class, (connection, snapshot) -> {
            Gdx.app.postRunnable(() -> packets.addFirst(new Packet(snapshot)));
            Gdx.app.log("qeue", packets.toString());
        });

        typeListener.addTypeHandler(Error.class, ((connection, error) ->
                error.printStackTrace())
        );

        client.addListener(typeListener);
    }
}
