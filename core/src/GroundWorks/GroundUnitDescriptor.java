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
	
	/*public GroundUnitDescriptor(Vector2 start, Vector2 end, String image) {
		this.start = start;
		this.end = end;


		initGraphic();
		//initGraphicFiller();
	}*/

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

	//private void initGraphic() {

		//this.texture = TextureLibrary.getTexture(textureName);
		//this.graphic = new Sprite(texture);

		/*this.graphic.setSize(Globals.PixelToMeters(texture.getWidth()),
				Globals.PixelToMeters(texture.getHeight()));
		this.graphic.setOrigin(0, graphic.getHeight() / 2);

		this.graphic.setPosition(end.x, end.y-0.2f);

		if (start.y == 0) {
			start.y = 0;
		}
		double baseRadians = Math.atan2(end.y - start.y, end.x - start.x);
		if (start.y < 0) {
			baseRadians += Math.PI;
		} // Adjust for -Y
		this.graphic
				.setRotation((float) (baseRadians * MathUtils.radiansToDegrees));
		this.graphic.setScale(start.dst(end) / 3, -2);*/
		
		

		
		/*this.shadowTexture = TextureLibrary.getTexture("temp_ground_shadow.png");
		this.shadowGraphic = new Sprite(shadowTexture);

		this.shadowGraphic.setSize(Globals.PixelToMeters(shadowGraphic.getWidth()),
				Globals.PixelToMeters(shadowGraphic.getHeight()));
		this.shadowGraphic.setOrigin(0, shadowGraphic.getHeight() / 2);

		this.shadowGraphic.setPosition(end.x, end.y-0.5f);

		this.shadowGraphic
				.setRotation((float) (baseRadians * MathUtils.radiansToDegrees));
		this.shadowGraphic.setScale(start.dst(end) / 3.2f, -2);*/
	//}

	private void initGraphicFiller() {

	

		//vertexId = GameMesh.addTriangle(start.x, start.y, end.x, end.y, end.x,
		//		end.y - 30, Globals.BROWN1);
		//vertexId = GameMesh.addTriangle(start.x, start.y - 30, end.x,
		//		end.y - 30, start.x, start.y, Globals.BROWN);
		
		
		//vertexId = GameMesh.addLine( start.x, start.y,start.x, start.y-30f, Globals.BROWN);
		vertexId = GameMesh.addPoint( start.x, start.y);
		//vertexId = GameMesh.addLine(end.x, end.y-30, end.x, end.y, Globals.BROWN);
		
		// vertexId = GameMesh.addSquare(start.x, start.y, start.x,
		// start.y-15,end.x, end.y,end.x, end.y-15, Globals.BROWN);

	}

	private void drawGraphicFiller(CameraManager cam, ShaderProgram shader) {

	}

	public void draw(SpriteBatch batch) {

		
		//this.graphic.draw(batch);
		

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