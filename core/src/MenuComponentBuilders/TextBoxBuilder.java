package MenuComponentBuilders;

import Menu.FontManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TextBoxBuilder extends TextField {
	
	public enum TextBoxStyles {
		TEST, WHITE
	}

	private String text;

	public TextBoxBuilder(String name,TextBoxStyles style ) {
		super(name, buildDefaultTextStyle(style));

		setText(text);

		initDefualt();
	}
	
	public TextBoxBuilder(int name, TextBoxStyles style) {
		super(Integer.toString(name), buildDefaultTextStyle(style));

		setText(text);

		initDefualt();
	}
	
	private static TextFieldStyle buildDefaultTextStyle(TextBoxStyles style) {
		//Skin skin = Skins.loadDefault();
		TextFieldStyle textfieldstyle = new TextFieldStyle();
		
		if(style == TextBoxStyles.TEST){
			textfieldstyle.font = FontManager.GenerateDefaultFont();
			textfieldstyle.fontColor = Color.GREEN;
			textfieldstyle.messageFontColor = Color.GREEN;
		} else if(style == TextBoxStyles.WHITE){
			textfieldstyle.font = FontManager.GenerateDefaultFont();
			textfieldstyle.fontColor = Color.WHITE;		
			textfieldstyle.messageFontColor = Color.WHITE;
		}
		
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
