package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONChallenge;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Bars.TitleBar;
import Menu.Bars.TitleBarObject;
import Multiplayer.Challenge;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_ParentContainer;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import UserPackage.TrackMode;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;
import UserPackage.User.GameMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;

public class ChallengeLobbyScreen implements Screen, TwoButtonDialogFlow {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;
	private PopQueManager popQueManager;

	private Table challengesTable, challengeItems, base;
	private ScrollPane challengeScroll;

	private Skin skin;
	private GameLoader gameLoader;
	private GameState gameState;

	private ChallengeLobbyScreen context;

	private TitleBarObject titleBar;
	private Integer currentMoney;

	private User user;

	protected AsyncExecutor ae = new AsyncExecutor(2);
	protected HttpRequest downloadRequest;
	protected Semaphore stallSemaphore = new Semaphore(1);
	protected volatile boolean killThreads = false;
	protected Semaphore downloadedCounter = new Semaphore(0);
	public Semaphore loaderSemaphore = new Semaphore(2);
	public Lock uniqueListLock = new ReentrantLock();
	public volatile boolean downloadCancelled = false;
	private ArrayList<JSONChallenge> uniquenessButtonList = new ArrayList<JSONChallenge>();

	Gson json = new Gson();

	public ChallengeLobbyScreen(final GameState gameState) {
		context = this;
		User.getInstance().setCurrentGameMode(GameMode.SET_CHALLENGE);
		gameLoader = gameState.getGameLoader();
		skin = Skins.loadDefault(gameLoader, 1);
		this.gameState = gameState;

		user = gameState.getUser();
		initStage();

		popQueManager = new PopQueManager(gameLoader, stage);

		base = new Table(skin);
		base.setFillParent(true);
		// base.pad(25);

		titleBar = TitleBar.create(base, ScreenType.CHALLENGE_LOBBY,
				popQueManager, gameState, null, false);

		currentMoney = user.getMoney();

		// Main Buttons

		initChallengesTable();

		base.row();

		initCreateChallengeButton();
		// Animations.fadeInAndSlideSide(base);

		stage.addActor(base);

	}

	private void initCreateChallengeButton() {
		TextButton createChallenge = new TextButton("create challenge", skin,
				"yesButton");

		createChallenge.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new ChallengeCreationScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		base.add(createChallenge).expandX().fillX()
				.height(Globals.baseSize * 4).pad(20);
	}

	boolean resultsRemaining;
	int currentOffset;

