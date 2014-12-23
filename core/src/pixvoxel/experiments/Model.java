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
    public Model()
    {
        name = "default";
        x = 0;
        y = 0;
        z = 0;
        cubeSize = 0;
        facing = 0;
    }
    public Model(String name, ModelDisplay model, int x, int y, int z, int facing, int cubeSize)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.cubeSize = cubeSize;
        if(!textures.containsKey(name + facing))
        {
            textures.put(name + facing, render(model));
        }
    }
    public static Pixmap img = null;
    public Texture render(ModelDisplay md)
    {
        short[][] current_edges = md.edges[facing];
        int screenxsize = current_edges.length;
        int screenysize = current_edges[0].length;
        Pixmap p = new Pixmap(screenxsize, screenysize, Pixmap.Format.RGBA8888);
        for(int c = 0; c < md.voxels[facing].length; c++) {
            short[] current_voxel = md.voxels[facing][c];

            int currentX = (current_voxel[0] + current_voxel[1]) * 2;
            int currentY = cubeSize * 4 + 1 + current_voxel[0] - current_voxel[1] - current_voxel[2] * 3;
                        /*if (currentX < 0)
                            continue;
                        if (currentY < 0)
                            continue;
                        if (currentX > width * 2)
                            continue;
                        if (currentY > height * 2)
                            continue;
*/
            p.drawPixmap(img, 4 * (current_voxel[3] & 0xff), 5 * (current_voxel[3] >> 8), 4, 4, currentX, currentY, 4, 4);
        }
        for(int sx = 0; sx < screenxsize; sx ++)
        {
            for(int sy = 0; sy < screenysize; sy ++) {
                if((current_edges[sx][sy] & 0xff) != 255)
                {
                    p.drawPixmap(img, 4 * (current_edges[sx][sy] & 0xff), 5 * (current_edges[sx][sy] >> 8) + 4, 2, 2, sx, sy, 1, 1);
                }
            }
        }
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }
}
