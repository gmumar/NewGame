package Menu;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.Globals;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import Assembly.Assembler;
import GroundWorks.GroundBuilder;
import JSONifier.JSONTrack.TrackType;
import ParallexBackground.ScrollingBackground;
import ParallexBackground.ScrollingBackground.BackgroundType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.gudesigns.climber.GameLoader;

public class CarAnimationRunner {

	private ShaderProgram shader, colorShader;
	private SpriteBatch batch;
	private ArrayList<TouchUnit> touches = new ArrayList<TouchUnit>();
	private ScrollingBackground scrollingBackground;
	private GameLoader gameLoader;
	private CameraManager camera;

	// State
	private World world = null;
	private AssembledObject builtCar = null;
	private GroundBuilder ground = null;

	public CarAnimationRunner(GameState gameState) {
		// GameMesh must be created before this is called
		super();

		gameLoader = gameState.getGameLoader();
		initCarStage();
		// this.camera = camera;

		initWorld();
		initShader();

		if (builtCar == null) {
			builtCar = Assembler.assembleObject(new GamePhysicalState(
					this.world, gameLoader), gameState.getUser()
					.getCurrentCar(), false);
			builtCar.setPosition(6, 45);
			builtCar.setMaxVelocity(20);
		}
		batch = new SpriteBatch();

		TouchUnit fakeTouch = new TouchUnit();
		fakeTouch.screenX = 5000;
		fakeTouch.touched = true;
		touches.add(fakeTouch);

		if (ground == null) {
			ground = new GroundBuilder(new GamePhysicalState(this.world,
					this.gameLoader), this.camera, shader, colorShader, true,
					gameState.getUser());
		}

		// ground.reset();

		scrollingBackground = new ScrollingBackground(this.gameLoader,
				builtCar, TrackType.FORREST, BackgroundType.NORMAL);
	}

	final private void initWorld() {
		if (world == null) {
			world = (new World(new Vector2(0, -38f), true));
			world.setAutoClearForces(true);
			world.setWarmStarting(true);
			world.step(10, 100, 120);
		}
	}

	final private void attachCameraTo(Body actor) {
		camera.position
				.set(actor.getPosition().x, actor.getPosition().y + 3, 1);
		camera.zoom = 4.2f;// 4.5f;
		camera.update();
	}

	final private void initShader() {
		String vertexShader = Gdx.files.internal("shaders/vertex.glsl")
				.readString();
		String fragmentShader = Gdx.files.internal("shaders/fragment.glsl")
				.readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			Gdx.app.log("ShaderError", shader.getLog());
			System.exit(0);
		}

		fragmentShader = Gdx.files.internal("shaders/colorfragment.glsl")
				.readString();
		colorShader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			Gdx.app.log("ShaderError", shader.getLog());
			System.exit(0);
		}
	}

	private void initCarStage() {
		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();
	}

	private float timeCounter = 0;

	public void draw(float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		timeCounter += delta;
		scrollingBackground.draw(BackgroundType.NORMAL);
		if (timeCounter >= Globals.STEP) {

			world.step(Globals.STEP, 80, 40);

			timeCounter -= Globals.STEP;
		}

		attachCameraTo(builtCar.getCameraFocusPart());
		ground.drawShapes();

		batch.begin();

		ground.drawMainMenu(batch);
		builtCar.draw(batch);

		batch.end();

		driveCar();
	}

	final private void driveCar() {
		builtCar.handleInput(touches);
	}

	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();

		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
	}

	public void dispose() {
		// ground.destory();
		// batch.dispose();
		// builtCar.dispose();
		// world.dispose();
		camera = null;
		// carCamera = null;
		// shader.dispose();
		// colorShader.dispose();
		touches.clear();
		touches = null;
	}

}
