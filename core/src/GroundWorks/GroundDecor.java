package GroundWorks;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GameState;
import Component.ComponentBuilder.ComponentNames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GroundDecor {
	
	GameState gameState;
	ArrayList<BaseActor> decorations;

	public GroundDecor( GameState gameState) {
		decorations = new ArrayList<BaseActor>();
		this.gameState = gameState;
	}

	public void addChequeredFlag(ArrayList<GroundUnitDescriptor> preMadeMapList) {
		
		GroundUnitDescriptor lastPos = preMadeMapList.get(preMadeMapList.size()-1);
		
		BaseActor flag = new BaseActor(ComponentNames._CEQUEREDFLAG_.name(), "chequered_flag.png", gameState);
		flag.setSensor();
		flag.setBodyType(BodyType.StaticBody);
		flag.setPosition(lastPos.end.x, (lastPos.end.y + lastPos.start.y)/2 + 3);
		
		decorations.add(flag);
	}

	public void draw(SpriteBatch batch) {
		
		Iterator<BaseActor> iter = decorations.iterator();
		
		while(iter.hasNext()){
			BaseActor decoration = iter.next();
			decoration.draw(batch);
		}
		
	}

}
