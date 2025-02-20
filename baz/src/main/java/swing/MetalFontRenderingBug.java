package swing;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * This reproducer can also be found <a href="https://github.com/rybak/foobar/blob/master/baz/src/main/java/swing/MetalFontRenderingBug.java">
 * on GitHub</a> with the needed resource files.
 * <h2>Bug: some TrueType fonts are rendered incorrectly in VolatileImage in the Metal rendering pipeline.</h2>
 * <h3>Prerequisites</h3>
 * Requirements for the bug reproduction:
 * <ol>
 *     <li>macOS aarch64</li>
 *     <li>Metal rendering pipeline is used. See <a href="https://bugs.openjdk.org/browse/JDK-8284378">
 *         JDK-8284378 Make Metal the default Java 2D rendering pipeline for macOS</a> for details.</li>
 *     <li>{@link VolatileImage} is used</li>
 *     <li>Default desktop rendering hints (from "awt.font.desktophints") are added</li>
 *     <li>A TrueType font is used. In this reproducer, the file {@code OpenSans-Regular.ttf} downloaded from
 *     <a href="https://fonts.google.com/specimen/Open+Sans">Google Fonts</a> is used.
 *     </li>
 *     <li>Font size is less than 51.</li>
 * </ol>
 * <h3>Steps to reproduce</h3>
 * <ol>
 *     <li>Launch class swing.MetalFontRenderingBug, with {@code swing/OpenSans-Regular.ttf} in resources</li>
 *     <li>Observe the text in the demo's window</li>
 * </ol>
 * <h3>Expected result</h3>
 * The text in the window ("Font: Open Sans Regular, size: 50") is readable.
 * <h3>Actual result</h3>
 * The text in the window is unreadable with some kind of diagonal artifacts.
 * <h3>Workaround</h3>
 * Disable the Metal rendering pipeline by passing a system property
 * {@code -Dsun.java2d.metal=false} or {@code -Dsun.java2d.opengl=true} to the JVM.
 * <h3>Notes</h3>
 * <p>
 * The controls in the window help demonstrate how different prerequisites are required.
 * <p>
 * Not reproducible on MS Windows. Reproduced on:
 * <ol>
 *     <li>zulu17.48.15-ca-jdk17.0.0-macosx_aarch64 with {@code -Dsun.java2d.metal=true}</li>
 *     <li>temurin17.0.13+11 aarch64 (aka {@code OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.13_11})
 *     with {@code -Dsun.java2d.metal=true}</li>
 *     <li>zulu21.38.21-ca-jdk21.0.5-macosx_aarch64 without additional system properties</li>
 * </ol>
 * <p>
 * The root cause was found to be <a href="https://bugs.openjdk.org/browse/JDK-8284378">JDK-8284378</a>
 * by bisecting on JDK versions, which landed me on {@code zulu19.0.43-ea-jdk19.0.0-ea.17-macosx_aarch64}
 * as the last good version and {@code zulu19.0.45-ea-jdk19.0.0-ea.18-macosx_aarch64} as the first bad
 * version. Then I looked at this range of versions in the
 * <a href="https://github.com/openjdk/jdk19u">JDK 19 updates repository</a>:
 * <pre>
 * $ git log jdk-19+17..jdk-19+18 --oneline -- '*macosx*'
 * f4edb59a6e4 8284567: Collapse identical catch branches in java.base
 * 3a0ddeba52b 8284378: Make Metal the default Java 2D rendering pipeline for macOS
 * 192886546bf 8284166: [macos] Replace deprecated alternateSelectedControlColor with selectedContentBackgroundColor
 * </pre>
 *
 * @author Andrei Rybak
 */
public class MetalFontRenderingBug {
	private static final int DEMO_WIDTH = 800;
	private static final int DEMO_HEIGHT = 600;

	private MetalFontRenderingBug() {
	}

