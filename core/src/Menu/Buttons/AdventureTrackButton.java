package Menu.Buttons;

import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import User.User;
import User.User.STARS;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gudesigns.climber.GameLoader;

public class AdventureTrackButton {

	public static final Button create(GameLoader gameLoader, JSONTrack track) {

		Skin skin = Skins.loadDefault(gameLoader, 1);
		String indexTxt = Integer.toString(track.getIndex());
		User user = User.getInstance();
		
		Button base =null;
		
		if(track.getType()==TrackType.FORREST){
			base = new Button(skin, "adventureTrack_forrest");
		} else if (track.getType()==TrackType.ARTIC) {
			base = new Button(skin, "adventureTrack_artic");
		}
 
		Table content = new Table(skin);
		content.setBackground("white");

		Image stars = new Image();
		float starWidth = Globals.baseSize*3/5;
		

		if (user.getStars(track.getObjectId()) == STARS.NONE) {

			System.out.println("AdventureTrackButton: none star");
		} else if (user.getStars(track.getObjectId()) == STARS.ONE) {
			stars = new Image((new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/images/one_star.png")))));
			
			starWidth*=1;
		} else if (user.getStars(track.getObjectId()) == STARS.TWO) {
			stars = new Image((new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/images/two_stars.png")))));
			
			starWidth*=2;

		} else if (user.getStars(track.getObjectId()) == STARS.THREE) {
			stars = new Image(
					(new TextureRegionDrawable(
							new TextureRegion(
									gameLoader.Assets
											.getFilteredTexture("menu/images/three_stars.png")))));
			
			starWidth*=3;
		}

		content.add(stars).top().padBottom(15).height(Globals.baseSize*1/2).width(starWidth);
		content.row();

		Label index = new Label("title", skin);
		index.setText(indexTxt);

		content.add(index);

		base.add(content).width(Globals.baseSize * 3)
				.height(Globals.baseSize * 3);

		return base;
	}

}
