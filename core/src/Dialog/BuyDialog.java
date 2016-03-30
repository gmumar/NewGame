package Dialog;

import Menu.PopQueObject;
import User.Costs;
import User.User;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class BuyDialog {

	public static DialogBase CreateDialog(final PopQueObject popQueObject) {

		final DialogBase base = new DialogBase("", Skins.loadDefault(), "default");

		base.setMovable(false);
		base.setModal(true);
		base.pad(10);

		base.getButtonTable().defaults().height(60);
		base.getButtonTable().defaults().width(60);
		
		final Integer itemCost = Costs.lookup(popQueObject.getComponentName(),
				popQueObject.getNextLevel());

		base.text("Buy level:"
				+ popQueObject.getNextLevel()
				+ " of "
				+ popQueObject.getComponentName()
				+ " for "
				+ itemCost.toString());
		
		TextButton yesButton = new TextButton("Yes",Skins.loadButton(),"yesButton");
		yesButton.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				User.getInstance().buyItem(popQueObject.getComponentName(), itemCost);
				popQueObject.getCallingInstance().successfulBuy();
				base.hide();
				super.clicked(event, x, y);
			}
			
		});
		base.getButtonTable().add(yesButton).height(60).width(120).pad(-2.5f);

		TextButton noButton = new TextButton("No",Skins.loadButton(),"noButton");
		noButton.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getCallingInstance().failedBuy();
				base.hide();
				super.clicked(event, x, y);
			}
			
		});
		base.getButtonTable().add(noButton).height(60).width(120).pad(-2.5f);
		
		
		
		int size = 50;

		Texture t = new Texture("bar/level1.png");
		TextureRegion tr = new TextureRegion(t);
		float ratio = (float)t.getHeight()/t.getWidth();
		Image i = new Image(tr);
		
		base.getContentTable().row();

		// How to resize stuff
		base.getContentTable().add(i).height(size*ratio).width(size).padBottom(5);
		
		// Make it look rite
		base.getButtonTable().pad(-2);
		base.getContentTable().pad(3);
		base.pad(0);

		return base;
	}

}
