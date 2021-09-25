#version 330 core
in vec3 zOut;
out vec4 FragColor;
void main()
{
        FragColor = vec4(exp(zOut.x), exp(zOut.y), exp(zOut.x*zOut.y), 0.f);
}