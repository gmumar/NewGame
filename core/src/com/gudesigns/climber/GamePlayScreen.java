package com.gudesigns.climber;

import java.util.ArrayList;

import throwaway.FullCar;
import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.Globals;
import wrapper.JointLimits;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;
import Menu.HUDBuilder;
import Shader.GameMesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GamePlayScreen implements Screen, InputProcessor {

	GameLoader gameLoader;
	SpriteBatch batch;
	FullCar fcar;
	AssembledObject builtCar;
	World world;
	CameraManager camera, secondCamera;
	HUDBuilder hud;
	Stage stage;
	FitViewport vp;
	ShaderProgram shader,colorShader;
	
	boolean paused = true;
	
	public static final float CAMERA_OFFSET =  15;
	//GameMesh mesh;

	//Box2DDebugRenderer debugRenderer;

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
		

		for (int i = 0; i < Globals.MAX_FINGERS; i++) {
			touches.add(new TouchUnit());
		}

		//debugRenderer = new Box2DDebugRenderer();

		Assembler asm = new Assembler();
		builtCar = asm.assembleObject(world);
		builtCar.setPosition(0, 10);

		ground = new GroundBuilder(world, camera);
		
		initShader();
		initHud();

	}

	private void initShader() {
		String vertexShader =  Gdx.files.internal("shaders/vertex.glsl").readString();
		String fragmentShader =  Gdx.files.internal("shaders/fragment.glsl").readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);
		 if (shader.isCompiled() == false) {
	         Gdx.app.log("ShaderError", shader.getLog());
	         System.exit(0);
	      }
		 
		 fragmentShader =  Gdx.files.internal("shaders/colorfragment.glsl").readString();
		 colorShader = new ShaderProgram(vertexShader, fragmentShader);
		 if (shader.isCompiled() == false) {
	         Gdx.app.log("ShaderError", shader.getLog());
	         System.exit(0);
	      }
		 
		 //mesh = new GameMesh();
		// GameMesh.create();	
		 
	}

	private void initHud() {

		hud = new HUDBuilder(stage, secondCamera, ground, gameLoader);

	}

	@Override
	public void render(float delta) {

		handleInput(touches);

		/*
		 * 
		 */

		renderWorld();
		//shader.begin();
		//shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
		//shader.setUniformi("u_texture", 0);
		ground.drawShapes(camera,shader,colorShader);
		//shader.end();
		attachCameraTo(builtCar.getBasePart().getObject());

		batch.begin();
		ground.draw(camera, batch);
		builtCar.draw(batch);
		batch.end();

		hud.update(delta);
		// stage.act(Gdx.graphics.getFramesPerSecond());
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

	}

	private void handleInput(ArrayList<TouchUnit> touchesIn) {
		builtCar.handleInput(touchesIn);

	}

	private void renderWorld() {

		//Gdx.gl.glClearColor((float)118/256, (float)211/256, (float)222/256, 1);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.setProjectionMatrix(camera.combined);

		//debugRenderer.render(world, camera.combined);
		if(!paused){
			world.step(Gdx.graphics.getDeltaTime()/1.1f, 200, 100);
		}

		if (timePassed > 0.2f) {
			paused = false;
		}
		
		
		if (timePassed > 5) {
			// enable joint checking
			//skip_count++;
			//if (skip_count >= SKIP_COUNT) {
			//	skip_count = 0;
				JointLimits.enableJointLimits(world, Gdx.graphics.getDeltaTime()/1.1f);
			//}

		} else {
			timePassed += Gdx.graphics.getDeltaTime()/1.1f;
		}
	}

	private void attachCameraTo(BaseActor actor) {

		camera.position.set(
				actor.getPosition().x + CAMERA_OFFSET,
				actor.getPosition().y, 1);// + camera.viewportWidth*2.5f
		camera.zoom = 4.5f;//4.5f;
		camera.update();
	}

	private void initWorld() {
		world = (new World(new Vector2(0, -38f), true));
		world.setWarmStarting(true);
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 5;
		camera.update();

		secondCamera = new CameraManager(Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		secondCamera.update();

		vp = new FitViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				secondCamera);

		stage = new Stage(vp);
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
		vp.update(width, height);

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
	public void show() {
		initInputs();
		Globals.updateScreenInfo();
		GameMesh.create(camera,shader);
		//

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		ground.destory();
		stage.dispose();
		GameMesh.destroy();
		
	}

	// ------------------------------------------------------UNUSED------------------------------------------------
	// //

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		paused = true;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		paused = false;
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
