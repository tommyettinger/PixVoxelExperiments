package pixvoxel.experiments;

/**
 * Created by Tommy Ettinger on 12/21/2014.
 */
public class Model
{
    public ModelDisplay md;
    public int x, y, z, facing, cubeSize;
    public Model()
    {
        md = new ModelDisplay();
        x = 0;
        y = 0;
        z = 0;
        cubeSize = 0;
        facing = 0;
    }
    public Model(ModelDisplay model, int x, int y, int z, int facing, int cubeSize)
    {
        this.md = model;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.cubeSize = cubeSize;
    }
}
