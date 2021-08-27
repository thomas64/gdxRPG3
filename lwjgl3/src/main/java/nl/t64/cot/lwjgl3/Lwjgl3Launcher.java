package nl.t64.cot.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import nl.t64.cot.GdxRpg3;
import nl.t64.cot.constants.Constant;


public class Lwjgl3Launcher {

    public static void main(String[] args) {
        new Lwjgl3Application(new GdxRpg3(), getConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle(Constant.TITLE);
        config.setWindowIcon("sprites/icon.png", "libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        config.setResizable(false);
        config.setIdleFPS(60);
        config.setForegroundFPS(60);
        config.setWindowedMode(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        config.setPreferencesConfig("T64.nl/", Files.FileType.External);

        return config;
    }

}
