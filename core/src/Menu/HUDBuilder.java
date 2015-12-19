package Menu;

import wrapper.CameraManager;
import wrapper.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	Stage stage;

	Button exit, restart;
	TextBox fps, version;

	CameraManager camera;
	GameLoader gameLoader;

	boolean init = false;

	public HUDBuilder(Stage stage, CameraManager secondCamera,
			final GameLoader gameLoader) {

		this.stage = stage;
		this.camera = secondCamera;
		this.gameLoader = gameLoader;

		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(Globals.GameWidth - 100, Globals.GameHeight - 100);
		stage.addActor(exit);

		restart = new Button("restart") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameLoader));
			}
		};

		restart.setPosition(Globals.GameWidth - 100, Globals.GameHeight - 200);
		stage.addActor(restart);

		fps = new TextBox("fps");
		fps.setPosition(0, Globals.ScreenHeight - 50);
		stage.addActor(fps);

		version = new TextBox("Version:" + Globals.VERSION);
		version.setPosition(0, Globals.ScreenHeight - 25);
		stage.addActor(version);

	}

	public void update() {
		fps.setTextBoxString("fps: " + Gdx.graphics.getFramesPerSecond());

		if (!init) {
			version.setTextBoxString("Version:" + Globals.VERSION);
			init = true;
		}

	}

}
