package Dialog;

import wrapper.GameState;
import wrapper.Globals;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueObject;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import UserPackage.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.CarBuilderScreen;
import com.gudesigns.climber.GameLoader;

public class KilledDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;
	Label coinsEarned;

	public KilledDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueObject);

		// return base;
	}

	private void buildTable(final GameLoader gameLoader,
			final PopQueObject popQueObject) {
		// Skin skin = Skins.loadDefault(gameLoader, 0);

		base = new Table(skin);
		// base.debugAll();
		base.setBackground("dialogDim");
		base.setFillParent(true);
		Animations.fadeIn(base);

		Table contextWrapper = new Table();

		Button restart = new Button(skin, "carBuilder_play");
		restart.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Label restartText = new Label("restart", skin);
		restart.add(restartText).pad(20);

		Image restartImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/restart_black.png"));
		restart.add(restartImage).width(Globals.baseSize)
				.height(Globals.baseSize * 1.2f).pad(10);

		ImageButton home = SimpleImageButton.create(
				SimpleImageButtonTypes.HOME, gameLoader);
		home.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		ImageButton car = SimpleImageButton.create(SimpleImageButtonTypes.CAR,
				gameLoader);
		car.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().carModeSelector();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Table textWrapper = new Table(skin);
		textWrapper.setTouchable(Touchable.enabled);

		Label text = new Label("Killed!", skin, "winTitle");
		text.setAlignment(Align.center);

		textWrapper.add(text);

		contextWrapper.add(textWrapper).fill().expand().center();

		contextWrapper.row();

		// Coins Earned
		Table coinsWrapper = new Table();
		Label coinsEarnedText = new Label("Coins Lost", skin, "defaultWhite");
		Image coinImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/white_coin.png"));

		coinsWrapper.add(coinsEarnedText).left().width(Globals.baseSize * 5);
		coinsWrapper.add(coinImage).width(Globals.baseSize)
				.height(Globals.baseSize).left().expand();

		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());
		coinsEarned = new Label(Integer.toString(-250
				* (playedTrack.getItemIndex())
				/ (playedTrack.getDifficulty() == 0 ? 1 : playedTrack
						.getDifficulty())), skin, "defaultWhite");
		coinsWrapper.add(coinsEarned).right().expand();
		contextWrapper.add(coinsWrapper).width(Globals.baseSize * 10).expand()
				.pad(5);
		contextWrapper.row();
		
		// Promotion
		Table promotionWrapper = new Table();
		Label promotionText = new Label("Having trouble winning this level?\n Try leveling up your car ", skin, "defaultWhite");
		promotionText.setAlignment(Align.center);
		promotionWrapper.add(promotionText);
		
		promotionWrapper.row();
		
		Button carBuilder = new Button(skin, "carBuilder_play");
		carBuilder.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new CarBuilderScreen(new GameState(gameLoader, User.getInstance())));
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Label carBuilderText = new Label("car builder", skin);
		carBuilder.add(carBuilderText).pad(20);

		Image carBuilderImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/car.png"));
		carBuilder.add(carBuilderImage).width(Globals.baseSize*1.5f)
				.height(Globals.baseSize).pad(10);
		
		promotionWrapper.add(carBuilder).pad(10);
		
		contextWrapper.add(promotionWrapper).padTop(30);
		base.add(contextWrapper).expandY().center();
		base.row();

		Table bottomBar = new Table(skin);

		bottomBar.setBackground("blackOut");

		Table bottomWrapper = new Table(skin);

		bottomWrapper.add(home).width(90).fillY().expandY();
		bottomWrapper.add(car).width(90).fillY().expandY();

		bottomBar.add(bottomWrapper).expand().left().fillY();

		bottomBar.add(restart).right().fillY().expand();
		// bottomBar.moveBy(0, -100);

		// Animations.fadeInFromBottom(bottomBar, 50);

		base.add(bottomBar).height(80).expandX().fillX().bottom();
	}

	public Table getBase() {
		return base;
	}

	public void update(GameLoader gameLoader, PopQueObject popQueObject) {
		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());
		buildTable(gameLoader, popQueObject);
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		User.getInstance().addCoin(
				-250
						* (playedTrack.getItemIndex())
						/ (playedTrack.getDifficulty() == 0 ? 1 : playedTrack
								.getDifficulty()));

	}
}
