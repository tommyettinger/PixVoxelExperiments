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

public class Experiments extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    short[][][] voxels;
    int[][] zbuffer, minbuffer, outlinebuffer, maxbuffer;
    int size = 40;
    int width = 800;
    int height = 600;

    BitmapFont font;
    OrthographicCamera cam;

    public short[][][] readBVX(String filename, int row)
    {
        row &= 0x7f;
        font = new BitmapFont(Gdx.files.internal("MonologyLarge.fnt"), Gdx.files.internal("MonologyLarge.png"), false, true);
        short[][][] vs = new short[size][size][size];
        FileHandle file = Gdx.files.internal(filename + ".bvx");
        if (file.exists()) {
            byte[] bins = file.readBytes();
            double total = bins.length;
            size = (int) (Math.round(Math.cbrt(total)));
            for (short z = 0; z < size; z++) {
                for (short y = 0; y < size; y++) {
                    for (short x = 0; x < size; x++) {
                        vs[x][y][z] = (short) ( (row << 8) | (0xff & bins[z * size * size + y * size + x]));
                    }
                }
            }
        }
        return vs;
    }

	@Override
	public void create () {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("voxels.png"), Pixmap.Format.RGBA8888, false);
        voxels = readBVX("Zombie", 2);

        minbuffer = new int[width][height];
        maxbuffer = new int[width][height];
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }
        zbuffer = minbuffer.clone();
        outlinebuffer = maxbuffer.clone();

        cam = new OrthographicCamera(width * 2, height * 2);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();
	}

	@Override
	public void render () {
        zbuffer = minbuffer.clone();
        outlinebuffer = maxbuffer.clone();

        cam.update();
        batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(0.5f, 0.45f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                for (int y = size - 1; y >= 0; y--) {
                    short color =voxels[x][y][z];
                    if((color & 0xff) != 255)
                    {
                        zbuffer[300 + (x + y) * 2][200 - x + y + z * 3] = z + x - y;
                        zbuffer[300 + 2 + (x + y) * 2][200 - x + y + z * 3] = z + x - y;
                        zbuffer[300 + (x + y) * 2][200 + 2 - x + y + z * 3] = z + x - y;
                        zbuffer[300 + 2 + (x + y) * 2][200 + 2 - x + y + z * 3] = z + x - y;
                        outlinebuffer[300 + (x + y) * 2][200 - x + y + z * 3] = color;
                        outlinebuffer[300 + 2 + (x + y) * 2][200 - x + y + z * 3] = color;
                        outlinebuffer[300 + (x + y) * 2][200 + 2 - x + y + z * 3] = color;
                        outlinebuffer[300 + 2 + (x + y) * 2][200 + 2 - x + y + z * 3] = color;
                        batch.draw(img, 300 + (x + y) * 2f, 200 - x + y + z * 3f, 4 * (color & 0xff), 5 * (color >> 8), 4, 4);
                    }
                }
            }
        }

        for (int x = 2; x < width - 2; x += 2) {
            for (int y = 2; y < height - 2; y += 2) {
                int z = zbuffer[x][y] - 2;
                int color = outlinebuffer[x][y] & 0xff;
                int row = outlinebuffer[x][y] >> 8;
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
                }
            }
        }

        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 500, 450);
		batch.end();
	}

    @Override
    public void resize(int width, int height) {

        this.width = width;
        this.height = height;

        minbuffer = new int[width][height];
        maxbuffer = new int[width][height];
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++) {
                minbuffer[i][j] = -9999;
                maxbuffer[i][j] = 255;
            }

        cam.viewportWidth = width * 2;
        cam.viewportHeight = height * 2;
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();
    }
}
