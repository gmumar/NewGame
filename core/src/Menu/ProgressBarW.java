package Menu;

import wrapper.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ProgressBarW extends ProgressBar {

	public ProgressBarW(float min, float max, float stepSize, boolean vertical,
			String name) {
		super(min, max, stepSize, vertical, buildDefaultButtonStyle(name));
	}

	private static ProgressBarStyle buildDefaultButtonStyle(String butName) {
		TextureRegionDrawable textureBar = new TextureRegionDrawable(
				new TextureRegion(Globals.Assets.get("life_small.png", Texture.class)));
	
		Skin skin = new Skin();

		Pixmap pixmap = new Pixmap((10), (10), Format.RGBA8888);
		pixmap.setColor(Color.GOLD);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		ProgressBarStyle pbs = new ProgressBarStyle(skin.newDrawable("white",
				Color.WHITE), textureBar);
		pbs.knob.setMinHeight(25);
		pbs.knob.setMinWidth(25);

		return pbs;
	}

}
