#version 400

in vec2 pass_textureCoords;

out vec4 out_color;

uniform vec3 colour;
uniform sampler2D fontAtlas;

void main() {
    out_color = vec4(colour, texture(fontAtlas, pass_textureCoords).a);
}
