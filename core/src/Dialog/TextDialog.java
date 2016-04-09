package Dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TextDialog extends DialogBase {
	
	private String title;

	public TextDialog(String title, Skin skin, String name) {
		super(title, skin, name);
		this.title = title;
	}

	{
		text(title);
		
	}

	
}
