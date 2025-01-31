package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import wrapper.CameraManager;
import wrapper.GameContactListener;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import wrapper.JointLimits;
import wrapper.RollingAverage;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import Assembly.ColliderCategories.ColliderGroups;
import GroundWorks.GroundBuilder;
import JSONifier.JSONCar;
import JSONifier.JSONChallenge;
import JSONifier.JSONComponentName;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.HUDBuilder;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Multiplayer.Challenge;
import Multiplayer.Recorder;
import Multiplayer.Replayer;
import ParallexBackground.ScrollingBackground;
import ParallexBackground.ScrollingBackground.BackgroundType;
import RESTWrapper.BackendFunctions;
import Shader.GameMesh;
import Sounds.SoundManager;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TrackMode;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;
import UserPackage.User.GameMode;
import UserPackage.User.STARS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.JsonObject;
import com.gudesigns.climber.SelectorScreens.TrackSelectorScreen.InfiniteTrackSelectorScreen;

public class GamePlayScreen implements Screen, InputProcessor {

	private GameLoader gameLoader;
	private GameState gameState;
	private SpriteBatch batch;
	private AssembledObject builtCar;
	private World world;
	private CameraManager camera, secondCamera;
	private HUDBuilder hud;
	private Stage stage;
	private GameViewport stageVp, gameVp;
	private ShaderProgram shader, colorShader;

	private RollingAverage rollingAvg;
	private float speedZoom = 0;
	// private float dlTime;

	public static final float CAMERA_OFFSET = 8;
	public static final int OPPONENT_RECORDER_SKIP = 4;

	private GroundBuilder ground;
	private TrackType trackType;

	private Semaphore touchLock = new Semaphore(1);
	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> fakeTouches = new ArrayList<TouchUnit>();

	private float timePassed = 0;

	private ScrollingBackground scrollingBackground;

	private JointLimits jointLimits;

	private final AsyncExecutor runner = new AsyncExecutor(3);

	boolean running = true;
	boolean paused = true;

	private PopQueManager popQueManager;

	private User user;

	private Color clearColor;

	// private Box2DDebugRenderer debugRenderer ;

	// ---- Game Play ----
	private float progress = 0, opponentProgress = 0;
	private float currentTrackLen = 0;
	private boolean gameWon = false, gameLost = false;
	private TouchUnit fakeTouch = new TouchUnit();
	private float gameOverOffset = 0;
	private GameContactListener contactListener;
	private int slowMoFactor = 1;
	private ArrayBlockingQueue<Body> destoryQue = new ArrayBlockingQueue<Body>(
			10);
	volatile private float mapTime = 0.0f;// new BigDecimal(0);
	volatile private int frameCounter = 0;
	// -------------------

	// ---- Sound FX ----
	private Sound coinCollected;

	// ------------------

	private boolean recordPlayer = true;
	private Recorder recorder;

	private boolean replay = true;
	private AssembledObject opponentCar;
	// private Sprite opponentCar;
	private Replayer multiplayerUser;

