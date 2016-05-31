package Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TextBox extends TextField {

	private String text;

	public TextBox(String name) {
		super(name, buildDefaultTextStyle());

		setText(text);

		initDefualt();
	}
	
	public TextBox(int name) {
		super(Integer.toString(name), buildDefaultTextStyle());

		setText(text);

		initDefualt();
	}

	
	private static Skin defaultSkin() {
		Skin skin = new Skin();
		BitmapFont bfont = FontManager.GenerateScaledFont("fonts/simpleFont.ttf", 4,
				Color.GREEN,10,1);
		skin.add("font", bfont);
		return skin;
	}
	
	private static TextFieldStyle buildDefaultTextStyle() {
		Skin skin = defaultSkin();
		
		TextFieldStyle textfieldstyle = new TextFieldStyle();
		textfieldstyle.font = skin.getFont("font");
		textfieldstyle.fontColor = Color.GREEN;
		
		return textfieldstyle;
	}

	public void setTextBoxString(String text) {
		this.text = text;
		this.setText(text);
	}
	
	public void setTextBoxString(int text) {
		this.text = Integer.toString(text);
		this.setText(this.text);
	}

	private void initDefualt() {
		this.setMessageText(text);
	}

}
