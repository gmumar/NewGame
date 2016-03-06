package GroundWorks;

import java.util.ArrayList;

import wrapper.BaseActor;
import wrapper.GamePhysicalState;
import Component.ComponentNames;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GroundDecor {
	
	private GamePhysicalState gameState;
	private ArrayList<BaseActor> decorations;

	public GroundDecor( GamePhysicalState gameState) {
		decorations = new ArrayList<BaseActor>();
		this.gameState = gameState;
	}

	public void addChequeredFlag(ArrayList<GroundUnitDescriptor> preMadeMapList) {
		
		GroundUnitDescriptor lastPos = preMadeMapList.get(preMadeMapList.size()-1);
		JSONComponentName name = new JSONComponentName();
		
		name.setBaseName(ComponentNames.CEQUEREDFLAG);
		
		BaseActor flag = new BaseActor(name, "chequered_flag.png", gameState);
		flag.setSensor();
		flag.setBodyType(BodyType.StaticBody);
		flag.setPosition(lastPos.getEnd().x, (lastPos.getEnd().y + lastPos.getStart().y)/2 + 3);
		
		decorations.add(flag);
	}

	public void draw(SpriteBatch batch) {
		
		for (BaseActor decoration : decorations){
			decoration.draw(batch);
		}
		
	}

}
