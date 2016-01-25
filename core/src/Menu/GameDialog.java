package Menu;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameDialog extends Dialog {

	public GameDialog(String title, Skin skin, String name) {
		super(title, skin, name);
	}

	{
		text("hello, this is the text");
		
	}

	
}
