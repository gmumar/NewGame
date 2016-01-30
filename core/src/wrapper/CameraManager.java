package wrapper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraManager extends OrthographicCamera {
	
	private float max_length = 0;

	public CameraManager(int screenWidth, int screenHeight) {
		super(screenWidth,screenHeight);
		
		Vector2 tmp = new Vector2(0,0);
		max_length = tmp.dst(screenWidth,screenHeight);
		
	}
	
	public float getViewPortRightEdge(){
		float positionX = this.position.x;
		float edgePosition = positionX + this.viewportWidth/2;
		return edgePosition;
	}
	
	public float getViewPortLeftEdge(){
		float positionX = this.position.x;
		float edgePosition = positionX - this.viewportWidth/2;
		return edgePosition;
	}
	
	public boolean pointOnCamera(Vector2 pnt){
		float dst = pnt.dst(this.position.x, this.position.y);
		if(dst<max_length){
			return true;
		}
		
		return false;
	}

}
