package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import Menu.Button;
import Menu.PopQueManager;
import User.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

	private GameLoader gameLoader;
	private GameState gameState;
	private CameraManager camera;
	private Stage stage;
	private FitViewport vp;
	private PopQueManager popQueManager;
	private User user;
	
	private Button builder, playGame, buildTrack, selectTrack, selectCar;

	public MainMenuScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();
		initButtons();
		initUser();
		
		this.gameState = new GameState(gameLoader , user);
		
	}

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

		playGame = new Button("playGame") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameState));
			}
		};

		playGame.setPosition(100, 0);
		stage.addActor(playGame);

		buildTrack = new Button("build track") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new TrackBuilderScreen(gameState));
			}
		};

		buildTrack.setPosition(200, 0);
		stage.addActor(buildTrack);

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
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new FitViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
		//batch = new SpriteBatch();
		stage = new Stage(vp);
		
		popQueManager = new PopQueManager(stage);

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

		popQueManager.update(delta);
		
	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//batch.setProjectionMatrix(camera.combined);
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

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
