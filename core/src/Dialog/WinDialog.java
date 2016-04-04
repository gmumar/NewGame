package Dialog;

import Menu.PopQueObject;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class WinDialog {
	
	//reference : https://github.com/EsotericSoftware/tablelayout

	public static Table CreateDialog(GameLoader gameLoader,final PopQueObject popQueObject) {

		final Table base = new Table( Skins.loadDefault(gameLoader,0));
		//base.debugAll();
		base.setColor(1, 1, 1, 0);
		base.setBackground("dialogDim");
		base.setFillParent(true);
		base.addAction(Actions.fadeIn(0.5f));
		
		TextButton restart = new TextButton("restart",Skins.loadDefault(gameLoader,1),"noButton");
		restart.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				//base.hide();
				super.clicked(event, x, y);
			}
			
		});
		
		TextButton exit = new TextButton("exit",Skins.loadDefault(gameLoader,1),"noButton");
		exit.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				//base.hide();
				super.clicked(event, x, y);
			}
			
		});
		
		Label text = new Label("Win!",Skins.loadDefault(gameLoader,1));
		//text.setTextBoxString("Win!");
		
		base.add(text).expandY().center();
		base.row();
		
		Table bottomBar = new Table(Skins.loadDefault(gameLoader,1));
		bottomBar.setBackground("dialogDim");
		
		Table bottomWrapper = new Table(Skins.loadDefault(gameLoader,1));
		
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
