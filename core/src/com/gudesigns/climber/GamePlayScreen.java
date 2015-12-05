package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.Iterator;

import throwaway.FullCar;
import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.Globals;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import Component.ComponentBuilder.ComponentNames;
import GroundWorks.GroundBuilder;
import Menu.HUDBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GamePlayScreen implements Screen, InputProcessor {

	GameLoader gameLoader;
	SpriteBatch batch;
	FullCar fcar;
	AssembledObject builtCar;
	World world;
	CameraManager camera, secondCamera;
	BitmapFont font12;
	HUDBuilder hud;
	Stage stage;
	

	Box2DDebugRenderer debugRenderer;

	GroundBuilder ground;
	float aspectRatio;

	ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();

	final int SKIP_COUNT = 10;
	int skip_count;

	float timePassed = 0;

	public GamePlayScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		Globals.updateScreenInfo();

		batch = new SpriteBatch();
		initStage();
		initWorld();
		initHud();

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		debugRenderer = new Box2DDebugRenderer();

		Assembler asm = new Assembler();
		builtCar = asm.assembleObject(world);
		// builtCar.setPosition(1, -1.50f);

		ground = new GroundBuilder(world, camera);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/simpleFont.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 10;
		parameter.color = Color.GREEN;
		font12 = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose();

	}

	private void initHud() {
		
		hud = new HUDBuilder( stage, secondCamera, gameLoader);
		
	}

	@Override
	public void render(float delta) {

		handleInput(touches);

		/*
		 * skip_count++; if(skip_count >= SKIP_COUNT){ skip_count = 0; return; }
		 */

		renderWorld();
		attachCameraTo(builtCar.getBasePart().getObject());

		batch.begin();
		ground.draw(camera, batch);
		builtCar.draw(batch);
		font12.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()),
				camera.position.x, camera.position.y);
		batch.end();

		stage.draw();
	}

	private void enableJointLimits(World worldIn, float step) {

		double forceFactor = 1e9;

		Array<Joint> joints = new Array<Joint>();
		worldIn.getJoints(joints);

		Iterator<Joint> iter = joints.iterator();
		while (iter.hasNext()) {
			Joint joint = iter.next();
			float force = joint.getReactionForce(1 / step).len2();
			float torque = joint.getReactionTorque(1 / step);
			
			if(torque > 45000){
				world.destroyJoint(joint);
				System.out.println("Torque break " + joint.getBodyA().getUserData()
						+ " " + joint.getBodyB().getUserData() + " " + torque);
				
				continue;
			}
			
			if (force > 20 * forceFactor) {
				
				if ( ((String) joint.getBodyA().getUserData())
						.contains(ComponentNames.axle.name())
						&& ((String) joint.getBodyB().getUserData())
								.contains(ComponentNames.tire.name())
								
						||
						
						((String) joint.getBodyA().getUserData())
						.contains(ComponentNames.tire.name())
						&& ((String) joint.getBodyB().getUserData())
								.contains(ComponentNames.axle.name())
								
						
						){
					;
				}else{
					world.destroyJoint(joint);
					System.out.println("break " + joint.getBodyA().getUserData()
							+ " " + joint.getBodyB().getUserData() + " " + force);
				}
			}
		}

	}

	private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		debugRenderer.render(world, camera.combined);
		world.step(Gdx.graphics.getDeltaTime(), 250, 125);

		timePassed += Gdx.graphics.getDeltaTime();

		if (timePassed > 10) {
			enableJointLimits(world, Gdx.graphics.getDeltaTime());
		}
	}

	private void attachCameraTo(BaseActor actor) {

		camera.position.set(
				actor.getPosition().x + camera.viewportWidth * 2.5f,
				actor.getPosition().y, 1);// + camera.viewportWidth*2.5f
		camera.zoom = 7;
		camera.update();
	}

	private void initWorld() {
		world = (new World(new Vector2(0, -58f), true));
		world.setWarmStarting(true);
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.update();
		
		secondCamera = new CameraManager(Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.update();

		FitViewport vp = new FitViewport(Globals.ScreenWidth,
				Globals.ScreenHeight, secondCamera);

		stage = new Stage(vp);

		// Gdx.input.setInputProcessor(this);
	}

	private void initInputs() {
		InputProcessor inputProcessorOne = this;
		InputProcessor inputProcessorTwo = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(inputProcessorOne);
		inputMultiplexer.addProcessor(inputProcessorTwo);
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
	public void show() {
		initInputs();
		Globals.updateScreenInfo();

	}
	
	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		ground.destory();
		stage.dispose();

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

}
