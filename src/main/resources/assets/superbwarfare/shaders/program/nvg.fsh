#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

float random(vec2 st) {
    return fract(sin(dot(st, vec2(12.9898, 78.233))) * 43758.5453123);
}

float luma(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec3 scene = texture(DiffuseSampler, texCoord).rgb;
    float l = luma(scene);

    // Усилитель: тёмное вытягивается, яркое упирается в потолок трубки.
    float gain = pow(clamp(l * 1.6, 0.0, 1.0), 0.6);

    // Засветка от ярких источников: размытая яркость соседей добавляет ореол.
    vec2 px = 1.0 / OutSize;
    float halo = 0.0;
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            halo += luma(texture(DiffuseSampler, texCoord + vec2(x, y) * px * 3.0).rgb);
        }
    }
    halo /= 25.0;
    gain += smoothstep(0.55, 1.0, halo) * 0.6;

    // Зерно сильнее в темноте: трубка шумит, где нечего усиливать.
    float grain = random(floor(texCoord * OutSize * 0.5) + fract(Time * 60.0)) - 0.5;
    gain += grain * mix(0.18, 0.04, gain);

    gain = clamp(gain, 0.0, 1.0);

    // Фосфор P43: тень уходит в тёмно-зелёный, пересвет — в бело-зелёный.
    vec3 phosphor = mix(vec3(0.02, 0.08, 0.02), vec3(0.35, 1.0, 0.45), gain);
    phosphor = mix(phosphor, vec3(0.85, 1.0, 0.85), smoothstep(0.85, 1.0, gain));

    // Круглая виньетка монокуляра.
    vec2 uv = texCoord * (1.0 - texCoord.yx);
    float vig = pow(clamp(uv.x * uv.y * 25.0, 0.0, 1.0), 0.45);

    fragColor = vec4(phosphor * vig, 1.0);
}
