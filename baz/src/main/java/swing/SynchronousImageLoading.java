package swing;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * <a href="https://github.com/rybak/foobar/blob/master/baz/src/main/java/swing/SynchronousImageLoading.java">
 * Available on Github with image.png resource.</a>
 * <p>
 * Demo of a bug in JDK 202+.
 * <p>
 * <em>Actual result:</em>
 * <p>
 * Images in the window are not shown.
 * </p>
 *
 * <em>Expected result:</em>
 * <p>
 * Images in the window are shown.
 * </p>
 *
 * <em>Notes</em>
 * <p>
 * Works correctly in Oracle JDK 1.8.0_152.
 * Work incorrectly in Zulu JDK 1.8.0_202.
 * Work incorrectly in Oracle JDK 1.8.0_212.
 * </p>
 *
 * @author Andrei Rybak
 */
public class SynchronousImageLoading {

	private final JFrame mainWindow;
	private final JTextPane htmlDisplay;
	private final JTextPane rawHtml;

	private SynchronousImageLoading() {
		mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainWindow.setTitle(String.format("Swing HTML image loading bug (%s) (%s)",
			System.getProperty("java.version"),
			System.getProperty("java.vendor")
		));
		JPanel content = new JPanel(new BorderLayout());

		JCheckBox synchronousLoading = new JCheckBox("load synchronously", true);
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit() {
			private final ViewFactory syncedImageLoadingFactory = new HTMLFactory() {
				@Override
				public View create(Element elem) {
					View view = super.create(elem);
					//force that icons loaded synchronously.
					if (view instanceof ImageView)
						((ImageView) view).setLoadsSynchronously(synchronousLoading.isSelected());
					return view;
				}
			};

			@Override
			public ViewFactory getViewFactory() {
				return syncedImageLoadingFactory;
			}
		};
		StyleSheet css = htmlEditorKit.getStyleSheet();
		css.addRule("a { color: red; }");

		rawHtml = new JTextPane();
		rawHtml.setEditable(false);
		rawHtml.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		htmlDisplay = new JTextPane();
		htmlDisplay.setEditorKit(htmlEditorKit);
		htmlDisplay.setEditable(false);

		setHtml();
		content.add(htmlDisplay, BorderLayout.CENTER);
		content.add(rawHtml, BorderLayout.NORTH);
		{
			JPanel bottomPanel = new JPanel();
			bottomPanel.add(synchronousLoading);
			JButton refreshButton = new JButton("Refresh");
			bottomPanel.add(refreshButton);
			refreshButton.addActionListener(ignored -> setHtml());
			content.add(bottomPanel, BorderLayout.SOUTH);
		}
		mainWindow.setContentPane(content);
	}

	private void setHtml() {
		String imageUrl = SynchronousImageLoading.class.getResource("image.png").toString();
		System.err.println(imageUrl);
		String html = String.format("<html><body>\n" +
				"Just an image: <img src=\"%s\" /><br/>\n" +
				"<a href=\"https://example.net\">A link</a><br/>\n" +
				"Image inside link:<a href=\"https://example.net\"><img src=\"%s\"></a><br/>\n" +
				"Image with big border:<img src=\"%s\" border=\"10\"><br/>\n" +
				"Image with workaround:<img src=\"%s\" width=\"64\" height=\"64\">\n" +
				"</body></html>",
			imageUrl,
			imageUrl,
			imageUrl,
			imageUrl
		);
		htmlDisplay.setText(html);
		rawHtml.setText(html);
	}

	private void run() {
		mainWindow.setResizable(false);
		mainWindow.setSize(800, 600);
		mainWindow.setVisible(true);
	}

	public static void main(String... args) {
		new SynchronousImageLoading().run();
	}
}
