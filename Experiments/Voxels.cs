using System;
using System.Collections.Generic;
using System.Linq;
using OpenTK;
using System.IO;

namespace Experiments
{
    public struct Voxel
    {
        public byte x;
        public byte y;
        public byte z;
        public byte color;
    }
    public class Voxels : Shape
    {
        
        private const uint White = 0xffffffff;

        public Voxel[] readBVX(string filename)
        {
            int size = 40;
            List<Voxel> voxels = new List<Voxel>(size * size * size);
            if (File.Exists(filename))
            {
                using (BinaryReader reader = new BinaryReader(File.Open(filename, FileMode.Open)))
                {
                    int total = (int)(reader.BaseStream.Length);
                    size = (int)(Math.Round(Math.Pow(total, 1.0 / 3.0)));
                    byte[] bins = reader.ReadBytes(total);
                    for (byte z = 0; z < size; z++)
                    {
                        for (byte y = 0; y < size; y++)
                        {
                            for (byte x = 0; x < size; x++)
                            {
                                if (bins[z * size * size + y * size + x] != 255)
                                    voxels.Add(new Voxel { x = x, y = y, z = z, color = (byte)(253 - 4 * bins[z * size * size + y * size + x]) });
                            }
                        }
                    }
                }
            }
            return voxels.OrderBy(v => v.x * 64 - v.y + v.z * 64 * 128 - ((v.color == 253 - 100) ? 64 * 128 * 64 : 0)).ToArray();
        }
        public Voxels(string filename, int palette)
        {
            Voxel[] voxels = readBVX(filename);
            Vertices = new Vector3[ 4 * voxels.Length];
            for(int c = 0; c < voxels.Length; c++)
            {
                int vx = (voxels[c].x + voxels[c].y) * 2;
                int vy = - voxels[c].x  + voxels[c].y + voxels[c].z * 3;
                Vertices[c * 4 + 0] = new Vector3(vx - 1.0f, vy - 1.0f, 0f);
                Vertices[c * 4 + 1] = new Vector3(vx + 1.0f, vy - 1.0f, 0f);
                Vertices[c * 4 + 2] = new Vector3(vx + 1.0f, vy + 1.0f, 0f);
                Vertices[c * 4 + 3] = new Vector3(vx - 1.0f, vx + 1.0f, 0f);
            };
            
            Normals = new Vector3[ 4 * voxels.Length];
            for (int c = 0; c < 4 * voxels.Length; c++)
            {
                Normals[c] = new Vector3(0f, 0f, 1f);
            };


            int i = 0;
            Indices = new int[6 * voxels.Length];
            for (int c = 0; c < voxels.Length; c += 4)
            {
                Indices[i++] = c;
                Indices[i++] = c + 2;
                Indices[i++] = c + 1;

                Indices[i++] = c + 1;
                Indices[i++] = c + 2;
                Indices[i++] = c + 3;
            }


            Colors = new uint[4 * voxels.Length];
            Colors.Fill(White);
            /*
top is    "(1.0 / 1024.0) * (5 * row)" and bottom is "(1.0 / 1024.0) * (5 * row + 4)"
sides are "(1.0 / 1024.0) * (4 * column)" and        "(1.0 / 1024.0) * (4 * column + 4)"
             */
            Texcoords = new Vector2[4 * voxels.Length];
            for (int c = 0; c < voxels.Length; c++)
            {
                Texcoords[c * 4 + 0] = new Vector2(0.0009765625f * (4 * voxels[c].color), 0.0009765625f * (5 * palette));//-1.0f, -1.0f
                Texcoords[c * 4 + 1] = new Vector2(0.0009765625f * (4 * voxels[c].color + 4), 0.0009765625f * (5 * palette));//1.0f, -1.0f
                Texcoords[c * 4 + 2] = new Vector2(0.0009765625f * (4 * voxels[c].color + 4), 0.0009765625f * (5 * palette + 4));//1.0f, 1.0f
                Texcoords[c * 4 + 3] = new Vector2(0.0009765625f * (4 * voxels[c].color), 0.0009765625f * (5 * palette + 4));//-1.0f, 1.0f
            };
        }
    }
}