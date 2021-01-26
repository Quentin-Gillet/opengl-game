#version 400 core

in vec3 position;
in vec3 normal;
in vec2 textureCoords;

out vec2 out_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition[4];
uniform float useFog;
uniform vec4 plane;

const float density = 0.0025;
const float gradient = 5.0;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;

    gl_ClipDistance[0] = dot(worldPosition, plane);

    gl_Position = projectionMatrix * positionRelativeToCamera;
    out_textureCoords = textureCoords;

    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    for(int i = 0; i < 4; i++){
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }

    vec4 cameraPostion = inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0);
    toCameraVector = cameraPostion.xyz - worldPosition.xyz;

    if(useFog == 1){
        float distance = length(positionRelativeToCamera.xyz);
        visibility = exp(-pow((distance * density), gradient));
        visibility = clamp(visibility, 0.0, 1.0);
    }else{
        visibility = 1.0;
    }
}
