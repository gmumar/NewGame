package MenuComponentBuilders;

import Dialog.Skins;
import Menu.FontManager;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DialogStyleBuilder extends Dialog {

	public enum DialogStyleType {
		BUY, TEXT
	}

	public DialogStyleBuilder(String title, DialogStyleType type) {
		super(title, buildWindowStyle(title, type));
		// super(title, skin, windowStyleName);
	}

	private static WindowStyle buildWindowStyle(String windowName,
			DialogStyleType type) {
		WindowStyle ws = new WindowStyle();

		Skin skin = Skins.loadDefault();// new Skin();

		if (type == DialogStyleType.BUY) {
			ws = skin.get("default", WindowStyle.class);
		} else if (type == DialogStyleType.TEXT){
			ws = skin.get("default", WindowStyle.class);
		} else {
			ws = skin.get("default", WindowStyle.class);
		}

		BitmapFont bfont = FontManager.GenerateDefaultFont();
		ws.titleFont = bfont;

		return ws;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(super.getColor());
		super.draw(batch, parentAlpha);
	}

	public void allClear() {

		this.clear();
		super.clear();
	}

	public void allFill() {

		this.setFillParent(true);
		super.setFillParent(true);
		super.validate();
		this.validate();
	}

}
