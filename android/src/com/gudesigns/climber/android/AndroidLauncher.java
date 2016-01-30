package com.gudesigns.climber.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Mesh;
import com.gudesigns.climber.GameLoader;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 0;
		config.hideStatusBar = true;
		config.touchSleepTime = 16;
		Mesh.clearAllMeshes(this);
		initialize(new GameLoader(), config);
	}
}
