package com.gudesigns.climber;

import java.util.ArrayList;

import throwaway.FullCar;
import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.Globals;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class GamePlayScreen implements Screen, InputProcessor {

	GameLoader gameLoader;
	SpriteBatch batch;
	FullCar fcar;
	AssembledObject builtCar;
	World world;
	CameraManager camera;

	Box2DDebugRenderer debugRenderer;

	GroundBuilder ground;
	float aspectRatio;

	ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();

	public GamePlayScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		Globals.updateScreenInfo();		

		batch = new SpriteBatch();
		initStage();
		initWorld();
		
		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		debugRenderer = new Box2DDebugRenderer();

		Assembler asm = new Assembler();
		builtCar = asm.assembleObject(world);
		//builtCar.setPosition(1, -1.50f);

		ground = new GroundBuilder(world, camera);
		
	}

	@Override
	public void render(float delta) {

		renderWorld();
		attachCameraTo(builtCar.getBasePart().getObject());

		ground.draw(camera);

		handleInput(touches);

		batch.begin();
		builtCar.draw(batch);
		batch.end();

	}

	private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		debugRenderer.render(world, camera.combined);
		world.step(Gdx.graphics.getDeltaTime(), 200, 100);
		
	}

	private void attachCameraTo(BaseActor actor) {

		camera.position.set(actor.getPosition().x + camera.viewportWidth*2.5f , actor.getPosition().y, 1);// + camera.viewportWidth*2.5f
		camera.zoom = 6;
		camera.update();
	}

	private void initWorld() {
		world = (new World(new Vector2(0, -78f), true));
		world.setWarmStarting(true);
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.update();

		//Gdx.input.setInputProcessor(this);
	}
	
	private void initInputs(){
		InputProcessor inputProcessorOne = this;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(inputProcessorOne);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println("touch");

		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;
			touches.get(pointer).touched = true;
			
			
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = 0;
			touches.get(pointer).screenY = 0;
			touches.get(pointer).touched = false;

			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (pointer < Globals.MAX_FINGERS) {
			touches.get(pointer).screenX = screenX;
			touches.get(pointer).screenY = screenY;

			return true;
		}
		return false;
	}

	// ------------------------------------------------------UNUSED------------------------------------------------
	// //

	@Override
	public void show() {
		initInputs();
		Globals.updateScreenInfo();

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

}
