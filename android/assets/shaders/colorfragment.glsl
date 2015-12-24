#ifdef GL_ES
 #define LOWP lowp
 precision mediump float;
 #else
 #define LOWP
 #endif
//input from vertex shader
varying LOWP vec4 vColor;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
	//if(vColor.w != 0.0){
   	 	gl_FragColor = vColor ;//* texture2D(u_texture, v_texCoords);
    //}else{
    //	 gl_FragColor = texture2D(u_texture, v_texCoords);
    //}
}