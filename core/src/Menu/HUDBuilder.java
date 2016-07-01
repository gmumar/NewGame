package Menu;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import UserPackage.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class HUDBuilder {

	private Button exit, restart;
	private TextBox fps;
	private Label clockTime, money, moneyAnimation;
	private ProgressBarW mapProgress;
	private User user;
	private ImageButton pause;

	private GameLoader gameLoader;
	private Integer currentMoney;

	public HUDBuilder(Stage stage, final GameState gameState,
			final PopQueManager popQueManager,
			final GamePlayScreen gamePlayScreen) {

		this.gameLoader = gameState.getGameLoader();
		this.user = gameState.getUser();
		this.currentMoney = user.getMoney();

		Table base = new Table();
		base.setFillParent(true);

		// Buttons
		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(Globals.ScreenWidth - 100, Globals.ScreenHeight - 300);
		// stage.addActor(exit);

		restart = new Button("restart") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameState));
			}
		};

		restart.setPosition(Globals.ScreenWidth - 100,
				Globals.ScreenHeight - 200);
		// stage.addActor(restart);

		// Text Feilds
		fps = new TextBox("fps");
		// fps.setPosition(0, Globals.ScreenHeight - 50);
		stage.addActor(fps);

		// Progress Bars
		mapProgress = new ProgressBarW(0, 100, 0.01f, false, "mapProgress");
		mapProgress.setPosition(0, Globals.ScreenHeight - 5);
		mapProgress.setSize(Globals.ScreenWidth, 0.2f);
		mapProgress.setAnimateDuration(0.5f);

		// stage.addActor(mapProgress);
		base.add(mapProgress).expandX().fillX();

		base.row();

		TextureRegionDrawable trd = new TextureRegionDrawable(
				new TextureRegion(gameLoader.Assets.get("menu/icons/pause.png",
						Texture.class)));

		trd.setMinHeight(Globals.baseSize * 2.3f);
		trd.setMinWidth(Globals.baseSize * 2);

		pause = SimpleImageButton.create(SimpleImageButtonTypes.PAUSE,
				gameLoader);
		pause.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				gamePlayScreen.pause();
				popQueManager.push(new PopQueObject(PopQueObjectType.PAUSE,
						gamePlayScreen));

				super.clicked(event, x, y);
			}

		});

		// pause.setPosition(Globals.ScreenWidth - 60, Globals.ScreenHeight -
		// 65);
		base.add(pause).top().expand().right().width(Globals.baseSize * 2)
				.height(Globals.baseSize * 1.5f * 2).padRight(13);
		// stage.addActor(pause);

		// Timer
		Vector2 timerLocation = new Vector2(20, -55);
		Image clock = new Image(
				gameLoader.Assets.getFilteredTexture("worlds/hud/clock.png"));
		clock.setPosition(timerLocation.x, timerLocation.y
				+ Globals.ScreenHeight);
		clock.setSize(40, 40);
		stage.addActor(clock);

		clockTime = new Label("Time", Skins.loadDefault(gameLoader, 1),
				"dialogTitle");
		clockTime.setPosition(timerLocation.x + 40 + 4, timerLocation.y
				+ Globals.ScreenHeight + 5.5f);
		stage.addActor(clockTime);

		// Money
		Vector2 moneyLocation = new Vector2(20, -100);
		Image coin = new Image(gameLoader.Assets.getFilteredTexture("coin.png"));
		coin.setPosition(moneyLocation.x - 5, moneyLocation.y - 5
				+ Globals.ScreenHeight);
		coin.setSize(50, 50);
		stage.addActor(coin);

		Animations.InitMoneyAnimation();
		moneyAnimation = new Label("Money", Skins.loadDefault(gameLoader, 1),
				"glowing-text");
		moneyAnimation.setPosition(moneyLocation.x + 40 + 4, moneyLocation.y
				+ Globals.ScreenHeight + 6.5f);
		stage.addActor(moneyAnimation);

		money = new Label("Money", Skins.loadDefault(gameLoader, 1),
				"glowing-text");
		money.setPosition(moneyLocation.x + 40 + 4, moneyLocation.y
				+ Globals.ScreenHeight + 6.5f);
		stage.addActor(money);

		stage.addActor(base);
		
		// Arrows 
		
		Image rightArrow = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/left.png"));
		rightArrow.setPosition(stage.getWidth()-100, 60);
		rightArrow.addAction(Actions.alpha(0.5f));
		float ratio = rightArrow.getHeight()/rightArrow.getWidth();
		rightArrow.setSize(Globals.baseSize*1.2f, Globals.baseSize*1.2f*ratio);
		stage.addActor(rightArrow);
		
		Image LeftArrow = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/right.png"));
		LeftArrow.setPosition(100, 60);
		LeftArrow.addAction(Actions.alpha(0.5f));
		LeftArrow.setSize(Globals.baseSize*1.2f, Globals.baseSize*1.2f*ratio);
		stage.addActor(LeftArrow);
		
		
	}

	public void update(float progress, float timeIn) {

		if (Globals.ADMIN_MODE) {

			fps.setTextBoxString("fps: " + Gdx.graphics.getFramesPerSecond());

		}
		clockTime.setText(Globals.makeTimeStr(timeIn));
		// money.setText(user.getMoney().toString());

		currentMoney = Animations.money(moneyAnimation, money, user.getMoney(),
				currentMoney);

		mapProgress.setValue(progress > 100 ? 100 : progress);
		// mapProgress.act(delta);

	}

	public void hideMenu() {
		ParallelAction fadeAndSide = new ParallelAction(Actions.fadeOut(0.8f),
				Actions.moveBy(110, 0, 0.6f));
		ParallelAction fadeAndSide1 = new ParallelAction(Actions.fadeOut(0.8f),
				Actions.moveBy(110, 0, 0.8f));
		ParallelAction fadeAndSide2 = new ParallelAction(Actions.fadeOut(0.4f),
				Actions.moveBy(110, 0, 0.8f));

		exit.addAction(fadeAndSide);
		restart.addAction(fadeAndSide1);
		pause.addAction(fadeAndSide2);

		mapProgress.addAction(Actions.fadeOut(0.8f));
	}
	
	
	public void showMenu() {
		ParallelAction fadeAndSide = new ParallelAction(Actions.alpha(Globals.BUTTON_OPACITY,0.8f),
				Actions.moveBy(-110, 0, 0.6f));
		ParallelAction fadeAndSide1 = new ParallelAction(Actions.alpha(Globals.BUTTON_OPACITY,0.8f),
				Actions.moveBy(-110, 0, 0.8f));
		ParallelAction fadeAndSide2 = new ParallelAction(Actions.alpha(Globals.BUTTON_OPACITY,0.4f),
				Actions.moveBy(-110, 0, 0.8f));

		exit.addAction(fadeAndSide);
		restart.addAction(fadeAndSide1);
		pause.addAction(fadeAndSide2);

		mapProgress.addAction(Actions.fadeIn(0.8f));
	}

	public void dispose() {

	}

}
