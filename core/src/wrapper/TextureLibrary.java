package wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class TextureLibrary {

	public static HashMap<String, Texture> textureMap = null;
	
	public static Texture getTexture(String textureName){
		if(textureMap == null){
			initMap();
		}
		
		if (textureMap.containsKey(textureName)) {
			return textureMap.get(textureName);
		} else {
			Texture texture = new Texture(textureName);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textureMap.put(textureName, texture);
			return texture;
		}
	}

	private static void initMap() {
		textureMap = new HashMap<String, Texture>();
	}
	
	public static void addTexture(String name, Texture texture){
		if(textureMap == null){
			initMap();
		}
		textureMap.put(name, texture);
		
	}
	
	public static void destroyMap(){
		if(textureMap == null){
			return;
		}
		Iterator<Entry<String, Texture>> iter = textureMap.entrySet().iterator();
		
		while(iter.hasNext()){
			Entry<String, Texture> item = iter.next();
			item.getValue().dispose();
		}
		
		textureMap = null;
	}
}
