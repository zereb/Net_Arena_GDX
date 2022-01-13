package com.zereb.netarena.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.netarena.Main;
import com.zereb.netarena.net.ClientController;
import com.zereb.netarena.net.packets.GameDisconnect;
import com.zereb.netarena.net.packets.KeyPressed;
import com.zereb.netarena.screens.MainMenuScreen;
import com.zereb.netarena.utils.ResourceManager;

public class HUD implements Disposable {

    public final OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private final Main main;

    private final Touchpad touchpad;
    private final ImageButton btnPause;
    private final TextButton btnExit;
    private final Skin skin;
    private final SkillButton btnSkill;

    private final String TAG = "HUD";

    private boolean isPaused = false;

    public HUD(Main main, InputMultiplexer inputMultiplexer, ClientController clientController) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);


        stage = new Stage(viewport);

        skin = ResourceManager.INSTANCE.skin();

        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, skin);
        touchpad.setBounds(50, 50, 200, 200);

        stage.addActor(touchpad);

        inputMultiplexer.addProcessor(stage);

        //pause button
        btnPause = new ImageButton(skin.getDrawable("pause"));
        btnPause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                touglePause();
            }
        });


        btnPause.setPosition(Main.WIDTH - 20f, Main.HEIGHT - 20f, Align.topRight);
        stage.addActor(btnPause);


        //resume and main menu buttons
        btnExit = new TextButton("MAIN MENU", skin);
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientController.client.sendTCP(new GameDisconnect());
                main.setScreen(new MainMenuScreen(main));
            }
        });
        btnExit.setPosition(Main.WIDTH / 2f - btnExit.getWidth() / 2f, Main.HEIGHT / 2f, Align.left);
        btnExit.setVisible(false);
        stage.addActor(btnExit);

        btnSkill = new SkillButton(skin.getDrawable("attack"), 0.25f, skin.getFont("font"));
        btnSkill.setPosition(Main.WIDTH - 20 - btnSkill.getWidth(), 20);
        btnSkill.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientController.client.sendTCP(new KeyPressed(Main.SKILL_1));
                btnSkill.fire();
            }
        });
        stage.addActor(btnSkill);

    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public void render(SpriteBatch batch) {
        btnSkill.update(Gdx.graphics.getDeltaTime());
        stage.setDebugAll(Main.isDebug);
        stage.act();
        stage.draw();
    }

    public void touglePause() {
        isPaused = !isPaused;

        if (isPaused) {
            btnPause.setBackground(skin.getDrawable("play"));
            btnExit.setVisible(true);
        } else {
            btnPause.setBackground(skin.getDrawable("pause"));
            btnExit.setVisible(false);
        }

    }

    public void resize(int w, int h) {
        viewport.update(w, h);
    }

    @Override
    public void dispose() {
        stage.dispose();

    }
}
