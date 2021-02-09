#version 400 core

in vec2 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;
out vec3 fromLightVector;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform vec3 lightPosition;
uniform float useFog;

const float density = 0.0025;
const float gradient = 5.0;
const float tiling = 6;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	toCameraVector = cameraPosition - worldPosition.xyz;
	fromLightVector = worldPosition.xyz - lightPosition;
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;

	clipSpace = projectionMatrix * positionRelativeToCamera;
	gl_Position = clipSpace;
	textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling;

	if(useFog == 1){
		float distance = length(positionRelativeToCamera.xyz);
		visibility = exp(-pow((distance * density), gradient));
		visibility = clamp(visibility, 0.0, 1.0);
	}else{
		visibility = 1.0;
	}
}