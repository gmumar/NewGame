package com.gudesigns.climber;

import wrapper.GameAssetManager;
import wrapper.Globals;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class GameLoader extends Game {

	GamePlayScreen gamePlayScreen;
	BuilderScreen builderScreen;
	MainMenuScreen menuScreen;
	LoaderScreen loadScreen;
	
	
	@Override
	public void create() {
		Globals.updateScreenInfo();
		Globals.Assets = new GameAssetManager();
		
		//gamePlayScreen = new GamePlayScreen(this);
		//builderScreen = new BuilderScreen(this);
		//menuScreen = new MainMenuScreen(this);
		loadScreen = new LoaderScreen(this);
		setScreen(loadScreen);
	}
	
	public void gameSetScreen(Screen s){
		this.getScreen().dispose();
		setScreen(s);
		System.gc();
	}

}