package com.teammerge.abandoned;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.teammerge.abandoned.AbandonedGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Abandoned (in-development)");
		config.setWindowedMode(1280, 800);
		config.setForegroundFPS(60);
		config.useVsync(true);
		new Lwjgl3Application(new AbandonedGame(), config);
	}
}
