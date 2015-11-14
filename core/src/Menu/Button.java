package Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Button extends TextButton {

	String name;

	public Button(String name) {
		super(name, buildDefaultButtonStyle());
		this.name = name;

		this.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Clicked();
			}

		});
	}

	public void Clicked() {
		;
	}

	private static TextButtonStyle buildDefaultButtonStyle() {
		TextButtonStyle tbs = new TextButtonStyle();
		Skin skin = new Skin();

		Pixmap pixmap = new Pixmap((100), (100), Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		BitmapFont bfont = new BitmapFont();
		skin.add("default", bfont);

		tbs.up = skin.newDrawable("white", Color.DARK_GRAY);
		tbs.down = skin.newDrawable("white", Color.DARK_GRAY);
		// tbs.checked = skin.newDrawable("white", Color.BLUE);
		tbs.over = skin.newDrawable("white", Color.LIGHT_GRAY);

		tbs.font = skin.getFont("default");

		return tbs;
	}

}
