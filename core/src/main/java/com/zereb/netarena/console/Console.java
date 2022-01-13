package com.zereb.netarena.console;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.zereb.netarena.Main;
import com.zereb.netarena.net.ClientInterpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

public class Console {

    private HashMap<String, Command> commands = new HashMap<>();
    private final Scanner in = new Scanner(System.in);
    private final String TAG = "Console";

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    {
        commands.put("debug", (args) -> {
            Main.isDebug = !Main.isDebug;
            Gdx.app.log("Console", "debug " + Main.isDebug);
        } );

        commands.put("help", args ->
                commands.keySet().forEach(cmd -> Gdx.app.log(TAG, cmd))
        );

        commands.put("lerp", args -> {
            ClientInterpolator.INTERPOLATION_DELAY = Float.parseFloat(getArgumentOrDefault(args, 1, "0.064"));
           Gdx.app.log(TAG, "ClientInterpolator.INTERPOLATION_DELAY " + ClientInterpolator.INTERPOLATION_DELAY);
        });

    }

    public void update() throws IOException {
        if (!br.ready()) return;

        String command = br.readLine();

        commands.forEach((cmd, run) -> {
            if (command.startsWith(cmd))
                run.run(command);
        });


    }

    public static String getArgumentOrDefault(String string, int index, String def){
        String[] args  = string.split(" ");
        if (args.length > index && index >= 0)
            return args[index];
        return def;
    }
}
