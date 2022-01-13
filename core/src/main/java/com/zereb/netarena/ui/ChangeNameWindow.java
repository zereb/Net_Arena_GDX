package com.zereb.netarena.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.zereb.netarena.Main;
import com.zereb.netarena.utils.ResourceManager;
import com.zereb.netarena.utils.Save;

public class ChangeNameWindow extends Window {


    public ChangeNameWindow(Skin skin) {
        super("Set new name: " + Save.INSTANCE.name, skin);

        TextField nameField = new TextField(null, ResourceManager.INSTANCE.skin());
        nameField.setMaxLength(12);
        nameField.setMessageText("New name");
        nameField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.toString(c).matches("^[a-zA-Z]");

            }
        });
        TextButton yes = new TextButton("Save", ResourceManager.INSTANCE.skin());
        TextButton no = new TextButton("Cancel", ResourceManager.INSTANCE.skin());
        yes.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newName = nameField.getText();
                Save.INSTANCE.save(nameField.getText());
                setVisible(false);
            }
        });

        no.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        setSize(Main.WIDTH - 60, Main.HEIGHT - 30);
        padTop(20);
        setPosition(20, 20);
        setMovable(false);
        setVisible(true);
        getTitleLabel().setAlignment(Align.center);

        row();
        add(nameField);
        row();

        HorizontalGroup hg = new HorizontalGroup();
        no.pad(10);
        yes.pad(10);
        hg.addActor(no);
        hg.addActor(yes);

        add(hg);
    }
}
