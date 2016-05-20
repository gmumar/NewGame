package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameViewport;
import wrapper.Globals;
import Menu.Animations;
import Sounds.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.async.AsyncTask;

public class SplashScreen implements Screen {

	private GameLoader gameLoader;
	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;

	private float time = 0;

	public class SplashActor extends Actor {
		Texture texture = gameLoader.Assets.get("colooorsssxcf.png",
				Texture.class);
		public boolean started = false;

		public SplashActor() {
			setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		}

		@Override
		public void draw(Batch batch, float alpha) {
			batch.setColor(getColor());
			batch.draw(texture, this.getX(), getY(), getWidth(), getHeight());
		}
	}

	public SplashScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();

		SplashActor splashActor = new SplashActor();
		splashActor.setSize(Globals.ScreenWidth * 1 / 6,
				Globals.ScreenWidth * 1 / 6);
		splashActor.setX(Globals.ScreenWidth * 1 / 2 - splashActor.getWidth()/2);
		splashActor.setY(Globals.ScreenHeight * 1 / 2 - 20 - splashActor.getHeight()/2);


		Animations.fadeInAndSlideUp(splashActor);
		
		Globals.globalRunner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				
				Globals.bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/track1.ogg"));
				SoundManager.loopMusic(Globals.bgMusic);
				
				return null;
			}
			
		});

		stage.addActor(splashActor);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
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

		time += delta;
		if (time > 1f) {
			gameLoader.setScreen(new MainMenuScreen(gameLoader));
		}

	}

	private void renderWorld() {

		Gdx.gl20.glClearColor(1, 1, 1, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();

		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());
		
		batch.disableBlending();
		Gdx.gl20.glDisable(GL20.GL_BLEND);

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
