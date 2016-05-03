package com.gudesigns.climber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Mesh;
import com.gudesigns.climber.GameLoader;

public class DesktopLauncher {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 2;
		config.width = 1080;
		config.height = 720;

		GameLoader game = new GameLoader(null);

		game.setPlatformResolver(new DesktopResolver());

		Mesh.clearAllMeshes(new LwjglApplication(game, config));
	}
}
