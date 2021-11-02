#version 330 core

in vec3 Normal;
in vec3 Color;
in vec3 FragPos;
in vec3 aPos;
in vec2 TexCoord;

uniform vec3 lightPos;
uniform sampler2D ourTexture;

out vec4 FragColor;
void main()
{
    float ambientStrength = 2.0;
    vec3 ambient = ambientStrength * vec3(1.0f);

    vec3 norm = normalize(Normal).xyz;
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * vec3(1.0f);
    vec3 result = (ambient + diffuse) * Color;
   // if (mod(aPos.first,  0.5f) < 0.02f && mod(aPos.second,  0.5f) < 0.02f )
    //    result = vec3(0.f);
   // if (mod(aPos.z,  0.5f) < 0.02f && mod(aPos.second,  0.5f) < 0.02f )
    //    result = vec3(0.f);
   // if (mod(aPos.first,  0.5f) < 0.02f && mod(aPos.z,  0.5f) < 0.02f )
    //    result = vec3(0.f);
    FragColor = texture(ourTexture, TexCoord)* vec4(result, 1.0);
}