	public static void main(String... args) throws IOException, FontFormatException {
		JFrame demo = new JFrame("Bug: some TrueType fonts are rendered incorrectly in VolatileImage in the Metal rendering pipeline.");
		demo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		demo.setMinimumSize(new Dimension(DEMO_WIDTH, DEMO_HEIGHT));
		JPanel contentPane = new JPanel(new BorderLayout());
		demo.setContentPane(contentPane);

		JCheckBox enableDesktopHints = new JCheckBox("Enable desktop hints", true);
		JCheckBox enableVolatileImage = new JCheckBox("Enable volatile image", true);
		JCheckBox enableOpenSans = new JCheckBox("Enable OpenSans-Regular.ttf", true);
		JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 96, 1));

		contentPane.add(createControlsPanel(enableDesktopHints, enableVolatileImage, enableOpenSans,
				new JLabel("Font size: "), fontSizeSpinner),
			BorderLayout.SOUTH);
		contentPane.add(new JLabel("JLabel is not affected." +
			" " + reportJvmVersion() +
			" " + reportSystemProperty("sun.java2d.opengl") +
			" " + reportSystemProperty("sun.java2d.metal")), BorderLayout.NORTH);
		contentPane.add(new MyDrawStringExample(enableDesktopHints::isSelected, enableVolatileImage::isSelected,
				enableOpenSans::isSelected, () -> (int) fontSizeSpinner.getValue()),
			BorderLayout.CENTER);
		ChangeListener controlsChangeListener = ignored -> {
			demo.invalidate();
			demo.repaint();
		};
		enableDesktopHints.addChangeListener(controlsChangeListener);
		enableVolatileImage.addChangeListener(controlsChangeListener);
		enableOpenSans.addChangeListener(controlsChangeListener);
		fontSizeSpinner.addChangeListener(controlsChangeListener);
		demo.pack();
		demo.setVisible(true);
	}

	private static String reportJvmVersion() {
		return "JVM version = " + Runtime.version();
	}

	private static String reportSystemProperty(String key) {
		return "System property " + key + "=" + System.getProperty(key);
	}

	private static JPanel createControlsPanel(JComponent... components) {
		JPanel panel = new JPanel();
		for (JComponent component : components) {
			panel.add(component);
		}
		return panel;
	}

	private static class MyDrawStringExample extends JComponent {
		// the file was downloaded from https://fonts.google.com/specimen/Open+Sans
		private static final String FONT_RESOURCE_PATH = "/swing/OpenSans-Regular.ttf";
		private final BooleanSupplier enableDesktopHints;
		private final BooleanSupplier enableVolatileImage;
		private final String fontName;
		private final BooleanSupplier enableOpenSans;
		private final IntSupplier fontSizeSupplier;

		public MyDrawStringExample(BooleanSupplier enableDesktopHints, BooleanSupplier enableVolatileImage,
			BooleanSupplier enableOpenSans, IntSupplier fontSizeSupplier)
			throws IOException, FontFormatException
		{
			InputStream openSansStream = MetalFontRenderingBug.class.getResourceAsStream(FONT_RESOURCE_PATH);
			Objects.requireNonNull(openSansStream, "cannot read " + FONT_RESOURCE_PATH);
			Font font = Font.createFont(Font.TRUETYPE_FONT, openSansStream);
			fontName = font.getName();
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			System.out.println("Registered font: " + font);
			this.enableDesktopHints = enableDesktopHints;
			this.enableVolatileImage = enableVolatileImage;
			this.enableOpenSans = enableOpenSans;
			this.fontSizeSupplier = fontSizeSupplier;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			GraphicsConfiguration deviceConfiguration = g2.getDeviceConfiguration();
			Image image;
			Rectangle bounds = g2.getClipBounds();
			if (enableVolatileImage.getAsBoolean()) {
				VolatileImage compatibleVolatileImage =
					deviceConfiguration.createCompatibleVolatileImage(bounds.width, bounds.height);
				drawToBuffer(compatibleVolatileImage::createGraphics);
				image = compatibleVolatileImage;
			} else {
				BufferedImage bufferedImage = deviceConfiguration.createCompatibleImage(bounds.width, bounds.height);
				drawToBuffer(bufferedImage::createGraphics);
				image = bufferedImage;
			}
			g2.drawImage(image, 0, 0, null);
			g2.dispose();
		}

		private void drawToBuffer(Supplier<Graphics2D> graphicsCreator) {
			Graphics2D g2 = graphicsCreator.get();
			// "Courier" selected from https://en.wikipedia.org/wiki/List_of_typefaces_included_with_macOS
			// as easy to distinguish from OpenSans
			String chosenFontName = enableOpenSans.getAsBoolean() ? fontName : "Courier";
			Font font = new Font(chosenFontName, Font.PLAIN, fontSizeSupplier.getAsInt());
			g2.setFont(font);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			if (enableDesktopHints.getAsBoolean()) {
				Map<?, ?> hints =
					(Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
				System.out.println(hints == null ? "null" : (hints.size() + " hints: " + hints));
				if (hints != null)
					g2.addRenderingHints(hints);
			}
			g2.drawString("Font: " + font.getName() + ", size: " + font.getSize(), 20, 50);
			g2.dispose();
		}
	}
}