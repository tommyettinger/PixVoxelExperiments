using System;
using System.Collections.Generic;

using OpenTK;

namespace Experiments
{    
    public class Rect : Shape
    {
        
        private const int White = (int)((0xff << 24) | 0xffffff);
        
        public Rect()
        {
            Vertices = new Vector3[]
            {
                new Vector3(-1.0f, -1.0f,  0f),
                new Vector3( 1.0f, -1.0f,  0f),
                new Vector3( 1.0f,  1.0f,  0f),
                new Vector3(-1.0f,  1.0f,  0f)
            };

            Normals = new Vector3[]
            {
                new Vector3(-1.0f, -1.0f,  1.0f),
                new Vector3( 1.0f, -1.0f,  1.0f),
                new Vector3( 1.0f,  1.0f,  1.0f),
                new Vector3(-1.0f,  1.0f,  1.0f),
            };
            Indices = new int[]
            {
                // front face
                0, 1, 2, 2, 3, 0,
            };
            Colors = new int[]
            {
                White, White, White, White
            };
            Texcoords = new Vector2[4];

        }
    }
}