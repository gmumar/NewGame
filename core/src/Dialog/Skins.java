package Dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gudesigns.climber.GameLoader;

public class Skins {

	static Skin skin = null;
	public static Skin loadDefault(GameLoader gameLoader, int quality) {
		

		if(skin == null){

			skin = gameLoader.Assets.get("skins/uiskin.json");

			/*FileHandle skinFile = Gdx.files.internal("skins/uiskin.json");
			skin = new Skin(new TextureAtlas("skins/uiskin.atlas"));

			//skin.remove("game-font",
				//	com.badlogic.gdx.graphics.g2d.BitmapFont.class);

			if (quality == 0) {
				BitmapFont font = FontManager.GenerateFont(
						"fonts/simpleFont.ttf", 4, Color.BLACK);
				skin.add("default-font", font,
						com.badlogic.gdx.graphics.g2d.BitmapFont.class);
			} else if (quality == 1) {
				BitmapFont font = FontManager.GenerateScaledFont(
						"fonts/simpleFont.ttf", 4, Color.WHITE, 30, 2.5f);
				skin.add("default-font", font,
						com.badlogic.gdx.graphics.g2d.BitmapFont.class);
			}

			skin.load(skinFile);*/

		}
		return skin;
	}

}
