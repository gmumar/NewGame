package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import Menu.TextBox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class FinalizeChallengeDialog {

	public static DialogBase CreateDialog(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);

		final DialogBase base = new DialogBase("", skin, "transparent");
		//final Table base = new Table();
		base.clear();
		base.setFillParent(true);
		//base.setMovable(false);
		//base.setModal(true);
		
		Table whiteBg = new Table(skin);
		whiteBg.setBackground("white");
		

		Table header = new Table();
		Table textFeilds = new Table();
		Table buttons = new Table();

		// Header
		Image upgradeImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/upload.png"));
		upgradeImage.setColor(Color.BLACK);

		header.add(upgradeImage).width(Globals.baseSize)
				.height(Globals.baseSize).pad(5);

		Label upgradeText = new Label("Send Challenge", skin, "dialogTitle");
		header.add(upgradeText);

		whiteBg.add(header).center().pad(5);
		whiteBg.row();
		
		// Text Feilds
		Label rewardMoneyLable = new Label("Reward ", skin, "default");
		textFeilds.add(rewardMoneyLable).padLeft(10).padBottom(5);
		Image rewardMeneyImage = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/dull_coin.png"));
		textFeilds.add(rewardMeneyImage).width(Globals.baseSize).height(Globals.baseSize).pad(3);
		final TextBox rewardMoney = new TextBox("1000", skin, "userInput");//("MONEY", skin, "dialogTitle");
		rewardMoney.setTextFieldFilter(new DigitsOnlyFilter());
		textFeilds.add(rewardMoney).padRight(10).padBottom(5);
		
		textFeilds.row();

		Label targetUserLable = new Label("Challenger ", skin, "default");
		textFeilds.add(targetUserLable).padLeft(10).padBottom(5);
		Image targetUserImage = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/opponent_user_black.png"));
		textFeilds.add(targetUserImage).width(Globals.baseSize).height(Globals.baseSize).pad(3);
		final TextBox targetUser = new TextBox("" , skin, "userInput");//("MONEY", skin, "dialogTitle");
		textFeilds.add(targetUser).padRight(10).padBottom(5);
		whiteBg.add(textFeilds);
		
		whiteBg.row();


		/*
		 * base.text("Buy level:" + popQueObject.getNextLevel() + " of " +
		 * popQueObject.getComponentName() + " for " + itemCost.toString());
		 */

		TextButton noButton = new TextButton("cancel", Skins.loadDefault(
				gameLoader, 1), "noButton");
		noButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// popQueObject.getCallingInstance().failedBuy();
				base.hide();
				super.clicked(event, x, y);
			}

		});
		buttons.add(noButton).height(Globals.baseSize * 2).fillX().expandX();

		TextButton yesButton = new TextButton("SEND!", Skins.loadDefault(
				gameLoader, 1), "yesButton");
		yesButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				if(popQueObject.getGamePlayInstance().submitChallenge(rewardMoney.getText(), targetUser.getText())){
					//base.hide();
				}

				super.clicked(event, x, y);
			}

		});
		buttons.add(yesButton).height(Globals.baseSize * 2).fillX().expandX();

		whiteBg.add(buttons).expandX().fillX();
		
		base.add(whiteBg).top().expandY().pad(10);

		return base;
	}

}
