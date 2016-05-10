package Menu.Bars;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.TableW;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import Purchases.GamePurchaseObserver;
import User.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GameModeScreen;
import com.gudesigns.climber.MainMenuScreen;

public class TitleBar {
	
	public enum TitleBarFor {MODE_SCREEN, SCREEN_SELECTOR};

	public final static TableW create(Table base, final TitleBarFor type,
			final PopQueManager popQueManager, final GameState gameState, boolean animate) {
		final GameLoader gameLoader = gameState.getGameLoader();
		Skin skin = Skins.loadDefault(gameLoader, 0);
		TableW titleBar = new TableW(skin);

		// Back button
		Button back = SimpleImageButton.create(SimpleImageButtonTypes.BACK, gameLoader);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(type== TitleBarFor.MODE_SCREEN){
					gameLoader.setScreen(new MainMenuScreen(gameLoader));
				} else if (type== TitleBarFor.SCREEN_SELECTOR){
					gameLoader.setScreen(new GameModeScreen(gameState));
				}
				super.clicked(event, x, y);
			}

		});

		titleBar.add(back).left().colspan(1);


		// Buy coins button
		Button buyCoins = new Button(skin, "transparentButton");
		buyCoins.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

				Globals.runOnUIThread(new Runnable() {

					@Override
					public void run() {
						gameLoader.getPlatformResolver().requestInformation(
								new GamePurchaseObserver() {
									@Override
									public void handleRecievedInformation(
											Purchases.GamePurchaseResult gamePurchaseResult) {
										popQueManager.push(new PopQueObject(
												PopQueObjectType.DELETE));

										popQueManager.push(new PopQueObject(
												PopQueObjectType.STORE_BUY));
									}
								});
					}
				});
				super.clicked(event, x, y);
			}

		});

		Image glowingCoin = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/glowing_coin.png"));
		buyCoins.add(glowingCoin).width(Globals.baseSize * 2.5f)
				.height(Globals.baseSize * 2f);
		Label coins = new Label(User.getInstance().getMoney().toString(), skin,
				"title");
		buyCoins.add(coins);

		titleBar.add(buyCoins).left().colspan(1).pad(4);

		// Title
		Label titleLabel = new Label("", skin, "title");
		if(type==TitleBarFor.MODE_SCREEN){
			titleLabel.setText("Game Mode");
		} else if (type==TitleBarFor.SCREEN_SELECTOR){
			titleLabel.setText("Please Select");
		}
		titleLabel.setPosition(Globals.ScreenWidth / 2,
				Globals.ScreenHeight / 12);
		titleBar.add(titleLabel).expand().center().padRight(100);

		// Sound
		Button sound = SimpleImageButton.create(SimpleImageButtonTypes.SOUND, gameLoader);
		sound.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueManager.push(new PopQueObject(PopQueObjectType.SOUND));
				super.clicked(event, x, y);
			}
			
		});
		
		titleBar.add(sound);
		
		base.add(titleBar).fillX().height(Globals.baseSize * 2).expandX()
		.center();

		base.row();
		
		if(animate)Animations.slideInFromTop(titleBar,-50);

		return titleBar;

	}

}
