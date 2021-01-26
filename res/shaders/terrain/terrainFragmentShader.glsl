#version 400

in vec2 out_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_colour;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;

uniform vec3 skyColour;

void main() {
    vec4 blendMapColour = texture(blendMap, out_textureCoords);
    float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g, blendMapColour.b);
    vec2 tiledCoords = out_textureCoords * 40.0;
    vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitCameraVector = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for(int i = 0; i < 4; i++){
        float distance = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot = dot(unitNormal, unitLightVector);
        float brightness = max(nDot, 0.0);

        vec3 lightDirection = -unitLightVector;
        vec3 reflectLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectLightDirection, unitCameraVector);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attenuationFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attenuationFactor;
    }

    totalDiffuse = max(totalDiffuse, 0.2);

    out_colour = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
    out_colour = mix(vec4(skyColour, 1.0), out_colour, visibility);
}
