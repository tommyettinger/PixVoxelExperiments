package pixvoxel.experiments;

/**
 * Created by Tommy Ettinger on 12/21/2014.
 */
public class ModelDisplay {
    public short[][][] voxels;
    public short[][][] edges;
    public ModelDisplay()
    {
        voxels = new short[0][0][0];
        edges = new short[0][0][0];
    }
    public ModelDisplay(short[][][] voxels, short[][][] edges)
    {
        this.voxels = voxels;
        this.edges = edges;
    }
}
