package com.zereb.netarena.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Save {
    private final Preferences preferences;
    public static final Save INSTANCE = new Save();
    public String name;


    private Save(){
        preferences = Gdx.app.getPreferences("save");
        load();
    }

    private void load() {
        name = preferences.getString("name", "player");
        Gdx.app.log("Loaded save", toString());
    }

    public void save(String name){
        preferences.putString("name", name);

        preferences.flush();
        load();
    }
}