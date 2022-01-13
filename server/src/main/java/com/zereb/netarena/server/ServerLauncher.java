package com.zereb.netarena.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.zereb.netarena.net.GameServer;

import java.io.IOException;

/** Launches the server application. */
public class ServerLauncher {
	public static void main(String[] args) throws IOException {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		Lwjgl3NativesLoader.load();
		Gdx.files = new Lwjgl3Files();
		new HeadlessApplication(new GameServer(), configuration);
	}
}