#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_progress;   // 0..1 over the whole effect
uniform float u_time;       // seconds since the effect started
uniform vec2 u_center;      // clock centre in texture space (0..1)
uniform float u_aspect;     // screen width / height
uniform vec2 u_texMin;      // region uv minimum
uniform vec2 u_texMax;      // region uv maximum

const float PI = 3.14159265;
const float TWO_PI = 6.2831853;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

// glowing clock hand: a ray from the centre at angle [ha], length [len]
float clockHand(vec2 rel, float ha, float len, float w) {
    vec2 hd = vec2(cos(ha), sin(ha));
    float proj = dot(rel, hd);
    float perp = abs(rel.x * hd.y - rel.y * hd.x);
    float lenMask = smoothstep(len, len * 0.55, proj) * step(0.0, proj);
    return exp(-perp * perp / (2.0 * w * w)) * lenMask;
}

void main() {
    vec2 span = u_texMax - u_texMin;
    vec2 nrm = (v_texCoords - u_texMin) / span;

    float p = u_progress;
    float pulse = sin(p * PI);                 // 0 -> 1 -> 0, magic appears then fades

    // aspect-corrected polar space around the player
    vec2 ar = vec2(u_aspect, 1.0);
    vec2 rel = (nrm - u_center) * ar;
    float r = length(rel);
    float a = atan(rel.y, rel.x);

    // --- swirl distortion of the world: keeps spinning (with the clock) -----
    float ang = u_time * 2.2 * (1.0 - smoothstep(0.0, 0.6, r));
    float c = cos(ang);
    float s = sin(ang);
    vec2 rr = vec2(rel.x * c - rel.y * s, rel.x * s + rel.y * c) / ar;
    vec2 uvNrm = u_center + rr;
    vec2 dir = normalize(nrm - u_center + 1e-5);
    float ab = 0.006 * p;
    vec3 col;
    col.r = texture2D(u_texture, clamp(u_texMin + (uvNrm + dir * ab) * span, u_texMin, u_texMax)).r;
    col.g = texture2D(u_texture, clamp(u_texMin + uvNrm * span, u_texMin, u_texMax)).g;
    col.b = texture2D(u_texture, clamp(u_texMin + (uvNrm - dir * ab) * span, u_texMin, u_texMax)).b;

    // --- magical clock overlay ----------------------------------------------
    vec3 glow = vec3(0.0);
    vec3 gold = vec3(1.0, 0.8, 0.35);
    vec3 arcane = vec3(0.5, 0.8, 1.3);

    // two hands sweeping backwards (time rewinding)
    float hourAng = u_time * 1.4 + p * TWO_PI;
    float minAng = u_time * 4.2 + p * TWO_PI * 3.0;
    glow += clockHand(rel, hourAng, 0.42, 0.018) * gold;
    glow += clockHand(rel, minAng, 0.62, 0.012) * gold * 0.9;

    // glowing hub at the centre
    glow += exp(-r * r * 240.0) * gold * 1.2;

    // 12 hour ticks on a ring
    float twelfth = TWO_PI / 12.0;
    float nearestTick = floor((a + PI) / twelfth + 0.5) * twelfth - PI;
    float dA = abs(mod(a - nearestTick + PI, TWO_PI) - PI);
    float tick = exp(-dA * dA / (2.0 * 0.04 * 0.04)) * exp(-pow(r - 0.5, 2.0) / (2.0 * 0.018 * 0.018));
    glow += tick * gold;

    // pulsing concentric time rings expanding outward
    float ring = 0.5 + 0.5 * sin(r * 42.0 - u_time * 4.0 - p * 24.0);
    ring = pow(ring, 10.0) * smoothstep(0.85, 0.0, r);
    glow += ring * arcane * 0.6;

    col += glow * pulse;

    // --- grain --------------------------------------------------------------
    col += (hash(nrm * 800.0 + u_time) - 0.5) * 0.06 * p;

    // --- desaturate, then resolve to gray for a clean hand-off --------------
    float gray = dot(col, vec3(0.299, 0.587, 0.114));
    col = mix(col, vec3(gray), p * 0.7);
    col = mix(col, vec3(0.5), p * p);

    // --- soft vignette around the player ------------------------------------
    float vig = smoothstep(1.1, 0.3, r);
    col *= mix(1.0, vig, p * 0.4);

    gl_FragColor = vec4(col, 1.0) * v_color;
}
