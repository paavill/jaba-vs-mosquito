#version 330 core

in vec3 Normal;
in vec3 Color;
in vec3 FragPos;
in vec3 aPos;
in vec2 TexCoord;

uniform sampler2D ourTexture;
uniform samplerCubeShadow depthMap;

uniform vec3 lightPos;
uniform vec3 viewPos;

uniform float far_plane;

out vec4 FragColor;

float ShadowCalculation(vec3 fragPos)
{
    vec3 fragToLight = fragPos - lightPos;

    float closestDepth = texture(depthMap, vec4(fragToLight, 1.0f));

    closestDepth *= far_plane;

    //FragColor = vec4(vec3(closestDepth / far_plane), 1.0);

    float currentDepth = length(fragToLight);
    float bias = 0.05;
    float shadow = currentDepth -  bias > closestDepth ? 2.0 : 0.0;

    return shadow;
}

void main()
{
    vec3 color = texture(ourTexture, TexCoord).rgb;
    float ambientStrength = 1.0;
    vec3 ambient = ambientStrength * vec3(1.0f);
    vec3 norm = normalize(Normal);
    vec3 lightColor = vec3(2.0);

    vec3 lightDir = normalize(lightPos - FragPos);

    float distance = length(lightPos - FragPos);

    float attenuation = 1.0 / (1.0f + 0.0002f * distance +
            		    0.007f * (distance * distance));

    float diff = max(dot(norm, lightDir), 0.0)*attenuation;
    vec3 diffuse = diff * lightColor;

    float shadow = ShadowCalculation(FragPos)*attenuation;

    vec3 result = (ambient + (1.0 - shadow) * (diffuse)) * color;


    FragColor = vec4(result, 1.0);
}

//добавить названия для коэффициентов
// if (mod(aPos.first,  0.5f) < 0.02f && mod(aPos.second,  0.5f) < 0.02f )
//    result = vec3(0.f);
// if (mod(aPos.z,  0.5f) < 0.02f && mod(aPos.second,  0.5f) < 0.02f )
//    result = vec3(0.f);
// if (mod(aPos.first,  0.5f) < 0.02f && mod(aPos.z,  0.5f) < 0.02f )
//    result = vec3(0.f);