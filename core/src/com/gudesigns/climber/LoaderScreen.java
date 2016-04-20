package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Semaphore;

import wrapper.CameraManager;
import wrapper.Globals;
import JSONifier.JSONCar;
import Menu.FontManager;
import Storage.FileManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class LoaderScreen implements Screen {

	private static final float LOADER_X = Globals.ScreenWidth*1/2;
	private static final float LOADER_Y = Globals.ScreenHeight*1/2;
	private static final float RADIUS = 45;
	//private static final float LOADER_LENGTH = Globals.GameWidth;

	private GameLoader gameLoader;
	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;
	private ShapeRenderer loaderBar;

	private float progress = 0f;
	private AsyncExecutor ae = new AsyncExecutor(2);
	private Semaphore carLoader = new Semaphore(1);

	public LoaderScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();
		loadAssets();

		loaderBar = new ShapeRenderer();

	}

	private void loadAssets() {
		loadLocalCars();

		// Textures
		gameLoader.Assets.load("temp_bar.png", Texture.class);
		gameLoader.Assets.load("solid_joint.png", Texture.class);
		gameLoader.Assets.load("temp_tire_2.png", Texture.class);
		gameLoader.Assets.load("suspension_lower.png", Texture.class);
		gameLoader.Assets.load("suspension_upper.png", Texture.class);
		gameLoader.Assets.load("life_small.png", Texture.class);
		gameLoader.Assets.load("chequered_flag.png", Texture.class);
		gameLoader.Assets.load("button.png", Texture.class);
		gameLoader.Assets.load("temp_ground_filler.png", Texture.class);
		gameLoader.Assets.load("colooorsssxcf.png", Texture.class);
		gameLoader.Assets.load("temp_background1.png", Texture.class);
		gameLoader.Assets.load("temp_background2.png", Texture.class);
		gameLoader.Assets.load("temp_post.png", Texture.class);
		gameLoader.Assets.load("coin.png", Texture.class);

		// // Bars
		gameLoader.Assets.load("bar/level1.png", Texture.class);
		gameLoader.Assets.load("bar/level2.png", Texture.class);
		gameLoader.Assets.load("bar/level3.png", Texture.class);
		gameLoader.Assets.load("bar/level4.png", Texture.class);
		gameLoader.Assets.load("bar/level5.png", Texture.class);

		// Sounds
		gameLoader.Assets.load("music/track1.ogg", Music.class);
		gameLoader.Assets.load("soundfx/coin.mp3", Sound.class);
		gameLoader.Assets.load("soundfx/idle.mp3", Sound.class);

		ObjectMap<String, Object> resources = new ObjectMap<String, Object>();
		BitmapFont font = FontManager.GenerateFont("fonts/simpleFont.ttf", 4,
				Color.BLACK);
		resources.put("default-font", font);
		gameLoader.Assets.load("skins/uiskin.json", Skin.class,
				new SkinParameter("skins/uiskin.atlas", resources));

	}

	public void loadLocalCars() {
		carLoader.tryAcquire();
		
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				Gson gson = new Gson();
				Reader stream = FileManager
						.getFileStream(FileManager.CAR_FILE_NAME);

				if (stream == null) {
					carLoader.release();
					return null;
				}

				JsonReader reader = new JsonReader(stream);
				try {
					reader.beginArray();
					while (reader.hasNext()) {
						while (reader.hasNext()) {
							final JSONCar car = gson.fromJson(reader,
									JSONCar.class);

							gameLoader.cars.add(car);
							break;
						}

					}

					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					carLoader.release();
				}
				return null;
			}
		});

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight,	camera);
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
	
		progress = gameLoader.Assets.getProgress();

		loaderBar.begin(ShapeRenderer.ShapeType.Line);
		loaderBar.setColor(Color.BLACK);
		loaderBar.circle(LOADER_X, LOADER_Y, RADIUS, 100);
		loaderBar.end();

		loaderBar.begin(ShapeRenderer.ShapeType.Filled);
		loaderBar.setColor(Color.GOLD);
		loaderBar.arc(LOADER_X, LOADER_Y, RADIUS, 90, 360*progress,100);
		//loaderBar.rect(LOADER_X, LOADER_Y, LOADER_LENGTH * progress, 100);
		loaderBar.end();

		if (gameLoader.Assets.update() && carLoader.tryAcquire()) {
			gameLoader.setScreen(new SplashScreen(gameLoader));
		}

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		loaderBar.setProjectionMatrix(camera.combined);
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
