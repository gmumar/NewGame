package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Semaphore;

import wrapper.CameraManager;
import wrapper.GameViewport;
import wrapper.Globals;
import JSONifier.JSONCar;
import Menu.FontManager;
import Shader.GameMesh;
import Storage.FileManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class LoaderScreen implements Screen {

	public static float LOADER_X = Globals.ScreenWidth * 1 / 2;
	public static float LOADER_Y = Globals.ScreenHeight * 1 / 2;
	public static final float RADIUS = 45;
	// private static final float LOADER_LENGTH = Globals.GameWidth;

	private GameLoader gameLoader;
	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;
	private LoaderBar loaderBar;

	private float progress = 0f;
	private AsyncExecutor ae = new AsyncExecutor(2);
	private Semaphore carLoader = new Semaphore(1);

	private class LoaderBar extends Actor {

		private ShapeRenderer shapeRenderer;

		public LoaderBar() {
			shapeRenderer = new ShapeRenderer();
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.end();
			
			// if (!projectionMatrixSet) {
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			shapeRenderer.translate(getX(),getY(), 0);
			// }

			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.circle(0	, 0, RADIUS, 100);
			shapeRenderer.end();

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.GOLD);
			shapeRenderer.arc(0, 0, RADIUS, 90, 360 * progress,
					100);
			shapeRenderer.end();

			//shapeRenderer.end();
			batch.begin();
		}
	}

	public LoaderScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		loaderBar = new LoaderBar();
		
		initStage();
		loadAssets();

	}

	private void loadAssets() {
		loadLocalCars();
		GameMesh.create();

		// Textures
		gameLoader.Assets.load("temp_bar.png", Texture.class);
		gameLoader.Assets.load("solid_joint.png", Texture.class);
		gameLoader.Assets.load("temp_tire_2.png", Texture.class);
		gameLoader.Assets.load("suspension_lower.png", Texture.class);
		gameLoader.Assets.load("suspension_upper.png", Texture.class);
		gameLoader.Assets.load("life_small.png", Texture.class);
		gameLoader.Assets.load("chequered_flag.png", Texture.class);
		gameLoader.Assets.load("button.png", Texture.class);
		// gameLoader.Assets.load("temp_ground_filler.png", Texture.class);
		gameLoader.Assets.load("colooorsssxcf.png", Texture.class);
		// gameLoader.Assets.load("temp_background1.png", Texture.class);
		// gameLoader.Assets.load("temp_background2.png", Texture.class);
		gameLoader.Assets.load("temp_post.png", Texture.class);
		gameLoader.Assets.load("coin.png", Texture.class);

		// // Worlds
		gameLoader.Assets.load("worlds/forrest/mountains.png", Texture.class);
		gameLoader.Assets.load("worlds/forrest/hills.png", Texture.class);
		gameLoader.Assets.load("worlds/forrest/texture.png", Texture.class);
		gameLoader.Assets.load("worlds/artic/mountains.png", Texture.class);
		gameLoader.Assets.load("worlds/artic/hills.png", Texture.class);
		gameLoader.Assets.load("worlds/artic/texture.png", Texture.class);
		
		gameLoader.Assets.load("worlds/gradient.png", Texture.class);

		gameLoader.Assets.load("worlds/hud/clock.png", Texture.class);

		// // Menus
		gameLoader.Assets.load("menu/icons/car.png", Texture.class);
		gameLoader.Assets.load("menu/icons/home.png", Texture.class);
		gameLoader.Assets.load("menu/icons/pause.png", Texture.class);
		gameLoader.Assets.load("menu/icons/play.png", Texture.class);
		gameLoader.Assets.load("menu/icons/restart_black.png", Texture.class);
		gameLoader.Assets.load("menu/icons/restart_white.png", Texture.class);
		gameLoader.Assets.load("menu/icons/sound_black.png", Texture.class);
		gameLoader.Assets.load("menu/icons/sound_white.png", Texture.class);
		gameLoader.Assets.load("menu/icons/back.png", Texture.class);
		gameLoader.Assets.load("menu/icons/glowing_coin.png", Texture.class);
		gameLoader.Assets.load("menu/icons/upload.png", Texture.class);
		
		gameLoader.Assets.load("menu/icons/up.png", Texture.class);
		gameLoader.Assets.load("menu/icons/down.png", Texture.class);
		gameLoader.Assets.load("menu/icons/left.png", Texture.class);
		gameLoader.Assets.load("menu/icons/right.png", Texture.class);

		gameLoader.Assets.load("menu/images/adventure.png", Texture.class);
		gameLoader.Assets.load("menu/images/infinity.png", Texture.class);
		gameLoader.Assets.load("menu/images/car_builder.png", Texture.class);
		gameLoader.Assets.load("menu/images/car_my_picks.png", Texture.class);
		gameLoader.Assets.load("menu/images/car_community.png", Texture.class);
		gameLoader.Assets.load("menu/images/one_star.png", Texture.class);
		gameLoader.Assets.load("menu/images/two_stars.png", Texture.class);
		gameLoader.Assets.load("menu/images/three_stars.png", Texture.class);

		gameLoader.Assets.load("menu/tags/new.png", Texture.class);
		gameLoader.Assets.load("menu/tags/lock.png", Texture.class);

		gameLoader.Assets.load("menu/icons/builder_bar.png", Texture.class);
		gameLoader.Assets.load("menu/icons/builder_clear.png", Texture.class);
		gameLoader.Assets.load("menu/icons/builder_rotate_left.png",
				Texture.class);
		gameLoader.Assets.load("menu/icons/builder_rotate_right.png",
				Texture.class);
		gameLoader.Assets.load("menu/icons/builder_spring.png", Texture.class);
		gameLoader.Assets.load("menu/icons/builder_wheel.png", Texture.class);

		// // Bars
		gameLoader.Assets.load("bar/builder.png", Texture.class);
		gameLoader.Assets.load("bar/builder_selected.png", Texture.class);
		gameLoader.Assets.load("bar/level1.png", Texture.class);
		gameLoader.Assets.load("bar/level2.png", Texture.class);
		gameLoader.Assets.load("bar/level3.png", Texture.class);
		gameLoader.Assets.load("bar/level4.png", Texture.class);
		gameLoader.Assets.load("bar/level5.png", Texture.class);

		// // Tire
		gameLoader.Assets.load("tire/builder.png", Texture.class);
		gameLoader.Assets.load("tire/builder_selected.png", Texture.class);

		// // Springs
		gameLoader.Assets.load("spring_upper/builder.png", Texture.class);
		gameLoader.Assets.load("spring_upper/builder_selected.png",
				Texture.class);

		gameLoader.Assets.load("spring_lower/builder.png", Texture.class);
		gameLoader.Assets.load("spring_lower/builder_selected.png",
				Texture.class);

		// // Life
		gameLoader.Assets.load("life/builder.png", Texture.class);
		gameLoader.Assets.load("life/builder_selected.png", Texture.class);

		// Sounds
		gameLoader.Assets.load("music/track1.ogg", Music.class);
		gameLoader.Assets.load("soundfx/coin.mp3", Sound.class);
		gameLoader.Assets.load("soundfx/idle.mp3", Sound.class);

		// https://www.google.com/fonts/specimen/Chivo
		ObjectMap<String, Object> resources = new ObjectMap<String, Object>();
		BitmapFont font = FontManager.GenerateFont("fonts/chivoRegular.ttf", 4,
				Color.BLACK);
		resources.put("default-font", font);

		BitmapFont fontWhite = FontManager.GenerateFont(
				"fonts/chivoRegular.ttf", 4, Color.WHITE);
		resources.put("default-font-white", fontWhite);
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

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
		batch = new SpriteBatch();
		stage = new Stage(vp);

		Table t= new Table();
		t.setFillParent(true);
		t.center();
		t.add(loaderBar).center().expand();
		//loaderBar
			//	.setPosition(Globals.ScreenWidth / 2, Globals.ScreenHeight / 2);
		stage.addActor(t);

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

		/*
		 * loaderBar.begin(ShapeRenderer.ShapeType.Line);
		 * loaderBar.setColor(Color.BLACK); loaderBar.circle(LOADER_X, LOADER_Y,
		 * RADIUS, 100); loaderBar.end();
		 * 
		 * loaderBar.begin(ShapeRenderer.ShapeType.Filled);
		 * loaderBar.setColor(Color.GOLD); loaderBar.arc(LOADER_X, LOADER_Y,
		 * RADIUS, 90, 360 * progress, 100); // loaderBar.rect(LOADER_X,
		 * LOADER_Y, LOADER_LENGTH * progress, 100); loaderBar.end();
		 */

		if (gameLoader.Assets.update() && carLoader.tryAcquire()) {
			gameLoader.setScreen(new SplashScreen(gameLoader));
		}

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		// loaderBar.setProjectionMatrix(camera.combined);
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
