package dev.andrybak.qc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.andrybak.qc.os.Opener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Andrei Rybak
 */
public final class Viewer {
	private static final int CACHE_SIZE = 800;
	private static final Path STATE_SAVE_PATH = Paths.get(".qc_viewer_state");

	private final JFrame window = new JFrame("QC viewer");
	private final Config config;

	private final JPanel content;
	private final Canvas view;
	private final Map<Integer, Path> comicFiles;
	private final ConcurrentNavigableMap<Integer, Image> cache = new ConcurrentSkipListMap<>();

	private final int min = 1;
	private final int max;
	private volatile Cursor cursor;
	private final NumberReader numberReader;
	private final CircularHistory history;
	private Toolkit t = Toolkit.getDefaultToolkit();

	private Viewer() {
		content = new JPanel(new BorderLayout());
		view = new Canvas() {
			@Override
			public void paint(Graphics g) {
				paintCurrImage(g, this);
				paintNumber(g);
			}
		};
		content.add(view, BorderLayout.CENTER);
		numberReader = new NumberReader(this::paintNumber, (byte) 4);

		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), this::exit);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, true), this::exit);

		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), this::scrollDown);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.SHIFT_DOWN_MASK), this::scrollUp);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), this::prevComic);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), this::nextComic);

		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), this::prevTenComic);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), this::nextTenComic);

		for (int k = KeyEvent.VK_0; k <= KeyEvent.VK_9; k++) {
			int key = k;
			initKeyStroke(KeyStroke.getKeyStroke(k, 0), () -> numberReader.consume(key - KeyEvent.VK_0));
		}
		for (int k = KeyEvent.VK_NUMPAD0; k <= KeyEvent.VK_NUMPAD9; k++) {
			int key = k;
			initKeyStroke(KeyStroke.getKeyStroke(k, 0), () -> numberReader.consume(key - KeyEvent.VK_NUMPAD0));
		}
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), this::showReadNumber);

		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK), this::historyBack);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK), this::historyForward);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), this::historyAbandon);

		// vim controls
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0), this::scrollDown);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0), this::scrollUp);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), this::historyBack);
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), this::historyForward);

		// browser, 'o' for "Open"
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), this::openCurrentInBrowser);
		// 'e' for "file Explorer". Shortcut Meta+E opens file browsers both in Windows and in KDE.
		initKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), this::openCurrentInFileBrowser);

		config = Config.readConfig();
		comicFiles = findAll(config);
		max = comicFiles.keySet().stream().mapToInt(i -> i).max().orElse(1);
		history = readState(this.max);
		presentJump(history.getCurrent());
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(this::loadNeighbors, 2, 5, TimeUnit.SECONDS);
	}

	private CircularHistory readState(int defaultValue) {
		if (!Files.exists(STATE_SAVE_PATH)) {
			System.out.println("No saved state.");
			return new CircularHistory(defaultValue);
		}
		try {
			List<String> lines = Files.readAllLines(STATE_SAVE_PATH);
			if (lines.isEmpty())
				return new CircularHistory(defaultValue);
			List<Integer> tmp = new ArrayList<>();
			for (String line : lines) {
				try {
					int candidate = Integer.parseInt(line);
					if (candidate < min || candidate > max) {
						System.err.println("Illegal value " + candidate + " was saved.");
						System.err.println("Allowed interval: [" + min + ", " + max + "].");
					} else {
						tmp.add(candidate);
					}
				} catch (NumberFormatException e) {
					System.err.println("Could not read value: " + sanitize(line));
				}
			}
			if (tmp.isEmpty())
				return new CircularHistory(defaultValue);
			return CircularHistory.deserialize(tmp);
		} catch (IOException e) {
			System.err.println("Could not read " + STATE_SAVE_PATH);
			return new CircularHistory(defaultValue);
		}
	}

	private static String sanitize(String s) {
		if (s.length() < 20)
			return s;
		return s.substring(0, 20);
	}

	private void saveState() {
		try {
			Files.write(STATE_SAVE_PATH, history.serialize().stream().map(String::valueOf).collect(toList()));
			System.out.println("Saved state.");
		} catch (IOException e) {
			System.err.println("Could not save current state in '" + STATE_SAVE_PATH + "'.");
		}
	}


	private void initKeyStroke(KeyStroke nextKeyStroke, Runnable runnable) {
		Object cmd = new Object();
		content.getInputMap().put(nextKeyStroke, cmd);
		content.getActionMap().put(cmd, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runnable.run();
			}
		});
	}

	private void presentJump(int n) {
		cursor = new Cursor(n);
		presentCurrentComic();
	}

	private void presentCurrentComic() {
		System.out.println("Current comic = #" + cursor);
		Image img = getCurrImage();
		view.prepareImage(img, view);
		repaintView();
		clearStale();
		numberReader.reset(cursor.getComicNum());
	}

	private Image getCurrImage() {
		int comicNum = cursor.getComicNum();
		return getImage(comicNum);
	}

	private Image getImage(int comicNum) {
		Path imagePath = comicFiles.get(comicNum);
		return cache.computeIfAbsent(comicNum, k -> {
			Image img = t.getImage(imagePath.toString());
			System.out.println("Getting image #" + comicNum + "... Got " + String.valueOf(img));
			return img;
		});
	}

	private void paintNumber() {
		view.repaint();
	}

	private void paintNumber(Graphics g) {
		if (numberReader.getNumber() == 0)
			return;
		g.drawString(String.valueOf(numberReader.getNumber()), 10, 20);
	}

	private void paintCurrImage(Graphics g, ImageObserver imageObserver) {
		Image currImage = getCurrImage();
		Rectangle bounds = g.getClipBounds();
		int iw = currImage.getWidth(imageObserver);
		final int y;
		switch (cursor.getPos()) {
		case TOP:
			y = 0;
			break;
		case BOTTOM:
			int ih = currImage.getHeight(imageObserver);
			y = bounds.height - ih;
			break;
		default:
			throw new IllegalStateException("Unhandled value " + cursor.getPos());
		}
		g.drawImage(currImage, bounds.x + (bounds.width - iw) / 2, y, imageObserver);
	}

	private void repaintView() {
		SwingUtilities.invokeLater(view::repaint);
	}

	private void clearStale() {
		if (cache.size() < CACHE_SIZE)
			return;
		final int minKeep = cursor.getComicNum() - (CACHE_SIZE / 4);
		final int maxKeep = cursor.getComicNum() + (CACHE_SIZE / 2);
		for (int i = cache.firstKey(); i < minKeep; i++)
			cache.remove(i);
		for (int i = maxKeep + 1, n = cache.lastKey() + 1; i < n; i++)
			cache.remove(i);
	}

	private void loadNeighbors() {
		try {
			final int comicNum = cursor.getComicNum();
			final int cacheFrom = Math.max(min, comicNum - CACHE_SIZE / 10);
			final int cacheTill = Math.min(comicNum + CACHE_SIZE / 2, this.max);
			int cnt = 0;
			for (int i = cacheFrom; i <= cacheTill; i++) {
				if (cache.containsKey(i))
					continue;
				getImage(i);
				cnt++;
			}
			if (cnt != 0) {
				System.out.println("Want to have cache [" + cacheFrom + ", " + cacheTill + "]");
				System.out.println("Loaded " + cnt + " images to cache");
			}
		} catch (Throwable t) {
			System.err.println(String.valueOf(t));
			throw t;
		}
	}

	/* Regular reading movement */

	private void scrollDown() {
		cursor = cursor.scrollDown();
		presentRegularReading();
	}

	private void scrollUp() {
		cursor = cursor.scrollUp();
		presentRegularReading();
	}

	private void nextComic() {
		cursor = cursor.nextComic();
		presentRegularReading();
	}

	private void prevComic() {
		cursor = cursor.prevComic();
		presentRegularReading();
	}

	private void nextTenComic() {
		for (int i = 0; i < 10; i++)
			cursor = cursor.nextComic();
		presentRegularReading();
	}

	private void prevTenComic() {
		for (int i = 0; i < 10; i++)
			cursor = cursor.prevComic();
		presentRegularReading();
	}

	private void presentRegularReading() {
		history.updateCurrent(cursor.getComicNum());
		presentCurrentComic();
	}

	/* Jumping movement */

	private void showReadNumber() {
		int comicNum = numberReader.getNumber();
		if (comicNum < min || comicNum > max)
			return;
		history.addEntry(comicNum);
		presentJump(comicNum);
	}

	private void historyBack() {
		presentJump(history.prev());
	}

	private void historyForward() {
		presentJump(history.next());
	}

	private void historyAbandon() {
		presentJump(history.abandonCurrent());
	}

	private void openCurrentInBrowser() {
		String url = "https://questionablecontent.net/" + history.getCurrent();
		Opener.openUrl(url);
	}

	private void openCurrentInFileBrowser() {
		Path p = comicFiles.get(history.getCurrent());
		Opener.browseFileDirectory(window, p.toFile());
	}

	private void go() {
		window.setMinimumSize(new Dimension(640, 480));
		window.setSize(800, 600);
		window.setContentPane(content);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setVisible(true);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveState();
			}
		});
		SwingUtilities.invokeLater(this::repaintView);
		System.out.println(config.getLocation());
	}

	private void exit() {
		saveState();
		window.dispose();
		System.exit(0);
	}

	private static Map<Integer, Path> findAll(Config config) {
		try (Stream<Path> files = Files.walk(Paths.get(config.getLocation()), 2)) {
			return files
				.filter(path -> isImageFile(path.getFileName().toString()))
				.filter(path -> isComicDir(path.getName(path.getNameCount() - 2).toString()))
				.filter(path -> path.getFileName().toString().length() > 4)
				.filter(path -> isComicNumber(path.getFileName().toString().substring(0, 4)))
				.collect(toMap(Viewer::extractComicNumber, Function.identity()));
		} catch (IOException e) {
			System.err.println("Could not find location " + config.getLocation());
			throw new IllegalArgumentException();
		}
	}

	private static boolean isComicNumber(String s) {
		return s.length() == 4 && s.chars().allMatch(Character::isDigit);
	}

	private static boolean isComicDir(String s) {
		return s.length() == 2 && s.chars().allMatch(Character::isDigit);
	}

	private static boolean isImageFile(String filename) {
		return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".gif");
	}

	private static int extractComicNumber(Path p) {
		return Integer.parseInt(p.getFileName().toString().substring(0, 4));
	}

	public static void main(String[] args) {
		new Viewer().go();
	}

	private static class NumberReader {
		private static final long READ_DELAY = 5000;
		private final int base;
		private int x = 0;
		private Runnable listener;
		private long lastTimeRead = System.currentTimeMillis();

		NumberReader(Runnable listener, byte digits) {
			if (digits > 9 || digits < 1)
				throw new IllegalArgumentException("Digits should be in [1, 9]. Got " + digits);
			this.listener = listener;
			int tmp = 1;
			for (byte i = 0; i < digits; i++)
				tmp *= 10;
			base = tmp;
		}

		int getNumber() {
			return x;
		}

		void consume(int digit) {
			if (lastTimeRead + READ_DELAY < System.currentTimeMillis())
				x = 0;
			x *= 10;
			x += digit;
			x %= base;
			listener.run();
			lastTimeRead = System.currentTimeMillis();
		}

		void reset(int x) {
			this.x = x;
		}
	}

	private enum Position {
		TOP, BOTTOM
	}

	private class Cursor {
		private final int comicNum;
		private final Position pos;

		Cursor(int comicNum) {
			this(comicNum, Position.TOP);
		}

		Cursor(int comicNum, Position pos) {
			this.comicNum = comicNum < min ? max : (comicNum > max ? min : comicNum);
			this.pos = pos;
		}

		int getComicNum() {
			return comicNum;
		}

		Position getPos() {
			return pos;
		}

		Cursor scrollDown() {
			switch (pos) {
			case TOP:
				return new Cursor(comicNum, Position.BOTTOM);
			case BOTTOM:
				return new Cursor(comicNum + 1, Position.TOP);
			default:
				throw new IllegalStateException("Unhandled value " + pos);
			}
		}

		Cursor scrollUp() {
			switch (pos) {
			case TOP:
				return new Cursor(comicNum - 1, Position.BOTTOM);
			case BOTTOM:
				return new Cursor(comicNum, Position.TOP);
			default:
				throw new IllegalStateException("Unhandled value " + pos);
			}
		}

		Cursor nextComic() {
			return new Cursor(comicNum + 1, Position.TOP);
		}

		Cursor prevComic() {
			return new Cursor(comicNum - 1, Position.TOP);
		}

		@Override
		public String toString() {
			return "Cursor{" +
				"comicNum=" + comicNum +
				", pos=" + pos +
				'}';
		}
	}

	private static class Config implements Serializable {
		private static final Gson GSON = new GsonBuilder().create();
		private static final String CONFIG_FILENAME = "viewer.cfg";
		private final String location;

		Config(String location) {
			this.location = location;
		}

		String getLocation() {
			return location;
		}

		static Config readConfig() {
			try (InputStream configStream = new FileInputStream(new File(CONFIG_FILENAME))) {
				return GSON.fromJson(new InputStreamReader(configStream), Config.class);
			} catch (IOException e) {
				throw new IllegalStateException("Can not run without a configuration file " + CONFIG_FILENAME);
			}
		}
	}
}