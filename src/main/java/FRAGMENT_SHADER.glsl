#version 330 core
in vec4 res;
out vec4 FragColor;
void main()
{
        FragColor = vec4(res.x, res.y, res.z, 0.0f);
}