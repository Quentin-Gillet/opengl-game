#version 400

in vec3 textureCoords;

out vec4 out_color;

uniform samplerCube cubeMap;
uniform samplerCube cubeMapNight;
uniform float blendFactor;
uniform vec3 fogColour;

const float lowerLimit = 0.0;
const float upperLimit = 30.0;

void main() {
    vec4 dayTexture = texture(cubeMap, textureCoords);
    vec4 nightTexture = texture(cubeMapNight, textureCoords);
    vec4 finalColour = mix(dayTexture, nightTexture, blendFactor);

    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0);
    out_color = mix(vec4(fogColour, 1.0), finalColour, factor);
}
