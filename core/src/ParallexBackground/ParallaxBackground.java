package ParallexBackground;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBackground {
   
   private ParallaxLayer[] layers;
   private Camera camera;
   private SpriteBatch batch;
   
   /**
    * @param layers  The  background layers 
    * @param width   The screenWith 
    * @param height The screenHeight
    */
   public ParallaxBackground(ParallaxLayer[] layers,float width,float height){
      this.layers = layers;
      camera = new OrthographicCamera(width, height);
      batch = new SpriteBatch();
   }
   
   /**
    * @param speed A Vector2 attribute to point out the x and y speed
    * 
    */
   public void render(Vector2 speed){
      this.camera.position.add(speed.x/100,speed.y/100, 0);
      batch.begin();
      for(ParallaxLayer layer:layers){
         batch.setProjectionMatrix(camera.projection);
        
         float currentX = - camera.position.x*layer.parallaxRatio.x % ( layer.region.getRegionWidth() + layer.padding.x) ;
         
         if( speed.x < 0 )currentX += -( layer.region.getRegionWidth() + layer.padding.x);
         do{
            float currentY = - camera.position.y*layer.parallaxRatio.y % ( layer.region.getRegionHeight() + layer.padding.y) ;
            if( speed.y < 0 )currentY += - (layer.region.getRegionHeight()+layer.padding.y);
            do{
               batch.draw(layer.region,
                     -this.camera.viewportWidth/2+currentX + layer.startPosition.x ,
                     -this.camera.viewportHeight/2 + currentY +layer.startPosition.y);
               currentY += ( layer.region.getRegionHeight() + layer.padding.y );
            }while( currentY < camera.viewportHeight);
            currentX += ( layer.region.getRegionWidth()+ layer.padding.x);
         }while( currentX < camera.viewportWidth);
         
      }
      batch.end();
   }
}