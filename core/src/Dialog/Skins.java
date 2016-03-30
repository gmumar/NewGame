package Dialog;

import Menu.FontManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Skins {

	public static Skin loadDefault() {
		Skin skin;

		FileHandle skinFile = Gdx.files.internal("skins/uiskin.json");
		skin = new Skin(new TextureAtlas("skins/uiskin.atlas"));

		BitmapFont font = FontManager.GenerateFont("fonts/simpleFont.ttf", 4,
				Color.BLACK);
		skin.add("default-font", font);

		skin.load(skinFile);

		return skin;
	}
	
	public static Skin loadButton() {
		Skin skin;

		FileHandle skinFile = Gdx.files.internal("skins/uiskin.json");
		skin = new Skin(new TextureAtlas("skins/uiskin.atlas"));

		BitmapFont font = FontManager.GenerateScaledFont("fonts/simpleFont.ttf", 4,
				Color.WHITE,30, 2.5f);
		skin.add("default-font", font);

		skin.load(skinFile);

		return skin;
	}

}
