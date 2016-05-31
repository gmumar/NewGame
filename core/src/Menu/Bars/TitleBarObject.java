package Menu.Bars;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public 	class TitleBarObject {
	Label baseMoney;
	Label animationMoney;
	
	public TitleBarObject(Label baseMoney, Label animationMoney) {
		super();
		this.baseMoney = baseMoney;
		this.animationMoney = animationMoney;
	}

	public Label getBaseMoney() {
		return baseMoney;
	}

	public Label getAnimationMoney() {
		return animationMoney;
	}
	
	
	
	
}