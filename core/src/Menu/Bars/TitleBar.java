package Menu.Bars;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import Dialog.StoreBuyDialog;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.TableW;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import RESTWrapper.BackendFunctions;
import RESTWrapper.RESTPaths;
import UserPackage.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.CarModeScreen;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GameModeScreen;
import com.gudesigns.climber.MainMenuScreen;
import com.gudesigns.climber.SelectorScreens.ForrestTrackSelectorScreen;

public class TitleBar {

	static Label coins;
	static Label animationCoins;
	static User user;

	public final static TitleBarObject create(Table base,
			final ScreenType type, final PopQueManager popQueManager,
			final GameState gameState, final BarObjects barObjects,
			boolean animate) {

		Animations.InitMoneyAnimation();

		final GameLoader gameLoader = gameState.getGameLoader();
		Skin skin = Skins.loadDefault(gameLoader, 0);

		TableW titleBar = new TableW(skin);
		;
		if (type == ScreenType.CAR_BUILDER) {
			titleBar.setBackground("darkGrey");
		}

		user = User.getInstance();

		// Back button
		Button back = SimpleImageButton.create(SimpleImageButtonTypes.BACK,
				gameLoader);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (type == ScreenType.MODE_SCREEN) {
					gameLoader.setScreen(new MainMenuScreen(gameLoader));
				} else if (type == ScreenType.FORREST_TRACK_SELECTOR
						|| type == ScreenType.ARCTIC_TRACK_SELECTOR
						|| type == ScreenType.INFINITE_TRACK_SELECTOR) {
					gameLoader.setScreen(new GameModeScreen(gameState));
				} else if (type == ScreenType.CAR_SELECTOR) {
					gameLoader.setScreen(new CarModeScreen(gameState));
				} else if (type == ScreenType.CAR_BUILDER) {
					gameLoader.setScreen(new CarModeScreen(gameState));
				} else if (type == ScreenType.CAR_MODE_SCREEN) {
					gameLoader.setScreen(new ForrestTrackSelectorScreen(
							gameState));
				}
				super.clicked(event, x, y);
			}

		});

		if (type != ScreenType.MAIN_MENU_SCREEN) {
			titleBar.add(back).left().colspan(1);
		}

		// Buy coins button
		Button buyCoins = new Button(skin, "transparentButton");
		buyCoins.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				/*
				 * popQueManager.push(new
				 * PopQueObject(PopQueObjectType.LOADING));
				 * 
				 * Globals.runOnUIThread(new Runnable() {
				 * 
				 * @Override public void run() {
				 * gameLoader.getPlatformResolver().requestInformation( new
				 * GamePurchaseObserver() {
				 * 
				 * @Override public void handleRecievedInformation(
				 * Purchases.GamePurchaseResult gamePurchaseResult) {
				 * popQueManager.push(new PopQueObject(
				 * PopQueObjectType.DELETE));
				 * 
				 * popQueManager.push(new PopQueObject(
				 * PopQueObjectType.STORE_BUY)); } }); } });
				 */
				StoreBuyDialog.launchDialogFlow(gameLoader, popQueManager);
				super.clicked(event, x, y);
			}

		});

		Image glowingCoin = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/glowing_coin.png"));
		buyCoins.add(glowingCoin).width(Globals.baseSize * 2.5f)
				.height(Globals.baseSize * 2f);
		Stack coinsStack = new Stack();
		coins = new Label(Globals.makeMoneyString(user.getMoney()), skin,
				"glowing-text");
		animationCoins = new Label(Globals.makeMoneyString(user.getMoney()),
				skin, "glowing-text");
		coinsStack.add(coins);
		coinsStack.add(animationCoins);
		buyCoins.add(coinsStack);
		if (type != ScreenType.MAIN_MENU_SCREEN) {
			titleBar.add(buyCoins).left().colspan(1).pad(4);
		}

		// Title
		Label titleLabel = new Label("", skin, "title");

		// titleLabel.setPosition(Globals.ScreenWidth / 2,
		// Globals.ScreenHeight / 12);
		if (type == ScreenType.CAR_BUILDER) {
			titleBar.add(titleLabel).expand().right().padRight(200);
		} else {
			titleBar.add(titleLabel).expand().right().padRight(260);
		}

		if (type == ScreenType.MODE_SCREEN) {
			titleLabel.setText("Game Mode");
		} else if (type == ScreenType.FORREST_TRACK_SELECTOR
				|| type == ScreenType.ARCTIC_TRACK_SELECTOR
				|| type == ScreenType.INFINITE_TRACK_SELECTOR) {
			titleLabel.setText("Select Track");
		} else if (type == ScreenType.CAR_SELECTOR) {
			titleLabel.setText("Select Car");
		} else if (type == ScreenType.CAR_MODE_SCREEN) {
			titleLabel.setText("Select Car");
		} else if (type == ScreenType.CAR_BUILDER) {
			titleLabel.setText("Build Car");

			// Upload
			Button upload = SimpleImageButton.create(
					SimpleImageButtonTypes.UPLOAD, gameLoader);
			upload.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (barObjects.menuBuilder.buildCar()) {
						BackendFunctions.uploadCar(user.getCurrentCar(),
								RESTPaths.COMMUNITY_CARS_DUMP);
					}

					super.clicked(event, x, y);
				}

			});

			titleBar.add(upload).right();

		} else if (type == ScreenType.MAIN_MENU_SCREEN) {
			titleLabel.setText(" ");
		}

		// Sound
		Button sound = SimpleImageButton.create(SimpleImageButtonTypes.SOUND,
				gameLoader);
		sound.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueManager.push(new PopQueObject(PopQueObjectType.SOUND));
				super.clicked(event, x, y);
			}

		});

		titleBar.add(sound).right();
		
		Table container = new Table(skin);
		
		container.add(titleBar).fillX().height(Globals.baseSize * 2.5f).expandX()
		.center().pad(12);
		if (type == ScreenType.CAR_BUILDER) {
			container.setBackground("darkGrey");
		}

		base.add(container).fillX().height(Globals.baseSize * 3f).expandX()
		.center();

		base.row();

		if (animate) {
			// Animations.slideInFromTop(titleBar, -50);
		}
		return new TitleBarObject(coins, animationCoins);

	}

}
