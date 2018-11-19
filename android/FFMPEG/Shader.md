Shader编写
```
vertex_shader.glsl

attribute vec4 av_Position;
attribute vec2 af_Position;
varying vec2 v_texPosition;
void main() {
    v_texPosition = af_Position;
    gl_Position = av_Position;
}

注： attribute 只能在vertex中使用
        varying 用于vertex和fragment之间传递值



fragment_shader.glsl

precision mediump float;
varying vec2 v_texPosition;
uniform sampler2D sampler_y;
uniform sampler2D sampler_u;
uniform sampler2D sampler_v;
void main() {
    float y,u,v;
    y = texture2D(sampler_y,v_texPosition).r;
    u = texture2D(sampler_u,v_texPosition).r- 0.5;
    v = texture2D(sampler_v,v_texPosition).r- 0.5;

    vec3 rgb;
    rgb.r = y + 1.403 * v;
    rgb.g = y - 0.344 * u - 0.714 * v;
    rgb.b = y + 1.770 * u;

    gl_FragColor = vec4(rgb,1);
}
注： uniform 用于在application中向vertex和fragment中传递值。




```


