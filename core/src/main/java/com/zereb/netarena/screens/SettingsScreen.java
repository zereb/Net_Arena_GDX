package com.zereb.netarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.netarena.Main;
import com.zereb.netarena.ui.ChangeNameWindow;
import com.zereb.netarena.utils.ResourceManager;

public class SettingsScreen implements Screen {
    private final Viewport viewport;
    private final Stage stage;
    private final Main main;


    public SettingsScreen(Main main){
        this.main = main;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        TextButton btnBack = new TextButton("back", ResourceManager.INSTANCE.skin());
        TextButton btnSetName = new TextButton("set new name", ResourceManager.INSTANCE.skin());

        btnBack.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                main.setScreen(new MainMenuScreen(main));
            }
        });

        Window setNameWindow = new ChangeNameWindow(ResourceManager.INSTANCE.skin());
        setNameWindow.setVisible(false);

        btnSetName.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setNameWindow.setVisible(true);
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(btnSetName);
        table.row();
        table.add(btnBack).padTop(10f).height(40);

        table.setDebug(Main.isDebug);
        table.pack();

        stage.addActor(table);
        stage.addActor(setNameWindow);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        main.batch.setProjectionMatrix(stage.getCamera().combined);
        main.batch.begin();
//        main.batch.draw(bg, Main.WIDTH / 2f - bg.getWidth() / 2f, 0);
        main.batch.end();

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        stage.dispose();
    }
}
