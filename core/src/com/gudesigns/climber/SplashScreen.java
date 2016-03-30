package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class SplashScreen implements Screen {

	private GameLoader gameLoader;
	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

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
		splashActor.setX(Globals.ScreenWidth * 5 / 12);
		splashActor.setY(Globals.ScreenHeight * 2 / 5 - 20);
		splashActor.setColor(1, 1, 1, 0);;

		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Globals.ScreenWidth * 5 / 12,
				Globals.ScreenHeight * 2 / 5);
		moveAction.setDuration(0.8f);

		ParallelAction pa = new ParallelAction(moveAction,Actions.fadeIn(0.8f));
		splashActor.addAction(pa);
		

		stage.addActor(splashActor);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
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
