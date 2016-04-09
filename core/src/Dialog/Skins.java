package Dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Skins {
	
	static 	Skin skin = null;

	public static Skin loadDefault() {
		
		if(skin==null){
		/*if (gameLoader.Assets.isLoaded("skins/uiskin.json")) {

			skin = gameLoader.Assets.get("skins/uiskin.json");
		} else {*/

			FileHandle skinFile = Gdx.files.internal("skins/uiskin.json");
			skin = new Skin(new TextureAtlas("skins/uiskin.atlas"));

			//skin.remove("game-font",
				//	com.badlogic.gdx.graphics.g2d.BitmapFont.class);

			//if (quality == 0) {
			//	BitmapFont font = FontManager.GenerateDefaultFont();
			//	skin.add("default-font", font, BitmapFont.class);
			/*} else if (quality == 1) {
				BitmapFont font = FontManager.GenerateScaledFont(
						"fonts/simpleFont.ttf", 4, Color.WHITE, 30, 2.5f);
				skin.add("default-font", font,
						com.badlogic.gdx.graphics.g2d.BitmapFont.class);
			}*/

			skin.load(skinFile);

		//}
		}
		return skin;
	}

}
