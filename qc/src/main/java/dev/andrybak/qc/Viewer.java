package dev.andrybak.qc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Andrei Rybak
 */
public final class Viewer {
	private final JFrame window = new JFrame("QC viewer");
	private final Config config;

	private Viewer() {
		window.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_ESCAPE:
						exit();
						break;
				}
			}
		});
		config = Config.readConfig();
	}

	private void go() {
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.setVisible(true);
	}

	private void exit() {
		window.dispose();
		System.exit(0);
	}

	public static void main(String[] args) {
		new Viewer().go();
	}

	private static class Config implements Serializable {
		public static final Gson GSON = new GsonBuilder().create();
		private final String location;

		Config(String location) {
			this.location = location;
		}

		public Path getLocation() {
			return Paths.get(location);
		}

		static Config readConfig() {
			InputStream configStream = Config.class.getClassLoader().getResourceAsStream("viewer.cfg");
			Config config = GSON.fromJson(new InputStreamReader(configStream), Config.class);
			return config;
		}
	}
}
