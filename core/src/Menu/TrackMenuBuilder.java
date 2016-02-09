package Menu;

import wrapper.CameraManager;
import wrapper.Globals;
import GroundWorks.TrackBuilder;
import JSONifier.JSONCompiler;
import RESTWrapper.BackendFunctions;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class TrackMenuBuilder {

	//private final float BOX_SIZE = 0.0001f;
	//private final float ROTATION_SIZE = 30;

	private Button zoomIn, zoomOut, panLeft, panRight, build, exit, upload;

	private CameraManager camera;
	private JSONCompiler compiler;
	private BackendFunctions backend;

	public TrackMenuBuilder(Stage stage, CameraManager secondCamera,
			final GameLoader gameLoader, final TrackBuilder trackBuilder) {

		//this.stage = stage;
		this.camera = secondCamera;
		this.backend = new BackendFunctions();
		
		compiler = new JSONCompiler();

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
				compiler
				.compile(
						trackBuilder.getMapList());
				gameLoader.setScreen(new GamePlayScreen(gameLoader));
				Destroy();
			}

		};

		build.setPosition(200, 0);
		stage.addActor(build);
		
		upload = new Button("^") {
			@Override
			public void Clicked() {
				compiler
				.compile(
						trackBuilder.getMapList());
				backend.uploadTrack();
			}

		};

		upload.setPosition(Globals.ScreenWidth - 100, Globals.ScreenHeight - 100);
		stage.addActor(upload);

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
		//stage.dispose();
	}
}
