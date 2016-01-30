package com.gudesigns.climber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Mesh;
import com.gudesigns.climber.GameLoader;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 2;
		config.width = 1280;
		config.height = 768;
		Mesh.clearAllMeshes(new LwjglApplication(new GameLoader(), config));
	}
}
