package pixvoxel.experiments;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sun.javafx.sg.PGShape;

import java.util.Random;

public class Experiments extends ApplicationAdapter {
    SpriteBatch batch;
   // Texture img;
    //short[][][] voxels;
    //short[][] culled;
    Model[][][] models;
    int culled_length = 0;
    short[][] zbuffer, minbuffer, outlinebuffer, maxbuffer;
    short xsize = 20, ysize = 20, zsize = 2;
    int width = 800;
    int height = 600;
    long total_rendered = 0;
    BitmapFont font;
    OrthographicCamera cam;

    public short[][][] readBVX(String filename, int row) {
        row &= 0x7f;
        short[][][] vs = new short[1][1][1];
        FileHandle file = Gdx.files.internal(filename + "/" + filename + ".bvx");
        if (file.exists()) {
            byte[] bins = file.readBytes();
            double total = bins.length;
            short size = (short) (Math.round(Math.cbrt(total)));
            vs = new short[size][size][size];
            for (short z = 0; z < size; z++) {
                for (short y = 0; y < size; y++) {
                    for (short x = 0; x < size; x++) {
                        vs[x][y][z] = (short) (((((0xff & bins[z * size * size + y * size + x]) == 255) ? 0 : row) << 8) | (0xff & bins[z * size * size + y * size + x]));
                    }
                }
            }
        }
        return vs;
    }

