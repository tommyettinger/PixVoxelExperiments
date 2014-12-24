package pixvoxel.experiments;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

/**
 * Created by Tommy Ettinger on 12/21/2014.
 */
public class Model
{
    public static HashMap<String, Texture> textures = new HashMap<String, Texture>(4 * 6);
    public String name;
    public int x, y, z, facing, cubeSize;
    public double damageRate;
    public Model()
    {
        name = "default";
        x = 0;
        y = 0;
        z = 0;
        cubeSize = 0;
        facing = 0;
        damageRate = 0;
    }
    public Model(String name, short[][][] model, int x, int y, int z, int facing)
    {
        this.name = name + facing + "-0";
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.cubeSize = model.length;
        damageRate = 0;
        if(!textures.containsKey(this.name))
        {
            textures.put(this.name, render(new ModelDisplay(model)));
        }
    }
    public Model(String name, short[][][] model, int x, int y, int z, int facing, double damage)
    {
        this.name = name + facing + "-" + damage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.cubeSize = model.length;
        damageRate = damage;
        if(!textures.containsKey(this.name))
        {
            textures.put(this.name, render(new ModelDisplay(model, damage)));
        }
    }
    public static Pixmap img = null;
    public Texture render(ModelDisplay md)
    {
        //short[][] current_edges = md.edges[facing];
        int screenxsize = cubeSize * 4 + 8;
        int screenysize = cubeSize * 5 + 8;
        short[][] colorBuffer = new short[screenxsize][screenysize], zbuffer  = new short[screenxsize][screenysize];
        for(int i = 0; i < screenxsize; i++)
        {
            for(int j = 0; j < screenysize; j++)
            {
                zbuffer[i][j] = -9999;
                colorBuffer[i][j] = 255;
            }
        }
        Pixmap p = new Pixmap(screenxsize, screenysize, Pixmap.Format.RGBA8888);

        for (int z = 0; z < cubeSize; z++) {
            for(int y = cubeSize - 1; y >= 0; y--) {
            for (int x = 0; x < cubeSize; x++) {
                    short current_voxel = md.voxels[facing][x][y][z];
                    if ((current_voxel & 0xff) == 255) continue;
                    int currentX = (x + y) * 2 + 2;
                    int currentY = cubeSize * 4 + 2 + x - y - z * 3;
                        /*if (currentX < 0)
                            continue;
                        if (currentY < 0)
                            continue;
                        if (currentX > width * 2)
                            continue;
                        if (currentY > height * 2)
                            continue;
*/
                    p.drawPixmap(img, 4 * (current_voxel & 0xff), 5 * (current_voxel >> 8), 4, 4, currentX, currentY, 4, 4);
                    if ((current_voxel & 0xff) != 25) {

                        for (int ix = 0; ix < 4; ix++) {
                            for (int iy = 0; iy < 4; iy++) {

                                if(currentX + ix >= screenxsize)
                                    ix = 0;
                                if(currentY + iy >= screenysize)
                                    iy = 0;
                                zbuffer[ix + currentX][iy + currentY]=(short) (z + x - y);
                                colorBuffer[ix + currentX][iy + currentY]=current_voxel;
                            }
                        }
                    }
                }
            }
        }
        for(int sx = 0; sx < screenxsize; sx ++)
        {
            for(int sy = 0; sy < screenysize; sy ++) {
                if((colorBuffer[sx][sy] & 0xff) != 255)
                {
                    for(int ix = -2; ix <= 2; ix++)
                    {
                        for(int iy = -2; iy <= 2; iy++)
                        {
                            if(sx + ix < 0 || sx + ix >= screenxsize)
                                ix = 0;
                            if(sy + iy < 0 || sy + iy >= screenysize)
                                iy = 0;
                            if(zbuffer[sx][sy] - 2 > zbuffer[sx + ix][sy + iy])
                            {
                                p.drawPixmap(img, 4 * (((colorBuffer[sx + ix][sy + iy] & 0xff) == 255) ? 254 : (colorBuffer[sx][sy] & 0xff)),
                                        5 * (colorBuffer[sx][sy] >> 8) + 4, 1, 1, sx + ix, sy + iy, 1, 1);
                            }
                        }
                    }
                }
            }
        }
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }
}
