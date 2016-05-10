package wrapper;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class GameAssetManager extends AssetManager{

	@Override
	public synchronized <T> T get(String fileName, Class<T> type) {
		// TODO Auto-generated method stub
		return super.get(fileName, type);
	}
	
	public synchronized  Texture getFilteredTexture(String fileName) {
		Texture texture = get(fileName,Texture.class);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}
	
	

}
