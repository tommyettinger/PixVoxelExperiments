package pixvoxel.experiments;

import java.util.Random;

/**
 * Created by Tommy Ettinger on 12/21/2014.
 */
public class ModelDisplay {
    public short[][][][] voxels;
    public int size;
    public static Random r = new Random();
    //public short[][][] edges;
    public ModelDisplay()
    {
        voxels = new short[4][0][0][0];
        size = 0;
        //edges = new short[0][0][0];
    }
    public ModelDisplay(short[][][] voxels) //, short[][][] edges
    {
        size = voxels.length;
        this.voxels = new short[4][][][];
        this.voxels[0] = voxels;
        for(int dir = 1; dir < 4; dir++) {
            this.voxels[dir] = new short[size][size][size];

            switch (dir) {
                case 0:
                    break;
                case 1:
                    for (int z = 0; z < size; z++) {
                        for (int y = 0; y < size; y++) {
                            for (int x = 0; x < size; x++) {
                                int newX = y, newY = size - x - 1;
                                this.voxels[dir][newX][newY][z] = voxels[x][y][z];
                            }
                        }
                    }
                    break;
                case 2:
                    for (int z = 0; z < size; z++) {
                        for (int y = 0; y < size; y++) {
                            for (int x = 0; x < size; x++) {
                                int newX = size - x - 1, newY = size - y - 1;
                                this.voxels[dir][newX][newY][z] = voxels[x][y][z];
                            }
                        }
                    }
                    break;
                case 3:
                    for (int z = 0; z < size; z++) {
                        for (int y = 0; y < size; y++) {
                            for (int x = 0; x < size; x++) {
                                int newX = size - y - 1, newY = x;
                                this.voxels[dir][newX][newY][z] = voxels[x][y][z];
                            }
                        }
                    }
                    break;
            }
        }
       // this.edges = edges;
    }
    public ModelDisplay(short[][][] voxels, double damageRate)
    {
        this(mangle(voxels, damageRate));
    }
    public static short[][][] mangle(short[][][] voxels, double damageRate)
    {
        int size = voxels.length;
        short[][][] vs = new short[size][size][size];
        for (int z = 0; z < size; z++) {
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    vs[x][y][z] = ((voxels[x][y][z] & 0xff) != 255 &&(voxels[x][y][z] & 0xff) != 25 && damageRate > r.nextDouble()) ? (short)(34 | (voxels[x][y][z] & 0xff00)) : voxels[x][y][z];
                }
            }
        }
        return vs;
    }
}
