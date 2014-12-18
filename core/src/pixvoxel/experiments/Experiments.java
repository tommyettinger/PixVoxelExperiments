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

import java.util.Random;

public class Experiments extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    short[][][] voxels;
    short[][] culled;
    int culled_length = 0;
    short[][] zbuffer, minbuffer, outlinebuffer, maxbuffer;
    short xsize = 48 * 20, ysize = 48 * 20, zsize = 52;
    int width = 800;
    int height = 600;
    long total_rendered = 0;
    BitmapFont font;
    OrthographicCamera cam;

    public short[][][] readBVX(String filename, int row) {
        row &= 0x7f;
        font = new BitmapFont(Gdx.files.internal("MonologyLarge.fnt"), Gdx.files.internal("MonologyLarge.png"), false, true);
        short[][][] vs = new short[1][1][1];
        FileHandle file = Gdx.files.internal(filename + ".bvx");
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
    static Random r = new Random();

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

	@Override
	public void create () {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("voxels.png"), Pixmap.Format.RGBA8888, false);

        voxels = new short[xsize][ysize][zsize];
        culled = new short[xsize*ysize*5][4];

        for (short x = 0; x < xsize; x++) {
            for (short y = 0; y < ysize; y++) {
                for (short z = 0; z < zsize; z++) {
                    voxels[x][y][z] = 255;
                }
            }
        }
        short[][][] zombie = readBVX("Zombie", 2), male = readBVX("Male", 16), female = readBVX("Female", 1),
                grass = readBVX("Terrain", 50), sand = readBVX("Terrain", 52), mud = readBVX("Terrain", 54);
        short[][][][] terrains = {grass, sand, mud}, units = {zombie, zombie, male, female};
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {                insertModel(terrains[r.nextInt(3)], x * 48, y * 48, 0);
                if(r.nextInt(6) == 0)
                    insertModel(units[r.nextInt(4)], x * 48 + 4, y * 48 + 4, 12);
            }
        }
        minbuffer = new short[width * 2][height * 2];
        maxbuffer = new short[width * 2][height * 2];
        for(int i = 0; i < width * 2; i++)
            for(int j = 0; j < height * 2; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }
        outlinebuffer = maxbuffer.clone();

        cullVoxels();


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
    private int currentX = 0, currentY = 0;
    private short[] current_voxel;
	@Override
	public void render () {
        zbuffer = minbuffer.clone();
        outlinebuffer = maxbuffer.clone();

        cam.update();
        batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(0.8f, 0.5f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
/*        for (int z = 0; z < zsize; z++) {
            for (int x = 0; x < xsize; x++) {
                for (int y = ysize - 1; y >= 0; y--) {*/
        for(int c = culled_length - 1; c >= 0; c--)
        {
                    current_voxel =culled[c];
                    if((current_voxel[3] & 0xff) != 255)
                    {
                        currentX = (current_voxel[0] + current_voxel[1]) * 2;
                        currentY = height + current_voxel[1] - current_voxel[0] + current_voxel[2] * 3;
                        if(currentX < 0)
                            continue;
                        if(currentY < 0)
                            continue;
                        if(currentX > width * 2 - 4)
                            continue;
                        if(currentY > height * 2 - 4)
                            continue;

                        for(int ix = 0; ix < 4; ix++) {
                            for (int iy = 0; iy < 4; iy++) {
                                zbuffer[ix + currentX][iy + currentY] = (short)(current_voxel[2] + current_voxel[0] - current_voxel[1]);
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
                }*/
            }
        }

        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() + ", total rendered items: " + total_rendered, width / 2, height / 2);
        total_rendered = 0;
		batch.end();
	}

    @Override
    public void resize(int width, int height) {

        this.width = width;
        this.height = height;

        minbuffer = new short[width * 2][height * 2];
        maxbuffer = new short[width * 2][height * 2];
        for(int i = 0; i < width * 2; i++) {
            for (int j = 0; j < height * 2; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }
        }
        cullVoxels();
        cam.viewportWidth = width * 2;
        cam.viewportHeight = height * 2;
        cam.position.set(width, height, 0); //cam.viewportHeight / 2f
        cam.update();
    }
}