	public GamePlayScreen(GameState gameState) {
		this.gameState = gameState;
		this.gameLoader = gameState.getGameLoader();
		this.recorder = new Recorder();

		trackType = JSONTrack.objectify(gameState.getUser().getCurrentTrack())
				.getType();

		if (trackType == TrackType.FORREST) {
			clearColor = new Color(Globals.FORREST_GREEN_BG);
		} else if (trackType == TrackType.ARTIC) {
			clearColor = new Color(Globals.ARTIC_BLUE_BG);
		}

		Globals.updateScreenInfo();

		System.gc();
		this.user = gameState.getUser();
		recordPlayer = user.getCurrentGameMode() == GameMode.SET_CHALLENGE;
		replay = user.getCurrentGameMode() == GameMode.PLAY_CHALLENGE;
		// currentMoney = user.getMoney();

		batch = new SpriteBatch();
		initStage();
		world = initWorld();

		contactListener = new GameContactListener(this);

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		initShader();
		ground = new GroundBuilder(new GamePhysicalState(this.world,
				this.gameLoader), camera, shader, colorShader, false,
				gameState.getUser(), replay);

		// debugRenderer = new Box2DDebugRenderer();

		if (replay) {

			String car = null;

			// car = gameState.getUser().getCurrentCar();
			car = gameState.getUser().getCurrentChallengeCar();
			multiplayerUser = new Replayer(gameState.getUser()
					.getCurrentChallengeRecording());

			opponentCar = Assembler.assembleCar(new GamePhysicalState(
					this.world, this.gameLoader), car,
					ColliderGroups.OPPONENT_CAR, false);

			/*
			 * opponentCar = new Sprite(Assembler.assembleCarImage(gameLoader,
			 * user.getCurrentChallenge().getCarJson().jsonify(), false, true));
			 * opponentCar.setSize(11, 11); opponentCar.setOrigin(5.5f, 5.5f);
			 */
			// opponentCar.initSound(this.gameLoader);
			opponentCar.setPosition(10, 50);
			opponentCar.setAlpha(0.5f);

		}

		builtCar = Assembler.assembleCar(new GamePhysicalState(this.world,
				this.gameLoader), gameState.getUser().getCurrentCar(),
				ColliderGroups.USER_CAR, false);
		builtCar.initSound(this.gameLoader);
		builtCar.setPosition(10, 50);
		// builtCar.setRotation(-100f*MathUtils.degreesToRadians);

		System.gc();

		currentTrackLen = ground.getTotalTrackLength();

		rollingAvg = new RollingAverage(60);

		world.step(Globals.STEP, 160, 60);
		frameCounter += 10;

		jointLimits = new JointLimits(world, this);

		scrollingBackground = new ScrollingBackground(this.gameLoader,
				builtCar, trackType, BackgroundType.NORMAL);

		popQueManager = new PopQueManager(gameLoader, stage);
		popQueManager
				.initWinTable(new PopQueObject(PopQueObjectType.WIN, this));
		popQueManager.initPauseTable(new PopQueObject(PopQueObjectType.PAUSE,
				this));
		popQueManager.initKilledTable(new PopQueObject(PopQueObjectType.KILLED,
				this));
		initHud();
		/*
		 * runner.submit(new AsyncTask<String>() {
		 * 
		 * @Override public String call() throws Exception { // float timePassed
		 * = 0; long prevTime = System.nanoTime(); long currentTime = prevTime;
		 * 
		 * while (running) {
		 * 
		 * currentTime = System.nanoTime();
		 * 
		 * Thread.sleep(100);
		 * 
		 * if (currentTime - prevTime >= 100000) { hud.update(Globals.STEP,
		 * progress, mapTime);
		 * 
		 * prevTime = currentTime;
		 * 
		 * } } return null; } });
		 */

		runner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				// float timePassed = 0;
				long prevTime = System.nanoTime();
				long currentTime = prevTime;

				while (running) {

					currentTime = System.nanoTime();

					if (currentTime - prevTime >= 100000) {
						monitorProgress();
						prevTime = currentTime;

					}
				}
				return null;
			}
		});

		fakeTouch.screenX = Globals.ScreenWidth;
		fakeTouch.touched = true;
		fakeTouches.add(fakeTouch);

		world.setContactListener(contactListener);

		// popQueManager.push(new PopQueObject(PopQueObjectType.WIN, this));
	}

	private void initSounds() {
		// coinCollected = gameLoader.Assets.get("soundfx/coin.mp3",
		// Sound.class);
		coinCollected = Gdx.audio.newSound(Gdx.files
				.internal("soundfx/coin.mp3"));
		// bgMusic = gameLoader.Assets.get("music/track1.ogg", Music.class);

		builtCar.startSound();
	}

	final private void initShader() {
		String vertexShader = Gdx.files.internal("shaders/vertex.glsl")
				.readString();
		String fragmentShader = Gdx.files.internal("shaders/fragment.glsl")
				.readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			Gdx.app.log("ShaderError", shader.getLog());
			System.exit(0);
		}

		fragmentShader = Gdx.files.internal("shaders/colorfragment.glsl")
				.readString();
		colorShader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			Gdx.app.log("ShaderError", shader.getLog());
			System.exit(0);
		}
	}

	final private void initHud() {

		hud = new HUDBuilder(stage, gameState, popQueManager, this, replay);
	}

	public void coinCollected(Body body) {
		user.addCoin(100);
		destroyBody(body);
		SoundManager.playFXSound(coinCollected);
	}

	public void destroyBody(Body body) {
		try {
			destoryQue.put(body);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// .addFirst(body);
	}

	private float fixedStep = 0;
	private float difference = 0;

	@Override
	public void render(float delta) {

		// renderWorld(delta);

		// dlTime = delta;

		fixedStep += delta;

		Gdx.gl20.glClearColor(clearColor.r, clearColor.g, clearColor.b,
				clearColor.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);

		batch.setProjectionMatrix(camera.combined);

		if (paused) {
			scrollingBackground.drawStationary();
		} else {

			if ((fixedStep >= Globals.STEP)) {
				fixedStep -= Globals.STEP;
				// if (!ground.loading) {

				if (isGameOver()) {
					worldStep(world, fixedStep);
				} else if (timePassed < 1f) {

				} else {

					if (!world.isLocked()) {
						mapTime += Globals.STEP;
						frameCounter++;

						if (replay) {

							// opponentCar.setAbsPosition(builtCar.getPosition().x,
							// builtCar.getPosition().y);

							builtCar.handleInput(touches);

							opponentCar.handleInput(multiplayerUser
									.getInputTypeWise(frameCounter, difference,
											opponentCar.getPosition().x,
											opponentCar.getPosition().y,
											ground, opponentCar, world));

							worldStep(world, fixedStep);

						} else if (recordPlayer) {

							if (touchLock.tryAcquire()) {

								builtCar.handleInput(touches);

								recorder.addTypeUnit(frameCounter, difference,
										builtCar.getPosition().x,
										builtCar.getPosition().y,
										builtCar.getRotation(), touches);

								touchLock.release();
							}

							worldStep(world, fixedStep);

						} else {

							builtCar.handleInput(touches);
							worldStep(world, fixedStep);

						}
					}

				}

				if (replay) {
					hud.update(progress, opponentProgress, mapTime);
				} else {
					hud.update(progress, mapTime);
				}
				// builtCar.step();
				// }

				if (timePassed > 3) {

					jointLimits.enableJointLimits(Globals.STEP_INVERSE);

				} else {
					// if (!ground.loading) {
					timePassed += Globals.STEP;
					// }
				}

				difference += fixedStep;
				// System.out.println("diff: " + fixedStep);
				// } else {
				// frameCounter++;
				// System.out.println("Delta tooo small");
			}

			scrollingBackground.drawNormal();
			builtCar.updateSound(user);

		}

		attachCameraTo(builtCar.getCameraFocusPart());
		ground.drawShapes();

		batch.begin();

		ground.draw(batch);
		builtCar.draw(batch);

		if (replay) {
			opponentCar.draw(batch);
		}

		batch.end();

		stage.draw();
		stage.act();
		popQueManager.update();

		if (!destoryQue.isEmpty()) {
			JSONComponentName userData = (JSONComponentName) destoryQue.poll()
					.getUserData();

			if (userData == null) {
				;
			} else {
				ground.destroyComponent(userData);
			}
			// world.destroyBody(body);
		}

		// debugRenderer.render(world, camera.combined);

	}

	private void worldStep(World inWorld, float fixedStep2) {

		if (slowMoFactor != 1) {
			inWorld.step(Globals.STEP / slowMoFactor, 60, 40);
		} else {

			inWorld.step(Globals.STEP, 160, 60);

		}
	}

	public void jointsBroken(ArrayList<Integer> jointsBroken) {
		if (recordPlayer) {
			recorder.addJointBreakUnit(frameCounter, difference,
					builtCar.getPosition().x, builtCar.getPosition().y,
					builtCar.getRotation(), jointsBroken);
		}
	}

	private int lastPositionUnit = 50;

	private void monitorProgress() {
		if (!gameWon && contactListener.isKilled()) {
			if (!gameLost) {
				popQueManager.push(new PopQueObject(PopQueObjectType.KILLED,
						this));
				gameLost = true;
				gameEnded();
			}
		}

		if (gameLost) {
			slowMoFactor = slowMoFactor < 4 ? slowMoFactor + 1 : slowMoFactor;
			return;
		}

		progress = (builtCar.getPosition().x / currentTrackLen) * 100;

		if (recordPlayer && progress > 80 && !isGameOver()) {
			if (Math.abs((int) progress - lastPositionUnit) > 2) {
				recorder.addPositionUnit(frameCounter, difference,
						builtCar.getPosition().x, builtCar.getPosition().y,
						builtCar.getRotation());
				lastPositionUnit = (int) progress;
			}
		}

		if (replay) {
			opponentProgress = (opponentCar.getPosition().x / currentTrackLen) * 100;
		}

		if (progress >= 100) {

			builtCar.setMaxVelocity(20);
			gameOverOffset = gameOverOffset < CAMERA_OFFSET + 0.05f ? gameOverOffset + 0.0006f
					: CAMERA_OFFSET + 0.05f;

			if (!gameWon) {

				popQueManager
						.push(new PopQueObject(PopQueObjectType.WIN, this));
				hud.hideMenu();
				gameWon = true;
				gameEnded();

			}
		}

	}

	private void gameEnded() {
		//System.out.println("Game ended");
		//System.out.println("Difference: " + difference);

		if (recordPlayer) {
			/*
			 * recorder.addEndPositionUnit(frameCounter, difference,
			 * builtCar.getPosition().x, builtCar.getPosition().y,
			 * builtCar.getRotation());
			 */

			recorder.addEndTypeUnit(frameCounter, difference,
					builtCar.getPosition().x, builtCar.getPosition().y,
					builtCar.getRotation());
			// user.saveRecording(recorder.jsonify());
		}

	}

	public int calculateWinings() {
		JSONTrack track = JSONTrack.objectify(user.getCurrentTrack());

		float bestTime = track.getBestTime();
		int position = Globals.POSITION_LOST;

		if (mapTime <= bestTime) {
			// first place
			user.setStars(track.getObjectId(), STARS.THREE);
			position = Globals.POSITION_FIRST;
		} else if (mapTime <= bestTime * 1.10) {
			// second place
			user.setStars(track.getObjectId(), STARS.TWO);
			position = Globals.POSITION_SECOND;
		} else if (mapTime <= bestTime * 1.20) {
			// third
			user.setStars(track.getObjectId(), STARS.ONE);
			position = Globals.POSITION_THIRD;
		} else {
			user.setStars(track.getObjectId(), STARS.NONE);
			position = Globals.POSITION_LOST;
		}

		// unlock the next track
		if (position != Globals.POSITION_LOST) {
			int nextIndex = track.getItemIndex() + 1;

			if (track.getType() == TrackType.FORREST) {
				user.Unlock(ItemsLookupPrefix.getForrestPrefix(Integer
						.toString(nextIndex)));
			} else if (track.getType() == TrackType.ARTIC) {
				user.Unlock(ItemsLookupPrefix.getArticPrefix(Integer
						.toString(nextIndex)));
			}
		}
		return position;

	}

	public void carModeSelector() {
		gameLoader.setScreen(new CarModeScreen(gameState));
	}

	public void infiniteTrackSelector() {
		gameLoader.setScreen(new InfiniteTrackSelectorScreen(gameState));
	}

	public void restart() {
		gameLoader.setScreen(new GamePlayScreen(gameState));
	}

	public void nextLevel(JSONTrack playedTrack) {

		int nextLevelindex = playedTrack.getItemIndex() + 1;

		ArrayList<JSONTrack> tracks = null;

		if (playedTrack.getType() == TrackType.ARTIC) {
			tracks = gameLoader.arcticTracks;
		} else if (playedTrack.getType() == TrackType.FORREST) {
			tracks = gameLoader.forrestTracks;
		}

		for (JSONTrack track : tracks) {

			if (track.getType() == playedTrack.getType()
					&& track.getItemIndex() == nextLevelindex) {
				user.setCurrentTrack(track.jsonify(), TrackMode.ADVENTURE,
						false);
				break;
			}
		}
		gameLoader.setScreen(new GamePlayScreen(gameState));
	}

	public void exit() {
		gameLoader.setScreen(new MainMenuScreen(gameLoader));
	}

	private boolean isGameOver() {
		return gameWon || gameLost;
	}

	public void createChallenge() {
		if (user.getCurrentGameMode() == GameMode.SET_CHALLENGE) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.CHALLENGE_FINALIZATION, this));
		}
	}

	private Semaphore challengeSubmitButtonEnabled = new Semaphore(1);
	public boolean submitChallenge(String reward, String targetUser) {
		// !challengeSubmitButtonEnabled
		if(challengeSubmitButtonEnabled.availablePermits()==0){
			return false;
		}
		
		if (user.getCurrentGameMode() == GameMode.SET_CHALLENGE) {
			
			if(reward.isEmpty()){
				popQueManager
				.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"Please enter a reward",
						"Reward must be between 100 and 1,000,000", null));
				
				return false;
			}
			


			if (Integer.parseInt(reward) > 1000000) {
				popQueManager
						.push(new PopQueObject(
								PopQueObjectType.ERROR_USER_NAME_ENTRY,
								"Reward too large",
								"Max reward can be 1,000,000", null));
			} else if (Integer.parseInt(reward) < 100) {
				popQueManager.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"Reward too small",
						"Reward must be greater than 100", null));
			} else if (Integer.parseInt(reward) > user.getMoney()) {
				popQueManager.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"Reward too large",
						"Reward cannot be greater than your coins", null));
			} else if (targetUser.compareTo(user.getLocalUserName()) == 0) {
				popQueManager
						.push(new PopQueObject(
								PopQueObjectType.ERROR_USER_NAME_ENTRY,
								"Cannot challenge yourself",
								"You must create a challenge for a friend, or leave empty for open",
								null));
			} else if (!targetUser.isEmpty() && targetUser.length() < 3 ) {
				popQueManager
						.push(new PopQueObject(
								PopQueObjectType.ERROR_USER_NAME_ENTRY,
								"Target user name too short",
								"Enter valid username or leave blank for open challenge",
								null));
			} else if (!targetUser.isEmpty() && targetUser.length() > 12 ) {
				popQueManager
						.push(new PopQueObject(
								PopQueObjectType.ERROR_USER_NAME_ENTRY,
								"Target user name too long",
								"Enter valid username or leave blank for open challenge",
								null));
			} else if(!targetUser.isEmpty()){
				challengeSubmitButtonEnabled.tryAcquire();
				JSONTrack playedTrack = JSONTrack.objectify(user
						.getCurrentTrack());
				Challenge.submitChallenge(this, recorder.getRecording(),
						JSONCar.objectify(user.getCurrentCar()),
						playedTrack.getObjectId(),
						Integer.toString(playedTrack.getItemIndex()),
						Integer.toString(playedTrack.getDifficulty()),
						user.getCurrentTrackMode(), playedTrack.getType(),
						targetUser, user.getLocalUserName(), mapTime, reward);
				
				return true;
			} else {
				if(targetUser.isEmpty()){
					challengeSubmitButtonEnabled.tryAcquire();
					JSONTrack playedTrack = JSONTrack.objectify(user
							.getCurrentTrack());
					Challenge.submitChallenge(this, recorder.getRecording(),
							JSONCar.objectify(user.getCurrentCar()),
							playedTrack.getObjectId(),
							Integer.toString(playedTrack.getItemIndex()),
							Integer.toString(playedTrack.getDifficulty()),
							user.getCurrentTrackMode(), playedTrack.getType(),
							Globals.OPEN_USER_NAME, user.getLocalUserName(), mapTime, reward);
				}
			}
			return false;
		}
		return false;
	}
	
	public void challengeSubmitted(String response) {
		
		final JsonObject fromServer = Globals.JSONifyResponses(response);
		
		Globals.runOnUIThread(new Runnable() {

			@Override
			public void run() {

				if (fromServer.get("status").getAsInt() == 0) {
					//Submit successful
					popQueManager
					.push(new PopQueObject(
							PopQueObjectType.ERROR_USER_NAME_ENTRY,
							"Challenge created!",
							"Your challenge has been created, check back to see if you won",
							new TwoButtonDialogFlow() {
								
								@Override
								public boolean successfulTwoButtonFlow(String string) {
									gameLoader.setScreen(new ChallengeLobbyScreen(gameState));
									return false;
								}
								
								@Override
								public boolean failedTwoButtonFlow(Integer moneyRequired) {
									// TODO Auto-generated method stub
									return false;
								}
							}));
				} else {
					//submit failed
					//challengeSubmitButtonEnabled = true;
					challengeSubmitButtonEnabled.release();
					popQueManager
					.push(new PopQueObject(
							PopQueObjectType.ERROR_USER_NAME_ENTRY,
							"Target user does not exist",
							"Enter valid username or leave blank for open challenge",
							null));
				}

			}
		});
		
		
	}

	public void challengeCompleted() {
		// This only runs on the target user
		// target user = player -> mapTime
		// source user = who created -> challenge.getbesttime
		if (user.getCurrentGameMode() == GameMode.PLAY_CHALLENGE) {
			user.setCurrentGameMode(GameMode.NORMAL);

			JSONChallenge JChallenge = user.getCurrentJSONChallenge();

			String winner = null;
			// reward user here
			if (mapTime < JChallenge.getBestTime()) {
				// player won
				winner = "TARGET";
				user.addCoin(JChallenge.getReward());
				popQueManager
				.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"Congratulations, you won the challenge",
						"You won " + JChallenge.getReward() + " coins",
						null));
			} else if (mapTime > JChallenge.getBestTime()) {
				// player lost
				winner = "SOURCE";
				user.addCoin(-JChallenge.getReward());
				popQueManager
				.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"You lost the challenge",
						"You lost " + JChallenge.getReward() + " coins",
						null));
			} else {
				// draw
				user.addCoin(JChallenge.getReward() / 2);
				winner = "NONE";
				popQueManager
				.push(new PopQueObject(
						PopQueObjectType.ERROR_USER_NAME_ENTRY,
						"The challenge was a DRAW",
						"You won " + JChallenge.getReward()/2 + " coins",
						null));
			}

			BackendFunctions.updateChallengeWinner(JChallenge.getObjectId(),
					winner);

			user.setCurrentChallenge(null, null);
		}
	}

	final private void attachCameraTo(Body actor) {
		rollingAvg.add(actor.getLinearVelocity().x);
		speedZoom = (float) rollingAvg.getAverage() * 0.035f;
		if (speedZoom < 0) {
			speedZoom = 0;
		}

		camera.position.set(actor.getPosition().x + CAMERA_OFFSET
				- (0.4f - speedZoom * 4) - gameOverOffset,
				actor.getPosition().y + 3, 1);// +
		// camera.viewportWidth*2.5f
		camera.zoom = 3.0f + speedZoom;// 4.5f;

		camera.update();
	}

	final private World initWorld() {
		World inWorld = (new World(new Vector2(0, -38f), true));

		inWorld.setAutoClearForces(true);
		inWorld.setContinuousPhysics(true);
		inWorld.setWarmStarting(true);

		return inWorld;
	}

	final private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 0.5f;
		camera.update();

		secondCamera = new CameraManager(Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.update();

		stageVp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				secondCamera);

		gameVp = new GameViewport(Globals.ScreenWidth / 80,
				Globals.ScreenHeight / 80, camera);

		stage = new Stage(stageVp);

	}

	private void initInputs() {
		InputProcessor inputProcessorOne = this;
		InputProcessor inputProcessorTwo = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(inputProcessorOne);
		inputMultiplexer.addProcessor(inputProcessorTwo);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		stageVp.update(width, height);
		gameVp.update(width, height);

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if (isGameOver())
			return false;
		if (touchLock.tryAcquire()) {

			if (pointer < Globals.MAX_FINGERS) {

				touches.get(pointer).screenX = screenX;
				touches.get(pointer).screenY = screenY;
				touches.get(pointer).touched = true;

			}
			touchLock.release();
			return false;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (isGameOver())
			return false;
		if (touchLock.tryAcquire()) {
			if (pointer < Globals.MAX_FINGERS) {

				touches.get(pointer).touched = false;

			}
			touchLock.release();
			return false;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		/*
		 * if (pointer < Globals.MAX_FINGERS) { touches.get(pointer).screenX =
		 * screenX; touches.get(pointer).screenY = screenY;
		 * 
		 * return true; }
		 */
		return true;
	}

	@Override
	public void show() {
		initInputs();
		initSounds();
		Globals.updateScreenInfo();
		GameMesh.create();
		paused = false;

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		ground.destory();
		stage.dispose();
		builtCar.stopSound();

		GameMesh.destroy();

	}

	@Override
	public void dispose() {
		running = false;
		paused = true;

		Globals.globalRunner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				runner.dispose();
				return null;
			}

		});

		// SoundManager.disposeSound(bgMusic);
		SoundManager.disposeSound(coinCollected);
		popQueManager.dispose();
		world.dispose();
		camera = null;
		hud.dispose();
		shader.dispose();
		colorShader.dispose();
		touches.clear();
		touches = null;
	}

	// ------------------------------------------------------UNUSED------------------------------------------------
	// //

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		hud.hideMenu();
		paused = true;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		hud.showMenu();
		paused = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public float getMapTime() {
		// TODO Auto-generated method stub
		return mapTime;
	}

}
