package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.Skins;
import Dialog.StoreBuyDialog;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Bars.BottomBar;
import Menu.Bars.TitleBar;
import Menu.Bars.TitleBarObject;
import Menu.Buttons.ButtonLockWrapper;
import Menu.Buttons.ModeButton;
import Menu.Buttons.ModeButton.ModeButtonTypes;
import UserPackage.Costs;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.SelectorScreens.CarSelectorScreen;
import com.gudesigns.climber.SelectorScreens.CommunityCarSelectorScreen;

public class CarModeScreen implements Screen, TwoButtonDialogFlow {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;
	private PopQueManager popQueManager;
	private CarModeScreen context;

	private ButtonLockWrapper carBuilder, myPicks, communityCars;

	private Table buttonHolder, base;

	private Skin skin;
	private GameLoader gameLoader;
	private GameState gameState;

	private TitleBarObject titleBar;
	private Integer currentMoney;

	private User user;

	public CarModeScreen(final GameState gameState) {
		context = this;
		gameLoader = gameState.getGameLoader();
		this.gameState = gameState;
		skin = Skins.loadDefault(gameLoader, 1);
		user = gameState.getUser();

		initStage();

		popQueManager = new PopQueManager(gameLoader, stage);

		base = new Table(skin);
		base.setFillParent(true);
		// base.pad(25);

		titleBar = TitleBar.create(base, ScreenType.CAR_MODE_SCREEN,
				popQueManager, gameState, null, true);

		currentMoney = user.getMoney();

		// Main Buttons
		buttonHolder = new Table(skin);

		initButtons();
		base.add(buttonHolder).expand().fill();

		BottomBar.create(base, ScreenType.CAR_MODE_SCREEN, gameState, true);

		// Animations.fadeInAndSlideSide(base);

		stage.addActor(base);

	}

	private void initButtons() {
		carBuilder = ModeButton.create(skin, gameLoader,
				ModeButtonTypes.CAR_BUILDER, false, false);
		carBuilder.button.setChecked(true);

		carBuilder.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!carBuilder.locked) {
					gameLoader.setScreen(new CarBuilderScreen(gameState));
				}
			}

		});

		myPicks = ModeButton.create(skin, gameLoader,
				ModeButtonTypes.CAR_MY_PICKS, true, false);

		myPicks.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!myPicks.locked) {
					gameLoader.setScreen(new CarSelectorScreen(gameState));
				}
			}
		});

		communityCars = ModeButton.create(skin, gameLoader,
				ModeButtonTypes.CAR_COMMUNITY_CARS, true, false);

		communityCars.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!communityCars.locked) {
					gameLoader.setScreen(new CommunityCarSelectorScreen(
							gameState));
				} else {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.UNLOCK_MODE,
							ItemsLookupPrefix.COMMUNITY_CARS_MODE,
							"Unlock Mode", "Unlock Infinite Cars for: ",
							Costs.COMMUNITY_CARS_MODE, context));
				}
			}
		});

		ButtonGroup<Button> group = new ButtonGroup<Button>();
		group.add(myPicks.button);
		group.add(carBuilder.button);
		if (!communityCars.locked) {
			group.add(communityCars.button);
		}
		group.setMaxCheckCount(1);
		group.setMinCheckCount(1);

		buttonHolder.add(carBuilder.button).pad(5)
				.height(Globals.baseSize * 10).width(Globals.baseSize * 8)
				.center();
		buttonHolder.add(myPicks.button).pad(5).height(Globals.baseSize * 10)
				.width(Globals.baseSize * 8).center();
		buttonHolder.add(communityCars.button).pad(5)
				.height(Globals.baseSize * 10).width(Globals.baseSize * 8)
				.center();

	}

	@Override
	public boolean successfulTwoButtonFlow(String itemName) {
		if (itemName.compareTo(ItemsLookupPrefix.ERROR_NOT_ENOUGH_MONEY) == 0) {
			//popQueManager.push(new PopQueObject(PopQueObjectType.STORE_BUY));
			StoreBuyDialog.launchDialogFlow(gameLoader, popQueManager);

		} else {
			//gameLoader.setScreen(new CarModeScreen(gameState));
			buttonHolder.clear();
			initButtons();
		}
		return false;
	}

	@Override
	public boolean failedTwoButtonFlow(Integer moneyRequired) {
		popQueManager.push(new PopQueObject(
				PopQueObjectType.ERROR_NOT_ENOUGH_MONEY, "Not Enough Coins",
				"You need " + moneyRequired.toString() + " more coins", this));
		return false;
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
		batch = new SpriteBatch();
		stage = new Stage(vp);

	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();
		vp.update(width, height);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		Globals.updateScreenInfo();

	}

	@Override
	public void render(float delta) {
		currentMoney = Animations.money(titleBar.getAnimationMoney(),
				titleBar.getBaseMoney(), user.getMoney(), currentMoney);

		renderWorld();
		popQueManager.update();

	}

	private void renderWorld() {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();

		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

		batch.disableBlending();
		Gdx.gl20.glDisable(GL20.GL_BLEND);

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
