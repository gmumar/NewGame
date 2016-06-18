package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

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
import GroundWorks.GroundBuilder;
import JSONifier.JSONComponentName;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.HUDBuilder;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import ParallexBackground.ScrollingBackground;
import ParallexBackground.ScrollingBackground.BackgroundType;
import Shader.GameMesh;
import Sounds.SoundManager;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TrackMode;
import UserPackage.User;
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
import com.gudesigns.climber.SelectorScreens.InfiniteTrackSelectorScreen;

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

	private GroundBuilder ground;
	private TrackType trackType;

	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> fakeTouches = new ArrayList<TouchUnit>();

	private float timePassed = 0;

	private ScrollingBackground scrollingBackground;

	private JointLimits jointLimits;

	private final AsyncExecutor runner = new AsyncExecutor(2);

	boolean running = true;
	boolean paused = true;

	private PopQueManager popQueManager;

	private User user;

	private Color clearColor;

	// private Box2DDebugRenderer debugRenderer ;

	// ---- Game Play ----
	private float progress = 0;
	private float currentTrackLen = 0;
	private boolean gameWon = false, gameLost = false;
	private TouchUnit fakeTouch = new TouchUnit();
	private float gameOverOffset = 0;
	private GameContactListener contactListener;
	private int slowMoFactor = 1;
	private ArrayBlockingQueue<Body> destoryQue = new ArrayBlockingQueue<Body>(
			10);
	private float mapTime = 0;
	// -------------------

	// ---- Sound FX ----
	private Sound coinCollected;

	// ------------------

	public GamePlayScreen(GameState gameState) {
		this.gameState = gameState;
		this.gameLoader = gameState.getGameLoader();

		trackType = JSONTrack.objectify(gameState.getUser().getCurrentTrack())
				.getType();

		if (trackType == TrackType.FORREST) {
			clearColor = new Color(Globals.FORREST_GREEN_BG);
		} else if (trackType == TrackType.ARTIC) {
			clearColor = new Color(Globals.ARTIC_BLUE_BG);
		}

		Globals.updateScreenInfo();

		this.user = gameState.getUser();

		// currentMoney = user.getMoney();

		batch = new SpriteBatch();
		initStage();
		initWorld();

		contactListener = new GameContactListener(this);

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		// debugRenderer = new Box2DDebugRenderer();
		builtCar = Assembler.assembleObject(new GamePhysicalState(this.world,
				this.gameLoader), gameState.getUser().getCurrentCar(), false);
		builtCar.initSound(this.gameLoader);
		builtCar.setPosition(10, 50);

		initShader();
		ground = new GroundBuilder(new GamePhysicalState(this.world,
				this.gameLoader), camera, shader, colorShader, false,
				gameState.getUser());

		currentTrackLen = ground.getTotalTrackLength();

		rollingAvg = new RollingAverage(60);

		world.step(10, 100, 100);

		jointLimits = new JointLimits(world);

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

		hud = new HUDBuilder(stage, gameState, popQueManager, this);
	}

	public void coinCollected(Body body) {
		user.addCoin(10);
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

		if (!paused && fixedStep >= Globals.STEP) {

			// if (!ground.loading) {

			if (gameWon) {
				handleInput(fakeTouches);
			} else if (gameLost) {
				;
			} else {
				handleInput(touches);
				mapTime += Globals.STEP;
			}

			if (slowMoFactor != 1) {
				world.step(Globals.STEP / slowMoFactor, 60, 40);
			} else {
				world.step(Globals.STEP, 200, 200);
			}
			hud.update(Globals.STEP, progress, mapTime, camera);
			// builtCar.step();
			// }

			if (timePassed > 2) {

				jointLimits.enableJointLimits(Globals.STEP_INVERSE);

			} else {
				// if (!ground.loading) {
				timePassed += Globals.STEP;
				// }
			}

			fixedStep -= Globals.STEP;
		}

		builtCar.updateSound();

		scrollingBackground.draw(paused ? BackgroundType.STATIONARY
				: BackgroundType.NORMAL);

		attachCameraTo(builtCar.getCameraFocusPart());
		ground.drawShapes();

		batch.begin();

		ground.draw(batch);
		builtCar.draw(batch);

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

	private void monitorProgress() {
		if (!gameWon && contactListener.isKilled()) {
			if (!gameLost) {
				popQueManager.push(new PopQueObject(PopQueObjectType.KILLED,
						this));
				gameLost = true;
			}
		}

		if (gameLost) {
			slowMoFactor = slowMoFactor < 4 ? slowMoFactor + 1 : slowMoFactor;
			return;
		}

		progress = (builtCar.getPosition().x / currentTrackLen) * 100;

		if (progress >= 100) {
			builtCar.setMaxVelocity(20);
			gameOverOffset = gameOverOffset < CAMERA_OFFSET + 0.05f ? gameOverOffset + 0.0006f
					: CAMERA_OFFSET + 0.05f;

			if (!gameWon) {
				popQueManager
						.push(new PopQueObject(PopQueObjectType.WIN, this));
				hud.hideMenu();
				gameWon = true;

			}
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
			int nextIndex = track.getIndex() + 1;
			user.Unlock(ItemsLookupPrefix.getForrestPrefix(Integer
					.toString(nextIndex)));
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

		int nextLevelindex = playedTrack.getIndex() + 1;

		ArrayList<JSONTrack> tracks = null;

		if (playedTrack.getType() == TrackType.ARTIC) {
			tracks = gameLoader.arcticTracks;
		} else if (playedTrack.getType() == TrackType.FORREST) {
			tracks = gameLoader.forrestTracks;
		}

		for (JSONTrack track : tracks) {

			if (track.getType() == playedTrack.getType()
					&& track.getIndex() == nextLevelindex) {
				user.setCurrentTrack(track.jsonify(), TrackMode.ADVENTURE, false);
				break;
			}
		}
		gameLoader.setScreen(new GamePlayScreen(gameState));
	}

	public void exit() {
		gameLoader.setScreen(new MainMenuScreen(gameLoader));
	}

	private boolean gameOver() {
		return gameWon || gameLost;
	}

	final private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);
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

	final private void initWorld() {
		world = (new World(new Vector2(0, -38f), true));
		// world.setAutoClearForces(true);

		world.setWarmStarting(true);
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
		
		gameVp = new GameViewport(Globals.ScreenWidth/80, Globals.ScreenHeight/80, camera);

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

		if (gameOver())
			return false;

		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;
			touches.get(pointer).touched = true;

			return false;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (gameOver())
			return false;

		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = 0;
			touches.get(pointer).screenY = 0;
			touches.get(pointer).touched = false;

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
		paused = true;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
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
