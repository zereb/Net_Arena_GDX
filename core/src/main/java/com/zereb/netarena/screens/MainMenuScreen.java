package com.zereb.netarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.netarena.Main;
import com.zereb.netarena.net.ClientController;
import com.zereb.netarena.net.packets.QueueMM;
import com.zereb.netarena.ui.ChangeNameWindow;
import com.zereb.netarena.utils.ResourceManager;
import com.zereb.netarena.utils.Save;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMenuScreen implements Screen {

    private final Main main;
    private final Viewport viewport;
    private final Stage stage;

    private Label labelConnection;
    private final Skin skin;
    private ExecutorService service;
    private TextButton btnPlay;

    public MainMenuScreen(Main main) {
        this.main = main;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        skin = ResourceManager.INSTANCE.skin();
        service = Executors.newSingleThreadExecutor();
    }

    @Override
    public void show() {
        btnPlay = new TextButton("PLAY", skin, "special");
        ImageButton btnInfo = new ImageButton(skin, "info");
        ImageButton bntSettings = new ImageButton(skin, "gear");

        labelConnection = new Label("Offline", skin, "red");
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (main.clientController.client.isConnected()){
                    main.clientController.client.sendTCP(new QueueMM());
                    btnPlay.setText("LOADING...");
                }
            }
        });

        bntSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new SettingsScreen(main));
            }
        });

        btnInfo.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new InfoScreen(main));
            }
        });


        HorizontalGroup miscVG = new HorizontalGroup();
        btnInfo.padRight(10);
        miscVG.addActor(btnInfo);
        miscVG.addActor(bntSettings);

        Container<Actor> empty = new Container<>();

        Table table = new Table();
        table.setFillParent(true);

        table.add(labelConnection).top().left().pad(10);
        table.add(miscVG).right().top().pad(10);
        table.row();
        table.add(empty).expand();
        table.add(btnPlay).bottom().padBottom(10);

        stage.addActor(table);

        if (Save.INSTANCE.name.equals("player")){
            Window setName = new ChangeNameWindow(skin);
            stage.addActor(setName);
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);

        stage.setDebugAll(Main.isDebug);

        if (main.clientController.client.isConnected()) {
            labelConnection.setColor(skin.getColor("green"));
            labelConnection.setText("Connected: " + main.clientController.client.getRemoteAddressTCP().getAddress().getHostAddress());
            btnPlay.setDisabled(false);
        } else {
            labelConnection.setColor(skin.getColor("red"));
            labelConnection.setText("Offline");
            btnPlay.setDisabled(true);
            if (TimeUtils.timeSinceMillis(main.clientController.lastConnectionAttempt()) > 10000) {
                service.execute(() -> main.clientController.connect());
            }
        }

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        service.shutdownNow();

    }


    @Override
    public void dispose() {
        stage.dispose();
        service.shutdownNow();
    }
}
