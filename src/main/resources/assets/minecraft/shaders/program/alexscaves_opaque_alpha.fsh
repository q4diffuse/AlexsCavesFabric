#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

out vec4 fragColor;

// The target already holds colour * alpha from the additive draw; forcing alpha to 1 makes the
// SRC_ALPHA/ONE composite add exactly that, as the original in-world draw did.
void main() {
    fragColor = vec4(texture(DiffuseSampler, texCoord).rgb, 1.0);
}
