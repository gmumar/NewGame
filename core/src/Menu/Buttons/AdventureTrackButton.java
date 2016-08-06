package Menu.Buttons;

import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.ScreenType;
import UserPackage.Costs;
import UserPackage.ItemsLookupPrefix;
import UserPackage.User;
import UserPackage.User.STARS;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
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

public class AdventureTrackButton {

	public static final ButtonLockWrapper create(GameLoader gameLoader,
			JSONTrack track, boolean forInfinite, ScreenType screenType) {

		Skin skin = Skins.loadDefault(gameLoader, 1);
		String indexTxt = "";
		User user = User.getInstance();
		Integer unlockCost = 0;

		boolean isLocked = true;
		boolean isNew = true;

		Table stackInlay = new Table();

		Button base = null;

		if (screenType == ScreenType.NONE) {
			indexTxt = Integer.toString(track.getItemIndex());
			if (forInfinite) {
				isNew = user.isNew(ItemsLookupPrefix
						.getInfiniteTrackPrefix(indexTxt));
				isLocked = false;

				if (track.getDifficulty() == 1) {
					base = new Button(skin, "adventureTrack_diff_1");
				} else if (track.getDifficulty() == 2) {
					base = new Button(skin, "adventureTrack_diff_2");
				} else if (track.getDifficulty() == 3) {
					base = new Button(skin, "adventureTrack_diff_3");
				} else {
					base = new Button(skin, "adventureTrack_diff_x");
				}

			} else {
				isNew = false;
				if (track.getType() == TrackType.FORREST) {
					base = new Button(skin, "adventureTrack_forrest");
					isLocked = user.isLocked(ItemsLookupPrefix
							.getForrestPrefix(indexTxt));
					unlockCost = Costs.ADVENTURE_TRACK*track.getItemIndex();
				} else if (track.getType() == TrackType.ARTIC) {
					base = new Button(skin, "adventureTrack_artic");
					isLocked = user.isLocked(ItemsLookupPrefix
							.getArticPrefix(indexTxt));
					unlockCost = Costs.ADVENTURE_TRACK*track.getItemIndex()*2;
				}
			}
		} else if (screenType == ScreenType.FORREST_TRACK_SELECTOR) {
			base = new Button(skin, "adventureTrack_artic");
			isLocked = user.isLocked(ItemsLookupPrefix.ARCTIC_WORLD);
			unlockCost = Costs.ARCTIC_WORLD;
		} else if (screenType == ScreenType.ARCTIC_TRACK_SELECTOR) {
			base = new Button(skin, "adventureTrack_forrest");
			isLocked = false;
		}

		Stack stack = new Stack();
		// stack.setFillParent(true);

		Table content = new Table(skin);
		content.setBackground("white");

		Image stars = new Image();
		float starWidth = Globals.baseSize * 3 / 5;
		STARS userStars = null;

		if (screenType == ScreenType.NONE) {
			userStars = user.getStars(track.getObjectId());
		} else if (screenType == ScreenType.FORREST_TRACK_SELECTOR 
				||screenType == ScreenType.ARCTIC_TRACK_SELECTOR) {

			userStars = STARS.NONE;
		}

		if (userStars == STARS.NONE) {
			;
		} else if (user.getStars(track.getObjectId()) == STARS.ONE) {
			stars = new Image((new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/images/one_star.png")))));

			starWidth *= 1;
		} else if (userStars == STARS.TWO) {
			stars = new Image((new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/images/two_stars.png")))));

			starWidth *= 2;

		} else if (userStars == STARS.THREE) {
			stars = new Image(
					(new TextureRegionDrawable(
							new TextureRegion(
									gameLoader.Assets
											.getFilteredTexture("menu/images/three_stars.png")))));

			starWidth *= 3;
		}

		content.add(stars).top().padBottom(15).height(Globals.baseSize * 1 / 2)
				.width(starWidth);
		content.row();

		Label index = new Label("index", skin, "index");

		if (screenType == ScreenType.NONE) {
			index.setText(indexTxt);
		} else if (screenType == ScreenType.FORREST_TRACK_SELECTOR) {
			index.setText("Arctic");
		}else if (screenType == ScreenType.ARCTIC_TRACK_SELECTOR) {
			index.setText("Country");
		}

		content.add(index);

		// stack.add(content);
		if (screenType == ScreenType.NONE) {
			stackInlay.add(content).width(Globals.baseSize * 3)
					.height(Globals.baseSize * 3);
		} else if (screenType == ScreenType.FORREST_TRACK_SELECTOR || screenType == ScreenType.ARCTIC_TRACK_SELECTOR) {
			stackInlay.add(content).width(Globals.baseSize * 6)
					.height(Globals.baseSize * 3);
		}

		stack.add(stackInlay);

		if (isLocked) {
			Pixmap overlay = new Pixmap((100), (100), Format.RGBA8888);
			overlay.setColor(Globals.LOCKED_COLOR);
			overlay.fill();

			stack.add(new Image(new Texture(overlay)));
		}

		TextureRegion tr = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/new.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);

		trd.setMinWidth(Globals.baseSize * 2.2f);
		trd.setMinHeight(Globals.baseSize * 2.2f);

		ImageButton newTag = new ImageButton(trd);
		newTag.align(Align.top | Align.right);

		if (isNew) {
			stack.add(newTag);
		}

		TextureRegion lockTextureRegion = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/lock.png"));
		TextureRegionDrawable lockTextureRegionDrawable = new TextureRegionDrawable(
				lockTextureRegion);

		lockTextureRegionDrawable.setMinWidth(Globals.baseSize);
		lockTextureRegionDrawable.setMinHeight(Globals.baseSize * 1.2f);

		Table lockTable = new Table();
		
		ImageButton lock = new ImageButton(lockTextureRegionDrawable);
		lock.align(Align.center);
		//lock.padBottom(30);
		
		Table coinLockPrice = new Table();
		Image coinImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/dull_coin.png"));
		
		Label lockPrice = new Label(unlockCost.toString(), skin);
		coinLockPrice.add(coinImage).width(Globals.baseSize).height(Globals.baseSize).pad(5);
		coinLockPrice.add(lockPrice);

		lockTable.add(lock).pad(10).center();
		lockTable.row();
		lockTable.add(coinLockPrice);

		if (isLocked) {
			stack.add(lockTable);
		}

		base.add(stack).fill().expand();

		base.setUserObject(indexTxt);

		return new ButtonLockWrapper(base, isLocked);
	}
}
