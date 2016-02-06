package Menu;

import java.util.ArrayList;

import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PopQueManager {

	private ArrayList<PopQueObject> que = new ArrayList<PopQueObject>();
	private static float timePassed = 0;
	private Stage stage;
	
	private GameDialog dialog;
	private PopQueObject currentMsg;
	
	public PopQueManager(Stage stage){
		this.stage = stage;
	}

	public void update(float delta) {
		
		timePassed += delta ;
		
		if(timePassed > 5){
			timePassed = 0;
		}
		
		if (que.isEmpty()) {
			return;
		} else {
			currentMsg = pop();
			handlePop(currentMsg);
		}
	}

	public void push(PopQueObject obj) {
		que.add(obj);
	}
	
	private PopQueObject pop(){
		PopQueObject obj = que.get(0);
		que.remove(0);
		return obj;
	}

	private void handlePop(PopQueObject popQueObject){
		
		if (popQueObject.getType() == PopQueObjectType.TEST)
		{
			 createDialog();
		}
		else if(popQueObject.getType() == PopQueObjectType.LOADING)
		{
			createLoadingDialog();
		}
		else if(popQueObject.getType() == PopQueObjectType.DELETE)
		{
			dialog.hide();
		}
		
	}
	
	private void createLoadingDialog() {
		Skin skin = new Skin();
		BitmapFont font = FontManager.GenerateFont("fonts/simpleFont.ttf", 4,
				Color.BLACK);
		skin.add("default-font", font);
		skin.load(Gdx.files.internal("skins/dialogSkin.json"));

		dialog = new GameDialog("Loading", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
		
	}

	private void createDialog(){
		
		Skin skin = new Skin();
		BitmapFont font = FontManager.GenerateFont("fonts/simpleFont.ttf", 4,
				Color.BLACK);
		skin.add("default-font", font);
		skin.load(Gdx.files.internal("skins/dialogSkin.json"));

		dialog = new GameDialog("blaa", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
	}
}
