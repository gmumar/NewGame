package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import wrapper.JointLimits;
import wrapper.Rolling;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;
import Menu.HUDBuilder;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GamePlayScreen implements Screen, InputProcessor {

	private GameLoader gameLoader;
	private SpriteBatch batch;
	private AssembledObject builtCar;
	private World world;
	private CameraManager camera, secondCamera;
	private HUDBuilder hud;
	private Stage stage;
	private FitViewport vp;
	private ShaderProgram shader, colorShader;

	private Rolling rollingAvg;
	private float speedZoom = 0;
	private float dlTime;

	public static final float CAMERA_OFFSET = 12;

	private GroundBuilder ground;

	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();

	private float timePassed = 0;
	
	private ScrollingBackground scrollingBackground;
	
	//private JointLimits jointLimits;

	private AsyncExecutor taskRunner;
	private boolean submit = true, running = true;
	private volatile boolean collisionWait = false;
	private AsyncTask<String> collisionTask;

	public GamePlayScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		Globals.updateScreenInfo();

		batch = new SpriteBatch();
		initStage();
		initWorld();

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		// debugRenderer = new Box2DDebugRenderer();
		builtCar = Assembler.assembleObject(new GameState(world, gameLoader));
		builtCar.setPosition(0, 50);

		initShader();
		ground = new GroundBuilder(new GameState(world, gameLoader), camera,
				shader, colorShader);

		initHud();

		rollingAvg = new Rolling(60);

		world.step(10, 100, 100);
		
		//jointLimits = new JointLimits(world);

		taskRunner = new AsyncExecutor(1);
		collisionTask = new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				while (running) {
					//jointLimits.enableJointLimits(1/dlTime);
					collisionWait = true;
					while (collisionWait)
						;

				}
				return null;
			}
		};
		
		scrollingBackground = new ScrollingBackground(new GameState(world, gameLoader), builtCar);
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

		hud = new HUDBuilder(stage, ground, gameLoader);

	}

	@Override
	public void render(float delta) {
		handleInput(touches);
		renderWorld();
		
		//batch.begin();

		scrollingBackground.draw(batch,delta);
		//batch.end();
		
		
		ground.drawShapes(camera);
		attachCameraTo(builtCar.getBasePart());

		batch.begin();

		ground.draw(camera, batch);
		builtCar.draw(batch);

		batch.end();
		
		

		hud.update(delta);
		stage.draw();

	}

	final private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);

	}

	final private void renderWorld() {

		dlTime = Gdx.graphics.getDeltaTime() / 1.1f;

		Gdx.gl20.glClearColor(Globals.SKY_BLUE.r, Globals.SKY_BLUE.g,
				Globals.SKY_BLUE.b, Globals.SKY_BLUE.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);

		batch.setProjectionMatrix(camera.combined);

		world.step(dlTime, 40, 40);

		if (timePassed > 2) {

			/*if (submit) {
				taskRunner.submit(collisionTask);
				submit = false;
			}*/
			
			//jointLimits.enableJointLimits(1/dlTime);

		} else {
			timePassed += dlTime;
		}

		collisionWait = false;

	}

	final private void attachCameraTo(Body actor) {
		// System.out.println();
		rollingAvg.add(actor.getLinearVelocity().x);
		speedZoom = (float) rollingAvg.getAverage() * 0.025f;
		if (speedZoom < 0) {
			speedZoom = 0;
		}
		camera.position.set(actor.getPosition().x + CAMERA_OFFSET
				- (0.4f - speedZoom * 4), actor.getPosition().y, 1);// +
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

		vp = new FitViewport(Globals.ScreenWidth, Globals.ScreenHeight,
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
		//

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
		// paused = true;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		// paused = false;
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
