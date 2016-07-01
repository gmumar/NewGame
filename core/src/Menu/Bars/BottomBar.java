package Menu.Bars;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import Menu.ScreenType;
import Menu.TableW;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.CarBuilderScreen;
import com.gudesigns.climber.CarModeScreen;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.SelectorScreens.TrackSelectorScreen.ForrestTrackSelectorScreen;

public class BottomBar {

	public final static TableW create(Table base, final ScreenType type,
			final GameState gameState, boolean animate) {

		base.row();

		final GameLoader gameLoader = gameState.getGameLoader();
		Skin skin = Skins.loadDefault(gameLoader, 0);
		TableW bottomBar = new TableW(skin);

		Button next = new Button(skin, "transparentButton");

		Image nextImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/start.png"));

		next.add(nextImage).width(Globals.baseSize * 3)
				.height(Globals.baseSize * 3).pad(18);

		next.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (type == ScreenType.MODE_SCREEN) {
					gameLoader.setScreen(new ForrestTrackSelectorScreen(
							gameState));
				} else if (type == ScreenType.FORREST_TRACK_SELECTOR
						|| type == ScreenType.ARCTIC_TRACK_SELECTOR
						|| type == ScreenType.INFINITE_TRACK_SELECTOR) {
					gameLoader.setScreen(new CarModeScreen(gameState));
				} else if (type == ScreenType.CAR_SELECTOR) {
					gameLoader.setScreen(new GamePlayScreen(gameState));
				} else if (type == ScreenType.CAR_MODE_SCREEN) {
					gameLoader.setScreen(new CarBuilderScreen(gameState));
				} else if (type == ScreenType.MAIN_MENU_SCREEN) {
					gameLoader.setScreen(new GamePlayScreen(gameState));
				}
				super.clicked(event, x, y);
			}

		});

		bottomBar.add(next).right().expand();

		base.add(bottomBar).fillX().height(Globals.baseSize * 3.5f).expandX()
				.bottom().padBottom(12).padLeft(12).padRight(12);

		if (animate) {
			// Animations.fadeInFromBottom(bottomBar, 50);
		}

		return bottomBar;
	}
}
