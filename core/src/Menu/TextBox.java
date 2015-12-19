package Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TextBox extends TextField {

	String name;
	String text;
	Skin skin;

	public TextBox(String name) {
		super(name, buildDefaultTextStyle());

		this.name = name;
		setText(text);

		initDefualt();
	}
	
	public TextBox(int name) {
		super(Integer.toString(name), buildDefaultTextStyle());

		this.name = Integer.toString(name);
		setText(text);

		initDefualt();
	}

	
	private static Skin defaultSkin() {
		Skin skin = new Skin();
		BitmapFont bfont = new BitmapFont();
		skin.add("font", bfont);
		return skin;
	}
	
	private static TextFieldStyle buildDefaultTextStyle() {
		Skin skin = defaultSkin();
		
		TextFieldStyle textfieldstyle = new TextFieldStyle();
		textfieldstyle.disabledFontColor = Color.BLACK;
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
