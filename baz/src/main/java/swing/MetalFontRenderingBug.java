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
import java.util.function.Supplier;

/**
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
 * </ol>
 * <h3>Steps to reproduce</h3>
 * <ol>
 *     <li>Launch class swing.MetalFontRenderingBug, with {@code swing/OpenSans-Regular.ttf} in resources</li>
 *     <li>Observe the text in the demo's window</li>
 * </ol>
 * <h3>Expected result</h3>
 * The text in the window ("Hello font name = Open Sans Regular") is readable.
 * <h3>Actual result</h3>
 * The text in the window is unreadable with some kind of diagonal artifacts.
 * <h3>Workaround</h3>
 * Disable the Metal rendering pipeline by passing a system property
 * {@code -Dsun.java2d.metal=false} or {@code -Dsun.java2d.opengl=true} to the JVM.
 * <h3>Notes</h3>
 * <p>
 * The checkboxes in the window help demonstrate that only {@link VolatileImage} with desktop rendering hints
 * are affected.
 * <p>
 * Reproduced on:
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
	private static final int DEMO_WIDTH = 500;
	private static final int DEMO_HEIGHT = 500;

	private MetalFontRenderingBug() {
	}

	public static void main(String... args) throws IOException, FontFormatException {
		JFrame demo = new JFrame("Bug: some TrueType fonts are rendered incorrectly in VolatileImage in the Metal rendering pipeline.");
		demo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		demo.setMinimumSize(new Dimension(DEMO_WIDTH, DEMO_HEIGHT));
		JPanel contentPane = new JPanel(new BorderLayout());
		demo.setContentPane(contentPane);

		JCheckBox enableDesktopHints = new JCheckBox("Enable desktop hints");
		enableDesktopHints.setSelected(true);
		JCheckBox enableVolatileImage = new JCheckBox("Enable volatile image");
		enableVolatileImage.setSelected(true);

		contentPane.add(createCheckboxPanel(enableDesktopHints, enableVolatileImage), BorderLayout.SOUTH);
		contentPane.add(new JLabel("JLabel is not affected." +
			" " + reportSystemProperty("sun.java2d.opengl") +
			" " + reportSystemProperty("sun.java2d.metal")), BorderLayout.NORTH);
		contentPane.add(new MyDrawStringExample(enableDesktopHints::isSelected, enableVolatileImage::isSelected),
			BorderLayout.CENTER);
		ChangeListener checkboxListener = ignored -> {
			demo.invalidate();
			demo.repaint();
		};
		enableDesktopHints.addChangeListener(checkboxListener);
		enableVolatileImage.addChangeListener(checkboxListener);
		demo.pack();
		demo.setVisible(true);
	}

	private static String reportSystemProperty(String key) {
		return "System property " + key + "=" + System.getProperty(key);
	}

	private static JPanel createCheckboxPanel(JCheckBox enableDesktopHints, JCheckBox enableVolatileImage) {
		JPanel checkboxes = new JPanel();
		checkboxes.add(enableDesktopHints);
		checkboxes.add(enableVolatileImage);
		return checkboxes;
	}

	private static class MyDrawStringExample extends JComponent {
		// the file was downloaded from https://fonts.google.com/specimen/Open+Sans
		private static final String FONT_RESOURCE_PATH = "/swing/OpenSans-Regular.ttf";
		private final BooleanSupplier enableDesktopHints;
		private final BooleanSupplier enableVolatileImage;
		private final String fontName;

		public MyDrawStringExample(BooleanSupplier enableDesktopHints, BooleanSupplier enableVolatileImage)
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
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			GraphicsConfiguration deviceConfiguration = g2.getDeviceConfiguration();
			Image image;
			if (enableVolatileImage.getAsBoolean()) {
				VolatileImage compatibleVolatileImage =
					deviceConfiguration.createCompatibleVolatileImage(DEMO_WIDTH, DEMO_HEIGHT);
				drawToBuffer(compatibleVolatileImage::createGraphics);
				image = compatibleVolatileImage;
			} else {
				BufferedImage bufferedImage = deviceConfiguration.createCompatibleImage(DEMO_WIDTH, DEMO_HEIGHT);
				drawToBuffer(bufferedImage::createGraphics);
				image = bufferedImage;
			}
			g2.drawImage(image, 0, 0, null);
			g2.dispose();
		}

		private void drawToBuffer(Supplier<Graphics2D> graphicsCreator) {
			Graphics2D g2 = graphicsCreator.get();
			g2.setFont(new Font(fontName, Font.PLAIN, 14));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			if (enableDesktopHints.getAsBoolean()) {
				Map<?, ?> hints =
					(Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
				System.out.println(hints == null ? "null" : (hints.size() + " hints: " + hints));
				if (hints != null)
					g2.addRenderingHints(hints);
			}
			g2.drawString("Hello font name = " + fontName, 20, 20);
			g2.dispose();
		}
	}
}