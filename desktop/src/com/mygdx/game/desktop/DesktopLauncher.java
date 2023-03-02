package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GameClient;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = GameClient.WIDTH;
		config.height = GameClient.HEIGHT;
		config.resizable = false;
		config.foregroundFPS = 120;
		config.vSyncEnabled = false;

		new LwjglApplication(new GameClient(), config);
	}
}
