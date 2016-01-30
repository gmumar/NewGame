package GroundWorks;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GameState;
import Component.ComponentBuilder.ComponentNames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GroundDecor {
	
	private GameState gameState;
	private ArrayList<BaseActor> decorations;
	
	private BaseActor decoration ;
	private Iterator<BaseActor> iter;

	public GroundDecor( GameState gameState) {
		decorations = new ArrayList<BaseActor>();
		this.gameState = gameState;
	}

	public void addChequeredFlag(ArrayList<GroundUnitDescriptor> preMadeMapList) {
		
		GroundUnitDescriptor lastPos = preMadeMapList.get(preMadeMapList.size()-1);
		
		BaseActor flag = new BaseActor(ComponentNames._CEQUEREDFLAG_.name(), "chequered_flag.png", gameState);
		flag.setSensor();
		flag.setBodyType(BodyType.StaticBody);
		flag.setPosition(lastPos.getEnd().x, (lastPos.getEnd().y + lastPos.getStart().y)/2 + 3);
		
		decorations.add(flag);
	}

	public void draw(SpriteBatch batch) {
		
		iter = decorations.iterator();
		
		while(iter.hasNext()){
			decoration = iter.next();
			decoration.draw(batch);
		}
		
	}

}
