package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Assembly.Assembler;
import Dialog.Skins;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Bars.TitleBar;
import Menu.Bars.TitleBarObject;
import Menu.Buttons.AdventureTrackButton;
import Menu.Buttons.ButtonLockWrapper;
import UserPackage.TrackMode;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;
import UserPackage.User.GameMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ChallengeCreationScreen implements Screen, TwoButtonDialogFlow {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;
	private PopQueManager popQueManager;

	private Table buttonHolder, base;

	private Skin skin;
	private GameLoader gameLoader;
	private GameState gameState;

	private ChallengeCreationScreen context;

	private TitleBarObject titleBar;
	private Integer currentMoney;

	private User user;

	public ChallengeCreationScreen(final GameState gameState) {
		User.getInstance().setCurrentGameMode(GameMode.SET_CHALLENGE);
		context = this;
		gameLoader = gameState.getGameLoader();
		skin = Skins.loadDefault(gameLoader, 1);
		this.gameState = gameState;

		user = gameState.getUser();
		initStage();

		popQueManager = new PopQueManager(gameLoader, stage);

		base = new Table(skin);
		base.setFillParent(true);
		// base.pad(25);

		titleBar = TitleBar.create(base, ScreenType.CHALLENGE_CREATION,
				popQueManager, gameState, null, false);

		currentMoney = user.getMoney();

		// Main Buttons
		initSelectionButtons();
		// base.row();
		// initSendChallengeButton();

		// Animations.fadeInAndSlideSide(base);

		stage.addActor(base);

	}

	private void initSendChallengeButton() {
		TextButton createChallenge = new TextButton(
				"set time and send challenge", skin, "yesButton");

		createChallenge.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		base.add(createChallenge).expandX().fillX()
				.height(Globals.baseSize * 4).pad(20);
	}

	private void initSelectionButtons() {
		buttonHolder = new Table(skin);

		Table trackHolder = new Table(skin);

		final JSONTrack currentTrack = JSONTrack.objectify(user.getCurrentTrack());
		
		final ButtonLockWrapper currentTrackButton = AdventureTrackButton
				.create(gameLoader, currentTrack,
						user.getCurrentTrackMode() == TrackMode.INFINTE,
						ScreenType.NONE);

		trackHolder.setBackground("dialogDim-white");
		
		Label selectTrackText = new Label("1. Select Track", skin);
		trackHolder.add(selectTrackText).bottom();
		trackHolder.row();
		
		trackHolder.add(currentTrackButton.button).pad(5)
				.height(Globals.baseSize * 4).width(Globals.baseSize * 4)
				.expandY();
		trackHolder.setTouchable(Touchable.enabled);
		trackHolder.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new GameModeScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		buttonHolder.add(trackHolder).expand().fill().center();

		Image arrow = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/forward.png"));
		// arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
		// arrow.setRotation(180);
		buttonHolder.add(arrow).width(Globals.baseSize)
				.height(Globals.baseSize).pad(20);

		Table carHolder = new Table(skin);
		carHolder.setBackground("dialogDim-white");
		TextureRegion tr = Assembler.assembleCarImage(gameLoader,
				user.getCurrentCar(), false, false);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(Globals.CAR_DISPLAY_BUTTON_WIDTH);
		trd.setMinHeight(Globals.CAR_DISPLAY_BUTTON_HEIGHT);

		ImageButton image = new ImageButton(trd);
		// image.setZIndex(100);
		// b.setPosition(100, 100);
		// image.setSize(100, 100);
		image.setTouchable(Touchable.enabled);
		
		Label selectCarText = new Label("2. Select Car", skin);
		carHolder.add(selectCarText).bottom();
		carHolder.row();

		carHolder.add(image).expandY();
		carHolder.setTouchable(Touchable.enabled);
		carHolder.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new CarModeScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		

		buttonHolder.add(carHolder).expand().fill().center();

		Image arrow2 = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/forward.png"));
		// arrow2.setOrigin(arrow2.getWidth() / 2, arrow2.getHeight() / 2);
		// arrow2.setRotation(180);
		buttonHolder.add(arrow2).width(Globals.baseSize)
				.height(Globals.baseSize).pad(20);

		Table createChallengeHolder = new Table(skin);
		createChallengeHolder.setBackground("dialogDim-white");

		TextButton createChallenge = new TextButton(
				"", skin, "yesButton");
		createChallenge.clear();

		createChallenge.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				if(currentTrack.getItemIndex()==0){
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_USER_NAME_ENTRY,
							"Invalid Track", "Please click on the track to select a correct track", null));
				} else {
					gameLoader.setScreen(new GamePlayScreen(gameState));
				}
				super.clicked(event, x, y);
			}

		});

		Image nextLevelImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/play_black.png"));
		createChallenge.add(nextLevelImage).width(Globals.baseSize)
				.height(Globals.baseSize * 1.2f);//.padLeft(-5).padRight(15);
		
		Label setTimeText = new Label("3. Set Time", skin);
		createChallengeHolder.add(setTimeText).bottom();
		createChallengeHolder.row();

		createChallengeHolder.add(createChallenge).width(Globals.baseSize * 7)
				.height(image.getHeight()).expandY();
		
	

		buttonHolder.add(createChallengeHolder).expand().fill().center();

		base.add(buttonHolder).expand().fill().pad(40);

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

		renderWorld();
		popQueManager.update();
		currentMoney = Animations.money(titleBar.getAnimationMoney(),
				titleBar.getBaseMoney(), user.getMoney(), currentMoney);

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

	@Override
	public boolean successfulTwoButtonFlow(String itemName) {

		return false;
	}

	@Override
	public boolean failedTwoButtonFlow(Integer moneyRequired) {
		popQueManager.push(new PopQueObject(
				PopQueObjectType.ERROR_NOT_ENOUGH_MONEY, "Not Enough Coins",
				"You need " + moneyRequired.toString() + " more coins", this));
		return false;
	}

}
