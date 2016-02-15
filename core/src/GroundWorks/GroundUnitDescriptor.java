package GroundWorks;

import wrapper.CameraManager;
import Shader.GameMesh;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GroundUnitDescriptor {
	private Vector2 start, end;
	private Fixture fixture;
	private boolean fixtureDeleted = true;
	private Sprite graphic = null, shadowGraphic;

	// ShapeRenderer renderer = new ShapeRenderer();
	private int vertexId = 0;
	
	public GroundUnitDescriptor(Vector2 start, Vector2 end, boolean initFiller) {
		this.start = start;
		this.end = end;
		
		//initGraphic();
		if(initFiller)
			initGraphicFiller();
	}
	
	public int getVertexId(){
		return vertexId;
	}

	public Vector2 getStart() {
		return start;
	}

	public Vector2 getEnd() {
		return end;
	}

	private void initGraphicFiller() {

		vertexId = GameMesh.addPoint( start.x, start.y);

	}

	private void drawGraphicFiller(CameraManager cam, ShaderProgram shader) {

	}

	public void drawShadow(SpriteBatch batch){
		this.shadowGraphic.draw(batch);
	}

	public void drawShapes(CameraManager cam, ShaderProgram shader) {
		drawGraphicFiller(cam, shader);
	}

	public void deleteUnit(Body floor) {
		floor.destroyFixture(fixture);
		fixtureDeleted = true;
		if (graphic != null) {
			// texture.dispose();
			graphic = null;
			// texture = null;
		}
	}

	public void setFixture(Fixture drawEdge) {
		this.fixture = drawEdge;
		fixtureDeleted = false;
	}

	public boolean isFixtureDeleted() {
		return fixtureDeleted;
	}

	public Sprite getGraphic() {
		return graphic;
	}
	
	
}