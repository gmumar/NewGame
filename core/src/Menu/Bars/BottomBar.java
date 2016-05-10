package Menu.Bars;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import Menu.Animations;
import Menu.TableW;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.TrackSelectorScreen;

public class BottomBar {
	public enum BottomBarFor {MODE_SCREEN};

	public final static TableW create(Table base,final BottomBarFor type, final GameState gameState, boolean animate) {
		
		
		base.row();
		
		final GameLoader gameLoader = gameState.getGameLoader();
		Skin skin = Skins.loadDefault(gameLoader, 0);
		TableW bottomBar = new TableW(skin);

		Button next = new Button(skin, "transparentButton");
		Pixmap pixmap = new Pixmap((100), (100), Format.RGBA8888);
		pixmap.setColor(Globals.GREEN);
		pixmap.fillCircle(50, 50, 48);
		pixmap.setColor(Color.WHITE);
		pixmap.fillTriangle(30, 100 * 3 / 4, 30, 100 * 1 / 4, 80, 100 * 2 / 4);
		Texture circle = new Texture(pixmap);
		circle.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Image nextImage = new Image(circle);

		next.add(nextImage).width(Globals.baseSize * 2)
				.height(Globals.baseSize * 2);

		next.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(type == BottomBarFor.MODE_SCREEN){
					gameLoader.setScreen(new TrackSelectorScreen(gameState));
				}
				super.clicked(event, x, y);
			}

		});

		bottomBar.add(next).right().expand();
		
		base.add(bottomBar).fillX().height(Globals.baseSize * 2).expandX()
				.bottom();
		
		if(animate) Animations.fadeInFromBottom(bottomBar,50);
		
		return bottomBar;
	}
}
