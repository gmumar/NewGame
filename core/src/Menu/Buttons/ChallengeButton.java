package Menu.Buttons;

import wrapper.Globals;
import Assembly.Assembler;
import Dialog.Skins;
import JSONifier.JSONChallenge;
import JSONifier.JSONTrack.TrackType;
import Multiplayer.Challenge;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TrackMode;
import UserPackage.User;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class ChallengeButton {

	public static final Button create(GameLoader gameLoader,
			JSONChallenge challengeJSON) {

		Skin skin = Skins.loadDefault(gameLoader, 1);
		String indexTxt = "";
		User user = User.getInstance();

		Challenge challenge = Challenge.objectify(challengeJSON.getChallenge());

		boolean isNew = user.isNew(ItemsLookupPrefix
				.getChallengePrefix(challengeJSON.getObjectId()));

		Table stackInlay = new Table();

		Button base = null;

		indexTxt = (challenge.getTrackIndex());
		if (challenge.getTrackMode() == TrackMode.INFINTE) {

			if (challenge.getTrackDifficulty() == 1) {
				base = new Button(skin, "adventureTrack_diff_1");
			} else if (challenge.getTrackDifficulty() == 2) {
				base = new Button(skin, "adventureTrack_diff_2");
			} else if (challenge.getTrackDifficulty() == 3) {
				base = new Button(skin, "adventureTrack_diff_3");
			} else {
				base = new Button(skin, "adventureTrack_diff_x");
			}

		} else {
			if (challenge.getTrackType() == TrackType.FORREST) {
				base = new Button(skin, "adventureTrack_forrest");
			} else if (challenge.getTrackType() == TrackType.ARTIC) {
				base = new Button(skin, "adventureTrack_artic");
			}
		}

		Stack stack = new Stack();
		// stack.setFillParent(true);

		Table content = new Table(skin);
		content.setBackground("white");

		Table userNameTable = new Table();

		Image fromImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/opponent_user_black.png"));
		userNameTable.add(fromImage).width(Globals.baseSize * 3 / 4)
				.height(Globals.baseSize * 3 / 4).left();

		Label index = new Label("index", skin, "dialogTitle");
		index.setText(challengeJSON.getSourceUser().toUpperCase());
		userNameTable.add(index).right();

		content.add(userNameTable).expandX().fill();
		content.row();

		Table trackNameTable = new Table();

		Label trackName = new Label("index", skin, "dialogTitle");

		String name = null;

		if (challenge.getTrackMode() == TrackMode.INFINTE) {
			name = "INFINITY";
		} else {
			if (challenge.getTrackType() == TrackType.ARTIC) {
				name = "ARCTIC";
			} else if (challenge.getTrackType() == TrackType.FORREST) {
				name = "COUNTRY";
			}
		}

		trackName.setText(name + " " + challenge.getTrackIndex());
		Image trackNameImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/map_black.png"));
		trackNameTable.add(trackNameImage).width(Globals.baseSize * 3 / 4)
				.height(Globals.baseSize * 3 / 4);

		trackNameTable.add(trackName);

		content.add(trackNameTable);

		content.row();

		TextureRegion Cartr = Assembler.assembleCarImage(gameLoader, challenge
				.getCarJson().jsonify(), false, false);
		TextureRegionDrawable Cartrd = new TextureRegionDrawable(Cartr);
		Cartrd.setMinWidth(Globals.CAR_DISPLAY_BUTTON_WIDTH * 3 / 4f);
		Cartrd.setMinHeight(Globals.CAR_DISPLAY_BUTTON_HEIGHT);

		Image carImage = new Image(Cartrd);
		content.add(carImage).pad(10);
		content.row();

		Table stats = new Table();

		Table timeTable = new Table();
		Image clock = new Image(
				gameLoader.Assets.getFilteredTexture("worlds/hud/clock.png"));
		timeTable.add(clock).width(Globals.baseSize).height(Globals.baseSize)
				.pad(5);

		Label userTime = new Label("index", skin, "default");

		userTime.setText(Globals.makeTimeStr(challengeJSON.getBestTime()));

		timeTable.add(userTime);

		stats.add(timeTable).expandX().fillX().left();

		Table rewardTable = new Table();
		Image coin = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/dull_coin.png"));
		rewardTable.add(coin).width(Globals.baseSize).height(Globals.baseSize)
				.pad(5);

		Label reward = new Label("index", skin, "default");

		reward.setText(challengeJSON.getReward().toString());

		rewardTable.add(reward);

		stats.add(rewardTable).expandX().fillX().right();

		content.add(stats).expandX().fillX().pad(5);
		content.row();

		// stack.add(content);
		stackInlay.add(content).width(Globals.baseSize * 9)
				.height(Globals.baseSize * 11);

		stack.add(stackInlay);

		TextureRegion tr = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/new.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);

		trd.setMinWidth(Globals.baseSize * 2f);
		trd.setMinHeight(Globals.baseSize * 2f);

		ImageButton newTag = new ImageButton(trd);
		newTag.align(Align.top | Align.right);

		if (isNew) {
			stack.add(newTag);
		}

		base.add(stack).fill().expand();

		base.setUserObject(indexTxt);

		return base;
	}
}
