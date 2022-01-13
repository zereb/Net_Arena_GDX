package com.zereb.netarena.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.netarena.*;
import com.zereb.netarena.ui.HUD;
import com.zereb.netarena.net.ClientController;
import com.zereb.netarena.net.ClientInterpolator;
import com.zereb.netarena.net.packets.*;

import java.util.HashMap;

public class GameScreen implements Screen {

    private final Main main;
    private final ClientController clientController;
    private final int gameId;
    public HashMap<Integer, Player> players = new HashMap<>();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final ClientInterpolator clientInterpolator;

    private final Map map;
    private final HUD hud;
    private final String TAG;

    public GameScreen(Main main, ClientController clientController, GameSetup gameSetup) {
        this.main = main;
        this.clientController = clientController;
        gameId = gameSetup.gameID;
        TAG = "Game screen " + gameId;
        clientInterpolator = new ClientInterpolator();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);
        camera.zoom = 0.5f;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);

        Gdx.app.log("Game setup", String.valueOf(gameSetup));
        Gdx.app.log("My id", clientController.client.getID() + "");

        map = new Map("arena01", false);
        map.render = new MapRender(map, main.batch);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        hud = new HUD(main, inputMultiplexer, clientController);
        inputMultiplexer.addProcessor(new InputAdapter() {

            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
                    clientController.client.sendUDP(new KeyPressed(Main.KEY_LEFT));
                if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
                    clientController.client.sendUDP(new KeyPressed(Main.KEY_RIGHT));
                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
                    clientController.client.sendUDP(new KeyPressed(Main.KEY_DOWN));
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W)
                    clientController.client.sendUDP(new KeyPressed(Main.KEY_UP));

                if (keycode == Input.Keys.SPACE)
                    Gdx.app.log("ping: ", clientController.client.getReturnTripTime() + " ");
                return false;
            }

            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
                    clientController.client.sendUDP(new KeyReleased(Main.KEY_LEFT));
                if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
                    clientController.client.sendUDP(new KeyReleased(Main.KEY_RIGHT));
                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
                    clientController.client.sendUDP(new KeyReleased(Main.KEY_DOWN));
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W)
                    clientController.client.sendUDP(new KeyReleased(Main.KEY_UP));

                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);

        //TODO name
//        players.put(clientController.client.getID(), new Player("me", 0, 0));
        addPlayer(clientController.client.getID(), "me");
        clientController.client.sendTCP(new Ready());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (hud.getTouchpad().isTouched()){
            clientController.client.sendUDP(new TouchPad(hud.getTouchpad().getKnobPercentX(), hud.getTouchpad().getKnobPercentY()));
        }

        Gdx.gl.glClearColor(0.0f, 0, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameInstanceSnapshot currentGIS = clientInterpolator.getCurrentSnapshot(clientController.packets);
        applySnapshot(currentGIS);

        if (players.containsKey(clientController.client.getID()))
            camera.position.set(players.get(clientController.client.getID()).position, 0);

        camera.update();
        map.render.renderBg(camera);

        //rendering objects and entities
        main.batch.setProjectionMatrix(camera.combined);
        main.batch.begin();
        players.values().forEach(player -> {
            player.render.render(main.batch);
        });
        main.batch.end();

        map.render.renderFg();

        main.debug.setProjectionMatrix(camera.combined);
        main.debug.begin();
        main.debug.set(ShapeRenderer.ShapeType.Line);
        main.debug.setColor(Color.GREEN);
        map.render.debug(main.debug);
        players.values().forEach(player -> {
            player.render.debug(main.debug);
        });
        main.debug.end();

        hud.render(main.batch);
    }


    public void applySnapshot(GameInstanceSnapshot snapshot){
        if (snapshot == null) return;

        for (PlayerSnapshot playerSnapshot : snapshot.players.values()) {
            if (!players.containsKey(playerSnapshot.id)){
                addPlayer(playerSnapshot.id, playerSnapshot.name);
                continue;
            }

            Player player = players.get(playerSnapshot.id);
            player.applySnapshot(playerSnapshot);
        }
    }

    public void addPlayer(int id, String name) {
        if (players.containsKey(id)) return;

        Player player = new Player(name, 0, 0);
        player.render = new PlayerRender(player);
        players.put(id, player);
        Gdx.app.log("New client connected", name);
    }

    public void removePlayer(int id) {
        Gdx.app.log("Client left", players.get(id).name);
        players.remove(id);
    }




    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        hud.dispose();

    }


}
