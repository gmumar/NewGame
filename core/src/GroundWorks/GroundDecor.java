package GroundWorks;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import Component.ComponentBuilder.ComponentNames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class GroundDecor {
	
	World world;
	ArrayList<BaseActor> decorations;

	public GroundDecor(World world) {
		this.world = world;
		decorations = new ArrayList<BaseActor>();
	}

	public void addChequeredFlag(ArrayList<GroundUnitDescriptor> preMadeMapList) {
		
		GroundUnitDescriptor lastPos = preMadeMapList.get(preMadeMapList.size()-1);
		
		BaseActor flag = new BaseActor(ComponentNames._CEQUEREDFLAG_.name(), "chequered_flag.png", world);
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
