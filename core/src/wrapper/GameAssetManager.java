package wrapper;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class GameAssetManager extends AssetManager{

	public synchronized  Texture getFilteredTexture(String fileName) {
		Texture texture = get(fileName,Texture.class);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}
	
	

}
