#version 330 core

in vec3 Normal;
in vec3 Color;
in vec3 FragPos;
in vec3 aPos;

uniform vec3 lightPos;

out vec4 FragColor;
void main()
{
    float ambientStrength = 1.0;
    vec3 ambient = ambientStrength * vec3(1.0f);

    vec3 norm = normalize(Normal).xyz;
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * vec3(1.0f);
    vec3 result = (ambient + diffuse) * Color;
   // if (mod(aPos.x,  0.5f) < 0.02f && mod(aPos.y,  0.5f) < 0.02f )
    //    result = vec3(0.f);
   // if (mod(aPos.z,  0.5f) < 0.02f && mod(aPos.y,  0.5f) < 0.02f )
    //    result = vec3(0.f);
   // if (mod(aPos.x,  0.5f) < 0.02f && mod(aPos.z,  0.5f) < 0.02f )
    //    result = vec3(0.f);
    FragColor = vec4(result, 1.0);
}