package Menu;

import wrapper.Globals;
import GroundWorks.GroundBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	private Button exit, restart;
	private TextBox fps, version;
	private ProgressBarW mapProgress;

	private GroundBuilder ground;
	
	private float progress;

	private boolean init = false;

	public HUDBuilder(Stage stage,
			GroundBuilder ground, final GameLoader gameLoader) {

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
				//System.out.println("restarting");
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
		mapProgress = new ProgressBarW(0, 100, 0.01f, false,  "mapProgress");
		mapProgress.setPosition(Globals.ScreenWidth/4, Globals.ScreenHeight - Globals.ScreenHeight/12);
		mapProgress.setSize(Globals.ScreenWidth/2, 0.1f);
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

	public void dispose() {
		
	}



}
