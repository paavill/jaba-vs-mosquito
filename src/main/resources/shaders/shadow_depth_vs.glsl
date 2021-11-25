#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aPos1;
layout (location = 2) in vec3 aPos2;
layout (location = 3) in vec3 aPos3;

uniform mat4 model;

void main()
{
    gl_Position = model * vec4(aPos, 1.0);
}