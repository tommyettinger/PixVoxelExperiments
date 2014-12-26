package pixvoxel.experiments.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pixvoxel.experiments.Experiments;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.width = 800;
        cfg.height = 600;

        cfg.foregroundFPS = 0;
        cfg.backgroundFPS = 0;
       // cfg.fullscreen = true;
        // vSync
        cfg.vSyncEnabled = false;

		new LwjglApplication(new Experiments(), cfg);
	}
}
