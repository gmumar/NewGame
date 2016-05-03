package Menu;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import Menu.PopQueObject.PopQueObjectType;
import User.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	private Button exit, restart;
	private TextBox fps, version, money, time;
	private ProgressBarW mapProgress;
	private User user;
	private ImageButton pause;

	private boolean init = false;
	private GameLoader gameLoader;

	public HUDBuilder(Stage stage, final GameState gameState,
			final PopQueManager popQueManager,
			final GamePlayScreen gamePlayScreen) {

		this.gameLoader = gameState.getGameLoader();
		this.user = gameState.getUser();

		if (Globals.ADMIN_MODE) {
			// Buttons
			exit = new Button("exit") {
				@Override
				public void Clicked() {
					gameLoader.setScreen(new MainMenuScreen(gameLoader));
				}
			};

			exit.setPosition(Globals.GameWidth - 100, Globals.GameHeight - 300);
			//stage.addActor(exit);

			restart = new Button("restart") {
				@Override
				public void Clicked() {
					// System.out.println("restarting");
					gameLoader.setScreen(new GamePlayScreen(gameState));
				}
			};

			restart.setPosition(Globals.GameWidth - 100,
					Globals.GameHeight - 200);
			//stage.addActor(restart);

			// Text Feilds
			fps = new TextBox("fps");
			fps.setPosition(0, Globals.ScreenHeight - 50);
			stage.addActor(fps);

			version = new TextBox("Version:" + Globals.VERSION);
			version.setPosition(0, Globals.ScreenHeight - 25);
			stage.addActor(version);

			money = new TextBox("Version:" + Globals.VERSION);
			money.setPosition(0, Globals.ScreenHeight - 75);
			stage.addActor(money);

			time = new TextBox("Version:" + Globals.VERSION);
			time.setPosition(0, Globals.ScreenHeight - 100);
			stage.addActor(time);
		}

		TextureRegionDrawable trd = new TextureRegionDrawable(
				new TextureRegion(gameLoader.Assets.get("menu/icons/pause.png",
						Texture.class)));
		
		trd.setMinHeight(45);
		trd.setMinWidth(45);

		pause = new ImageButton(trd);
		pause.pad(8.5f);
		pause.addAction(Actions.alpha(0.3f));

		pause.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				gamePlayScreen.pause();
				
				pause.addAction(new SequenceAction(Actions.alpha(0.8f, 0.2f),
						Actions.alpha(0.3f, 0.4f)));
				popQueManager.push(new PopQueObject(PopQueObjectType.PAUSE, gamePlayScreen));
				super.clicked(event, x, y);
			}

		});

		pause.setPosition(Globals.GameWidth - 60, Globals.GameHeight - 65);
		stage.addActor(pause);

		// Progress Bars
		mapProgress = new ProgressBarW(0, 100, 0.01f, false, "mapProgress");
		mapProgress.setPosition(0, Globals.ScreenHeight - 5);
		mapProgress.setSize(Globals.ScreenWidth, 0.2f);
		mapProgress.setAnimateDuration(0.5f);

		stage.addActor(mapProgress);

	}

	public void update(float delta, float progress, float timeIn,
			CameraManager camera) {

		if (Globals.ADMIN_MODE) {
			time.setTextBoxString(Globals.makeTimeStr(timeIn));

			fps.setTextBoxString("fps: " + Gdx.graphics.getFramesPerSecond());
			if (!init) {
				version.setTextBoxString("Version:" + Globals.VERSION);
				init = true;
			}
		}
		money.setTextBoxString(user.getMoney());
		mapProgress.setValue(progress > 100 ? 100 : progress);
		// mapProgress.act(delta);

	}

	public void hideMenu() {
		ParallelAction fadeAndSide = new ParallelAction(Actions.fadeOut(0.8f),
				Actions.moveBy(110, 0, 0.6f));
		ParallelAction fadeAndSide1 = new ParallelAction(Actions.fadeOut(0.8f),
				Actions.moveBy(110, 0, 0.8f));
		ParallelAction fadeAndSide2 = new ParallelAction(Actions.fadeOut(0.8f),
				Actions.moveBy(110, 0, 0.8f));

		exit.addAction(fadeAndSide);
		restart.addAction(fadeAndSide1);
		pause.addAction(fadeAndSide2);
		
		mapProgress.addAction(Actions.fadeOut(0.8f));
	}

	public void dispose() {

	}

}
