package com.zereb.netarena;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zereb.netarena.console.Console;
import com.zereb.netarena.net.ClientController;
import com.zereb.netarena.screens.MainMenuScreen;
import com.zereb.netarena.utils.ResourceManager;

import java.io.IOException;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public static boolean isDebug = true;
    public static boolean isGod = false;
    public static boolean isPaused = false;
    public ClientController clientController;

    public SpriteBatch batch;
    public ShapeRenderer debug;

    public static int WIDTH = 800, HEIGHT = 480;
    public static final float FRAME_TIME = 0.1f;

    public static final byte KEY_LEFT = 1;
    public static final byte KEY_RIGHT = -1;
    public static final byte KEY_UP = 2;
    public static final byte KEY_DOWN = -2;
    public static final int SKILL_1 = 3;
    public static final int SKILL_2 = 4;

    private static boolean musicOn = true;
    public Music music;
    public static Console console;

    @Override
    public void create () {
        float screenRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        Gdx.app.log("Screen resolution", "Width = " + Gdx.graphics.getWidth() + " Height = " + Gdx.graphics.getHeight());
        Gdx.app.log("Screen ratio", String.valueOf(screenRatio));
        WIDTH = (int) (HEIGHT * screenRatio);
        Gdx.app.log("Calculated resolution", WIDTH + "x" + HEIGHT);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        batch = new SpriteBatch();
        debug = new ShapeRenderer();
        debug.setAutoShapeType(true);

        clientController = new ClientController(this);
        this.setScreen(new MainMenuScreen(this));
    }


    @Override
    public void render () {
        super.render();
        try {
            if (console != null)
                console.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMusicOn(){
        return musicOn;
    }

    public void setMusic(Music music) {
        if (this.music != null)
            this.music.dispose();
        this.music = music;
        this.music.setLooping(true);
        if (musicOn)
            music.play();
    }

    public void musicSwitch(){
        musicOn = !musicOn;
        if (musicOn) music.play();
        else music.stop();
    }

    @Override
    public void dispose () {
        Gdx.app.log("Main: ", "Disposing");
        getScreen().dispose();
        batch.dispose();
        debug.dispose();
        ResourceManager.INSTANCE.dispose();
    }

}