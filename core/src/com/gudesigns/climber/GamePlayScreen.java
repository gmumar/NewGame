package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GameContactListener;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.Globals;
import wrapper.JointLimits;
import wrapper.RollingAverage;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;
import Menu.HUDBuilder;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import ParallexBackground.ScrollingBackground;
import Shader.GameMesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GamePlayScreen implements Screen, InputProcessor {

	private GameLoader gameLoader;
	private GameState gameState;
	private SpriteBatch batch;
	private AssembledObject builtCar;
	private World world;
	private CameraManager camera, secondCamera;
	private HUDBuilder hud;
	private Stage stage;
	private StretchViewport vp;
	private ShaderProgram shader, colorShader;

	private RollingAverage rollingAvg;
	private float speedZoom = 0;
	private float dlTime;

	public static final float CAMERA_OFFSET = 12;

	private GroundBuilder ground;

	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> fakeTouches = new ArrayList<TouchUnit>();

	private float timePassed = 0;

	private ScrollingBackground scrollingBackground;

	private JointLimits jointLimits;

	private final AsyncExecutor runner = new AsyncExecutor(4);
	boolean running = true;
	boolean paused = true;

	private PopQueManager popQueManager;
	
	//private Box2DDebugRenderer debugRenderer ;

	// ---- Game Play ----
	private float progress = 0;
	private float currentTrackLen = 0;
	private boolean gameWon = false, gameLost = false;
	private TouchUnit fakeTouch = new TouchUnit();
	private float gameOverOffset = 0;
	private GameContactListener contactListener = new GameContactListener();
	private int slowMoFactor = 1;
	// -------------------

	public GamePlayScreen(GameState gameState) {
		this.gameState = gameState;
		this.gameLoader = gameState.getGameLoader();
		Globals.updateScreenInfo();

		batch = new SpriteBatch();
		initStage();
		initWorld();

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		//debugRenderer = new Box2DDebugRenderer();
		builtCar = Assembler.assembleObject(new GamePhysicalState(this.world,
				this.gameLoader), gameState.getUser().getCurrentCar());
		builtCar.setPosition(0, 50);

		initShader();
		ground = new GroundBuilder(new GamePhysicalState(this.world,
				this.gameLoader), camera, shader, colorShader, false);

		currentTrackLen = ground.getTotalTrackLength();
		initHud();

		rollingAvg = new RollingAverage(60);

		world.step(10, 100, 100);

		jointLimits = new JointLimits(world);

		scrollingBackground = new ScrollingBackground(this.gameLoader, builtCar);

		popQueManager = new PopQueManager(stage);

		runner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				// float timePassed = 0;
				long prevTime = System.nanoTime();
				long currentTime = prevTime;

				while (running) {

					currentTime = System.nanoTime();

					if (currentTime - prevTime >= 1 / 60f) {
						hud.update(1 / 60f, progress);

						prevTime = currentTime;

					}
				}
				return null;
			}
		});

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

		fakeTouch.screenX = 5000;
		fakeTouch.touched = true;
		fakeTouches.add(fakeTouch);

		world.setContactListener(contactListener);

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

		hud = new HUDBuilder(stage, gameState);

	}

	@Override
	public void render(float delta) {
		
		renderWorld(delta);

		scrollingBackground.draw();

		attachCameraTo(builtCar.getCameraFocusPart());
		ground.drawShapes();

		batch.begin();

		ground.draw(batch, false);
		builtCar.draw(batch);

		batch.end();

		stage.draw();
		popQueManager.update(delta);
		
		//debugRenderer.render(world, camera.combined);

	}

	private void monitorProgress() {
		if (contactListener.isKilled()) {
			if (!gameLost) {
				popQueManager.push(new PopQueObject(PopQueObjectType.KILLED));
				gameLost = true;
			}
		}

		if (gameLost){
			slowMoFactor = slowMoFactor < 4 ? slowMoFactor + 1 : slowMoFactor;
			return;
		}

		progress = (builtCar.getPosition().x / currentTrackLen) * 100;

		if (progress >= 100) {
			builtCar.setMaxVelocity(20);
			gameOverOffset = gameOverOffset < CAMERA_OFFSET + 0.05f ? gameOverOffset + 0.0006f
					: CAMERA_OFFSET + 0.05f;

			if (!gameWon) {
				popQueManager.push(new PopQueObject(PopQueObjectType.WIN));
				gameWon = true;
			}
		}

	}

	private boolean gameOver() {
		return gameWon || gameLost;
	}

	final private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);
	}

	float fixedStep = 0;

	final private void renderWorld(float delta) {

		dlTime = delta / (1.1f );

		fixedStep += dlTime;

		Gdx.gl20.glClearColor(Globals.SKY_BLUE.r, Globals.SKY_BLUE.g,
				Globals.SKY_BLUE.b, Globals.SKY_BLUE.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);

		batch.setProjectionMatrix(camera.combined);

		if (fixedStep >= 1 / 30f) {
			fixedStep = 1 / 100f;

		} else if (fixedStep >= 1 / 85f) {

			if (gameWon) {
				handleInput(fakeTouches);
			} else if (gameLost) {
				;
			} else {
				handleInput(touches);
			}

			if (!ground.loading) {
				world.step(fixedStep/slowMoFactor, 40, 20);
			}

			if (timePassed > 2) {

				jointLimits.enableJointLimits(1 / fixedStep);

			} else {
				if (!ground.loading) {
					timePassed += dlTime;
				}
			}

			fixedStep = 0;
		}
	}

	final private void attachCameraTo(Body actor) {
		// System.out.println();
		rollingAvg.add(actor.getLinearVelocity().x);
		speedZoom = (float) rollingAvg.getAverage() * 0.025f;
		if (speedZoom < 0) {
			speedZoom = 0;
		}

		camera.position.set(actor.getPosition().x + CAMERA_OFFSET
				- (0.4f - speedZoom * 4) - gameOverOffset,
				actor.getPosition().y, 1);// +
		// camera.viewportWidth*2.5f
		camera.zoom = 4.2f + speedZoom;// 4.5f;

		camera.update();
	}

	final private void initWorld() {
		world = (new World(new Vector2(0, -38f), true));
		world.setAutoClearForces(true);

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

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				secondCamera);

		stage = new Stage(vp);

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
		vp.update(width, height);

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
		Globals.updateScreenInfo();
		GameMesh.create(camera, shader);
		paused = false;

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		ground.destory();
		stage.dispose();
		GameMesh.destroy();

	}

	@Override
	public void dispose() {
		running = false;
		paused = true;

		batch.dispose();
		builtCar.dispose();
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

}
