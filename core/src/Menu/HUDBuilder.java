package Menu;

import wrapper.GameState;
import wrapper.Globals;
import MenuComponentBuilders.TextBoxBuilder;
import MenuComponentBuilders.TextBoxBuilder.TextBoxStyles;
import User.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	private Button exit, restart;
	private TextBoxBuilder fps, version, money, time;
	private ProgressBarW mapProgress;
	private User user;

	private boolean init = false;
	private GameLoader gameLoader;

	public HUDBuilder(Stage stage, final GameState gameState) {

		this.gameLoader = gameState.getGameLoader();
		this.user = gameState.getUser();

		// Buttons
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
				// System.out.println("restarting");
				gameLoader.setScreen(new GamePlayScreen(gameState));
			}
		};

		restart.setPosition(Globals.GameWidth - 100, Globals.GameHeight - 200);
		stage.addActor(restart);

		// Text Feilds
		fps = new TextBoxBuilder("fps", TextBoxStyles.TEST);
		fps.setPosition(0, Globals.ScreenHeight - 50);
		stage.addActor(fps);

		version = new TextBoxBuilder("Version:" + Globals.VERSION, TextBoxStyles.TEST);
		version.setPosition(0, Globals.ScreenHeight - 25);
		stage.addActor(version);

		money = new TextBoxBuilder("Version:" + Globals.VERSION, TextBoxStyles.TEST);
		money.setPosition(0, Globals.ScreenHeight - 75);
		stage.addActor(money);

		time = new TextBoxBuilder("Version:" + Globals.VERSION, TextBoxStyles.TEST);
		time.setPosition(0, Globals.ScreenHeight - 100);
		stage.addActor(time);

		// Progress Bars
		mapProgress = new ProgressBarW(0, 100, 0.01f, false, "mapProgress");
		mapProgress.setPosition(Globals.ScreenWidth / 4, Globals.ScreenHeight
				- Globals.ScreenHeight / 12);
		mapProgress.setSize(Globals.ScreenWidth / 2, 0.1f);
		mapProgress.setAnimateDuration(0.5f);

		stage.addActor(mapProgress);

	}

	public static String makeTimeStr(float input) {

		int mins = (int) (input / 60);
		int seconds = (int) (input) - mins * 60;
		int milli = (int) (input * 10) - seconds * 10 - mins * 60 * 10;

		return Integer.toString(mins) + ":" + Integer.toString(seconds) + ":"
				+ Integer.toString(milli);

	}

	public void update(float delta, float progress, float timeIn) {
		money.setTextBoxString(user.getMoney());
		time.setTextBoxString(makeTimeStr(timeIn));

		fps.setTextBoxString("fps: " + Gdx.graphics.getFramesPerSecond());
		if (!init) {
			version.setTextBoxString("Version:" + Globals.VERSION);
			init = true;
		}

		mapProgress.setValue(progress > 100 ? 100 : progress);
		mapProgress.act(delta);

	}
	
	public void hideMenu(){
		ParallelAction fadeAndSide = new ParallelAction(Actions.fadeOut(0.8f),Actions.moveBy(110,0, 0.6f));
		ParallelAction fadeAndSide1 = new ParallelAction(Actions.fadeOut(0.8f),Actions.moveBy(110,0, 0.8f));
		
		exit.addAction(fadeAndSide);
		restart.addAction(fadeAndSide1);
		mapProgress.addAction(Actions.fadeOut(0.8f));
	}

	public void dispose() {

	}

}
