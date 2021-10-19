#version 330 core

//in vec3 Normal;
//in vec3 Color;
//in vec3 FragPos;
in vec3 aPos;

//uniform vec3 lightPos;

out vec4 FragColor;
void main()
{
    //float ambientStrength = 0.4;
    //vec3 ambient = ambientStrength * vec3(1.0f);

    //vec3 norm = normalize(Normal);
    //vec3 lightDir = normalize(lightPos - FragPos);
    //float diff = max(dot(norm, lightDir), 0.0);
    //vec3 diffuse = diff * vec3(1.0f);
    //vec3 result = (ambient + diffuse) * Color;
    //if (abs(aPos.x) - 0.48f > 0.01f && abs(aPos.y) - 0.48f > 0.01f )
     //   result = vec3(0.f);
    //if (abs(aPos.z) - 0.48f > 0.01f && abs(aPos.y) - 0.48f > 0.01f )
      //      result = vec3(0.f);
    //if (abs(aPos.x) - 0.48f > 0.01f && abs(aPos.z) - 0.48f > 0.01f )
      //      result = vec3(0.f);
    FragColor = vec4(1.0, 1.0, 1.0, 1.0);
}