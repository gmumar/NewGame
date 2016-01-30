package com.gudesigns.climber;

import wrapper.GameAssetManager;
import wrapper.Globals;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class GameLoader extends Game {

	//private GamePlayScreen gamePlayScreen;
	//private BuilderScreen builderScreen;
	//private MainMenuScreen menuScreen;
	private LoaderScreen loadScreen;
	
	public AssetManager Assets;
	
	@Override
	public void create() {
		Globals.updateScreenInfo();
		Assets = new GameAssetManager();
		
		//gamePlayScreen = new GamePlayScreen(this);
		//builderScreen = new BuilderScreen(this);
		//menuScreen = new MainMenuScreen(this);
		loadScreen = new LoaderScreen(this);
		setScreen(loadScreen);
	}
	
	
	
	@Override
	public void setScreen(Screen screen) {
		//System.out.println("switching screen");
		Screen curScreen = this.getScreen();
		if(curScreen!=null){
			this.getScreen().dispose();
		}
		System.gc();
		super.setScreen(screen);
	}

}