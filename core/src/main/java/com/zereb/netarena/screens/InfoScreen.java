package com.zereb.netarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.netarena.Main;
import com.zereb.netarena.utils.ResourceManager;

public class InfoScreen implements Screen {
    private final Viewport viewport;
    private final Table table;
    private final Stage stage;
    private final TextButton btnBack;
    private final Main main;
//    private final Texture bg;

    private Label info;

    public InfoScreen(Main main){
        this.main = main;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        Skin skin = ResourceManager.INSTANCE.skin();
        info = new Label("", skin);

//        bg = ResourseManager.INSTANCE.loadTexture("bg_menu.png");

        btnBack = new TextButton("BACK", skin);
        info.setText(" Developed by Plett Oleg" +
                "\n Contact: zizereb@gmail.com"
        );
        info.setAlignment(Align.center);
        info.setWrap(true);

        //todo illigalArgument


        table = new Table();
        table.setFillParent(true);
        table.pad(10f);
        table.row();
        table.add(info);
        table.row();
        table.add(btnBack).padTop(10f).height(40);
        btnBack.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("InfoScreen", "back");
                dispose();
                main.setScreen(new MainMenuScreen(main));
            }
        });

        table.setDebug(Main.isDebug);
        table.pack();

        stage.addActor(table);


    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
