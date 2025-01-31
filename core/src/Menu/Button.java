package Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Button extends TextButton {

	public Button(String name) {
		super(name, buildDefaultButtonStyle(name));

		this.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Clicked();
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// TODO Auto-generated method stub
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				// TODO Auto-generated method stub
				super.touchUp(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				Pressed();
				super.touchDragged(event, x, y, pointer);
			}

		});

	}

	public void Clicked() {
		;
	}

	public void Pressed() {
		;
	}

	private static TextButtonStyle buildDefaultButtonStyle(String butName) {
		TextButtonStyle tbs = new TextButtonStyle();
		Skin skin = new Skin();

		Pixmap pixmap = new Pixmap((100), (100), Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		BitmapFont bfont = FontManager.GenerateScaledFont("fonts/simpleFont.ttf", 4,
				Color.WHITE,10,1);

		skin.add("default", bfont);

		tbs.up = skin.newDrawable("white", Color.DARK_GRAY);
		tbs.down = skin.newDrawable("white", Color.GREEN);
		// tbs.checked = skin.newDrawable("white", Color.BLUE);
		tbs.over = skin.newDrawable("white", Color.LIGHT_GRAY);

		tbs.font = skin.getFont("default");

		return tbs;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(super.getColor());
		super.draw(batch, parentAlpha);
	}
	
	

}