    public short[][][] readCVX(String filename, int size, int row) {
        row &= 0x7f;
        short[][][] vs = new short[4][1][4];
        int dirnum = 0;
        for (String dir : new String[] {"SE", "SW", "NW", "NE"}) {
            FileHandle file = Gdx.files.internal(filename + "/" + filename + "_[" + size + "]_" + dir + ".cvx");

            if (file.exists()) {
                byte[] bins = file.readBytes();
                short total = (short)(bins.length / 4);
                vs[dirnum] = new short[total][4];
                for (short i = 0; i < total; i++) {
                    short x = (short)(bins[i*4 + 0] & 0xff);
                    short y = (short)(bins[i*4 + 1] & 0xff);
                    short z = (short)(bins[i*4 + 2] & 0xff);
                    short color = (short)((((0xff & bins[i*4 + 3]) == 255 ? 0 : row) << 8) | (0xff & bins[i*4 + 3]));
                    vs[dirnum][i][0] = x;
                    vs[dirnum][i][1] = y;
                    vs[dirnum][i][2] = z;
                    vs[dirnum][i][3] = color;
                    //vs[x][y][z] = (short) (((((0xff & bins[z * size * size + y * size + x]) == 255) ? 0 : row) << 8) | (0xff & bins[z * size * size + y * size + x]));
                }
            }
            dirnum++;
        }
        return vs;
    }
    public short[][][] readEVX(String filename, int wide, int tall, int row) {
        row &= 0x7f;
        short[][][] vs = new short[4][wide][tall];
        int dirnum = 0;
        for (String dir : new String[] {"SE", "SW", "NW", "NE"}) {
            FileHandle file = Gdx.files.internal(filename + "/" + filename + "_[" + wide + "x" + tall + "]_" + dir + ".evx");

            if (file.exists()) {
                byte[] bins = file.readBytes();
                vs[dirnum] = new short[wide][tall];
                for (short i = 0; i < tall; i++) {
                    for (short j = 0; j < wide; j++) {
                        short color = (short) ((((0xff & bins[i * wide + j]) == 255 ? 0 : row) << 8) | (0xff & bins[i * wide + j]));
                        vs[dirnum][j][i] = color;
                        //vs[x][y][z] = (short) (((((0xff & bins[z * size * size + y * size + x]) == 255) ? 0 : row) << 8) | (0xff & bins[z * size * size + y * size + x]));
                    }
                }
            }
            dirnum++;
        }
        return vs;
    }
/*
    public void insertModel(short[][][] model, int xpos, int ypos, int zpos)
    {
        for (int x = 0; x < model.length; x++) {
            for (int y = 0; y < model[0].length; y++) {
                for (int z = 0; z < model[0][0].length; z++) {
                    if((0xff & model[x][y][z]) != 255)
                        voxels[x + xpos][y + ypos][z + zpos] = model[x][y][z];
                }
            }
        }
    }
    */
    static Random r = new Random();
/*
    public void cullVoxels()
    {
        boolean visible = false;
        culled_length = 0;
        zbuffer = minbuffer.clone();
        for (short z = (short)(zsize - 1); z >= 0 ; z--) {
            for (short x = (short) (xsize - 1); x >= 0; x--) {
                for (short y = 0; y < ysize; y++) {
                    currentX = (x + y) * 2;
                    currentY = height + y - x + z * 3;
                    if ((voxels[x][y][z] & 0xff) == 255 || currentX < 0 || currentY < 0 || currentX >= width * 2 - 4 || currentY >= height * 2 - 4)
                        continue;
                    for (int ix = 0; ix < 4; ix++) {
                        for (int iy = 0; iy < 4; iy++) {
                            if (zbuffer[ix + currentX][iy + currentY] == -9999) {
                                zbuffer[ix + currentX][iy + currentY] = (short) (z + x - y);
                                visible = true;
                            }
                        }
                    }
                    if (visible) {
                        culled[culled_length++] = new short[]{x, y, z, voxels[x][y][z]};
                        visible = false;
                    }
                }
            }
        }
    }
*/
	@Override
	public void create () {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
        Pixmap.setFilter(Pixmap.Filter.BiLinear);
		Model.img = new Pixmap(Gdx.files.internal("voxels.png"));


        font = new BitmapFont(Gdx.files.internal("MonologyLarge.fnt"), Gdx.files.internal("MonologyLarge.png"), false, true);

//        voxels = new short[xsize][ysize][zsize];
//        culled = new short[xsize*ysize*5][4];
        models = new Model[xsize][ysize][zsize];
        //short[][][] zombieVoxels = readCVX("Zombie", 40, 2), maleVoxels = readCVX("Male", 40, 16), femaleVoxels = readCVX("Female", 40, 1),
        //        grassVoxels = readCVX("Terrain", 48, 50), sandVoxels = readCVX("Terrain", 48, 52), mudVoxels = readCVX("Terrain", 48, 54);
        short[][][] zombie = readBVX("Zombie", 2), skeleton = readBVX("Skeleton", 6), male = readBVX("Male", 16), female = readBVX("Female", 1),
                grass = readBVX("Terrain", 50), sand = readBVX("Terrain", 52), mud = readBVX("Terrain", 54);
//        short[][][] zombieEdges = readEVX("Zombie", 168, 208, 2), maleEdges = readEVX("Male", 168, 208, 16), femaleEdges = readEVX("Female", 168, 208, 1),
//                grassEdges = readEVX("Terrain", 200, 248, 50), sandEdges = readEVX("Terrain", 200, 248, 52), mudEdges = readEVX("Terrain", 200, 248, 54);
        //ModelDisplay zombie = new ModelDisplay(zombieVoxels), skeleton  = new ModelDisplay(skeletonVoxels), male = new ModelDisplay(maleVoxels), female = new ModelDisplay(femaleVoxels),
        //        grass = new ModelDisplay(grassVoxels), sand = new ModelDisplay(sandVoxels), mud = new ModelDisplay(mudVoxels);
        short[][][][] terrains = {grass, sand, mud}, units = {zombie, zombie, skeleton, male, female};
        String[] terrainNames = {"grass", "sand", "mud"}, unitNames = {"zombie", "zombie", "skeleton", "male", "female"};
        double[] terrainDamages = {0.01, 0.02, 0.03, 0.05, 0.1, 0.15, 0.2}, unitDamages = {0.0, 0.02, 0.04, 0.06, 0.08, 0.1, 0.12};
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                int rt = r.nextInt(3);
                models[x][y][0] = new Model(terrainNames[rt], terrains[rt], x * 48, y * 48, 0, 0, terrainDamages[r.nextInt(7)]);
//                insertModel(terrains[r.nextInt(3)], x * 48, y * 48, 0);
                if(r.nextInt(6) == 0) {
                    int ru = r.nextInt(5);
                    models[x][y][1] = new Model(unitNames[ru], units[ru],x * 48 + 4, y * 48 + 4, 12, r.nextInt(4), unitDamages[r.nextInt(7)]);
                }
//                insertModel(units[r.nextInt(4)], x * 48 + 4, y * 48 + 4, 12);
            }
        }



