package Dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Text2ButtonDialog extends DialogBase {
	
	private String title;

	public Text2ButtonDialog(String title, Skin skin, String name) {
		super(title, skin, name);
		this.title = title;
	}

	{
		button("BUY!");
		button("cancel");
		text(title);
	}

	@Override
	public Dialog button(String text) {
		// TODO Auto-generated method stub
		return super.button(text);
	}

	
	
}
