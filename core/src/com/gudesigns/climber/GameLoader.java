package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.Globals;
import AdsInterface.IActivityRequestHandler;
import JSONifier.JSONCar;
import Purchases.PlatformResolver;
import Purchases.PurchaseManager;

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
	public PurchaseManager purchaseManager = new PurchaseManager();

	public GameLoader(IActivityRequestHandler handler) {
		super();
		Globals.nativeRequestHandler = handler;
	}

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

	public void setPlatformResolver(PlatformResolver platformResolver) {
		PurchaseManager.setPlatformResolver(platformResolver);
	}

	public PlatformResolver getPlatformResolver() {
		if(purchaseManager == null)return null;
		
		return purchaseManager.getPlatformResolver();
	}

}