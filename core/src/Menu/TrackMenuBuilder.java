package Menu;

import wrapper.CameraManager;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.BuilderScreen;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.MainMenuScreen;

public class TrackMenuBuilder {

	final float BOX_SIZE = 0.0001f;
	final float ROTATION_SIZE = 30;

	Stage stage;

	Button zoomIn, zoomOut, panLeft, panRight, build, exit;

	CameraManager camera;
	GameLoader gameLoader;

	public TrackMenuBuilder(Stage stage, CameraManager secondCamera,
			final GameLoader gameLoader) {

		this.stage = stage;
		this.camera = secondCamera;
		this.gameLoader = gameLoader;

		zoomIn = new Button("+") {
			@Override
			public void Clicked() {
				camera.zoom -= 0.01;
				camera.update();
			}
		};

		zoomIn.setPosition(0, 0);
		stage.addActor(zoomIn);

		zoomOut = new Button("-") {
			@Override
			public void Clicked() {
				camera.zoom += 0.01;
				camera.update();
			}
		};

		zoomOut.setPosition(100, 0);
		stage.addActor(zoomOut);

		build = new Button("build") {
			@Override
			public void Clicked() {
				// compiler.compile(world, parts);
				gameLoader.gameSetScreen(new BuilderScreen(gameLoader));
				Destroy();
			}

		};

		build.setPosition(200, 0);
		stage.addActor(build);

		panLeft = new Button(">") {
			@Override
			public void Clicked() {
				camera.position.x += 5;
				camera.update();
			}

		};

		panLeft.setPosition(0, 100);
		stage.addActor(panLeft);

		panRight = new Button("<") {
			@Override
			public void Clicked() {
				camera.position.x -= 5;
				camera.update();
			}
		};

		panRight.setPosition(0, 200);
		stage.addActor(panRight);

		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(300, 0);
		stage.addActor(exit);

	}

	private void Destroy() {
		// this.world.dispose();
		stage.dispose();
	}
}
