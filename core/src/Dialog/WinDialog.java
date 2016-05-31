package Dialog;

import wrapper.Globals;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueObject;
import Menu.TextBox;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import RESTWrapper.BackendFunctions;
import RESTWrapper.RESTPaths;
import User.User;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class WinDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	Image stars;
	Label coinsEarned;

	public WinDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueObject);

		// return base;
	}

	private void buildTable(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		final float wrapperWidths = Globals.baseSize * 10;
		final float textWidth = Globals.baseSize * 5;

		// Skin skin = Skins.loadDefault(gameLoader, 0);
		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());

		base = new Table(skin);
		// base.debugAll();
		base.setBackground("dialogDim");
		base.setFillParent(true);
		Animations.fadeIn(base);

		Table contentWrapper = new Table(skin);
		Table header = new Table(skin);
		Table timeWrapper = new Table(skin);
		Table coinsWrapper = new Table(skin);
		Table performanceWrapper = new Table(skin);

		// Header
		Label text = new Label("Level Complete!", skin, "winTitle");
		text.setAlignment(Align.center);
		// text.setTextBoxString("Win!");
		header.add(text).expandY().left().padBottom(4);
		contentWrapper.add(header);
		contentWrapper.row();

		// Time Wrapper
		Label timeRemainingText = new Label("Level Time", skin, "defaultWhite");
		Image clockImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/clock_white.png"));

		timeWrapper.add(timeRemainingText).left().width(textWidth);
		timeWrapper.add(clockImage).width(Globals.baseSize)
				.height(Globals.baseSize).left().expand();

		Label bestTime = new Label(Globals.makeTimeStr(playedTrack
				.getBestTime()), skin, "defaultWhite");
		timeWrapper.add(bestTime).right().expand();
		contentWrapper.add(timeWrapper).expand().width(wrapperWidths).pad(5);
		contentWrapper.row();

		// Coins Earned
		Label coinsEarnedText = new Label("Coins Earned", skin, "defaultWhite");
		Image coinImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/white_coin.png"));

		coinsWrapper.add(coinsEarnedText).left().width(textWidth);
		coinsWrapper.add(coinImage).width(Globals.baseSize)
				.height(Globals.baseSize).left().expand();

		coinsEarned = new Label("coins", skin, "defaultWhite");
		coinsWrapper.add(coinsEarned).right().expand();
		contentWrapper.add(coinsWrapper).width(wrapperWidths).expand().pad(5);
		contentWrapper.row();

		// Stars Won
		Label starsText = new Label("Stars Won", skin, "defaultWhite");
		stars = new Image(gameLoader.Assets.getFilteredTexture("coin.png"));

		performanceWrapper.add(starsText).left().expand().width(textWidth);
		performanceWrapper.add(stars).width(Globals.baseSize * 3.4f)
				.height(Globals.baseSize).right().expand();

		contentWrapper.add(performanceWrapper).width(wrapperWidths).pad(5);
		contentWrapper.row();

		// ADMIN MODE
		if (Globals.ADMIN_MODE) {
			contentWrapper.row();

			final TextBox difficulty = new TextBox("difficulty");
			difficulty.setMaxLength(1);
			difficulty.setTextBoxString("d");
			difficulty.setWidth(100);

			final TextBox index = new TextBox("index");
			index.setMaxLength(1000);
			index.setTextBoxString("i");
			index.setWidth(100);

			contentWrapper.row();
			contentWrapper.add(index).pad(5);
			contentWrapper.row();
			contentWrapper.add(difficulty).pad(5);

			Menu.Button upload = new Menu.Button("adventure");
			upload.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					BackendFunctions.uploadTrack(User.getInstance()
							.getCurrentTrack(), RESTPaths.MAPS, popQueObject
							.getGamePlayInstance().getMapTime(), Integer
							.parseInt(difficulty.getText()), Integer
							.parseInt(index.getText()));
					super.clicked(event, x, y);
				}

			});
			contentWrapper.row();
			contentWrapper.add(upload);

			Menu.Button infiniteUpload = new Menu.Button("infinte");
			infiniteUpload.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					BackendFunctions.uploadTrack(User.getInstance()
							.getCurrentTrack(), RESTPaths.INFINITE_MAPS, popQueObject
							.getGamePlayInstance().getMapTime(), Integer
							.parseInt(difficulty.getText()), Integer
							.parseInt(index.getText()));
					super.clicked(event, x, y);
				}

			});
			contentWrapper.add(infiniteUpload);
		}

		base.add(contentWrapper).expandY().center();

		base.row();

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

		Button nextLevel = new Button(skin, "carBuilder_play");
		nextLevel.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Label nextLevelText = new Label("Next Level", skin);
		nextLevel.add(nextLevelText).pad(20);

		Image nextLevelImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/play.png"));
		nextLevel.add(nextLevelImage).width(Globals.baseSize)
				.height(Globals.baseSize * 1.2f).pad(10);

		TextButton exit = new TextButton("exit", skin, "noButton");
		exit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		ImageButton restart = SimpleImageButton.create(
				SimpleImageButtonTypes.RESTART, gameLoader);
		restart.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Table bottomBar = new Table(skin);

		bottomBar.setBackground("blackOut");

		Table bottomWrapper = new Table(skin);

		bottomWrapper.add(home).width(90).fillY().expandY();
		bottomWrapper.add(restart).width(90).fillY().expandY();

		bottomBar.add(bottomWrapper).expand().left().fillY();

		bottomBar.add(nextLevel).right().fillY().expand();
		// bottomBar.moveBy(0, -100);

		// Animations.fadeInFromBottom(bottomBar, 50);

		base.add(bottomBar).height(80).expandX().fillX();
	}

	public Table getBase() {
		return base;
	}

	public void updateMoney(GameLoader gameLoader, PopQueObject popQueObject) {
		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());
		Integer position = popQueObject.getGamePlayInstance()
				.calculateWinings();

		if (position == 1) {
			stars.setDrawable(new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/images/three_gold_stars.png"))));
		} else if (position == 2) {
			stars.setDrawable(new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/images/two_gold_stars.png"))));
		} else if (position == 3) {
			stars.setDrawable(new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/images/one_gold_star.png"))));
		} else {
			stars.setDrawable(new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/images/no_gold_stars.png"))));
		}
		stars.act(1 / 60f);

		coinsEarned.setText(position.toString());
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		User.getInstance()
				.addCoin(
						(popQueObject.getGamePlayInstance().calculateWinings() >= Globals.POSITION_LOST ? 0
								: popQueObject.getGamePlayInstance()
										.calculateWinings())
								* playedTrack.getDifficulty());

	}
}
