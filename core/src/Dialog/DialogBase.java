package Dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DialogBase extends Dialog {

	public DialogBase(String title, Skin skin, String windowStyleName) {
		super(title, skin, windowStyleName);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(super.getColor());
		super.draw(batch, parentAlpha);
	}
	
	public void allClear(){
		
		this.clear();
		super.clear();
	}
	
	public void allFill(){
		
		this.setFillParent(true);
		super.setFillParent(true);
		super.validate();
		this.validate();
	}
	
	
}