/*        minbuffer = new short[width * 2][height * 2];
        maxbuffer = new short[width * 2][height * 2];
        for(int i = 0; i < width * 2; i++)
            for(int j = 0; j < height * 2; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }
        outlinebuffer = maxbuffer.clone();

        cullVoxels();
*/

        /*
        for (int sx = 0; sx < width * 2; sx += 2) {
            for (int sy = 0; sy < height * 2; sy++) {
                for (int vz = zsize - 1; vz >= 0; vz--) {
                    int vx = (2*height - 2*sy + sx + 2*vz * 3)/4;
                    int vy = (2*sy + sx - 2*height - 2*vz * 3)/4;
                    /*
                    vy = (2*sy + sx - 2*height - 2*vz * 3)/4
                    vx = (2*height - 2*sy + sx + 2*vz * 3)/4
                    THANK YOU Jakob Progsch!
                    /

                    if(vx >= 0 && vy >= 0 && vx < xsize && vy < ysize && voxels[vx][vy][vz] != 255)
                    {
                        culled[counter++] = voxels[vx][vy][vz];
                        break;
                    }
                }
            }
        }
        */
        //zbuffer = minbuffer.clone();

        cam = new OrthographicCamera(width * 2, height * 2);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();
	}
    private int currentX = 0, currentY = 0, screenxsize = 208, screenysize = 248,
            screenxoffset = 0, screenyoffset = 0;
    private short[][] current_edges;
    private short[] current_voxel;
	@Override
	public void render () {
//        zbuffer = minbuffer.clone();
//        outlinebuffer = maxbuffer.clone();

        cam.update();
        batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(0.5f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
/*        for (int z = 0; z < zsize; z++) {
            for (int x = 0; x < xsize; x++) {
                for (int y = ysize - 1; y >= 0; y--) {*/

        for(int boardZ = 0; boardZ < zsize; boardZ++)
        {
            for(int boardX = 0; boardX < xsize; boardX++)
            {
                for(int boardY = ysize - 1; boardY >= 0; boardY--)
                {
                    Model m = models[boardX][boardY][boardZ];
                    if(m == null)
                        continue;
                    batch.draw(Model.textures.get(m.name), (m.x + m.y) * 2, -64 + m.y - m.x + m.z * 3);
                    total_rendered++;
                    /*for(int c = 0; c < m.md.voxels[m.facing].length; c++) {
                        current_voxel = m.md.voxels[m.facing][c];

                        currentX = (current_voxel[0] + m.x + current_voxel[1] + m.y) * 2;
                        currentY = current_voxel[1] + m.y - current_voxel[0] - m.x + (current_voxel[2] + m.z) * 3;
                        /*if (currentX < 0)
                            continue;
                        if (currentY < 0)
                            continue;
                        if (currentX > width * 2)
                            continue;
                        if (currentY > height * 2)
                            continue; * /
                        batch.draw(img, currentX, currentY, 4 * (current_voxel[3] & 0xff), 5 * (current_voxel[3] >> 8), 4, 4);
                        total_rendered++;
                    }
                    current_edges = m.md.edges[m.facing];
                    screenxsize = current_edges.length;
                    screenysize = current_edges[0].length;
                    screenxoffset = (m.x + m.y) * 2;
                    screenyoffset = - m.cubeSize + m.y - m.x + m.z * 3;
                    for(int x = 0; x < screenxsize; x+=2)
                    {
                        for(int y = 0; y < screenysize; y+=2) {
                            if((current_edges[x][y] & 0xff) != 255)
                            {
                                batch.draw(img, x + screenxoffset, y + screenyoffset, 2f, 2f, 4 * (current_edges[x][y] & 0xff), 5 * (current_edges[x][y] >> 8) + 4, 1, 1, false, false);
                                total_rendered++;
                            }
                        }
                    }*/
                }
            }
        }
        /*for(int c = culled_length - 1; c >= 0; c--) {
            current_voxel = culled[c];
            if ((current_voxel[3] & 0xff) != 255) {
                currentX = (current_voxel[0] + current_voxel[1]) * 2;
                currentY = height + current_voxel[1] - current_voxel[0] + current_voxel[2] * 3;
                if (currentX < 0)
                    continue;
                if (currentY < 0)
                    continue;
                if (currentX > width * 2 - 4)
                    continue;
                if (currentY > height * 2 - 4)
                    continue;

                for (int ix = 0; ix < 4; ix++) {
                    for (int iy = 0; iy < 4; iy++) {
                        zbuffer[ix + currentX][iy + currentY] = (short) (current_voxel[2] + current_voxel[0] - current_voxel[1]);
                        outlinebuffer[ix + currentX][iy + currentY] = current_voxel[3];
                    }
                }
                batch.draw(img, currentX, currentY, 4 * (current_voxel[3] & 0xff), 5 * (current_voxel[3] >> 8), 4, 4);
                total_rendered++;
            }
        }



        for (int x = 2; x < width * 2 - 2; x += 2) {
            for (int y = 2; y < height * 2 - 2; y += 2) {
                for(int ix = -2; ix <= 2; ix+=2){
                    for(int iy = -2; iy <= 2; iy+=2){
                        if (!(ix == 0 && iy == 0) && zbuffer[x][y] - 2 > zbuffer[x + ix][y + iy]) {
                            batch.draw(img, x + ix, y + iy, 2f, 2f, 4 * (outlinebuffer[x][y] & 0xff), 5 * (outlinebuffer[x][y] >> 8) + 4, 1, 1, false, false);
                            total_rendered++;
                        }
                    }
                }
                /*
                if (z > zbuffer[x - 2][y]) {
                    batch.draw(img, x - 2, y, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x - 2][y - 2]) {
                    batch.draw(img, x - 2, y - 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x][y - 2]) {
                    batch.draw(img, x, y - 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x + 2][y - 2]) {
                    batch.draw(img, x + 2, y - 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x + 2][y]) {
                    batch.draw(img, x + 2, y, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x + 2][y + 2]) {
                    batch.draw(img, x + 2, y + 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x][y + 2]) {
                    batch.draw(img, x, y + 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                }
                if (z > zbuffer[x - 2][y + 2]) {
                    batch.draw(img, x - 2, y + 2, 2f, 2f, 4 * color, 5 * row + 4, 1, 1, false, false);
                } * /
            }
        }*/

        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() + ", total rendered items: " + total_rendered, width / 2, height / 2);
        total_rendered = 0;
		batch.end();
	}

    @Override
    public void resize(int width, int height) {

        this.width = width;
        this.height = height;
/*
        minbuffer = new short[width * 2][height * 2];
        maxbuffer = new short[width * 2][height * 2];
        for(int i = 0; i < width * 2; i++) {
            for (int j = 0; j < height * 2; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }
        }
        cullVoxels();*/
        cam.viewportWidth = width * 2;
        cam.viewportHeight = height * 2;
        cam.position.set(width, 0, 0); //cam.viewportHeight / 2f
        cam.update();
    }
}
