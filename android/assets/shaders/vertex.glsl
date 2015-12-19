//our attributes
attribute vec2 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

//our camera matrix
uniform mat4 u_projTrans;
varying vec2 v_texCoords;

//send the color out to the fragment shader
varying vec4 vColor;

void main() {
    vColor = a_color;
    v_texCoords = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position.xy, 0.0, 1.0);
    
}