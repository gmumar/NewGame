package Menu;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.gudesigns.climber.GameLoader;

public class FontManager {

	private static final int SCALE = 10;

	public static BitmapFont GenerateFont(GameLoader gameLoader, String fontFile, int size, Color color) {

		String key = color + "_" + fontFile + "_" + Integer.toString(size);

		if (gameLoader.fonts.containsKey(key)) {
			return gameLoader.fonts.get(key);
		} else {
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
					Gdx.files.internal(fontFile));

			FreeTypeFontParameter parameters = new FreeTypeFontParameter();
			parameters.size = size * SCALE;
			parameters.borderStraight = false;
			parameters.genMipMaps = true;
			parameters.incremental = true;
			// parameters.magFilter = TextureFilter.MipMapLinearLinear;
			// parameters.minFilter = TextureFilter.MipMapLinearLinear;
			parameters.hinting = Hinting.Full;
			parameters.spaceX = -2;
			parameters.kerning = true;
			parameters.color = color;
			BitmapFont bfont = generator.generateFont(parameters);// new
																	// BitmapFont();
			bfont.getData().setScale((float) size / SCALE);
			bfont.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			gameLoader.fonts.put(key, bfont);
			return bfont;
		}
	}
	
	private static  HashMap<String, BitmapFont> fonts = new HashMap<String, BitmapFont>();

	public static BitmapFont GenerateScaledFont(String fontFile, int size,
			Color color, int Scale, float boader) {
		String key = color + "_" + fontFile + "_" + Scale;

		if (fonts.containsKey(key)) {
			return fonts.get(key);
		} else {
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
					Gdx.files.internal(fontFile));

			FreeTypeFontParameter parameters = new FreeTypeFontParameter();
			parameters.size = size * Scale;
			// parameters.kerning = true;
			// parameters.borderStraight = true;
			// parameters.genMipMaps = true;
			// parameters.incremental = true;
			parameters.magFilter = TextureFilter.Linear;
			parameters.minFilter = TextureFilter.Nearest;
			parameters.borderWidth = boader;
			parameters.borderColor = color;
			parameters.color = color;
			BitmapFont bfont = generator.generateFont(parameters);// new
																	// BitmapFont();
			bfont.getData().setScale((float) size / Scale);
			bfont.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			fonts.put(key, bfont);
			return bfont;
		}
	}

}
