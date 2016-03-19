package com.gudesigns.climber.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

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
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        hideVirtualButtons();
	    }
		
		initialize(new GameLoader(), config);
	}
	
	@TargetApi(19)
	private void hideVirtualButtons() {
	    getWindow().getDecorView().setSystemUiVisibility(
	              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	            | View.SYSTEM_UI_FLAG_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	        // In KITKAT (4.4) and next releases, hide the virtual buttons
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	            hideVirtualButtons();
	        }
	    }
	}
}