	private void initChallengesTable() {

		challengesTable = new Table(skin);
		challengesTable.setBackground("dialogDim-white");
		challengeItems = new Table(skin);

		challengeScroll = new ScrollPane(challengeItems);
		challengeScroll.layout();
		// scrollPane.setWidth(100);
		challengeScroll.setScrollingDisabled(true, false);
		challengeScroll.setSmoothScrolling(true);
		challengeScroll.setLayoutEnabled(true);
		challengeScroll.setTouchable(Touchable.enabled);
		challengeScroll.setOverscroll(false, true);
		
		challengesTable.add(challengeScroll).expand().fill();

		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() {

				while (resultsRemaining && !killThreads) {
					// stall = true;

					if (killThreads) {
						loaderSemaphore.release();
						resultsRemaining = false;

						return null;
					}

					while (stallSemaphore.tryAcquire()) {
						downloadRequest = REST.getData(
								getDownloadRequestString(currentOffset,
										"gmumar"), new HttpResponseListener() {

									@Override
									public void handleHttpResponse(
											HttpResponse httpResponse) {

										Backendless_ParentContainer obj = Backendless_JSONParser
												.processDownloadedChallenge(httpResponse
														.getResultAsString());

										for (ServerDataUnit fromServer : obj
												.getData()) {

											System.out.println("got challenge");
											downloadedCounter.release();
											JSONChallenge challenge = new JSONChallenge();
											challenge.setChallenge(fromServer
													.getData());
											challenge.setSourceUser(fromServer
													.getSourceUser());
											challenge.setTargetUser(fromServer
													.getTargetUser());
											challenge.setBestTime(fromServer
													.getTrackBestTime());

											addItemToList(challenge);

										}

										uniqueListLock.lock();
										uniqueListLock.unlock();

										if (obj.getTotalObjects()
												- obj.getOffset() > 0) {
											resultsRemaining = true;
										} else {
											resultsRemaining = false;
										}
										stallSemaphore.release();
										// stall = false;

									}

									@Override
									public void failed(Throwable t) {
										t.printStackTrace();
										loaderSemaphore.release();
										// stallSemaphore.release();
										// stall = false;
										resultsRemaining = false;
										downloadCancelled = true;
										// return;
									}

									@Override
									public void cancelled() {
										loaderSemaphore.release();
										// stallSemaphore.release();
										// stall = false;
										resultsRemaining = false;
										downloadCancelled = true;
										// return;
									}

								});
						currentOffset += REST.PAGE_SIZE;
					}
					// while (stall);

				}
				loaderSemaphore.release();

				return null;
			}

		});

		base.add(challengesTable).expand().fill().pad(20);
	}

	protected void addItemToList(final JSONChallenge string) {

		uniqueListLock.lock();

		uniquenessButtonList.add(string);

		uniqueListLock.unlock();

	}
	

	private String getDownloadRequestString(int offset, String targetUser) {
		return RESTPaths.CHALLENGES + RESTProperties.URL_ARG_SPLITTER
				+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
				+ RESTProperties.PROP_ARG_SPLITTER + RESTProperties.OFFSET
				+ offset + RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.PROPS + RESTProperties.CREATED
				+ RESTProperties.PROP_PROP_SPLITTER + RESTProperties.OBJECT_ID
				+ RESTProperties.PROP_PROP_SPLITTER + RESTProperties.CHALLENGE
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.SOURCE_USER
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TARGET_USER
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TRACK_BEST_TIME
				+ RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.WhereTargetUserIs(targetUser);
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

	private int loadedCount = -1;

	@Override
	public void render(float delta) {

		renderWorld();
		popQueManager.update();
		currentMoney = Animations.money(titleBar.getAnimationMoney(),
				titleBar.getBaseMoney(), user.getMoney(), currentMoney);

		if (uniqueListLock.tryLock()) {

			if (loadedCount != uniquenessButtonList.size()) {
				updateChallengesTable();
				loadedCount = uniquenessButtonList.size();
			}

			uniqueListLock.unlock();
		}

	}

	private void updateChallengesTable() {
		challengeItems.clear();

		if (uniquenessButtonList.isEmpty()) {
			Label noNewChallenges = new Label("No New Challenges", skin,
					"defaultWhite");
			challengeItems.add(noNewChallenges);

			return;
		}

		for (final JSONChallenge challenge : uniquenessButtonList) {

			TextButton createChallenge = new TextButton("challenge from "
					+ challenge.getSourceUser() + " on " + challenge.getBestTime(), skin, "yesButton");

			createChallenge.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));
					user.setCurrentGameMode(GameMode.PLAY_CHALLENGE);
					user.setCurrentChallenge(Challenge.objectify(challenge.getChallenge()), context);
					//
					super.clicked(event, x, y);
				}

			});

			challengeItems.add(createChallenge).expandX().fillX()
					.height(Globals.baseSize * 2).pad(5);
			challengeItems.row();

		}

	}
	
	public void challengeMapLoaded(JSONTrack trackJson, TrackMode trackMode) {
		popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
		user.setCurrentTrack(trackJson.jsonify(), trackMode, true);
		System.out.println("maploaded");
		Globals.runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				//gameLoader.setScreen(new GamePlayScreen(gameState));
				gameLoader.setScreen(new CarModeScreen(gameState));
			}
		});
		

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
		Globals.globalRunner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				ae.dispose();
				return null;
			}

		});
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
