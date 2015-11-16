package com.gudesigns.climber;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class GameLoader extends Game {

	GamePlayScreen gamePlayScreen;
	BuilderScreen builderScreen;
	
	@Override
	public void create() {
		//gamePlayScreen = new GamePlayScreen(this);
		builderScreen = new BuilderScreen(this);
		setScreen(builderScreen);
	}
	
	public void gameSetScreen(Screen s){
		this.getScreen().dispose();
		setScreen(s);
	}

}