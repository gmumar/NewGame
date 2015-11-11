package com.gudesigns.climber;

import com.badlogic.gdx.Game;

public class GameLoader extends Game {

	GamePlayScreen gamePlayScreen;
	
	@Override
	public void create() {
		gamePlayScreen = new GamePlayScreen();
		setScreen(gamePlayScreen);
	}

}