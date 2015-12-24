package Menu;

import wrapper.CameraManager;
import wrapper.Globals;
import GroundWorks.GroundBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	Stage stage;

	Button exit, restart;
	TextBox fps, version;
	ProgressBarW mapProgress;

	CameraManager camera;
	GameLoader gameLoader;
	GroundBuilder ground;
	
	float progress;

	boolean init = false;

	public HUDBuilder(Stage stage, CameraManager secondCamera,
			GroundBuilder ground, final GameLoader gameLoader) {

		this.stage = stage;
		this.camera = secondCamera;
		this.gameLoader = gameLoader;
		this.ground = ground;

		//Buttons
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

		//Text Feilds
		fps = new TextBox("fps");
		fps.setPosition(0, Globals.ScreenHeight - 50);
		stage.addActor(fps);

		version = new TextBox("Version:" + Globals.VERSION);
		version.setPosition(0, Globals.ScreenHeight - 25);
		stage.addActor(version);
		
		//Progress Bars
		mapProgress = new ProgressBarW(GroundBuilder.BACK_EDGE_UNITS, 100, 0.01f, false,  "mapProgress");
		mapProgress.setPosition(Globals.ScreenWidth/4, Globals.ScreenHeight - Globals.ScreenHeight/12);
		mapProgress.setSize(Globals.ScreenWidth/2, 0.1f);
		mapProgress.setScale(0.1f);
		mapProgress.setAnimateDuration(0.5f);
			
		stage.addActor(mapProgress);
		

	}

	public void update(float delta) {
		fps.setTextBoxString("fps: " + Gdx.graphics.getFramesPerSecond());

		if (!init) {
			version.setTextBoxString("Version:" + Globals.VERSION);
			init = true;
		}
		
		progress = ground.getProgress();
		mapProgress.setValue(progress);
		mapProgress.act(delta);

	}

}
