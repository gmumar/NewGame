package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import wrapper.TouchUnit;
import GroundWorks.TrackBuilder;
import Menu.TrackMenuBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class TrackBuilderScreen implements Screen, InputProcessor, GestureListener {

	private SpriteBatch batch;
	private CameraManager camera, secondCamera;
	private World world;
	private Stage stage;
	private StretchViewport vp;
	
	private Box2DDebugRenderer debugRenderer;
	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	
	private TrackBuilder trackBuilder;

	private float zoom = 0.1f;

	public TrackBuilderScreen(GameState gameState) {
		Globals.updateScreenInfo();
		batch = new SpriteBatch();
		initStage();
		initWorld();

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		trackBuilder = new TrackBuilder(world, camera);
		new TrackMenuBuilder( stage, camera, gameState, trackBuilder);
		debugRenderer = new Box2DDebugRenderer();

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		
		camera.zoom = zoom;
		camera.update();

		secondCamera = new CameraManager(Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.update();

		vp = new StretchViewport(Globals.ScreenWidth,
				Globals.ScreenHeight, secondCamera);

		stage = new Stage(vp);

	}

	private void initInputs() {
		InputProcessor inputProcessorOne = this;
		InputProcessor inputProcessorTwo = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(inputProcessorOne);
		inputMultiplexer.addProcessor(inputProcessorTwo);
		inputMultiplexer.addProcessor(new GestureDetector(this));
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	private void initWorld() {
		world = (new World(new Vector2(0, 0), false));
	}

	@Override
	public void render(float delta) {
		renderWorld();
		trackBuilder.handleTouches(touches);
		
		batch.begin();
		
		trackBuilder.draw(batch);
		
		batch.end();
		
	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		
		debugRenderer.render(world, camera.combined);
		world.step(Gdx.graphics.getDeltaTime(), 100, 100);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
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

		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;

			return false;
		}
		return false;
	}

	@Override
	public void resize(int width, int height) {
		Globals.updateScreenInfo();
		vp.update(width, height);

	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		//camera.position.x -= deltaX/(40+camera.zoom);
		//camera.position.y += deltaY/(40+camera.zoom);
		//camera.update();
		return true;
	}

	@Override
	public void show() {
		initInputs();
		Globals.updateScreenInfo();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		destroy();
	}
	
	public void destroy(){
		stage.dispose();
		trackBuilder.destroy();
	}

	// ------------------------------------------------------UNUSED------------------------------------------------
	// //

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

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

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
