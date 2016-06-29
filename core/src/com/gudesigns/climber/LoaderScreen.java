package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import wrapper.CameraManager;
import wrapper.GameViewport;
import wrapper.Globals;
import JSONifier.JSONCar;
import Menu.FontManager;
import Shader.GameMesh;
import Storage.FileManager;
import UserPackage.User;

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
			shapeRenderer.translate(getX(), getY(), 0);
			// }

			/*
			 * shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			 * shapeRenderer.setColor(Color.BLACK); shapeRenderer.circle(0 , 0,
			 * RADIUS, 100); shapeRenderer.end();
			 * 
			 * shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			 * shapeRenderer.setColor(Color.GOLD); shapeRenderer.arc(0, 0,
			 * RADIUS, 90, 360 * progress, 100); shapeRenderer.end();
			 */

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.GRAY);

			/*
			 * shapeRenderer.rect(-Gdx.graphics.getWidth() / 2,
			 * -Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() progress,
			 * Gdx.graphics.getHeight(), Color.WHITE, Color.BLACK, Color.BLACK,
			 * Color.WHITE);
			 */

			shapeRenderer.rect(-Gdx.graphics.getWidth() / 2,
					-Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth()
							* progress, Gdx.graphics.getHeight());
			shapeRenderer.end();

			batch.begin();
		}
	}

	public LoaderScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		loaderBar = new LoaderBar();

		initStage();

		loadAssets();
		
		Globals.setAds(!(User.getInstance().isAdsBought()));// || Globals.ADMIN_MODE));
		//Globals.setAds(false);
	}

	private void loadAssets() {
		checkAllDataFiles();
		loadLocalCars(FileManager.CAR_FILE_NAME);
		loadLocalCars(FileManager.COMMUNITY_FILE_NAME);
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
		gameLoader.Assets.load("big_grid.png", Texture.class);
		gameLoader.Assets.load("loading.png", Texture.class);

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
		gameLoader.Assets.load("menu/icons/cart.png", Texture.class);
		gameLoader.Assets.load("menu/icons/home.png", Texture.class);
		gameLoader.Assets.load("menu/icons/pause.png", Texture.class);
		gameLoader.Assets.load("menu/icons/play.png", Texture.class);
		gameLoader.Assets.load("menu/icons/play_black.png", Texture.class);
		gameLoader.Assets.load("menu/icons/restart_black.png", Texture.class);
		gameLoader.Assets.load("menu/icons/restart_white.png", Texture.class);
		gameLoader.Assets.load("menu/icons/sound_black.png", Texture.class);
		gameLoader.Assets.load("menu/icons/sound_white.png", Texture.class);
		gameLoader.Assets.load("menu/icons/back.png", Texture.class);
		gameLoader.Assets.load("menu/icons/glowing_coin.png", Texture.class);
		gameLoader.Assets.load("menu/icons/upload.png", Texture.class);
		gameLoader.Assets.load("menu/icons/upgrade.png", Texture.class);
		gameLoader.Assets.load("menu/icons/dull_coin.png", Texture.class);
		gameLoader.Assets.load("menu/icons/white_coin.png", Texture.class);
		gameLoader.Assets.load("menu/icons/dull_forward.png", Texture.class);
		gameLoader.Assets.load("menu/icons/mute.png", Texture.class);
		gameLoader.Assets.load("menu/icons/start.png", Texture.class);
		gameLoader.Assets.load("menu/icons/warning.png", Texture.class);
		gameLoader.Assets.load("menu/icons/clock_white.png", Texture.class);
		gameLoader.Assets.load("menu/icons/wrench.png", Texture.class);
		gameLoader.Assets.load("menu/icons/help.png", Texture.class);

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
		gameLoader.Assets.load("menu/images/one_gold_star.png", Texture.class);
		gameLoader.Assets.load("menu/images/two_gold_stars.png", Texture.class);
		gameLoader.Assets.load("menu/images/three_gold_stars.png",
				Texture.class);
		gameLoader.Assets.load("menu/images/no_gold_stars.png", Texture.class);

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
		gameLoader.Assets.load("bar/level6.png", Texture.class);
		gameLoader.Assets.load("bar/level7.png", Texture.class);
		gameLoader.Assets.load("bar/level8.png", Texture.class);
		gameLoader.Assets.load("bar/level9.png", Texture.class);
		gameLoader.Assets.load("bar/level10.png", Texture.class);
		gameLoader.Assets.load("bar/level11.png", Texture.class);
		gameLoader.Assets.load("bar/level12.png", Texture.class);
		gameLoader.Assets.load("bar/level13.png", Texture.class);
		gameLoader.Assets.load("bar/level14.png", Texture.class);
		gameLoader.Assets.load("bar/level15.png", Texture.class);

		// // Tire
		gameLoader.Assets.load("tire/builder.png", Texture.class);
		gameLoader.Assets.load("tire/builder_selected.png", Texture.class);
		gameLoader.Assets.load("tire/level1.png", Texture.class);
		gameLoader.Assets.load("tire/level2.png", Texture.class);
		gameLoader.Assets.load("tire/level3.png", Texture.class);
		gameLoader.Assets.load("tire/level4.png", Texture.class);
		gameLoader.Assets.load("tire/level5.png", Texture.class);
		gameLoader.Assets.load("tire/level6.png", Texture.class);
		gameLoader.Assets.load("tire/level7.png", Texture.class);
		gameLoader.Assets.load("tire/level8.png", Texture.class);
		gameLoader.Assets.load("tire/level9.png", Texture.class);
		gameLoader.Assets.load("tire/level10.png", Texture.class);

		// // Springs
		gameLoader.Assets.load("spring_upper/builder.png", Texture.class);
		gameLoader.Assets.load("spring_upper/builder_selected.png",
				Texture.class);
		gameLoader.Assets.load("spring_upper/level1.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level2.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level3.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level4.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level5.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level6.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level7.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level8.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level9.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level10.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level11.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level12.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level13.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level14.png", Texture.class);
		gameLoader.Assets.load("spring_upper/level15.png", Texture.class);

		gameLoader.Assets.load("spring_lower/builder.png", Texture.class);
		gameLoader.Assets.load("spring_lower/builder_selected.png",
				Texture.class);
		gameLoader.Assets.load("spring_lower/level1.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level2.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level3.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level4.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level5.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level6.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level7.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level8.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level9.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level10.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level11.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level12.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level13.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level14.png", Texture.class);
		gameLoader.Assets.load("spring_lower/level15.png", Texture.class);

		
		gameLoader.Assets.load("spring/level1.png", Texture.class);
		gameLoader.Assets.load("spring/level2.png", Texture.class);
		gameLoader.Assets.load("spring/level3.png", Texture.class);
		gameLoader.Assets.load("spring/level4.png", Texture.class);
		gameLoader.Assets.load("spring/level5.png", Texture.class);
		gameLoader.Assets.load("spring/level6.png", Texture.class);
		gameLoader.Assets.load("spring/level7.png", Texture.class);
		gameLoader.Assets.load("spring/level8.png", Texture.class);
		gameLoader.Assets.load("spring/level9.png", Texture.class);
		gameLoader.Assets.load("spring/level10.png", Texture.class);
		gameLoader.Assets.load("spring/level11.png", Texture.class);
		gameLoader.Assets.load("spring/level12.png", Texture.class);
		gameLoader.Assets.load("spring/level13.png", Texture.class);
		gameLoader.Assets.load("spring/level14.png", Texture.class);
		gameLoader.Assets.load("spring/level15.png", Texture.class);
		
		// // Life
		gameLoader.Assets.load("life/builder.png", Texture.class);
		gameLoader.Assets.load("life/builder_selected.png", Texture.class);

		// Sounds
		gameLoader.Assets.load("music/track1.ogg", Music.class);
		gameLoader.Assets.load("soundfx/coin.mp3", Sound.class);
		gameLoader.Assets.load("soundfx/idle.mp3", Sound.class);

		// https://www.google.com/fonts/specimen/Chivo
		ObjectMap<String, Object> resources = new ObjectMap<String, Object>();
		BitmapFont font = FontManager.GenerateFont(gameLoader,
				"fonts/chivoRegular.ttf", 4, Color.BLACK);
		resources.put("default-font", font);

		BitmapFont fontWhite = FontManager.GenerateFont(gameLoader,
				"fonts/chivoRegular.ttf", 4, Color.WHITE);
		resources.put("white-4", fontWhite);

		BitmapFont fontGlowingSmall = FontManager.GenerateFont(gameLoader,
				"fonts/chivoBlack.ttf", 4, Globals.GLOWING_LIGHT);
		resources.put("glowing-4", fontGlowingSmall);

		BitmapFont fontGlowingLarge = FontManager.GenerateFont(gameLoader,
				"fonts/chivoRegular.ttf", 5, Globals.GLOWING_LIGHT);
		resources.put("glowing-5", fontGlowingLarge);

		BitmapFont fontWhiteLarge = FontManager.GenerateFont(gameLoader,
				"fonts/chivoRegular.ttf", 5, Color.WHITE);
		resources.put("white-5", fontWhiteLarge);

		BitmapFont fontWhiteBold = FontManager.GenerateFont(gameLoader,
				"fonts/chivoBlack.ttf", 4, Color.WHITE);
		resources.put("white-bold-4", fontWhiteBold);

		BitmapFont fontWhiteBoldLarge = FontManager.GenerateFont(gameLoader,
				"fonts/chivoBlack.ttf", 5, Color.WHITE);
		resources.put("white-bold-5", fontWhiteBoldLarge);

		BitmapFont fontBold = FontManager.GenerateFont(gameLoader,
				"fonts/chivoBlack.ttf", 4, Color.BLACK);
		resources.put("default-font-bold", fontBold);

		BitmapFont fontBoldLarge = FontManager.GenerateFont(gameLoader,
				"fonts/chivoBlack.ttf", 5, Color.BLACK);
		resources.put("black-bold-5", fontBoldLarge);

		BitmapFont fontLarge = FontManager.GenerateFont(gameLoader,
				"fonts/chivoRegular.ttf", 5, Color.BLACK);
		resources.put("black-5", fontLarge);

		gameLoader.Assets.load("skins/uiskin.json", Skin.class,
				new SkinParameter("skins/uiskin.atlas", resources));

	}

	private void checkAllDataFiles() {
	
		ArrayList<String> fileList = new ArrayList<String>();
		fileList.add(FileManager.ARTIC_TRACK_FILE_NAME);
		fileList.add(FileManager.CAR_FILE_NAME);
		fileList.add(FileManager.COMMUNITY_FILE_NAME);
		fileList.add(FileManager.FORREST_TRACK_FILE_NAME);
		fileList.add(FileManager.INFINITE_TRACK_FILE_NAME);

		for (String file : fileList) {
			FileManager.validateFileState(file);
		}
	}

	public void loadLocalCars(final String fileName) {
		carLoader.tryAcquire();

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				Gson gson = new Gson();
				Reader stream = FileManager.getFileStream(fileName);

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

							if (fileName.compareTo(FileManager.CAR_FILE_NAME) == 0) {
								gameLoader.autherCars.add(car);
							} else if (fileName
									.compareTo(FileManager.COMMUNITY_FILE_NAME) == 0) {
								gameLoader.communityCars.add(car);
							}
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

		Table t = new Table();
		t.setFillParent(true);
		t.center();
		t.add(loaderBar).center().expand();
		// loaderBar
		// .setPosition(Globals.ScreenWidth / 2, Globals.ScreenHeight / 2);
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

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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
