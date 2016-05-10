package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.HashMap;

import wrapper.GameAssetManager;
import wrapper.Globals;
import AdsInterface.IActivityRequestHandler;
import JSONifier.JSONCar;
import Purchases.GameItemInformation;
import Purchases.IAPManager;
import Purchases.PurchaseManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class GameLoader extends Game {

	// private GamePlayScreen gamePlayScreen;
	// private BuilderScreen builderScreen;
	// private MainMenuScreen menuScreen;
	private LoaderScreen loadScreen;

	public GameAssetManager Assets;
	public ArrayList<JSONCar> cars = new ArrayList<JSONCar>();
	public PurchaseManager purchaseManager = new PurchaseManager();
	public final HashMap<String,GameItemInformation> IAPItemInformation = new HashMap<String,GameItemInformation>();

	public GameLoader(IActivityRequestHandler handler) {
		super();
		Globals.nativeRequestHandler = handler;
	}

	@Override
	public void create() {
		Globals.updateScreenInfo();
		Assets = new GameAssetManager();// new GameAssetManager();
		//
		// gamePlayScreen = new GamePlayScreen(this);
		// builderScreen = new BuilderScreen(this);
		// menuScreen = new MainMenuScreen(this);
		loadScreen = new LoaderScreen(this);
		setScreen(loadScreen);
	}

	@Override
	public void setScreen(Screen screen) {
		// System.out.println("switching screen");
		Screen curScreen = this.getScreen();
		if (curScreen != null) {
			this.getScreen().dispose();
		}
		System.gc();
		super.setScreen(screen);
	}

	public void setPlatformResolver(IAPManager platformResolver) {
		PurchaseManager.setPlatformResolver(platformResolver);
	}

	public IAPManager getPlatformResolver() {
		if (purchaseManager == null)
			return null;

		return purchaseManager.getPlatformResolver();
	}

}