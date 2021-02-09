#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 fromLightVector;
in float visibility;

out vec4 out_Colour;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;

uniform vec3 lightColour;
uniform float moveFactor;

uniform vec3 skyColour;

const float waveStrength = 0.008;
const float shineDamper = 20.0;
const float reflectivity = 0.6;

void main(void) {
	vec2 ndc = (clipSpace.xy / clipSpace.w) / 2.0 + 0.5f;
	vec2 reflectionTextureCoords = vec2(ndc.x, -ndc.y);
	vec2 refractionTextureCoords = vec2(ndc.x, ndc.y);

	float near = 0.1;
	float far = 1000.0;
	float depth = texture(depthMap, refractionTextureCoords).r;
	float floorDistance = 2 * near * far / (far + near - (2 * depth - 1) * (far - near));

	depth = gl_FragCoord.z;
	float waterDistance = 2 * near * far / (far + near - (2 * depth - 1) * (far - near));
	float waterDepth = floorDistance - waterDistance;

	vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y + moveFactor);
	vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 20, 0, 1);

	refractionTextureCoords += totalDistortion;
	refractionTextureCoords= clamp(refractionTextureCoords, 0.001, 0.999);

	reflectionTextureCoords += totalDistortion;
	reflectionTextureCoords.x = clamp(reflectionTextureCoords.x, 0.001, 0.999);
	reflectionTextureCoords.y = clamp(reflectionTextureCoords.y, -0.999, -0.001);

	vec4 reflectColour = texture(reflectionTexture, reflectionTextureCoords);
	vec4 refractColour = texture(refractionTexture, refractionTextureCoords);

	vec4 normalMapColour = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColour.r * 2 - 1, normalMapColour.b * 3, normalMapColour.g * 2 - 1);
	normal = normalize(normal);

	vec3 viewVector = normalize(toCameraVector);
	float refractiveFactor = dot(viewVector, normal);

	vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
	float specular = max(dot(reflectedLight, viewVector), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlights = lightColour * specular * reflectivity * clamp(waterDepth / 5, 0, 1);

	out_Colour = mix(reflectColour, refractColour, refractiveFactor);
	out_Colour = mix(out_Colour, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0);
	out_Colour.a = clamp(waterDepth / 5, 0, 1);

}