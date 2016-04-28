package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.Globals;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;
import Menu.Button;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Purchases.GamePurchaseObserver;
import Shader.GameMesh;
import User.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen implements Screen {

	private GameLoader gameLoader;
	private GameState gameState;
	private CameraManager camera, carCamera;
	private Stage stage;
	private StretchViewport vp;
	private PopQueManager popQueManager;
	private User user;
	private MainMenuScreen instance;

	private Button builder, playGame, buildTrack, selectTrack, selectCar,
			quickNext, buyCoins;

	// -------------- Car running animation ------------------
	private World world;
	private ShaderProgram shader, colorShader;
	private AssembledObject builtCar;
	private SpriteBatch batch;
	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private GroundBuilder ground;

	// -------------------------------------------------------

	public MainMenuScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		instance = this;

		initStage();
		initButtons();
		initUser();

		this.gameState = new GameState(gameLoader, user);

		// -------------- Car running animation ------------------
		initWorld();
		initShader();
		builtCar = Assembler.assembleObject(new GamePhysicalState(this.world,
				this.gameLoader), gameState.getUser().getCurrentCar(), false);
		builtCar.setPosition(6, 45);
		builtCar.setMaxVelocity(20);
		batch = new SpriteBatch();
		initCarStage();

		TouchUnit fakeTouch = new TouchUnit();
		fakeTouch.screenX = 5000;
		fakeTouch.touched = true;
		touches.add(fakeTouch);

		ground = new GroundBuilder(new GamePhysicalState(this.world,
				this.gameLoader), carCamera, shader, colorShader, true,
				gameState.getUser());

		world.step(10, 100, 120);
		// -------------------------------------------------------

	}

	// -------------- Car running animation ------------------
	final private void initWorld() {
		world = (new World(new Vector2(0, -38f), true));
		world.setAutoClearForces(true);
		world.setWarmStarting(true);
	}

	final private void attachCameraTo(Body actor) {
		carCamera.position.set(actor.getPosition().x, actor.getPosition().y, 1);
		carCamera.zoom = 4.2f;// 4.5f;
		carCamera.update();
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

	private void initCarStage() {
		carCamera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		carCamera.update();
	}

	private float timeCounter = 0;

	private void carAnimationStep(float delta) {
		batch.setProjectionMatrix(carCamera.combined);

		timeCounter += delta;

		if (timeCounter >= Globals.STEP) {

			world.step(Globals.STEP, 80, 40);

			timeCounter -= Globals.STEP;
		}

		attachCameraTo(builtCar.getCameraFocusPart());
		ground.drawShapes();

		batch.begin();

		ground.drawMainMenu(batch);
		builtCar.draw(batch);

		batch.end();

		driveCar();
	}

	final private void driveCar() {
		builtCar.handleInput(touches);
	}

	// -------------------------------------------------------

	private void initUser() {
		user = User.getInstance();

	}

	private void initButtons() {

		builder = new Button("builder") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new BuilderScreen(gameState));
			}
		};

		builder.setPosition(0, 0);
		stage.addActor(builder);

		quickNext = new Button("Next") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new TrackSelectorScreen(gameState));
			}
		};

		quickNext.setPosition(Globals.ScreenWidth - 150, 25);
		quickNext.setWidth(75);
		quickNext.setHeight(75);
		stage.addActor(quickNext);

		playGame = new Button("playGame") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameState));
			}
		};

		playGame.setPosition(100, 0);
		stage.addActor(playGame);

		if (Globals.ADMIN_MODE) {
			buildTrack = new Button("build track") {
				@Override
				public void Clicked() {
					gameLoader.setScreen(new TrackBuilderScreen(gameState));
				}
			};

			buildTrack.setPosition(200, 0);
			stage.addActor(buildTrack);
		}

		selectTrack = new Button("select track") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new TrackSelectorScreen(gameState));
			}
		};

		selectTrack.setPosition(0, 100);
		stage.addActor(selectTrack);

		selectCar = new Button("select car") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new CarSelectorScreen(gameState));
			}
		};

		selectCar.setPosition(100, 100);
		stage.addActor(selectCar);

		buyCoins = new Button("buy coins") {
			@Override
			public void Clicked() {

				popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

				Globals.runOnUIThread(new Runnable() {

					@Override
					public void run() {
						gameLoader.getPlatformResolver().requestInformation(
								new GamePurchaseObserver() {
									@Override
									public void handleRecievedInformation(
											Purchases.GamePurchaseResult gamePurchaseResult) {
										popQueManager.push(new PopQueObject(
												PopQueObjectType.DELETE));
										
										popQueManager.push(new PopQueObject(
												PopQueObjectType.STORE_BUY,
												instance));
									}
								});
					}
				});

				// gameLoader.getPlatformResolver().requestPurchase("pack_one");
			}
		};

		buyCoins.setPosition(25, Globals.ScreenHeight - 75);
		buyCoins.setHeight(50);
		stage.addActor(buyCoins);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				camera);
		vp.apply();
		// batch = new SpriteBatch();
		stage = new Stage(vp);

		popQueManager = new PopQueManager(gameLoader, stage);

	}

	@Override
	public void resize(int width, int height) {

		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();

		carCamera.viewportWidth = Globals.PixelToMeters(width);
		carCamera.viewportHeight = Globals.PixelToMeters(height);
		carCamera.update();

		vp.update(width, height);
		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		carCamera.position.set(carCamera.viewportWidth / 2,
				carCamera.viewportHeight / 2, 0);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		GameMesh.create(camera, shader);
		Globals.updateScreenInfo();

	}

	@Override
	public void render(float delta) {
		camera.update();
		carCamera.update();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		carAnimationStep(delta);

		stepStage();

		popQueManager.update();

	}

	private void stepStage() {

		// batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

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
		ground.destory();
		stage.dispose();
		GameMesh.destroy();

	}

	@Override
	public void dispose() {
		batch.dispose();
		builtCar.dispose();
		world.dispose();
		camera = null;
		carCamera = null;
		shader.dispose();
		colorShader.dispose();
		touches.clear();
		touches = null;
	}
}
