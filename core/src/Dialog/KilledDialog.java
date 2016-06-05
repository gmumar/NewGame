package Dialog;

import wrapper.Globals;
import Menu.Animations;
import Menu.PopQueObject;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import User.User;

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
import com.gudesigns.climber.GameLoader;

public class KilledDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	public KilledDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueObject);

		// return base;
	}

	private void buildTable(GameLoader gameLoader,
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

		Label coinsEarned = new Label("100", skin, "defaultWhite");
		coinsWrapper.add(coinsEarned).right().expand();
		contextWrapper.add(coinsWrapper).width(Globals.baseSize * 10).expand().pad(5);
		contextWrapper.row();
		
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
		// JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
		// .getCurrentTrack());
		buildTable(gameLoader, popQueObject);
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		User.getInstance().addCoin(-100);

	}
}
