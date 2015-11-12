package com.gudesigns.climber;

import com.badlogic.gdx.Game;

public class GameLoader extends Game {

	GamePlayScreen gamePlayScreen;
	BuilderScreen builderScreen;
	
	@Override
	public void create() {
		gamePlayScreen = new GamePlayScreen(this);
		builderScreen = new BuilderScreen(this);
		setScreen(builderScreen);
	}

}