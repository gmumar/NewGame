package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LoaderScreen implements Screen {

	private static final float LOADER_X = 0;
	private static final float LOADER_Y = 0;
	private static final float LOADER_LENGTH = Globals.GameWidth ;
	
	GameLoader gameLoader;
	CameraManager camera, secondCamera;
	SpriteBatch batch;
	Stage stage;
	FitViewport vp;
	ShapeRenderer loaderBar;
	
	float progress = 0f;

	float time=0;

	public LoaderScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();
		loadAssets();

		loaderBar = new ShapeRenderer();

	}
	
	private void loadAssets(){
		//Textures
		Globals.Assets.load("temp_bar.png", Texture.class);
		Globals.Assets.load("solid_joint.png", Texture.class);
		Globals.Assets.load("temp_tire_2.png", Texture.class);
		Globals.Assets.load("suspension_lower.png", Texture.class);
		Globals.Assets.load("suspension_upper.png", Texture.class);
		Globals.Assets.load("life_small.png", Texture.class);
		Globals.Assets.load("chequered_flag.png", Texture.class);
		Globals.Assets.load("button.png", Texture.class);
		Globals.Assets.load("temp_ground_filler.png", Texture.class);
		Globals.Assets.load("colooorsssxcf.png", Texture.class);
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new FitViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
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
		progress = Globals.Assets.getProgress();
		
		loaderBar.begin(ShapeRenderer.ShapeType.Line);
		loaderBar.setColor(Color.BLACK);
		loaderBar.rect(LOADER_X, LOADER_Y, LOADER_LENGTH, 100);
		loaderBar.end();
		
		loaderBar.begin(ShapeRenderer.ShapeType.Filled);
		loaderBar.setColor(Color.GRAY);
		loaderBar.rect(LOADER_X, LOADER_Y, LOADER_LENGTH * progress , 100);
		loaderBar.end();
		
		if(Globals.Assets.update()){
			gameLoader.setScreen(new SplashScreen(gameLoader));
		}
		
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
