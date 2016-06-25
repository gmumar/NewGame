package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import wrapper.TouchUnit;
import Menu.MenuBuilder;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class CarBuilderScreen implements Screen, InputProcessor,
		GestureListener, TwoButtonDialogFlow {

	// private GameLoader gameLoader;
	private SpriteBatch batch;
	private CameraManager camera, secondCamera;
	private World world;
	private Stage stage;
	private Table grid;
	private MenuBuilder menu;
	private GameViewport vp;
	private ShapeRenderer shapeRenderer;

	private Box2DDebugRenderer debugRenderer;
	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private PopQueManager popQueManager;

	private CarBuilderScreen instance;
	private GameLoader gameLoader;

	private float zoom = 0.015f;

	public CarBuilderScreen(GameState gameState) {
		// this.gameLoader = gameLoader;
		Globals.updateScreenInfo();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		gameLoader = gameState.getGameLoader();
		initStage();
		initWorld();

		instance = this;

		popQueManager = new PopQueManager(gameState.getGameLoader(), stage);

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		menu = new MenuBuilder(new GamePhysicalState(world,
				gameState.getGameLoader()), stage, camera, shapeRenderer,
				gameState.getUser(), popQueManager, instance);

		debugRenderer = new Box2DDebugRenderer();

		User user = gameState.getUser();

		if (user.isNew(ItemsLookupPrefix.CAR_BUILDER)) {
			popQueManager.initTutorialTable(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN));
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_INTRO, instance));

			user.setNonNew(ItemsLookupPrefix.CAR_BUILDER, false);
		}

	}

	@Override
	public boolean successfulTwoButtonFlow(String string) {
		if (string.compareTo(PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1
				.toString()) == 0) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1, this));
		} else if (string
				.compareTo(PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2
						.toString()) == 0) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2, this));
		} else if (string
				.compareTo(PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3
						.toString()) == 0) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3, this));
		} else if (string
				.compareTo(PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4
						.toString()) == 0) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4, this));
		} else if (string
				.compareTo(PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5
						.toString()) == 0) {
			popQueManager.push(new PopQueObject(
					PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5, this));
		}

		return false;
	}

	@Override
	public boolean failedTwoButtonFlow(Integer moneyRequired) {
		// TODO Auto-generated method stub
		return false;
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

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				secondCamera);

		stage = new Stage(vp);

		grid = new Table();

		int startX = -300, startY = 820;
		Image oneGrid = null;

		for (int i = 0; i < 4; i++) {
			startY -= 410;
			startX = -300;
			for (int j = 0; j < 5; j++) {
				oneGrid = new Image(
						gameLoader.Assets.getFilteredTexture("big_grid.png"));
				oneGrid.setColor(1, 1, 1, 0.1f);
				oneGrid.setScale(0.4f);
				oneGrid.setPosition(startX, startY);
				startX += 410;
				grid.addActor(oneGrid);
			}
		}

		stage.addActor(grid);

	}

	private void initInputs() {
		InputProcessor inputProcessorOne = this;
		InputProcessor inputProcessorTwo = stage;
		InputProcessor inputProcessorThree = menu;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(inputProcessorOne);
		inputMultiplexer.addProcessor(inputProcessorTwo);
		inputMultiplexer.addProcessor(inputProcessorThree);
		inputMultiplexer.addProcessor(new GestureDetector(this));
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	private void initWorld() {
		world = (new World(new Vector2(0, 0), false));
	}

	@Override
	public void render(float delta) {

		renderWorld();
		batch.begin();
		menu.drawForBuilder(batch, delta);
		batch.end();

		shapeRenderer.begin(ShapeType.Filled);
		menu.drawShapes(batch);
		shapeRenderer.end();
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		popQueManager.update();
	}

	private void renderWorld() {

		Gdx.gl.glClearColor(13 / 256f, 22 / 256f, 46 / 256f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		// debugRenderer.render(world, camera.combined);
		world.step(Gdx.graphics.getDeltaTime(), 100, 100);

		grid.setPosition(-camera.position.x * 72, -camera.position.y * 72);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;
			touches.get(pointer).touched = true;

			return false;
		}

		// menu.handleClick(screenX, screenY);

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = 0;
			touches.get(pointer).screenY = 0;
			touches.get(pointer).touched = false;

			return false;
		}

		// menu.handleRelease(screenX, screenY);

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;

			return false;
		}

		// menu.handleDrag(screenX, screenY);

		return true;
	}

	@Override
	public void resize(int width, int height) {
		Globals.updateScreenInfo();
		vp.update(width, height);

	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if (menu.isJoined())
			return false;

		menu.handlePan(x, y, deltaX, deltaY);
		camera.position.x -= deltaX / ((30 + camera.zoom) / 0.8f);
		camera.position.y += deltaY / ((30 + camera.zoom) / 0.8f);

		if (camera.position.x > 4.5f) {
			camera.position.x = 4.5f;
		}

		if (camera.position.x < -4.5f) {
			camera.position.x = -4.5f;
		}

		if (camera.position.y > 2.5f) {
			camera.position.y = 2.5f;
		}

		if (camera.position.y < -5.5f) {
			camera.position.y = -5.5f;
		}

		camera.update();
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (menu.isJoined())
			return false;

		camera.zoom += (initialDistance - distance) / 500000;
		if (camera.zoom < 0.004f) {
			camera.zoom = 0.004f;
		}
		if (camera.zoom > 0.025f) {
			camera.zoom = 0.025f;
		}

		camera.update();

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

	}

	@Override
	public void dispose() {
		batch.dispose();
		camera = null;
		secondCamera = null;
		// these two self destroyed by menu
		// world.dispose();
		// stage.dispose();
		shapeRenderer.dispose();
		debugRenderer.dispose();
		touches.clear();
		touches = null;
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
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pinchStop() {
		// TODO Auto-generated method stub

	}

}
