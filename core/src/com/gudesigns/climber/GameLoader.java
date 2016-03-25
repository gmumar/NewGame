package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.Globals;
import JSONifier.JSONCar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public class GameLoader extends Game {

	//private GamePlayScreen gamePlayScreen;
	//private BuilderScreen builderScreen;
	//private MainMenuScreen menuScreen;
	private LoaderScreen loadScreen;
	
	public AssetManager Assets;
	public ArrayList<JSONCar> cars = new ArrayList<JSONCar>();
	
	@Override
	public void create() {
		Globals.updateScreenInfo();
		Assets = new AssetManager();//new GameAssetManager();
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