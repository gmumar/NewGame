package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.Globals;
import Menu.Button;
import Menu.GameDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

	GameLoader gameLoader;
	CameraManager camera,secondCamera;
	SpriteBatch batch;
	Stage stage;
	FitViewport vp;

	Button builder, playGame, buildTrack, selectTrack;
	
	GameDialog testDia;

	public MainMenuScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();
		initButtons();
		
		/*Skin skin = new Skin();//Gdx.files.internal("skins/dialogSkin.json")
		BitmapFont font;
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/simpleFont.ttf"));
		font = generator.generateFont(15);
		skin.add("default", font);
				
		testDia = new GameDialog("test", skin,"default");
		testDia.setHeight(10);
		testDia.setWidth(15);
		testDia.show(stage);*/
		
		
	}

	private void initButtons() {

		builder = new Button("builder") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new BuilderScreen(gameLoader));
			}
		};

		builder.setPosition(0, 0);
		stage.addActor(builder);

		playGame = new Button("playGame") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameLoader));
			}
		};

		playGame.setPosition(100, 0);
		stage.addActor(playGame);
		
		buildTrack = new Button("build track") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new TrackBuilderScreen(gameLoader));
			}
		};

		buildTrack.setPosition(200, 0);
		stage.addActor(buildTrack);
		
		selectTrack = new Button("select track"){
			@Override
			public void Clicked() {
				gameLoader.setScreen(new TrackSelectorScreen(gameLoader));
			}
		};

		selectTrack.setPosition(0, 100);
		stage.addActor(selectTrack);
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		camera.update();

		vp = new FitViewport(Globals.ScreenWidth,
				Globals.ScreenHeight, camera);
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

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
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
