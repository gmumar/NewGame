package Dialog;

import MenuComponentBuilders.DialogStyleBuilder;
import MenuComponentBuilders.TextBoxBuilder;
import MenuComponentBuilders.TextBoxBuilder.TextBoxStyles;

public class TextDialog extends DialogStyleBuilder {
	

	public TextDialog(String title) {
		super("", DialogStyleType.TEXT);
		
		TextBoxBuilder line = new TextBoxBuilder(title, TextBoxStyles.WHITE);
		line.setText(title);
		this.add(line).center();
		
	}

	
}
