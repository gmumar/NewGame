package Dialog;

import MenuComponentBuilders.TextBoxBuilder;
import MenuComponentBuilders.TextButtonStyleBuilder;
import MenuComponentBuilders.TextBoxBuilder.TextBoxStyles;
import MenuComponentBuilders.TextButtonStyleBuilder.TextButtonTypes;
import Menu.PopQueObject;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gudesigns.climber.GameLoader;

public class WinDialog {
	
	//reference : https://github.com/EsotericSoftware/tablelayout

	public static Table CreateDialog(GameLoader gameLoader,final PopQueObject popQueObject) {
		
		Skin skin = Skins.loadDefault();
		

		System.out.println("making table");
		final Table base = new Table();
		//base.debugAll();
		//base.setColor(1, 1, 1, 0);
		base.setBackground(skin.get("dialogDim", Drawable.class));
		base.setFillParent(true);
		base.addAction(Actions.fadeIn(0.5f));
		
		System.out.println("making restart button");
		
		TextButtonStyleBuilder restart = new TextButtonStyleBuilder("restart", TextButtonTypes.RESTART);
		restart.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				//base.hide();
				super.clicked(event, x, y);
			}
			
		});
		
		System.out.println("making exit button");
		TextButtonStyleBuilder exit = new TextButtonStyleBuilder("exit", TextButtonTypes.EXIT);
		exit.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				//base.hide();
				super.clicked(event, x, y);
			}
			
		});
		
		System.out.println("making text box");
		
		TextBoxBuilder text = new TextBoxBuilder("Win!", TextBoxStyles.WHITE);
		//text.setTextBoxString("Win!");
		
		base.add(text).expandY().center();
		base.row();
		
		Table bottomBar = new Table(skin);
		bottomBar.setBackground("dialogDim");
		
		Table bottomWrapper = new Table(skin);
		
		bottomWrapper.add(exit).width(90).pad(2);
		bottomWrapper.add(restart).width(90);
		
		bottomBar.add(bottomWrapper).expand().left().fillY();
		//bottomBar.moveBy(0, -100);
		bottomBar.addAction(Actions.moveTo(0, -100, 0.0f));
		
		bottomBar.addAction(new ParallelAction(Actions.fadeIn(0.5f),Actions.moveBy(0, 100, 0.2f)));
		
		base.add(bottomBar).height(100).expandX().fillX();
		
		return base;
	}

}
