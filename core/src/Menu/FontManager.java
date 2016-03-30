package Menu;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {
	
	private static final int SCALE = 10;
	
	private static HashMap<String,BitmapFont> fonts = new HashMap<String,BitmapFont>();
	
	public static BitmapFont GenerateFont(String fontFile, int size, Color color){
		
		String key = color + "_" + fontFile;
		
		if(fonts.containsKey(key)){
			return fonts.get(key);
		}else{
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontFile));
	
			FreeTypeFontParameter parameters = new FreeTypeFontParameter();
			parameters.size =  size * SCALE;
			//parameters.borderStraight = false;
			//parameters.genMipMaps = true;
			//parameters.incremental = true;
			parameters.magFilter = TextureFilter.Linear;
			parameters.minFilter = TextureFilter.Linear;
			parameters.borderWidth = 1f;
			parameters.borderColor = color;
			parameters.color = color;
			BitmapFont bfont = generator.generateFont(parameters);// new BitmapFont();
			bfont.getData().setScale((float)size/SCALE);
			fonts.put(key, bfont);
			return bfont;
		}
	}

	public static BitmapFont GenerateScaledFont(String fontFile, int size, Color color, int Scale, float boader) {
	String key = color + "_" + fontFile + "_" +Scale;
		
		if(fonts.containsKey(key)){
			return fonts.get(key);
		}else{
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontFile));
	
			FreeTypeFontParameter parameters = new FreeTypeFontParameter();
			parameters.size =  size * Scale;
			//parameters.kerning = true;
			//parameters.borderStraight = true;
			//parameters.genMipMaps = true;
			//parameters.incremental = true;
			parameters.magFilter = TextureFilter.Linear;
			parameters.minFilter = TextureFilter.Nearest;
			parameters.borderWidth = boader;
			parameters.borderColor = color;
			parameters.color = color;
			BitmapFont bfont = generator.generateFont(parameters);// new BitmapFont();
			bfont.getData().setScale((float)size/Scale);
			fonts.put(key, bfont);
			return bfont;
		}
	}

}
